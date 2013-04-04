package r.builtins;

import java.util.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.RDouble.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Rnorm extends CallFactory {

    static final CallFactory _ = new Rnorm("rnorm", new String[]{"n", "mean", "sd"}, new String[]{"n"});

    private Rnorm(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static int parseN(RAny narg, ASTNode ast) {
        if (!(narg instanceof RArray)) {
            throw RError.getInvalidUnnamedArguments(ast);
        }
        RArray a = (RArray) narg;
        int size = a.size();
        if (size == 1) {
            int i = a.asInt().getInt(0);
            if (i < 0) { // includes i == RInt.NA
                throw RError.getInvalidUnnamedArguments(ast);
            }
            return i;
        }
        return size;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny narg) {
                    int n = parseN(narg, ast);
                    return RDouble.RDoubleFactory.getFor(rnormStd(n, ast));
                }
            };
        }

        final int nPosition = ia.position("n");
        final int meanPosition = ia.position("mean");
        final int sdPosition = ia.position("sd");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                Utils.nyi("non-standard normal");
                return null;
            }
        };
    }

    // this is close to GNU-R, but runs much slower because of JNI
    public static double[] rnormNaive(int n, double mu, double sigma, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = false;
        for (int i = 0; i < n; i++) {
            double d = GNUR.rnorm(mu, sigma);
            res[i] = d;
            naProduced = naProduced || RDouble.RDoubleUtils.isNAorNaN(d);
        }
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);
        }
        return res;
    }

    public static double[] rnormBatch(int n, double mu, double sigma, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rnorm(res,  n, mu, sigma);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);
        }
        return res;
    }

    // this is slightly faster than rnormBatch, but so far the difference seems quite small
    public static double[] rnormBatch2(int n, double mu, double sigma, ASTNode ast) {
        double[] res = new double[n];
        if (RDoubleUtils.isNAorNaN(mu) || !RDoubleUtils.isFinite(sigma) || sigma < 0) {
            Arrays.fill(res,  RDouble.NaN);
            RContext.warning(ast, RError.NA_PRODUCED);
            return res;
        }
        if (sigma == 0 || !RDoubleUtils.isFinite(mu)) {
            Arrays.fill(res,  mu); // includes mu = +/- Inf with finite sigma
            return res;
        }

        boolean naProduced = GNUR.rnormNonChecking(res,  n, mu, sigma);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);
        }
        return res;
    }

    // faster than rnormBatch2 for standard normal
    public static double[] rnormStd(int n, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rnormStd(res,  n);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

}
