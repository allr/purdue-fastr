package r.nodes;

public abstract class Operation extends ASTNode {
    public static final int EQ_PRECEDENCE = 1;
    public static final int NOT_PRECEDENCE = EQ_PRECEDENCE + 1;
    public static final int COMPARE_PRECEDENCE = NOT_PRECEDENCE + 1;

    public static final int ADD_PRECEDENCE = COMPARE_PRECEDENCE + 1;
    public static final int SUB_PRECEDENCE = ADD_PRECEDENCE;

    public static final int MULT_PRECEDENCE = SUB_PRECEDENCE + 1;

    public static final int MAT_MULT_PRECEDENCE = MULT_PRECEDENCE + 1;
    public static final int OUTER_MULT_PRECEDENCE = MAT_MULT_PRECEDENCE;

    public static final int COLON_PRECEDENCE = MAT_MULT_PRECEDENCE + 1;
    public static final int SIGN_PRECEDENCE = COLON_PRECEDENCE + 1;
    public static final int POW_PRECEDENCE = SIGN_PRECEDENCE + 1;
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
