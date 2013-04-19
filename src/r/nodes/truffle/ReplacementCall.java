package r.nodes.truffle;

import r.data.*;
import r.nodes.*;
import r.Truffle.*;

// TODO: support optimizations that avoid copying - that is, "ref" values if they are being duplicated by the update
//       (like in UpdateVector)

// FIXME: could get some performance for local variable updates (non-super assignment), e.g. like in UpdateVector

// the call passed must have SimpleAccessVariable of "var" as its first argument
// and RememberLast (the value) as its last argument
public class ReplacementCall extends BaseR {
    @Child RNode callNode;
    @Child RememberLast valueNode;
    @Child RNode assign;

    @Override public void replace0(RNode o, RNode n) {
        if (callNode == o) callNode = n;
        if (valueNode == o) valueNode = (RememberLast) n;
        if (assign == o) assign = n;
    }

    final boolean isSuper;

    Object newContent;

    public ReplacementCall(ASTNode ast, boolean isSuper, RSymbol var, RNode callNode, RememberLast valueNode) {
        super(ast);
        this.isSuper = isSuper;
        this.callNode = adoptChild(callNode);
        this.valueNode = (RememberLast) adoptChild(valueNode);

        RNode node = adoptChild(new BaseR(ast) {
            @Override public final Object execute(Frame frame) {
                return newContent;
            }

            @Override public void replace0(RNode o, RNode n) {}
        });
        if (isSuper) {
            this.assign = adoptChild(SuperWriteVariable.getUninitialized(ast, var, node));
        } else {
            this.assign = adoptChild(WriteVariable.getUninitialized(ast, var, node));
        }
    }

    @Override public Object execute(Frame frame) {
        newContent = callNode.execute(frame);
        assign.execute(frame);
        return valueNode.lastValue();
    }

    public static final class RememberLast extends BaseR {
        @Child RNode node;
        Object lastValue;

        public RememberLast(ASTNode ast, RNode node) {
            super(ast);
            this.node = node;
        }

        @Override public Object execute(Frame frame) {
            lastValue = node.execute(frame);
            return lastValue;
        }

        public Object lastValue() {
            return lastValue;
        }

        @Override public void replace0(RNode o, RNode n) {
            if (node == o) node = n;
        }

    }
}
