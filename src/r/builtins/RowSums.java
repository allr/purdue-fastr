package r.builtins;

import r.data.*;

/**
 * "rowSums"
 * 
 * <pre>
 * x -- an array of two or more dimensions, containing numeric, complex, integer or logical values, or a numeric data frame.
 * na.rm -- logical. Should missing values (including NaN) be omitted from the calculations?
 * dims -- integer: Which dimensions are regarded as rows or columns to sum over. For row*, the sum or mean is over 
 *         dimensions dims+1, ...; for col* it is over dimensions 1:dims.
 * </pre>
 */
final class RowSums extends ColRowBase {

    static final CallFactory _ = new RowSums("rowSums", new Stats() {
        @Override public double[] stat(RComplex x, int m, int n, boolean naRM) {
            return rowSumsMeans(x, m, n, false, naRM);
        }

        @Override public double[] stat(RDouble x, int m, int n, boolean naRM) {
            return rowSumsMeans(x, m, n, false, naRM);
        }

        @Override public double[] stat(RInt x, int m, int n, boolean naRM) {
            return rowSumsMeans(x, m, n, false, naRM);
        }

        @Override public int[] getResultDimension(int[] sourceDim) {
            return null; // row sum results have no dim
        }
    });

    private RowSums(String name, ColRowBase.Stats stats) {
        super(name, stats);
    }

}
