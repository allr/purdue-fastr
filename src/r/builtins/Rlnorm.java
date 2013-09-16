package r.builtins;

import r.*;
import r.builtins.internal.Random;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

public class Rlnorm extends CallFactory {

    static final CallFactory _ = new Rlnorm("rlnorm", new String[]{"n", "meanlog", "sdlog"}, new String[]{"n"});

    private Rlnorm(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultMeanlog = new double[] {0};
    static final double[] defaultSdlog = new double[] {1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny narg) {
                    int n = Random.parseNArgument(narg, ast);
                    int [] rngKind = Random.updateNativeSeed(ast);
                    try {
                        return RDouble.RDoubleFactory.getFor(rlnormStd(n, ast));
                    } finally {
                        Random.updateWorkspaceSeed(rngKind);
                    }
                }
            };
        }

        final int nPosition = ia.position("n");
        final int meanlogPosition = ia.position("meanlog");
        final int sdlogPosition = ia.position("sdlog");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                int n = Random.parseNArgument(args[nPosition], ast);
                double[] meanlog = meanlogPosition == -1 ? defaultMeanlog : Random.parseNumericArgument(args[meanlogPosition], ast);
                double[] sdlog = sdlogPosition == -1 ? defaultSdlog : Random.parseNumericArgument(args[sdlogPosition], ast);

                if (meanlog.length == 0 || sdlog.length == 0) {
                    return Random.allNAs(n, ast);
                }
                int [] rngKind = Random.updateNativeSeed(ast);
                try {
                    return RDouble.RDoubleFactory.getFor(rlnorm(n, meanlog, sdlog, ast));
                } finally {
                    Random.updateWorkspaceSeed(rngKind);
                }
            }
        };
    }


    public static double[] rlnormStd(int n, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rlnormStd(res,  n);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

    public static double[] rlnorm(int n, double[] meanlog, double[] sdlog, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rlnorm(res, n, meanlog, meanlog.length, sdlog, sdlog.length);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

}
