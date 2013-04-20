package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpOrElem extends OperationsBase {
    static final CallFactory _ = new OpOrElem("|");

    private OpOrElem(String name) {
        super(name, 1);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return ElementwiseLogicalOperation.createUninitialized(ast, exprs[0], ElementwiseLogicalOperation.OR, exprs[1]);
    }

}
