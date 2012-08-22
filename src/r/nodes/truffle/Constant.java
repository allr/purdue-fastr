package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;


public class Constant extends BaseRNode {
    final RAny value;

    public Constant(ASTNode ast, RAny val) {
        super(ast);
        value = val;
    }

    @Override
    public RAny execute(RContext context, RFrame frame) {
        return value;
    }

    public static RNode getNull() {
        return new Constant(null, RNull.getNull());
    }
}
