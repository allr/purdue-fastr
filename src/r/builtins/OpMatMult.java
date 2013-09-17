package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

/**
 * "%*%"
 * 
 * <pre>
 * x, y -- numeric or complex vectors or objects which can be coerced to such, or other objects for which methods have been 
 *         written.
 * </pre>
 */
final class OpMatMult extends OperationsBase {
    static final CallFactory _ = new OpMatMult("%*%");

    private OpMatMult(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return new MatrixOperation.MatrixProduct(ast, exprs[0], exprs[1]);
    }
}
