package r.nodes;

import java.util.*;


public class Sequence extends Node {
    Node[] exprs;
    Sequence(Node[] e) {
        exprs = updateParent(e); // FIXME or not ... do we need to duplicate this array
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Node[] getExprs() {
        return exprs;
    }

    @Override
    public void visit_all(Visitor v) {
        for (Node e: exprs) {
            e.accept(v);
        }
    }

    public static Node create(ArrayList<Node> exprs) {
        return new Sequence(exprs.toArray(new Node[exprs.size()]));
    }
    public static Node create(Node[] exprs) {
        return new Sequence(exprs);
    }
}
