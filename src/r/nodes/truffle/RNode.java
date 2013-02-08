package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;

public abstract class RNode extends Node {

    public ASTNode getAST() {
        return ((RNode) getParent()).getAST();
    }

    public abstract Object execute(RContext context, Frame frame);

    public int executeScalarLogical(RContext context, Frame frame) throws UnexpectedResultException {
        return RValueConversion.expectScalarLogical((RAny) execute(context, frame));
    }

    public Object executeVoid(RContext context, Frame frame) {
        execute(context, frame);
        return RNull.getNull();
    }
}
