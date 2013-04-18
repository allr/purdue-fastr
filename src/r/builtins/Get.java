package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "get"
 * 
 * <pre>
 * x -- a variable name (given as a character string).
 * pos -- where to look for the object; if omitted, the function will search as if the name of the object appeared 
 *         unquoted in an expression.
 * envir -- an alternative way to specify an environment to look in
 * mode -- the mode or type of object sought
 * inherits -- should the enclosing frames of the environment be searched?
 * ifnotfound -- A list of values to be used if the item is not found: it will be coerced to list if necessary.
 * </pre>
 */
final class Get extends CallFactory {
    static final CallFactory _ = new Get("get", new String[]{"x", "pos", "envir", "mode", "inherits"}, new String[]{"x"});

    private Get(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    private static final boolean DEFAULT_INHERITS = true;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("mode")) { throw Utils.nyi(); }
        final int posEnvir = ia.position("envir");
        final int posX = ia.position("x");
        final int posPos = ia.position("pos");
        final int posInherits = ia.position("inherits");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RSymbol nm = EnvBase.parseXSilent(args[posX], ast);
                RAny envirArg = posEnvir != -1 ? args[posEnvir] : null;
                RAny posArg = posPos != -1 ? args[posPos] : null;
                REnvironment envir = EnvBase.extractEnvironment(envirArg, posArg, frame, ast);
                boolean inherits = posInherits != -1 ? EnvBase.parseInherits(args[posInherits], ast) : DEFAULT_INHERITS;
                RAny res = envir.get(nm, inherits);
                if (!inherits || res != null) { // FIXME: fix this for get on toplevel with inherits == false
                    return res;
                } else {
                    return ReadVariable.readNonVariable(ast, nm);
                }
            }
        };
    }
}
