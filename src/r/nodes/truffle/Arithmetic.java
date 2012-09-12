package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.BinaryOperation.BinaryOperator;


public class Arithmetic extends BaseR {

    RNode left;
    RNode right;
    final ValueArithmetic arit;
    final BinaryOperator op;

    private static final boolean DEBUG_AR = true;

    public Arithmetic(ASTNode ast, RNode left, RNode right, BinaryOperator op) {
        super(ast);
        this.left = updateParent(left);
        this.right = updateParent(right);
        this.op = op;

        switch(op) {
            case ADD: this.arit = ADD; break;
            case SUB: this.arit = SUB; break;
            case MULT: this.arit = MULT; break;
            default:
                throw new RuntimeException("not implemented arithmetic operation");
        }
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
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
                if (ldbl == RDouble.NA) {
                    return RDouble.BOXED_NA;
                }
                if (rarr instanceof RDouble) {
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (rdbl == RDouble.NA) {
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
                } else if (rarr instanceof RDouble) {
                    if (lint == RInt.NA) {
                        return RDouble.BOXED_NA;
                    }
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (rdbl == RDouble.NA) {
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
        }
        return null;
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

//        public RInt op(RInt a, RInt b, RContext context);
//        public RDouble op(RDouble a, RDouble b, RContext context);
//        public RDouble op(RDouble a, double b);
//        public RDouble op(double a, RDouble b);
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

    protected static Add ADD = new Add();
    protected static Sub SUB = new Sub();
    protected static Mult MULT = new Mult();
}
