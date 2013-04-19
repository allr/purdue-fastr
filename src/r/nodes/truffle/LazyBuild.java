package r.nodes.truffle;

import r.Truffle.*;

import r.*;
import r.nodes.*;

public class LazyBuild extends BaseR {

    public LazyBuild(ASTNode orig) {
        super(orig);
    }

    @Override public final Object execute(Frame frame) {
        return replace(RContext.createNode(getAST()), "expandLazyBuildNode").execute(frame);
    }

    @Override public final int executeScalarLogical(Frame frame) throws UnexpectedResultException {
        return replace(RContext.createNode(getAST()), "expandLazyBuildNode").executeScalarLogical(frame);
    }

    @Override public final int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
        return replace(RContext.createNode(getAST()), "expandLazyBuildNode").executeScalarNonNALogical(frame);
    }

    @Override public void replace0(RNode o, RNode n) {
        // TODO Auto-generated method stub

    }

}
