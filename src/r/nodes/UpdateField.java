package r.nodes;

/**
 * * UpdateField AST, for $ operator at top level LHS of assignment.
 */
public class UpdateField extends ASTNode {

    FieldAccess vector;
    ASTNode rhs;
    final boolean isSuper;

    public UpdateField(final boolean isSuper, final FieldAccess vector, final ASTNode rhs) {
        this.vector = updateParent(vector);
        this.rhs = updateParent(rhs);
        this.isSuper = isSuper;
    }

    public FieldAccess getVector() {
        return vector;
    }

    public ASTNode getRHS() {
        return rhs;
    }

    public boolean isSuper() {
        return isSuper;
    }

    @Override public void accept(final Visitor v) {
        v.visit(this);
    }

    @Override public void visit_all(final Visitor v) {
        rhs.accept(v);
        vector.accept(v);
    }
}
