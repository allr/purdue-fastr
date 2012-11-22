package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Extreme {

    public abstract static class Operation {
        public abstract boolean moreExtreme(int a, int b);
        public abstract boolean moreExtreme(double a, double b);
        public abstract double extreme(double a, double b);
        public abstract int extreme(int a, int b);
        public abstract RDouble emptySetExtreme();
    }

    public static final class ExtremeCallFactory extends CallFactory {
        final Operation op;

        public ExtremeCallFactory(Operation op) {
            this.op = op;
        }

        // result is RDouble scalar
        public RDouble extreme(RDouble arg, RContext context, ASTNode ast) {
            int size = arg.size();
            if (size == 0) {
                context.warning(ast, RError.NO_NONMISSING_MAX);
                return op.emptySetExtreme();
            }
            double res = arg.getDouble(0);
            for (int i = 1; i < size; i++) {
                double d = arg.getDouble(i);
                if (op.moreExtreme(d, res)) {
                    res = d;
                }
                if (RDouble.RDoubleUtils.isNA(d)) {
                    return RDouble.BOXED_NA;
                }
            }
            return RDouble.RDoubleFactory.getScalar(res);
        }

        // result is RDouble or RInt scalar
        public RAny extreme(RInt arg, RContext context, ASTNode ast) {
            int size = arg.size();
            if (size == 0) {
                context.warning(ast, RError.NO_NONMISSING_MAX);
                return op.emptySetExtreme();
            }
            int res = arg.getInt(0);
            for (int i = 1; i < size; i++) {
                int v = arg.getInt(i);
                if (op.moreExtreme(v, res)) {
                    res = v;
                }
                if (v == RInt.NA) {
                    return RInt.BOXED_NA;
                }
            }
            return RInt.RIntFactory.getScalar(res);
        }

        // result is RDouble or RInt scalar
        public RAny extreme(RLogical arg, RContext context, ASTNode ast) {
            int size = arg.size();
            if (size == 0) {
                context.warning(ast, RError.NO_NONMISSING_MAX);
                return op.emptySetExtreme();
            }
            int res = arg.getLogical(0);
            for (int i = 1; i < size; i++) {
                int v = arg.getLogical(i);
                if (v > res) {
                    res = v;
                }
                if (v == RLogical.NA) {
                    return RInt.BOXED_NA;
                }
            }
            return RInt.RIntFactory.getScalar(res);
        }

        // result is RDouble or RInt scalar
        public RAny extreme(RAny arg, RContext context, ASTNode ast) {
            if (arg instanceof RDouble) {
                return extreme((RDouble) arg, context, ast);
            }
            if (arg instanceof RInt) {
                return extreme((RInt) arg, context, ast);
            }
            if (arg instanceof RLogical) {
                return extreme((RLogical) arg, context, ast);
            }
            Utils.nyi("unsupported type");
            return null;
        }

        // takes RDouble and RInt scalars
        // returns RDouble or RInt scalar
        public RAny extreme(RAny scalar0, RAny scalar1) { // FIXME: does this preserve NA's ?
            if (scalar0 instanceof RDouble) {
                if (scalar1 instanceof RDouble) {
                    return RDouble.RDoubleFactory.getScalar(op.extreme(((RDouble) scalar0).getDouble(0), ((RDouble) scalar1).getDouble(0)));
                } else {
                    return RDouble.RDoubleFactory.getScalar(op.extreme(((RDouble) scalar0).getDouble(0), Convert.int2double(((RInt) scalar1).getInt(0))));
                }
            } else {
                if (scalar1 instanceof RDouble) {
                    return RDouble.RDoubleFactory.getScalar(op.extreme(Convert.int2double(((RInt) scalar0).getInt(0)), ((RDouble) scalar1).getDouble(0)));
                } else {
                    return RInt.RIntFactory.getScalar(op.extreme(((RInt) scalar0).getInt(0), ((RInt) scalar1).getInt(0)));
                }
            }
        }

        // args has length at least 2
        public RAny extreme(RAny[] args, RContext context, ASTNode ast) {
            int size = args.length;
            RAny res = extreme(args[0], context, ast);
            for (int i = 1; i < size; i++) {
                res = extreme(res, extreme(args[i], context, ast));
            }
            return res;
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        context.warning(ast, RError.NO_NONMISSING_MAX);
                        return op.emptySetExtreme();
                    }

                };
            }
            if (exprs.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                        return extreme(arg, context, ast);
                    }

                };
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    return extreme(args, context, ast);
                }
            };
        }

    }

    public static final CallFactory MAX_FACTORY = new ExtremeCallFactory(
                    new Operation() {

                        @Override
                        public boolean moreExtreme(int a, int b) {
                            return a > b;
                        }

                        @Override
                        public boolean moreExtreme(double a, double b) {
                            return a > b;
                        }

                        @Override
                        public double extreme(double a, double b) {
                            return Math.max(a, b);
                        }

                        @Override
                        public int extreme(int a, int b) {
                            return Math.max(a, b);
                        }

                        @Override
                        public RDouble emptySetExtreme() {
                            return RDouble.BOXED_NEG_INF;
                        }

                    });

    public static final CallFactory MIN_FACTORY = new ExtremeCallFactory(
                    new Operation() {

                        @Override
                        public boolean moreExtreme(int a, int b) {
                            return a < b;
                        }

                        @Override
                        public boolean moreExtreme(double a, double b) {
                            return a < b;
                        }

                        @Override
                        public double extreme(double a, double b) {
                            return Math.min(a, b);
                        }

                        @Override
                        public int extreme(int a, int b) {
                            return Math.min(a, b);
                        }

                        @Override
                        public RDouble emptySetExtreme() {
                            return RDouble.BOXED_POS_INF;
                        }

                    });

}
