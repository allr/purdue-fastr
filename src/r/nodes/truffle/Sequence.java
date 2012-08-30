package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;


public class Sequence extends BaseR {

    final RNode[] exprs; // FIXME: elements cannot be final; can Truffle handle this?

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        updateParent(exprs);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = e.execute(context, frame);
        }
        return res;
    }
}
