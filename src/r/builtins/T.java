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
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RArray) {
                    RArray a = (RArray) arg;
                    int[] dim = a.dimensions();
                    int size = a.size();
                    if (dim == null || dim.length == 1) {
                        dim = new int[]{1, size};
                        if (a.isTemporary()) {
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
                            inPlaceSquare(a, m);
                            return a.setNames(null).setDimensions(ndim);
                        } else {
                            RArray res = Utils.createArray(a, size, null, null, a.attributesRef());

// naive version is slower than the R version below
//                            for (int i = 0; i < m; i++) {
//                                for (int j = 0; j < n; j++) {
//                                    res.set(i * n + j, a.getRef(j * m + i));
//                                }
//                            }
                            if (n > 1000 && m == n) {  // FIXME: a tuning parameter
                                square(a, n, res);
                                return res.setDimensions(ndim);
                            }
                            int j = 0;  // LICENSE: transcribed from GNU-R, which is licensed under GPL
                            int size1 = size - 1;
                            for (int i = 0; i < size ; i++) {
                                res.set(i, a.getRef(j));
                                j += m;
                                if (j > size1) {
                                    j -= size1;
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

    public static void inPlaceSquare(RArray a, int m) {
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
    public static void inPlaceSquare(DoubleImpl adbl, int m) {
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

    public static void square(RArray a, int n, RArray res) {

        final int delta = n > 1000 ? 40 : 0; // FIXME: the choice of 40 is probably quite fragile wrt to performance
                                             // should better do some auto-tuning or a self-oblivious algorithm
        final int size = n * n;
        final int size1 = size - 1;
        int i = 0;
        int j = 0;

        // will be transposing by blocks delta x delta
        // reading a by rows, writing to a by columns, block-wise
        // i is offset into res, j is offset into a
        final int blocksPerSide = delta > 0 ? n / delta : 0;
        final int epsilon = n - blocksPerSide * delta;

        final int iadvance1 = n - delta;
        final int jadvance1 = - ((delta * n) - 1);
        final int iadvance2 = - (delta * (n - 1));
        final int jadvance2 = delta * (n - 1);
        final int iadvance3 = (delta - 1) * n + epsilon;
        final int jadvance3 = -((n - epsilon) * n - delta);

        for (int bcol = 0; bcol < blocksPerSide; bcol++) {
            for (int brow = 0; brow < blocksPerSide; brow++) {
                for (int icol = 0; icol < delta; icol++) {
                    for (int irow = 0; irow < delta; irow++) {
                        res.set(i, a.getRef(j));
                        i ++;
                        j += n;
                    }
                    i += iadvance1;
                    j += jadvance1;
                }
                i += iadvance2;
                j += jadvance2;
            }
            i += iadvance3;
            j += jadvance3;
        }


        // transponse the remainder of the matrix

        if (epsilon > 0) {
            // transpose the lower remainder (epsilon * n) into the right remainder (n * epsilon)
            i = (n - epsilon) * n;
            j = n - epsilon;
            for (; i < size; i++) {
                res.set(i, a.getRef(j));
                j += n;
                if (j > size1) {
                    j -= size1;
                }
            }

            // transpose the upper-right remainder ( (n-epsilon) * epsilon) into the bottom-left remainder (epsilon * (n-epsilon))
            i = n - epsilon;
            j = (n - epsilon) * n;
            final int iend = j;
            final int rsize1 = n * epsilon - 1;
            final int rowoffset = n - epsilon;
            for (;i < iend;) {
                res.set(i, a.getRef(j));
                j += n;
                i ++;
                if (j > size1) {
                    j -= rsize1;
                    i += rowoffset;
                }
            }
        }
    }
}
