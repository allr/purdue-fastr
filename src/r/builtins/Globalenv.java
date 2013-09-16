package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

final class Globalenv extends CallFactory {
    static final CallFactory _ = new Globalenv("globalenv", new String[]{}, null);

    private Globalenv(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return REnvironment.GLOBAL;
            }
        };
    }

}
