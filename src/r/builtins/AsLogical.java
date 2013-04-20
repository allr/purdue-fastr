package r.builtins;

import r.data.*;
import r.nodes.*;

final class AsLogical extends AsBase {
    static final CallFactory _ = new AsLogical("as.logical", new String[]{"x", "..."}, new String[]{});

    private AsLogical(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RAny genericCast(ASTNode ast, RAny arg) {
        return genericAsLogical(ast, arg);
    }

    @Override public RAny getEmpty() {
        return RLogical.EMPTY;
    }
}
