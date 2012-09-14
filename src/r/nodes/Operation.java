package r.nodes;

public abstract class Operation extends ASTNode {
    public static final int EQ_PRECEDENCE = 1;
    public static final int COMPARE_PRECEDENCE = EQ_PRECEDENCE + 1;
    public static final int ADD_PRECEDENCE = COMPARE_PRECEDENCE + 1;
    public static final int SUB_PRECEDENCE = ADD_PRECEDENCE;
    public static final int MULT_PRECEDENCE = SUB_PRECEDENCE + 1;
    public static final int COLON_PRECEDENCE = MULT_PRECEDENCE + 1;
    ASTNode lhs;

    public Operation(ASTNode left) {
        setLHS(left);
    }

    public ASTNode getLHS() {
        return lhs;
    }

    public void setLHS(ASTNode left) {
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
