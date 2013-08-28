package r.nodes.truffle;

import r.data.*;
import r.nodes.*;

import com.oracle.truffle.api.frame.*;

// TODO: support optimizations that avoid copying - that is, "ref" values if they are being duplicated by the update
//       (like in UpdateVector)

// FIXME: could get some performance for local variable updates (non-super assignment), e.g. like in UpdateVector

// Note that the semantics is not exactly as of GNU-R. In GNU-R, the variable is locally copied into a temporary variable *tmp*.
// We don't do this. However, we have the forcePromiseNode to read the variable to force a promise to get closer to that behavior.
// This indeed leads to double look-up on the variable, which is not great for performance.

// the call passed must have SimpleAccessVariable of "var" as its first argument
// and RememberLast (the value) as its last argument
public class ReplacementCall extends BaseR {
    @Child RNode forcePromiseNode;
    @Child RNode callNode;
    @Child RememberLast valueNode;
    @Child RNode assign;

    final boolean isSuper;

    Object newContent;

    public ReplacementCall(ASTNode ast, boolean isSuper, RSymbol var, RNode callNode, RememberLast valueNode) {
        super(ast);
        this.isSuper = isSuper;
        this.callNode = adoptChild(callNode);
        this.valueNode = adoptChild(valueNode);

        RNode node = adoptChild(new BaseR(ast) {
            @Override
            public final Object execute(Frame frame) {
                return newContent;
            }
        });
        if (isSuper) {
            this.assign = adoptChild(SuperWriteVariable.getUninitialized(ast, var, node));
        } else {
            this.assign = adoptChild(WriteVariable.getUninitialized(ast, var, node));
        }

        this.forcePromiseNode = adoptChild(ReadVariable.getUninitialized(ast, var));
    }

    @Override
    public Object execute(Frame frame) {
        forcePromiseNode.execute(frame);
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

        @Override
        public Object execute(Frame frame) {
            lastValue = node.execute(frame);
            return lastValue;
        }

        public Object lastValue() {
            return lastValue;
        }
    }
}
