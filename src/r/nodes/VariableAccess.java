package r.nodes;


public class VariableAccess extends Node {
    Symbol name;

    public VariableAccess(Symbol symbol) {
       name = symbol;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) { }

    public static Node create(String name) {
        return new VariableAccess(Symbol.getSymbol(name));
    }
}
