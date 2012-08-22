package r.nodes;

import java.util.*;


public class Sequence extends ASTNode {
    ASTNode[] exprs;
    Sequence(ASTNode[] e) {
        exprs = updateParent(e); // FIXME or not ... do we need to duplicate this array
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public ASTNode[] getExprs() {
        return exprs;
    }

    @Override
    public void visit_all(Visitor v) {
        for (ASTNode e: exprs) {
            e.accept(v);
        }
    }

    public static ASTNode create(ArrayList<ASTNode> exprs) {
        return new Sequence(exprs.toArray(new ASTNode[exprs.size()]));
    }
    public static ASTNode create(ASTNode[] exprs) {
        return new Sequence(exprs);
    }
}
