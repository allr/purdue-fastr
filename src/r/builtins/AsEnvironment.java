package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "as.environment"
 * 
 * <pre>
 * x -- an R object to convert. If it is already an environment, just return it. If it is a number, return the
 *  environment corresponding to that position on the search list. If it is a character string, match the string to the names on 
 *  the search list. If it is a list, the equivalent of list2env(x, parent=emptyenv()) is returned. If is.object(x) is true 
 *  and it has a class for which an as.environment method is found, that is used.
 * </pre>
 */
final class AsEnvironment extends CallFactory {
    static final CallFactory _ = new AsEnvironment("as.environment", new String[]{"x"}, new String[]{"x"});

    private AsEnvironment(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                return EnvBase.asEnvironment(frame, ast, arg);
            }
        };
    }

}
