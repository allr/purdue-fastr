package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: also could do a lazy version using views
public class Transpose {

    private static final boolean IN_PLACE = true;

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "x", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {
                @Override
                public RAny doBuiltIn(Frame frame, RAny arg) {
                    if (arg instanceof RArray) {
                        RArray a = (RArray) arg;
                        int[] dim = a.dimensions();
                        int size = a.size();
                        if (dim == null || dim.length == 1) {
                            dim = new int[] {1, size};
                            if (!a.isShared()) {
                                return a.setDimensions(dim);
                            } else {
                                return Utils.copyArray(a).setDimensions(dim);
                            }
                        }
                        if (dim.length == 2) {
                            int m = dim[0];
                            int n = dim[1];
                            int[] ndim = new int[] {n, m};
                            if (IN_PLACE && a.isTemporary() && m == n) {
                                for (int i = 0; i < m - 1; i++) {
                                    for (int j = i + 1; j < m; j++) {
                                        int first = i * m + j;
                                        int second = j * m + i;

                                        Object tmp = a.get(first);
                                        a.set(first, a.get(second));
                                        a.set(second, tmp);
                                    }
                                }
                                return a.setNames(null).setDimensions(ndim);
                            } else {
                                RArray res = Utils.createArray(a, size, null, null, a.attributesRef());
                                for (int i = 0; i < m; i++) {
                                    for (int j = 0; j < n; j++) {
                                        res.set(i * n + j, a.getRef(j * m + i));
                                    }
                                }
                                // this is how it is done in GNU-R, though I would hope (and haven't be proven otherwise) that
                                // the java compiler can get that out of the much more readable code above
//                                int size1 = size - 1;
//                                int i = 0;
//                                int j = 0;
//                                while (i < size) {
//                                    if (j > size1) {
//                                        j -= size1;
//                                    }
//                                    res.set(i, a.getRef(j));
//                                    i++;
//                                    j += m;
//                                }
                                return res.setDimensions(ndim);
                            }
                         }
                    }
                    throw RError.getArgumentNotMatrix(ast);
                }
            };
        }
    };

}
