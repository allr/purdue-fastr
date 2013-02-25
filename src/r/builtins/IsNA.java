package r.builtins;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: add more specializations, e.g. for a numeric vector
// FIXME: Truffle can't inline BuiltIn.BuiltIn1
public class IsNA {

    public static RLogical generic(RAny arg) {
        if (arg instanceof RArray) {
            final RArray a = (RArray) arg;
            final int asize = a.size();
            if (asize == 1) {
                return a.isNAorNaN(0) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
            }
            if (asize > 1) {
                return new View.RLogicalProxy<RArray>(a) {

                    @Override
                    public int size() {
                        return asize;
                    }

                    @Override
                    public int getLogical(int i) {
                        return a.isNAorNaN(i) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
            }
            // asize == 0
            return RLogical.EMPTY;
        }
        Utils.nyi("unsupported argument");
        return null;
    }

    public static BuiltIn createGeneric(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new BuiltIn(ast, names, exprs) {
            @Override
            public final RAny doBuiltIn(Frame frame, RAny[] params) {
                return generic(params[0]);
            }
        };
    }

    enum Transition {
        SCALAR,
        GENERIC
    }

    public abstract static class IsNAAction {
        public abstract int isNA(Frame frame, RAny param) throws UnexpectedResultException;
    }

    public static class Specialized extends AbstractCall {
        // FIXME: could create ScalarLogicalBuiltIn1 for this if needed more often

        final IsNAAction isNA;

        public Specialized(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, IsNAAction isNA) {
            super(orig, argNames, argExprs);
            this.isNA = isNA;
        }

        public static Specialized createUninitialized(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            return createTransition(ast, names, exprs, Transition.SCALAR);
        }

        // FIXME: check on a larger set of benchmarks if all these are needed
        public static Specialized createTransition(ASTNode ast, RSymbol[] names, RNode[] exprs, final Transition t) {
            IsNAAction a = new IsNAAction() {
                @Override
                public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                    throw new UnexpectedResultException(t);
                }
            };
            return new Specialized(ast, names, exprs, a);
        }

        private static void checkScalar(RArray a, Transition t) throws UnexpectedResultException {
            if (a.size() != 1) {
                throw new UnexpectedResultException(t);
            }
        }

        public static Specialized createScalar(ASTNode ast, RSymbol[] names, RNode[] exprs, RAny typeTemplate) {
            if (typeTemplate instanceof ScalarDoubleImpl) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        return ((ScalarDoubleImpl) param).isNAorNaN() ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RDouble) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RDouble)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RDouble v = (RDouble) param;
                        checkScalar(v, Transition.GENERIC);
                        return RDouble.RDoubleUtils.isNAorNaN(v.getDouble(0)) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarIntImpl) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        return ((ScalarIntImpl) param).isNAorNaN() ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RInt) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RInt)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RInt v = (RInt) param;
                        checkScalar(v, Transition.GENERIC);
                        return v.getInt(0) == RInt.NA ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarLogicalImpl) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof ScalarLogicalImpl)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        return ((ScalarLogicalImpl) param).isNAorNaN() ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RLogical) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RLogical)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RLogical v = (RLogical) param;
                        checkScalar(v, Transition.GENERIC);
                        return v.getLogical(0) == RLogical.NA ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RString) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final int isNA(Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RString)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RString v = (RString) param;
                        checkScalar(v, Transition.GENERIC);
                        return v.getString(0) == RString.NA ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            return createTransition(ast, names, exprs, Transition.GENERIC);
        }

        @Override
        public final int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
            RAny arg = (RAny) argExprs[0].execute(frame);
            return executeScalarNonNALogical(frame, arg);
        }

        @Override
        public final int executeScalarLogical(Frame frame) throws UnexpectedResultException {
            RAny arg = (RAny) argExprs[0].execute(frame);
            return executeScalarNonNALogical(frame, arg);
        }

        @Override
        public final Object execute(Frame frame) {
            try {
                return RLogical.RLogicalFactory.getScalar(executeScalarNonNALogical(frame));
            } catch (UnexpectedResultException e) {
                return e.getResult();
            }
        }

        public final int executeScalarNonNALogical(Frame frame, RAny arg) throws UnexpectedResultException {
            try {
                return isNA.isNA(frame, arg);
            } catch (UnexpectedResultException e) {
                Transition t = (Transition) e.getResult();
                Specialized s = null;
                switch(t) {
                    case SCALAR:
                        s = createScalar(ast, argNames, argExprs, arg);
                        replace(s, "install SimpleScalars in IsNA.Specialized");
                        return s.executeScalarNonNALogical(frame, arg);

                    case GENERIC:
                    default:
                        replace(createGeneric(ast, argNames, argExprs), "install Generic in IsNA.Specialized");
                        RLogical res = generic(arg);
                        throw new UnexpectedResultException(res);
                }
             }
        }
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
           BuiltIn.ensureArgName(call, "x", names[0]);
           return Specialized.createUninitialized(call, names, exprs);
//             return createGeneric(call, names, exprs);
        }
    };
}

