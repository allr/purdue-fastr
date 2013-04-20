package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

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
        if (Builtin.isNumericConstant(baseExpr, 10)) { return Log10._.create(call, new RSymbol[]{names[ia.position("x")]}, new RNode[]{exprs[ia.position("x")]}); }
        if (Builtin.isNumericConstant(baseExpr, 2)) { return Log2._.create(call, new RSymbol[]{names[ia.position("x")]}, new RNode[]{exprs[ia.position("x")]}); }
        // TODO: implement the generic case
        throw Utils.nyi("unsupported case");
    }
}
