package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "raw"
 * 
 * <pre>
 * length -- desired length.
 * </pre>
 */
final class Raw extends ArrayConstructorBase {
    static final CallFactory _ = new Raw("raw", new String[]{"length"}, new String[]{});

    private Raw(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (args.length == 0) { return RRaw.EMPTY; }
                int len = arrayLength(args[0], ast);
                return RRaw.RRawFactory.getUninitializedArray(len);
            }
        };
    }

    // TODO: complex constructor (it is more elaborate than constructors for the other types)

}
