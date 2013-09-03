package r.analysis.guards;

import com.oracle.truffle.api.frame.Frame;
import r.nodes.truffle.RNode;

public class SameEvaluationResult extends Guard {

    final Object expected;
    @Child
    final RNode content;

    public SameEvaluationResult(Object expected, RNode contents) {
        this.expected = expected;
        this.content = adoptChild(contents);
    }

    @Override
    public void check(Frame frame) throws GuardFailureException {
        if (content.execute(frame) != expected)
            throw new GuardFailureException();
    }
}
