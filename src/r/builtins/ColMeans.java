package r.builtins;

import r.data.*;

final class ColMeans extends ColRowBase {

    static final CallFactory _ = new ColMeans("colMeans");

    @Override double[] stat(RComplex x, int m, int n, boolean naRM) {
        return colSumsMeans(x, m, n, true, naRM);
    }

    @Override double[] stat(RDouble x, int m, int n, boolean naRM) {
        return colSumsMeans(x, m, n, true, naRM);
    }

    @Override double[] stat(RInt x, int m, int n, boolean naRM) {
        return colSumsMeans(x, m, n, true, naRM);
    }

    @Override int[] getResultDimension(int[] sourceDim) {
        int[] result = new int[sourceDim.length - 1];
        System.arraycopy(sourceDim, 1, result, 0, result.length);
        return result;
    }

    private ColMeans(String name) {
        super(name);
    }

}
