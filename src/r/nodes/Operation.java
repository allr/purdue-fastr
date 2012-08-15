package r.nodes;

public abstract class Operation extends Node {
    public static final int ADD_PRECEDENCE = 1;
    public static final int MULT_PRECEDENCE = ADD_PRECEDENCE + 1;
    Node lhs;

    public Operation(Node left) {
        setLHS(left);
    }

    public Node getLHS() {
        return lhs;
    }

    public void setLHS(Node left) {
        this.lhs = updateParent(left);
    }

    @Override
    public void visit_all(Visitor v) {
        getLHS().accept(v);
    }

    public String getPrettyOperator() {
        Class<?> clazz = getClass();
        PrettyName op = clazz.getAnnotation(PrettyName.class);
        return op == null ? clazz.getSimpleName() : op.value();
    }
}
