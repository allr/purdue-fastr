package r.nodes;


public class VariableAccess implements Node {
    Symbol name;

    public VariableAccess(Symbol symbol) {
       name = symbol;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
