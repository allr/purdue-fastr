package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

public class LazyBuildNode extends BaseRNode {

    public LazyBuildNode(ASTNode orig) {
        super(orig);
        assert Utils.check(orig != null);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RNode node = context.createNode(getAST());
        replace(node, null);
        return node.execute(context, frame);
    }

}
