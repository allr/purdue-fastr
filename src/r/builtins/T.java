package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * "t"
 * 
 * <pre>
 * x -- a matrix or data frame, typically.
 * </pre>
 */
// FIXME: also could do a lazy version using views
// TODO: there is a more efficient, cache-oblivious algorithm, which operates on parts of the original matrix
// TODO: the m*n matrix (m != n) can also be transposed nearly-in-place
final class T extends CallFactory {

    static final CallFactory _ = new T("t", new String[]{"x"}, new String[]{"x"});

    T(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    private static final boolean IN_PLACE = true;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new BuiltIn.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RArray) {
                    RArray a = (RArray) arg;
                    int[] dim = a.dimensions();
                    int size = a.size();
                    if (dim == null || dim.length == 1) {
                        dim = new int[]{1, size};
                        if (!a.isShared()) {
                            return a.setDimensions(dim);
                        } else {
                            return Utils.copyArray(a).setDimensions(dim);
                        }
                    }
                    if (dim.length == 2) {
                        int m = dim[0];
                        int n = dim[1];
                        int[] ndim = new int[]{n, m};
                        if (IN_PLACE && a.isTemporary() && m == n) { // note: a view is not temporary
                            inPlaceRectangular(a, m);
                            return a.setNames(null).setDimensions(ndim);
                        } else {
                            RArray res = Utils.createArray(a, size, null, null, a.attributesRef());
                            for (int i = 0; i < m; i++) {
                                for (int j = 0; j < n; j++) {
                                    res.set(i * n + j, a.getRef(j * m + i));
                                }
                            }
                            return res.setDimensions(ndim);
                        }
                    }
                }
                throw RError.getArgumentNotMatrix(ast);
            }
        };
    }

    public static void inPlaceRectangular(RArray a, int m) {
        for (int i = 0; i < m - 1; i++) {
            for (int j = i + 1; j < m; j++) {
                int first = i * m + j;
                int second = j * m + i;

                Object tmp = a.get(first);
                a.set(first, a.get(second));
                a.set(second, tmp);
            }
        }
    }

    // note: not any faster than inPlaceRectangular
    public static void inPlaceRectangular(DoubleImpl adbl, int m) {
        double[] a = adbl.getContent();
        for (int i = 0; i < m - 1; i++) {
            for (int j = i + 1; j < m; j++) {
                int first = i * m + j;
                int second = j * m + i;

                double tmp = a[first];
                a[first] = a[second];
                a[second] = tmp;
            }
        }
    }
}
