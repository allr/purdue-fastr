package r.builtins;

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

    static final CallFactory _ = new LowerTri("lower.tri", new String[]{"x", "diag"}, new String[]{"x"}, UpperTri.LOWER);

    private LowerTri(String name, String[] params, String[] required, Triangular trian) {
        super(name, params, required, trian);
    }
}
