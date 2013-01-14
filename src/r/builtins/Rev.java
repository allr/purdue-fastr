package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

// FIXME: could also do lazy rev of int sequence
public class Rev {

    public static RString rev(RString orig) {
        final int size = orig.size() - 1;
        return new View.RStringProxy<RString>(orig) {

            @Override
            public String getString(int i) {
                return orig.getString(size - i);
            }
        };
    }

    public static RLogical rev(RLogical orig) {
        final int size = orig.size() - 1;
        return new View.RLogicalProxy<RLogical>(orig) {

            @Override
            public int getLogical(int i) {
                return orig.getLogical(size - i);
            }
        };
    }

    public static RInt rev(RInt orig) {
        final int size = orig.size() - 1;
        return new View.RIntProxy<RInt>(orig) {

            @Override
            public int getInt(int i) {
                return orig.getInt(size - i);
            }
        };
    }

    public static RDouble rev(RDouble orig) {
        final int size = orig.size() - 1;
        return new View.RDoubleProxy<RDouble>(orig) {

            @Override
            public double getDouble(int i) {
                return orig.getDouble(size - i);
            }
        };
    }

    public static RAny rev(RAny arg) {
        if (arg instanceof RDouble) {
            return rev((RDouble) arg);
        }
        if (arg instanceof RInt) {
            return rev((RInt) arg);
        }
        if (arg instanceof RLogical) {
            return rev((RLogical) arg);
        }
        if (arg instanceof RString) {
            return rev((RString) arg);
        }

        // default implementation
        if (!(arg instanceof RArray)) {
            Utils.nyi("unsupported type");
        }
        RArray a = (RArray) arg;
        int size =  a.size();
        RArray res = Utils.createArray(arg, size);
        for (int i = 0; i < size; i++) {
            res.set(i, a.getRef(size - 1 - i));
        }
        return res;
    }

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    return rev(arg);
                }
            };
        }
    };
}
