package r.builtins;

import org.netlib.blas.*;

import r.Truffle.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Crossprod extends CallFactory {

    static final CallFactory _ = new Crossprod("crossprod", new String[]{"x", "y"}, new String[]{"x"});

    private Crossprod(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xpos = ia.position("x");
        final int ypos = ia.position("y");

        if (ypos != -1) {
            return new Builtin.Builtin2(call, names, exprs) {
                @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                    RAny x;
                    RAny y;
                    if (xpos == 0) {
                        x = arg0;
                        y = arg1;
                    } else {
                        x = arg1;
                        y = arg0;
                    }
                    if (y instanceof RNull) {
                        return crossprod(x, ast);
                    } else {
                        return crossprod(x, y, ast);
                    }
                }
            };
        } else {
            return new Builtin.Builtin1(call, names, exprs) {
                @Override public RAny doBuiltIn(Frame frame, RAny x) {
                    return crossprod(x, ast);
                }
            };
        }
    }

    public static RAny crossprod(RAny l, RAny r, ASTNode ast) {

        // LICENSE: transcribed code from GNU R, which is licensed under GPL
        MatrixOperation.checkNumeric(l, r, ast); // TODO: support also complex matrices
        RDouble ld = l.asDouble().materialize();
        RDouble rd = r.asDouble().materialize();
        int[] ldims = ld.dimensions();
        int nldims = ldims == null ? 0 : ldims.length;
        int[] rdims = rd.dimensions();
        int nrdims = rdims == null ? 0 : rdims.length;

        int lrow;
        int lcol;
        int rrow;
        int rcol;

        if (nldims != 2) {
            if (nrdims != 2) {
                return MatrixOperation.dotProduct(ast, ld, rd);
            } else { // FIXME: could have a special case like with matrix product
                lrow = ld.size();
                lcol = 1;
                rrow = rdims[0];
                rcol = rdims[1];
            }
        } else {
            lrow = ldims[0];
            lcol = ldims[1];
            if (nrdims != 2) { // FIXME: could have a special case like with matrix product
                rrow = rd.size();
                rcol = 1;
            } else {
                rrow = rdims[0];
                rcol = rdims[1];
            }
        }

        if (lrow != rrow) { throw RError.getNonConformableArgs(ast); }
        //        double[] res = matrixTimesMatrixGetters(ld, rd, lcol, lrow, rcol);
        double[] res = matrixTimesMatrixNative(ld, rd, lcol, lrow, rcol);
        return RDouble.RDoubleFactory.getFor(res, new int[]{lcol, rcol}, null);
    }

    public static RAny crossprod(RAny x, ASTNode ast) {

        MatrixOperation.checkNumeric(x, ast); // TODO: support also complex matrices
        RDouble xd = x.asDouble().materialize();
        int[] dims = xd.dimensions();
        int ndims = dims == null ? 0 : dims.length;

        if (ndims != 2) { return MatrixOperation.dotProduct(xd); }
        int row = dims[0];
        int col = dims[1];
        double[] res = matrixNative(xd, row, col);
        return RDouble.RDoubleFactory.getFor(res, new int[]{col, col}, null);
    }

    public static double[] matrixTimesMatrixNative(RDouble a, RDouble b, int m, int n, int p) {
        // a is n x m, b is n x p, result is m x p
        double[] res = new double[m * p];
        if (m > 0 && n > 0 && p > 0) {
            BLAS.getInstance().dgemm("T", "N", m, p, n, 1.0, a.getContent(), n, b.getContent(), n, 0.0, res, m);
        } else {
            // leave zeros
        }
        return res;

    }

    public static double[] matrixNative(RDouble x, int row, int col) {
        // LICENSE: transcribed code from GNU R, which is licensed under GPL

        // x is row x col, result is col x col
        double[] res = new double[col * col];
        if (row > 0 && col > 0) {
            BLAS.getInstance().dsyrk("U", "T", col, row, 1.0, x.getContent(), row, 0.0, res, col);
            for (int i = 0; i < col; i++) {
                for (int j = 0; j < i; j++) {
                    res[col * j + i] = res[col * i + j];
                }
            }
        } else {
            // leave zeros
        }
        return res;

    }

    // NOTE: in crossprod of GNU-R, blas is always used even for inputs with NAs, so this method would not be needed
    // however, in matprod, blas is bypassed whenever the input contains NA or other NaN (so the method below may become needed when
    // GNU-R is made more consistent internally)
    public static double[] matrixTimesMatrixGetters(RDouble a, RDouble b, int m, int n, int p) {

        // a is n x m, t(a) is m x n, b is n x p, result is m x p
        double[] content = new double[m * p];
        for (int j = 0; j < p; j++) {
            for (int i = 0; i < m; i++) {
                double d = 0;
                for (int k = 0; k < n; k++) {
                    d += a.getDouble(i * n + k) * b.getDouble(j * n + k);
                }
                content[j * m + i] = d;
            }
        }
        return content;
    }

}
