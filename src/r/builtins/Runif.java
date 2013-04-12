package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.internal.*;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Runif extends CallFactory {

    static final CallFactory _ = new Runif("runif", new String[]{"n", "min", "max"}, new String[]{"n"});

    private Runif(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultMin = new double[] {0};
    static final double[] defaultMax = new double[] {1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny narg) {
                    int n = Random.parseNArgument(narg, ast);
                    int [] rngKind = Random.updateNativeSeed(ast);
                    try {
                        return RDouble.RDoubleFactory.getFor(runifStd(n, ast));
                    } finally {
                        Random.updateWorkspaceSeed(rngKind);

                    }
                }
            };
        }

        final int nPosition = ia.position("n");
        final int minPosition = ia.position("min");
        final int maxPosition = ia.position("max");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                int n = Random.parseNArgument(args[nPosition], ast);
                double[] min = minPosition == -1 ? defaultMin : Random.parseNumericArgument(args[minPosition], ast);
                double[] max = maxPosition == -1 ? defaultMax : Random.parseNumericArgument(args[maxPosition], ast);

                if (min.length == 0 || max.length == 0) {
                    return Random.allNAs(n, ast);
                }
                int [] rngKind = Random.updateNativeSeed(ast);
                try {
                    return RDouble.RDoubleFactory.getFor(runif(n, min, max, ast));
                } finally {
                    Random.updateWorkspaceSeed(rngKind);
                }
            }
        };
    }

    public static double[] runifStd(int n, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.runifStd(res,  n);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen?
        }
        return res;
    }

    public static double[] runif(int n, double[] min, double[] max, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.runif(res, n, min, min.length, max, max.length);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

}
