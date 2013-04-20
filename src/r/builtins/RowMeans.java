package r.builtins;

import r.data.*;

/**
 
 */
final class RowMeans extends ColRowBase {

    static final CallFactory _ = new RowMeans("rowMeans");

    @Override double[] stat(RComplex x, int m, int n, boolean naRM) {
        return rowSumsMeans(x, m, n, true, naRM);
    }

    @Override double[] stat(RDouble x, int m, int n, boolean naRM) {
        return rowSumsMeans(x, m, n, true, naRM);
    }

    @Override double[] stat(RInt x, int m, int n, boolean naRM) {
        return rowSumsMeans(x, m, n, true, naRM);
    }

    @Override int[] getResultDimension(int[] sourceDim) {
        return null; // row means results have no dim
    }

    private RowMeans(String name) {
        super(name);
    }

}
