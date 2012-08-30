package r.nodes;

import r.data.*;

public class FunctionCall extends Call {

    RSymbol name; // FIXME: LHS of a call does not need to be a symbol, it can be a lambda expression

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
