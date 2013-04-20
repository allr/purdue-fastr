package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpEq extends OperationsBase {
    static final CallFactory _ = new OpEq("==");

    private OpEq(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return new Comparison(ast, exprs[0], exprs[1], Comparison.getEQ());
    }
}
