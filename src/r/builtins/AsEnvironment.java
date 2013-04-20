package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

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
