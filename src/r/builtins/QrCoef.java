package r.builtins;

import org.netlib.lapack.*;
import org.netlib.util.*;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class QrCoef extends CallFactory {

    static final CallFactory _ = new QrCoef("qr.coef", new String[]{"qr", "y"}, null);

    private QrCoef(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseUseLapack(RAny.Attributes attr, ASTNode ast) {
        if (attr == null) { return false; }
        RAny v = attr.map().get(useLAPACKSymbol);
        if (v != null && v instanceof RLogical) {
            return parseUncheckedLogical(v, ast);
        } else {
            return false;
        }
    }

    static final RSymbol pivotSymbol = RSymbol.getSymbol("pivot");
    static final RSymbol qrauxSymbol = RSymbol.getSymbol("qraux");
    static final RSymbol qrSymbol = RSymbol.getSymbol("qr");
    static final RSymbol rankSymbol = RSymbol.getSymbol("rank");
    static final RSymbol useLAPACKSymbol = RSymbol.getSymbol("useLAPACK");

    public static RAny coef(RAny qrArg, RAny yArg, ASTNode ast) {
        // LICENSE: transcribed code from GNU R, which is licensed under GPL

        // TODO: check qrArg is of class "qr"
        if (!(qrArg instanceof RList)) { throw RError.getFirstQR(ast); }
        RList qr = (RList) qrArg;
        RArray.Names qrNames = qr.names();

        // NOTE: here we are very permissive on "qr", we accept any order of the elements of the qr list
        // so does the GNU-R code written in R, however, the LAPACK code it calls to then assumes a fixed
        // ordering of the elements --- we should decide how permissive we want to be in S3
        int pivotIndex = qrNames.map(pivotSymbol);
        int qrauxIndex = qrNames.map(qrauxSymbol);
        int qrIndex = qrNames.map(qrSymbol);
        int rankIndex = qrNames.map(rankSymbol);
        if (pivotIndex == -1 || qrauxIndex == -1 || qrIndex == -1 || rankIndex == -1) { throw RError.getFirstQR(ast); // not exactly R error
        }

        RAny qrqrArg = qr.getRAny(qrIndex);
        if (!(qrqrArg instanceof RArray)) { throw RError.getFirstQR(ast); // not exactly R error
        }
        RArray qrqr = (RArray) qrqrArg;
        int[] dims = qrqr.dimensions();
        if (dims == null || dims.length != 2) { throw RError.getFirstQR(ast); // not exactly R error
        }
        int n = dims[0];
        int p = dims[1];
        RAny qrRankArg = qr.getRAny(rankIndex);
        RInt qrRank = qrRankArg.asInt();
        if (qrRank.size() < 1) { throw RError.getFirstQR(ast); // not exactly R error
        }
        int rank = qrRank.getInt(0);
        if (rank == RInt.NA) { throw RError.getFirstQR(ast); // not exactly R error
        }

        if (!(yArg instanceof RArray)) { throw RError.getInvalidArgument(ast, "y"); // FIXME: not an R error message, GNU-R uses implicit error in R
        }
        RArray y = (RArray) yArg;
        int[] ydims = y.dimensions();
        boolean yWasMatrix = (ydims != null) && (ydims.length == 2);
        int ny;
        if (!yWasMatrix) {
            y = AsMatrix.castToMatrix(y);
            ny = 1;
        } else {
            ny = ydims[1];
        }

        if (p == 0) {
            if (yWasMatrix) {
                return RDouble.RDoubleFactory.getUninitializedArray(p * ny, new int[]{p, ny}, null, null);
            } else {
                return RDouble.EMPTY;
            }
        }
        if (qrqr instanceof RComplex) {
            Utils.nyi("LAPACK routines zunmgr, ztrtrs not supported by netlib-java");
            return null;
        }

        RDouble dy = Convert.coerceToDoubleError(y, ast).materialize();
        if (!y.isTemporary()) {
            dy = RDouble.RDoubleFactory.copy(dy);
        }
        RDouble qrAux = qr.getRAny(qrauxIndex).asDouble();
        RInt qrPivot = qr.getRAny(pivotIndex).asInt();
        RDouble qrqrDouble = Convert.coerceToDoubleError(qrqr, ast);

        boolean useLAPACK = parseUseLapack(qr.attributes(), ast);
        if (useLAPACK) {
            double[] laA = qrqrDouble.getContent();
            if (n != y.dimensions()[0]) { throw RError.getRHSShouldHaveRows(ast, n, y.dimensions()[0]); }
            double[] laTAU = qrAux.getContent();
            int laK = laTAU.length;
            double[] laC = dy.getContent();
            double[] laWORK = new double[1];
            intW laINFO = new intW(0);

            // SUBROUTINE DORMQR( SIDE, TRANS, M, N, K, A, LDA, TAU, C, LDC, WORK, LWORK, INFO )
            LAPACK.getInstance().dormqr("L", "T", n, ny, laK, laA, n, laTAU, laC, n, laWORK, -1, laINFO);
            if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dormqr"); }
            int laLWORK = (int) laWORK[0];
            laWORK = new double[laLWORK];
            LAPACK.getInstance().dormqr("L", "T", n, ny, laK, laA, n, laTAU, laC, n, laWORK, laLWORK, laINFO);
            if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dormqr"); }

            //  SUBROUTINE DTRTRS( UPLO, TRANS, DIAG, N, NRHS, A, LDA, B, LDB, INFO )
            LAPACK.getInstance().dtrtrs("U", "N", "N", laK, ny, laA, n, laC, n, laINFO);
            if (laINFO.val != 0) { throw RError.getLapackError(ast, laINFO.val, "dtrtrs"); }

            // laC has dimensions n * ny
            // ix has length p
            // we need to return a matrix p * ny

            double[] coef = new double[p * ny];
            int minnp = n < p ? n : p;

            for (int i = 0; i < minnp; i++) { // could probably exchange the loops, but this is unlikely performance critical here
                int tgtRow = qrPivot.getInt(i) - 1;

                for (int j = 0; j < ny; j++) {
                    coef[j * p + tgtRow] = laC[j * n + i];
                }
            }
            for (int i = minnp; i < p; i++) {
                int tgtRow = qrPivot.getInt(i) - 1;
                for (int j = 0; j < ny; j++) {
                    coef[j * p + tgtRow] = RDouble.NA;
                }
            }

            if (yWasMatrix) {
                return RDouble.RDoubleFactory.getFor(coef, new int[]{p, ny}, null);
            } else {
                return RDouble.RDoubleFactory.getFor(coef);
            }
        }
        // LINPACK

        if (rank == 0) {
            // NOTE: GNU-R returns a logical matrix/vector, but that is unlikely intentional
            if (yWasMatrix) {
                return RDouble.RDoubleFactory.getNAArray(p * ny, new int[]{p, ny});
            } else {
                return RDouble.RDoubleFactory.getNAArray(p);
            }
        }
        if (n != y.dimensions()[0]) { throw RError.getSameNumberRows(ast, "qr", "y"); }

        double[] raX = qrqrDouble.getContent();
        double[] raB = new double[rank * ny];
        int[] info = new int[1];
        GNUR.dqrcf(raX, n, rank, qrAux.getContent(), dy.getContent(), ny, raB, info);
        if (info[0] != 0) { throw RError.getExactSingularity(ast, "qr.coef"); }
        double[] coef;
        int[] coefDims = null;
        if (rank < p) {
            coef = new double[p * ny];
            for (int i = 0; i < rank; i++) { // could probably exchange the loops, but this is unlikely performance critical here
                int tgtRow = qrPivot.getInt(i) - 1;

                for (int j = 0; j < ny; j++) {
                    coef[j * p + tgtRow] = raB[j * n + i];
                }
            }
            for (int i = rank; i < p; i++) {
                int tgtRow = qrPivot.getInt(i) - 1;
                for (int j = 0; j < ny; j++) {
                    coef[j * p + tgtRow] = RDouble.NA;
                }
            }
            if (yWasMatrix) {
                coefDims = new int[]{p, ny};
            }
        } else {
            coef = raB;
            if (yWasMatrix) {
                coefDims = new int[]{rank, ny};
            }
        }
        // TODO: rownames, colnames of coef
        return RDouble.RDoubleFactory.getFor(coef, coefDims, null);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int qrPosition = ia.position("qr");
        final int yPosition = ia.position("y");

        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny qrArg = args[qrPosition];
                RAny yArg = args[yPosition];
                return coef(qrArg, yArg, ast);
            }
        };
    }

}
