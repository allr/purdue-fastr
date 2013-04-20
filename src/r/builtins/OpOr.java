package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpOr extends OperationsBase {
    static final CallFactory _ = new OpOr("||");

    private OpOr(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return new LogicalOperation.Or(ast, exprs[0], exprs[1]);
    }
}
