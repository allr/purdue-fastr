package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't inline BuiltIn.BuiltIn1, so using BuiltIn
public class Length {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    RAny arg = args[0];
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
