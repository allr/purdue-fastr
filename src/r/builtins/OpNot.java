package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.Not;

final class OpNot extends OperationsBase {
    static final CallFactory _ = new OpNot("!");

    private OpNot(String name) {
        super(name, -1); // one argument
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 1
        return new Not.LogicalScalar(ast, exprs[0]);
    }

}
