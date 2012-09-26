package r.nodes.truffle;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.nodes.*;

public class Function extends RNode {
    final RFunction function;

    public Function(RFunction function) {
        this.function = function;
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        return function.createClosure(frame);
    }

    @Override
    public ASTNode getAST() {
     return function.getSource();
    }
}
