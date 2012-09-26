package r.nodes.truffle;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.nodes.*;


public class Sequence extends BaseR {

    final RNode[] exprs; // FIXME: elements cannot be final; can Truffle handle this?

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        updateParent(exprs);
    }

    @Override
    public Object execute(RContext context, Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = e.execute(context, frame);
        }
        return res;
    }
}
