package r.nodes.ast;

/** UpdateField AST, for $ operator at top level LHS of assignment.
 */
public class UpdateField extends ASTNode {

    FieldAccess vector;
    ASTNode rhs;
    final boolean isSuper;

    public UpdateField(boolean isSuper, FieldAccess vector, ASTNode rhs) {
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

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        rhs.accept(v);
        vector.accept(v);
    }
}
