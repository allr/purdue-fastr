package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Rgamma extends CallFactory {

    static final CallFactory _ = new Rgamma("rgamma", new String[]{"n", "shape", "rate", "scale"}, new String[]{"n", "shape"});

    private Rgamma(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultScale = new double[]{1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int nPosition = ia.position("n");
        final int shapePosition = ia.position("shape");
        final int ratePosition = ia.position("rate");
        final int scalePosition = ia.position("scale");
        throw Utils.nyi("rgamma to be implemented");
    }
}
