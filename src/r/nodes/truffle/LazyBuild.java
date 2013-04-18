package r.nodes.truffle;

import r.Truffle.*;

import r.*;
import r.nodes.*;

public class LazyBuild extends BaseR {

    public LazyBuild(ASTNode orig) {
        super(orig);
        assert Utils.check(orig != null);
    }

    @Override public final Object execute(Frame frame) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            RNode node = RContext.createNode(getAST());
            replace(node, "expandLazyBuildNode");
            return node.execute(frame);
        }
    }

    @Override public final int executeScalarLogical(Frame frame) throws UnexpectedResultException {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            RNode node = RContext.createNode(getAST());
            replace(node, "expandLazyBuildNode");
            return node.executeScalarLogical(frame);
        }
    }

    @Override public final int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            RNode node = RContext.createNode(getAST());
            replace(node, "expandLazyBuildNode");
            return node.executeScalarNonNALogical(frame);
        }
    }

}
