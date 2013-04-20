package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

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
