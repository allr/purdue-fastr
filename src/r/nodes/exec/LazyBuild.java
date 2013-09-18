package r.nodes.exec;

import r.*;
import r.nodes.ast.*;
import r.runtime.*;

public class LazyBuild extends BaseR {

    public LazyBuild(ASTNode orig) {
        super(orig);
        assert Utils.check(orig != null);
    }

    @Override
    public final Object execute(Frame frame) {
        try {
            throw new SpecializationException(null);
        } catch (SpecializationException e) {
            RNode node = RContext.createNode(getAST());
            replace(node, "expandLazyBuildNode");
            return node.execute(frame);
        }
    }

    @Override
    public final int executeScalarLogical(Frame frame) throws SpecializationException {
        try {
            throw new SpecializationException(null);
        } catch (SpecializationException e) {
            RNode node = RContext.createNode(getAST());
            replace(node, "expandLazyBuildNode");
            return node.executeScalarLogical(frame);
        }
    }

    @Override
    public final int executeScalarNonNALogical(Frame frame) throws SpecializationException {
        try {
            throw new SpecializationException(null);
        } catch (SpecializationException e) {
            RNode node = RContext.createNode(getAST());
            replace(node, "expandLazyBuildNode");
            return node.executeScalarNonNALogical(frame);
        }
    }

}
