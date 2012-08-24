package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.nodes.*;

public class AssignVariable extends BaseRNode {

    final RSymbol symbol;
    @Stable
    int position;
    RNode expr;

    public AssignVariable(ASTNode orig, RSymbol symbol, RNode expr) {
        super(orig);
        this.symbol = symbol;
        setExpr(expr);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        Utils.nyi();
        return null;
    }

    public void setExpr(RNode expr) {
        this.expr = updateParent(expr);
    }

    public RNode getExpr() {
        return expr;
    }

    private static class AssignInLocals extends AssignVariable {

        public AssignInLocals(ASTNode orig, RSymbol symbol, RNode expr, int pos) {
            super(orig, symbol, expr);
            position = pos;
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            Utils.nyi();
            return null;
        }
    }

    private static class AssignInExtension extends AssignVariable {

        public AssignInExtension(ASTNode orig, RSymbol symbol, RNode expr) {
            super(orig, symbol, expr);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            Utils.nyi();
            return null;
        }

    }
}
