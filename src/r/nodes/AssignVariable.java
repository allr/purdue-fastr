package r.nodes;

import r.*;

public abstract class AssignVariable extends Node {
    Node rhs;
    AssignVariable(Node expr) {
        rhs = updateParent(expr);
    }

    @Override
    public void visit_all(Visitor v) {
        getExpr().accept(v);
    }

    public Node getExpr() {
        return rhs;
    }

    public static Node create(Node lhs, Node rhs) {
        if (lhs instanceof VariableAccess) {
            return writeVariable(((VariableAccess) lhs).name, rhs);
        }
        Utils.nyi();
        return null;
    }

    public static Node createSuper(Node lhs, Node rhs) {
        Utils.nyi();
        return null;
    }

    public static Node writeVariable(Symbol name, Node rhs) {
        return new SimpleAssignVariable(name, rhs);
    }
}
