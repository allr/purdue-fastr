package r.nodes.truffle;


import com.oracle.truffle.api.frame.*;

import r.data.*;
import r.nodes.*;


public class Function extends RNode {
    final RFunction function;

    public Function(RFunction function) {
        this.function = function;
    }

    @Override
    public final Object execute(Frame frame) {
        return function.createClosure(frame == null ? null : frame.materialize());
    }

    @Override
    public final ASTNode getAST() {
     return function.getSource();
    }
}
