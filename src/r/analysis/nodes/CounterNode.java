package r.analysis.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import r.nodes.truffle.RNode;

/** Counter node which counts the number of its executions.
 *
 * When the number of executions hits certain threshold, the thresholdReached() abstract method is called which can
 * perform the desired action. The counter node is then replaced with its contents so that it gets no longer executed.
 *
 * Override this node and implement its thresholdReached() method to make use of it.
 *
 * TODO Maybe different thresholds.
 *
 * TODO Maybe discard counter node on exception from content execution, or content node change? 
 */
public abstract class CounterNode extends RNode {

    /** Counter threshold. When number of executions reaches this number, an action will be taken.
     */
    public static final int THRESHOLD = 10;

    int count;

    @Child
    RNode content;

    /** Creates the counter node.
     *
     * @param content Content node whose execution is guarded.
     */
    public CounterNode(RNode content) {
        count = 0;
        this.content = adoptChild(content);
    }

    /** Executes the content node and increases the counter.
     *
     * First the content node gets executed. Then the counter is incremented and if the counter reaches the threshold,
     * execution is transferred to the interpreter, the thresholdReached() method is executed. If thresholdReached()
     * returns true, the counter node is removed from the execution stream by replacing it with the content node.
     *
     * If the thresholdReached() returns false, the counter node stays in place and it is assumed that the counter's
     * value have been changed.
     *
     * @return Result of the content node invocation.
     */
    @Override
    public Object execute(Frame frame) {
        Object result = content.execute(frame);
        ++count;
        if (count >= THRESHOLD) {
            CompilerDirectives.transferToInterpreter();
            if (thresholdReached())
                replace(content);
        }
        return result;
    }

    /** Action to be taken when the execution counter reaches the threshold.
     *
     * This method is guaranteed to run in the interpreter.
     *
     * This method should define the action that should be taken when the threshold is reached. True should then be
     * returned to get rid of the counter node replacing it with the content node. However, if further counting is
     * required, the counter's value should be changed and false returned.
     *
     * @return True if the counter node should be replaced with its contents, false otherwise.
     */
    public abstract boolean thresholdReached();
}
