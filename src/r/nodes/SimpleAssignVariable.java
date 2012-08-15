package r.nodes;

public class SimpleAssignVariable extends AssignVariable {
    Symbol variable;

    public SimpleAssignVariable(Symbol var, Node rhs) {
        super(rhs);
        variable = var;
    }

    public Symbol getSymbol() {
        return variable;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
