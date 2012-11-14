package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;


public class Arithmetic extends BaseR {

    @Stable RNode left;
    @Stable RNode right;
    final ValueArithmetic arit;

    private static final boolean EAGER = false;

    public Arithmetic(ASTNode ast, RNode left, RNode right, ValueArithmetic arit) {
        super(ast);
        this.left = updateParent(left);
        this.right = updateParent(right);
        this.arit = arit;
    }

    public static boolean returnsDouble(ValueArithmetic arit) {
        return (arit == POW || arit == DIV);
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RAny lexpr = (RAny) left.execute(context, frame);
        RAny rexpr = (RAny) right.execute(context, frame);
        return execute(context, lexpr, rexpr);
    }

    public Object execute(RContext context, RAny lexpr, RAny rexpr) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {

            if (left instanceof Constant || right instanceof Constant) {
                SpecializedConst sc = SpecializedConst.createSpecialized(lexpr, rexpr, ast, left, right, arit);
                replace(sc, "install Specialized from Uninitialized");
                return sc.execute(context,  lexpr, rexpr);
            } else {
                Specialized sn = Specialized.createSpecialized(lexpr, rexpr, ast, left, right, arit);
                replace(sn, "install Specialized from Uninitialized");
                return sn.execute(context,  lexpr, rexpr);
            }
        }
    }

    static class Specialized extends Arithmetic {
        final String dbg;
        final Calculator calc;

        public Specialized(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            super(ast, left, right, arit);
            this.dbg = dbg;
            this.calc = calc;
        }

        public abstract static class Calculator {
            public abstract Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException;
        }

        public static Specialized createSpecialized(RAny leftTemplate, RAny rightTemplate, ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                        double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (RDouble.RDoubleUtils.isNA(ldbl) || RDouble.RDoubleUtils.isNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rdbl));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarDouble, ScalarDouble>");
            }
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarIntImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                        int rint = ((ScalarIntImpl) rexpr).getInt();
                        if (RDouble.RDoubleUtils.isNA(ldbl) || rint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rint));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarDouble, ScalarInt>");
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        RInt li = (RInt) lexpr;
                        RDouble rd = (RDouble) rexpr;
                        if (li.size() != 1 || rd.size() != 1) {
                            throw new UnexpectedResultException(null);
                        }
                        int lint = ((ScalarIntImpl) lexpr).getInt();
                        double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (lint == RInt.NA || RDouble.RDoubleUtils.isNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(lint, rdbl));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<ScalarInt, ScalarDouble>");
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarIntImpl) {
                if (returnsDouble(arit)) {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (lint == RInt.NA || rint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op((double) lint, (double) rint));
                        }
                    };
                    return new Specialized(ast, left, right, arit, c, "<ScalarInt, ScalarInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (lint == RInt.NA || rint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.op(lint, rint));
                        }
                    };
                    return new Specialized(ast, left, right, arit, c, "<ScalarInt, ScalarInt>");
                }
            }
            return createGeneric(ast, left, right, arit);
        }

        public static Specialized createGeneric(final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            Calculator c;

            if (returnsDouble(arit)) {
                c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) {
                        RDouble ldbl = lexpr.asDouble();
                        RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                        RDouble res = DoubleView.create(ldbl, rdbl, context, arit, ast);
                        if (!EAGER) {
                            return res;
                        } else {
                            return RDouble.RDoubleFactory.copy(res);
                        }
                    }
                };
            } else {
                c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) {
                        if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                            RDouble ldbl = lexpr.asDouble();
                            RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                            RDouble res = DoubleView.create(ldbl, rdbl, context, arit, ast);
                            if (!EAGER) {
                                return res;
                            } else {
                                return RDouble.RDoubleFactory.copy(res);
                            }

                        }
                        if (lexpr instanceof RInt || rexpr instanceof RInt || lexpr instanceof RLogical || rexpr instanceof RLogical) { // FIXME: this check should be simpler
                            RInt lint = lexpr.asInt();
                            RInt rint = rexpr.asInt();
                            RInt res = IntView.create(lint, rint, context, arit, ast);
                            if (!EAGER) {
                                return res;
                            } else {
                                return RInt.RIntFactory.copy(res);
                            }
                        }
                        Utils.nyi("unsupported case for binary arithmetic operation");
                        return null;
                    }
                };
            }
            return new Specialized(ast, left, right, arit, c, "<Generic, Generic>");
        }

        @Override
        public final Object execute(RContext context, RAny lexpr, RAny rexpr) {
            try {
                return calc.calc(context, lexpr, rexpr);
            } catch (UnexpectedResultException e) {
                Specialized gn = createGeneric(ast, left, right, arit);
                replace(gn, "install Specialized<Generic, Generic> from Specialized");
                return gn.execute(context, lexpr, rexpr);
            }
        }
    }

    static class SpecializedConst extends Arithmetic {
        final String dbg;
        final Calculator calc;

        public SpecializedConst(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            super(ast, left, right, arit);
            this.dbg = dbg;
            this.calc = calc;
        }

        public abstract static class Calculator {
            public abstract Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException;
        }

        public static SpecializedConst createSpecialized(RAny leftTemplate, RAny rightTemplate, ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            boolean leftConst = left instanceof Constant;
            boolean rightConst = right instanceof Constant;
            // non-const is double
            if (leftConst && (rightTemplate instanceof ScalarDoubleImpl) && (leftTemplate instanceof ScalarDoubleImpl || leftTemplate instanceof ScalarIntImpl || leftTemplate instanceof ScalarLogicalImpl)) {
                final double ldbl = (leftTemplate.asDouble()).getDouble(0);
                final boolean isLeftNA = RDouble.RDoubleUtils.isNA(ldbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(rexpr instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (isLeftNA || RDouble.RDoubleUtils.isNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rdbl));
                    }
                };
                return createLeftConst(ast, left, right, arit, c, "<ConstScalarDouble, Number>");
            }
            if (rightConst && (leftTemplate instanceof ScalarDoubleImpl) && (rightTemplate instanceof ScalarDoubleImpl || rightTemplate instanceof ScalarIntImpl || rightTemplate instanceof ScalarLogicalImpl)) {
                final double rdbl = (rightTemplate.asDouble()).getDouble(0);
                final boolean isRightNA = RDouble.RDoubleUtils.isNA(rdbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                        if (isRightNA || RDouble.RDoubleUtils.isNA(ldbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rdbl));
                    }
                };
                return createRightConst(ast, left, right, arit, c, "<Number, ConstScalarDouble>");
            }
            // non-const is int and const is double
            // FIXME: handle also logical?
            if (leftConst && (leftTemplate instanceof ScalarDoubleImpl) && (rightTemplate instanceof ScalarIntImpl)) {
                final double ldbl = (leftTemplate.asDouble()).getDouble(0);
                final boolean isLeftNA = RDouble.RDoubleUtils.isNA(ldbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(rexpr instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        int rint = ((ScalarIntImpl) rexpr).getInt();
                        if (isLeftNA || rint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rint));
                    }
                };
                return createLeftConst(ast, left, right, arit, c, "<ConstScalarDouble, ScalarInt>");
            }
            if (rightConst && (rightTemplate instanceof ScalarDoubleImpl) && (leftTemplate instanceof ScalarIntImpl)) {
                final double rdbl = (rightTemplate.asDouble()).getDouble(0);
                final boolean isRightNA = RDouble.RDoubleUtils.isNA(rdbl);
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(null);
                        }
                        int lint = ((ScalarIntImpl) lexpr).getInt();
                        if (isRightNA || lint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(lint, rdbl));
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
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(rexpr instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (isLeftNA || rint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, (double) rint));
                        }
                    };
                    return createLeftConst(ast, left, right, arit, c, "<ConstScalarInt, ScalarInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(rexpr instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int rint = ((ScalarIntImpl) rexpr).getInt();
                            if (isLeftNA || rint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.op(lint, rint));
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
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(lexpr instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            if (isRightNA || lint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op((double) lint, rdbl));
                        }
                    };
                    return createRightConst(ast, left, right, arit, c, "<ScalarInt, ConstScalarInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(lexpr instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int lint = ((ScalarIntImpl) lexpr).getInt();
                            if (isRightNA || lint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.op(lint, rint));
                        }
                    };
                    return createRightConst(ast, left, right, arit, c, "<ScalarInt, ConstScalarInt>");
                }
            }
            return createGeneric(leftTemplate, rightTemplate, ast, left, right, arit);
        }

        public static SpecializedConst createGeneric(RAny leftTemplate, RAny rightTemplate, final ASTNode ast, RNode left, RNode right, final ValueArithmetic arit) {
            Calculator c = null;
            boolean leftConst = left instanceof Constant;
            boolean rightConst = right instanceof Constant;

            if (returnsDouble(arit)) {
                if (leftConst) {
                    final RDouble ldbl = leftTemplate.asDouble(); // FIXME: could force materialization
                    c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) {
                            RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                            RDouble res = DoubleView.create(ldbl, rdbl, context, arit, ast);
                            if (!EAGER) {
                                return res;
                            } else {
                                return RDouble.RDoubleFactory.copy(res);
                            }
                        }
                    };
                }
                if (rightConst) {
                    final RDouble rdbl = rightTemplate.asDouble(); // FIXME: could force materialization
                    c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) {
                            RDouble ldbl = lexpr.asDouble();  // if the cast fails, a zero-length array is returned
                            RDouble res = DoubleView.create(ldbl, rdbl, context, arit, ast);
                            if (!EAGER) {
                                return res;
                            } else {
                                return RDouble.RDoubleFactory.copy(res);
                            }
                        }
                    };
                }
            } else {
                if (leftConst) {
                    final boolean leftDouble = leftTemplate instanceof RDouble;
                    final boolean leftLogicalOrInt = leftTemplate instanceof RLogical || leftTemplate instanceof RInt;
                    final RDouble ldbl = (leftDouble) ? (RDouble) leftTemplate : leftTemplate.asDouble();
                    final RInt lint = (leftLogicalOrInt) ? leftTemplate.asInt() : null;
                    c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) {
                            if (leftDouble || rexpr instanceof RDouble) {
                                RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                                RDouble res = DoubleView.create(ldbl, rdbl, context, arit, ast);
                                if (!EAGER) {
                                    return res;
                                } else {
                                    return RDouble.RDoubleFactory.copy(res);
                                }

                            }
                            if (leftLogicalOrInt || rexpr instanceof RInt || rexpr instanceof RLogical) { // FIXME: this check should be simpler
                                RInt rint = rexpr.asInt();
                                RInt res = IntView.create(lint, rint, context, arit, ast);
                                if (!EAGER) {
                                    return res;
                                } else {
                                    return RInt.RIntFactory.copy(res);
                                }
                            }
                            Utils.nyi("unsupported case for binary arithmetic operation");
                            return null;
                        }
                    };
                }
                if (rightConst) {
                    final boolean rightDouble = rightTemplate instanceof RDouble;
                    final boolean rightLogicalOrInt = rightTemplate instanceof RLogical || rightTemplate instanceof RInt;
                    final RDouble rdbl = (rightDouble) ? (RDouble) rightTemplate : rightTemplate.asDouble();
                    final RInt rint = (rightLogicalOrInt) ? rightTemplate.asInt() : null;
                    c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) {
                            if (rightDouble || lexpr instanceof RDouble) {
                                RDouble ldbl = lexpr.asDouble();  // if the cast fails, a zero-length array is returned
                                RDouble res = DoubleView.create(ldbl, rdbl, context, arit, ast);
                                if (!EAGER) {
                                    return res;
                                } else {
                                    return RDouble.RDoubleFactory.copy(res);
                                }

                            }
                            if (rightLogicalOrInt || lexpr instanceof RInt || lexpr instanceof RLogical) { // FIXME: this check should be simpler
                                RInt lint = lexpr.asInt();
                                RInt res = IntView.create(lint, rint, context, arit, ast);
                                if (!EAGER) {
                                    return res;
                                } else {
                                    return RInt.RIntFactory.copy(res);
                                }
                            }
                            Utils.nyi("unsupported case for binary arithmetic operation");
                            return null;
                        }
                    };
                }
            }
            if (c == null) {
                Utils.nyi("unreachable");
                return null;
            }
            if (rightConst) {
                return createRightConst(ast, left, right, arit, c, "<Generic, ConstGeneric>");
            } else {
                return createLeftConst(ast, right, left, arit, c, "<ConstGeneric, Generic>");
            }
        }

        public static SpecializedConst createLeftConst(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            return new SpecializedConst(ast, left, right, arit, calc, dbg) {
                @Override
                public Object execute(RContext context, Frame frame) {
                    RAny rexpr = (RAny) right.execute(context, frame);
                    return execute(context, null, rexpr);
                }
            };
        }

        public static SpecializedConst createRightConst(ASTNode ast, RNode left, RNode right, ValueArithmetic arit, Calculator calc, String dbg) {
            return new SpecializedConst(ast, left, right, arit, calc, dbg) {
                @Override
                public Object execute(RContext context, Frame frame) {
                    RAny lexpr = (RAny) left.execute(context, frame);
                    return execute(context, lexpr, null);
                }
            };
        }

        private static RAny getExpr(RNode node, RAny value) {
            if (value == null) {
                return (RAny) node.execute(null, null);
            } else {
                return value;
            }
        }

        @Override
        public Object execute(RContext context, RAny lexpr, RAny rexpr) {
            try {
                return calc.calc(context, lexpr, rexpr);
            } catch (UnexpectedResultException e) {
                RAny leftTemplate = getExpr(left, lexpr);
                RAny rightTemplate = getExpr(right, rexpr);
                SpecializedConst gn = createGeneric(leftTemplate, rightTemplate, ast, left, right, arit);
                replace(gn, "install SpecializedConst<Generic, Generic> from SpecializedConst");
                return gn.execute(context, leftTemplate, rightTemplate);
            }
        }
    }

    public abstract static class ValueArithmetic {
        public abstract double op(double a, double b);
        public abstract int op(int a, int b);

        public double op(double a, int b) {
            return op(a, (double) b);
        }
        public double op(int a, double b) {
            return op((double) a, b);
        }
    }

    public static final class Add extends ValueArithmetic {
        @Override
        public double op(double a, double b) {
            return a + b;
        }
        @Override
        public int op(int a, int b) {
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
    }

    public static final class Sub extends ValueArithmetic {
        @Override
        public double op(double a, double b) {
            return a - b;
        }
        @Override
        public int op(int a, int b) {
            int r = a - b;
            if ((a < 0 == b < 0) || (a < 0 == r < 0)) {
                return r;
            } else {
                return RInt.NA;
            }
        }
    }

    public static final class Mult extends ValueArithmetic {
        @Override
        public double op(double a, double b) {
            return a * b;
        }
        @Override
        public int op(int a, int b) {
            long l = (long) a * (long) b;
            if (!(l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)) {
                return (int) l;
            } else {
                return RInt.NA;
            }
        }
    }

    public static final class Pow extends ValueArithmetic {
        @Override
        public double op(double a, double b) {
            return Math.pow(a, b); // FIXME: check that the R rules correspond to Java
        }
        @Override
        public int op(int a, int b) {
            Utils.nyi("unreachable");
            return -1;
        }
    }

    public static final class Div extends ValueArithmetic {
        @Override
        public double op(double a, double b) {
            return a / b; // FIXME: check that the R rules correspond to Java
        }
        @Override
        public int op(int a, int b) {
            Utils.nyi("unreachable");
            return -1;
        }
    }

    public static final Add ADD = new Add();
    public static final Sub SUB = new Sub();
    public static final Mult MULT = new Mult();
    public static final Pow POW = new Pow();
    public static final Div DIV = new Div();

    static class DoubleView extends View.RDoubleView implements RDouble {
        final RDouble a;
        final RDouble b;
        final RContext context;
        final int na;
        final int nb;
        final int n;
        final int[] dimensions;

        final ValueArithmetic arit;
        final ASTNode ast;

        public static DoubleView create(RDouble a, RDouble b, RContext context, ValueArithmetic arit, ASTNode ast) {
            int[] dim = resultDimensions(ast, a, b);
            return new DoubleView(a, b, dim, context, arit, ast);
        }

        public DoubleView(RDouble a, RDouble b, int[] dimensions, RContext context, ValueArithmetic arit, ASTNode ast) {
            this.a = a;
            this.b = b;
            this.context = context;
            na = a.size();
            nb = b.size();

            this.arit = arit;
            this.ast = ast;
            this.dimensions = dimensions;

            if (na > nb) {
                n = na;
                if ((n / nb) * nb != n) {
                    context.warning(ast, RError.LENGTH_NOT_MULTI);
                }
            } else {
                n = nb;
                if ((n / na) * na != n) {
                    context.warning(ast, RError.LENGTH_NOT_MULTI);
                }
            }
        }

        @Override
        public int size() {
            return n;
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
            if (RDouble.RDoubleUtils.isNA(adbl) || RDouble.RDoubleUtils.isNA(bdbl)) {
                return RDouble.NA;
            } else {
                return arit.op(adbl, bdbl);
            }
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
        public int[] dimensions() {
            return dimensions;
        }
    }

    static class IntView extends View.RIntView implements RInt {
        final RInt a;
        final RInt b;
        final RContext context;
        final int na;
        final int nb;
        final int n;
        final int[] dimensions;
        boolean overflown = false;

        final ValueArithmetic arit;
        final ASTNode ast;

        public static IntView create(RInt a, RInt b, RContext context, ValueArithmetic arit, ASTNode ast) {
            int[] dim = resultDimensions(ast, a, b);
            return new IntView(a, b, dim, context, arit, ast);
        }

        public IntView(RInt a, RInt b, int[] dimensions, RContext context, ValueArithmetic arit, ASTNode ast) {
            this.a = a;
            this.b = b;
            this.context = context;
            na = a.size();
            nb = b.size();
            this.ast = ast;
            this.arit = arit;
            this.dimensions = dimensions;

            if (na > nb) {
                n = na;
                if ((n / nb) * nb != n) {
                    context.warning(ast, RError.LENGTH_NOT_MULTI);
                }
            } else {
                n = nb;
                if ((n / na) * na != n) {
                    context.warning(ast, RError.LENGTH_NOT_MULTI);
                }
            }
        }

        @Override
        public int size() {
            return n;
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
                int res = arit.op(aint, bint);
                if (res == RInt.NA && !overflown) {
                    overflown = true;
                    context.warning(ast, RError.INTEGER_OVERFLOW);
                }
                return res;
            }
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
        public int[] dimensions() {
            return dimensions;
        }
    }

    public static int[] resultDimensions(ASTNode ast, RArray a, RArray b) {
        int[] dima = a.dimensions();
        int[] dimb = b.dimensions();
        if (dimb == null) {
            return dima;
        }
        if (dima == null) {
            return dimb;
        }
        if (dima == dimb) {
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
}
