package r.builtins;

import r.data.*;

/**
 * "rowMeans"
 * 
 * <pre>
 * x -- an array of two or more dimensions, containing numeric, complex, integer or logical values, or a numeric data frame.
 * na.rm -- logical. Should missing values (including NaN) be omitted from the calculations?
 * dims -- integer: Which dimensions are regarded as rows or columns to sum over. For row*, the sum or mean is over 
 *         dimensions dims+1, ...; for col* it is over dimensions 1:dims.
 * </pre>
 */
final class RowMeans extends ColRowBase {

    static final CallFactory _ = new RowMeans("rowMeans", new Stats() {
        @Override public double[] stat(RComplex x, int m, int n, boolean naRM) {
            return rowSumsMeans(x, m, n, true, naRM);
        }

        @Override public double[] stat(RDouble x, int m, int n, boolean naRM) {
            return rowSumsMeans(x, m, n, true, naRM);
        }

        @Override public double[] stat(RInt x, int m, int n, boolean naRM) {
            return rowSumsMeans(x, m, n, true, naRM);
        }

        @Override public int[] getResultDimension(int[] sourceDim) {
            return null; // row means results have no dim
        }
    });

    private RowMeans(String name, ColRowBase.Stats stats) {
        super(name, stats);
    }

}
