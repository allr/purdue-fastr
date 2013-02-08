package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: also could do a lazy version using views
public class Transpose {

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "x", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
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
                            RArray res = Utils.createArray(a, size);
                            for (int i = 0; i < m; i++) {
                                for (int j = 0; j < n; j++) {
                                    res.set(i * n + j, a.getRef(j * m + i));
                                }
                            }
                            return res.setDimensions(ndim);
                         }
                    }
                    throw RError.getArgumentNotMatrix(ast);
                }
            };
        }
    };

}
