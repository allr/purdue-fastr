package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: this is super-slow generic version, can be type-specialized and scalar-specialized, can take advantage of non-shared replacements
public class Diagonal {

    public static final CallFactory REPLACEMENT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn.BuiltIn2(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny x, RAny value) {
                    if (!(x instanceof RArray)) {
                        throw RError.getOnlyMatrixDiagonals(ast);
                    }
                    RArray a = (RArray) x;
                    int[] dim = a.dimensions();
                    if (dim == null || dim.length != 2) {
                        throw RError.getOnlyMatrixDiagonals(ast);
                    }
                    int m = dim[0];
                    int n = dim[1];
                    int rsize = (m < n) ? m : n;

                    RArray typedX;
                    RArray typedValue;

                    if (x instanceof RList || value instanceof RList) {
                        typedX = x.asList();
                        typedValue = value.asList();
                    } else if (x instanceof RString || value instanceof RString) {
                        typedX = x.asString();
                        typedValue = value.asString();
                    } else if (x instanceof RComplex || value instanceof RComplex) {
                        typedX = x.asComplex();
                        typedValue = value.asComplex();
                    } else if (x instanceof RDouble || value instanceof RDouble) {
                        typedX = x.asDouble();
                        typedValue = value.asDouble();
                    } else if (x instanceof RInt || value instanceof RInt) {
                        typedX = x.asInt();
                        typedValue = value.asInt();
                    } else if (x instanceof RLogical || value instanceof RLogical) {
                        typedX = x.asLogical();
                        typedValue = value.asLogical();
                    } else if (x instanceof RRaw && value instanceof RRaw) {
                        typedX = (RRaw) x;
                        typedValue = (RRaw) value;
                    } else {
                        Utils.nyi("unsupported types");
                        return null;
                    }

                    int vsize = typedValue.size();
                    if (vsize != 1 && vsize != rsize) {
                        throw RError.getReplacementDiagonalLength(ast);
                    }

                    RArray res = Utils.createArray(typedX, m * n, dim, null);
                    int vi = 0;

                    for (int i = 0; i < m; i++) {
                        for (int j = 0; j < n; j++) {
                            int xindex = j * m + i;
                            if (i != j) {
                                res.set(xindex, typedX.getRef(xindex));   // TODO: revisit whether ref is needed (note non-local read but local write)
                            } else {
                                res.set(xindex, typedValue.getRef(vi++));
                                if (vi == vsize) {
                                    vi = 0;
                                }
                            }
                        }
                    }
                    return res;
                }
            };
        }
    };

}
