package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpOuterMult extends OperationsBase {
    static final CallFactory _ = new OpOuterMult("%o%");

    private OpOuterMult(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return new MatrixOperation.OuterProduct(ast, exprs[0], exprs[1]);
    }
}
