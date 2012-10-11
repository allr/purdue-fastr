package r.builtins;

import com.oracle.truffle.runtime.Frame;

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

    public static RAny combine(RAny[] params) {
        int len = 0;
        boolean hasDouble = false;
        boolean hasLogical = false;
        boolean hasInt = false;
        boolean hasList = false;
        for (int i = 0; i < params.length; i++) {
            RAny v = params[i];

            if (v instanceof RList) {
                hasList = true;
            } else if (v instanceof RDouble) {
                hasDouble = true;
            } else if (v instanceof RInt) {
                hasInt = true;
            } else if (v instanceof RLogical) {
                hasLogical = true;
            } else {
                Utils.nyi("unsupported type");
                return null;
            }
            len += ((RArray) v).size();
        }
        int offset = 0;
        if (hasList) {
            ListImpl res = RList.RListFactory.getUninitializedArray(len);
            for (RAny v : params) {
                RArray a = (RArray) v;
                int asize = a.size();
                if (v instanceof RList) {
                    RList l = (RList) v;
                    // FIXME: no deep copying here
                    for (int i = 0; i < asize; i++) {
                        res.set(offset + i, l.getRAny(i));
                    }
                } else {
                    for (int i = 0; i < asize; i++) {
                        res.set(offset + i, a.boxedGet(i));
                    }
                }
                offset += asize;
            }
            return res;
        }
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

    public static final CallFactory FACTORY = new CallFactory() {

        // only supports a vector of integers, doubles, or logical
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        return RNull.getNull();
                    }

                };
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                    return combine(params);
                }
            };
        }
    };
}
