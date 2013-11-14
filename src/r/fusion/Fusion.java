package r.fusion;

import r.data.*;
import r.data.internal.View;

import java.util.*;

/** Fusion manager for the views.
 *
 *
 */
public class Fusion {

    public static final boolean DEBUG = false;

    public static final boolean ENABLED_MATERIALIZE = true;

    public static final boolean ENABLED_INT = false;

    public static final boolean ENABLED_DOUBLE = false;

    public static final boolean ENABLE_STATISTICS = false;

    public static final boolean VERIFY = false;

    /* The indices of these features are random numbers to give the hashing function broader scope and therefore less
     * chances of collisions. They are used throughout the fusion system to identify them.
     */
    static final int SCALAR =      -1753807778;
    static final int VECTOR =      -661390690;
    static final int DOUBLE =       1858103944;
    static final int INT =         -1860336380;
    static final int COMPLEX =      630400415;
    static final int ADD =          393835468;
    static final int SUB =          1744231241;
    static final int MUL =         -1486659162;
    static final int DIV =          2108082263;
    static final int MOD =         -604326540;
    static final int EQUAL =       -513021938;
    static final int A =            64259293;
    static final int B =            1000853063;
    static final int INPUT =        1825547246;
    static final int SEQUENCE =    -436454746;
    static final int BINARY =      -958270914;
    static final int CONVERSION =  -1803245152;
    static final int SUBSET =      -1649474957;
    static final int INT_SEQUENCE = INT + SEQUENCE;

    /** HashMap containing created fusion operators and their respective view signatures.
     */
    static final HashMap<Integer, FusedOperator.Prototype> operators = new HashMap<>();

    static int compiled = 0;

    static int compilationFailed = 0;

    static int reused = 0;

    static int materialize = 0;

    static int materialize_hashed = 0;

    static int materialize_hashFailed = 0;

    static int materialize_rebound = 0;

    static int getInt = 0;

    static int getInt_hashed = 0;

    static int getInt_hashFailed = 0;

    static int getInt_rebound = 0;

    static int getDouble = 0;

    static int getDouble_hashed = 0;

    static int getDouble_hashFailed = 0;

    static int getDouble_rebound = 0;

    static double totSize = 0;






    /** Materializes the given view.
     *
     * Computes the hash of the view to determine if the view has already been compiled. If found, obtains the cached
     * fused operator, otherwise builds a new fused operator for the view.
     *
     * The fused operator is then executed to materialize the view.
     *
     * Note that if anything goes wrong a special fused operator that only calls the classic materialize in the view.
     *
     * @param view View to be materialized
     * @return Materialized contents of the view.
     */
    public static RArray materialize(View view) {
        if (ENABLE_STATISTICS) {
            ++materialize;
            totSize += view.size();
        }
        FusedOperator.Prototype fusedOperator = view.boundFusedOperator();
        if (fusedOperator == null) {
            if (ENABLE_STATISTICS)
                ++materialize_hashed;
            int hash = Hash.view(view);
            if (hash == 0) {
                if (ENABLE_STATISTICS)
                    ++materialize_hashFailed;
                FusedOperator.NO_FUSION.bind(view); // bind to NO_FUSION if we ever use it again
                return view.materialize_();
            }
            fusedOperator = getOrCompileFusedOperator(view, hash);
        } else {
            if (ENABLE_STATISTICS) {
                if (fusedOperator.boundView != view) // bind happens automatically in materialize call below
                    ++materialize_rebound;
            }
        }
        RArray result = fusedOperator.materialize(view);
        if (VERIFY)
            verify(result, view);
        return result;
    }

    public static int getInt(View view, int index) {
        if (ENABLE_STATISTICS)
            ++getInt;
        FusedOperator.Prototype fusedOperator = view.boundFusedOperator();
        if (fusedOperator == null) {
            if (ENABLE_STATISTICS)
                ++getInt_hashed;
            int hash = Hash.view(view);
            if (hash == 0) {
                if (ENABLE_STATISTICS)
                    ++getInt_hashFailed;
                FusedOperator.NO_FUSION.bind(view); // bind to NO_FUSION if we ever use it again
                return view.getInt_(index);
            }
            fusedOperator = getOrCompileFusedOperator(view, hash);
            if (ENABLE_STATISTICS && fusedOperator.boundView != null)
                ++getInt_rebound;
            fusedOperator.bind(view);
        } else {
            if (fusedOperator.boundView != null && fusedOperator.boundView != view) {
                if (ENABLE_STATISTICS)
                    ++getInt_rebound;
                fusedOperator.bind(view);
            }
        }
        int result = fusedOperator.getInt(view, index);
        if (VERIFY) {
            int check = view.getInt_(index);
            if (result != check)
                throw new Error("FUSION: elements differ");
        }
        return result;
    }

