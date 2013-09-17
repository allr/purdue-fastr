package r.builtins;

import r.data.*;
import r.nodes.ast.*;

/**
 * "tolower"
 * 
 * <pre>
 * x -- a character vector, or an object that can be coerced to character by as.character.
 * </pre>
 */
final class Tolower extends CharBase {

    private Tolower(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final CallFactory _ = new Tolower("tolower", new String[]{"x"}, null);

    @Override String op(ASTNode ast, String string) {
        return string != RString.NA ? string.toLowerCase() : RString.NA;
    }
}
