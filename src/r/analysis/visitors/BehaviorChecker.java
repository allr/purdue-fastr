package r.analysis.visitors;

import r.nodes.truffle.RNode;

import java.util.HashSet;

// TODO possibly throw an exception when found for faster return
// TODO implement tests for the behavior not present and mixture of both if required

/** Behavior checker class.
 *
 * Behavior checker is capable of performing a check whether certain behavior is present in a given execution tree. This
 * is done by visiting each of the tree nodes and checking its behavior against the specified one.
 *
 * There are two versions - one for a single behavior check and second one for a multiple behavior check. These are
 * created by the static methods create().
 *
 * The behavior classes are expected to come from r.analysis.codegen.annotations.behavior package.
 */
public abstract class BehaviorChecker implements NodeVisitor {

    /** Call this method with a given execution tree root to get whether the behavior is present in it.
     *
     * For a single behavior, returns true if the behavior is present in any of the nodes in the tree. For multiple
     * behaviors the result depends on the type argument passed to the create method. For ResultType.ANY if at least one
     * of the behaviors checked is found, true is returned and for ResultType.ALL all behaviors must be found in order
     * to return true from the method.
     */
    public abstract boolean test(RNode root);

    /** Type of multiple behavior checks.
     *
     * If ANY, the behavior check is valid if any of the behaviors specified is found.
     * If ALL, all behaviors must be present for the check to be valid.
     */
    public static enum ResultType {
        ALL,
        ANY
    }

    /** Creates a behavior checker for single behavior class.
     */
    public static BehaviorChecker create(Class behavior) {
        return new Single(behavior);
    }

    /** Creates a behavior checker for multiple behaviors and specified result type.
     *
     * If result type is ALL, all behaviors must be found in the examined tree for the check to be valid. For result
     * type ANY, only one behavior found of the many is enough.
     */
    public static BehaviorChecker create(Class[] behaviors, ResultType type) {
        return new Multiple(behaviors, type);
    }

    /** Creates a behavior checker for multiple behaviors with result type ResultType.ANY.
     */
    public static BehaviorChecker create(Class[] behaviors) {
        return new Multiple(behaviors, ResultType.ANY);
    }

    // Single behavior -------------------------------------------------------------------------------------------------

    static class Single extends BehaviorChecker {

        final Class behavior;
        boolean found;

        public Single(Class behavior) {
            this.behavior = behavior;
        }

        @Override
        public boolean test(RNode root) {
            found = false;
            root.accept(this);
            return found;
        }

        @Override
        public boolean visit(RNode node) {
            if (found)
                return false; // no subsequent checks are needed
            if (node.behaviorCheck(behavior)) {
                found = true;
                return false;
            }
            return true;
        }
    }

    // Multiple behavior -----------------------------------------------------------------------------------------------

    static class Multiple extends BehaviorChecker {

        final Class[] behaviors;
        final ResultType type;
        boolean[] found;
        HashSet<Integer> left = new HashSet<>();


        public Multiple(Class[] behaviors, ResultType type) {
            this.behaviors = behaviors;
            this.type = type;
            found = new boolean[behaviors.length];
        }

        @Override
        public boolean test(RNode root) {
            left.clear();
            for (int i = 0; i < found.length; ++i) {
                left.add(i);
                found[i] = false;
            }
            root.accept(this);
            if (type == ResultType.ANY) {
                for (boolean b : found)
                    if (b) return true;
                return false;
            } else {
                for (boolean b : found)
                    if (!b) return false;
                return true;
            }
        }

        @Override
        public boolean visit(RNode node) {
            for (int i : left) {
                if (node.behaviorCheck(behaviors[i])) {
                    found[i] = true;
                    if (type == ResultType.ANY)
                        left.clear();
                    else
                        left.remove(i);
                }
            }
            return ! left.isEmpty();
        }
    }
}
