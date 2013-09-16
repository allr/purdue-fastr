package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "exists"
 * 
 * <pre>
 * x -- a variable name (given as a character string).
 * where -- where to look for the object (see the details section); if omitted, the function will search as if the name of 
 *         the object appeared unquoted in an expression.
 * envir -- an alternative way to specify an environment to look in, but it is usually simpler to just use the where argument.
 * frame -- a frame in the calling list. Equivalent to giving where as sys.frame(frame).
 * mode -- the mode or type of object sought
 * inherits -- should the enclosing frames of the environment be searched?
 * </pre>
 */
final class Exists extends CallFactory {
    static final CallFactory _ = new Exists("exists", new String[]{"x", "where", "envir", "frame", "mode", "inherits"}, new String[]{"x"});

    private Exists(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    private static final boolean DEFAULT_INHERITS = true;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("frame")) { throw Utils.nyi("frame"); }
        if (ia.provided("mode")) { throw Utils.nyi("mode"); }
        final int posX = ia.position("x");
        final int posEnvir = ia.position("envir");
        final int posWhere = ia.position("where");
        final int posInherits = ia.position("inherits");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RSymbol nm = EnvBase.parseXSilent(args[posX], ast);
                // FIXME: add support for frame argument
                RAny envirArg = posEnvir != -1 ? args[posEnvir] : null;
                RAny posArg = posWhere != -1 ? args[posWhere] : null;
                REnvironment envir = EnvBase.extractEnvironment(envirArg, posArg, frame, ast);
                boolean inherits = posInherits != -1 ? EnvBase.parseInherits(args[posInherits], ast) : DEFAULT_INHERITS;
                boolean res = envir.exists(nm, inherits);
                if (res) { return RLogical.BOXED_TRUE; }
                // TODO: handle top-level
                return RLogical.BOXED_FALSE;
            }
        };
    }
}
