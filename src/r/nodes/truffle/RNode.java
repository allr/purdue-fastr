package r.nodes.truffle;

import r.*;
import r.Truffle.Frame;
import r.Truffle.VirtualFrame;
import r.data.*;
import r.nodes.*;
import r.Truffle.*;

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
        // TODO Auto-generated method stub
        return null;
    }

    public RNode replace(RNode sc) {
        // TODO Auto-generated method stub
        return null;
    }

    public RNode replaceChild(RNode sc, RNode s) {
        // TODO Auto-generated method stub
        return null;
    }

    public RNode adoptChild(RNode o) {
        return null;
    }

    public RNode[] adoptChildren(RNode[] argsExprs) {
        // TODO Auto-generated method stub
        return null;
    }

    public RNode getParent() {
        // TODO Auto-generated method stub
        return null;
    }

}
