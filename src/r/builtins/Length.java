package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't inline BuiltIn.BuiltIn1, so using BuiltIn
public class Length {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    if (arg instanceof RArray) {
                        return RInt.RIntFactory.getScalar(((RArray) arg).size());
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }

            };
        }
    };

    public static int parseNewLength(int value, RContext context, ASTNode ast) {
        if (value >= 0) {
            return value;
        } else {
            throw RError.getVectorSizeNegative(ast);
        }
    }

    public static int parseNewLength(RAny value, RContext context, ASTNode ast) {
        if (value instanceof RDouble) {
            RDouble dvalue = (RDouble) value;
            if (dvalue.size() == 1) {
                return parseNewLength(Convert.double2int(dvalue.getDouble(0)), context, ast);
            }

        }
        if (value instanceof RInt) {
            RInt ivalue = (RInt) value;
            if (ivalue.size() == 1) {
                return parseNewLength(ivalue.getInt(0), context, ast);
            }
        }
        throw RError.getInvalidUnnamedValue(ast);
    }

    public static RArray changeSize(RArray x, int nsize) {
        int xsize = x.size();
        if (xsize == nsize) {
            return x; // does not drop dimensions
        }
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

    public static final CallFactory REPLACEMENT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn.BuiltIn2(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny x, RAny value) {
                    if (!(x instanceof RArray)) {
                        throw RError.getInvalidUnnamedArgument(ast);
                    }
                    int nlen = parseNewLength(value, context, ast);
                    return changeSize((RArray) x, nlen);
                }
            };
        }
    };
}
