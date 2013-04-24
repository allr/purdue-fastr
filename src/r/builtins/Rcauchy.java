package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class Rcauchy extends CallFactory {
    static final CallFactory _ = new Rcauchy("rcauchy", new String[]{"n", "location", "scale"}, new String[]{"n"});

    private Rcauchy(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultLocation = new double[]{0};
    static final double[] defaultScale = new double[]{1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int nPosition = ia.position("n");
        final int locationPosition = ia.position("location");
        final int scalePosition = ia.position("scale");

        throw Utils.nyi("rcauchy to be implementd");
    }
}
