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
        return parent.replaceChild(this, sc);
    }

    public RNode replace(RNode sc) {
        return replace(sc, "");
    }

    public abstract void replace0(RNode o, RNode n);

    public static void replace(RNode[] a, RNode o, RNode n) {
        for (int i = 0; i < a.length; i++)
            if (a[i] == o) {
                a[i] = n;
                break;
            }
    }

    public RNode replaceChild(RNode old, RNode nw) {
        replace0(old, nw);
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
