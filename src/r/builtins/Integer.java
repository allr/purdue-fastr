package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "integer"
 * 
 * <pre>
 * length -- desired length.
 * </pre>
 */
final class Integer extends ArrayConstructorBase {
    static final CallFactory _ = new Integer("integer", new String[]{"length"}, new String[]{});

    private Integer(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (args.length == 0) { return RInt.EMPTY; }
                int len = arrayLength(args[0], ast);
                return RInt.RIntFactory.getUninitializedArray(len);
            }
        };
    }
}
