package r.analysis.guards;

import com.oracle.truffle.api.frame.Frame;
import r.analysis.codegen.annotations.VisitOrder;
import r.nodes.ASTNode;
import r.nodes.truffle.*;

/** Node holding the guard.
 *
 * A guard node is always child of a holder node. The holder node holds the guard and the node that comes right after
 * the guard which is either the guarded node in the coupled position, or the node before which the guard has moved.
 *
 * The guard holder always executes first the guard and then returns the result of the execution of the next node.
 *
 * TODO maybe different GuardHolder nodes should be defined for different result types of the node (?)
 */
public class GuardHolder extends BaseR {
    @Child
    @VisitOrder(index = 1)
    Guard guard;
    @Child
    @VisitOrder(index = 2)
    RNode node;

    public GuardHolder(ASTNode orig, Guard guard, RNode node) {
        super(orig);
        this.guard = adoptChild(guard);
        this.node = adoptChild(node);
    }

    public GuardHolder(Guard guard, BaseR node) {
        super(node.getAST());
        this.guard = adoptChild(guard);
        this.node = adoptChild(node);
    }

    @Override
    public Object execute(Frame frame) {
        guard.execute(frame);
        return node.execute(frame);
    }

    public void invalidate() {
        replace(node);
    }
}
