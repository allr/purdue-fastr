package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.*;
import r.nodes.*;


public class Sequence extends BaseR {

    @Stable RNode[] exprs;

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        updateParent(exprs);
    }

    @Override
    @ExplodeLoop // needed to make Truffle happy
    public Object execute(RContext context, Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = e.execute(context, frame);
        }
        return res;
    }
}
