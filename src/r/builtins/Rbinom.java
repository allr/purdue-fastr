package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public final class Rbinom extends CallFactory {

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
        throw Utils.nyi("rbinom to be implemented");
    }
}
