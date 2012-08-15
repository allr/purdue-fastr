package r.nodes;

public abstract class Loop extends Node {
    Node body;

    public Loop(Node body) {
        setBody(body);
    }

    public Node getBody() {
        return body;
    }

    public void setBody(Node expr) {
        this.body = updateParent(expr);
    }

    @Override
    public void visit_all(Visitor v) {
        getBody().accept(v);
    }

    public static While create(Node cond, Node expr) {
        return new While(cond, expr);
    }

    public static Repeat create(Node expr) {
        return new Repeat(expr);
    }
}
