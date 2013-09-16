package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "as.vector"
 *
 * <pre>
 * x -- An object.
 * mode -- A character string giving an atomic mode or "list", or (except for vector) "any".
 * </pre>
 */
final class AsVector extends AsBase {
    static final CallFactory _ = new AsVector("as.vector", new String[]{"x", "mode"}, new String[]{"x"});

    private AsVector(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override RAny genericCast(ASTNode ast, RAny arg) {
        throw Utils.nyi("Never called.");
    }

    @Override RAny getEmpty() {
        throw Utils.nyi("Never called.");
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final boolean xfirst = ia.position("x") == 0;
        if (exprs.length == 1) { return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RList) { return arg; } // is it a bug of GNU-R that list attributes are not stripped?
                return arg.stripAttributes();
            }
        }; }
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                return genericAsVector(ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
            }
        };
    }
}
