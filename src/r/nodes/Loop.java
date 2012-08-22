package r.nodes;

public abstract class Loop extends ASTNode {
    ASTNode body;

    public Loop(ASTNode body) {
        setBody(body);
    }

    public ASTNode getBody() {
        return body;
    }

    public void setBody(ASTNode expr) {
        this.body = updateParent(expr);
    }

    @Override
    public void visit_all(Visitor v) {
        getBody().accept(v);
    }

    public static While create(ASTNode cond, ASTNode expr) {
        return new While(cond, expr);
    }

    public static Repeat create(ASTNode expr) {
        return new Repeat(expr);
    }
}
