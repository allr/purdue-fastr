package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;


public class Function extends BaseR {

    final RFunction function;
    final RSymbol[] names;
    RNode body;
    RNode[] expressions;

    public Function(ASTNode ast, RFunction function, RSymbol[] argNames, RNode[] argExprs, RNode body) {
        super(ast);
        this.function = function;
        this.names = argNames;
        this.expressions = updateParent(argExprs);
        this.body = updateParent(body);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        return function; // NOTE: do not execute the function, this just returns a function object
    }

    public RNode body() {
        return body;
    }

    public RSymbol[] argNames() {
        return names;
    }

    public RNode[] argExprs() {
        return expressions;
    }
}
