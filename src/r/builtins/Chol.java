package r.builtins;

import org.netlib.lapack.*;
import org.netlib.util.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.RDouble.RDoubleFactory;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: add S3
// TODO: add pivoting with LAPACK and add LINPACK
final class Chol extends CallFactory {

    static final CallFactory _ = new Chol("chol", new String[]{"x", "pivot", "LINPACK"}, new String[] {"x"});

    private Chol(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        final int pivotPosition = ia.position("pivot");
        final int linpackPosition = ia.position("LINPACK");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny xarg = args[xPosition];
                RDouble x;
                int n;
                int[] dims;
                if (xarg instanceof RDouble || xarg instanceof RInt) {
                    x = xarg.asDouble().materialize();
                    if (!x.isTemporary()) {
                        x = RDoubleFactory.copy(x);
                    }
                    dims = x.dimensions();
                    if (dims != null && dims.length == 2) {
                        n = dims[0];
                        if (n != dims[1]) {
                            throw RError.getNonSquareMatrix(ast, "chol");
                        }
                    } else {
                        n = x.size();
                        if (n != 1) {
                            throw RError.getNonMatrix(ast, "chol");
                        }
                    }
                } else if (xarg instanceof RComplex) {
                    throw RError.getComplexNotPermitted(ast);
                } else {
                    throw RError.getNonNumericArgumentTo(ast, "chol");
                }

                boolean pivot = pivotPosition == -1 ? false : parseUncheckedLogical(args[pivotPosition], ast);
                boolean linpack = linpackPosition == -1 ? pivot : parseUncheckedLogical(args[linpackPosition], ast);

                if (!linpack) {
                    // lapack without pivoting
                    if (n == 0) {
                        throw RError.getDimsGTZero(ast, "x"); // NOTE: GNU-R says "a" because this is down in LAPACK wrapper
                    }
                    if (dims == null) {
                        if (x instanceof ScalarDoubleImpl) {  // FIXME: should this go to RDouble?
                            x = new DoubleImpl(new double[] { x.getDouble(0) }, RArray.SCALAR_DIMENSIONS, null, null, false);
                        } else {
                            x.setDimensions(RArray.SCALAR_DIMENSIONS); // see above we have our private copy
                        }
                    }
                    double[] res = x.getContent();
                    for (int j = 0; j < n; j++) {
                        for (int i = j + 1; i < n; i++) {
                            res[i + j * n] = 0;
                        }
                    }
                    intW laINFO = new intW(0);
                    if (!pivot) {
                        // SUBROUTINE DPOTRF( UPLO, N, A, LDA, INFO )
                        LAPACK.getInstance().dpotrf("U", n, res, n, laINFO);
                        if (laINFO.val != 0) {
                            if (laINFO.val > 0) {
                                throw RError.getNotPositiveDefinite(ast, laINFO.val);
                            } else {
                                throw RError.getLapackInvalidValue(ast, laINFO.val, "dportf");
                            }
                        }
                        return x;
                    } else {
                        Utils.nyi("pivoting not implemented - need lapack routine dpstrf which is not available in netlib-java");
                        return null;
                    }
                }
                Utils.nyi("LINPACK version not implemented");
                return null;
            }
        };
    }

}
