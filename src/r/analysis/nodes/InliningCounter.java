package r.analysis.nodes;

import r.analysis.optimizations.Inlining;
import r.nodes.truffle.*;

public class InliningCounter extends CounterNode {

    public InliningCounter(RNode content) {
        super(content);
    }

    @Override
    public boolean thresholdReached() {
        try {
            // builtin calls cannot be inlined
            if (! (content instanceof FunctionCall))
                return false;
            FunctionCall fcall = (FunctionCall) content;
            // try the trivial inline for parameter & frame less functions
            GuardedNode opt = Inlining.optimizeNoArgsNoWrites(fcall);
            // if the optimization was successful, replace the content node with the guarded one
            if (opt != null)
                content.replace(opt);
            // always return false - as the counter should be removed
        } catch (Exception e) {
            e.printStackTrace();
            //assert (false) : " An optimization should never throw an error.";
        }
        return false;
    }
}
