package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.Not;

/**
 * "!"
 *
 * <pre>
 * x, y -- numeric or complex vectors or objects which can be coerced to such, or other objects for which methods have been
 *         written.
 * </pre>
 */
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
