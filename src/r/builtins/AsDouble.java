package r.builtins;

import r.data.*;
import r.nodes.*;

final class AsDouble extends AsBase {
    static final CallFactory _ = new AsDouble("as.double", new String[]{"x", "..."}, new String[]{});

    private AsDouble(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override RAny genericCast(ASTNode ast, RAny arg) {
        return genericAsDouble(ast, arg);
    }

    @Override RAny getEmpty() {
        return RDouble.EMPTY;
    }
}
