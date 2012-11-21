package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

import com.oracle.truffle.runtime.*;

// TODO: support optimizations that avoid copying - that is, "ref" values if they are being duplicated by the update (like in UpdateVector)

// FIXME: could get some performance for local variable updates (non-super assignment), e.g. like in UpdateVector

// the call passed must have SimpleAccessVariable of "var" as its first argument
// and RememberLast (the value) as its last argument
public class ReplacementCall extends BaseR {
    @Stable RNode callNode;
    @Stable RememberLast valueNode;
    @Stable RNode assign;

    final boolean isSuper;

    Object newContent;

    public ReplacementCall(ASTNode ast, boolean isSuper, RSymbol var, RNode callNode, RememberLast valueNode) {
        super(ast);
        this.isSuper = isSuper;
        this.callNode = updateParent(callNode);
        this.valueNode = updateParent(valueNode);

        RNode node = updateParent(new BaseR(ast) {
            @Override
            public final Object execute(RContext context, Frame frame) {
                return newContent;
            }
        });
        if (isSuper) {
            this.assign = updateParent(SuperWriteVariable.getUninitialized(ast, var, node));
        } else {
            this.assign = updateParent(WriteVariable.getUninitialized(ast, var, node));
        }
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        newContent = callNode.execute(context, frame);
        assign.execute(context, frame);
        return valueNode.lastValue();
    }

    public static final class RememberLast extends BaseR {
        @Stable RNode node;
        Object lastValue;

        public RememberLast(ASTNode ast, RNode node) {
            super(ast);
            this.node = node;
        }

        @Override
        public Object execute(RContext context, Frame frame) {
            lastValue = node.execute(context, frame);
            return lastValue;
        }

        public Object lastValue() {
            return lastValue;
        }
    }
}
