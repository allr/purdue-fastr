package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't inline BuiltIn.BuiltIn1, so using BuiltIn
public class Names {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg) {
                    if (arg instanceof RArray) {
                        RArray.Names sNames = ((RArray) arg).names();
                        if (sNames != null) {
                            return RString.RStringFactory.getFor(sNames.asStringArray());
                        }
                        return RNull.getNull();
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }

            };
        }
    };

    public static final CallFactory REPLACEMENT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn.BuiltIn2(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny x, RAny value) {
                    if (!(x instanceof RArray)) {
                        throw RError.getNamesNonVector(ast);
                    }
                    RArray xarr = (RArray) x;
                    RString str = Convert.coerceToStringError(value, ast);
                    int xsize = xarr.size();
                    int strsize = str.size();

                    if (strsize > xsize) {
                        throw RError.getAttributeVectorSameLength(ast, "names", strsize, xsize); // NOTE: the error message is a bit confusing
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

                    RArray.Names newNames = RArray.Names.create(symbols);
                    if (!xarr.isShared()) {
                        return xarr.setNames(newNames);
                    } else {
                        return Utils.copyArray(xarr).setNames(newNames);
                    }
                }
            };
        }
    };
}
