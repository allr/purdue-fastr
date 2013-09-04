package r.analysis.guards;

import com.oracle.truffle.api.frame.Frame;
import r.nodes.truffle.RNode;

/** Guard for an tree evaluation result.
 *
 * This is a generic guard that remembers a tree and an expected result. It evaluates the tree and if the evaluation
 * result differs from the expected result, invalidates itself.
 */
public class SameEvaluationResult extends Guard {

    final Object expected;

    @Child
    final RNode content;

    /** Creates the guard.
     *
     * @param expected Expected result to which the content must evaluate in order for the guard to hold.
     * @param content The execution tree that must evaluate to the expected result.
     */
    public SameEvaluationResult(Object expected, RNode content) {
        this.expected = expected;
        this.content = adoptChild(content);
    }

    /** Evaluates the content and invalidtes itself if it is different from the expected result, or if any error occurs
     * during its evaluation.
     */
    @Override
    public void check(Frame frame) throws GuardFailureException {
        try {
          if (content.execute(frame) == expected)
              return;
        } catch (Exception e) {
        }
        invalidate();
    }
}
