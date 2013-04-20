package r.builtins;

import java.util.*;

import org.netlib.lapack.*;
import org.netlib.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.Truffle.*;
import java.lang.Integer;
import java.lang.Double;

// NOTE: GNU-R has some of the eigen code implemented in R (and some uses LAPACK)
final class Eigen extends CallFactory {
    // LICENSE: transcribed code from GNU R, which is licensed under GPL

    static final CallFactory _ = new Eigen("eigen", new String[]{"x", "symmetric", "only.values", "EISPACK"}, new String[]{"x"});

    private Eigen(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseLogical(RAny arg, ASTNode ast, String argName) {
        // FIXME: not exactly R semantics, R would ignore non-coerceable values at indexes 2 and higher
        // FIXME: not exactly R error messages

        RLogical larg = arg.asLogical();
        if (larg.size() >= 1) {
            int l = larg.getLogical(0);
            if (l == RLogical.TRUE) { return true; }
            if (l == RLogical.FALSE) { return false; }
        }
        throw RError.getInvalidArgument(ast, argName);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("EISPACK")) { throw Utils.nyi("EISPACK argument not supported"); }
        final int posX = ia.position("x");
        final int posSymmetric = ia.position("symmetric");
        final int posOnlyvalue = ia.position("only.values");
        final RArray.Names resultNames = RArray.Names.create(RSymbol.getSymbols(new String[]{"values", "vectors"}));

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                RAny xany = params[posX];
                RArray x;
                boolean complex = false;
                double[] values;
                if (xany instanceof RDouble) {
                    x = (RDouble) xany;
                    values = RDouble.RDoubleUtils.copyAsDoubleArray((RDouble) xany);
                } else if (xany instanceof RComplex) {
                    complex = true;
                    x = (RComplex) xany;
                    values = RComplex.RComplexUtils.asDoubleArray((RComplex) xany);
                } else {
                    RDouble xd = Convert.coerceToDoubleError(xany, ast); // FIXME: not exactly R error handling / semantics
                    values = RDouble.RDoubleUtils.copyAsDoubleArray(xd); // note: could copy and coerce in one step
                    x = xd;
                }
                // as.matrix(x)
                // TODO: add more semantics
                int[] dim = x.dimensions();
                int[] dimNN;
                int n;
                int size = x.size();
                if (dim == null || dim.length != 2) {
                    if (size == 0) { throw RError.getInvalidArgument(ast, "x"); }// FIXME: not an R warning
                    if (size != 1) { throw RError.getNonSquareMatrix(ast, "eigen"); }
                    n = 1;
                    dimNN = RArray.SCALAR_DIMENSIONS;
                } else {
                    n = dim[0];
                    if (dim[1] != n) { throw RError.getNonSquareMatrix(ast, "x"); }
                    dimNN = dim;
                }
                // check for infinite or missing values
                for (int i = 0; i < values.length; i++) {
                    if (!RDouble.RDoubleUtils.isFinite(values[i])) { throw RError.getInfiniteMissingValues(ast, "x"); }
                }
                boolean symmetric;
                if (posSymmetric != -1) {
                    symmetric = parseLogical(params[posSymmetric], ast, "symmetric");
                } else { // isSymmetric.matrix, but a much simpler case, as we know we have finite non-na numeric values
                    symmetric = complex ? isSymmetricComplex(values, n) : isSymmetricDouble(values, n);
                }

                boolean onlyValues = posOnlyvalue != -1 ? parseLogical(params[posOnlyvalue], ast, "only.values") : false;

                RAny resValues = RNull.getNull(); // TODO: init not needed when done with impl below
                RAny resVectors = RNull.getNull();
                // ./src/modules/lapack/Lapack.c
                if (!complex) {
                    if (symmetric) {
                        // symmetric real input matrix
                        String laJOBZ;
                        double[] laZ;
                        if (onlyValues) {
                            laJOBZ = "N";
                            laZ = null;
                        } else {
                            laJOBZ = "V";
                            laZ = new double[size];
                        }
                        int[] laISUPPZ = new int[2 * n];
                        intW laM = new intW(0);
                        double[] laW = new double[n];
                        intW laINFO = new intW(0);
                        // get optimum sizes for the work arrays
                        double[] laWORK = new double[1];
                        int[] laIWORK = new int[1];

                        //  SUBROUTINE DSYEVR( JOBZ, RANGE, UPLO, N, A, LDA, VL, VU, IL, IU,
                        //                          ABSTOL, M, W, Z, LDZ, ISUPPZ, WORK, LWORK,
                        //                          IWORK, LIWORK, INFO )

                        LAPACK.getInstance().dsyevr(laJOBZ, "A", "L", n, values, n, 0, 0, 0, 0, 0, laM, laW, laZ, n, laISUPPZ, laWORK, -1, laIWORK, -1, laINFO);
                        if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dsyevr"); }
                        int laLWORK = (int) laWORK[0];
                        int laLIWORK = laIWORK[0];
                        // do the real work
                        laWORK = new double[laLWORK];
                        laIWORK = new int[laLIWORK];
                        LAPACK.getInstance().dsyevr(laJOBZ, "A", "L", n, values, n, 0, 0, 0, 0, 0, laM, laW, laZ, n, laISUPPZ, laWORK, laLWORK, laIWORK, laLIWORK, laINFO);
                        if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dsyevr"); }
                        resValues = RDouble.RDoubleFactory.getFor(Utils.reverse(laW));
                        if (!onlyValues) {
                            resVectors = RDouble.RDoubleFactory.getFor(reverseColumns(laZ, n, n), dimNN, null);
                        }

                    } else {
                        // general real input matrix
                        String laJOBVR;
                        double[] laVR;
                        if (onlyValues) {
                            laJOBVR = "N";
                            laVR = null;
                        } else {
                            laJOBVR = "V";
                            laVR = new double[size];
                        }
                        double[] laWR = new double[n];
                        double[] laWI = new double[n];
                        intW laINFO = new intW(0);
                        // get optimum size for the work array
                        double[] laWORK = new double[1];

                        //       SUBROUTINE DGEEV( JOBVL, JOBVR, N, A, LDA, WR, WI, VL, LDVL, VR,
                        //                                LDVR, WORK, LWORK, INFO )
                        LAPACK.getInstance().dgeev("N", laJOBVR, n, values, n, laWR, laWI, null, n, laVR, n, laWORK, -1, laINFO);
                        if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dgeev"); }
                        int laLWORK = (int) laWORK[0];

                        // do the real work
                        laWORK = new double[laLWORK];
                        LAPACK.getInstance().dgeev("N", laJOBVR, n, values, n, laWR, laWI, null, n, laVR, n, laWORK, laLWORK, laINFO);
                        if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dgeev"); }

                        // check if the imaginary parts of the results are negligible
                        boolean returnComplex = false;
                        for (int i = 0; i < n; i++) {
                            if (Math.abs(laWI[i]) > 10 * RDouble.EPSILON * Math.abs(laWR[i])) { // R_AccuracyInfo.eps
                                returnComplex = true;
                                break;
                            }
                        }

                        Integer[] order;
                        if (returnComplex) {
                            order = decreasingModOrder(laWR, laWI, n);
                            resValues = RComplex.RComplexFactory.getArray(reorder(laWR, order, n), reorder(laWI, order, n), null);

                            if (!onlyValues) {
                                resVectors = RComplex.RComplexFactory.getFor(unscrambleComplexEigenVectors(laWI, laVR, n), dimNN, null);
                            }
                        } else {
                            order = decreasingAbsOrder(laWR, n);
                            resValues = RDouble.RDoubleFactory.getFor(reorder(laWR, order, n));
                            if (!onlyValues) {
                                resVectors = RDouble.RDoubleFactory.getMatrixFor(reorderColumns(laVR, order, n, n), n, n);
                            }
                        }
                    }

                } else { // TODO: port JLAPACK to the new LAPACK or find another library
                    if (symmetric) { // symmetric complex input matrix
                        throw Utils.nyi("ZHEEV not supported by netlib-java");
                    } else { // general complex input matrix.
                        throw Utils.nyi("ZGEEV not supported by netlib-java");
                    }
                }
                return RList.RListFactory.getFor(new RAny[]{resValues, resVectors}, null, resultNames);
            }
        };
    }

    public static final double tolerance = 100 * RDouble.EPSILON;

    // see ./src/library/base/R/eigen.R
    public static boolean isSymmetricDouble(double[] values, int n) {
        assert Utils.check(values.length == n * n);
        // TODO: attributes

        // xn = mean( abs( target ) ) .... => sumAbsTarget
        int size = values.length;
        double sumAbsTarget = 0;
        for (int i = 0; i < size; i++) {
            sumAbsTarget += Math.abs(values[i]);
        }

        // xy = mean( abs( target - current ) )
        double sumAbsDiff = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double target = values[j * n + i];
                double current = values[i * n + j];
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
    public static boolean isSymmetricComplex(double[] values, int n) {
        assert Utils.check(values.length == n * n * 2);
        // TODO: attributes

        // xn = mean( abs( target ) ) .... => sumAbsTarget
        int size = values.length;
        double sumAbsTarget = 0;
        int i = 0;
        while (i < size) {
            double real = values[i++];
            double imag = values[i++];

            sumAbsTarget += Math.sqrt(real * real + imag * imag);
        }

        // xy = mean( abs( target - current ) )
        double sumAbsDiff = 0;
        for (i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int targetIndex = j * n + i;
                int currentIndex = i * n + j;

                double real = values[2 * targetIndex] - values[2 * currentIndex];
                double imag = values[2 * targetIndex + 1] + values[2 * currentIndex + 1];

                sumAbsDiff += Math.sqrt(real * real + imag * imag);
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

    public static double[] unscrambleComplexEigenVectors(double[] valuesImag, double[] scrambledVectors, int n) {
        assert Utils.check(valuesImag.length == n);
        assert Utils.check(scrambledVectors.length == n * n);

        int rawsize = 2 * n * n;
        double[] content = new double[rawsize];

        int j = 0;
        while (j < n) {
            if (valuesImag[j] != 0) {
                for (int i = 0; i < n; i++) {
                    int offset = j * n + i;
                    int nextOffset = offset + n; // next column

                    double real = scrambledVectors[offset];
                    content[2 * offset] = real;
                    content[2 * nextOffset] = real;

                    double imag = scrambledVectors[nextOffset];
                    content[2 * offset + 1] = imag;
                    content[2 * nextOffset + 1] = -imag;
                }
                j += 2;
            } else {
                for (int i = 0; i < n; i++) {
                    int offset = j * n + i;
                    content[2 * offset] = scrambledVectors[offset]; // real part
                    // imaginary part is left zero
                }
                j++;
            }
        }
        return content;
    }

    // reverse the order of columns of a real matrix
    public static double[] reverseColumns(double[] x, int m, int n) {
        assert Utils.check(x.length == m * n);

        for (int j = 0; j < n / 2; j++) {
            int colOffset1 = j * m;
            int colOffset2 = (n - 1 - j) * m;

            for (int i = 0; i < m; i++) {
                double tmp = x[i + colOffset1];
                x[i + colOffset1] = x[i + colOffset2];
                x[i + colOffset2] = tmp;
            }
        }
        return x;
    }

    // return the order of complex numbers, by their decreasing modulus
    // the order is 0-based
    //
    // FIXME: could implement a special sorting method to avoid Java boxing
    public static Integer[] decreasingModOrder(double[] real, double[] imag, int n) {
        assert Utils.check(real.length == n);
        assert Utils.check(imag.length == n);

        final double[] mod = new double[n];

        for (int i = 0; i < n; i++) {
            double re = real[i];
            double im = imag[i];
            mod[i] = re * re + im * im; // note, ignoring the square root as it won't change the order
        }

        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }

        Arrays.sort(order, new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                return Double.compare(mod[o2], mod[o1]);
            }

        });
        return order;
    }

    // return the order of real numbers, by their decreasing absolute value
    // the order is 0-based
    //
    // FIXME: could implement a special sorting method to avoid Java boxing
    public static Integer[] decreasingAbsOrder(final double[] real, int n) {
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }

        Arrays.sort(order, new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                return Double.compare(Math.abs(real[o2]), Math.abs(real[o1]));
            }

        });
        return order; // ? faster than creating an array of absolute values?
    }

    // reorder the elements of x to be in the given order
    public static double[] reorder(double[] x, Integer[] order, int n) {
        assert Utils.check(x.length == n);
        assert Utils.check(order.length == n);

        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[order[i]] = x[i];
        }
        return res;
    }

    // reorder the columns of matrix x to be in the given order
    public static double[] reorderColumns(double[] x, Integer[] order, int m, int n) {
        assert Utils.check(x.length == m * n);
        assert Utils.check(order.length == m);

        double[] res = new double[m * n];
        for (int j = 0; j < m; j++) {
            int srcOffset = j * m;
            int dstOffset = order[j] * m;

            System.arraycopy(x, srcOffset, res, dstOffset, n);
        }
        return res;
    }
}
