package r.builtins;

import com.oracle.truffle.nodes.control.ReturnException;
import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't handle BuiltIn1
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
            if (exprs.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame, RAny param) {
                        RFrame.setReturnValue(frame, param);
                        throw ReturnException.instance;
                    }
                };
            }
            Utils.nyi("unreachable");
            return null;
        }
    };

}
