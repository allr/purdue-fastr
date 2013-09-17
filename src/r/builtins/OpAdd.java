package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

/**
 * "+"
 * 
 * <pre>
 * x, y -- numeric or complex vectors or objects which can be coerced to such, or other objects for which methods have been 
 *         written.
 * </pre>
 */
final class OpAdd extends OperationsBase {
    static final CallFactory _ = new OpAdd("+", 1);

    private OpAdd(String name, int minarg) {
        super(name, minarg);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 1) { return idempotentNumeric(ast, names, exprs); } // FIXME: should implement unary plus, anyway       
        // exprs.length == 2
        return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.ADD);
    }
}
