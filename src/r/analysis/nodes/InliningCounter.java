package r.analysis.nodes;

import r.analysis.optimizations.Inlining;
import r.nodes.truffle.*;

public class InliningCounter extends CounterNode {

    public InliningCounter(FunctionCall content) {
        super(content);
    }

    @Override
    public boolean thresholdReached() {
        FunctionCall fcall = (FunctionCall) content;
        // try the trivial inline for parameter & frame less functions
        GuardedNode opt = Inlining.optimizeNoArgsNoWrites(fcall);
        // if the optimization was successful, replace the content node with the guarded one
        if (opt != null)
            content.replace(opt);
        // always return false - as the counter should be removed
        return false;
    }
}
