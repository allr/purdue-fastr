package r.builtins;

import com.oracle.truffle.nodes.control.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Return {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, RFrame frame) {
                        frame.setReturnValue(RNull.getNull());
                        throw ReturnException.instance;
                    }

                };
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, RFrame frame, RAny[] params) {
                    RAny toReturn = Combine.combine(context, frame, params);
                    frame.setReturnValue(toReturn);
                    throw ReturnException.instance;
                }
            };
        }
    };

}
