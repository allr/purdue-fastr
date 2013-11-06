package r.fusion;

import r.data.*;
import r.data.internal.View;

import java.util.*;

/** View fusion manager.
 */
public class Fusion {

    public static final boolean DEBUG = true;

    public static final boolean ENABLED = true;

    static final int SCALAR = -1753807778;
    static final int VECTOR = -661390690;
    static final int DOUBLE =  1858103944;
    static final int INT =    -1860336380;
    static final int COMPLEX = 630400415;
    static final int ADD =     393835468;
    static final int SUB =     1744231241;
    static final int MUL =    -1486659162;
    static final int DIV =     2108082263;
    static final int MOD =    -604326540;
    static final int EQUAL =  -513021938;
    static final int A =       64259293;
    static final int B =       1000853063;
    static final int INPUT =   1825547246;
    static final int BINARY = -958270914;
    static final int UNARY =  -1803245152;



    /** Prototype class for all fusion operators.
     *
     * Each single fusion operator is capable of executing views of certain signature in a fused way with single loop
     * and grouped NA checks.
     *
     * Most subclasses of fusion operator prototype are dynamically generated at runtime for particular views observed
     * during the program execution.
     */
    public static abstract class Prototype extends View.Visitor {

        protected int idx;

        @Override
        public void visitLeaf(RAny element) {
            assert (false);
        }

        /** An assert function because javassist does not support asserts easily. This might be optimized, but I hope java
         * would always inline this anyways.
         */
        protected final void assert_(boolean condition) {
            assert (condition);
        }

        /** Bounds the fusion operator to the given view, computes and returns the result and calls the free() method to
         * allow potential garbage collection of the inputs.
         */
        public final RArray materialize(View view) {
            reinitialize(view);
            RArray result = materialize_();
            free();
            return result;
        }


        /** Rebounds the fusion operator to the new view.
         *
         * In practice this should consist of walking a view visitor collecting the inputs for the particular fusion
         * operator.
         */
        public void reinitialize(View view) {
            idx = 0;
            view.visit(this);
        }

        /** Nullifies all inputs to the view so that they can be garbage collected if not referenced elsewhere.
         */
        public abstract void free();

        /** Performs the fused computation of the view and returns the result.
         */
        public abstract RArray materialize_();

    }



    /** Special prototype for signatures which failed the fused operatior generation so that they are not re-attempted.
     *
     * Just calls the materialize_() method on the view.
     */
    static class NoFusion extends Prototype {
        View view;

        @Override
        public void reinitialize(View view) {
            this.view = view;
        }

        @Override
        public void free() {
            view = null;
        }

        @Override
        public RArray materialize_() {
            return view.materialize_();
        }
    }

    static final NoFusion NO_FUSION = new NoFusion();

    // Fusion caching --------------------------------------------------------------------------------------------------

    /** HashMap containing created fusion operators and their respective view signatures.
     */
    static final HashMap<Integer, FusedOperator.Prototype> operators = new HashMap<>();

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


    // TODO only DoubleView is instrumented, others should be too
    public static RArray materialize(View view) {
        int hash = Hash.view(view);
        if (hash == 0)
            return view.materialize_();
        FusedOperator.Prototype fusedOperator = operators.get(hash);
        if (fusedOperator == null) {
            fusedOperator = FusedOperator.build(view, hash);
            operators.put(hash, fusedOperator);

            System.exit(0);

            fusedOperator = FusedOperator.NO_FUSION;
        }
        return fusedOperator.materialize(view);

/*
        String signature = view.signature();
        if (signature == null)
            return view.materialize_();
        Prototype fusion = operators.get(signature);
        if (fusion == null) {
            // add a fusion operator, or materialize the view directly
            if (DEBUG)
                System.out.println("Found view signature " + signature + ", creating fused operator...");
            fusion = FusionBuilder.build(signature);
            if (fusion == null) {
                fusion = NO_FUSION;
                if (DEBUG)
                    System.out.println("NOT SUPPORTED");
            }
            operators.put(signature, fusion);
        }
        return fusion.materialize(view); */
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
