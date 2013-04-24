package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class Rlnorm extends CallFactory {

    static final CallFactory _ = new Rlnorm("rlnorm", new String[]{"n", "meanlog", "sdlog"}, new String[]{"n"});

    private Rlnorm(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final double[] defaultMeanlog = new double[]{0};
    static final double[] defaultSdlog = new double[]{1};

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int nPosition = ia.position("n");
        final int meanlogPosition = ia.position("meanlog");
        final int sdlogPosition = ia.position("sdlog");
        throw Utils.nyi("rlnorm to be implemented");
    }
}
