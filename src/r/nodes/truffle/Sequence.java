package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.*;
import r.nodes.*;


public class Sequence extends BaseR {

    @ContentStable final RNode[] exprs;

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        updateParent(exprs);
    }

    @Override
    @ExplodeLoop
    public final Object execute(RContext context, Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = e.execute(context, frame);
        }
        return res;
    }
}
