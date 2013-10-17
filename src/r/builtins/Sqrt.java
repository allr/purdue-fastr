package r.builtins;

import r.*;
import r.data.*;
import r.data.RDouble.*;
import r.errors.*;
import r.ext.*;
import r.nodes.ast.*;

/**
 * "sqrt"
 *
 * <pre>
 * x -- a numeric or complex vector or array.
 * </pre>
 */
// FIXME: scalar optimizations
// FIXME: NaNs produce warning should be issued only once for a vector (and should fix this in MathBase)

final class Sqrt extends MathBase {

    static final CallFactory _ = new Sqrt("sqrt");

    private Sqrt(String name) {
        super(name);
    }

    @Override
    double op(ASTNode ast, double value) {
        return sqrt(value, ast);
    }

    @Override
    void op(ASTNode ast, double[] x, double[] res) {
        sqrt(x, res, ast);
    }

    public static double sqrt(double d, ASTNode ast) {
        if (RDouble.RDoubleUtils.isNAorNaN(d)) {
            return RDouble.NA;
        } else {
            double res = Math.sqrt(d);
            if (RDouble.RDoubleUtils.isNAorNaN(res)) {
                RContext.warning(ast, RError.NAN_PRODUCED);
            }
            return res;
        }
    }

    // FIXME: could also optimize for the case that the operation does produce NaNs (although that is not so common)
    public static void sqrt(double[] x, double[] res, ASTNode ast) {
        int size = x.length;
        if (!RDoubleUtils.ARITH_NA_CHECKS && RContext.hasMKL() && MKL.use(size)) {
            MKL.vdSqrt(size, x, res);
            return;
        }
        for (int i = 0; i < x.length; i++) {
            res[i] = sqrt(x[i], ast);
        }
    }
}
