package r.builtins;

import com.oracle.truffle.nodes.control.ReturnException;
import com.oracle.truffle.runtime.Frame;

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
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        RFrame.setReturnValue(frame, RNull.getNull());
                        throw ReturnException.instance;
                    }

                };
            }
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                    RAny toReturn = Combine.combine(context, frame, params);
                    RFrame.setReturnValue(frame, toReturn);
                    throw ReturnException.instance;
                }
            };
        }
    };

}
