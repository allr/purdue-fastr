package r.nodes;


public class FieldAccess extends ASTNode {
    ASTNode lhs;
    ASTNode fieldName;

    public FieldAccess(ASTNode value, String fieldName) {
        lhs = updateParent(value);
        this.fieldName = Constant.createStringConstant(fieldName);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        lhs.accept(v);
    }

    public static ASTNode create(FieldOperator op, ASTNode value, String fieldName) {
        switch (op) {
            case AT: // Since I dunno what does AT, I put this case
            case FIELD: return new FieldAccess(value, fieldName);
        }
        throw new Error("No node implemented for: '" + op + "' (" + value + ": " + fieldName + ")");
    }

    public ASTNode lhs() {
        return lhs;
    }

    public ASTNode fieldName() {
        return fieldName;
    }
}
