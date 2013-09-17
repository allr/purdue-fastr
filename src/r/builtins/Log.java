package r.builtins;

import r.*;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

/**
 * "log"
 *
 * <pre>
 * x -- a numeric or complex vector.
 * base -- a positive or complex number: the base with respect to which logarithms are computed. Defaults to e=exp(1).
 * </pre>
 */
// TODO: complex numbers
final class Log extends CallFactory {

    static final CallFactory _ = new Log("log", new String[]{"x", "base"}, new String[]{"x"});

    private Log(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        int xPosition = ia.position("x");
        int basePosition = ia.position("base");

        if (basePosition == -1) {
            return Ln._.create(call, new RSymbol[]{names[xPosition]}, new RNode[]{exprs[xPosition]});
            // NOTE: cannot just pass names, exprs - a null argument may have been passed
        }

        RNode baseExpr = exprs[basePosition];
        if (Builtin.isNumericConstant(baseExpr, 10)) {
            return Log10._.create(call, new RSymbol[]{names[xPosition]}, new RNode[]{exprs[xPosition]});
        }
        if (Builtin.isNumericConstant(baseExpr, 2)) {
            return Log2._.create(call, new RSymbol[]{names[xPosition]}, new RNode[]{exprs[xPosition]});
        }
        // TODO: implement the generic case
        throw Utils.nyi("unsupported case");
    }
}
