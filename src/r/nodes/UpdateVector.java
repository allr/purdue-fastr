package r.nodes;

public class UpdateVector extends ASTNode {

    AccessVector vector;
    ASTNode rhs;

    public UpdateVector(AccessVector vector, ASTNode rhs) {
        this.vector = updateParent(vector);
        this.rhs = updateParent(rhs);
    }

    public AccessVector getVector() {
        return vector;
    }

    public ASTNode getRHS() {
        return rhs;
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
