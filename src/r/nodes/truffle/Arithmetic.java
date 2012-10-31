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
    public final Object execute(RContext context, Frame frame) {
        RAny lexpr = (RAny) left.execute(context, frame);
        RAny rexpr = (RAny) right.execute(context, frame);
        return execute(context, lexpr, rexpr);
    }

    public Object execute(RContext context, RAny lexpr, RAny rexpr) {
        Specialized sn = Specialized.createSpecialized(lexpr, rexpr, ast, left, right, arit);
        replace(sn, "install Specialized from Uninitialized");
        return sn.execute(context,  lexpr, rexpr);
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
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RDouble) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RDouble && rexpr instanceof RDouble)) {
                            throw new UnexpectedResultException(null);
                        }
                        RDouble ld = (RDouble) lexpr;
                        RDouble rd = (RDouble) rexpr;
                        if (ld.size() != 1 || rd.size() != 1) {
                            throw new UnexpectedResultException(null);
                        }
                        double ldbl = ld.getDouble(0);
                        double rdbl = rd.getDouble(0);
                        if (RDouble.RDoubleUtils.isNA(ldbl) || RDouble.RDoubleUtils.isNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rdbl));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<RDouble, RDouble>");
            }
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RInt) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RDouble && rexpr instanceof RInt)) {
                            throw new UnexpectedResultException(null);
                        }
                        RDouble ld = (RDouble) lexpr;
                        RInt ri = (RInt) rexpr;
                        if (ld.size() != 1 || ri.size() != 1) {
                            throw new UnexpectedResultException(null);
                        }
                        double ldbl = ld.getDouble(0);
                        int rint = ri.getInt(0);
                        if (RDouble.RDoubleUtils.isNA(ldbl) || rint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(ldbl, rint));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<RDouble, RInt>");
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RDouble) {
                Calculator c = new Calculator() {
                    @Override
                    public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RInt && rexpr instanceof RDouble)) {
                            throw new UnexpectedResultException(null);
                        }
                        RInt li = (RInt) lexpr;
                        RDouble rd = (RDouble) rexpr;
                        if (li.size() != 1 || rd.size() != 1) {
                            throw new UnexpectedResultException(null);
                        }
                        int lint = li.getInt(0);
                        double rdbl = rd.getDouble(0);
                        if (lint == RInt.NA || RDouble.RDoubleUtils.isNA(rdbl)) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getScalar(arit.op(lint, rdbl));
                    }
                };
                return new Specialized(ast, left, right, arit, c, "<RInt, RDouble>");
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RInt) {
                if (returnsDouble(arit)) {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(lexpr instanceof RInt && rexpr instanceof RInt)) {
                                throw new UnexpectedResultException(null);
                            }
                            RInt li = (RInt) lexpr;
                            RInt ri = (RInt) rexpr;
                            if (li.size() != 1 || ri.size() != 1) {
                                throw new UnexpectedResultException(null);
                            }
                            int lint = li.getInt(0);
                            int rint = ri.getInt(0);
                            if (lint == RInt.NA || rint == RInt.NA) {
                                return RDouble.BOXED_NA;
                            }
                            return RDouble.RDoubleFactory.getScalar(arit.op((double) lint, (double) rint));
                        }
                    };
                    return new Specialized(ast, left, right, arit, c, "<RInt, RInt>");
                } else {
                    Calculator c = new Calculator() {
                        @Override
                        public Object calc(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                            if (!(lexpr instanceof RInt && rexpr instanceof RInt)) {
                                throw new UnexpectedResultException(null);
                            }
                            RInt li = (RInt) lexpr;
                            RInt ri = (RInt) rexpr;
                            if (li.size() != 1 || ri.size() != 1) {
                                throw new UnexpectedResultException(null);
                            }
                            int lint = li.getInt(0);
                            int rint = ri.getInt(0);
                            if (lint == RInt.NA || rint == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                            return RInt.RIntFactory.getScalar(arit.op(lint, rint));
                        }
                    };
                    return new Specialized(ast, left, right, arit, c, "<RInt, RInt>");
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
                        RDouble res = new DoubleView(ldbl, rdbl, context, arit, ast);
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
                            RDouble res = new DoubleView(ldbl, rdbl, context, arit, ast);
                            if (!EAGER) {
                                return res;
                            } else {
                                return RDouble.RDoubleFactory.copy(res);
                            }

                        }
                        if (lexpr instanceof RInt || rexpr instanceof RInt || lexpr instanceof RLogical || rexpr instanceof RLogical) { // FIXME: this check should be simpler
                            RInt lint = lexpr.asInt();
                            RInt rint = rexpr.asInt();
                            RInt res = new IntView(lint, rint, context, arit, ast);
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

        final ValueArithmetic arit;
        final ASTNode ast;

        public DoubleView(RDouble a, RDouble b, RContext context, ValueArithmetic arit, ASTNode ast) {
            this.a = a;
            this.b = b;
            this.context = context;
            na = a.size();
            nb = b.size();

            this.arit = arit;
            this.ast = ast;

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
    }

    static class IntView extends View.RIntView implements RInt {
        final RInt a;
        final RInt b;
        final RContext context;
        final int na;
        final int nb;
        final int n;
        boolean overflown = false;

        final ValueArithmetic arit;
        final ASTNode ast;


        public IntView(RInt a, RInt b, RContext context, ValueArithmetic arit, ASTNode ast) {
            this.a = a;
            this.b = b;
            this.context = context;
            na = a.size();
            nb = b.size();
            this.ast = ast;
            this.arit = arit;

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
    }
}
