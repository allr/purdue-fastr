package r.nodes.truffle;

import r.*;
import r.Truffle.Frame;
import r.Truffle.UnexpectedResultException;
import r.data.*;
import r.nodes.*;

public abstract class RNode {

    public ASTNode getAST() {
        return (getParent()).getAST();
    }

    static RNode[] Empty = new RNode[0];
    RNode[] children = Empty;

    public abstract Object execute(Frame frame);

    public int executeScalarLogical(Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectScalarLogical((RAny) execute(frame));
    }

    public int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectScalarNonNALogical((RAny) execute(frame));
    }

    public Object executeVoid(Frame frame) {
        execute(frame);
        return RNull.getNull();
    }

    public RAny execute(Frame frame, RAny lhs, RAny rhs) {
        // TODO Auto-generated method stub
        return null;
    }

    public RNode replace(RNode sc, String string) {
        return replace(sc);
    }

    public RNode replace(RNode sc) {
        return getParent().replaceChild(this, sc);
    }

    public RNode replaceChild(RNode old, RNode nw) {
        for (int i = 0; i < children.length; i++)
            if (children[i] == old) children[i] = nw;
        return adoptChild(nw);
    }

    public RNode adoptChild(RNode o) {
        if (o != null) o.parent = this;
        return o;
    }

    public RNode[] adoptChildren(RNode[] c) {
        if (c != null) for (RNode n : c)
            adoptChild(n);
        return c;
    }

    public RNode getParent() {
        return parent;
    }

    private RNode parent;
}
