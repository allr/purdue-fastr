package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// TODO: support na.rm
abstract class ExtremeBase extends CallFactory {

    ExtremeBase(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    abstract int extreme(int a, int b);

    abstract double extreme(double a, double b);

    abstract String extreme(String a, String b);

    abstract RAny emptySetExtreme();

    // result is RString scalar
    public RAny extreme(RString arg, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            RContext.warning(ast, RError.NO_NONMISSING_MAX);
            return emptySetExtreme();
        }
        String res = arg.getString(0);
        for (int i = 1; i < size; i++) {
            String s = arg.getString(i);
            if (s != RString.NA) {
                res = extreme(s, res);
            } else {
                return RString.BOXED_NA;
            }
        }
        return RString.RStringFactory.getScalar(res);
    }

    // result is RDouble scalar
    public RAny extreme(RDouble arg, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            RContext.warning(ast, RError.NO_NONMISSING_MAX);
            return emptySetExtreme();
        }
        double res = arg.getDouble(0);
        for (int i = 1; i < size; i++) {
            double d = arg.getDouble(i);
            res = extreme(d, res);
            if (RDouble.RDoubleUtils.arithIsNA(d)) { return RDouble.BOXED_NA; }
        }
        return RDouble.RDoubleFactory.getScalar(res);
    }

    // result is RInt scalar (or RDouble +-infinity)
    public RAny extreme(RInt arg, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            RContext.warning(ast, RError.NO_NONMISSING_MAX);
            return emptySetExtreme();
        }
        int res = arg.getInt(0);
        for (int i = 1; i < size; i++) {
            int v = arg.getInt(i);
            res = extreme(v, res);
            if (v == RInt.NA) { return RInt.BOXED_NA; }
        }
        return RInt.RIntFactory.getScalar(res);
    }

    // result is RInt scalar (or RDouble +-infinity)
    public RAny extreme(RLogical arg, ASTNode ast) {
        int size = arg.size();
        if (size == 0) {
            RContext.warning(ast, RError.NO_NONMISSING_MAX);
            return emptySetExtreme();
        }
        int res = arg.getLogical(0);
        for (int i = 1; i < size; i++) {
            int v = arg.getLogical(i);
            if (v > res) {
                res = v;
            }
            if (v == RLogical.NA) { return RInt.BOXED_NA; }
        }
        return RInt.RIntFactory.getScalar(res);
    }

    // result is RDouble, RInt or RString scalar
    public RAny extreme(RAny arg, ASTNode ast) {
        if (arg instanceof RDouble) { return extreme((RDouble) arg, ast); }
        if (arg instanceof RInt) { return extreme((RInt) arg, ast); }
        if (arg instanceof RLogical) { return extreme((RLogical) arg, ast); }
        if (arg instanceof RString) { return extreme((RString) arg, ast); }
        throw RError.getInvalidTypeArgument(ast, arg.typeOf());
    }

    // takes RDouble, RInt, RString scalars
    // returns RDouble, RInt or RString scalar
    public RAny extreme(RAny scalar0, RAny scalar1) { // FIXME: does this preserve NA's ?
        if (scalar0 instanceof RDouble) {
            if (scalar1 instanceof RDouble) {
                return RDouble.RDoubleFactory.getScalar(extreme(((RDouble) scalar0).getDouble(0), ((RDouble) scalar1).getDouble(0)));
            } else if (scalar1 instanceof RInt) {
                return RDouble.RDoubleFactory.getScalar(extreme(((RDouble) scalar0).getDouble(0), Convert.int2double(((RInt) scalar1).getInt(0))));
            } else {
                return RString.RStringFactory.getScalar(extreme(Convert.double2string(((RDouble) scalar0).getDouble(0)), ((RString) scalar1).getString(0)));
            }
        }
        if (scalar0 instanceof RInt) {
            if (scalar1 instanceof RDouble) {
                return RDouble.RDoubleFactory.getScalar(extreme(Convert.int2double(((RInt) scalar0).getInt(0)), ((RDouble) scalar1).getDouble(0)));
            } else if (scalar1 instanceof RInt) {
                return RInt.RIntFactory.getScalar(extreme(((RInt) scalar0).getInt(0), ((RInt) scalar1).getInt(0)));
            } else {
                return RString.RStringFactory.getScalar(extreme(Convert.int2string(((RInt) scalar0).getInt(0)), ((RString) scalar1).getString(0)));
            }
        }
        // scalar0 instance of RString
        if (scalar1 instanceof RDouble) {
            return RString.RStringFactory.getScalar(extreme(((RString) scalar0).getString(0), Convert.double2string(((RDouble) scalar1).getDouble(0))));
        } else if (scalar1 instanceof RInt) {
            return RString.RStringFactory.getScalar(extreme(((RString) scalar0).getString(0), Convert.int2string(((RInt) scalar1).getInt(0))));
        } else {
            return RString.RStringFactory.getScalar(extreme(((RString) scalar0).getString(0), ((RString) scalar1).getString(0)));
        }

    }

    // args has length at least 2
    public RAny extreme(RAny[] args, ASTNode ast) {
        int size = args.length;
        RAny res = extreme(args[0], ast);
        for (int i = 1; i < size; i++) {
            res = extreme(res, extreme(args[i], ast));
        }
        return res;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        if (exprs.length == 0) { return new Builtin.Builtin0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                RContext.warning(ast, RError.NO_NONMISSING_MAX);
                return emptySetExtreme();
            }
        }; }
        if (exprs.length == 1) { return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                return extreme(arg, ast);
            }

        }; }
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return extreme(args, ast);
            }
        };
    }
}
