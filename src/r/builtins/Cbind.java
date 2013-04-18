package r.builtins;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: add names support (when rownames, colnames are implemented)
final class Cbind extends CallFactory {

    static final CallFactory _ = new Cbind("cbind", new String[]{"...", "deparse.level"}, new String[]{});

    private Cbind(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("deparse.level")) {
            Utils.nyi("deparse.level not implemented");
        }
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return cbind(args, ast);
            }
        };
    }

    private static int fillIn(RArray result, RArray input, int offset, int nrow, ASTNode ast, int argIndex) {
        int[] dims = input.dimensions();
        int isize = input.size();
        if (dims != null && dims.length == 2) {
            // input is a matrix
            for (int i = 0; i < isize; i++) {
                result.set(offset + i, input.get(i));
            }
            return offset + isize;
        } else {
            // input is a non-matrix
            int ii = 0;
            for (int i = 0; i < nrow; i++) {
                result.set(offset + i, input.get(ii++));
                if (ii == isize) {
                    ii = 0;
                }
            }
            if (ii != 0) {
                RContext.warning(ast, String.format(RError.ROWS_NOT_MULTIPLE, argIndex + 1));
            }
            return offset + nrow;
        }
    }

    private static int fillInListToList(RList result, RList input, int offset, int nrow, ASTNode ast, int argIndex) {
        int[] dims = input.dimensions();
        int isize = input.size();
        if (dims != null && dims.length == 2) {
            // input is a matrix
            for (int i = 0; i < isize; i++) {
                result.set(offset + i, input.getRAnyRef(i));
            }
            return offset + isize;
        } else {
            // input is a non-matrix
            int ii = 0;
            for (int i = 0; i < nrow; i++) {
                result.set(offset + i, input.getRAnyRef(ii++));
                if (ii == isize) {
                    ii = 0;
                }
            }
            if (ii != 0) {
                RContext.warning(ast, String.format(RError.ROWS_NOT_MULTIPLE, argIndex + 1));
            }
            return offset + nrow;
        }
    }

    // generic version
    public static RAny cbind(RAny[] args, ASTNode ast) {
        int maxVectorSize = 0;
        int ncol = 0;
        int nrow = -1;
        int nargs = args.length;
        boolean hasRaw = false;
        boolean hasLogical = false;
        boolean hasInt = false;
        boolean hasDouble = false;
        boolean hasComplex = false;
        boolean hasString = false;
        boolean hasList = false;

        for (int i = 0; i < nargs; i++) {
            RAny x = args[i];
            if (x instanceof RNull) {
                // nothing added to result
                continue;
            }
            if (!(x instanceof RArray)) {
                // x repeated, added to result
                hasList = true;
                ncol++;
                continue;
            }
            RArray a = (RArray) x;
            int[] dims = a.dimensions();
            if (dims != null && dims.length == 2) {
                if (nrow != dims[0]) {
                    if (nrow == -1) {
                        nrow = dims[0];
                    } else {
                        throw RError.getRowsMustMatch(ast, i + 1);
                    }
                }
                ncol += dims[1];
            } else {
                // a is not a matrix
                int asize = a.size();
                if (asize > maxVectorSize) {
                    maxVectorSize = asize;
                }
                ncol++;
            }
            if (a instanceof RDouble) {
                hasDouble = true;
            } else if (a instanceof RInt) {
                hasInt = true;
            } else if (a instanceof RLogical) {
                hasLogical = true;
            } else if (a instanceof RList) {
                hasList = true;
            } else if (a instanceof RString) {
                hasString = true;
            } else if (a instanceof RComplex) {
                hasComplex = true;
            } else if (a instanceof RRaw) {
                hasRaw = true;
            } else {
                throw Utils.nyi("unsupported type");
            }
        }

        if (nrow == -1) {
            // no matrix present
            nrow = maxVectorSize;
        }
        int size = nrow * ncol;
        int offset = 0;
        int[] ndim = new int[]{nrow, ncol};

        if (hasList) {
            ListImpl res = RList.RListFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RList) {
                    offset = fillInListToList(res, (RList) v, offset, nrow, ast, ai);
                    continue;
                }
                if (v instanceof RNull) {
                    continue;
                }
                if (!(v instanceof RArray)) {
                    for (int i = 0; i < nrow; i++) {
                        res.set(offset + i, v);
                    }
                    offset += nrow;
                    continue;
                }
                // v is a non-list array
                offset = fillIn(res, v.asList(), offset, nrow, ast, ai);
            }
            return res;
        }
        if (hasString) {
            RString res = RString.RStringFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v.asString(), offset, nrow, ast, ai);
            }
            return res;
        }
        if (hasComplex) {
            RComplex res = RComplex.RComplexFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v.asComplex(), offset, nrow, ast, ai);
            }
            return res;
        }
        if (hasDouble) {
            RDouble res = RDouble.RDoubleFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v.asDouble(), offset, nrow, ast, ai);
            }
            return res;
        }
        if (hasInt) {
            RInt res = RInt.RIntFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v.asInt(), offset, nrow, ast, ai);
            }
            return res;
        }
        if (hasLogical) {
            RLogical res = RLogical.RLogicalFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v.asLogical(), offset, nrow, ast, ai);
            }
            return res;
        }
        if (hasRaw) {
            RRaw res = RRaw.RRawFactory.getUninitializedArray(size, ndim, null, null);
            for (int ai = 0; ai < nargs; ai++) {
                RAny v = args[ai];
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v.asRaw(), offset, nrow, ast, ai);
            }
            return res;
        }
        return RNull.getNull();
    }

}
