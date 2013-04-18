package r.builtins;

import r.Truffle.*;

import r.*;
import r.builtins.internal.*;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class Rbinom extends CallFactory {

    static final CallFactory _ = new Rbinom("rbinom", new String[]{"n", "size", "prob"}, null);

    private Rbinom(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultScale = new double[]{1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int nPosition = ia.position("n");
        final int sizePosition = ia.position("size");
        final int probPosition = ia.position("prob");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                int n = Random.parseNArgument(args[nPosition], ast);
                double[] size = Random.parseNumericArgument(args[sizePosition], ast);
                double[] prob = Random.parseNumericArgument(args[probPosition], ast);

                if (size.length == 0 || prob.length == 0) { return Random.allNAs(n, ast); }
                int[] rngKind = Random.updateNativeSeed(ast);
                try {
                    return RDouble.RDoubleFactory.getFor(rbinom(n, size, prob, ast));
                } finally {
                    Random.updateWorkspaceSeed(rngKind);
                }
            }
        };
    }

    public static double[] rbinom(int n, double[] size, double[] prob, ASTNode ast) {
        double[] res = new double[n];
        boolean naProduced = GNUR.rbinom(res, n, size, size.length, prob, prob.length);
        if (naProduced) {
            RContext.warning(ast, RError.NA_PRODUCED); // FIXME: can this happen for std normal and R generators?
        }
        return res;
    }
}
