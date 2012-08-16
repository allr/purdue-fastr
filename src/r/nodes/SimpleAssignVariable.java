package r.nodes;

import r.data.*;

public class SimpleAssignVariable extends AssignVariable {
    RSymbol variable;

    public SimpleAssignVariable(RSymbol var, Node rhs) {
        super(rhs);
        variable = var;
    }

    public RSymbol getSymbol() {
        return variable;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
