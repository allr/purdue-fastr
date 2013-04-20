package r.builtins;

import r.data.*;

final class ColSums extends ColRowBase {

    static final CallFactory _ = new ColSums("colSums");

    @Override double[] stat(RComplex x, int m, int n, boolean naRM) {
        return colSumsMeans(x, m, n, false, naRM);
    }

    @Override double[] stat(RDouble x, int m, int n, boolean naRM) {
        return colSumsMeans(x, m, n, false, naRM);
    }

    @Override double[] stat(RInt x, int m, int n, boolean naRM) {
        return colSumsMeans(x, m, n, false, naRM);
    }

    @Override int[] getResultDimension(int[] sourceDim) {
        int[] result = new int[sourceDim.length - 1];
        System.arraycopy(sourceDim, 1, result, 0, result.length);
        return result;
    }

    private ColSums(String name) {
        super(name);
    }

}
