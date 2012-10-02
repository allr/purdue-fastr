package r.nodes.truffle;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.nodes.*;


public class Constant extends BaseR {
    final RAny value;

    public Constant(ASTNode ast, RAny val) {
        super(ast);
        value = val;
    }

    @Override
    public final RAny execute(RContext context, Frame frame) {
        return value;
    }

    public static RNode getNull() {
        return new Constant(null, RNull.getNull());
    }
}
