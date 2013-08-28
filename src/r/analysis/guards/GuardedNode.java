package r.analysis.guards;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.InvalidAssumptionException;
import r.analysis.codegen.annotations.*;
import r.nodes.truffle.RNode;

/** A guarded original implementation.
 *
 * The guarded original consists of the link to the guard that must be true, the guarded original itself, which is the original that
 * will be executed iff the guard holds and a fallback original, which implements the functionality of the guarded original for
 * cases when the guard does not hold anymore.
 *
 * Upon execution, the guard's assumption is checked and if valid, guarded original is executed. Otherwise the guarded original
 * is replaced with its fallback original which is then executed.
 */
public class GuardedNode extends RNode {
    @Shared
    @DoNotVisit
    final Guard guard;

    @Child
    RNode guardedNode;

    @Child
    @DoNotVisit
    RNode fallbackNode;

    /** Creates the guarded original.
     *
     * @param guard Guard which guards the execution.
     * @param node Node that depends on the validity of the guard.
     * @param fallback Fallback original providing the same functionality as original, but does not rely on the guard.
     */
    protected GuardedNode(Guard guard, RNode node, RNode fallback) {
        this.guard = guard;
        this.guardedNode = adoptChild(node);
        this.fallbackNode = adoptChild(fallback);
    }

    /** Executes the original.
     *
     * Iff the guard holds, returns the result of executed guarded original.
     *
     * If the guard is invalidated, rewrites itself to the fallback original and returns its execution result.
     */
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
