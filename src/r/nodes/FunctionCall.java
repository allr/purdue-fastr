package r.nodes;

import r.data.*;

public class FunctionCall extends Call {

    RSymbol name;

    public FunctionCall(RSymbol funName, ArgumentList args) {
        super(args);
        name = funName;
    }

    public RSymbol getName() {
        return name;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
    }
}
