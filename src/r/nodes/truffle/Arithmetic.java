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
    final boolean returnsDouble;

    private static final boolean DEBUG_AR = false;

    public Arithmetic(ASTNode ast, RNode left, RNode right, ValueArithmetic arit) {
        super(ast);
        this.left = updateParent(left);
        this.right = updateParent(right);
        this.arit = arit;
        this.returnsDouble = (arit == POW || arit == DIV);
    }

    @Override
    public final Object execute(RContext context, Frame frame) {
        // version for scalars
        RAny lexpr = (RAny) left.execute(context, frame);
        RAny rexpr = (RAny) right.execute(context, frame);

        try {
            if (DEBUG_AR) Utils.debug("arithmetic - assuming scalar numbers");
            RArray larr = RValueConversion.expectArrayOne(lexpr);
            RArray rarr = RValueConversion.expectArrayOne(rexpr);
              // FIXME: partially copy-paste from comparison, but not quite the same
              // FIXME: If we can assume that when numeric scalars are compared, their types (int, double) are stable,
              //        we might try to improve performance by splitting the code below into different truffle nodes
            if (larr instanceof RDouble) { // note: could make this shorter if we didn't care about Java-level boxing
                double ldbl = ((RDouble) larr).getDouble(0);
                if (RDouble.RDoubleUtils.isNA(ldbl)) {
                    return RDouble.BOXED_NA;
                }
                if (rarr instanceof RDouble) {
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RDouble.BOXED_NA;
                    }
                    return RDouble.RDoubleFactory.getArray(arit.op(ldbl, rdbl)); // FIXME: can we get rid of R-level boxing?
                } else if (rarr instanceof RInt) {
                    int rint = ((RInt) rarr).getInt(0);
                    if (rint == RInt.NA) {
                        return RDouble.BOXED_NA;
                    }
                    return RDouble.RDoubleFactory.getArray(arit.op(ldbl, rint));
                } else {
                    throw new UnexpectedResultException(null);
                }
            } else if (larr instanceof RInt) {
                int lint = ((RInt) larr).getInt(0);
                if (rarr instanceof RInt) {
                    if (returnsDouble) {
                        if (lint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        int rint = ((RInt) rarr).getInt(0);
                        if (rint == RInt.NA) {
                            return RDouble.BOXED_NA;
                        }
                        return RDouble.RDoubleFactory.getArray(arit.op((double) lint, (double) rint));
                    } else {
                        if (lint == RInt.NA) {
                            return RInt.BOXED_NA;
                        }
                        int rint = ((RInt) rarr).getInt(0);
                        if (rint == RInt.NA) {
                            return RInt.BOXED_NA;
                        }
                        int res = arit.op(lint, rint);
                        if (res != RInt.NA) {
                            return RInt.RIntFactory.getArray(res);
                        } else {
                            context.warning(ast, RError.INTEGER_OVERFLOW);
                            return RInt.BOXED_NA;
                        }
                    }
                } else if (rarr instanceof RDouble) {
                    if (lint == RInt.NA) {
                        return RDouble.BOXED_NA;
                    }
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RDouble.BOXED_NA;
                    }
                    return RDouble.RDoubleFactory.getArray(arit.op(lint, rdbl));
                } else {
                    throw new UnexpectedResultException(null);
                }
            } else {
                throw new UnexpectedResultException(null);
            }
        } catch (UnexpectedResultException e) {
            if (DEBUG_AR) Utils.debug("arithmetic - optimistic arithmetic failed, values are not scalars");
            GenericArithmetic ga = new GenericArithmetic(ast);
            replace(ga, "genericArithmetic");
            return ga.execute(context, lexpr, rexpr);
        }
    }

    class GenericArithmetic extends BaseR {
        public GenericArithmetic(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
            RAny lexpr = (RAny) left.execute(context, frame);
            RAny rexpr = (RAny) right.execute(context, frame);
            return execute(context, lexpr, rexpr);
        }

        public Object execute(RContext context, RAny lexpr, RAny rexpr) {
            if (DEBUG_AR) Utils.debug("arithmetic - generic case");

            if (returnsDouble) {
                RDouble ldbl = lexpr.asDouble();
                RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                return new DoubleView(ldbl, rdbl, context);
            } else {
                if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                    RDouble ldbl = lexpr.asDouble();
                    RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                    return new DoubleView(ldbl, rdbl, context);
                }
                if (lexpr instanceof RInt || rexpr instanceof RInt || lexpr instanceof RLogical || rexpr instanceof RLogical) {
                    RInt lint = lexpr.asInt();
                    RInt rint = rexpr.asInt();
                    return new IntView(lint, rint, context);
                }
            }
            Utils.nyi("unsupported case for binary arithmetic operation");
            return null;
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

    class DoubleView extends View.RDoubleView implements RDouble {
        final RDouble a;
        final RDouble b;
        final RContext context;
        final int na;
        final int nb;
        final int n;

        public DoubleView(RDouble a, RDouble b, RContext context) {
            this.a = a;
            this.b = b;
            this.context = context;
            na = a.size();
            nb = b.size();

            if (na > nb) {
                n = na;
                if ((n / nb) * nb != n) {
                    context.warning(Arithmetic.this.ast, RError.LENGTH_NOT_MULTI);
                }
            } else {
                n = nb;
                if ((n / na) * na != n) {
                    context.warning(Arithmetic.this.ast, RError.LENGTH_NOT_MULTI);
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
                return Arithmetic.this.arit.op(adbl, bdbl);
            }
         }
    }

    class IntView extends View.RIntView implements RInt {
        final RInt a;
        final RInt b;
        final RContext context;
        final int na;
        final int nb;
        final int n;
        boolean overflown = false;

        public IntView(RInt a, RInt b, RContext context) {
            this.a = a;
            this.b = b;
            this.context = context;
            na = a.size();
            nb = b.size();

            if (na > nb) {
                n = na;
                if ((n / nb) * nb != n) {
                    context.warning(Arithmetic.this.ast, RError.LENGTH_NOT_MULTI);
                }
            } else {
                n = nb;
                if ((n / na) * na != n) {
                    context.warning(Arithmetic.this.ast, RError.LENGTH_NOT_MULTI);
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
                int res = Arithmetic.this.arit.op(aint, bint);
                if (res == RInt.NA && !overflown) {
                    overflown = true;
                    context.warning(ast, RError.INTEGER_OVERFLOW);
                }
                return res;
            }
        }
    }
}
