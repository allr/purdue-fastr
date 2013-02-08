package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.nodes.*;

public class Sequence extends BaseR {

    @Children final RNode[] exprs;

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        adoptChildren(exprs);
    }

    @Override
    @ExplodeLoop
    public final Object execute(Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = e.execute(frame);
        }
        return res;
    }
}
