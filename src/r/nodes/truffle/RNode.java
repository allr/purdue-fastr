package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

import com.oracle.truffle.*;
import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

public abstract class RNode extends Node {

    public ASTNode getAST() {
        return ((RNode) getParent()).getAST();
    }

    @Override
    public Object execute(Context context, Frame frame) {
        return execute((RContext) context, (RFrame) frame);
    }

    public abstract Object execute(RContext context, RFrame frame);

    public int executeInt(RContext context, RFrame frame) throws UnexpectedResultException {
        return ValueConversion.expectInt(execute(context, frame));
    }

    public double executeDouble(RContext context, RFrame frame) throws UnexpectedResultException {
        return ValueConversion.expectDouble(execute(context, frame));
    }

    public int executeLogical(RContext context, RFrame frame) throws UnexpectedResultException {
        Object res = execute(context, frame);
        if (res instanceof RLogical) { // FIXME call this nice value converter api // FIXME Check branch order
            return ((RLogical) res).getLogical(0);
        } else if (res instanceof Integer) {
            return ((Integer) res).intValue();
        }
        throw new UnexpectedResultException(res);
    }

    public Object executeVoid(RContext context, RFrame frame) {
        execute(context, frame);
        return RNull.getNull();
    }
}
