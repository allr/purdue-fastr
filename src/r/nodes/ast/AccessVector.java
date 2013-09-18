package r.nodes.ast;


public class AccessVector extends Call {

    final ASTNode vector;
    final boolean subset;

    public AccessVector(ASTNode vector, ArgumentList args, boolean subset) {
        super(args);
        this.vector = vector;
        this.subset = subset;
    }

    public ASTNode getVector() {
        return vector;
    }

    public  boolean isSubset() {
        return subset;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
        vector.accept(v);
    }

}
