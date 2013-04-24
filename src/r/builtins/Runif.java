package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Runif extends CallFactory {

    static final CallFactory _ = new Runif("runif", new String[]{"n", "min", "max"}, new String[]{"n"});

    private Runif(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultMin = new double[]{0};
    static final double[] defaultMax = new double[]{1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int nPosition = ia.position("n");
        final int minPosition = ia.position("min");
        final int maxPosition = ia.position("max");
        throw Utils.nyi("runif to be implemneted");
    }
}
