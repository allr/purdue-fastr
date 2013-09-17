package r.builtins;

import r.data.*;
import r.nodes.ast.*;

/**
 * "toupper"
 * 
 * <pre>
 * x -- a character vector, or an object that can be coerced to character by as.character.
 * </pre>
 */
final class Toupper extends CharBase {

    private Toupper(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final CallFactory _ = new Toupper("toupper", new String[]{"x"}, null);

    @Override public String op(ASTNode ast, String string) {
        return string != RString.NA ? string.toUpperCase() : RString.NA;
    }
}
