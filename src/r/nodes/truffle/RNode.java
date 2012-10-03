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
    public final Object execute(Context context, Frame frame) {
        return execute((RContext) context, frame);
    }

    public abstract Object execute(RContext context, Frame frame);

    public int executeInt(RContext context, Frame frame) throws UnexpectedResultException {
        return ValueConversion.expectInt(execute(context, frame));
    }

    public double executeDouble(RContext context, Frame frame) throws UnexpectedResultException {
        return ValueConversion.expectDouble(execute(context, frame));
    }

    public int executeLogicalOne(RContext context, Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectLogicalOne((RAny) execute(context, frame));
    }

    public Object executeVoid(RContext context, Frame frame) {
        execute(context, frame);
        return RNull.getNull();
    }
}
