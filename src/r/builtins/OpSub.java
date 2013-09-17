package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.UnaryMinus;

/**
 * "-"
 *
 * <pre>
 * x, y -- numeric or complex vectors or objects which can be coerced to such, or other objects for which methods have been
 *         written.
 * </pre>
 */
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