    public static double getDouble(View view, int index) {
        if (ENABLE_STATISTICS)
            ++getDouble;
        FusedOperator.Prototype fusedOperator = view.boundFusedOperator();
        if (fusedOperator == null) {
            if (ENABLE_STATISTICS)
                ++getDouble_hashed;
            int hash = Hash.view(view);
            if (hash == 0) {
                if (ENABLE_STATISTICS)
                    ++getDouble_hashFailed;
                FusedOperator.NO_FUSION.bind(view); // bind to NO_FUSION if we ever use it again
                return view.getDouble_(index);
            }
            fusedOperator = getOrCompileFusedOperator(view, hash);
            if (ENABLE_STATISTICS && fusedOperator.boundView != null)
                ++getDouble_rebound;
            fusedOperator.bind(view);
        } else {
            if (fusedOperator.boundView != null && fusedOperator.boundView != view) {
                if (ENABLE_STATISTICS)
                    ++getDouble_rebound;
                fusedOperator.bind(view);
            }
        }
        double result = fusedOperator.getDouble(view, index);
        if (VERIFY) {
            double check = view.getDouble_(index);
            if (result != check && !Double.isNaN(result) && !Double.isNaN(check))
                throw new Error("FUSION: elements differ");
        }
        return result;
    }

    public static FusedOperator.Prototype getOrCompileFusedOperator(View view, int hash) {
        FusedOperator.Prototype fusedOperator = operators.get(hash);
        if (fusedOperator == null) {
            if (ENABLE_STATISTICS)
                ++compiled;
            fusedOperator = FusedOperator.build(view, hash);
            operators.put(hash, fusedOperator);
            if (ENABLE_STATISTICS && fusedOperator == FusedOperator.NO_FUSION)
                ++compilationFailed;
        } else if (ENABLE_STATISTICS) {
            ++reused;
        }
        return fusedOperator;
    }

    public static void verify(RArray result, View view) {
        RArray check = view.materialize_();
        if (result.getClass() != check.getClass())
            throw new Error("FUSION: different class types");
        if (result.size() != check.size())
            throw new Error("FUSION: different result sizes");
        if (result instanceof RDouble) {
            double[] r = ((RDouble) result).getContent();
            double[] c = ((RDouble) check).getContent();
            for (int i = 0; i < r.length; ++i)
                if ((r[i] != c[i]) && !Double.isNaN(r[i]) && !Double.isNaN(c[i]))
                    throw new Error("FUSION: elements differ");
        } else if (result instanceof RInt) {
            int[] r = ((RInt) result).getContent();
            int[] c = ((RInt) check).getContent();
            for (int i = 0; i < r.length; ++i)
                if (r[i] != c[i])
                    throw new Error("FUSION: elements differ");
        } else {
            throw new Error("FUSION: unverified result type class");
        }
    }

    public static String statistics() {
        if (ENABLE_STATISTICS) {
            StringBuilder sb = new StringBuilder();
            sb.append("Materialized             " + materialize + "\n");
            sb.append("  hashed                 " + materialize_hashed + "\n");
            sb.append("  hash failed            " + materialize_hashFailed + "\n");
            sb.append("  rebound                " + materialize_rebound + "\n");
            sb.append("  total size:            "+totSize+"\n");
            sb.append("  average size:          "+totSize / materialize+ "\n");
            sb.append("\n");
            sb.append("getInt                   " + getInt + "\n");
            sb.append("  hashed                 " + getInt_hashed + "\n");
            sb.append("  hash failed            " + getInt_hashFailed + "\n");
            sb.append("  rebound                " + getInt_rebound + "\n");
            sb.append("\n");
            sb.append("getDouble                " + getDouble + "\n");
            sb.append("  hashed                 " + getDouble_hashed + "\n");
            sb.append("  hash failed            " + getDouble_hashFailed + "\n");
            sb.append("  rebound                " + getDouble_rebound + "\n");
            sb.append("\n");
            sb.append("Compiled (including attempts):  "+compiled+"\n");
            sb.append("Compilation failed:             "+compilationFailed+"\n");
            sb.append("Reused:                         "+reused+"\n");
            sb.append("Cached:                         "+operators.size()+"\n");

            return sb.toString();
        } else {
            return "FUSION STATISTICS DISABLED - Enable by setting Fusion.ENABLE_STATISTICS to true.\n";
        }

    }

    public static void clearStatistics() {
        materialize = 0;
        totSize = 0;
        materialize_hashed = 0;
        materialize_hashFailed = 0;
        materialize_rebound = 0;
        getInt = 0;
        getInt_hashed = 0;
        getInt_hashFailed = 0;
        getInt_rebound = 0;
        getDouble = 0;
        getDouble_hashed = 0;
        getDouble_hashFailed = 0;
        getDouble_rebound = 0;
        reused = 0;
    }
}
