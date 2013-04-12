package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.CallFactory.*;
import r.builtins.internal.*;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Rgamma extends CallFactory {

    static final CallFactory _ = new Rgamma("rgamma", new String[]{"n", "shape", "rate", "scale"}, new String[]{"n", "shape"});

    private Rgamma(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultScale = new double[] {1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int nPosition = ia.position("n");
        final int shapePosition = ia.position("shape");
        final int ratePosition = ia.position("rate");
        final int scalePosition = ia.position("scale");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                int n = Random.parseNArgument(args[nPosition], ast);
                double[] shape = Random.parseNumericArgument(args[shapePosition], ast);
                double[] scale;
                if (scalePosition != -1) {
                    scale = Random.parseNumericArgument(args[scalePosition], ast);
                } else {
                    if (ratePosition != -1) {
                        double[] rate = Random.parseNumericArgument(args[ratePosition], ast);
                        for (int i = 0; i < rate.length; i++) {
                            rate[i] = 1/rate[i];
                        }
                        scale = rate;
                    } else {
                        scale = defaultScale;
                    }
                }

                if (shape.length == 0 || scale.length == 0) {
                    return Random.allNAs(n, ast);
                }
                int [] rngKind = Random.updateNativeSeed(ast);
                try {
                    return RDouble.RDoubleFactory.getFor(rgamma(n, shape, scale, ast));
                } finally {
                    Random.updateWorkspaceSeed(rngKind);
                }
            }
        };
    }

    public static double[] rgamma(int n, double[] shape, double[] scale, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rgamma(res, n, shape, shape.length, scale, scale.length);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED);  // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }

}
