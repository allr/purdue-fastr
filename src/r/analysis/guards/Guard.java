package r.analysis.guards;

import com.oracle.truffle.api.*;
import r.analysis.codegen.annotations.DoNotVisit;
import r.nodes.truffle.RNode;

/** Basic guard node.
 *
 * The guard node, when invalidated
 *
 *
 */
public abstract class Guard extends RNode {
    final Assumption assumption;
    @Child @DoNotVisit
    RNode nextNode;

    public Guard(RNode nextNode) {
        assumption = Truffle.getRuntime().createAssumption();
        this.nextNode = nextNode;
    }

    public void invalidate() {
        assumption.invalidate();
        replace(nextNode);
    }

}
