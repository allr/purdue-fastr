package r.analysis.guards;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.InvalidAssumptionException;
import r.analysis.codegen.annotations.*;
import r.nodes.truffle.RNode;

public class GuardedNode extends RNode {
    @Shared
    @DoNotVisit
    final Guard guard;

    @Child
    RNode guardedNode;

    @Child
    @DoNotVisit
    RNode fallbackNode;



    protected GuardedNode(Guard guard, RNode node, RNode fallback) {
        this.guard = guard;
        this.guardedNode = adoptChild(node);
        this.fallbackNode = adoptChild(fallback);
    }

    @Override
    public Object execute(Frame frame) {
        try {
            guard.assumption.check();
            return guardedNode.execute(frame);
        } catch (InvalidAssumptionException e) {
            CompilerDirectives.transferToInterpreter();
            this.replace(fallbackNode);
            return fallbackNode.execute(frame);
        }
    }
}
