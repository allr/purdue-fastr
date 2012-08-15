package r.nodes;


public class While extends Loop {
    Node cond;
    While(Node cond, Node expr) {
        super(expr);
        setCond(cond);
    }

    public Node getCond() {
        return cond;
    }

    public void setCond(Node cond) {
        this.cond = updateParent(cond);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
        getCond().accept(v);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
