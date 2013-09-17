package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "names"
 *
 * <pre>
 * x -- an R object.
 * </pre>
 */
final class NamesAssign extends CallFactory {
    static final CallFactory _ = new NamesAssign("names<-", new String[]{"x", "value"}, new String[]{"x", "value"});

    private NamesAssign(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x, RAny value) {
                return replaceNames(x, value, ast);
            }
        };
    }

    public static RAny replaceNames(RAny x, RAny value, ASTNode ast) {
        if (!(x instanceof RArray)) { throw RError.getNamesNonVector(ast); }
        RArray xarr = (RArray) x;

        RArray.Names newNames;
        if (value instanceof RNull) {
            newNames = null;
        } else {
            RString str = Convert.coerceToStringError(value, ast);
            int xsize = xarr.size();
            int strsize = str.size();

            if (strsize > xsize) { throw RError.getAttributeVectorSameLength(ast, "names", strsize, xsize);
            // NOTE: the error message is a bit confusing
            }
            RSymbol[] symbols = new RSymbol[xsize];
            int i = 0;
            for (; i < strsize; i++) {
                String s = str.getString(i);
                symbols[i] = RSymbol.getSymbol(s);
            }
            for (; i < xsize; i++) {
                symbols[i] = RSymbol.NA_SYMBOL;
            }
            newNames = RArray.Names.create(symbols);
        }
        if (!xarr.isShared()) {
            return xarr.setNames(newNames);
        } else {
            return Utils.copyArray(xarr).setNames(newNames);
        }
    }

}
