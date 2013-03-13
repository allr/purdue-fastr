package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

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
        if (exprs.length == 1) { return Ln._.create(call, names, exprs); }
        RNode baseExpr = exprs[ia.position("base")];
        if (Builtin.isNumericConstant(baseExpr, 10)) { return Log10._.create(call, names, exprs); }
        if (Builtin.isNumericConstant(baseExpr, 2)) { return Log2._.create(call, names, exprs); }
        // TODO: implement the generic case
        throw Utils.nyi("unsupported case");
    }
}
