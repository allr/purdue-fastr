package r.nodes;

// generic update expression, e.g. a recursive replacement call such as f(g(x)) <- 3

public class UpdateExpression extends ASTNode {

    ASTNode lhs;
    ASTNode rhs;
    final boolean isSuper;

    public UpdateExpression(boolean isSuper, ASTNode lhs, ASTNode rhs) {
        this.lhs = updateParent(lhs);
        this.rhs = updateParent(rhs);
        this.isSuper = isSuper;
    }

    public ASTNode getLHS() {
        return lhs;
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
        lhs.accept(v);
    }
}
