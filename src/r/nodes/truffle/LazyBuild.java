package r.nodes.truffle;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.nodes.*;

public class LazyBuild extends BaseR {

    public LazyBuild(ASTNode orig) {
        super(orig);
        assert Utils.check(orig != null);
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RNode node = context.createNode(getAST());
        replace(node, "expandLazyBuildNode");
        return node.execute(context, frame);
    }

}
