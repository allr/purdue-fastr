package r.builtins;

import r.data.*;
import r.nodes.*;

final class AsComplex extends AsBase {
    static final CallFactory _ = new AsComplex("as.complex", new String[]{"x", "..."}, new String[]{});

    private AsComplex(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override RAny genericCast(ASTNode ast, RAny arg) {
        return genericAsComplex(ast, arg);
    }

    @Override RAny getEmpty() {
        return RDouble.EMPTY;
    }
}
