package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.UnaryMinus;

final class OpSub extends OperationsBase {
    static final CallFactory _ = new OpSub("-");

    private OpSub(String name) {
        super(name, 1);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 1) { return new UnaryMinus.NumericScalar(ast, exprs[0]); }
        // exprs.length == 2
        return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.SUB);
    }
}
