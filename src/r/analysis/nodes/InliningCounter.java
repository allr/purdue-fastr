package r.analysis.nodes;

import r.analysis.optimizations.Inlining;
import r.nodes.truffle.*;

/** Counter node for inlining optimizations.
 *
 * This counter is inserted before a function call and upon threshold tries to inline the called function. If
 * successful, replaces itself with the GuardHolder and GuardedNode for the inlining returned by the Inlining.optimize
 * methods. Otherwise just disappears from the execution tree.
 *
 * The inlining optimizations used are described in greater detail in the class Inlining.
 */
public class InliningCounter extends CounterNode {

    public InliningCounter(RNode content) {
        super(content);
    }

    /** Tries to apply the inlining optimizations and forces the removal of the counter from the execution tree.
     */
    @Override
    public boolean thresholdReached() {
        try {
            // builtin calls cannot be inlined
            if (! (content instanceof FunctionCall))
                return false;
            FunctionCall fcall = (FunctionCall) content;
            // try the trivial inline for parameter & frame less functions
            RNode opt = Inlining.optimizeNoArgsNoWrites(fcall);
            // if the optimization was successful, replace the content node with the guarded one
            if (opt != null)
                content = adoptChild(opt);
            // always return false - as the counter should be removed
        } catch (Exception e) {
            e.printStackTrace();
            //assert (false) : " An optimization should never throw an error.";
        }
        return true;
    }
}
