package r.nodes.exec;


import java.util.*;

import r.*;
import r.data.*;
import r.data.RAny.Attributes;
import r.data.RArray.Names;
import r.data.RComplex.Complex;
import r.data.RComplex.RComplexUtils;
import r.data.RDouble.RDoubleUtils;
import r.data.internal.*;
import r.data.internal.IntImpl.RIntSequence;
import r.data.internal.IntImpl.RIntSimpleRange;
import r.data.internal.ProfilingView.ViewProfile;
import r.data.internal.TracingView.*;
import r.errors.*;
import r.ext.*;
import r.nodes.ast.*;
import r.runtime.*;

// FIXME: the complex arithmetic differs for scalars/non-scalars (NA semantics - which part is NA), though
// this should not be visible to the end-user

public class Arithmetic extends BaseR {

    @Child RNode left;
    @Child RNode right;
    final ValueArithmetic arit;
    final VectorArithmetic vectorArit;

    private static final boolean SINGLE_CHILD_TIGHT_LOOP_MATERIALIZATION = true;
    private static final boolean EAGER = false;
    private static final boolean LIMIT_VIEW_DEPTH = false && !(Frame.MATERIALIZE_ON_ASSIGNMENT && AbstractCall.MATERIALIZE_FUNCTION_ARGUMENTS);
    private static final int MAX_VIEW_DEPTH = 5;

    private static final boolean DEBUG_AR = false;

    public Arithmetic(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, VectorArithmetic vectorArit) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
        this.arit = arit;
        this.vectorArit = vectorArit;
    }

    public Arithmetic(ASTNode ast, RNode left, RNode right, ValueArithmetic arit) {
        this(ast, left, right, arit, null);
    }

    public static boolean returnsDouble(ValueArithmetic arit) {
        return arit.returnsDouble();
    }

    @Override
    public Object execute(Frame frame) {

//        // an experiment
//        if (!arit.returnsDouble()) {
//            return replace(new ScalarIntSpecialized(ast, left, right, arit)).execute(frame);
//        }

        Object lexpr = left.execute(frame);
        if (getNewNode() != null) {
            return ((Arithmetic) getNewNode()).executeWithLexpr(frame, lexpr);
        }
        // hand-inlined execute(Frame, Object)
        Object rexpr = right.execute(frame);
        if (getNewNode() != null) {
            return ((Arithmetic) getNewNode()).execute(lexpr, rexpr);
        }
        return execute(lexpr, rexpr);
    }

    public Object executeWithLexpr(Frame frame, Object lexpr) {
        Object rexpr = right.execute(frame);
        if (getNewNode() != null) {
            return ((Arithmetic) getNewNode()).execute(lexpr, rexpr);
        }
        return execute(lexpr, rexpr);
    }

    public Object execute(Object lexpr, Object rexpr) {
        try {
            throw new SpecializationException(null);
        } catch (SpecializationException e) {

            if (left instanceof Constant || right instanceof Constant) {
                SpecializedConst sc = SpecializedConst.createSpecialized((RAny) lexpr, (RAny) rexpr, ast, left, right, arit);
                replace(sc, "install Specialized from Uninitialized");
                if (DEBUG_AR) Utils.debug("Installed " + sc.dbg + " for expressions " + lexpr + "(" + ((RAny) lexpr).pretty() + ") and " + rexpr + "(" + ((RAny) rexpr).pretty() + ")");
                return sc.execute(lexpr, rexpr);
            } else {
                Specialized sn = Specialized.createSpecialized((RAny) lexpr, (RAny) rexpr, ast, left, right, arit);
                replace(sn, "install Specialized from Uninitialized");
                if (DEBUG_AR) Utils.debug("Installed " + sn.dbg);
                return sn.execute(lexpr, rexpr);
            }
        }
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (left == oldNode) {
            left = newNode;
            return adoptInternal(newNode);
        }
        if (right == oldNode) {
            right = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    public enum FailedSpecialization {
        FIXED_TYPE,
        MULTI_TYPE
    }

    static class Specialized extends Arithmetic {
        final String dbg;
        final Calculator calc;

        public Specialized(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, VectorArithmetic vectorArit, Calculator calc, String dbg) {
            super(ast, left, right, arit, vectorArit);
            this.dbg = dbg;
            this.calc = calc;
        }

        public Specialized(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            super(ast, left, right, arit);
            this.dbg = dbg;
            this.calc = calc;
        }

        public abstract static class Calculator {
            public abstract Object calc(Object lexpr, Object rexpr) throws SpecializationException;
        }

        public static Specialized createSpecialized(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            if (leftTemplate instanceof ScalarComplexImpl && rightTemplate instanceof ScalarComplexImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarComplexImpl && rexpr instanceof ScalarComplexImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        ScalarComplexImpl lcomp = (ScalarComplexImpl) lexpr;
                        double lreal = lcomp.getReal();
                        double limag = lcomp.getImag();
                        ScalarComplexImpl rcomp = (ScalarComplexImpl) rexpr;
                        double rreal = rcomp.getReal();
                        double rimag = rcomp.getImag();
                        if (!RComplex.RComplexUtils.arithEitherIsNA(lreal, limag) && !RComplex.RComplexUtils.arithEitherIsNA(rreal, rimag)) {
                            return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, limag, rreal, rimag), arit.opImag(ast, lreal, limag, rreal, rimag));
                        } else {
                            return RComplex.BOXED_NA;
                        }
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarComplex, ScalarComplex>");
            }
            if (leftTemplate instanceof ScalarComplexImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarComplexImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        ScalarComplexImpl lcomp = (ScalarComplexImpl) lexpr;
                        double lreal = lcomp.getReal();
                        double limag = lcomp.getImag();
                        double rreal = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (!RComplex.RComplexUtils.arithEitherIsNA(lreal, limag) && !RDouble.RDoubleUtils.arithIsNA(rreal)) {
                            return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, limag, rreal, 0), arit.opImag(ast, lreal, limag, rreal, 0));
                        } else {
                            return RComplex.BOXED_NA;
                        }
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarComplex, ScalarDouble>");
            }
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarComplexImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarComplexImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        double lreal = ((ScalarDoubleImpl) lexpr).getDouble();
                        ScalarComplexImpl rcomp = (ScalarComplexImpl) rexpr;
                        double rreal = rcomp.getReal();
                        double rimag = rcomp.getImag();
                        if (!RDouble.RDoubleUtils.arithIsNA(lreal) && !RComplex.RComplexUtils.arithEitherIsNA(rreal, rimag)) {
                            return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, 0, rreal, rimag), arit.opImag(ast, lreal, 0, rreal, rimag));
                        } else {
                            return RComplex.BOXED_NA;
                        }
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarDouble, ScalarComplex>");
            }
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                        double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (RDouble.RDoubleUtils.arithIsNA(ldbl) || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rdbl));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarDouble, ScalarDouble>");
            }
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarIntImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                        int rint = ((ScalarIntImpl) rexpr).getInt();
                        if (RDouble.RDoubleUtils.arithIsNA(ldbl) || rint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rint));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarDouble, ScalarInt>");
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        int lint = ((ScalarIntImpl) lexpr).getInt();
                        double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (lint == RInt.NA || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, lint, rdbl));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarInt, ScalarDouble>");
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarIntImpl) {
                if (returnsDouble(arit)) {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarIntImpl)) {
                                throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (lint == RInt.NA || rint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op(ast, (double) lint, (double) rint));
                        }
                    };
                    return new Specialized(ast, left, right, arit, c, "<ScalarInt, ScalarInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarIntImpl)) {
                                throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (lint == RInt.NA || rint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.opWarnOverflow(ast, lint, rint));
                        }
                    };
                    return new Specialized(ast, left, right, arit, c, "<ScalarInt, ScalarInt>");
                }
            }

            // vectors
            return createProfiling(ast, left, right, arit);
        }

        public static Specialized createSpecializedVector(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit, final VectorArithmetic vectorArit) {
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RDouble) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof RDouble && rexpr instanceof RDouble)) {
                            throw new SpecializationException(null);
                        }
                        return vectorArit.doubleBinary((RDouble)lexpr,  (RDouble)rexpr, arit, ast);
                    }
                };
                return new Specialized(ast, left, right, arit, vectorArit, c, "<RDouble, RDouble>");
            }
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RInt) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof RDouble && rexpr instanceof RInt)) {
                            throw new SpecializationException(null);
                        }
                        return vectorArit.doubleBinary((RDouble)lexpr,  (RInt)rexpr, arit, ast);
                    }
                };
                return new Specialized(ast, left, right, arit, vectorArit, c, "<RDouble, RInt>");
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RDouble) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof RInt && rexpr instanceof RDouble)) {
                            throw new SpecializationException(null);
                        }
                        return vectorArit.doubleBinary((RInt)lexpr,  (RDouble)rexpr, arit, ast);
                    }
                };
                return new Specialized(ast, left, right, arit, vectorArit, c, "<RInt, RDouble>");
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RInt && !returnsDouble(arit)) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof RInt && rexpr instanceof RInt)) {
                            throw new SpecializationException(null);
                        }
                        return vectorArit.intBinary((RInt)lexpr,  (RInt)rexpr, arit, ast);
                    }
                };
                return new Specialized(ast, left, right, arit, vectorArit, c, "<RInt, RInt>");
            }
            return createGeneric(ast, left, right, arit, vectorArit);
        }

        public static Specialized createSpecializedMultiType(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            if ((leftTemplate instanceof ScalarIntImpl || leftTemplate instanceof ScalarDoubleImpl) &&
               (rightTemplate instanceof ScalarIntImpl || rightTemplate instanceof ScalarDoubleImpl)) {

                final boolean alwaysDouble = returnsDouble(arit);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (lexpr instanceof ScalarDoubleImpl) {
                            double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                            boolean leftIsNA = RDouble.RDoubleUtils.arithIsNA(ldbl);
                            if (rexpr instanceof ScalarDoubleImpl) {
                                double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                                if (leftIsNA || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rdbl));
                            }
                            if (rexpr instanceof ScalarIntImpl) {
                                int rint = ((ScalarIntImpl) rexpr).getInt();
                                if (leftIsNA || rint == RInt.NA) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rint));
                            }
                        } else if (lexpr instanceof ScalarIntImpl) {
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            boolean leftIsNA = lint == RInt.NA;
                            if (rexpr instanceof ScalarDoubleImpl) {
                                double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                                if (leftIsNA || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, lint, rdbl));
                            }
                            if (rexpr instanceof ScalarIntImpl) {
                                int rint = ((ScalarIntImpl) rexpr).getInt();
                                boolean arithIsNA = leftIsNA || rint == RInt.NA;
                                if (alwaysDouble) {
                                    if (arithIsNA) {
                                        return RDouble.BOXED_NA;
                                    }
                                    return RDouble.RDoubleFactory.getScalar(arit.op(ast, (double) lint, (double) rint));
                                } else {
                                    if (arithIsNA) {
                                        return RInt.BOXED_NA;
                                    }
                                    return RInt.RIntFactory.getScalar(arit.opWarnOverflow(ast, lint, rint));
                                }
                            }
                        }
                        throw new SpecializationException(FailedSpecialization.MULTI_TYPE);
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarDouble|Int, ScalarDouble|Int>");
            }
            return null;
        }

        public static RArray genericCalc(Object lexpr, Object rexpr, ValueArithmetic arit, boolean returnsDouble, VectorArithmetic vectorArit, ASTNode ast) {
            // TODO: re-visit this, the error semantics with non-numeric types is very likely wrong
            if (lexpr instanceof RComplex || rexpr instanceof RComplex) {
                RComplex lcmp = ((RAny)lexpr).asComplex();
                RComplex rcmp = ((RAny)rexpr).asComplex();
                return vectorArit.complexBinary(lcmp, rcmp, arit, ast);
            }
            if (returnsDouble) {
                RDouble ldbl = ((RAny)lexpr).asDouble();
                RDouble rdbl = ((RAny)rexpr).asDouble();
                return vectorArit.doubleBinary(ldbl, rdbl, arit, ast);
            }
            if (lexpr instanceof RDouble) {
                RDouble ldbl = (RDouble) lexpr;
                if (rexpr instanceof RDouble) {
                    return vectorArit.doubleBinary(ldbl, (RDouble) rexpr, arit, ast);
                } else if (rexpr instanceof RInt) {
                    return vectorArit.doubleBinary(ldbl, (RInt) rexpr, arit, ast);
                } else {
                    return vectorArit.doubleBinary(ldbl, ((RAny) rexpr).asDouble(), arit, ast);
                }
            }
            if (rexpr instanceof RDouble) {
                RDouble rdbl = (RDouble) rexpr;
                if (lexpr instanceof RInt) {
                    return vectorArit.doubleBinary((RInt) lexpr, rdbl, arit, ast);
                } else {
                    return vectorArit.doubleBinary(((RAny) lexpr).asDouble(), rdbl, arit, ast);
                }
            }
            if (lexpr instanceof RInt || rexpr instanceof RInt || lexpr instanceof RLogical || rexpr instanceof RLogical) { // FIXME: this check should be simpler
                RInt lint = ((RAny) lexpr).asInt();
                RInt rint = ((RAny) rexpr).asInt();
                return vectorArit.intBinary(lint, rint, arit, ast);
            }
            throw RError.getNonNumericBinary(ast);
        }

        public static Specialized createProfiling(final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {

            Calculator c;
            final boolean returnsDouble = returnsDouble(arit);
            c = new Calculator() {
                ViewProfile profile;

                @Override
                public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                    if (profile == null) {
                        profile = new ViewProfile();
                        RArray res = genericCalc(lexpr, rexpr, arit, returnsDouble, LAZY_VECTOR, ast);
                        return ProfilingView.ViewProfile.profile(res, profile);
                    } else {
                        throw new SpecializationException(chooseVectorArithmetic(profile));
                    }
                }
            };
            return new Specialized(ast, left, right, arit, LAZY_VECTOR, c, "profiling Generic, Generic>");
        }

        public static Specialized createGeneric(final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit, final VectorArithmetic vectorArit) {
            Calculator c;
            final boolean returnsDouble = returnsDouble(arit);
            c = new Calculator() {
                @Override
                public Object calc(Object lexpr, Object rexpr) {
                    return genericCalc(lexpr, rexpr, arit, returnsDouble, vectorArit, ast);
                }
            };
            return new Specialized(ast, left, right, arit, vectorArit, c, "<Generic, Generic>");
        }

        @Override
        public final Object execute(Object lexpr, Object rexpr) {
            try {
                return calc.calc(lexpr, rexpr);
            } catch (SpecializationException e) {
                Object r = e.getResult();
                if (r instanceof VectorArithmetic) {
                    // result of profiling - the previous node must have been a profiling node
                    Specialized sn = createSpecializedVector((RAny)lexpr, (RAny) rexpr, ast, left, right, arit, (VectorArithmetic) r);
                    replace(sn, "install SpecializedVector from Specialized-Profiling");
                    return sn.execute(lexpr, rexpr);
                }

                FailedSpecialization f = (FailedSpecialization) r;
                if (f == FailedSpecialization.FIXED_TYPE) {
                    Specialized sn = createSpecializedMultiType((RAny) lexpr, (RAny) rexpr, ast, left, right, arit);
                    if (sn != null) {
                        replace(sn, "install SpecializedMultiType from Specialized");
                        return sn.execute(lexpr, rexpr);
                    }
                }

                if (vectorArit == null) {
                    Specialized sn = createProfiling(ast, left, right, arit);
                    replace(sn, "install Profiling from Specialized-?");
                    return sn.execute(lexpr, rexpr);
                }

                Specialized gn = createGeneric(ast, left, right, arit, vectorArit);
                replace(gn, "install Specialized<Generic, Generic> from Specialized");
                return gn.execute(lexpr, rexpr);
            }
        }
    }

