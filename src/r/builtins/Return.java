package r.builtins;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * "rep"
 * 
 * <pre>
 * value -- An expression.
 * </pre>
 */
// FIXME: Truffle can't handle BuiltIn1
public final class Return extends CallFactory {

    static final CallFactory _ = new Return("return", new String[]{"value"}, new String[]{});

    Return(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static final class ReturnException extends ControlFlowException {
        public static ReturnException instance = new ReturnException();
        private static final long serialVersionUID = -9147675462255551205L;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 0) { return new BuiltIn.BuiltIn0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                RFrameHeader.setReturnValue(frame, RNull.getNull());
                throw ReturnException.instance;
            }
        }; }
        if (exprs.length == 1) { return new BuiltIn.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny param) {
                RFrameHeader.setReturnValue(frame, param);
                throw ReturnException.instance;
            }
        }; }
        throw Utils.nyi("unreachable");
    }
}
