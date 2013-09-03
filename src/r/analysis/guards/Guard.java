package r.analysis.guards;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.SlowPathException;
import r.nodes.truffle.RNode;


/** Abstract guard original.
 *
 * A guard is a special node that should only check whether a certain condition holds, or not. This is done in the
 * check() method, which is not allowed to return anything. If the guard preprocesses something, it must be stored as
 * the guard's field.
 *
 * If the guard fails, it should call its invalidate method, which throws the GuardFailureException. The call to
 * invalidate should thus be the last thing the check() method does upon failure.
 */
public abstract class Guard extends RNode {

    static public class GuardFailureException extends SlowPathException { }

    public final Assumption assumption;

    /** Creates the guard.
     *
     * Creates the Assumption associated with the guard.
     */
    public Guard() {
        assumption = Truffle.getRuntime().createAssumption();
    }

    /** Invalidates the assumption.
     *
     * Override this method, if more is to be done upon invalidation. This method is also called by MultiGuard when
     * invalidating automatically after a single guard failure.
     */
    protected void invalidateInternal() {
        assumption.invalidate();
    }

    /** Invalidates the guard.
     *
     * Call this method from check() if the guard should be invalidated. Calls the internal invalidate and then throws
     * the GuardFailureException to inform the GuardHandler below.
     * @throws GuardFailureException
     */
    public void invalidate() throws GuardFailureException {
        invalidateInternal();
        throw new GuardFailureException();
    }

    /** Never call execute on guards.
     */
    public Object execute(Frame frame) {
        assert (false) : " Use guard.check() instead.";
        return null;
    }

    /** Checks that the condition for the guard still holds.
     *
     * Override this method to check for the condition. If the condition is not valid, call invalidate() to invalidate
     * the guard.
     */
    public abstract void check(Frame frame) throws GuardFailureException;
}
