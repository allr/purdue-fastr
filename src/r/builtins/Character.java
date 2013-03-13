package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "character"
 * 
 * <pre>
 * length -- desired length.
 * </pre>
 */
final class Character extends ArrayConstructorBase {
    static final CallFactory _ = new Character("character", new String[]{"length"}, new String[]{});

    private Character(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (args.length == 0) { return RString.EMPTY; }
                int len = arrayLength(args[0], ast);
                String[] content = new String[len];
                for (int i = 0; i < len; i++) {
                    content[i] = "";
                }
                return RString.RStringFactory.getFor(content);
            }
        };
    }

}
