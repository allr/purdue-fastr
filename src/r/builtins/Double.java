package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "double"
 * 
 * <pre>
 * length -- desired length.
 * </pre>
 */
final class Double extends ArrayConstructorBase {
    static final CallFactory _ = new Double("double", new String[]{"length"}, new String[]{});

    private Double(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (args.length == 0) { return RDouble.EMPTY; }
                int len = arrayLength(args[0], ast);
                return RDouble.RDoubleFactory.getUninitializedArray(len);
            }
        };
    }
}
