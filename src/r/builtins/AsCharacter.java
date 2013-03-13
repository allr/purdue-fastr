package r.builtins;

import r.data.*;
import r.nodes.*;

/**
 * "as.character"
 * 
 * <pre>
 * x -- An object. 
 * mode -- A character string giving an atomic mode or "list", or (except for vector) "any".
 * </pre>
 */
final class AsCharacter extends AsBase {
    static final CallFactory _ = new AsCharacter("as.character", new String[]{"x", "..."}, new String[]{});

    private AsCharacter(String name, String[] params, String[] required) {
        super(name, params, required, new AsBase.Operation() {

            @Override public RAny genericCast(ASTNode ast, RAny arg) {
                return genericAsString(ast, arg);
            }

            @Override public RAny getEmpty() {
                return RString.EMPTY;
            }
        });
    }
}
