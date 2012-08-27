package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

public class LazyBuild extends BaseR {

    public LazyBuild(ASTNode orig) {
        super(orig);
        assert Utils.check(orig != null);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RNode node = context.createNode(getAST());
        replace(node, "expandLazyBuildNode");
        return node.execute(context, frame);
    }

}
