package r.builtins;

import org.netlib.lapack.*;
import org.netlib.util.*;

import r.*;
import r.data.*;
import r.data.RDouble.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// FIXME: add S3 support
final class Det extends CallFactory {

    static final CallFactory _ = new Det("det", new String[]{"x", "..."}, new String[] {"x"});

    private Det(String name, String[] params, String[] required) {
        super(name, params, required);
    }


    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                // LICENSE: transcribed code from GNU R, which is licensed under GPL

                RAny xarg = args[xPosition];
                if (!(xarg instanceof RArray)) {
                    throw RError.getInvalidArgument(ast, "x"); // TODO: S3 dispatch / failure here
                }
                RArray xa = (RArray) xarg;
                int[] dims = xa.dimensions();
                if (dims == null || dims.length != 2) {
                    throw RError.getInvalidArgument(ast, "x"); // TODO: S3 dispatch / failure here
                }
                int n = dims[0];
                if (n != dims[1]) {
                    throw RError.getMustBeSquare(ast, "x");
                }
                if (xarg instanceof RComplex) {
                    throw RError.getDeterminantComplex(ast);
                }
                RDouble x = Convert.coerceToDoubleError(xa, ast);

                double[] laA = RDoubleUtils.copyAsDoubleArray(x);
                int[] laIPIV = new int[n];
                intW laINFO = new intW(0);

                // SUBROUTINE DGETRF( M, N, A, LDA, IPIV, INFO )
                LAPACK.getInstance().dgetrf(n, n, laA, n, laIPIV, laINFO);
                if (laINFO.val < 0) {
                    throw RError.getLapackError(ast, laINFO.val, "dgetrf");
                }
                if (laINFO.val > 0) {
                    return RDouble.BOXED_ZERO;
                }
                double res = 1;
                for (int i = 0; i < n; i++) {
                    if (laIPIV[i] != i + 1) {
                        res = -res;
                    }
                }
                for (int i = 0; i < n; i++) {
                    res *= laA[i *(n + 1)];
                }
                return RDouble.RDoubleFactory.getScalar(res);
            }
        };
    }

}
