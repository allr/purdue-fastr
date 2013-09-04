package r.analysis.nodes;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.*;
import r.analysis.guards.Guard;
import r.nodes.ASTNode;
import r.nodes.truffle.*;

/** A guard holder.
 *
 * Guard holder always remembers the original that should be executed after the guards, which allows the guard holder to be
 * placed anywhere in the execution tree. The holder executes the guard(s) and then executes the original and returns its
 * result. The result of the guard's execution method is discarded.
 *
 * If the guard(s) fail, the holder is replaced with the original original.
 *
 * There are two main types of guard holders:
 *
 * A SingleGuard holder which holds a single guard and the MultiGuard holder with holds multiple guards, executes them
 * all, but when a single guard fails, invalidates all of them.
 *
 * TODO maybe other execute methods need to be implemented too, waiting for integrations if this changes
 */
public abstract class GuardHolder extends BaseR {

    @Child
    RNode original;

    public GuardHolder(ASTNode orig, RNode original) {
        super(orig);
        this.original = adoptChild(original);
    }

    public GuardHolder(BaseR original) {
        super(original.getAST());
        this.original = adoptChild(original);
    }

    /** Adds the given guard to the guard(s) already held.
     *
     * Creates a MultiGuard holder if necessary. Replaces itself with the newly created guard holder and also returns
     * it.
     *
     * @param guard Guard to be added.
     * @return Guard holder holding also the specified guard. The guard holder itself is automatically replaced by this
     * new guard holder.
     */
    public abstract GuardHolder join(Guard guard);

    /** Node holding a single the guard.
     *
     * A guard original is always child of a holder original. The holder original holds the guard and the original that comes right after
     * the guard which is either the guarded original in the coupled position, or the original before which the guard has moved.
     *
     * The guard holder always executes first the guard and then returns the result of the execution of the next original.
     *
     * TODO maybe different GuardHolder nodes should be defined for different result types of the original (?)
     */
    public static class SingleGuard extends GuardHolder {
        @Child
        Guard guard;

        /** Creates the guard holder for single guard and replaced original.
         *
         * @param ast AST original referring to the guard holder.
         * @param guard Guard itself.
         * @param original Node previously in place.
         */
        public SingleGuard(ASTNode ast, Guard guard, RNode original) {
            super(ast, original);
            this.guard = adoptChild(guard);
        }

        /** Creates the guard holder using AST of the old original.
         *
         * @param guard Guard itself.
         * @param original Old original.
         */
        public SingleGuard(Guard guard, BaseR original) {
            super(original);
            this.guard = adoptChild(guard);
        }

        /** Executes the guard and the original original.
         *
         * First executes the guard, then executes the original original and return its execution result. Guard's execution
         * result is ignored as guards are not allowed to return anything.
         */
        @Override
        public Object execute(Frame frame) {
            try {
                guard.check(frame);
                return original.execute(frame);
            } catch (Guard.GuardFailureException e) {
                return replace(original).execute(frame); // the same code, but for better clarity
            }
        }

        /** Adds the given guard to the holder.
         *
         * Creates new MultiGuard with the guard already held and the new one, replaces itself with it and returns the
         * newly created MultiGuard.
         *
         * @param guard Guard to be added.
         * @return New MultiGuard with which the single guard has already been replaced.
         */
        @Override
        public GuardHolder join(Guard guard) {
            return replace(new MultiGuard(original.getAST(), new Guard[] { this.guard, guard }, original));
        }
    }

    /** Holder for multiple guards.
     *
     * Holds an array of guards. Upon execution checks all of them and if any of the guards fail, stops their execution,
     * invalidates all the guards it holds and replaces itself with the original node.
     *
     * After all guards are checked, executes the original node and returns its result.
     */
    public static class MultiGuard extends GuardHolder {

        @Children
        final Guard[] guards;

        /** Creates the multiguard with reference AST and original node.
         */
        public MultiGuard(ASTNode ast, Guard[] guards, RNode original) {
            super(ast, original);
            this.guards = adoptChildren(guards);
        }

        /** Creates the multi guard with original node whose ast will be used.
         */
        public MultiGuard(Guard[] guards, BaseR original) {
            super(original);
            this.guards = adoptChildren(guards);
        }

        /** Checks all the guards and then executes the original node.
         *
         * If any of the guards fail, invalidates all of them, replaces itself with the original node, executes it and
         * returns its result.
         */
        @Override
        @ExplodeLoop
        public Object execute(Frame frame) {
            try {
                for (Guard g : guards)
                    g.check(frame);
                return original.execute(frame);
            } catch (Guard.GuardFailureException e) {
                for (Guard g : guards)
                    g.invalidateInternal();
                return replace(original).execute(frame);
            }
        }

        /** Replaces itself with another multi holder which also checks for the given guard.
         */
        @Override
        public GuardHolder join(Guard guard) {
            Guard[] guards = new Guard[this.guards.length + 1];
            System.arraycopy(this.guards, 0, guards, 0, this.guards.length);
            guards[this.guards.length] = guard;
            return replace(new MultiGuard(original.getAST(), guards, original));
        }
    }

}
