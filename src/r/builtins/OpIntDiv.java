package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpIntDiv extends OperationsBase {
    static final CallFactory _ = new OpIntDiv("%/%");

    private OpIntDiv(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.INTEGER_DIV);
    }
}
