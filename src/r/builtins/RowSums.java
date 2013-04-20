package r.builtins;

import r.data.*;

final class RowSums extends ColRowBase {

    static final CallFactory _ = new RowSums("rowSums");

    @Override double[] stat(RComplex x, int m, int n, boolean naRM) {
        return rowSumsMeans(x, m, n, false, naRM);
    }

    @Override double[] stat(RDouble x, int m, int n, boolean naRM) {
        return rowSumsMeans(x, m, n, false, naRM);
    }

    @Override double[] stat(RInt x, int m, int n, boolean naRM) {
        return rowSumsMeans(x, m, n, false, naRM);
    }

    @Override int[] getResultDimension(int[] sourceDim) {
        return null; // row sum results have no dim
    }

    private RowSums(String name) {
        super(name);
    }

}
