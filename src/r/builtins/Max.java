package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Max {

    // result is RDouble scalar
    public static RDouble max(RDouble arg, RContext context, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            context.warning(ast, RError.NO_NONMISSING_MAX);
            return RDouble.BOXED_NEG_INF;
        }
        double res = arg.getDouble(0);
        for (int i = 1; i < size; i++) {
            double d = arg.getDouble(i);
            if (d > res) {
                res = d;
            }
            if (RDouble.RDoubleUtils.isNA(d)) {
                return RDouble.BOXED_NA;
            }
        }
        return RDouble.RDoubleFactory.getScalar(res);
    }

    // result is RDouble or RInt scalar
    public static RAny max(RInt arg, RContext context, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            context.warning(ast, RError.NO_NONMISSING_MAX);
            return RDouble.BOXED_NEG_INF;
        }
        int res = arg.getInt(0);
        for (int i = 1; i < size; i++) {
            int v = arg.getInt(i);
            if (v > res) {
                res = v;
            }
            if (v == RInt.NA) {
                return RInt.BOXED_NA;
            }
        }
        return RInt.RIntFactory.getScalar(res);
    }

    // result is RDouble or RInt scalar
    public static RAny max(RLogical arg, RContext context, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            context.warning(ast, RError.NO_NONMISSING_MAX);
            return RDouble.BOXED_NEG_INF;
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
    public static RAny max(RAny arg, RContext context, ASTNode ast) {
        if (arg instanceof RDouble) {
            return max((RDouble) arg, context, ast);
        }
        if (arg instanceof RInt) {
            return max((RInt) arg, context, ast);
        }
        if (arg instanceof RLogical) {
            return max((RLogical) arg, context, ast);
        }
        Utils.nyi("unsupported type");
        return null;
    }

    // takes RDouble and RInt scalars
    // returns RDouble or RInt scalar
    public static RAny max(RAny scalar0, RAny scalar1) {
        if (scalar0 instanceof RDouble) {
            if (scalar1 instanceof RDouble) {
                return RDouble.RDoubleFactory.getScalar(Math.max(((RDouble) scalar0).getDouble(0), ((RDouble) scalar1).getDouble(0)));
            } else {
                return RDouble.RDoubleFactory.getScalar(Math.max(((RDouble) scalar0).getDouble(0), Convert.int2double(((RInt) scalar1).getInt(0))));
            }
        } else {
            if (scalar1 instanceof RDouble) {
                return RDouble.RDoubleFactory.getScalar(Math.max(Convert.int2double(((RInt) scalar0).getInt(0)), ((RDouble) scalar1).getDouble(0)));
            } else {
                return RDouble.RDoubleFactory.getScalar(Math.max(((RInt) scalar0).getInt(0), ((RInt) scalar1).getInt(0)));
            }
        }
    }

    // args has length at least 2
    public static RAny max(RAny[] args, RContext context, ASTNode ast) {
        int size = args.length;
        RAny res = max(args[0], context, ast);
        for (int i = 1; i < size; i++) {
            res = max(res, max(args[i], context, ast));
        }
        return res;
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, Frame frame) {
                        return RDouble.BOXED_NEG_INF;
                    }

                };
            }
            if (exprs.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                        return max(arg, context, ast);
                    }

                };
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                    return max(params, context, ast);
                }
            };
        }
    };
}
