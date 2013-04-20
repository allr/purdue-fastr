package r.builtins;

import r.data.*;
import r.nodes.*;

final class AsCharacter extends AsBase {
    static final CallFactory _ = new AsCharacter("as.character", new String[]{"x", "..."}, new String[]{});

    private AsCharacter(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override RAny genericCast(ASTNode ast, RAny arg) {
        return genericAsString(ast, arg);
    }

    @Override RAny getEmpty() {
        return RString.EMPTY;
    }
}
