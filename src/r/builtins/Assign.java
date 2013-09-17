package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "assign"
 * 
 * <pre>
 * x -- a variable name, given as a character string. No coercion is done, and the first element of a character vector 
 *      of length greater than one will be used, with a warning.
 * value -- a value to be assigned to x.
 * pos -- where to do the assignment. By default, assigns into the current environment.
 * envir -- the environment to use. 
 * inherits -- should the enclosing frames of the environment be inspected?
 * immediate -- an ignored compatibility feature.
 * </pre>
 */
final class Assign extends CallFactory {
    static final CallFactory _ = new Assign("assign", new String[]{"x", "value", "pos", "envir", "inherits", "immediate"}, new String[]{"x", "value"});

    private Assign(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    private static final boolean DEFAULT_INHERITS = false;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posX = ia.position("x");
        final int posValue = ia.position("value");
        final int posEnvir = ia.position("envir");
        final int posInherits = ia.position("inherits");
        final int posPos = ia.position("pos");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RSymbol nm = EnvBase.parseX(args[posX], ast);
                RAny value = args[posValue];
                RAny envirArg = posEnvir != -1 ? args[posEnvir] : null;
                RAny posArg = posPos != -1 ? args[posPos] : null;
                REnvironment envir = EnvBase.extractEnvironment(envirArg, posArg, frame, ast);
                boolean inherits = posInherits != -1 ? EnvBase.parseInherits(args[posInherits], ast) : DEFAULT_INHERITS;
                envir.assign(nm, value, inherits, ast);
                return value;
            }
        };
    }

}
