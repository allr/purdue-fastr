package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class QrSolve extends CallFactory {

    static final CallFactory _ = new QrSolve("qr.solve", new String[]{"a", "b", "tol"}, new String[]{"a"});

    private QrSolve(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int aPosition = ia.position("a");
        final int bPosition = ia.position("b");
        final int tolPosition = ia.position("tol");

        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                // LICENSE: transcribed code from GNU R, which is licensed under GPL

                RAny aArg = args[aPosition];

                // TODO: call "qr" whenever aArg is not of class "qr"
                RList a;
                if (!(aArg instanceof RList)) {
                    a = (RList) Qr.qr(aArg, tolPosition == -1 ? null : args[tolPosition], null, ast);
                } else {
                    a = (RList) aArg;
                }
                // NOTE: this code is not very robust against broken instance of qr (but maybe it is what we want, revisit when implementing S3)
                RArray.Names aNames = a.names();
                int qrIndex = aNames.map(QrCoef.qrSymbol);
                int rankIndex = aNames.map(QrCoef.rankSymbol);

                RArray qr = (RArray) a.getRAny(qrIndex);
                int[] qdim = qr.dimensions();
                int nrow = qdim[0];
                int ncol = qdim[1];
                RInt qrRank = a.getRAny(rankIndex).asInt();
                int rank = qrRank.getInt(0);
                int mind = ncol > nrow ? nrow : ncol;
                if (rank != mind) {
                    throw RError.getSingularSolve(ast, "a");
                }
                RAny b;
                if (bPosition == -1) {
                    if (ncol != nrow) {
                        throw RError.getOnlySquareInverted(ast);
                    }
                    // make b a diagonal matrix
                    double[] bcontent = new double[ncol * ncol];
                    for (int i = 0; i < ncol; i++) {
                        bcontent[i * (ncol + 1)] = 1;
                    }
                    b = RDouble.RDoubleFactory.getMatrixFor(bcontent, ncol, ncol);
                } else {
                    b = args[bPosition];
                }
                RAny res = QrCoef.coef(a, b, ast);
                if (res instanceof RDouble) {
                    return RDouble.RDoubleUtils.convertNAandNaNtoZero((RDouble) res);
                } else {
                    Utils.nyi("complex case not implemented");
                    return null;
                }
            }
        };
    }

}
