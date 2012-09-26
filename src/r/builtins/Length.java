package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Length {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    if (arg instanceof RArray) {
                        return RInt.RIntFactory.getScalar(((RArray) arg).size());
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }

            };
        }
    };
}
