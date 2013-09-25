package r.builtins;

import r.data.*;

/**
 * "lower.tri"
 *
 * <pre>
 * x -- a matrix.
 * diag -- logical. Should the diagonal be included?
 * </pre>
 */
// note: in GNU-R, this is implemented in R
final class LowerTri extends UpperTri {

    static final Triangular LOWER = new Triangular() {

        @Override public RArray triangular(int m, int n, boolean diag) {
            int[] content = new int[m * n];
            int startTakingFrom = diag ? 0 : 1;
            for (int j = 0; j < n; j++) {
                for (int i = startTakingFrom; i < m; i++) {
                    content[j * m + i] = RLogical.TRUE;
                }
                startTakingFrom++;
                // FALSE is the default value
            }
            return RLogical.RLogicalFactory.getFor(content);
        }
    };

    @SuppressWarnings("hiding") static final CallFactory _ = new LowerTri("lower.tri", new String[]{"x", "diag"}, new String[]{"x"}, LOWER);

    private LowerTri(String name, String[] params, String[] required, Triangular trian) {
        super(name, params, required, trian);
    }
}
