package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.data.RDouble.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;
import r.builtins.internal.Random;

final class Rnorm extends CallFactory {

    static final CallFactory _ = new Rnorm("rnorm", new String[]{"n", "mean", "sd"}, new String[]{"n"});

    private Rnorm(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultMean = new double[] {0};
    static final double[] defaultSD = new double[] {1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny narg) {
                    int n = Random.parseNArgument(narg, ast);
                    int [] rngKind = Random.updateNativeSeed(ast);
                    try {
                        return RDouble.RDoubleFactory.getFor(rnormStd(n, ast));
                    } finally {
                        Random.updateWorkspaceSeed(rngKind);
                    }
                }
            };
        }

        final int nPosition = ia.position("n");
        final int meanPosition = ia.position("mean");
        final int sdPosition = ia.position("sd");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                int n = Random.parseNArgument(args[nPosition], ast);
                double[] mean = meanPosition == -1 ? defaultMean : Random.parseNumericArgument(args[meanPosition], ast);
                double[] sd = sdPosition == -1 ? defaultSD : Random.parseNumericArgument(args[sdPosition], ast);

                if (mean.length == 0 || sd.length == 0) {
                    return Random.allNAs(n, ast);
                }
                int [] rngKind = Random.updateNativeSeed(ast);
                try {
                    return RDouble.RDoubleFactory.getFor(rnorm(n, mean, sd, ast));
                } finally {
                    Random.updateWorkspaceSeed(rngKind);
                }
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

    public static double[] rnorm(int n, double[] mean, double[] sd, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rnorm(res, n, mean, mean.length, sd, sd.length);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

}
