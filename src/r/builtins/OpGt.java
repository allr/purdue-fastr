package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.Not;

/**
 * ">"
 * 
 * <pre>
 * x, y -- numeric or complex vectors or objects which can be coerced to such, or other objects for which methods have been 
 *         written.
 * </pre>
 */
final class OpGt extends OperationsBase {
    static final CallFactory _ = new OpGt(">");

    private OpGt(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return new Comparison(ast, exprs[0], exprs[1], Comparison.getGT());
    }
}
