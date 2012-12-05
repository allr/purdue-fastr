package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Matrix {
    private static final String[] paramNames = new String[]{"data", "nrow", "ncol", "byrow", "dimnames"};

    private static final int IDATA = 0;
    private static final int INROW = 1;
    private static final int INCOL = 2;
    private static final int IBYROW = 3;
    private static final int IDIMNAMES = 4;

    public static boolean parseByRow(ASTNode ast, RAny arg) {
        RLogical l = arg.asLogical();
        int il;
        if (l.size() == 0 || (il = l.getLogical(0)) == RLogical.NA) {
            throw RError.getInvalidByRow(ast);
        }
        return (il == RLogical.TRUE);
    }

    public static RArray parseData(ASTNode ast, RAny arg) {
        if (arg instanceof RArray) {
            return (RArray) arg;
        }
        throw RError.getDataVector(ast);
    }

    public static int parseExtent(ASTNode ast, RAny arg) {
        if (arg instanceof RInt) {
            RInt v = (RInt) arg;
            if (v.size() == 0) {
                return RInt.NA;
            }
            return v.getInt(0);
        }
        if (arg instanceof RDouble) {
            RDouble v = (RDouble) arg;
            if (v.size() == 0) {
                return RInt.NA;
            }
            double d = v.getDouble(0);
            return Convert.double2int(d);
        }
        if (arg instanceof RLogical) {
            RLogical v = (RLogical) arg;
            if (v.size() == 0) {
                return RInt.NA;
            }
            return Convert.logical2int(v.getLogical(0));
        }
        throw RError.getNonNumericMatrixExtent(ast);
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (provided[IDIMNAMES]) {
                Utils.nyi();
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                    RArray data = RLogical.BOXED_NA;
                    if (provided[IDATA]) {
                        data = parseData(ast, args[paramPositions[IDATA]]);
                        if (data.size() == 0) {
                            data = Utils.getBoxedNA(data);
                        }
                    }
                    int dsize = data.size();
                    int nRow = -1;
                    if (provided[INROW]) {
                        nRow = parseExtent(ast, args[paramPositions[INROW]]);
                        if (nRow == RInt.NA) {
                            throw RError.getInvalidNRow(ast);
                        }
                        if (nRow < 0) {
                            throw RError.getNegativeNRow(ast);
                        }
                    }
                    int nCol = -1;
                    int size;
                    if (provided[INCOL]) {
                        nCol = parseExtent(ast, args[paramPositions[INCOL]]);
                        if (nCol == RInt.NA) {
                            throw RError.getInvalidNCol(ast);
                        }
                        if (nCol < 0) {
                            throw RError.getNegativeNCol(ast);
                        }
                    }

                    if (nRow == -1) {
                        if (nCol == -1) {
                            nCol = 1;
                            nRow = dsize;
                        } else {
                            if (dsize >= nCol) {
                                nRow = dsize / nCol;
                                if (nRow * nCol != dsize) {
                                    context.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                                }
                            } else {
                                nRow = 1;
                                if (nCol % dsize != 0) {
                                    context.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                                }
                            }
                        }
                        size = nRow * nCol;
                    } else { // nRow != -1
                        if (nCol == -1) {
                            if (dsize >= nRow) {
                                nCol = dsize / nRow;
                                if (nCol * nRow != dsize) {
                                    context.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                                }
                            } else {
                                nCol = 1;
                                if (nRow % dsize != 0) {
                                    context.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                                }
                            }
                            size = nRow * nCol;
                        } else {
                            size = nRow * nCol;
                            if (size % dsize != 0 && dsize % size != 0) {
                                context.warning(ast, String.format(RError.DATA_NOT_MULTIPLE_ROWS, dsize, nRow));
                            }
                        }
                    }

                    boolean byRow = provided[IBYROW] ? parseByRow(ast, args[paramPositions[IBYROW]]) : false;

                    RArray res = Utils.createArray(data, size, new int[] {nRow, nCol});
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
    };
}
