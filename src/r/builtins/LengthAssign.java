package r.builtins;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * "length<-"
 * 
 * <pre>
 * x -- an R object. For replacement, a vector or factor.
 * value -- an integer: double values will be coerced to integer.
 * </pre>
 */

final class LengthAssign extends CallFactory {

    static final CallFactory _ = new LengthAssign("length<-", new String[]{"x", "value"}, new String[]{"x", "value"});

    private LengthAssign(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x, RAny value) {
                if (!(x instanceof RArray)) { throw RError.getInvalidUnnamedArgument(ast); } // FIXME: error differs from GNU R
                int nlen = parseNewLength(value, ast);
                return changeSize((RArray) x, nlen);
            }
        };
    }

    private static int parseNewLength(int value, ASTNode ast) {
        if (value >= 0) { return value; }
        throw RError.getVectorSizeNegative(ast);
    }

    private static int parseNewLength(RAny value, ASTNode ast) {
        if (value instanceof RDouble) {
            RDouble dvalue = (RDouble) value;
            if (dvalue.size() == 1) { return parseNewLength(Convert.double2int(dvalue.getDouble(0)), ast); }
        }
        if (value instanceof RInt) {
            RInt ivalue = (RInt) value;
            if (ivalue.size() == 1) { return parseNewLength(ivalue.getInt(0), ast); }
        }
        throw RError.getInvalidUnnamedValue(ast);
    }

    private static RArray changeSize(RArray x, int nsize) {
        int xsize = x.size();
        if (xsize == nsize) { return x; } // does not drop dimensions
        RArray res = Utils.createArray(x, nsize);
        if (nsize > xsize) {
            for (int i = 0; i < xsize; i++) {
                res.set(i, x.get(i));
            }
            for (int i = xsize; i < nsize; i++) {
                Utils.setNA(res, i); // FIXME: likely slow
            }
        } else {
            for (int i = 0; i < nsize; i++) {
                res.set(i, x.get(i));
            }
        }
        return res; // drops dimensions
    }

}
