package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "matrix"
 * 
 * <pre>
 * data -- an optional data vector (including a list or expression vector). Other R objects are coerced by as.vector.
 * nrow -- the desired number of rows.
 * ncol -- the desired number of columns.
 * byrow -- logical. If FALSE (the default) the matrix is filled by columns, otherwise the matrix is filled by rows.
 * dimnames -- A dimnames attribute for the matrix: NULL or a list of length 2 giving the row and column names respectively.
 *  An empty list is treated as NULL, and a list of length one as row names. The list can be named, and the list names will be
 *   used as names for the dimensions.
 * </pre>
 */
final class Matrix extends CallFactory {
    static final CallFactory _ = new Matrix("matrix", new String[]{"data", "nrow", "ncol", "byrow", "dimnames"}, new String[]{});

    private Matrix(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseByRow(ASTNode ast, RAny arg) {
        RLogical l = arg.asLogical();
        int il;
        if (l.size() == 0 || (il = l.getLogical(0)) == RLogical.NA) { throw RError.getInvalidByRow(ast); }
        return (il == RLogical.TRUE);
    }

    public static RArray parseData(ASTNode ast, RAny arg) {
        if (arg instanceof RArray) { return (RArray) arg; }
        throw RError.getDataVector(ast);
    }

    public static int parseExtent(ASTNode ast, RAny arg) {
        if (arg instanceof RInt) {
            RInt v = (RInt) arg;
            if (v.size() == 0) { return RInt.NA; }
            return v.getInt(0);
        }
        if (arg instanceof RDouble) {
            RDouble v = (RDouble) arg;
            if (v.size() == 0) { return RInt.NA; }
            double d = v.getDouble(0);
            return Convert.double2int(d);
        }
        if (arg instanceof RLogical) {
            RLogical v = (RLogical) arg;
            if (v.size() == 0) { return RInt.NA; }
            return Convert.logical2int(v.getLogical(0));
        }
        throw RError.getNonNumericMatrixExtent(ast);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("dimnames")) { throw Utils.nyi(); }
        final int posData = ia.position("data");
        final int posNrow = ia.position("nrow");
        final int posNcol = ia.position("ncol");
        final int posByrow = ia.position("byrow");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RArray data = RLogical.BOXED_NA;
                if (posData != -1) {
                    data = parseData(ast, args[posData]);
                    if (data.size() == 0) {
                        data = Utils.getBoxedNA(data);
                    }
                }
                int dsize = data.size();
                int nRow = -1;
                if (posNrow != -1) {
                    nRow = parseExtent(ast, args[posNrow]);
                    if (nRow == RInt.NA) { throw RError.getInvalidNRow(ast); }
                    if (nRow < 0) { throw RError.getNegativeNRow(ast); }
                }
                int nCol = -1;
                int size;
                if (posNcol != -1) {
                    nCol = parseExtent(ast, args[posNcol]);
                    if (nCol == RInt.NA) { throw RError.getInvalidNCol(ast); }
                    if (nCol < 0) { throw RError.getNegativeNCol(ast); }
                }

                if (nRow == -1) {
                    if (nCol == -1) {
                        nCol = 1;
                        nRow = dsize;
                    } else {
                        if (dsize >= nCol) {
                            nRow = dsize / nCol;
                            if (nRow * nCol != dsize) {
                                RContext.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                            }
                        } else {
                            nRow = 1;
                            if (nCol % dsize != 0) {
                                RContext.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                            }
                        }
                    }
                    size = nRow * nCol;
                } else { // nRow != -1
                    if (nCol == -1) {
                        if (dsize >= nRow) {
                            nCol = dsize / nRow;
                            if (nCol * nRow != dsize) {
                                RContext.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                            }
                        } else {
                            nCol = 1;
                            if (nRow % dsize != 0) {
                                RContext.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                            }
                        }
                        size = nRow * nCol;
                    } else {
                        size = nRow * nCol;
                        if (size % dsize != 0 && dsize % size != 0) {
                            RContext.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                        }
                    }
                }

                boolean byRow = posByrow != -1 ? parseByRow(ast, args[posByrow]) : false;
                RArray res = Utils.createArray(data, size, new int[]{nRow, nCol}, null, null);
                if (data instanceof ScalarDoubleImpl && ((ScalarDoubleImpl) data).getDouble() == 0) { return res; }
                int di = 0;
                if (!byRow) {
                    for (int i = 0; i < size; i++) {
                        res.set(i, data.get(di++));
                        if (di == dsize) {
                            di = 0;
                        }
                    }
                } else {
                    for (int j = 0; j < nRow; j++) {
                        for (int i = 0; i < nCol; i++) {
                            res.set(i * nRow + j, data.get(di++));
                            if (di == dsize) {
                                di = 0;
                            }
                        }
                    }
                }
                return res;
            }
        };
    }
}
