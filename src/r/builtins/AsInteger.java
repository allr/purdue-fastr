package r.builtins;

import r.data.*;
import r.nodes.*;

/**
 * "as.integer"
 * 
 * <pre>
 * x -- An object.
 * </pre>
 */
final class AsInteger extends AsBase {
    static final CallFactory _ = new AsInteger("as.integer", new String[]{"x", "..."}, new String[]{});

    private AsInteger(String name, String[] params, String[] required) {
        super(name, params, required, new AsBase.Operation() {

            @Override public RAny genericCast(ASTNode ast, RAny arg) {
                return genericAsInt(ast, arg);
            }

            @Override public RAny getEmpty() {
                return RInt.EMPTY;
            }
        });
    }
}
