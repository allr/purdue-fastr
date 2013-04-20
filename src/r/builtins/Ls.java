package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

final class Ls extends CallFactory {
    static final CallFactory _ = new Ls("ls", new String[]{"name", "pos", "envir", "all.names", "pattern"}, new String[]{});

    private Ls(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("all.names") || ia.provided("pattern")) { throw Utils.nyi(); }
        final int posName = ia.position("name");
        final int posEnvir = ia.position("envir");
        final int posPos = ia.position("pos");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny nameArg = posName != -1 ? args[posName] : null;
                REnvironment envir;
                if (nameArg != null) {
                    envir = EnvBase.asEnvironment(frame, ast, nameArg, true);
                } else {
                    RAny envirArg = posEnvir != -1 ? args[posEnvir] : null;
                    RAny posArg = posPos != -1 ? args[posPos] : null;
                    envir = EnvBase.extractEnvironment(envirArg, posArg, frame, ast);
                }
                String[] varNames = Convert.symbols2strings(envir.ls());
                Arrays.sort(varNames);
                return RString.RStringFactory.getFor(varNames);
            }
        };
    }
}