//    // just an experiment for now
//    static class ScalarIntSpecialized extends BaseR {
//        @Child RNode left;
//        @Child RNode right;
//        final ValueArithmetic arit;
//
//        public ScalarIntSpecialized(ASTNode ast, RNode left, RNode right, ValueArithmetic arit) {
//            super(ast);
//            this.left = adoptChild(left);
//            this.right = adoptChild(right);
//            this.arit = arit;
//            assert Utils.check(!arit.returnsDouble());
//        }
//
//        private Object recover(Object lobj, Object robj) {
//            RAny lexpr = (RAny) lobj;
//            RAny rexpr = (RAny) robj;
//
//            Arithmetic an = new Arithmetic(ast, left, right, arit);
//            replace(an);
//            return an.execute(lexpr, rexpr);
//
////            Specialized sn = Specialized.createSpecializedMultiType(lexpr, rexpr, ast, left, right, arit);
////            if (sn != null) {
////                replace(sn, "install SpecializedMultiType from ScalarIntSpecialized");
////                return sn.execute(lexpr, rexpr);
////            }
////            Specialized gn = Specialized.createGeneric(ast, left, right, arit);
////            replace(gn, "install Specialized<Generic, Generic> from ScalarIntSpecialized");
////            return gn.execute(lexpr, rexpr);
//        }
//
//        @Override
//        public int executeScalarInteger(Frame frame) throws UnexpectedResultException {
//            int lint;
//            try {
//                lint = left.executeScalarInteger(frame);
//            } catch (UnexpectedResultException e) {
//                throw new UnexpectedResultException(recover(e.getResult(), right.execute(frame)));
//            }
//            int rint;
//            try {
//                rint = right.executeScalarInteger(frame);
//            } catch (UnexpectedResultException e) {
//                throw new UnexpectedResultException(recover(left.execute(frame), e.getResult()));
//            }
//            if (lint == RInt.NA || rint == RInt.NA) {
//                return RInt.NA;
//            }
//            return arit.opWarnOverflow(ast, lint, rint);
//        }
//
//        @Override
//        public Object execute(Frame frame) {
//            try {
//                return RInt.RIntFactory.getScalar(executeScalarInteger(frame)); // does the rewriting
//            } catch (UnexpectedResultException e) {
//                return e.getResult();
//            }
//        }
//    }


    static class SpecializedConst extends Arithmetic {
        final String dbg;
        final Calculator calc;

        public SpecializedConst(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            super(ast, left, right, arit);
            this.dbg = dbg;
            this.calc = calc;
        }

        public abstract static class Calculator {
            public abstract Object calc(Object lexpr, Object rexpr) throws SpecializationException;
        }

        public static SpecializedConst createSpecialized(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            boolean leftConst = left instanceof Constant;
            boolean rightConst = right instanceof Constant;
            // non-const is complex
            if (leftConst && (rightTemplate instanceof ScalarComplexImpl) &&
               (leftTemplate instanceof ScalarComplexImpl || leftTemplate instanceof ScalarDoubleImpl || leftTemplate instanceof ScalarIntImpl || leftTemplate instanceof ScalarLogicalImpl)) {
                RComplex lcmp = leftTemplate.asComplex();
                final double lreal = lcmp.getReal(0);
                final double limag =  lcmp.getImag(0);
                final boolean isLeftNA = RComplex.RComplexUtils.arithEitherIsNA(lreal, limag);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(rexpr instanceof ScalarComplexImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        ScalarComplexImpl rcmp = (ScalarComplexImpl) rexpr;
                        double rreal = rcmp.getReal();
                        double rimag = rcmp.getImag();
                        if (isLeftNA || RComplex.RComplexUtils.arithEitherIsNA(rreal, rimag)) {
                            return RComplex.BOXED_NA;
                        }
                        return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, limag, rreal, rimag), arit.opImag(ast, lreal, limag, rreal, rimag));
                    }
                };
                return createLeftConst(ast, left, right, arit, c, "<ConstScalarNumber, ScalarComplex>");
            }
            if (rightConst && (leftTemplate instanceof ScalarComplexImpl) &&
                (rightTemplate instanceof ScalarComplexImpl || rightTemplate instanceof ScalarDoubleImpl || rightTemplate instanceof ScalarIntImpl || rightTemplate instanceof ScalarLogicalImpl)) {
                 RComplex rcmp = rightTemplate.asComplex();
                 final double rreal = rcmp.getReal(0);
                 final double rimag =  rcmp.getImag(0);
                 final boolean isRightNA = RComplex.RComplexUtils.arithEitherIsNA(rreal, rimag);
                 Calculator c = new Calculator() {
                     @Override
                     public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                         if (!(lexpr instanceof ScalarComplexImpl)) {
                             throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                         }
                         ScalarComplexImpl lcmp = (ScalarComplexImpl) lexpr;
                         double lreal = lcmp.getReal();
                         double limag = lcmp.getImag();
                         if (isRightNA || RComplex.RComplexUtils.arithEitherIsNA(lreal, limag)) {
                             return RComplex.BOXED_NA;
                         }
                         return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, limag, rreal, rimag), arit.opImag(ast, lreal, limag, rreal, rimag));
                     }
                 };
                 return createRightConst(ast, left, right, arit, c, "<ScalarComplex, ConstScalarNumber>");
            }
            // non-const is double and const is complex
            if (leftConst && (rightTemplate instanceof ScalarDoubleImpl) && (leftTemplate instanceof ScalarComplexImpl)) {
                 ScalarComplexImpl lcmp = (ScalarComplexImpl) leftTemplate;
                 final double lreal = lcmp.getReal(0);
                 final double limag =  lcmp.getImag(0);
                 final boolean isLeftNA = RComplex.RComplexUtils.arithEitherIsNA(lreal, limag);
                 Calculator c = new Calculator() {
                     @Override
                     public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                         if (!(rexpr instanceof ScalarDoubleImpl)) {
                             throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                         }
                         double rreal = ((ScalarDoubleImpl) rexpr).getDouble();
                         if (isLeftNA || RDouble.RDoubleUtils.isNAorNaN(rreal)) { // NOTE: not arithIsNA !
                             return RComplex.BOXED_NA;
                         }
                         return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, limag, rreal, 0), arit.opImag(ast, lreal, limag, rreal, 0));
                     }
                 };
                 return createLeftConst(ast, left, right, arit, c, "<ConstScalarComplex, ScalarDouble>");
            }
            if (rightConst && (leftTemplate instanceof ScalarDoubleImpl) && (rightTemplate instanceof ScalarComplexImpl)) {
                ScalarComplexImpl rcmp = (ScalarComplexImpl) rightTemplate;
                final double rreal = rcmp.getReal(0);
                final double rimag =  rcmp.getImag(0);
                final boolean isRightNA = RComplex.RComplexUtils.arithEitherIsNA(rreal, rimag);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        double lreal = ((ScalarDoubleImpl) lexpr).getDouble();
                        if (isRightNA || RDouble.RDoubleUtils.isNAorNaN(lreal)) { // NOTE: not arithIsNA !
                            return RComplex.BOXED_NA;
                        }
                        return RComplex.RComplexFactory.getScalar(arit.opReal(ast, lreal, 0, rreal, rimag), arit.opImag(ast, lreal, 0, rreal, rimag));
                    }
                };
                return createRightConst(ast, left, right, arit, c, "<ScalarDouble, ConstScalarComplex>");
           }
            // non-const is double
            if (leftConst && (rightTemplate instanceof ScalarDoubleImpl) && (leftTemplate instanceof ScalarDoubleImpl || leftTemplate instanceof ScalarIntImpl || leftTemplate instanceof ScalarLogicalImpl)) {
                final double ldbl = (leftTemplate.asDouble()).getDouble(0);
                final boolean isLeftNA = RDouble.RDoubleUtils.arithIsNA(ldbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(rexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (isLeftNA || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rdbl));
                    }
                };
                return createLeftConst(ast, left, right, arit, c, "<ConstScalarNon-Complex, ScalarDouble>");
            }
            if (rightConst && (leftTemplate instanceof ScalarDoubleImpl) && (rightTemplate instanceof ScalarDoubleImpl || rightTemplate instanceof ScalarIntImpl || rightTemplate instanceof ScalarLogicalImpl)) {
                final double rdbl = (rightTemplate.asDouble()).getDouble(0);
                final boolean isRightNA = RDouble.RDoubleUtils.arithIsNA(rdbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                        if (isRightNA || RDouble.RDoubleUtils.arithIsNA(ldbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rdbl));
                    }
                };
                return createRightConst(ast, left, right, arit, c, "<ScalarDouble, ConstScalarNon-Complex>");
            }
            // non-const is int and const is double
            // FIXME: handle also logical?
            if (leftConst && (leftTemplate instanceof ScalarDoubleImpl) && (rightTemplate instanceof ScalarIntImpl)) {
                final double ldbl = (leftTemplate.asDouble()).getDouble(0);
                final boolean isLeftNA = RDouble.RDoubleUtils.arithIsNA(ldbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(rexpr instanceof ScalarIntImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        int rint = ((ScalarIntImpl) rexpr).getInt();
                        if (isLeftNA || rint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rint));
                    }
                };
                return createLeftConst(ast, left, right, arit, c, "<ConstScalarDouble, ScalarInt>");
            }
            if (rightConst && (rightTemplate instanceof ScalarDoubleImpl) && (leftTemplate instanceof ScalarIntImpl)) {
                final double rdbl = (rightTemplate.asDouble()).getDouble(0);
                final boolean isRightNA = RDouble.RDoubleUtils.arithIsNA(rdbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarIntImpl)) {
                            throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                        }
                        int lint = ((ScalarIntImpl) lexpr).getInt();
                        if (isRightNA || lint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ast, lint, rdbl));
                    }
                };
                return createRightConst(ast, left, right, arit, c, "<ScalarInt, ConstScalarDouble>");
            }
            // non-const is int and const is int or logical
            if (leftConst && (leftTemplate instanceof ScalarIntImpl || leftTemplate instanceof ScalarLogicalImpl) && (rightTemplate instanceof ScalarIntImpl)) {
                final int lint = (leftTemplate.asInt()).getInt(0);
                final boolean isLeftNA = (lint == RInt.NA);
                if (returnsDouble(arit)) {
                    final double ldbl = lint;
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (!(rexpr instanceof ScalarIntImpl)) {
                                throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                            }
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (isLeftNA || rint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, (double) rint));
                        }
                    };
                    return createLeftConst(ast, left, right, arit, c, "<ConstScalarInt, ScalarInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (!(rexpr instanceof ScalarIntImpl)) {
                                throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                            }
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (isLeftNA || rint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.opWarnOverflow(ast, lint, rint));
                        }
                    };
                    return createLeftConst(ast, left, right, arit, c, "<ConstScalarInt, ScalarInt>");
                }
            }
            if (rightConst && (rightTemplate instanceof ScalarIntImpl || rightTemplate instanceof ScalarLogicalImpl) && (leftTemplate instanceof ScalarIntImpl)) {
                final int rint = (rightTemplate.asInt()).getInt(0);
                final boolean isRightNA = (rint == RInt.NA);
                if (returnsDouble(arit)) {
                    final double rdbl = rint;
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (!(lexpr instanceof ScalarIntImpl)) {
                                throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            if (isRightNA || lint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op(ast, (double) lint, rdbl));
                        }
                    };
                    return createRightConst(ast, left, right, arit, c, "<ScalarInt, ConstScalarInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (!(lexpr instanceof ScalarIntImpl)) {
                                throw new SpecializationException(FailedSpecialization.FIXED_TYPE);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            if (isRightNA || lint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.opWarnOverflow(ast, lint, rint));
                        }
                    };
                    return createRightConst(ast, left, right, arit, c, "<ScalarInt, ConstScalarInt>");
                }
            }
            return createGeneric(leftTemplate, rightTemplate, ast, left, right, arit, chooseVectorArithmetic(leftTemplate, rightTemplate, arit));
        }

        public static SpecializedConst createSpecializedMultiType(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {

            boolean leftConst = left instanceof Constant;
            boolean rightConst = right instanceof Constant;
            assert Utils.check(leftConst || rightConst);

            final boolean alwaysDouble = returnsDouble(arit);

            if ((!(leftTemplate instanceof ScalarIntImpl) && !(leftTemplate instanceof ScalarDoubleImpl)) ||
               (!(rightTemplate instanceof ScalarIntImpl) && !(rightTemplate instanceof ScalarDoubleImpl))) {
                return null;
            }

            if (leftConst) {
                double tldbl;
                int tlint;
                boolean tisLeftNA;
                boolean tisLeftDouble;
                if (leftTemplate instanceof ScalarDoubleImpl) {
                    tlint = -1; // not used
                    tldbl = ((ScalarDoubleImpl) leftTemplate).getDouble();
                    tisLeftNA = RDouble.RDoubleUtils.arithIsNA(tldbl);
                    tisLeftDouble = true;
                } else {
                    tlint = ((ScalarIntImpl) leftTemplate).getInt();
                    tldbl = tlint;
                    tisLeftNA = tlint == RInt.NA;
                    tisLeftDouble = false;
                }
                final double ldbl = tldbl;
                final int lint = tlint;
                final boolean isLeftNA = tisLeftNA;
                final boolean isLeftDouble = tisLeftDouble;

                if (isLeftDouble || alwaysDouble) {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (rexpr instanceof ScalarDoubleImpl) {
                                double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                                if (isLeftNA || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rdbl));
                            } else if (rexpr instanceof ScalarIntImpl) {
                                int rint = ((ScalarIntImpl) rexpr).getInt();
                                if (isLeftNA || rint == RInt.NA) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rint));
                            } else {
                                throw new SpecializationException(FailedSpecialization.MULTI_TYPE);
                            }
                        }
                    };
                    return createLeftConst(ast, left, right, arit, c, "<ConstScalarDouble, ScalarInt|Double>");
                } else {
                    // left is constant int
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (rexpr instanceof ScalarDoubleImpl) {
                                double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                                if (isLeftNA || RDouble.RDoubleUtils.arithIsNA(rdbl)) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, lint, rdbl));
                            } else if (rexpr instanceof ScalarIntImpl) {
                                int rint = ((ScalarIntImpl) rexpr).getInt();
                                if (isLeftNA || rint == RInt.NA) {
                                    return RInt.BOXED_NA;
                                }
                                return RInt.RIntFactory.getScalar(arit.opWarnOverflow(ast, lint, rint));
                            } else {
                                throw new SpecializationException(FailedSpecialization.MULTI_TYPE);
                            }
                        }
                    };
                    return createLeftConst(ast, left, right, arit, c, "<ConstScalarInt, ScalarInt|Double>");
                }
            } else {
                // rightConst
                double trdbl;
                int trint;
                boolean tisRightNA;
                boolean tisRightDouble;
                if (rightTemplate instanceof ScalarDoubleImpl) {
                    trint = -1; // not used
                    trdbl = ((ScalarDoubleImpl) rightTemplate).getDouble();
                    tisRightNA = RDouble.RDoubleUtils.arithIsNA(trdbl);
                    tisRightDouble = true;
                } else {
                    trint = ((ScalarIntImpl) rightTemplate).getInt();
                    trdbl = trint;
                    tisRightNA = trint == RInt.NA;
                    tisRightDouble = false;
                }
                final double rdbl = trdbl;
                final int rint = trint;
                final boolean isRightNA = tisRightNA;
                final boolean isRightDouble = tisRightDouble;

                if (isRightDouble || alwaysDouble) {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (lexpr instanceof ScalarDoubleImpl) {
                                double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                                if (isRightNA || RDouble.RDoubleUtils.arithIsNA(ldbl)) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rdbl));
                            } else if (lexpr instanceof ScalarIntImpl) {
                                int lint = ((ScalarIntImpl) lexpr).getInt();
                                if (isRightNA || lint == RInt.NA) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, lint, rdbl));
                            } else {
                                throw new SpecializationException(FailedSpecialization.MULTI_TYPE);
                            }
                        }
                    };
                    return createRightConst(ast, left, right, arit, c, "<ScalarInt|Double, ConstScalarDouble>");
                } else {
                    // left is constant int
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(Object lexpr, Object rexpr) throws SpecializationException {
                            if (lexpr instanceof ScalarDoubleImpl) {
                                double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                                if (isRightNA || RDouble.RDoubleUtils.arithIsNA(ldbl)) {
                                    return RDouble.BOXED_NA;
                                }
                                return RDouble.RDoubleFactory.getScalar(arit.op(ast, ldbl, rint));
                            } else if (lexpr instanceof ScalarIntImpl) {
                                int lint = ((ScalarIntImpl) lexpr).getInt();
                                if (isRightNA || lint == RInt.NA) {
                                    return RInt.BOXED_NA;
                                }
                                return RInt.RIntFactory.getScalar(arit.opWarnOverflow(ast, lint, rint));
                            } else {
                                throw new SpecializationException(FailedSpecialization.MULTI_TYPE);
                            }
                        }
                    };
                    return createRightConst(ast, left, right, arit, c, "<ScalarInt|Double, ConstScalarInt>");
                }
            }
        }

        public static SpecializedConst createGeneric(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit, final VectorArithmetic vectorArit) {
            Calculator c = null;
            boolean leftConst = left instanceof Constant;
            boolean rightConst = right instanceof Constant;
            final boolean returnsDouble = returnsDouble(arit);

            if (leftConst) {
                final boolean leftComplex = leftTemplate instanceof RComplex;
                final boolean leftDouble = leftTemplate instanceof RDouble;
                final boolean leftInt = leftTemplate instanceof RInt;
                final boolean leftLogicalOrInt = leftTemplate instanceof RLogical || leftTemplate instanceof RInt; // FIXME: does this pre-allocation pay off?
                final RComplex lcmp = (leftComplex) ? (RComplex) leftTemplate : leftTemplate.asComplex();
                final RDouble ldbl = (leftDouble) ? (RDouble) leftTemplate : leftTemplate.asDouble();
                final RInt lint = (leftLogicalOrInt) ? leftTemplate.asInt() : null;
                c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) {
                     // TODO: re-visit this, the error semantics with non-numeric types is very likely wrong
                        if (leftComplex || rexpr instanceof RComplex) {
                            RComplex rcmp = ((RAny) rexpr).asComplex();
                            return vectorArit.complexBinary(lcmp, rcmp, arit, ast);
                        }
                        if (returnsDouble) {
                            return vectorArit.doubleBinary(ldbl, ((RAny) rexpr).asDouble(), arit, ast);
                        }
                        if (leftDouble) {
                            if (rexpr instanceof RDouble) {
                                return vectorArit.doubleBinary(ldbl, (RDouble) rexpr, arit, ast);
                            } else if (rexpr instanceof RInt) {
                                return vectorArit.doubleBinary(ldbl, (RInt) rexpr, arit, ast);
                            } else {
                                return vectorArit.doubleBinary(ldbl, ((RAny) rexpr).asDouble(), arit, ast);
                            }
                        }
                        if (rexpr instanceof RDouble) {
                            RDouble rdbl = (RDouble) rexpr;
                            if (leftInt) {
                                return vectorArit.doubleBinary(lint, rdbl, arit, ast);
                            } else {
                                return vectorArit.doubleBinary(ldbl, rdbl, arit, ast);
                            }
                        }
                        if (leftLogicalOrInt || rexpr instanceof RInt || rexpr instanceof RLogical) { // FIXME: this check should be simpler
                            RInt rint = ((RAny) rexpr).asInt();
                            return vectorArit.intBinary(lint, rint, arit, ast);
                        }
                        Utils.nyi("unsupported case for binary arithmetic operation");
                        return null;
                    }
                };
            }
            if (rightConst) {
                final boolean rightComplex = rightTemplate instanceof RComplex;
                final boolean rightDouble = rightTemplate instanceof RDouble;
                final boolean rightInt = rightTemplate instanceof RInt;
                final boolean rightLogicalOrInt = rightTemplate instanceof RLogical || rightTemplate instanceof RInt;
                final RComplex rcmp = (rightComplex) ? (RComplex) rightTemplate : rightTemplate.asComplex();
                final RDouble rdbl = (rightDouble) ? (RDouble) rightTemplate : rightTemplate.asDouble();
                final RInt rint = (rightLogicalOrInt) ? rightTemplate.asInt() : null;
                c = new Calculator() {
                    @Override
                    public Object calc(Object lexpr, Object rexpr) {
                     // TODO: re-visit this, the error semantics with non-numeric types is very likely wrong
                        if (rightComplex || lexpr instanceof RComplex) {
                            RComplex lcmp = ((RAny) lexpr).asComplex();
                            return vectorArit.complexBinary(lcmp, rcmp, arit, ast);
                        }
                        if (returnsDouble) {
                            return vectorArit.doubleBinary(((RAny) lexpr).asDouble(), rdbl, arit, ast);
                        }
                        if (rightDouble) {
                            if (lexpr instanceof RDouble) {
                                return vectorArit.doubleBinary((RDouble) lexpr, rdbl, arit, ast);
                            } else if (lexpr instanceof RInt) {
                                return vectorArit.doubleBinary((RInt) lexpr, rdbl, arit, ast);
                            } else {
                                return vectorArit.doubleBinary(((RAny) lexpr).asDouble(), rdbl, arit, ast);
                            }
                        }
                        if (lexpr instanceof RDouble) {
                            RDouble ldbl = (RDouble) lexpr;
                            if (rightInt) {
                                return vectorArit.doubleBinary(ldbl, rint, arit, ast);
                            } else {
                                return vectorArit.doubleBinary(((RAny) lexpr).asDouble(), rdbl, arit, ast);
                            }
                        }
                        if (rightLogicalOrInt || lexpr instanceof RInt || lexpr instanceof RLogical) { // FIXME: this check should be simpler
                            RInt lint = ((RAny) lexpr).asInt();
                            return vectorArit.intBinary(lint, rint, arit, ast);
                        }
                        Utils.nyi("unsupported case for binary arithmetic operation");
                        return null;
                    }
                };
            }
            assert Utils.check(c != null);
            if (rightConst) {
                return createRightConst(ast, left, right, arit, c, "<Generic, ConstGeneric>");
            } else {
                return createLeftConst(ast, left, right, arit, c, "<ConstGeneric, Generic>");
            }
        }

        public static SpecializedConst createLeftConst(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            assert Utils.check(left instanceof Constant);
            return new SpecializedConst(ast, left, right, arit, calc, dbg) {

                @Override
                public Object execute(Frame frame) {
                    RAny rexpr = (RAny) right.execute(frame);
                    if (getNewNode() != null) {
                        ((SpecializedConst) getNewNode()).executeWithLexpr(null, rexpr);
                    }
                    return execute(null, rexpr);
                }
            };
        }

        public static SpecializedConst createRightConst(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            assert Utils.check(right instanceof Constant);
            return new SpecializedConst(ast, left, right, arit, calc, dbg) {

                @Override
                public Object execute(Frame frame) {
                    RAny lexpr = (RAny) left.execute(frame);
                    if (getNewNode() != null) {
                        ((SpecializedConst) getNewNode()).execute(lexpr, null);
                    }
                    return execute(lexpr, null);
                }
            };
        }

        private static RAny getExpr(RNode node, RAny value) {
            if (value == null) {
                return (RAny) node.execute(null);
            } else {
                return value;
            }
        }

        @Override
        public Object execute(Object lexpr, Object rexpr) {
            try {
                return calc.calc(lexpr, rexpr);
            } catch (SpecializationException e) {
                FailedSpecialization f = (FailedSpecialization) e.getResult();
                RAny leftTemplate = getExpr(left, (RAny) lexpr);
                RAny rightTemplate = getExpr(right, (RAny) rexpr);
                if (f == FailedSpecialization.FIXED_TYPE) {
                    SpecializedConst sn = createSpecializedMultiType(leftTemplate, rightTemplate, ast, left, right, arit);
                    if (sn != null) {
                        replace(sn, "install SpecializedConstMultiType from SpecializedConst");
                        return sn.execute(lexpr, rexpr);
                    }
                }
                SpecializedConst gn = createGeneric(leftTemplate, rightTemplate, ast, left, right, arit, chooseVectorArithmetic(leftTemplate, rightTemplate, arit));
                replace(gn, "install SpecializedConst<Generic, Generic> from SpecializedConst");
                if (DEBUG_AR) Utils.debug("Rewrote Const" + dbg + " to " + gn.dbg);
                return gn.execute(leftTemplate, rightTemplate);
            }
        }
    }

    public static class IntStatus {
        boolean overflown;
    }

    private static final IntStatus intStatus = new IntStatus();

    public abstract static class ValueArithmetic {
        public abstract double opReal(ASTNode ast, double a, double b, double c, double d); // (a + bi)  op  (c + di)
        public abstract double opImag(ASTNode ast, double a, double b, double c, double d);
        public abstract Complex opComplex(ASTNode ast, double a, double b, double c, double d);

        public abstract double op(ASTNode ast, double a, double b);
        public abstract int op(ASTNode ast, int a, int b);
        public abstract void emitOverflowWarning(ASTNode ast);

        public final int opWarnOverflow(ASTNode ast, int a, int b) {
            int res = op(ast, a, b);
            if (res == RInt.NA) {
                emitOverflowWarning(ast);
            }
            return res;
        }
        public final double op(ASTNode ast, double a, int b) {
            return op(ast, a, (double) b);
        }
        public final double op(ASTNode ast, int a, double b) {
            return op(ast, (double) a, b);
        }
        public final double opCheckingNA(ASTNode ast, double a, double b) {
            if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                return RDouble.NA;
            }
            return op(ast, a, b);
        }
        public final double opCheckingNA(ASTNode ast, double a, int b) {
            if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                return RDouble.NA;
            }
            return op(ast, a, b);
        }
        public final Complex opComplexCheckingNA(ASTNode ast, double a, double b, double c, double d) {
            if (RComplex.RComplexUtils.arithEitherIsNA(a, c) || RComplex.RComplexUtils.arithEitherIsNA(b, d)) {
                return RComplex.COMPLEX_BOXED_NA;
            }
            return opComplex(ast, a, b, c, d);
        }

        // TODO: NA checks on operations with scalars below

        public abstract void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size);
        public abstract void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size);
        public abstract void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size);
        public abstract void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize);
        public abstract void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize);

        public RComplex opComplexImplEqualSize(ASTNode ast, ComplexImpl xcomp, ComplexImpl ycomp, int size, int[] dimensions, Names names, Attributes attributes) {

            double[] x = xcomp.getContent();
            double[] y = ycomp.getContent();
            if (xcomp.isTemporary()) {
                opComplexEqualSize(ast, x, y, x, size);
                xcomp.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xcomp;
            } else if (ycomp.isTemporary()) {
                opComplexEqualSize(ast, x, y, y, size);
                ycomp.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ycomp;
            } else {
                int rsize = size * 2;
                double[] res = new double[rsize];
                opComplexEqualSize(ast, x, y, res, size);
                return RComplex.RComplexFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RComplex opComplexImplScalar(ASTNode ast, ComplexImpl xcomp, double c, double d, int size, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xcomp.getContent();
            if (xcomp.isTemporary()) {
                opComplexScalar(ast, x, c, d, x, size);
                xcomp.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xcomp;
            } else {
                int rsize = size * 2;
                double[] res = new double[rsize];
                opComplexScalar(ast, x, c, d, res, size);
                return RComplex.RComplexFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RComplex opComplexImplScalarCheckingNA(ASTNode ast, ComplexImpl xcomp, double c, double d, int size, int[] dimensions, Names names, Attributes attributes) {
            if (RComplex.RComplexUtils.arithEitherIsNA(c, d)) {
                // FIXME could re-use the array, but arithIsNA should be non-checking anyway
                return RComplex.RComplexFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opComplexImplScalar(ast, xcomp, c, d, size, dimensions, names, attributes);
            }
        }

        public RComplex opScalarComplexImpl(ASTNode ast, double a, double b, ComplexImpl ycomp, int size, int[] dimensions, Names names, Attributes attributes) {
            double[] y = ycomp.getContent();
            if (ycomp.isTemporary()) {
                opScalarComplex(ast, a, b, y, y, size);
                ycomp.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ycomp;
            } else {
                int rsize = size * 2;
                double[] res = new double[rsize];
                opScalarComplex(ast, a, b, y, res, size);
                return RComplex.RComplexFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RComplex opScalarComplexImplCheckingNA(ASTNode ast, double a, double b, ComplexImpl ycomp, int size, int[] dimensions, Names names, Attributes attributes) {
            if (RComplex.RComplexUtils.arithEitherIsNA(a, b)) {
                // FIXME could re-use the array, but arithIsNA should be non-checking anyway
                return RComplex.RComplexFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opScalarComplexImpl(ast, a, b, ycomp, size, dimensions, names, attributes);
            }
        }

        public RComplex opComplexImplASized(ASTNode ast, ComplexImpl xcomp, ComplexImpl ycomp, int size, int bsize, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xcomp.getContent();
            double[] y = ycomp.getContent();
            if (xcomp.isTemporary()) {
                opComplexASized(ast, x, y, x, size, bsize);
                xcomp.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xcomp;
            } else {
                double[] res = new double[size];
                opComplexASized(ast, x, y, res, size, bsize);
                return RComplex.RComplexFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RComplex opComplexImplBSized(ASTNode ast, ComplexImpl xcomp, ComplexImpl ycomp, int size, int asize, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xcomp.getContent();
            double[] y = ycomp.getContent();
            if (ycomp.isTemporary()) {
                opComplexBSized(ast, x, y, y, size, asize);
                ycomp.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ycomp;
            } else {
                double[] res = new double[size];
                opComplexBSized(ast, x, y, res, size, asize);
                return RComplex.RComplexFactory.getFor(res, dimensions, names, attributes);
            }
        }


        public abstract void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size);
        public abstract void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size);
        public abstract void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size);
        public abstract void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize);
        public abstract void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize);

        public RDouble opDoubleImplEqualSize(ASTNode ast, DoubleImpl xdbl, DoubleImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            double[] y = ydbl.getContent();
            if (xdbl.isTemporary()) {
                opDoubleEqualSize(ast, x, y, x, size);
                xdbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xdbl;
            } else if (ydbl.isTemporary()) {
                opDoubleEqualSize(ast, x, y, y, size);
                ydbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ydbl;
            } else {
                double[] res = new double[size];
                opDoubleEqualSize(ast, x, y, res, size);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplScalar(ASTNode ast, DoubleImpl xdbl, double y, int size, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            if (xdbl.isTemporary()) {
                opDoubleScalar(ast, x, y, x, size);
                xdbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xdbl;
            } else {
                double[] res = new double[size];
                opDoubleScalar(ast, x, y, res, size);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplScalarCheckingNA(ASTNode ast, DoubleImpl xdbl, double y, int size, int[] dimensions, Names names, Attributes attributes) {
            if (RDouble.RDoubleUtils.arithIsNA(y)) {
                // FIXME could re-use the array, but arithIsNA should be non-checking anyway
                return RDouble.RDoubleFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opDoubleImplScalar(ast, xdbl, y, size, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplScalarIntCheckingNA(ASTNode ast, DoubleImpl xdbl, int y, int size, int[] dimensions, Names names, Attributes attributes) {
            if (y == RInt.NA) {
                // FIXME could re-use the array
                return RDouble.RDoubleFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opDoubleImplScalar(ast, xdbl, y, size, dimensions, names, attributes);
            }
        }

        public RDouble opScalarIntDoubleImplCheckingNA(ASTNode ast, int x, DoubleImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            if (x == RInt.NA) {
                // FIXME could re-use the array
                return RDouble.RDoubleFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opDoubleImplScalar(ast, ydbl, x, size, dimensions, names, attributes);
            }
        }

        public RDouble opScalarDoubleImpl(ASTNode ast, double x, DoubleImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            double[] y = ydbl.getContent();
            if (ydbl.isTemporary()) {
                opScalarDouble(ast, x, y, y, size);
                ydbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ydbl;
            } else {
                double[] res = new double[size];
                opScalarDouble(ast, x, y, res, size);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opScalarDoubleImplCheckingNA(ASTNode ast, double x, DoubleImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            if (RDouble.RDoubleUtils.arithIsNA(x)) {
                // FIXME could re-use the array, but arithIsNA should be non-checking anyway
                return RDouble.RDoubleFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opScalarDoubleImpl(ast, x, ydbl, size, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplASized(ASTNode ast, DoubleImpl xdbl, DoubleImpl ydbl, int size, int bsize, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            double[] y = ydbl.getContent();
            if (xdbl.isTemporary()) {
                opDoubleASized(ast, x, y, x, size, bsize);
                xdbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xdbl;
            } else {
                double[] res = new double[size];
                opDoubleASized(ast, x, y, res, size, bsize);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplBSized(ASTNode ast, DoubleImpl xdbl, DoubleImpl ydbl, int size, int asize, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            double[] y = ydbl.getContent();
            if (ydbl.isTemporary()) {
                opDoubleBSized(ast, x, y, y, size, asize);
                ydbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ydbl;
            } else {
                double[] res = new double[size];
                opDoubleBSized(ast, x, y, res, size, asize);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public abstract void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size);
        public abstract void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size);
        public abstract void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize);
        public abstract void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize);

        public RDouble opDoubleImplIntImplEqualSize(ASTNode ast, DoubleImpl xdbl, IntImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            int[] y = ydbl.getContent();
            if (xdbl.isTemporary()) {
                opDoubleIntEqualSize(ast, x, y, x, size);
                xdbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xdbl;
            } else {
                double[] res = new double[size];
                opDoubleIntEqualSize(ast, x, y, res, size);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opScalarDoubleIntImpl(ASTNode ast, double x, IntImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            int[] y = ydbl.getContent();
            double[] res = new double[size];
            opScalarDoubleInt(ast, x, y, res, size);
            return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
        }

        public RDouble opScalarDoubleIntImplCheckingNA(ASTNode ast, double x, IntImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {

            if (RDouble.RDoubleUtils.arithIsNA(x)) {
                // FIXME could re-use the array, but arithIsNA should be non-checking anyway
                return RDouble.RDoubleFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opScalarDoubleIntImpl(ast, x, ydbl, size, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplIntImplASized(ASTNode ast, DoubleImpl xdbl, IntImpl ydbl, int size, int bsize, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            int[] y = ydbl.getContent();
            if (xdbl.isTemporary()) {
                opDoubleIntASized(ast, x, y, x, size, bsize);
                xdbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xdbl;
            } else {
                double[] res = new double[size];
                opDoubleIntASized(ast, x, y, res, size, bsize);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opDoubleImplIntImplBSized(ASTNode ast, DoubleImpl xdbl, IntImpl ydbl, int size, int bsize, int[] dimensions, Names names, Attributes attributes) {
            double[] x = xdbl.getContent();
            int[] y = ydbl.getContent();
            double[] res = new double[size];
            opDoubleIntBSized(ast, x, y, res, size, bsize);
            return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
        }

        public abstract void opIntDoubleEqualSize(ASTNode ast, int[] x, double[] y, double[] res, int size);
        public abstract void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size);
        public abstract void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize);
        public abstract void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize);

        public RDouble opIntImplDoubleImplEqualSize(ASTNode ast, IntImpl xdbl, DoubleImpl ydbl, int size, int[] dimensions, Names names, Attributes attributes) {
            int[] x = xdbl.getContent();
            double[] y = ydbl.getContent();
            if (ydbl.isTemporary()) {
                opIntDoubleEqualSize(ast, x, y, y, size);
                xdbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ydbl;
            } else {
                double[] res = new double[size];
                opIntDoubleEqualSize(ast, x, y, res, size);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public RDouble opIntImplScalarDouble(ASTNode ast, IntImpl xdbl, double y, int size, int[] dimensions, Names names, Attributes attributes) {
            int[] x = xdbl.getContent();
            double[] res = new double[size];
            opIntScalarDouble(ast, x, y, res, size);
            return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
        }

        public RDouble opIntImplScalarDoubleCheckingNA(ASTNode ast, IntImpl xdbl, double y, int size, int[] dimensions, Names names, Attributes attributes) {
            if (RDouble.RDoubleUtils.arithIsNA(y)) {
                // FIXME could re-use the array, but arithIsNA should be non-checking anyway
                return RDouble.RDoubleFactory.getNAArray(size, dimensions, names, attributes);
            } else {
                return opIntImplScalarDouble(ast, xdbl, y, size, dimensions, names, attributes);
            }
        }

        public RDouble opIntImplDoubleImplASized(ASTNode ast, IntImpl xdbl, DoubleImpl ydbl, int size, int bsize, int[] dimensions, Names names, Attributes attributes) {
            int[] x = xdbl.getContent();
            double[] y = ydbl.getContent();
            double[] res = new double[size];
            opIntDoubleASized(ast, x, y, res, size, bsize);
            return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
        }

        public RDouble opIntImplDoubleImplBSized(ASTNode ast, IntImpl xdbl, DoubleImpl ydbl, int size, int asize, int[] dimensions, Names names, Attributes attributes) {
            int[] x = xdbl.getContent();
            double[] y = ydbl.getContent();
            if (ydbl.isTemporary()) {
                opIntDoubleBSized(ast, x, y, y, size, asize);
                ydbl.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return ydbl;
            } else {
                double[] res = new double[size];
                opIntDoubleBSized(ast, x, y, res, size, asize);
                return RDouble.RDoubleFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public abstract void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size);

        public RInt opIntImplSequenceASized(ASTNode ast, IntImpl xint, int yfrom, int yto, int ystep, int size, int[] dimensions, Names names, Attributes attributes) {
            int[] x = xint.getContent();
            if (xint.isTemporary()) {
                opIntImplSequenceASized(ast, x, yfrom, yto, ystep, x, size);
                xint.setNames(names).setDimensions(dimensions).setAttributes(attributes);
                return xint;
            } else {
                int[] res = new int[size];
                opIntImplSequenceASized(ast, x, yfrom, yto, ystep, res, size);
                return RInt.RIntFactory.getFor(res, dimensions, names, attributes);
            }
        }

        public abstract boolean returnsDouble();
    }

    public static final class Add extends ValueArithmetic {
        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            return a + c;
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            return b + d;
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            return new Complex(a + c, b + d);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            return a + b;
        }
        public static int add(int a, int b) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL
            int r = a + b;
            boolean bLTr = b < r;
            if (a > 0) {
                if (bLTr) {
                    return r;
                }
            } else {
                if (!bLTr) {
                    return r;
                }
            }
            return RInt.NA;
        }

        @Override
        public int op(ASTNode ast, int a, int b) {
            return add(a, b);
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            RContext.warning(ast, RError.INTEGER_OVERFLOW);
        }
        private static void add(double[] x, double[] y, double[] res, int rsize) {
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    res[i] = a + c;
                    res[j] = b + d;
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vzAdd(size, x, y, res);
                return;
            }
            add(x, y, res, size * 2);
        }

        private static void add(double[] x, double c, double d, double[] res, int rsize) {
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    res[i] = a + c;
                    res[j] = b + d;
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        private static void add(double a, double b, double[] y, double[] res, int rsize) {
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    res[i] = a + c;
                    res[j] = b + d;
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }


        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            add(x, c, d, res, size * 2);
        }

        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            add(a, b, y, res, size * 2);
        }

        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            int rsize = 2 * size;
            int bsize2 = 2 * bsize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[i];
                double b = x[i + 1];
                double c = y[j];
                double d = y[j + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    res[i] = a + c;
                    res[i + 1] = b + d;
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == bsize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            int rsize = 2 * size;
            int asize2 = 2 * asize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[j];
                double b = x[j + 1];
                double c = y[i];
                double d = y[i + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    res[i] = a + c;
                    res[i + 1] = b + d;
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == asize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vdAdd(size, x, y, res);
                return;
            }

            for (int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[i];
                double c = a + b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                    // FIXME There is no read of the result if result is NA but none of the arguments are
                } else {
                    res[i] = c;
                }
            }
        }

        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                if (RDouble.RDoubleUtils.arithIsNA(a)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + y;
                }
            }
        }

        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                if (RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x + b;
                }
            }
        }

        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = a + b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = a + b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + b;
                }
            }
        }

        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x + b;
                }
            }
        }

        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + b;
                }
            }
        }

        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + y;
                }
            }
        }

        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a + b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            int y = yfrom;
            boolean overflown = false;
            for (int i = 0; i < size; i++) {
                int a = x[i];
                if (a == RInt.NA) {
                    res[i] = RInt.NA;
                } else {
                    int r = add(a, y);
                    if (r == RInt.NA) {
                        overflown = true;
                    }
                    res[i] = r;
                }
                y += ystep;
                if (y > yto) {
                    y = yfrom;
                }
            }
            if (overflown) {
                emitOverflowWarning(ast);
            }
        }

        @Override
        public boolean returnsDouble() {
            return false;
        }
    }

    public static final class Sub extends ValueArithmetic {
        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            return a - c;
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            return b - d;
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            return new Complex(a - c, b - d);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            return a - b;
        }
        @Override
        public int op(ASTNode ast, int a, int b) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL
            int r = a - b;
            if ((a < 0 == b < 0) || (a < 0 == r < 0)) {
                return r;
            } else {
                return RInt.NA;
            }
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            RContext.warning(ast, RError.INTEGER_OVERFLOW);
        }

        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vzSub(size, x, y, res);
                return;
            }
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    res[i] = a - c;
                    res[j] = b - d;
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    res[i] = a - c;
                    res[j] = b - d;
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    res[i] = a - c;
                    res[j] = b - d;
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            int rsize = 2 * size;
            int bsize2 = 2 * bsize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[i];
                double b = x[i + 1];
                double c = y[j];
                double d = y[j + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    res[i] = a - c;
                    res[i + 1] = b - d;
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == bsize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            int rsize = 2 * size;
            int asize2 = 2 * asize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[j];
                double b = x[j + 1];
                double c = y[i];
                double d = y[i + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    res[i] = a - c;
                    res[i + 1] = b - d;
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == asize2) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vdSub(size, x, y, res);
                return;
            }
            for (int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[i];
                double c = a - b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                if (RDouble.RDoubleUtils.arithIsNA(a)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - y;
                }
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                if (RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x - b;
                }
            }
        }
        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = a - b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = a - b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - b;
                }
            }
        }

        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x - b;
                }
            }
        }

        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - b;
                }
            }
        }

        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - y;
                }
            }
        }

        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a - b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }
        @Override
        public boolean returnsDouble() {
            return false;
        }
    }

    public static double convertNaN(double d) {
        if (Double.isNaN(d)) {
            return Math.copySign(0, d);
        } else {
            return d;
        }
    }

    public static double convertInf(double d) {
        return Math.copySign(Double.isInfinite(d) ? 1 : 0, d);
    }


    public static final class Mult extends ValueArithmetic { // FIXME: will be slow for complex numbers (same calculations for real and imaginary parts)

        private static final double[] opTMP = new double[2];

        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            cmult(a, b, c, d, opTMP, 0);
            return opTMP[0];
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            cmult(a, b, c, d, opTMP, 0);
            return opTMP[1];
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            cmult(a, b, c, d, opTMP, 0);
            return new Complex(opTMP[0], opTMP[1]);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            return a * b;
        }
        @Override
        public int op(ASTNode ast, int a, int b) {
            long l = (long) a * (long) b;
            if (!(l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)) {
                return (int) l;
            } else {
                return RInt.NA;
            }
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            RContext.warning(ast, RError.INTEGER_OVERFLOW);
        }

        private static void cmult(double[] x, double[] y, double[] res, int rsize) {

            if (x == y) {
                int j = 1;
                for (int i = 0; i < rsize; i++, i++, j++, j++) {
                    double a = x[i];
                    double b = x[j];
                    if (!RComplexUtils.arithEitherIsNA(a, b)) {
                        Arithmetic.Pow.cpow2(a, b, res, i);
                    } else {
                        res[i] = RDouble.NA;
                        res[j] = RDouble.NA;
                    }
                }
                return;
            }

            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cmult(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        private static void cmult(double a, double b, double c, double d, double[] res, int offset) {
            // LICENSE: transcribed code from GCC, which is licensed under GPL
            // libgcc2

            double ac = a * c;
            double bd = b * d;
            double bc = b * c;
            double ad = a * d;

            double real = ac - bd;
            double imag = bc + ad;

            if (Double.isNaN(real) && Double.isNaN(imag)) {
                boolean recalc = false;
                double ra = a;
                double rb = b;
                double rc = c;
                double rd = d;
                if (Double.isInfinite(ra) || Double.isInfinite(rb)) {
                    ra = convertInf(ra);
                    rb = convertInf(rb);
                    rc = convertNaN(rc);
                    rd = convertNaN(rd);
                    recalc = true;
                }
                if (Double.isInfinite(rc) || Double.isInfinite(rd)) {
                    rc = convertInf(rc);
                    rd = convertInf(rd);
                    ra = convertNaN(ra);
                    rb = convertNaN(rb);
                    recalc = true;
                }
                if (!recalc && (Double.isInfinite(ac) || Double.isInfinite(bd) || Double.isInfinite(ad) || Double.isInfinite(bc))) {
                    ra = convertNaN(ra);
                    rb = convertNaN(rb);
                    rc = convertNaN(rc);
                    rd = convertNaN(rd);
                    recalc = true;
                }
                if (recalc) {
                    real = Double.POSITIVE_INFINITY * (ra * rc - rb * rd);
                    imag = Double.POSITIVE_INFINITY * (ra * rd + rb * rc);
                }
            }
            res[ offset ] = real;
            res[ offset + 1 ] = imag;
        }
        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vzMul(size, x, y, res);
                return;
            }
            cmult(x, y, res, 2 * size);
        }

        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    cmult(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    cmult(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            int rsize = 2 * size;
            int bsize2 = 2 * bsize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[i];
                double b = x[i + 1];
                double c = y[j];
                double d = y[j + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cmult(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == bsize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            int rsize = 2 * size;
            int asize2 = 2 * asize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[j];
                double b = x[j + 1];
                double c = y[i];
                double d = y[i + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cmult(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == asize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                if (x == y) {
                    MKL.vdSqr(size, x, res);
                } else {
                    MKL.vdMul(size, x, y, res);
                }
                return;
            }
            for (int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[i];
                double c = a * b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                if (RDouble.RDoubleUtils.arithIsNA(a)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * y;
                }
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                if (RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x * b;
                }
            }
        }
        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = a * b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = a * b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * b;
                }
            }
        }
        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x * b;
                }
            }
        }
        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * b;
                }
            }
        }
        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * y;
                }
            }
        }
        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a * b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }
        @Override
        public boolean returnsDouble() {
            return false;
        }
    }

    public static double chypot(double real, double imag) {
        // LICENSE: transcribed code from GCC, which is licensed under GPL

        // after libgcc2's x86 hypot - note the sign of NaN below (what GNU-R uses)
        // note that Math.hypot in Java is _very_ slow as it tries to be more precise
        double res = Math.sqrt(real * real + imag * imag);

        if (!isFinite(real) || !isFinite(imag)) {
            if (Double.isInfinite(real) || Double.isInfinite(imag)) {
                res = Double.POSITIVE_INFINITY;
            } else if (Double.isNaN(imag)) {
                res = imag;
            } else {
                res = real;
            }
        }

        return res;
    }

    public static final class Pow extends ValueArithmetic {

        private static void creciprocal(double[] z, int offset) {
            // LICENSE: this code is derived from the division code, which is transcribed code from GCC, which is licensed under GPL

            double c = z[offset];
            double d = z[offset + 1];
            double ratio;
            double denom;
            double x;
            double y;

            if (Math.abs(c) < Math.abs(d)) {
                ratio = c / d;
                denom = (c * ratio) + d;
                x = ratio / denom;
                y = -1 / denom;
            } else {
                ratio = d / c;
                denom = (d * ratio) + c;
                x = 1 / denom;
                y = -ratio / denom;
            }

            if (Double.isNaN(x) && Double.isNaN(y)) {
                if (c == 0.0 && d == 0.0) {
                    x = Math.copySign(Double.POSITIVE_INFINITY, c);
                    y = Math.copySign(Double.NaN, c);
                } else if (Double.isInfinite(c) || Double.isInfinite(d)) {
                    double rc = convertInf(c);
                    double rd = convertInf(d);
                    x = 0.0 * rc;
                    y = 0.0 * (-rd);
                }
            }
            z[offset] = x;
            z[offset + 1] = y;
        }

        private static final double[] cpowTMP = new double[2];

        // R_cpow_n in complex.c
        private static void cpow(double xr, double xi, int k, double[] z, int offset) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL

            if (k == 0) {
                z[offset] = 1;
                z[offset + 1] = 0; // FIXME: perhaps should rely on cleared z
                return;
            }
            if (k == 1) {
                z[offset] = xr;
                z[offset + 1] = xi;
                return;
            }
            if (k < 0) {
                cpow(xr, xi, -k, z, offset); // x^(-k)
                creciprocal(z, offset);
                return;
            }
            double[] x = cpowTMP; // "x"
            x[0] = xr;
            x[1] = xi;
            z[offset] = 1; // "z"
            z[offset + 1] = 0;
            int kk = k;
            while (kk > 0) {
                if ((kk & 1) != 0) {
                    // "z = z * X"
                    Mult.cmult(z[offset], z[offset + 1], x[0], x[1], z, offset);
                    if (kk == 1) {
                        break;
                    }
                }
                kk = kk / 2;
                // "X = X * X"
                cpow2(x[0], x[1], x, 0);
            }
        }

        private static void cpow(double xr, double xi, double yr, double yi, double[] z, int offset) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL
            if (xr == 0) {
                if (yi == 0) {
                    z[offset] = pow(0, yr);
                    z[offset + 1] = xi;
                } else {
                    z[offset] = Double.NaN;
                    z[offset + 1] = Double.NaN;
                }
                return;
            }

            if (yi == 0) {
                int k = (int) yr;
                if (yr == k && Math.abs(k) <= 65536) {
                    cpow(xr, xi, k, z, offset);
                    return;
                }
            }

            double zr = chypot(xr, xi);
            double zi = Math.atan2(xi, xr);
            double theta = zi * yr;
            double rho;
            if (yi == 0) {
                rho = pow(zr, yr);
            } else {
                zr = Math.log(zr);
                theta += zr * yi;
                rho = Math.exp(zr * yr - zi * yi);
            }
            z[offset] = rho * Math.cos(theta);
            z[offset + 1] = rho * Math.sin(theta);
        }

        private static final double[] opTMP = new double[2];

        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            cpow(a, b, c, d, opTMP, 0);
            return opTMP[0];
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            cpow(a, b, c, d, opTMP, 0); // FIXME: remember last values? would a boxed version be faster?
            return opTMP[1];
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            cpow(a, b, c, d, opTMP, 0); // FIXME: remember last values? would a boxed version be faster?
            return new Complex(opTMP[0], opTMP[1]);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL

            // NOTE: Math.pow (which uses FDLIBM) is very slow, the version written in assembly in GLIBC (SSE2 optimized) is about 2x faster

            // arithmetic.c (GNU R)
            if (b == 2) {
                return a * a;
            }
            if (a == 1 || b == 0) {
                return 1;
            }
            if (a == 0) {
                if (b > 0) {
                    return 0;
                }
                if (b < 0) {
                    return Double.POSITIVE_INFINITY;
                }
                return b;  // NA or NaN
            }
            if (isFinite(a) && isFinite(b)) {
                return pow(a, b);
            }
            if (RDouble.RDoubleUtils.isNAorNaN(a) || RDouble.RDoubleUtils.isNAorNaN(b)) {
                // NA check was before, so this can only mean NaN
                return a + b;
            }
            if (!isFinite(a)) {
                if (a > 0) { // Inf ^ y
                    if (b < 0) {
                        return 0;
                    }
                    return Double.POSITIVE_INFINITY;
                } else if (isFinite(b) && b == Math.floor(b)) { // (-Inf) ^ n
                    if (b < 0) {
                        return 0;
                    }
                    return fmod(ast, b, 2) != 0 ? a : -a;
                }
            }
            if (!isFinite(b)) {
                if (a >= 0) {
                    if (b > 0) {
                        return (a >= 1) ? Double.POSITIVE_INFINITY : 0;
                    }
                    return (a < 1) ? Double.POSITIVE_INFINITY : 0;
                }
            }
            return Double.NaN;
        }
        @Override
        public int op(ASTNode ast, int a, int b) {
            Utils.nyi("unreachable");
            return -1;
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            Utils.nyi("unreachable");
        }

        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) { // FIXME: check it has the same semantics as GNU-R
                MKL.vzPow(size, x, y, res);
                return;
            }
            int rsize = 2 * size;
            for (int i = 0; i < rsize; i += 2) {
                double xr = x[i];
                double xi = x[i + 1];
                double yr = y[i];
                double yi = y[i + 1];
                if (!RComplex.RComplexUtils.arithEitherIsNA(xr,  xi) && !RComplex.RComplexUtils.arithEitherIsNA(yr, yi)) {
                    cpow(xr, xi, yr, yi, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
            }
        }

        public static void cpow2(double a, double b, double[] res, int offset) {
            // LICENSE: this code is derived from the multiplication code, which is transcribed code from GCC, which is licensed under GPL

            double a2 = a * a;
            double b2 = b * b;
            double ab = a * b;

            double real = a2 - b2;
            double imag = 2 * ab;

            if (Double.isNaN(real) && Double.isNaN(imag)) {
                boolean recalc = false;
                double ra = a;
                double rb = b;
                if (Double.isInfinite(ra) || Double.isInfinite(rb)) {
                    ra = convertInf(ra);
                    rb = convertInf(rb);
                    recalc = true;
                }
                if (!recalc && (Double.isInfinite(a2) || Double.isInfinite(b2) || Double.isInfinite(ab))) {
                    ra = convertNaN(ra);
                    rb = convertNaN(rb);
                    recalc = true;
                }
                if (recalc) {
                    real = Double.POSITIVE_INFINITY * (ra * ra - rb * rb);
                    imag = Double.POSITIVE_INFINITY * (ra * rb);
                }
            }
            res[ offset ] = real;
            res[ offset + 1 ] = imag;
        }

        private static void cpow(double[] x, double yr, double yi, double[] res, int rsize) {
            if (yr == 2 && yi == 0) {
                for (int i = 0; i < rsize; i += 2) {
                    double xr = x[i];
                    double xi = x[i + 1];
                    if (!RComplex.RComplexUtils.arithEitherIsNA(xr, xi)) {
                        cpow2(xr, xi, res, i);
                    } else {
                        res[i] = RDouble.NA;
                        res[i + 1] = RDouble.NA;
                    }
                }
            } else {
                for (int i = 0; i < rsize; i += 2) {
                    double xr = x[i];
                    double xi = x[i + 1];
                    if (!RComplex.RComplexUtils.arithEitherIsNA(xr, xi)) {
                        cpow(xr, xi, yr, yi, res, i); // FIXME: extract some checks on the exponent here
                    } else {
                        res[i] = RDouble.NA;
                        res[i + 1] = RDouble.NA;
                    }

                }
            }
        }

        private static void cpow(double xr, double xi, double[] y, double[] res, int rsize) {
            for (int i = 0; i < rsize; i += 2) {
                double yr = y[i];
                double yi = y[i + 1];
                if (!RComplex.RComplexUtils.arithEitherIsNA(xr, xi)) {
                    cpow(xr, xi, yr, yi, res, i); // FIXME: extract some checks on the exponent here
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }

            }
        }

        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            cpow(x, c, d, res, size * 2);
        }

        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            cpow(a, b, y, res, size * 2);
        }

        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            int rsize = 2 * size;
            int bsize2 = 2 * bsize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[i];
                double b = x[i + 1];
                double c = y[j];
                double d = y[j + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cpow(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == bsize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            int rsize = 2 * size;
            int asize2 = 2 * asize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[j];
                double b = x[j + 1];
                double c = y[i];
                double d = y[i + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cpow(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == asize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vdPow(size, x, y, res);
                return;
            }
            if (!RContext.hasSystemLibs()) {
                for (int i = 0; i < size; i++) {
                    double a = x[i];
                    double b = y[i];
                    double c = pow(a, b);
                    if (RDouble.RDoubleUtils.arithIsNA(c)) {
                        if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                            res[i] = RDouble.NA;
                        }
                    } else {
                        res[i] = c;
                    }
                }
            } else {
                SystemLibs.pow(x, y, res, size);
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vdPowx(size, x, y, res);
                return;
            }
            if (!RContext.hasSystemLibs()) {
                for (int i = 0; i < size; i++) {
                    double a = x[i];
                    if (RDouble.RDoubleUtils.arithIsNA(a)) {
                        res[i] = RDouble.NA;
                    } else {
                        res[i] = pow(a, y);
                    }
                }
            } else {
                SystemLibs.pow(x, y, res, size);
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            if (!RContext.hasSystemLibs()) {
                for (int i = 0; i < size; i++) {
                    double b = y[i];
                    if (RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    } else {
                        res[i] = pow(x, b);
                    }
                }
            } else {
                SystemLibs.pow(x, y, res, size);
            }
        }
        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = pow(a, b); // FIXME: should move the loop to native code
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = pow(a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, b);
                }
            }
        }

        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(x, b);
                }
            }
        }

        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, b);
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, b);
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, b);
                }
            }
        }
        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, y);
                }
            }
        }
        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, b);
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = pow(a, b);
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }

        @Override
        public boolean returnsDouble() {
            return true;
        }
    }

    public static boolean isFinite(double d) {
        // NOTE: this is currently equivalent to RDoubleUtils.isFinite, but that can change in the future
        return !Double.isInfinite(d) && !Double.isNaN(d);
    }

    public static double pow(double a, double b) {
        if (!RContext.hasSystemLibs()) {
            return Math.pow(a, b);
        } else {
            return SystemLibs.pow(a, b);
        }
    }

    public static void cdiv(double a, double b, double c, double d, double[] res, int offset) {
        // LICENSE: transcribed code from GCC, which is licensed under GPL
        // libgcc2

        double ratio;
        double denom;
        double x;
        double y;

        if (Math.abs(c) < Math.abs(d)) {
            ratio = c / d;
            denom = (c * ratio) + d;
            x = ((a * ratio) + b) / denom;
            y = ((b * ratio) - a) / denom;
        } else {
            ratio = d / c;
            denom = (d * ratio) + c;
            x = ((b * ratio) + a) / denom;
            y = (b - (a * ratio)) / denom;
        }

        if (Double.isNaN(x) && Double.isNaN(y)) {
            if (c == 0.0 && d == 0.0 && (!Double.isNaN(a) || !Double.isNaN(b))) {
                x = Math.copySign(Double.POSITIVE_INFINITY, c) * a;
                y = Math.copySign(Double.POSITIVE_INFINITY, c) * b;
            } else if ((Double.isInfinite(a) || Double.isInfinite(b)) && isFinite(c) && isFinite(d)) {
                double ra = convertInf(a);
                double rb = convertInf(b);
                x = Double.POSITIVE_INFINITY * (ra * c + rb * d);
                y = Double.POSITIVE_INFINITY * (rb * c - ra * d);
            } else if ((Double.isInfinite(c) || Double.isInfinite(d)) && isFinite(a) && isFinite(b)) {
                double rc = convertInf(c);
                double rd = convertInf(d);
                x = 0.0 * (a * rc + b * rd);
                y = 0.0 * (b * rc - a * rd);
            }
        }
        res[offset] = x;
        res[offset + 1] = y;
    }

    public static final class Div extends ValueArithmetic {

        private static final double[] opTMP = new double[2];

        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            cdiv(a, b, c, d, opTMP, 0);
            return opTMP[0];
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            cdiv(a, b, c, d, opTMP, 0);
            return opTMP[1];
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            cdiv(a, b, c, d, opTMP, 0);
            return new Complex(opTMP[0], opTMP[1]);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            return a / b; // FIXME: check that the R rules correspond to Java
        }
        @Override
        public int op(ASTNode ast, int a, int b) {
            Utils.nyi("unreachable");
            return -1;
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            Utils.nyi("unreachable");
        }

        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vzDiv(size, x, y, res);
                return;
            }
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cdiv(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double a = x[i];
                double b = x[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    cdiv(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            int rsize = size * 2;
            int j = 1;
            for (int i = 0; i < rsize; i++, i++, j++, j++) {
                double c = y[i];
                double d = y[j];
                if (!RComplexUtils.arithEitherIsNA(a, b)) {
                    cdiv(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[j] = RDouble.NA;
                }
            }
        }

        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            int rsize = 2 * size;
            int bsize2 = 2 * bsize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[i];
                double b = x[i + 1];
                double c = y[j];
                double d = y[j + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cdiv(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == bsize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            int rsize = 2 * size;
            int asize2 = 2 * asize;
            for(int i = 0; i < rsize; i += 2) {
                double a = x[j];
                double b = x[j + 1];
                double c = y[i];
                double d = y[i + 1];
                if (!RComplexUtils.arithEitherIsNA(a, b) && !RComplexUtils.arithEitherIsNA(c, d)) {
                    cdiv(a, b, c, d, res, i);
                } else {
                    res[i] = RDouble.NA;
                    res[i + 1] = RDouble.NA;
                }
                j += 2;
                if (j == asize2) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
                MKL.vdDiv(size, x, y, res);
                return;
            }
            for (int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[i];
                double c = a / b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                if (RDouble.RDoubleUtils.arithIsNA(a)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / y;
                }
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                if (RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x / b;
                }
            }
        }
        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = a / b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = a / b;
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / b;
                }
            }
        }
        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = x / b;
                }
            }
        }
        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / b;
                }
            }
        }
        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / y;
                }
            }
        }
        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / b;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = a / b;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }
        @Override
        public boolean returnsDouble() {
            return true;
        }
    }

    public static final class IntegerDiv extends ValueArithmetic {
        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL
            double q = a / b;
            if (b != 0) {
                double qfloor = Math.floor(q);
                double tmp = a - qfloor * b; // FIXME: this is R implementation, check if we can avoid this in Java
                return qfloor + Math.floor(tmp / b);

            } else {
                return q;
            }
        }
        @Override
        public int op(ASTNode ast, int a, int b) {
            if (b != 0) {
                return (int) Math.floor((double) a / (double) b); // FIXME: this is R implementation, can we do faster without floating point?
            } else {
                return RInt.NA;
            }
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            // no warning
        }
        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            throw RError.getUnimplementedComplex(ast);
        }

        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            throw RError.getUnimplementedComplex(ast);
        }

        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            throw RError.getUnimplementedComplex(ast);
        }

        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[i];
                double c = op(ast, a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                if (RDouble.RDoubleUtils.arithIsNA(a)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, y);
                }
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                if (RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, x,  b);
                }
            }
        }
        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = op(ast, a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = op(ast, a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, b);
                }
            }
        }

        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, x, b);
                }
            }
        }
        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, b);
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, b);
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, b);
                }
            }
        }
        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, y);
                }
            }
        }
        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, b);
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = op(ast, a, b);
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }
        @Override
        public boolean returnsDouble() {
            return false;
        }
    }

    public static double fmod(ASTNode ast, double a, double b) { // FIXME: this is R implementation, can we do faster in Java?
        // LICENSE: transcribed code from GNU R, which is licensed under GPL
        double q = a / b;
        if (b != 0) {
            double tmp = a - Math.floor(q) * b;
            if (RDouble.RDoubleUtils.isFinite(q) && Math.abs(q) > 1 / RDouble.EPSILON) {
                RContext.warning(ast, RError.ACCURACY_MODULUS);
            }
            return tmp - Math.floor(tmp / b) * b;
        } else {
            return RDouble.NaN;
        }
    }

    public static final class Mod extends ValueArithmetic {
        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public Complex opComplex(ASTNode ast, double a, double b, double c, double d) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public double op(ASTNode ast, double a, double b) {
            return fmod(ast, a, b);
        }
        @Override
        public int op(ASTNode ast, int a, int b) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL
            if (b != 0) {
                if (a >= 0 && b > 0) {
                    return a % b;
                } else {
                    return (int) fmod(ast, a, b);
                }
            } else {
                return RInt.NA;
            }
        }
        @Override
        public void emitOverflowWarning(ASTNode ast) {
            // no warning
        }
        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opComplexASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opComplexBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            throw RError.getUnimplementedComplex(ast);
        }
        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            if (!RContext.hasSystemLibs()) {
                for (int i = 0; i < size; i++) {
                    double a = x[i];
                    double b = y[i];
                    double c = fmod(ast, a, b);
                    if (RDouble.RDoubleUtils.arithIsNA(c)) {
                        if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                            res[i] = RDouble.NA;
                        }
                    } else {
                        res[i] = c;
                    }
                }
            } else { // FIXME: check if it won't be better to use the Java version for short vectors (branch above)
                boolean warn = SystemLibs.fmod(x, y, res, size);
                if (warn) {
                    RContext.warning(ast, RError.ACCURACY_MODULUS); // FIXME: will only appear once per vector
                }
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                if (RDouble.RDoubleUtils.arithIsNA(a)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a,  y);
                }
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                if (RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, x, b);
                }
            }
        }
        @Override
        public void opDoubleASized(ASTNode ast, double[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[j];
                double c = fmod(ast, a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleBSized(ASTNode ast, double[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                double b = y[i];
                double c = fmod(ast, a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }
        @Override
        public void opDoubleIntEqualSize(ASTNode ast, double[] x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, b);
                }
            }
        }

        @Override
        public void opScalarDoubleInt(ASTNode ast, double x, int[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int b = y[i];

                if (b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, x, b);
                }
            }
        }

        @Override
        public void opDoubleIntASized(ASTNode ast, double[] x, int[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[i];
                int b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, b);
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opDoubleIntBSized(ASTNode ast, double[] x, int[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                double a = x[j];
                int b = y[i];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, b);
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntDoubleEqualSize(ASTNode ast, int [] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, b);
                }
            }
        }

        @Override
        public void opIntScalarDouble(ASTNode ast, int[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                int a = x[i];

                if (a == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, y);
                }
            }
        }

        @Override
        public void opIntDoubleASized(ASTNode ast, int[] x, double[] y, double[] res, int size, int bsize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[i];
                double b = y[j];

                if (RDouble.RDoubleUtils.arithIsNA(a) || b == RInt.NA) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, b);
                }
                j++;
                if (j == bsize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntDoubleBSized(ASTNode ast, int[] x, double[] y, double[] res, int size, int asize) {
            int j = 0;
            for(int i = 0; i < size; i++) {
                int a = x[j];
                double b = y[i];

                if (a == RInt.NA || RDouble.RDoubleUtils.arithIsNA(b)) {
                    res[i] = RDouble.NA;
                } else {
                    res[i] = fmod(ast, a, b);
                }
                j++;
                if (j == asize) {
                    j = 0;
                }
            }
        }

        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }
        @Override
        public boolean returnsDouble() {
            return false;
        }
    }

    public static final Add ADD = new Add();
    public static final Sub SUB = new Sub();
    public static final Mult MULT = new Mult();
    public static final Pow POW = new Pow();
    public static final Div DIV = new Div();
    public static final IntegerDiv INTEGER_DIV = new IntegerDiv();
    public static final Mod MOD = new Mod();


    public abstract static class VectorArithmetic {

        public abstract RComplex complexBinary(RComplex a, RComplex b, ValueArithmetic arit, ASTNode ast);

        public abstract RDouble doubleBinary(RDouble a, RDouble b, ValueArithmetic arit, ASTNode ast);
        public abstract RDouble doubleBinary(RDouble a, RInt b, ValueArithmetic arit, ASTNode ast);
        public abstract RDouble doubleBinary(RInt a, RDouble b, ValueArithmetic arit, ASTNode ast);

        public abstract RInt intBinary(RInt a, RInt b, ValueArithmetic arit, ASTNode ast);

    }

    public static final class LazyVectorArithmetic extends VectorArithmetic {

        @Override
        public RComplex complexBinary(RComplex a, RComplex b, ValueArithmetic arit, ASTNode ast) {
            int depth = 0;
            if (LIMIT_VIEW_DEPTH) {
                depth = complexViewDepth(a) + complexViewDepth(b) + 1;
            }
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();
            RComplex res;

            if (na == nb) {
                res = new ComplexViewForComplexComplex.EqualSize(a, b, dim, names, attributes, na, depth, arit, ast);
            } else if (nb == 1 && na > 0) {
                res = new ComplexViewForComplexComplex.VectorScalar(a, b, dim, names, attributes, na, depth, arit, ast);
            } else if (na == 1 && nb > 0) {
                res = new ComplexViewForComplexComplex.ScalarVector(a, b, dim, names, attributes, nb, depth, arit, ast);
            } else {
                int n = resultSize(ast, na, nb);
                if (n == na) {
                    res = new ComplexViewForComplexComplex.GenericASized(a, b, dim, names, attributes, n, depth, arit, ast);
                } else {
                    res = new ComplexViewForComplexComplex.GenericBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                }
            }
            res = TracingView.ViewTrace.trace(res);
            if (EAGER || (LIMIT_VIEW_DEPTH && (depth > MAX_VIEW_DEPTH)) || (na == 1 && nb == 1)) {
                return res.materialize();
            }
            return res;
        }

        @Override
        public RDouble doubleBinary(RDouble a, RDouble b, ValueArithmetic arit, ASTNode ast) {
            int depth = 0;
            if (LIMIT_VIEW_DEPTH) {
                depth = doubleViewDepth(a) + doubleViewDepth(b) + 1;
            }
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();
            RDouble res;

            if (na == nb) {
//                if (arit == POW && na > 1) {
//                    // FIXME: this is a hack.. POW is so expensive though that this is likely to pay off
//                    return arit.opDoubleImplEqualSize(ast, (DoubleImpl) a.materialize(), (DoubleImpl) b.materialize(), na, dim, names, attributes);
//                }
//                if (a instanceof DoubleImpl && b instanceof DoubleImpl && (a.isTemporary() || b.isTemporary())) {
//                    // FIXME: do this only for Pow? sometimes? the check may be costly for short vectors
//                    return arit.opDoubleImplEqualSize(ast, (DoubleImpl) a, (DoubleImpl) b, na, dim, names, attributes);
//                }
                res = new DoubleViewForDoubleDouble.EqualSizeVectorVector(a, b, dim, names, attributes, na, depth, arit, ast);
            } else if (nb == 1 && na > 0) {
//                if (arit == POW && na > 1) {
//                    return arit.opDoubleImplScalarCheckingNA(ast, (DoubleImpl) a.materialize(), b.getDouble(0), na, dim, names, attributes);
//                }
//                if (na > 1 && a instanceof DoubleImpl && a.isTemporary()) {
//                    // FIXME: re-visit the condition, like above
//                    return arit.opDoubleImplScalar(ast, (DoubleImpl) a, b.getDouble(0), na, dim, names, attributes);
//                }
                res = new DoubleViewForDoubleDouble.VectorScalar(a, b, dim, names, attributes, na, depth, arit, ast);
            } else if (na == 1 && nb > 0) {
                res = new DoubleViewForDoubleDouble.ScalarVector(a, b, dim, names, attributes, nb, depth, arit, ast);
            } else {
                int n = resultSize(ast, na, nb);
                if (n == na) {
                    res = new DoubleViewForDoubleDouble.GenericASized(a, b, dim, names, attributes, n, depth, arit, ast);
                } else {
                    res = new DoubleViewForDoubleDouble.GenericBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                }
            }
            res = TracingView.ViewTrace.trace(res);
            if (EAGER || (LIMIT_VIEW_DEPTH && (depth > MAX_VIEW_DEPTH)) || (na == 1 && nb == 1)) {
                return res.materialize();
            }
            return res;
        }

        // FIXME: try to reduce copy-paste, but may not be easy without harming performance
        @Override
        public RDouble doubleBinary(RDouble a, RInt b, ValueArithmetic arit, ASTNode ast) {
            int depth = 0;
            if (LIMIT_VIEW_DEPTH) {
                depth = doubleViewDepth(a) + intViewDepth(b) + 1;
            }
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();
            RDouble res;

            if (na == nb) {
                if (RIntSimpleRange.isInstance(b)) {
                    res = new DoubleViewForDoubleInt.EqualSizeVectorSimpleRange(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSequence.isInstance(b)) {
                    res = new DoubleViewForDoubleInt.EqualSizeVectorSequence(a, b, dim, names, attributes, na, depth, arit, ast);
                } else {
                    res = new DoubleViewForDoubleInt.EqualSizeVectorVector(a, b, dim, names, attributes, na, depth, arit, ast);
                }
            } else if (nb == 1 && na > 0) {
                res = new DoubleViewForDoubleDouble.VectorScalar(a, b.asDouble(), dim, names, attributes, na, depth, arit, ast);
            } else if (na == 1 && nb > 0) {
                if (RIntSimpleRange.isInstance(b)) {
                    res = new DoubleViewForDoubleInt.ScalarSimpleRange(a, b, dim, names, attributes, nb, depth, arit, ast);
                } else if (RIntSequence.isInstance(b)) {
                    res = new DoubleViewForDoubleInt.ScalarSequence(a, b, dim, names, attributes, nb, depth, arit, ast);
                } else {
                    res = new DoubleViewForDoubleDouble.ScalarVector(a, b.asDouble(), dim, names, attributes, nb, depth, arit, ast);
                }
            } else {
                int n = resultSize(ast, na, nb);
                if (RIntSimpleRange.isInstance(b)) {
                    if (n == na) {
                        res = new DoubleViewForDoubleInt.VectorSimpleRangeASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new DoubleViewForDoubleInt.VectorSimpleRangeBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else if (RIntSequence.isInstance(b)) {
                    if (n == na) {
                        res = new DoubleViewForDoubleInt.VectorSequenceASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new DoubleViewForDoubleInt.VectorSequenceBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else {
                    if (n == na) {
                        res = new DoubleViewForDoubleDouble.GenericASized(a, b.asDouble(), dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new DoubleViewForDoubleDouble.GenericBSized(a, b.asDouble(), dim, names, attributes, n, depth, arit, ast);
                    }
                }
            }
            res = TracingView.ViewTrace.trace(res);
            if (EAGER || (LIMIT_VIEW_DEPTH && (depth > MAX_VIEW_DEPTH)) ||  (na == 1 && nb == 1)) {
                return res.materialize();
            }
            return res;
        }

        // FIXME: try to reduce copy-paste, but may not be easy without harming performance
        @Override
        public RDouble doubleBinary(RInt a, RDouble b, ValueArithmetic arit, ASTNode ast) {
            int depth = 0;
            if (LIMIT_VIEW_DEPTH) {
                depth = intViewDepth(a) + doubleViewDepth(b) + 1;
            }
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();
            RDouble res;

            if (na == nb) {
                if (RIntSimpleRange.isInstance(a)) {
                    res = new DoubleViewForIntDouble.EqualSizeSimpleRangeVector(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSequence.isInstance(a)) {
                    res = new DoubleViewForIntDouble.EqualSizeSequenceVector(a, b, dim, names, attributes, na, depth, arit, ast);
                } else {
                    res = new DoubleViewForIntDouble.EqualSizeVectorVector(a, b, dim, names, attributes, na, depth, arit, ast);
                }
            } else if (nb == 1 && na > 0) {
                if (RIntSimpleRange.isInstance(a)) {
                    res = new DoubleViewForIntDouble.SimpleRangeScalar(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSequence.isInstance(a)) {
                    res = new DoubleViewForIntDouble.SequenceScalar(a, b, dim, names, attributes, na, depth, arit, ast);
                } else {
                    res = new DoubleViewForIntDouble.VectorScalar(a, b, dim, names, attributes, na, depth, arit, ast);
                }
            } else if (na == 1 && nb > 0) {
                res = new DoubleViewForDoubleDouble.ScalarVector(a.asDouble(), b, dim, names, attributes, nb, depth, arit, ast);
            } else {
                int n = resultSize(ast, na, nb);
                if (RIntSimpleRange.isInstance(a)) {
                    if (n == na) {
                        res = new DoubleViewForIntDouble.SimpleRangeVectorASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new DoubleViewForIntDouble.SimpleRangeVectorBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else if (RIntSequence.isInstance(a)) {
                    if (n == na) {
                        res = new DoubleViewForIntDouble.SequenceVectorASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new DoubleViewForIntDouble.SequenceVectorBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else {
                    if (n == na) {
                        res = new DoubleViewForDoubleDouble.GenericASized(a.asDouble(), b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new DoubleViewForDoubleDouble.GenericBSized(a.asDouble(), b, dim, names, attributes, n, depth, arit, ast);
                    }
                }
            }
            res = TracingView.ViewTrace.trace(res);
            if (EAGER || (LIMIT_VIEW_DEPTH && (depth > MAX_VIEW_DEPTH)) ||  (na == 1 && nb == 1)) {
                return res.materialize();
            }
            return res;
        }

        // FIXME: it might pay off to use some of the optimizations only with sufficiently large vectors, so e.g. conditionally on the size
        // that is being checked already anyway
        @Override
        public RInt intBinary(RInt a, RInt b, ValueArithmetic arit, ASTNode ast) {
            assert Utils.check(!arit.returnsDouble());

            int depth = 0;
            if (LIMIT_VIEW_DEPTH) {
                depth = intViewDepth(a) + intViewDepth(b) + 1;
            }
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();
            RInt res;

            if (na == nb) {
                if (RIntSimpleRange.isInstance(b)) {
                    res = new IntViewForIntInt.EqualSizeIntSimpleRange(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSimpleRange.isInstance(a)) {
                    res = new IntViewForIntInt.EqualSizeSimpleRangeInt(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSequence.isInstance(b)) {
                    res = new IntViewForIntInt.EqualSizeIntSequence(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSequence.isInstance(a)) {
                    res = new IntViewForIntInt.EqualSizeSequenceInt(a, b, dim, names, attributes, na, depth, arit, ast);
                } else {
                    res = new IntViewForIntInt.EqualSize(a, b, dim, names, attributes, na, depth, arit, ast);
                }
            } else if (nb == 1 && na > 0) {
                if (RIntSimpleRange.isInstance(a)) {
                    res = new IntViewForIntInt.SimpleRangeScalar(a, b, dim, names, attributes, na, depth, arit, ast);
                } else if (RIntSequence.isInstance(a)) {
                    res = new IntViewForIntInt.SequenceScalar(a, b, dim, names, attributes, na, depth, arit, ast);
                } else {
                    res = new IntViewForIntInt.VectorScalar(a, b, dim, names, attributes, na, depth, arit, ast);
                }
            } else if (na == 1 && nb > 0) {
                if (RIntSimpleRange.isInstance(b)) {
                    res = new IntViewForIntInt.ScalarSimpleRange(a, b, dim, names, attributes, nb, depth, arit, ast);
                } else if (RIntSequence.isInstance(b)) {
                    res = new IntViewForIntInt.ScalarSequence(a, b, dim, names, attributes, nb, depth, arit, ast);
                } else {
                    res = new IntViewForIntInt.ScalarVector(a, b, dim, names, attributes, nb, depth, arit, ast);
                }
            } else {
                int n = resultSize(ast, na, nb);
                if (RIntSimpleRange.isInstance(b)) {
                    if (na == n) {
                        res = new IntViewForIntInt.VectorSimpleRangeASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new IntViewForIntInt.VectorSimpleRangeBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else if (RIntSimpleRange.isInstance(a)) {
                    if (na == n) {
                        res = new IntViewForIntInt.SimpleRangeVectorASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new IntViewForIntInt.SimpleRangeVectorBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else if (RIntSequence.isInstance(b)) {
                    // HACK HACK just to test if this would help in one benchmark
//                    if (na > 1 && arit == ADD && a.isTemporary() && n == na) {
//                        return hackAddTemporaryIntandSequence(a, (RIntSequence) b, na, nb, arit, ast);
//                    }

                    // TODO: why is this actually slowing us down?
//                    if (a instanceof IntImpl && a.isTemporary() && n == na) {
//                        return arit.op(ast, (IntImpl) a, (RIntSequence) b, n, dim, names, attributes);
//                    }

                    if (na == n) {
                        res = new IntViewForIntInt.VectorSequenceASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new IntViewForIntInt.VectorSequenceBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else if (RIntSequence.isInstance(a)) {
                    if (na == n) {
                        res = new IntViewForIntInt.SequenceVectorASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new IntViewForIntInt.SequenceVectorBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                } else {
                    if (n == na) {
                        res = new IntViewForIntInt.GenericASized(a, b, dim, names, attributes, n, depth, arit, ast);
                    } else {
                        res = new IntViewForIntInt.GenericBSized(a, b, dim, names, attributes, n, depth, arit, ast);
                    }
                }
            }
            res = TracingView.ViewTrace.trace(res);
            if (EAGER || (LIMIT_VIEW_DEPTH && (depth > MAX_VIEW_DEPTH)) ||  (na == 1 && nb == 1)) {
                return res.materialize();
            }
            return res;
        }
    }

    public static final class EagerVectorArithmetic extends VectorArithmetic {

        @Override
        public RComplex complexBinary(RComplex a, RComplex b, ValueArithmetic arit, ASTNode ast) {
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();

            if (na == nb) {
                if (na > 1) {
                    return arit.opComplexImplEqualSize(ast, (ComplexImpl) a.materialize(), (ComplexImpl) b.materialize(), na, dim, names, attributes);
                } else {
                    // scalars
                    Complex acomp = a.getComplex(0);
                    Complex bcomp = b.getComplex(0);
                    Complex res = arit.opComplexCheckingNA(ast, acomp.realValue(), acomp.imagValue(), bcomp.realValue(), bcomp.imagValue());
                    // FIXME: it may really be worth having Complex == ScalarComplexImpl
                    return RComplex.RComplexFactory.getScalar(res.realValue(), res.imagValue(), dim, names, attributes);
                }
            } else if (nb == 1) {
                Complex bcomp = b.getComplex(0);
                return arit.opComplexImplScalarCheckingNA(ast, (ComplexImpl) a.materialize(), bcomp.realValue(), bcomp.imagValue(), na, dim, names, attributes);
            } else if (na == 1) {
                Complex acomp = a.getComplex(0);
                return arit.opScalarComplexImplCheckingNA(ast, acomp.realValue(), acomp.imagValue(), (ComplexImpl) b.materialize(), nb, dim, names, attributes);
            } else {
                int n = resultSize(ast, na, nb);
                if (n == na) {
                    return arit.opComplexImplASized(ast, (ComplexImpl) a.materialize(), (ComplexImpl) b.materialize(), n, nb, dim, names, attributes);
                } else {
                    return arit.opComplexImplBSized(ast, (ComplexImpl) a.materialize(), (ComplexImpl) b.materialize(), n, na, dim, names, attributes);
                }
            }
        }

        @Override
        public RDouble doubleBinary(RDouble a, RDouble b, ValueArithmetic arit, ASTNode ast) {
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();

            if (na == nb) {
                if (na > 1) {
                    return arit.opDoubleImplEqualSize(ast, (DoubleImpl) a.materialize(), (DoubleImpl) b.materialize(), na, dim, names, attributes);
                } else {
                    // scalars
                    return RDouble.RDoubleFactory.getScalar(arit.opCheckingNA(ast, a.getDouble(0), b.getDouble(0)), dim, names, attributes);
                }
            } else if (nb == 1) {
                return arit.opDoubleImplScalarCheckingNA(ast, (DoubleImpl) a.materialize(), b.getDouble(0), na, dim, names, attributes);
            } else if (na == 1) {
                return arit.opScalarDoubleImplCheckingNA(ast, a.getDouble(0), (DoubleImpl) b.materialize(), nb, dim, names, attributes);
            } else {
                int n = resultSize(ast, na, nb);
                if (n == na) {
                    return arit.opDoubleImplASized(ast, (DoubleImpl) a.materialize(), (DoubleImpl) b.materialize(), n, nb, dim, names, attributes);
                } else {
                    return arit.opDoubleImplBSized(ast, (DoubleImpl) a.materialize(), (DoubleImpl) b.materialize(), n, na, dim, names, attributes);
                }
            }
        }

        @Override
        public RDouble doubleBinary(RDouble a, RInt b, ValueArithmetic arit, ASTNode ast) { // TODO: int sequences
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();

            if (na == nb) {
                if (na > 1) {
                    return arit.opDoubleImplIntImplEqualSize(ast, (DoubleImpl) a.materialize(), (IntImpl) b.materialize(), na, dim, names, attributes);
                } else {
                    // scalars
                    return RDouble.RDoubleFactory.getScalar(arit.opCheckingNA(ast, a.getDouble(0), b.getInt(0)), dim, names, attributes);
                }
            } else if (nb == 1) {
                return arit.opDoubleImplScalarIntCheckingNA(ast, (DoubleImpl) a.materialize(), b.getInt(0), na, dim, names, attributes);
            } else if (na == 1) {
                return arit.opScalarDoubleIntImplCheckingNA(ast, a.getDouble(0), (IntImpl) b.materialize(), nb, dim, names, attributes);
            } else {
                int n = resultSize(ast, na, nb);
                if (n == na) {
                    return arit.opDoubleImplIntImplASized(ast, (DoubleImpl) a.materialize(), (IntImpl) b.materialize(), n, nb, dim, names, attributes);
                } else {
                    return arit.opDoubleImplIntImplBSized(ast, (DoubleImpl) a.materialize(), (IntImpl) b.materialize(), n, na, dim, names, attributes);
                }
            }
        }

        @Override
        public RDouble doubleBinary(RInt a, RDouble b, ValueArithmetic arit, ASTNode ast) { // TODO: int sequences
            int[] dim = resultDimensions(ast, a, b);
            Names names = resultNames(ast, a, b);
            Attributes attributes = resultAttributes(ast, a, b);
            int na = a.size();
            int nb = b.size();

            if (na == nb) {
                if (na > 1) {
                    return arit.opIntImplDoubleImplEqualSize(ast, (IntImpl) a.materialize(), (DoubleImpl) b.materialize(), na, dim, names, attributes);
                } else {
                    // scalars
                    return RDouble.RDoubleFactory.getScalar(arit.opCheckingNA(ast, a.getInt(0), b.getDouble(0)), dim, names, attributes);
                }
            } else if (nb == 1) {
                return arit.opIntImplScalarDoubleCheckingNA(ast, (IntImpl) a.materialize(), b.getDouble(0), na, dim, names, attributes);
            } else if (na == 1) {
                return arit.opScalarIntDoubleImplCheckingNA(ast, a.getInt(0), (DoubleImpl) b.materialize(), nb, dim, names, attributes);
            } else {
                int n = resultSize(ast, na, nb);
                if (n == na) {
                    return arit.opIntImplDoubleImplASized(ast, (IntImpl) a.materialize(), (DoubleImpl) b.materialize(), n, nb, dim, names, attributes);
                } else {
                    return arit.opIntImplDoubleImplBSized(ast, (IntImpl) a.materialize(), (DoubleImpl) b.materialize(), n, na, dim, names, attributes);
                }
            }
        }

        @Override
        public RInt intBinary(RInt a, RInt b, ValueArithmetic arit, ASTNode ast) {
            return LAZY_VECTOR.intBinary(a, b, arit, ast).materialize();
        }

    }

    public static final LazyVectorArithmetic LAZY_VECTOR = new LazyVectorArithmetic();
    public static final EagerVectorArithmetic EAGER_VECTOR = new EagerVectorArithmetic();

    public static VectorArithmetic chooseVectorArithmetic(Object leftTemplate, Object rightTemplate, ValueArithmetic arit) {

        if (leftTemplate instanceof RArray && rightTemplate instanceof RArray) {
            int lsize = ((RArray) leftTemplate).size();
            int rsize = ((RArray) rightTemplate).size();

            if (lsize < 30 && rsize < 30) {
                return EAGER_VECTOR;
            }
        }
        return LAZY_VECTOR; // default
    }

    public static VectorArithmetic chooseVectorArithmetic(ViewProfile profile) {

        if (profile.shouldBeLazy()) {
            return LAZY_VECTOR;
        } else {
            return EAGER_VECTOR;
        }
    }

    public abstract static class ComplexView extends View.RComplexView implements RComplex {
        final int n;
        final int[] dimensions;
        final Names names;
        final Attributes attributes;

        final ValueArithmetic arit;
        final ASTNode ast;

        // limiting view depth
        protected int depth;  // total views involved

        public ComplexView(int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            this.ast = ast;
            this.arit = arit;
            this.dimensions = dimensions;
            this.names = names;
            this.attributes = attributes;
            this.n = n;
            this.depth = depth;
        }

        @Override
        public int size() {
            return n;
        }

        @Override
        public int[] dimensions() {
            return dimensions;
        }

        @Override
        public Names names() {
            return names;
        }

        @Override
        public Attributes attributes() {
            return attributes;
        }

    }


    public abstract static class ComplexViewForComplexComplex extends ComplexView implements RComplex {
        final RComplex a;
        final RComplex b;


        public ComplexViewForComplexComplex(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            super(dimensions, names, attributes, n, depth, arit, ast);
            this.a = a;
            this.b = b;
        }


        @Override
        public boolean isSharedReal() {
            return a.isShared() || b.isShared();
        }

        @Override
        public void ref() {
            a.ref();
            b.ref();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return a.dependsOn(value) || b.dependsOn(value);
        }

        @Override
        public void visit_all(ValueVisitor v) {
            a.accept(v);
            b.accept(v);
        }

        static final class Generic extends ComplexViewForComplexComplex implements RComplex {
            final int na;
            final int nb;

            public Generic(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
            }

            @Override
            public double getReal(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opReal(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public double getImag(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opImag(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public Complex getComplex(int i) {
                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                Complex acmp = a.getComplex(ai);
                Complex bcmp = b.getComplex(bi);
                double areal = acmp.realValue();
                double aimag = acmp.imagValue();
                double breal = bcmp.realValue();
                double bimag = bcmp.imagValue();
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opComplex(ast, areal, aimag, breal, bimag);
                } else {
                    return RComplex.COMPLEX_BOXED_NA;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class GenericASized extends ComplexViewForComplexComplex implements RComplex {
            final int nb;

            public GenericASized(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == a.size());
                nb = b.size();
            }

            @Override
            public double getReal(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                int ai = i;
                int bi = i % nb;
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opReal(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public double getImag(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                int ai = i;
                int bi = i % nb;
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opImag(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public Complex getComplex(int i) {
                int ai = i;
                int bi = i % nb;
                Complex acmp = a.getComplex(ai);
                Complex bcmp = b.getComplex(bi);
                double areal = acmp.realValue();
                double aimag = acmp.imagValue();
                double breal = bcmp.realValue();
                double bimag = bcmp.imagValue();
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opComplex(ast, areal, aimag, breal, bimag);
                } else {
                    return RComplex.COMPLEX_BOXED_NA;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class GenericBSized extends ComplexViewForComplexComplex implements RComplex {
            final int na;

            public GenericBSized(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == b.size());
                na = a.size();
            }

            @Override
            public double getReal(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                int ai = i % na;
                int bi = i;
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opReal(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public double getImag(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                int ai = i % na;
                int bi = i;
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opImag(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public Complex getComplex(int i) {
                int ai = i % na;
                int bi = i;
                Complex acmp = a.getComplex(ai);
                Complex bcmp = b.getComplex(bi);
                double areal = acmp.realValue();
                double aimag = acmp.imagValue();
                double breal = bcmp.realValue();
                double bimag = bcmp.imagValue();
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opComplex(ast, areal, aimag, breal, bimag);
                } else {
                    return RComplex.COMPLEX_BOXED_NA;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSize extends ComplexViewForComplexComplex implements RComplex {

            public EqualSize(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
            }

            @Override
            public double getReal(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                double areal = a.getReal(i);
                double aimag = a.getImag(i);
                double breal = b.getReal(i);
                double bimag = b.getImag(i);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opReal(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public double getImag(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                double areal = a.getReal(i);
                double aimag = a.getImag(i);
                double breal = b.getReal(i);
                double bimag = b.getImag(i);
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opImag(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public Complex getComplex(int i) {
                Complex acmp = a.getComplex(i);
                Complex bcmp = b.getComplex(i);
                double areal = acmp.realValue();
                double aimag = acmp.imagValue();
                double breal = bcmp.realValue();
                double bimag = bcmp.imagValue();
                if (!RComplexUtils.arithEitherIsNA(areal, aimag) && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opComplex(ast, areal, aimag, breal, bimag);
                } else {
                    return RComplex.COMPLEX_BOXED_NA;
                }
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (a instanceof ComplexImpl) {
                    if (b instanceof ComplexImpl) {
                        arit.opComplexEqualSize(ast, a.getContent(), b.getContent(), resContent, n);
                        return;
                    }
                    if (b instanceof RComplexView) {
                        ((RComplexView) b).materializeInto(resContent);
                        arit.opComplexEqualSize(ast, a.getContent(), resContent, resContent, n);
                        return;
                    }
                } else if (a instanceof RComplexView) {
                    if (b instanceof ComplexImpl) {
                        ((RComplexView) a).materializeInto(resContent);
                        arit.opComplexEqualSize(ast, resContent, b.getContent(), resContent, n);
                        return;
                    }
                    if (SINGLE_CHILD_TIGHT_LOOP_MATERIALIZATION && b instanceof RComplexView) {
                        // use a tight loop for at least one child
                        ((RComplexView) a).materializeInto(resContent);
                        EqualSize myClone = new EqualSize(RComplex.RComplexFactory.getFor(resContent), b, dimensions, names, attributes, n, depth, arit, ast);
                        myClone.materializeIntoOnTheFly(resContent);
                        return;
                    }
                }
                super.materializeInto(resContent);
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (a instanceof ComplexImpl && b instanceof ComplexImpl) {
                    arit.opComplexEqualSize(ast, a.getContent(), b.getContent(), resContent, n);
                } else {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }

        }

        static final class VectorScalar extends ComplexViewForComplexComplex implements RComplex {

            final boolean arithIsNA;
            final double breal;
            final double bimag;

            public VectorScalar(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                breal = b.getReal(0);
                bimag = b.getImag(0);
                arithIsNA = RComplex.RComplexUtils.eitherIsNA(breal, bimag);
            }

            @Override
            public double getReal(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                double areal = a.getReal(i);
                double aimag = a.getImag(i);
                if (!arithIsNA && !RComplexUtils.arithEitherIsNA(areal, aimag)) {
                    return arit.opReal(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public double getImag(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                double areal = a.getReal(i);
                double aimag = a.getImag(i);
                if (!arithIsNA && !RComplexUtils.arithEitherIsNA(areal, aimag)) {
                    return arit.opImag(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public Complex getComplex(int i) {
                Complex acmp = a.getComplex(i);
                double areal = acmp.realValue();
                double aimag = acmp.imagValue();
                if (!arithIsNA && !RComplexUtils.arithEitherIsNA(areal, aimag)) {
                    return arit.opComplex(ast, areal, aimag, breal, bimag);
                } else {
                    return RComplex.COMPLEX_BOXED_NA;
                }
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (a instanceof ComplexImpl) {
                    arit.opComplexScalar(ast, a.getContent(), breal, bimag, resContent, n);
                } else if (a instanceof RComplexView) {
                    ((RComplexView) a).materializeInto(resContent);
                    arit.opComplexScalar(ast, resContent, breal, bimag, resContent, n);
                } else  {
                    super.materializeInto(resContent);
                }
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (a instanceof ComplexImpl) {
                    arit.opComplexScalar(ast, a.getContent(), breal, bimag, resContent, n);
                } else  {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class ScalarVector extends ComplexViewForComplexComplex implements RComplex {

            final boolean arithIsNA;
            final double areal;
            final double aimag;

            public ScalarVector(RComplex a, RComplex b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                areal = a.getReal(0);
                aimag = a.getImag(0);
                arithIsNA = RComplex.RComplexUtils.eitherIsNA(areal, aimag);
            }

            @Override
            public double getReal(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                double breal = b.getReal(i);
                double bimag = b.getImag(i);
                if (!arithIsNA && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opReal(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public double getImag(int i) { // FIXME: this is very slow (real and imag getters repeat the same computation)
                double breal = b.getReal(i);
                double bimag = b.getImag(i);
                if (!arithIsNA && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opImag(ast, areal, aimag, breal, bimag);
                } else {
                    return RDouble.NA;
                }
            }

            @Override
            public Complex getComplex(int i) {
                Complex bcmp = b.getComplex(i);
                double breal = bcmp.realValue();
                double bimag = bcmp.imagValue();
                if (!arithIsNA && !RComplexUtils.arithEitherIsNA(breal, bimag)) {
                    return arit.opComplex(ast, areal, aimag, breal, bimag);
                } else {
                    return RComplex.COMPLEX_BOXED_NA;
                }
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (b instanceof ComplexImpl) {
                    arit.opScalarComplex(ast, areal, aimag, b.getContent(), resContent, n);
                } else if (b instanceof RComplexView) {
                    ((RComplexView) b).materializeInto(resContent);
                    arit.opScalarComplex(ast, areal, aimag, resContent, resContent, n);
                } else  {
                    super.materializeInto(resContent);
                }
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (b instanceof ComplexImpl) {
                    arit.opScalarComplex(ast, areal, aimag, b.getContent(), resContent, n);
                } else  {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }

        }
    }

    private static int doubleViewDepth(RDouble a) {
        RDouble x = a;

        if (TracingView.VIEW_TRACING) {
            if (a instanceof RDoubleTracingView) {
                x = ((RDoubleTracingView) a).orig;
            }
        }
        if (x instanceof DoubleView) {
            return ((DoubleView) x).depth();
        } else {
            return 0;
        }
    }

    private static int intViewDepth(RInt a) {
        RInt x = a;

        if (TracingView.VIEW_TRACING) {
            if (a instanceof RIntTracingView) {
                x = ((RIntTracingView) a).orig;
            }
        }
        if (x instanceof IntView) {
            return ((IntView) x).depth();
        } else {
            return 0;
        }
    }

    private static int complexViewDepth(RComplex a) {
        RComplex x = a;

        if (TracingView.VIEW_TRACING) {
            if (a instanceof RComplexTracingView) {
                x = ((RComplexTracingView) a).orig;
            }
        }
        if (x instanceof ComplexView) {
            return ((ComplexView) x).depth;
        } else {
            return 0;
        }
    }





//    private static WeakReference doubleMaterializeBuffer;
//
//    private static double[] getDoubleBuffer(int size) { // FIXME: for single-threaded use only
//        if (doubleMaterializeBuffer != null) {
//            Object b = doubleMaterializeBuffer.get();
//            if (b != null) {
//                double[] ba = (double[]) b;
//                if (ba.length >= size) {
//                    return ba;
//                }
//            }
//        }
//        double[] ba = new double[size];
//        doubleMaterializeBuffer = new WeakReference<>(ba);
//        return ba;
//    }

    abstract static class DoubleView extends View.RDoubleView implements RDouble {
        final int n;
        final int[] dimensions;
        final Names names;
        final Attributes attributes;

        public final ValueArithmetic arit;
        final ASTNode ast;

        // limiting view depth
        protected int depth;  // total views involved

        public DoubleView(int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            this.dimensions = dimensions;
            this.names = names;
            this.attributes = attributes;
            this.n = n;
            this.depth = depth;
            this.arit = arit;
            this.ast = ast;
        }

        @Override
        public final int size() {
            return n;
        }

        @Override
        public final int[] dimensions() {
            return dimensions;
        }

        @Override
        public final Names names() {
            return names;
        }

        @Override
        public final Attributes attributes() {
            return attributes;
        }

        public final int depth() {
            return depth;
        }

        // TODO: implement more efficient versions of materializeIntoOnTheFly
        //   note that one can change to DoubleImpl, and then use .dependsOn to rule out a dependency, and hence fall back
        //   to tight loops
    }

    // NOTE: it is tempting to template this class by the type of a and type of b, re-using for
    // int and double combinations; unfortunately, that leads to slower execution
    public abstract static class DoubleViewForDoubleDouble extends DoubleView implements RDouble {
        public final RDouble a;
        public final RDouble b;

        public DoubleViewForDoubleDouble(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            super(dimensions, names, attributes, n, depth, arit, ast);
            this.a = a;
            this.b = b;
        }


        /** FUSION Calls the visitor on the current view (type-dispatch).
         */
        @Override
        public void visit(View.Visitor visitor) {
            visitor.visit(this);
        }

        @Override
        public final boolean isSharedReal() {
            return a.isShared() || b.isShared();
        }

        @Override
        public final void ref() {
            a.ref();
            b.ref();
        }

        @Override
        public final boolean dependsOn(RAny value) {
            return a.dependsOn(value) || b.dependsOn(value);
        }

        @Override
        public void visit_all(ValueVisitor v) {
            a.accept(v);
            b.accept(v);
        }

        @Override
        public RDouble materializeOnAssignmentRef(Object oldValue) {
            DoubleImpl res;
            if (a == oldValue && a instanceof DoubleImpl && !a.isShared() && a.size() == n) {
                res = (DoubleImpl) a;
            } else if (b == oldValue && b instanceof DoubleImpl && !b.isShared() && b.size() == n) {
                res = (DoubleImpl) b;
            } else {
                return super.materializeOnAssignmentRef(oldValue);
            }
            materializeIntoOnTheFly(res.getContent()); // no ref
            return (RDouble) res.setNames(names).setDimensions(dimensions).setAttributes(attributes);
        }

        static final class Generic extends DoubleViewForDoubleDouble implements RDouble {
            final int na;
            final int nb;

            public Generic(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
            }

            @Override
            public double getDouble(int i) {

                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                double adbl = a.getDouble(ai);
                double bdbl = b.getDouble(bi);
                if (RDouble.RDoubleUtils.arithIsNA(adbl) || RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bdbl);
                }
             }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class GenericASized extends DoubleViewForDoubleDouble implements RDouble {
            final int nb;

            public GenericASized(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(a.size() == n);
                nb = b.size();
            }

            /** FUSION Calls the visitor on the current view (type-dispatch).
             */
            @Override
            public void visit(View.Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {

                int bi = i % nb;
                double adbl = a.getDouble(i);
                double bdbl = b.getDouble(bi);
                if (RDouble.RDoubleUtils.arithIsNA(adbl) || RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class GenericBSized extends DoubleViewForDoubleDouble implements RDouble {
            final int na;

            public GenericBSized(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                assert Utils.check(b.size() == n);
            }

            /** FUSION Calls the visitor on the current view (type-dispatch).
             */
            @Override
            public void visit(View.Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {

                int ai = i % na;
                double adbl = a.getDouble(ai);
                double bdbl = b.getDouble(i);
                if (RDouble.RDoubleUtils.arithIsNA(adbl) || RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class EqualSizeVectorVector extends DoubleViewForDoubleDouble implements RDouble {

            public EqualSizeVectorVector(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
            }

            /** FUSION Calls the visitor on the current view (type-dispatch).
             */
            @Override
            public void visit(View.Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {

                double adbl = a.getDouble(i);
                double bdbl = b.getDouble(i);
                if (RDouble.RDoubleUtils.arithIsNA(adbl) || RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bdbl);
                }
             }

            @Override
            public void materializeInto(double[] resContent) {
                if (a instanceof DoubleImpl) {
                    if (b instanceof DoubleImpl) {
                        arit.opDoubleEqualSize(ast, a.getContent(), b.getContent(), resContent, n);
                        return;
                    }
                    if (b instanceof RDoubleView) {
                        ((RDoubleView) b).materializeInto(resContent);
                        arit.opDoubleEqualSize(ast, a.getContent(), resContent, resContent, n);
                        return;
                    }
                } else if (a instanceof RDoubleView) {
                    if (b instanceof DoubleImpl) {
                        ((RDoubleView) a).materializeInto(resContent);
                        arit.opDoubleEqualSize(ast, resContent, b.getContent(), resContent, n);
                        return;
                    }
                    if (SINGLE_CHILD_TIGHT_LOOP_MATERIALIZATION && b instanceof RDoubleView) {
                        // use a tight loop for at least one child
                        ((RDoubleView) a).materializeInto(resContent);
                        EqualSizeVectorVector myClone = new EqualSizeVectorVector(RDouble.RDoubleFactory.getFor(resContent), b, dimensions, names, attributes, n, depth, arit, ast);
                        myClone.materializeIntoOnTheFly(resContent);
                        return;
                    }
                }
                super.materializeInto(resContent);
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (a instanceof DoubleImpl && b instanceof DoubleImpl) {
                    arit.opDoubleEqualSize(ast, a.getContent(), b.getContent(), resContent, n);
                } else {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class VectorScalar extends DoubleViewForDoubleDouble implements RDouble {

            final boolean arithIsNA;
            final double bdbl;

            public VectorScalar(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                bdbl = b.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(bdbl);
            }

            /** FUSION Calls the visitor on the current view (type-dispatch).
             */
            @Override
            public void visit(View.Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {
                double adbl = a.getDouble(i);
                if (arithIsNA || RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bdbl);
                }
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (a instanceof DoubleImpl) {
                    arit.opDoubleScalar(ast, a.getContent(), bdbl, resContent, n);
                } else if (a instanceof RDoubleView) {
                    ((RDoubleView) a).materializeInto(resContent);
                    arit.opDoubleScalar(ast, resContent, bdbl, resContent, n);
                } else  {
                    super.materializeInto(resContent);
                }
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (a instanceof DoubleImpl) {
                    arit.opDoubleScalar(ast, a.getContent(), bdbl, resContent, n);
                } else  {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        // FIXME: this should be specialized much more in the call stack (building names, dimensions, attributes, calling ref, depends on, ...)
        public static final class ScalarVector extends DoubleViewForDoubleDouble implements RDouble {

            final boolean arithIsNA;
            final double adbl;

            public ScalarVector(RDouble a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                adbl = a.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(adbl);
            }

            /** FUSION Calls the visitor on the current view (type-dispatch).
             */
            @Override
            public void visit(View.Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {
                double bdbl = b.getDouble(i);
                if (arithIsNA || RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bdbl);
                }
             }

            @Override
            public double sum(boolean narm) {
                if (arit == ADD) {
                    double bsum = b.sum(narm);
                    if (narm && arithIsNA) {
                        return bsum;
                    } else {
                        return adbl + bsum;
                    }
                }
                if (false) { // hack to test if synthesis could help for b25-prog2 (perfres)
                    if (a instanceof ScalarDoubleImpl && b instanceof RInt.RDoubleView) {
                        RInt bint = ((RInt.RDoubleView) b).asInt(); // hack to get the original view
                        if (bint instanceof IntViewForIntInt.VectorSequenceASized) {
                            // 1 / (t(b) + 0:(a-1))
                            IntViewForIntInt.VectorSequenceASized bview = (IntViewForIntInt.VectorSequenceASized) bint;
                            double avalue = ((ScalarDoubleImpl) a).getDouble();
                            int[] ba = bview.a.getContent();
                            RIntSequence bbs = (RIntSequence) bview.b;
                            int bbfrom = bbs.from();
                            int bbto = bbs.to();
                            int bbstep = bbs.step();
                            ValueArithmetic barith = bview.arit;

                            double res = 0;
                            int bbb = bbfrom;
                            boolean overflown = false;

                            for(int i = 0; i < n; i++) {
                                int bba = ba[i];
                                if (bba == RInt.NA) {
                                    if (!narm) {
                                        res = RDouble.NA;
                                    }
                                } else {
                                    int r = barith.op(ast, bba, bbb);
                                    if (r == RInt.NA) {
                                        overflown = true;
                                        if (!narm) {
                                            res = RDouble.NA;
                                        }
                                    } else {
                                        res += arit.op(ast, avalue, (double) r);
                                    }
                                }
                                bbb += bbstep;
                                if (bbb > bbto) {
                                    bbb = bbfrom;
                                }
                            }
                            if (overflown) {
                                barith.emitOverflowWarning(ast);
                            }
                            return res;
                        }
                    }
                }
                return super.sum(narm);
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (b instanceof DoubleImpl) {
                    arit.opScalarDouble(ast, adbl, b.getContent(), resContent, n);
                } else if (b instanceof RDoubleView) {
                    ((RDoubleView) b).materializeInto(resContent);
                    arit.opScalarDouble(ast, adbl, resContent, resContent, n);
                } else  {
                    super.materializeInto(resContent);
                }
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (b instanceof DoubleImpl) {
                    arit.opScalarDouble(ast, adbl, b.getContent(), resContent, n);
                } else  {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

//            @Override
//            public RDouble materialize() {
//
//                if (false) { // hack to test if synthesis could help for b25-prog2 (perfres)
//                    if (a instanceof ScalarDoubleImpl && b instanceof RInt.RDoubleView) {
//                        RInt bint = ((RInt.RDoubleView) b).asInt(); // hack to get the original view
//                        if (bint instanceof IntView.VectorSequenceASized) {
//                            // 1 / (t(b) + 0:(a-1))
//                            IntView.VectorSequenceASized bview = (IntView.VectorSequenceASized) bint;
//                            double avalue = ((ScalarDoubleImpl) a).getDouble();
//                            int[] ba = bview.a.getContent();
//                            RIntSequence bbs = (RIntSequence) bview.b;
//                            int bbfrom = bbs.from();
//                            int bbto = bbs.to();
//                            int bbstep = bbs.step();
//                            ValueArithmetic barith = bview.arit;
//
//                            double[] content = new double[n];
//                            int bbb = bbfrom;
//                            boolean overflown = false;
//
//                            for(int i = 0; i < n; i++) {
//                                int bba = ba[i];
//                                if (bba == RInt.NA) {
//                                    content[i] = RDouble.NA;
//                                } else {
//                                    int r = barith.op(ast, bba, bbb);
//                                    double rd;
//                                    if (r == RInt.NA) {
//                                        overflown = true;
//                                        rd = RDouble.NA;
//                                    } else {
//                                        rd = r;
//                                    }
//                                    content[i] = arit.op(ast, avalue, rd);
//                                }
//                                bbb += bbstep;
//                                if (bbb > bbto) {
//                                    bbb = bbfrom;
//                                }
//                            }
//                            if (overflown) {
//                                barith.emitOverflowWarning(ast);
//                            }
//                            return RDouble.RDoubleFactory.getFor(content, dimensions, names, attributes);
//                        }
//                    }
//                }
//
//                if (b instanceof DoubleImpl) {
//                    return arit.opScalarDoubleImpl(ast, a.getDouble(0), (DoubleImpl) b, n, dimensions, names, attributes);
//                } else {
//                    return super.materialize();
//                }
//            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

    }


    // note: the base class is a copy-paste of ArithmeticDoubleView, but templates make it slower
    public abstract static class DoubleViewForDoubleInt extends DoubleView implements RDouble {
        public final RDouble a;
        public final RInt b;

        public DoubleViewForDoubleInt(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            super(dimensions, names, attributes, n, depth, arit, ast);
            this.a = a;
            this.b = b;
        }


        /** FUSION Visits the type-dispatched view.
         */
        @Override public void visit(Visitor visitor) {
            visitor.visit(this);
        }


        @Override
        public final boolean isSharedReal() {
            return a.isShared() || b.isShared();
        }

        @Override
        public final void ref() {
            a.ref();
            b.ref();
        }

        @Override
        public final boolean dependsOn(RAny value) {
            return a.dependsOn(value) || b.dependsOn(value);
        }

        @Override
        public void visit_all(ValueVisitor v) {
            a.accept(v);
            b.accept(v);
        }

/*        static final class VectorSequence extends DoubleViewForDoubleInt implements RDouble {
            final int na;
            final int nb;
            final int bfrom;
            final int bstep;

            public VectorSequence(RDouble a, RIntSequence b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
                bfrom = b.from();
                bstep = b.step();
            }

            @Override
            public double getDouble(int i) {

                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                double adbl = a.getDouble(ai);
                int bint = bfrom + bi * bstep;
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bint);
                }
            }
            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        } */

        static final class VectorSequenceASized extends DoubleViewForDoubleInt implements RDouble {
            final int nb;
            final int bfrom;
            final int bstep;

            public VectorSequenceASized(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(a.size() == n);
                nb = b.size();
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public double getDouble(int i) {

                int bi = i % nb;
                double adbl = a.getDouble(i);
                int bint = bfrom + bi * bstep;
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bint);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class VectorSimpleRangeASized extends DoubleViewForDoubleInt implements RDouble {
            final int nb;

            public VectorSimpleRangeASized(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
                assert Utils.check(a.size() == n);
                nb = b.size();
            }

            @Override
            public double getDouble(int i) {

                int bi = i % nb;
                double adbl = a.getDouble(i);
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bi + 1);
                }
            }

            @Override
            public double sum(boolean narm) {
                if (a instanceof DoubleImpl) {
                    double[] acontent = ((DoubleImpl) a).getContent();
                    double res = 0;
                    int j = 1;
                    for(int i = 0; i < n; i++) {
                        double adbl = acontent[i];
                        if (narm && RDouble.RDoubleUtils.arithIsNA(adbl)) {
                            continue;
                        }
                        res += arit.op(ast,  adbl, (double) j); // FIXME: possibly virtual call
                        j++;
                        if (j > nb) {
                            j = 1;
                        }
                    }
                    return res;
                } else {
                    return super.sum(narm);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }

        }

        static final class VectorSequenceBSized extends DoubleViewForDoubleInt implements RDouble {
            final int na;
            final int bfrom;
            final int bstep;

            public VectorSequenceBSized(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == b.size());
                na = a.size();
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public double getDouble(int i) {

                int ai = i % na;
                double adbl = a.getDouble(ai);
                int bint = bfrom + i * bstep;
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bint);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class VectorSimpleRangeBSized extends DoubleViewForDoubleInt implements RDouble {
            final int na;

            public VectorSimpleRangeBSized(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
                assert Utils.check(n == b.size());
                na = a.size();
            }

            @Override
            public double getDouble(int i) {

                int ai = i % na;
                double adbl = a.getDouble(ai);
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, i + 1);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class EqualSizeVectorVector extends DoubleViewForDoubleInt implements RDouble {

            public EqualSizeVectorVector(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
            }

            /** FUSION Visits the type-dispatched view.
             */
            @Override public void visit(Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {

                double adbl = a.getDouble(i);
                int bint = b.getInt(i);
                if (bint == RInt.NA || RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                }
                return arit.op(ast, adbl, bint);
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (a instanceof DoubleImpl) {
                    if (b instanceof IntImpl) {
                        arit.opDoubleIntEqualSize(ast, a.getContent(), b.getContent(), resContent, n);
                        return;
                    }
                    // FIXME: perhaps an int view should be able to materialize itself into a double?
                }
                if (a instanceof RDoubleView) {
                    if (b instanceof IntImpl) {
                        ((RDoubleView) a).materializeInto(resContent);
                        arit.opDoubleIntEqualSize(ast, resContent, b.getContent(), resContent, n);
                        return;
                    }
                    if (SINGLE_CHILD_TIGHT_LOOP_MATERIALIZATION) {
                        // use a tight loop for at least one child
                        ((RDoubleView) a).materializeInto(resContent);
                        EqualSizeVectorVector myClone = new EqualSizeVectorVector(RDouble.RDoubleFactory.getFor(resContent), b, dimensions, names, attributes, n, depth, arit, ast);
                        myClone.materializeIntoOnTheFly(resContent);
                        return;
                    }
                }
                // TODO: handle int view
                // FIXME: should create an extra buffer?
                super.materializeInto(resContent);
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (a instanceof DoubleImpl && b instanceof IntImpl) {
                    arit.opDoubleIntEqualSize(ast, a.getContent(), b.getContent(), resContent, n);
                } else {
                    super.materializeIntoOnTheFly(resContent);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }

        }

        static final class EqualSizeVectorSequence extends DoubleViewForDoubleInt implements RDouble {

            final int bfrom;
            final int bstep;

            public EqualSizeVectorSequence(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public double getDouble(int i) {

                double adbl = a.getDouble(i);
                int bint = bfrom + i * bstep;
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                }
                return arit.op(ast, adbl, bint);
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeVectorSimpleRange extends DoubleViewForDoubleInt implements RDouble {

            public EqualSizeVectorSimpleRange(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
            }

            @Override
            public double getDouble(int i) {

                double adbl = a.getDouble(i);
                if (RDouble.RDoubleUtils.arithIsNA(adbl)) {
                    return RDouble.NA;
                }
                return arit.op(ast, adbl, i + 1);
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class ScalarSequence extends DoubleViewForDoubleInt implements RDouble {

            final boolean arithIsNA;
            final double adbl;
            final int bfrom;
            final int bstep;

            public ScalarSequence(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                adbl = a.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(adbl);
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public double getDouble(int i) {
                int bint = bfrom + i * bstep;
                if (arithIsNA) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, bint);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class ScalarSimpleRange extends DoubleViewForDoubleInt implements RDouble {

            final boolean arithIsNA;
            final double adbl;

            public ScalarSimpleRange(RDouble a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
                adbl = a.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(adbl);
            }

            @Override
            public double getDouble(int i) {
                if (arithIsNA) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, adbl, i + 1);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }
    }

 // note: the base class is a copy-paste of Arithmetic.DoubleView, but templates make it slower
    public abstract static class DoubleViewForIntDouble extends DoubleView implements RDouble {
        public final RInt a;
        public final RDouble b;

        public DoubleViewForIntDouble(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            super(dimensions, names, attributes, n, depth, arit, ast);
            this.a = a;
            this.b = b;
        }


        /** FUSION Visits the type-dispatched view.
         */
        @Override public void visit(Visitor visitor) {
            visitor.visit(this);
        }

        @Override
        public final boolean isSharedReal() {
            return a.isShared() || b.isShared();
        }

        @Override
        public final void ref() {
            a.ref();
            b.ref();
        }

        @Override
        public final boolean dependsOn(RAny value) {
            return a.dependsOn(value) || b.dependsOn(value);
        }

        @Override
        public void visit_all(ValueVisitor v) {
            a.accept(v);
            b.accept(v);
        }

/*        static final class SequenceVector extends DoubleViewForIntDouble implements RDouble {
            final int na;
            final int nb;
            final int afrom;
            final int astep;

            public SequenceVector(RIntSequence a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
                afrom = a.from();
                astep = a.step();
            }

            @Override
            public double getDouble(int i) {

                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                int aint = afrom + ai * astep;
                double bdbl = b.getDouble(bi);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, aint, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        } */

        static final class SequenceVectorASized extends DoubleViewForIntDouble implements RDouble {
            final int nb;
            final int afrom;
            final int astep;

            public SequenceVectorASized(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == a.size());
                nb = b.size();
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public double getDouble(int i) {

                int bi = i % nb;
                int aint = afrom + i * astep;
                double bdbl = b.getDouble(bi);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, aint, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SimpleRangeVectorASized extends DoubleViewForIntDouble implements RDouble {
            final int nb;

            public SimpleRangeVectorASized(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
                assert Utils.check(n == a.size());
                nb = b.size();
            }

            @Override
            public double getDouble(int i) {

                int bi = i % nb;
                double bdbl = b.getDouble(bi);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, i + 1, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SequenceVectorBSized extends DoubleViewForIntDouble implements RDouble {
            final int na;
            final int afrom;
            final int astep;

            public SequenceVectorBSized(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == b.size());
                na = a.size();
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public double getDouble(int i) {

                int ai = i % na;
                int aint = afrom + ai * astep;
                double bdbl = b.getDouble(i);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, aint, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SimpleRangeVectorBSized extends DoubleViewForIntDouble implements RDouble {
            final int na;

            public SimpleRangeVectorBSized(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
                assert Utils.check(n == b.size());
                na = a.size();
            }

            @Override
            public double getDouble(int i) {

                int ai = i % na;
                double bdbl = b.getDouble(i);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, ai + 1, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class EqualSizeVectorVector extends DoubleViewForIntDouble implements RDouble {

            public EqualSizeVectorVector(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
            }

            /** FUSION Visits the type-dispatched view.
             */
            @Override public void visit(Visitor visitor) {
                visitor.visit(this);
            }

            @Override
            public double getDouble(int i) {

                int aint = a.getInt(i);
                double bdbl = b.getDouble(i);

                if (aint == RInt.NA || RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                }
                return arit.op(ast, aint, bdbl);
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeSequenceVector extends DoubleViewForIntDouble implements RDouble {
            final int afrom;
            final int astep;

            public EqualSizeSequenceVector(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public double getDouble(int i) {

                int aint = afrom + i * astep;
                double bdbl = b.getDouble(i);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                }
                return arit.op(ast, aint, bdbl);
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeSimpleRangeVector extends DoubleViewForIntDouble implements RDouble {

            public EqualSizeSimpleRangeVector(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
            }

            @Override
            public double getDouble(int i) {

                double bdbl = b.getDouble(i);

                if (RDouble.RDoubleUtils.arithIsNA(bdbl)) {
                    return RDouble.NA;
                }
                return arit.op(ast, i + 1, bdbl);
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class VectorScalar extends DoubleViewForIntDouble implements RDouble {

            final boolean arithIsNA;
            final double bdbl;

            public VectorScalar(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                bdbl = b.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(bdbl);
            }

            /** FUSION Visits the type-dispatched view.
             */
            @Override public void visit(Visitor visitor) {
                visitor.visit(this);
            }


            @Override
            public double getDouble(int i) {
                double aint = a.getInt(i);
                if (arithIsNA || aint == RInt.NA) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, aint, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SequenceScalar extends DoubleViewForIntDouble implements RDouble {

            final boolean arithIsNA;
            final double bdbl;
            final int afrom;
            final int astep;

            public SequenceScalar(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                bdbl = b.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(bdbl);
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public double getDouble(int i) {
                double aint = afrom + i*astep;
                if (arithIsNA) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, aint, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SimpleRangeScalar extends DoubleViewForIntDouble implements RDouble {

            final boolean arithIsNA;
            final double bdbl;

            public SimpleRangeScalar(RInt a, RDouble b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
                bdbl = b.getDouble(0);
                arithIsNA = RDouble.RDoubleUtils.arithIsNA(bdbl);
            }

            @Override
            public double getDouble(int i) {
                if (arithIsNA) {
                    return RDouble.NA;
                } else {
                    return arit.op(ast, i + 1, bdbl);
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }
    }

//    // experimental optimization - may have to be re-visited later
//    private static RInt hackAddTemporaryIntandSequence(RInt aArg, RIntSequence b, int na, int nb, ValueArithmetic arit, ASTNode ast) {
//        int[] a = aArg.getContent();
//        int bfrom = b.from();
//        int bstep = b.step();
//
//        boolean overflown = false;
//
//        int nfull = na / nb;
//        int aoffset = 0;
//        for (int fi = 0; fi < nfull; fi++) {
//            int bval = bfrom;
//            for (int bi = 0; bi < nb; bi++) {
//                int ai = aoffset + bi;
//                int aval = a[ai];
//                int res;
//                if (aval > 0) {
//                    res = aval + bval;
//                    // NOTE: aval cannot be RInt.NA
//                    if (bval >= res) {
//                        res = RInt.NA;
//                        overflown = true;
//                    }
//                } else {
//                    if (aval == RInt.NA) {
//                        res = RInt.NA;
//                    } else {
//                        res = aval + bval;
//                        if (bval < res) {
//                            res = RInt.NA;
//                            overflown = true;
//                        } else {
//                            if (res == RInt.NA) {
//                             // r may be NA (may "naturally" reach NA which is however still reported as overflow by R)
//                                overflown = true;
//                            }
//                        }
//                    }
//                }
//                a[ai] = res;
//                bval += bstep;
//            }
//            aoffset += nb;
//        }
//        int bval = bfrom;
//        for (int ai = aoffset; ai < na; ai++) {
//            int aval = a[ai];
//            int res;
//            if (aval > 0) {
//                res = aval + bval;
//                // NOTE: aval cannot be RInt.NA
//                if (bval >= res) {
//                    res = RInt.NA;
//                    overflown = true;
//                }
//            } else {
//                if (aval == RInt.NA) {
//                    res = RInt.NA;
//                } else {
//                    res = aval + bval;
//                    if (bval < res) {
//                        res = RInt.NA;
//                        overflown = true;
//                    } else {
//                        if (res == RInt.NA) {
//                         // r may be NA (may "naturally" reach NA which is however still reported as overflow by R)
//                            overflown = true;
//                        }
//                    }
//                }
//            }
//            bval += bstep;
//        }
//        if (overflown) {
//            arit.emitOverflowWarning(ast);
//        }
//        return aArg;
//    }


    abstract static class IntView extends View.RIntView implements RInt {
        final int n;
        final int[] dimensions;
        final Names names;
        final Attributes attributes;

        public final ValueArithmetic arit;
        final ASTNode ast;

        boolean overflown;

        // limiting view depth
        protected int depth;  // total views involved

        public IntView(int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            this.dimensions = dimensions;
            this.names = names;
            this.attributes = attributes;
            this.n = n;
            this.depth = depth;
            this.arit = arit;
            this.ast = ast;
        }

        @Override
        public final int size() {
            return n;
        }

        @Override
        public final int[] dimensions() {
            return dimensions;
        }

        @Override
        public final Names names() {
            return names;
        }

        @Override
        public final Attributes attributes() {
            return attributes;
        }

        public final int depth() {
            return depth;
        }

        // TODO: implement more efficient versions of materializeIntoOnTheFly
        //   note that one can change to DoubleImpl, and then use .dependsOn to rule out a dependency, and hence fall back
        //   to tight loops
    }

    public abstract static class IntViewForIntInt extends IntView implements RInt {
        public final RInt a;
        public final RInt b;

        public IntViewForIntInt(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
            super(dimensions, names, attributes, n, depth, arit, ast);
            this.a = a;
            this.b = b;
        }

        /** FUSION Visits the type-dispatched view.
         */
        @Override public void visit(Visitor visitor) {
            visitor.visit(this);
        }


        @Override
        public boolean isSharedReal() {
            return a.isShared() || b.isShared();
        }

        @Override
        public void ref() {
            a.ref();
            b.ref();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return a.dependsOn(value) || b.dependsOn(value);
        }

        @Override
        public void visit_all(ValueVisitor v) {
            a.accept(v);
            b.accept(v);
        }

        static final class Generic extends IntViewForIntInt implements RInt {
            final int na;
            final int nb;

            public Generic(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
            }

            @Override
            public int getInt(int i) {
                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                int aint = a.getInt(ai);
                int bint = b.getInt(bi);
                if (aint == RInt.NA || bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class GenericASized extends IntViewForIntInt implements RInt {
            final int nb;

            public GenericASized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == a.size());
                nb = b.size();
            }

            @Override
            public int getInt(int i) {
                int bi = i % nb;
                int aint = a.getInt(i);
                int bint = b.getInt(bi);
                if (aint == RInt.NA || bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class GenericBSized extends IntViewForIntInt implements RInt {
            final int na;

            public GenericBSized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                assert Utils.check(b.size() == n);
            }

            @Override
            public int getInt(int i) {
                int ai = i % na;
                int aint = a.getInt(ai);
                int bint = b.getInt(i);
                if (aint == RInt.NA || bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

/*        static final class VectorSequence extends IntViewForIntInt implements RInt {
            final int na;
            final int nb;
            final int bfrom;
            final int bstep;

            public VectorSequence(RInt a, RIntSequence b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
                bfrom = b.from();
                bstep = b.step();
            }

            @Override
            public int getInt(int i) {
                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                int aint = a.getInt(ai);
                int bint = bfrom + bi * bstep;
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        } */

        static final class VectorSequenceASized extends IntViewForIntInt implements RInt {
            final int nb;
            final int bfrom;
            final int bstep;
            final int bto;

            public VectorSequenceASized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(a.size() == n);
                nb = b.size();
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
                bto = bs.to();
            }

            @Override
            public int getInt(int i) {
                int bi = i % nb;
                int aint = a.getInt(i);
                int bint = bfrom + bi * bstep;
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class VectorSimpleRangeASized extends IntViewForIntInt implements RInt {
            final int nb;

            public VectorSimpleRangeASized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
                assert Utils.check(a.size() == n);
                nb = b.size();
            }

            @Override
            public int getInt(int i) {
                int bi = i % nb;
                int aint = a.getInt(i);
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bi + 1);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class VectorSequenceBSized extends IntViewForIntInt implements RInt {
            final int na;
            final int bfrom;
            final int bstep;

            public VectorSequenceBSized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(b.size() == n);
                na = a.size();
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public int getInt(int i) {
                int ai = i % na;
                int aint = a.getInt(ai);
                int bint = bfrom + i * bstep;
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class VectorSimpleRangeBSized extends IntViewForIntInt implements RInt {
            final int na;

            public VectorSimpleRangeBSized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
                assert Utils.check(b.size() == n);
                na = a.size();
            }

            @Override
            public int getInt(int i) {
                int ai = i % na;
                int aint = a.getInt(ai);
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, i + 1);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SequenceVector extends IntViewForIntInt implements RInt {
            final int na;
            final int nb;
            final int afrom;
            final int astep;

            public SequenceVector(RIntSequence a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                na = a.size();
                nb = b.size();
                afrom = a.from();
                astep = a.step();
            }

            @Override
            public int getInt(int i) {
                int ai;
                int bi;
                if (i >= na) {
                    ai = i % na;
                    bi = i;
                } else if (i >= nb) {
                    bi = i % nb;
                    ai = i;
                } else {
                    ai = i;
                    bi = i;
                }
                int aint = afrom + ai * astep;
                int bint = b.getInt(bi);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SequenceVectorASized extends IntViewForIntInt implements RInt {
            final int nb;
            final int afrom;
            final int astep;

            public SequenceVectorASized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == a.size());
                nb = b.size();
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public int getInt(int i) {
                int bi = i % nb;
                int aint = afrom + i * astep;
                int bint = b.getInt(bi);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SimpleRangeVectorASized extends IntViewForIntInt implements RInt {
            final int nb;

            public SimpleRangeVectorASized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
                assert Utils.check(n == a.size());
                nb = b.size();
            }

            @Override
            public int getInt(int i) {
                int bi = i % nb;
                int bint = b.getInt(bi);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, i + 1, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SequenceVectorBSized extends IntViewForIntInt implements RInt {
            final int na;
            final int afrom;
            final int astep;

            public SequenceVectorBSized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(n == b.size());
                na = a.size();
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public int getInt(int i) {
                int ai = i % na;
                int aint = afrom + ai * astep;
                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SimpleRangeVectorBSized extends IntViewForIntInt implements RInt {
            final int na;

            public SimpleRangeVectorBSized(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
                assert Utils.check(n == b.size());
                na = a.size();
            }

            @Override
            public int getInt(int i) {
                int ai = i % na;
                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, ai + 1, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        public static final class EqualSize extends IntViewForIntInt implements RInt {

            public EqualSize(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
            }

            /** FUSION Visits the type-dispatched view.
             */
            @Override public void visit(Visitor visitor) {
                visitor.visit(this);
            }


            @Override
            public int getInt(int i) {

                int aint = a.getInt(i);
                int bint = b.getInt(i);
                if (aint == RInt.NA || bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeIntSequence extends IntViewForIntInt implements RInt {

            final int bfrom;
            final int bstep;

            public EqualSizeIntSequence(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public int getInt(int i) {

                int aint = a.getInt(i);
                int bint = bfrom + i * bstep;
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeIntSimpleRange extends IntViewForIntInt implements RInt {


            public EqualSizeIntSimpleRange(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
            }

            @Override
            public int getInt(int i) {

                int aint = a.getInt(i);
                if (aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, i + 1);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeSequenceInt extends IntViewForIntInt implements RInt {

            final int afrom;
            final int astep;

            public EqualSizeSequenceInt(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public int getInt(int i) {

                int aint = afrom + i * astep;
                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class EqualSizeSimpleRangeInt extends IntViewForIntInt implements RInt {

            public EqualSizeSimpleRangeInt(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
            }

            @Override
            public int getInt(int i) {

                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, i + 1, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class VectorScalar extends IntViewForIntInt implements RInt {

            final boolean arithIsNA;
            final int bint;

            public VectorScalar(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                bint = b.getInt(0);
                arithIsNA = bint == RInt.NA;
            }

            @Override
            public int getInt(int i) {

                int aint = a.getInt(i);
                if (arithIsNA || aint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SequenceScalar extends IntViewForIntInt implements RInt {

            final boolean arithIsNA;
            final int bint;
            final int afrom;
            final int astep;

            public SequenceScalar(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                bint = b.getInt(0);
                arithIsNA = bint == RInt.NA;
                RIntSequence as = RIntSequence.cast(a);
                afrom = as.from();
                astep = as.step();
            }

            @Override
            public int getInt(int i) {

                int aint = afrom + i * astep;
                if (arithIsNA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class SimpleRangeScalar extends IntViewForIntInt implements RInt {

            final boolean arithIsNA;
            final int bint;

            public SimpleRangeScalar(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(a));
                bint = b.getInt(0);
                arithIsNA = bint == RInt.NA;
            }

            @Override
            public int getInt(int i) {

                if (arithIsNA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, i + 1, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class ScalarVector extends IntViewForIntInt implements RInt {

            final boolean arithIsNA;
            final int aint;

            public ScalarVector(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                aint = a.getInt(0);
                arithIsNA = aint == RInt.NA;
            }

            @Override
            public int getInt(int i) {

                int bint = b.getInt(i);
                if (arithIsNA || bint == RInt.NA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class ScalarSequence extends IntViewForIntInt implements RInt {

            final boolean arithIsNA;
            final int aint;
            final int bfrom;
            final int bstep;

            public ScalarSequence(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                aint = a.getInt(0);
                arithIsNA = aint == RInt.NA;
                RIntSequence bs = RIntSequence.cast(b);
                bfrom = bs.from();
                bstep = bs.step();
            }

            @Override
            public int getInt(int i) {

                int bint = bfrom + i * bstep;
                if (arithIsNA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, bint);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

        static final class ScalarSimpleRange extends IntViewForIntInt implements RInt {

            final boolean arithIsNA;
            final int aint;

            public ScalarSimpleRange(RInt a, RInt b, int[] dimensions, Names names, Attributes attributes, int n, int depth, ValueArithmetic arit, ASTNode ast) {
                super(a, b, dimensions, names, attributes, n, depth, arit, ast);
                assert Utils.check(RIntSimpleRange.isInstance(b));
                aint = a.getInt(0);
                arithIsNA = aint == RInt.NA;
            }

            @Override
            public int getInt(int i) {

                if (arithIsNA) {
                    return RInt.NA;
                } else {
                    int res = arit.op(ast, aint, i + 1);
                    if (res == RInt.NA && !overflown) {
                        overflown = true;
                        arit.emitOverflowWarning(ast);
                    }
                    return res;
                }
            }

            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        }

    }

    public static int[] resultDimensions(ASTNode ast, RArray a, RArray b) {
        int[] dima = a.dimensions();
        int[] dimb = b.dimensions();
        if (dima == null) {
            if (dimb != null) {
                int asize = a.size();
                int bsize = b.size();
                if (asize > bsize) {
                    throw RError.getDimsDontMatchLength(ast, bsize, asize);
                }
            }
            return dimb;
        }
        if (dimb == null) {
            if (dima != null) {
                int asize = a.size();
                int bsize = b.size();
                if (bsize > asize) {
                    throw RError.getDimsDontMatchLength(ast, asize, bsize);
                }
            }
            return dima;
        }

        int alen = dima.length;
        int blen = dimb.length;
        if (alen == 2 && blen == 2 && dima[0] == dimb[0] && dima[1] == dimb[1]) {
            return dima;
        }

        if (alen == blen) {
            for (int i = 0; i < alen; i++) {
                if (dima[i] != dimb[i]) {
                    throw RError.getNonConformableArrays(ast);
                }
            }
            return dima;
        }
        throw RError.getNonConformableArrays(ast);
    }

    public static Names resultNames(@SuppressWarnings("unused") ASTNode ast, RArray a, RArray b) {
        Names na = a.names();
        Names nb = b.names();
        if (nb == null) {
            return na;
        }
        if (na == null) {
            return nb;
        }
        int asize = a.size();
        int bsize = b.size();

        if (bsize > asize) {
            return nb;
        } else {
            return na;
        }
    }

    // note: increments reference count on attributes
    public static Attributes resultAttributes(@SuppressWarnings("unused") ASTNode ast, RArray a, RArray b) {
        Attributes aa = a.attributes();
        Attributes ba = b.attributes();

        if (ba == null && aa == null) {
            return null;
        }
        int asize = a.size();
        int bsize = b.size();

        if (asize > bsize) {
            return Attributes.markShared(aa);
        }
        if (bsize > asize) {
            return Attributes.markShared(ba);
        }
        // asize == bsize
        if (ba == null) {
            return Attributes.markShared(aa);
        }
        if (aa == null) {
            return Attributes.markShared(ba);
        }
        // both aa != null and ba != null

        Attributes res = ba.copy();
        Map<RSymbol, RAny> amap = aa.map();
        for (Map.Entry<RSymbol, RAny> ae : amap.entrySet()) {
            RAny value = ae.getValue();
            value.ref();
            res.put(ae.getKey(), value);
        }
        return res;
    }

    public static int resultSize(ASTNode ast, int na, int nb) {
        int n;
        if (na == 0 || nb == 0) {
            return 0;
        }
        if (na > nb) {
            n = na;
            if ((n / nb) * nb != n) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
        } else {
            n = nb;
            if ((n / na) * na != n) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
        }
        return n;
    }
}

