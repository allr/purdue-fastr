package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ASTNode;
import r.nodes.truffle.*;

/**
 * ":"
 * 
 * <pre>
 * from -- starting value of sequence.
 * to -- (maximal) end value of the sequence.
 * </pre>
 */
// FIXME: Truffle can't optimize BuiltIn2
// FIXME: there is a version of colon for factors that is currently not implemented.
final class Colon extends CallFactory {

    static final CallFactory _ = new Colon(":", new String[]{"from", "to"}, null);

    Colon(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new BuiltIn.BuiltIn2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                return generic(ast, arg0, arg1);
            }
        };
    }

    // a simple version that eagerly creates the vector, create(int, int) and (double, double) below are more efficient
    public static RAny createEager(int left, int right) {
        if (left <= right) {
            int len = right - left + 1;
            int[] data = new int[len];
            int val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val++;
            }
            return RInt.RIntFactory.getFor(data);
        } else {
            int len = left - right + 1;
            int[] data = new int[len];
            int val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val--;
            }
            return RInt.RIntFactory.getFor(data);
        }
    }

    public static RAny create(int left, int right) {
        int step = (left <= right) ? 1 : -1;
        return RInt.RIntFactory.forSequence(left, right, step);
    }

    public static RAny create(double left, double right) {
        if (left <= right) {
            int len = (int) (right - left + 1); // FIXME: probably should check for a too long vector
            double[] data = new double[len];
            double val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val;
                val = val + 1.0;
            }
            return RDouble.RDoubleFactory.getFor(data);
        } else {
            int len = (int) (left - right + 1); // FIXME: probably should check for a too long vector
            double[] data = new double[len];
            double val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val;
                val = val - 1.0;
            }
            return RDouble.RDoubleFactory.getFor(data);
        }
    }

    public static void checkScalar(RArray a, ASTNode ast) {
        int n = a.size();
        if (n == 0) { throw RError.getLengthZero(ast); }
        if (n > 1) {
            RContext.warning(ast, String.format(RError.ONLY_FIRST_USED, n));
        }
    }

    public static void checkNAandNaN(double d, ASTNode ast) {
        if (RDouble.RDoubleUtils.isNAorNaN(d)) { throw RError.getNAorNaN(ast); }
    }

    public static void checkNA(int i, ASTNode ast) {
        if (i == RInt.NA) { throw RError.getNAorNaN(ast); }
    }

    @SuppressWarnings("cast") public static RAny generic(ASTNode ast, RAny arg0, RAny arg1) {
        if (arg0 instanceof RInt) {
            RInt a0rint = (RInt) arg0;
            checkScalar(a0rint, ast);
            int a0 = a0rint.getInt(0);
            checkNA(a0, ast);
            if (arg1 instanceof RInt) {
                RInt a1rint = (RInt) arg1;
                checkScalar(a1rint, ast);
                int a1 = a1rint.getInt(0);
                checkNA(a1, ast);
                return create(a0, a1);
            }
            if (arg1 instanceof RDouble) {
                RDouble a1rdbl = (RDouble) arg1;
                checkScalar(a1rdbl, ast);
                double a1 = a1rdbl.getDouble(0);
                checkNAandNaN(a1, ast);
                if (RDouble.RDoubleUtils.fitsRInt(a1)) {
                    return create(a0, (int) a1); // note casting to int does exactly what we want - truncate towards zero
                } else {
                    return create(a0, a1);
                }
            }
            throw Utils.nyi("unsupported argument type for colon operator");
        }

        if (arg0 instanceof RDouble) {
            RDouble a0rdbl = (RDouble) arg0;
            checkScalar(a0rdbl, ast);
            double a0 = a0rdbl.getDouble(0);
            checkNAandNaN(a0, ast);
            int ia0 = (int) a0;
            if (((double) ia0) == a0) { // this re-casting is intentional
                // note: nearly copy-paste from above, but we should rewrite to nodes anyway
                if (arg1 instanceof RInt) {
                    RInt a1rint = (RInt) arg1;
                    checkScalar(a1rint, ast);
                    int a1 = a1rint.getInt(0);
                    checkNA(a1, ast);
                    return create(ia0, a1);
                }
                if (arg1 instanceof RDouble) {
                    RDouble a1rdbl = (RDouble) arg1;
                    checkScalar(a1rdbl, ast);
                    double a1 = a1rdbl.getDouble(0);
                    checkNAandNaN(a1, ast);
                    if (RDouble.RDoubleUtils.fitsRInt(a1)) {
                        return create(ia0, (int) a1);
                        // note casting to int does exactly what we want - truncate towards zero
                    } else {
                        return create(a0, a1);
                    }
                }
                Utils.nyi("unsupported argument type for colon operator");

            } else {
                if (arg1 instanceof RDouble) {
                    RDouble a1rdbl = (RDouble) arg1;
                    checkScalar(a1rdbl, ast);
                    double a1 = a1rdbl.getDouble(0);
                    checkNAandNaN(a1, ast);
                    return create(a0, a1);
                } else {
                    throw Utils.nyi("unsupported argument type for colon operator");
                }
            }
        }
        throw Utils.nyi("unsupported argument types for colon operator");
    }
}
