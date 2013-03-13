package r.builtins;

import r.data.*;
import r.nodes.*;

/**
 * "as.complex"
 * 
 * <pre>
 * x -- An object. 
 * mode -- A character string giving an atomic mode or "list", or (except for vector) "any".
 * </pre>
 */
final class AsComplex extends AsBase {
    static final CallFactory _ = new AsComplex("as.complex", new String[]{"x", "..."}, new String[]{});

    private AsComplex(String name, String[] params, String[] required) {
        super(name, params, required, new AsBase.Operation() {

            @Override public RAny genericCast(ASTNode ast, RAny arg) {
                return genericAsComplex(ast, arg);
            }

            @Override public RAny getEmpty() {
                return RDouble.EMPTY;
            }
        });
    }
}
