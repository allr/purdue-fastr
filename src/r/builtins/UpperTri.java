package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "upper.tri"
 * 
 * <pre>
 * x -- a matrix.
 * diag -- logical. Should the diagonal be included?
 * </pre>
 */
// note: in GNU-R, this is implemented in R
class UpperTri extends CallFactory {

    static final CallFactory _ = new UpperTri("upper.tri", new String[]{"x", "diag"}, new String[]{"x"}, null);

    UpperTri(String name, String[] params, String[] required, Triangular trian) {
        super(name, params, required);
        this.trian = trian == null ? UPPER : trian;
    }

    final Triangular trian;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final boolean xfirst = ia.position("x") == 0;
        if (names.length == 1) { return new BuiltIn.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny argx) {
                return UPPER.triangular(argx, false);
            }
        }; }
        // names.length == 2
        // FIXME check that "diag" is passed
        return new BuiltIn.BuiltIn2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                return xfirst ? trian.triangular(ast, arg0, arg1) : trian.triangular(ast, arg1, arg0);
            }
        };
    }

    public abstract static class Triangular {

        public abstract RArray triangular(int m, int n, boolean diag);

        public RAny triangular(RAny argx, boolean diag) {
            if (!(argx instanceof RArray)) { throw Utils.nyi("unsupported (invalid) argument type"); }// FIXME: this could be faster using node rewriting
            RArray a = (RArray) argx;
            int[] dim = a.dimensions();
            if (dim == null) {
                int m = a.size();
                return triangular(m, 1, diag).setDimensions(new int[]{m, 1});
            }
            if (dim.length == 2) { return triangular(dim[0], dim[1], diag).setDimensions(dim); }
            throw Utils.nyi("unsupported case");
        }

        public RAny triangular(ASTNode ast, RAny argx, RAny argdiag) {
            if (argdiag instanceof RLogical || argdiag instanceof RDouble || argdiag instanceof RInt) { // FIXME: this could be faster using node rewriting
                RArray diag = (RArray) argdiag;
                int size = diag.size();
                if (size == 0) { throw RError.getInvalidArgument(ast, "diag"); }
                if (size > 1) {
                    RContext.warning(ast, RError.LENGTH_GT_1);
                }
                int l = diag.asLogical().getLogical(0);
                if (l != RLogical.NA) { return triangular(argx, l == RLogical.TRUE); }
            }
            throw RError.getInvalidArgument(ast, "diag");
        }
    }

    public static Triangular UPPER = new Triangular() {

        @Override public RArray triangular(int m, int n, boolean diag) {
            int[] content = new int[m * n];
            int takeFromColumn = diag ? 1 : 0;
            for (int j = 0; j < n; j++) {
                for (int i = 0; i < takeFromColumn; i++) {
                    content[j * m + i] = RLogical.TRUE;
                }
                if (takeFromColumn < m) { // FIXME: split into two loops
                    takeFromColumn++;
                }
                // FALSE is the default value
            }
            return RLogical.RLogicalFactory.getFor(content);
        }
    };

    public static Triangular LOWER = new Triangular() {

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
}
