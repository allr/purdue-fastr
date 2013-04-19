package r.nodes.truffle;

import r.Truffle.*;

import r.nodes.*;

public class Sequence extends BaseR {

    @Children final RNode[] exprs;

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        adoptChildren(exprs);
    }

    @Override @ExplodeLoop public final Object execute(Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = null; // NOTE: this line is important, it allows the GC to clean-up temporaries
            res = e.execute(frame);
        }
        return res;
    }

    @Override public void replace0(RNode o, RNode n) {
        replace(exprs, o, n);
    }

}
