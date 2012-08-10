package r.nodes;


public class Sequence implements Node {
    Node[] exprs;
    Sequence(Node[] e) {
        exprs = e; // FIXME or not ... do we need to duplicate this array
    }
}
