package r.builtins;

import r.*;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "list"
 *
 * <pre>
 * ...  -- objects, possibly named.
 * </pre>
 */
final class List extends CallFactory {

    static final CallFactory _ = new List("list", new String[]{"..."}, new String[]{});

    private List(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        RArray.Names listNames = null;
        if (names != null) {
            boolean hasNonNull = false;
            for (RSymbol s : names) {
                if (s != null) {
                    hasNonNull = true;
                    break;
                }
            }
            if (hasNonNull) {
                RSymbol[] symbols = new RSymbol[names.length];
                for (int i = 0; i < symbols.length; i++) {
                    if (names[i] != null) {
                        symbols[i] = names[i];
                    } else {
                        symbols[i] = RSymbol.EMPTY_SYMBOL;
                    }
                }
                listNames = RArray.Names.create(symbols);
            }
        }
        final RArray.Names fListNames = listNames;
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                Utils.ref(params);
                return RList.RListFactory.getFor(params, null, fListNames); // shallow copy (in fact no copy)
            }
        };
    }
}
