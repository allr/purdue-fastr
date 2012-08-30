package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;


public class Function extends BaseR {

    final RFunction function;
    RNode body;

    public Function(ASTNode ast, RFunction function, RNode body) {
        super(ast);
        this.function = function;
        this.body = updateParent(body);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        return function; // NOTE: do not execute the function, this just returns a function object
    }

    public RNode body() {
        return body;
    }
}
