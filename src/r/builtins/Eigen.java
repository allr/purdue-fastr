package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// NOTE: GNU-R has some of the eigen code implemented in R (and some uses LAPACK)
public class Eigen {

    private static final String[] paramNames = new String[]{"x", "symmetric", "only.values", "EISPACK"};

    private static final int IX = 0;
    private static final int ISYMMETRIC = 1;
    private static final int IONLY_VALUES = 2;
    private static final int IEISPACK = 3;

    public static boolean parseLogicalAsCondition(RAny arg, ASTNode ast, String argName) {
        // FIXME: not exactly R semantics, R would ignore non-coerceable values at indexes 2 and higher
        // FIXME: not exactly R error messages

        RLogical larg = arg.asLogical();
        if (larg.size() >= 1) {
            int l = larg.getLogical(0);
            if (l == RLogical.TRUE) {
                return true;
            }
            if (l == RLogical.FALSE) {
                return false;
            }
        }
        throw RError.getInvalidArgument(ast, argName);
    }

    public static final CallFactory EIGEN_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }
            if (provided[IEISPACK]) {
                Utils.nyi("EISPACK argument not supported");
            }

            return new BuiltIn(call, names, exprs) {

                @Override
                public RAny doBuiltIn(Frame frame, RAny[] params) {
                    RAny xany = params[paramPositions[IX]];

                    RArray x;
                    boolean complex = false;
                    double[] values;

                    if (xany instanceof RDouble) {
                        x = (RDouble) xany;
                        values = RDouble.RDoubleUtils.asDoubleArray((RDouble) xany);
                    } else if (xany instanceof RComplex) {
                        complex = true;
                        x = (RComplex) xany;
                        values = RComplex.RComplexUtils.asDoubleArray((RComplex) xany);
                    } else {
                        RDouble xd = Convert.coerceToDoubleError(xany, ast); // FIXME: not exactly R error handling / semantics
                        values = RDouble.RDoubleUtils.asDoubleArray(xd);
                        x = xd;
                    }

                    // as.matrix(x)
                    // TODO: add more semantics
                    int[] dim = x.dimensions();
                    int m;
                    int n;
                    int size = x.size();

                    if (dim == null) {
                        if (size == 0) {
                            throw RError.getInvalidArgument(ast, paramNames[IX]); // FIXME: not an R warning
                        }
                        m = size;
                        n = 1;
                    } else {
                        m = dim[0];
                        n = dim[1];
                    }

                    // check for infinite or missing values
                    for (int i = 0; i < values.length; i++) {
                        if (!RDouble.RDoubleUtils.isFinite(values[i])) {
                            throw RError.getInfiniteMissingValues(ast, paramNames[IX]);
                        }
                    }

                    boolean symmetric;
                    if (provided[ISYMMETRIC]) {
                        symmetric = parseLogicalAsCondition(params[paramPositions[ISYMMETRIC]], ast, paramNames[ISYMMETRIC]);
                    } else {
                        // isSymmetric.matrix, but a much simpler case, as we know we have finite non-na numeric values
                        if (complex) {
                            symmetric = isSymmetricComplex(values, m, n);
                        } else {
                            symmetric = isSymmetricDouble(values, m, n);
                        }
                    }

                    Utils.nyi("FINISH THIS");
                    // ./modules/lapack/Lapack.c

                    // TODO Auto-generated method stub
                    return null;
                }
            };
        }

    };

    public static final double tolerance = 100 * RDouble.EPSILON;

    // see library/base/all.R
    public static boolean isSymmetricDouble(double[] values, int m, int n) {
        if (m != n) {
            return false;
        }

        // xn = mean( abs( target ) ) .... => sumAbsTarget
        int size = values.length;
        double sumAbsTarget = 0;
        for (int i = 0; i < size; i++) {
            sumAbsTarget += Math.abs(values[i]);
        }

        // xy = mean( abs( target - current ) )
        double sumAbsDiff = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double target = values[j * m + i];
                double current = values[i * m + j];
                sumAbsDiff += Math.abs(target - current);
            }
        }

        double meanAbsTarget = sumAbsTarget / size;
        double metric;
        if (RDouble.RDoubleUtils.isFinite(meanAbsTarget) && meanAbsTarget > tolerance) {
            // relative equality check
            metric = sumAbsDiff / sumAbsTarget;
        } else {
            // absolute equality check
            metric = sumAbsDiff;
        }
        return metric <= tolerance;
    }

    // see library/base/all.R
    public static boolean isSymmetricComplex(double[] values, int m, int n) {
        Utils.nyi("complex isSymmetric.matrix");
        return false;
    }
}
