package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpMult extends OperationsBase {
    static final CallFactory _ = new OpMult("*");

    private OpMult(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.MULT);
    }
}
