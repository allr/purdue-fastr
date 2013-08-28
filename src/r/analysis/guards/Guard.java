package r.analysis.guards;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import r.analysis.codegen.annotations.DoNotVisit;
import r.nodes.truffle.RNode;

import java.util.LinkedList;

/** Abstract guard node.
 *
 *
 */
public abstract class Guard extends RNode {
    final Assumption assumption;

    public Guard() {
        assumption = Truffle.getRuntime().createAssumption();
    }

    public void invalidate() {
        assumption.invalidate();
        GuardHolder holder = (GuardHolder) getParent();
        holder.invalidate();
    }
}
