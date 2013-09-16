package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "__inspect" Fastr specific debugging.
 */
final class Inspect extends CallFactory {
    static final CallFactory _ = new Inspect("__inspect", new String[]{"arg"}, null);

    private Inspect(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                System.out.println("INSPECT: " + arg + " type=" + arg.typeOf() + " isShared=" + arg.isShared() + " isTemporary=" + arg.isTemporary());
                return RNull.getNull();
            }
        };
    }
}
