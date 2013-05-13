package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;

public abstract class RNode extends Node {

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

        public PushbackNode(RNode oldNode, ASTNode ast, RNode realChildNode, Object nextValue) {
            super(ast);
            oldNode.replace(this);
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
        new PushbackNode(childNode, childNode.getAST(), childNode, value);
    }

    public <T extends RNode> Object replace(T childNode, Object childValue, RNode newNode, Frame frame) {
        pushBack(childNode, childValue);
        return replace(newNode).execute(frame);
    }
}
