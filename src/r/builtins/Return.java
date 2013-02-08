package r.builtins;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


// FIXME: Truffle can't handle BuiltIn1
public class Return {

    public static final class ReturnException extends ControlFlowException {
        public static ReturnException instance = new ReturnException();
        private static final long serialVersionUID = -9147675462255551205L;
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(Frame frame) {
                        RFrameHeader.setReturnValue(frame, RNull.getNull());
                        throw ReturnException.instance;
                    }

                };
            }
            if (exprs.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(Frame frame, RAny param) {
                        RFrameHeader.setReturnValue(frame, param);
                        throw ReturnException.instance;
                    }
                };
            }
            Utils.nyi("unreachable");
            return null;
        }
    };

}
