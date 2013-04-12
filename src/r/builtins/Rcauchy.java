package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.internal.*;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class Rcauchy extends CallFactory {
    static final CallFactory _ = new Rcauchy("rcauchy", new String[]{"n", "location", "scale"}, new String[]{"n"});

    private Rcauchy(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultLocation = new double[] {0};
    static final double[] defaultScale = new double[] {1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny narg) {
                    int n = Random.parseNArgument(narg, ast);
                    int [] rngKind = Random.updateNativeSeed(ast);
                    try {
                        return RDouble.RDoubleFactory.getFor(rcauchyStd(n, ast));
                    } finally {
                        Random.updateWorkspaceSeed(rngKind);
                    }
                }
            };
        }

        final int nPosition = ia.position("n");
        final int locationPosition = ia.position("location");
        final int scalePosition = ia.position("scale");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                int n = Random.parseNArgument(args[nPosition], ast);
                double[] location = locationPosition == -1 ? defaultLocation : Random.parseNumericArgument(args[locationPosition], ast);
                double[] scale = scalePosition == -1 ? defaultScale : Random.parseNumericArgument(args[scalePosition], ast);

                if (location.length == 0 || scale.length == 0) {
                    return Random.allNAs(n, ast);
                }
                int [] rngKind = Random.updateNativeSeed(ast);
                try {
                    return RDouble.RDoubleFactory.getFor(rcauchy(n, location, scale, ast));
                } finally {
                    Random.updateWorkspaceSeed(rngKind);
                }
            }
        };
    }


    public static double[] rcauchyStd(int n, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rcauchyStd(res,  n);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

    public static double[] rcauchy(int n, double[] location, double[] scale, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rcauchy(res, n, location, location.length, scale, scale.length);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }
}
