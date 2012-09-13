package r.builtins;

import r.*;
import r.builtins.BuiltIn.BuiltIn2;
import r.data.*;
import r.data.internal.*;
import r.nodes.FunctionCall;
import r.nodes.truffle.*;

public class Sequence {

    public static RAny create(int left, int right) {
        int len = right - left;
        IntImpl data = new IntImpl(len);
        for (int i = 0; i < len; i++) {
            data.set(i, left + i);
        }
        return data;
    }

    public static RAny create(double left, double right) {
        int len = (int) (right - left);
        DoubleImpl data = new DoubleImpl(len);
        for (int i = 0; i < len; i++) {
            data.set(i, left + i);
        }
        return data;
    }

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(FunctionCall call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn2(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, RFrame frame, RAny arg0, RAny arg1) {
                    boolean l = arg0 instanceof RDouble;
                    boolean r = arg1 instanceof RDouble;

                    if (l || r) {
                        if (l && r) {
                            return Sequence.create(((RDouble) arg0).getDouble(0), ((RDouble) arg1).getDouble(0));
                        }
                        if (l) {
                            return Sequence.create(((RDouble) arg0).getDouble(0), arg1.asDouble().getDouble(0));
                        }
                        return Sequence.create(arg0.asDouble().getDouble(0), ((RDouble) arg1).getDouble(0));
                    }

                    l = arg0 instanceof RInt;
                    r = arg1 instanceof RInt;

                    if (l || r) {
                        if (l && r) {
                            return Sequence.create(((RInt) arg0).getInt(0), ((RInt) arg1).getInt(0));
                        }
                        if (l) {
                            return Sequence.create(((RInt) arg0).getInt(0), arg1.asInt().getInt(0));
                        }
                        return Sequence.create(arg0.asInt().getInt(0), ((RInt) arg1).getInt(0));
                    }

                    Utils.nyi();
                    return null;
                }
            };
        }
    };
}
