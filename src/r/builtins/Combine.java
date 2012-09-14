package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class Combine {

    private static <T extends RArray, U extends T> int fillIn(U result, T input, int offset) {
        int len = input.size();
        for (int i = 0; i < len; i++) {
            result.set(offset + i, input.get(i));
        }
        return offset + len;
    }

    public static final CallFactory FACTORY = new CallFactory() {

        // only supports a vector of integers, doubles, or logical
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, RFrame frame) {
                        return RNull.getNull();
                    }

                };
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, RFrame frame, RAny[] params) {
                    int len = 0;
                    boolean hasDouble = false;
                    boolean hasLogical = false;
                    boolean hasInt = false;
                    for (int i = 0; i < params.length; i++) {
                        RAny v = params[i];
                        if ((hasDouble = v instanceof RDouble) || (hasInt = v instanceof RInt) || (hasLogical = v instanceof RLogical)) {
                            len += ((RArray) v).size();
                        } else {
                            Utils.nyi("unsupported vector element");
                        }
                    }
                    int offset = 0;
                    if (hasDouble) {
                        DoubleImpl res = RDouble.RDoubleFactory.getUninitializedArray(len);
                        for (RAny v : params) {
                            offset = fillIn(res, v instanceof RDouble ? (RDouble) v : v.asDouble(), offset);
                        }
                        return res;
                    }
                    if (hasInt) {
                        IntImpl res = RInt.RIntFactory.getUninitializedArray(len);
                        for (RAny v : params) {
                            offset = fillIn(res, v instanceof RInt ? (RInt) v : v.asInt(), offset);
                        }
                        return res;
                    }
                    if (hasLogical) {
                        LogicalImpl res = RLogical.RLogicalFactory.getUninitializedArray(len);
                        for (RAny v : params) {
                            offset = fillIn(res, v instanceof RLogical ? (RLogical) v : v.asLogical(), offset);
                        }
                        return res;
                    }
                    Utils.nyi("Unreacheable");
                    return null;
                }
            };
        }
    };
}
