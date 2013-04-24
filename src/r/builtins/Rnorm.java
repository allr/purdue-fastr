package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Rnorm extends CallFactory {

    static final CallFactory _ = new Rnorm("rnorm", new String[]{"n", "mean", "sd"}, new String[]{"n"});

    private Rnorm(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultMean = new double[]{0};
    static final double[] defaultSD = new double[]{1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);

        final int nPosition = ia.position("n");
        final int meanPosition = ia.position("mean");
        final int sdPosition = ia.position("sd");

        throw Utils.nyi("rnorm to be implemented");
    }

}
