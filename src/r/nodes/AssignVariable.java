package r.nodes;

import r.*;
import r.data.*;

public abstract class AssignVariable extends ASTNode {

    ASTNode rhs;

    AssignVariable(ASTNode expr) {
        rhs = updateParent(expr);
    }

    @Override
    public void visit_all(Visitor v) {
        getExpr().accept(v);
    }

    public ASTNode getExpr() {
        return rhs;
    }

    public static ASTNode create(ASTNode lhs, ASTNode rhs) {
        if (lhs instanceof SimpleAccessVariable) {
            return writeVariable(((SimpleAccessVariable) lhs).name, rhs);
        }
        Utils.nyi();
        return null;
    }

    public static ASTNode createSuper(ASTNode lhs, ASTNode rhs) {
        Utils.nyi();
        return null;
    }

    public static ASTNode writeVariable(RSymbol name, ASTNode rhs) {
        return new SimpleAssignVariable(name, rhs);
    }
}
