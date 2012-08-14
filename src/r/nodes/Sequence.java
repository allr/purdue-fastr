package r.nodes;


public class Sequence implements Node {
    Node[] exprs;
    Sequence(Node[] e) {
        exprs = e; // FIXME or not ... do we need to duplicate this array
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Node[] getExprs() {
        return exprs;
    }

    public void visit_all(Visitor v) {
        for (Node e: exprs) {
            e.accept(v);
        }
    }
}
