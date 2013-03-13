package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "emptyenv".
 */
final class Emptyenv extends CallFactory {
    static final CallFactory _ = new Emptyenv("emptyenv", new String[]{}, null);

    private Emptyenv(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.BuiltIn0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return REnvironment.EMPTY;
            }
        };
    }

}
