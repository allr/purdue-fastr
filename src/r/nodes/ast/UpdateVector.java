package r.nodes.ast;

public class UpdateVector extends ASTNode {

    AccessVector vector;
    ASTNode rhs;
    final boolean isSuper;

    public UpdateVector(boolean isSuper, AccessVector vector, ASTNode rhs) {
        this.vector = updateParent(vector);
        this.rhs = updateParent(rhs);
        this.isSuper = isSuper;
    }

    public AccessVector getVector() {
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
