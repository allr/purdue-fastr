package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class DimAssign extends CallFactory {

    static final CallFactory _ = new DimAssign("dim<-", new String[]{"x", "value"}, null);

    private DimAssign(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static int[] buildDimensions(int size, RInt dims, ASTNode ast) {
        int dprod = 1;
        int dsize = dims.size();
        if (dsize == 0) {
            throw RError.getLengthZeroDimInvalid(ast);
        }
        int[] res = new int[dsize];
        for (int i = 0; i < dsize; i++) {
            int d = dims.getInt(i);
            if (d >= 0) {
                dprod *= d;
                res[i] = d;
                continue;
            }
            if (d == RInt.NA) {
                throw RError.getDimsContainNA(ast);
            }
            throw RError.getDimsContainNegativeValues(ast);
        }
        if (dprod != size) {
            throw RError.getDimsDontMatchLength(ast, dprod, size);
        }
        return res;
    }

    public static RArray replaceDims(RAny x, RAny value, ASTNode ast) {
        if (!(x instanceof RArray)) {
            throw RError.getInvalidFirstArgument(ast);
        }
        RArray a = (RArray) x;
        int[] dims;
        if (value instanceof RNull) {
            dims = null;
        } else {
            RInt ndims = Convert.coerceToIntWarning(value, ast);
            dims = buildDimensions(a.size(), ndims, ast);
        }

        if (!a.isShared()) {
            return a.setNames(null).setDimensions(dims);
        } else {
            return Utils.copyArray(a).setNames(null).setDimensions(dims);
        }
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x, RAny value) {
               return replaceDims(x, value, ast);
            }
        };
     }
}
