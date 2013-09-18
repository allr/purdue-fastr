package r.nodes.ast;

import r.data.*;

public class SimpleAssignVariable extends AssignVariable {
    RSymbol variable;

    public SimpleAssignVariable(boolean isSuper, RSymbol var, ASTNode rhs) {
        super(isSuper, rhs);
        variable = var;
    }

    public RSymbol getSymbol() {
        return variable;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(variable.pretty());
        str.append("<-");
        str.append(rhs.toString());
        return str.toString();
    }
}
