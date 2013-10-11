package r.builtins;

import org.netlib.lapack.*;
import org.netlib.util.*;

import r.*;
import r.data.*;
import r.data.RDouble.*;
import r.errors.*;
import r.ext.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// TODO: S3
public class Qr extends CallFactory {
    // LICENSE: transcribed code from GNU R, which is licensed under GPL

    static final CallFactory _ = new Qr("qr", new String[]{"x", "tol", "LAPACK"}, new String[] {"x"});

    private Qr(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static RArray.Names resultNames =  RArray.Names.create(RSymbol.getSymbols(new String[] {"qr", "rank", "qraux", "pivot"}));
    static RAny.Attributes useLAPACKAttr = RAny.Attributes.createAndPut("useLAPACK", RLogical.BOXED_TRUE);

    public static double parseTol(RAny arg, ASTNode ast) {
        RDouble d = Convert.coerceToDoubleError(arg, ast);
        if (d.size() != 1) {
            throw RError.getInvalidArgument(ast, "tol"); // FIXME: not an R error message, R passes the arg without checking to Fortran code
        }
        return d.getDouble(0);
    }

    public static RAny qr(RAny xArg, RAny tolArg, RAny lapackArg, ASTNode ast) {
        if (xArg instanceof RComplex) {
            Utils.nyi("ZGEQP3 not supported by netlib");
            return null;
        }
        RDouble x = Convert.coerceToDoubleError(xArg, ast).materialize();
        if (!x.isTemporary()) {
            x = RDoubleFactory.copy(x);
        }
        int[] dims = x.dimensions();
        if (dims == null || dims.length != 2) {
            int size = x.size();
            int[] ndims = new int[] {size, 1};
            x = (RDouble) x.setDimensions(ndims); // note: x can be a scalar
            dims = ndims;
        }
        // x is now a (private) matrix
        int m = dims[0];
        int n = dims[1];
        boolean lapack = lapackArg == null ? false : parseUncheckedLogical(lapackArg, ast);
        if (lapack) {
            int[] laJPVT = new int[n];
            int rank = m < n ? m : n;
            double[] laTAU = new double[rank];
            double[] laA = x.getContent();
            double[] laWORK = new double[1];
            intW laINFO = new intW(0);
            // SUBROUTINE DGEQP3( M, N, A, LDA, JPVT, TAU, WORK, LWORK, INFO )
            LAPACK.getInstance().dgeqp3(m, n, laA, m, laJPVT, laTAU, laWORK, -1, laINFO);
            if (laINFO.val < 0) {
                throw RError.getLapackError(ast, laINFO.val, "dgeqp3");
            }
            int laLWORK = (int) laWORK[0];
            laWORK = new double[laLWORK];
            LAPACK.getInstance().dgeqp3(m, n, laA, m, laJPVT, laTAU, laWORK, laLWORK, laINFO);
            if (laINFO.val < 0) {
                throw RError.getLapackError(ast, laINFO.val, "dgeqp3");
            }
            RAny[] content = new RAny[] { x, RInt.RIntFactory.getScalar(rank),
                    RDouble.RDoubleFactory.getFor(laTAU), RInt.RIntFactory.getFor(laJPVT) };
            return RList.RListFactory.getFor(content, null, resultNames, useLAPACKAttr); // TODO: class "qr"
        }

        // LINPACK version
        double tol = tolArg == null ? 1e-7 : parseTol(tolArg, ast);

        double[] raX = x.getContent();
        int[] raK = new int[1];
        double[] raQRAUX = new double[n];
        int[] raJPVT = new int[n];
        for (int i = 0; i < n; i++) {
            raJPVT[i] = i + 1;
        }
        double[] raWORK = new double[2 * n];
        GNUR.dqrdc2(raX, m, m, n, tol, raK, raQRAUX, raJPVT, raWORK);
        RAny[] content = new RAny[] { x, RInt.RIntFactory.getScalar(raK[0]),
                RDouble.RDoubleFactory.getFor(raQRAUX), RInt.RIntFactory.getFor(raJPVT) };
        // TODO: update colnames (permutation by pivot)
        return RList.RListFactory.getFor(content, null, resultNames); // TODO: class "qr"
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        final int tolPosition = ia.position("tol");
        final int lapackPosition = ia.position("LAPACK");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return qr(args[xPosition], tolPosition == -1 ? null : args[tolPosition], lapackPosition == -1 ? null : args[lapackPosition], ast);
            }
        };
    }

}
