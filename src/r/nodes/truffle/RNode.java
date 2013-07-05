package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.analysis.*;
import r.data.*;
import r.nodes.*;

public abstract class RNode extends Node implements DeepCopyable {


    protected RNode(RNode other) {
    }

    public RNode() {
    }

    /** Returns the deep copy of the node. This method should never be called as it should be overridden either manually
     * or automatically using fastr loader in subclasses.
     */
    public RNode deepCopy() {
        assert false : "This code should never be reached if the fastr class patcher is active. Make sure to run r.fastr instead of r.Console!";
        return null;
    }

    /** Linear visitor method. These methods must either be specified manually, or will be generated automatically by
     * the fastr loader for all RNode fields.
     *
     * The return value of this method should largely be ignored by the user, but internally it is used to signify that
     * children of the node should not be visited. The pattern for each child is to call first the parent's linearVisit
     * and return false if its result is false.
     *
     * Otherwise the children of the node should be visited in desired order and true returned.
     *
     * When overriding completely, calling visitor in self must be the first statement and no children should be visited
     * if this result is false.
     */
    public boolean linearVisit(r.analysis.NodeVisitor visitor) {
        return visitor.visit(this);
    }

    public ASTNode getAST() {
        return ((RNode) getParent()).getAST();
    }

    public abstract Object execute(Frame frame);

    public int executeScalarLogical(Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectScalarLogical((RAny) execute(frame));
    }

    public int executeScalarInteger(Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectScalarInteger((RAny) execute(frame));
    }

    public int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectScalarNonNALogical((RAny) execute(frame));
    }

    public Object executeVoid(Frame frame) {
        execute(frame);
        return RNull.getNull();
    }

    public static class PushbackNode extends BaseR {
        @Child RNode realChildNode;
        final Object nextValue;

        public PushbackNode(ASTNode ast, RNode realChildNode, Object nextValue) {
            super(ast);
            realChildNode.replace(this);
            this.realChildNode = adoptChild(realChildNode);
            this.nextValue = nextValue;
        }

        @Override
        public Object execute(Frame frame) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                replace(realChildNode);
                return nextValue;
            }
        }
    }

    public <T extends RNode> void pushBack(T childNode, Object value) {
        new PushbackNode(childNode.getAST(), childNode, value);
    }

    public <T extends RNode> Object replace(T childNode, Object childValue, RNode newNode, Frame frame) {
        pushBack(childNode, childValue);
        return replace(newNode).execute(frame);
    }

}
