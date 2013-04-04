package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
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

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny narg) {
                    int n = Rnorm.parseN(narg, ast);
                    return RDouble.RDoubleFactory.getFor(runifStd(n, ast));
                }
            };
        }

        final int nPosition = ia.position("n");
        final int minPosition = ia.position("min");
        final int maxPosition = ia.position("max");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                Utils.nyi("non-standard uniform");
                return null;
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

}
