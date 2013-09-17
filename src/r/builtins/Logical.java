package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "logical" Integer vectors exist so that data can be passed to C or Fortran code which expects them, and so that
 * (small) integer data can be represented exactly and compactly.
 * 
 * <pre>
 * length -- desired length.
 * </pre>
 */
final class Logical extends ArrayConstructorBase {
    static final CallFactory _ = new Logical("logical", new String[]{"length"}, new String[]{});

    private Logical(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (args.length == 0) { return RLogical.EMPTY; }
                int len = arrayLength(args[0], ast);
                return RLogical.RLogicalFactory.getUninitializedArray(len);
            }
        };
    }
}
