package r.nodes;

import r.data.*;


public class VariableAccess extends Node {
    RSymbol name;

    public VariableAccess(RSymbol symbol) {
       name = symbol;
    }

    public RSymbol getName() {
        return name;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) { }

    public static Node create(String name) {
        return new VariableAccess(RSymbol.getSymbol(name));
    }
}
