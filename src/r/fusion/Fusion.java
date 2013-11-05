package r.fusion;

import r.data.*;
import r.data.internal.View;

import java.util.*;

/** View fusion manager.
 */
public class Fusion {

    /** Prototype class for all fusion operators.
     *
     * Each single fusion operator is capable of executing views of certain signature in a fused way with single loop
     * and grouped NA checks.
     *
     * Most subclasses of fusion operator prototype are dynamically generated at runtime for particular views observed
     * during the program execution.
     */
    public abstract static class Prototype {

        /** Rebounds the fusion operator to the new view.
         *
         * In practice this should consist of walking a view visitor collecting the inputs for the particular fusion
         * operator.
         */
        protected abstract void reinitialize(View view);

        /** Nullifies all inputs to the view so that they can be garbage collected if not referenced elsewhere.
         */
        protected abstract void free();

        /** Performs the fused computation of the view and returns the result.
         */
        protected abstract RArray materialize_();

        /** Bounds the fusion operator to the given view, computes and returns the result and calls the free() method to
         * allow potential garbage collection of the inputs.
         */
        public final RArray materialize(View view) {
            reinitialize(view);
            RArray result = materialize_();
            free();
            return result;
        }
    }

    // Fusion caching --------------------------------------------------------------------------------------------------

    /** HashMap containing created fusion operators and their respective view signatures.
     */
    static final HashMap<String, Prototype> operators = new HashMap<>();

    /** Materializes the given view.
     *
     * A signature of the view is obtained. An empty signature means that the view contains elements that cannot be
     * fused (automatically), in which case the view is materialized in the standard way.
     *
     * Otherwise the cache of existing fusion operators is scanned for given signature and if a match is found, the
     * fused operation is used to materialize the view.
     *
     * If a fused operation for given signature is not found, it can either be created, or the view is materialized
     * in the standard way.
     *
     * @param view View to be materialized
     * @return Materialized contents of the view.
     */
    public static RArray materialize(View view) {
        String signature = view.signature();
        assert (signature != null);
        if (signature.isEmpty())
            return view.materialize();
        Prototype fusion = operators.get(signature);
        if (fusion != null)
            return fusion.materialize(view);
        // add a fusion operator, or materialize the view directly
        // TODO add fusion creation logic
        return view.materialize();
    }

    // Fusion operator generation --------------------------------------------------------------------------------------

    /** Signatures -- the signatures should be as simple as possible
     *
     * operator arity (B, U)
     * operator type ( +, -, ...)
     * result size (S, A, B, E)
     * left operand size (V, S)
     * left operand type (D, I, C, L)
     * right operand size (V, S)
     * right operand type (D, I, C, L)
     * left operand
     * right operand
     */


}
