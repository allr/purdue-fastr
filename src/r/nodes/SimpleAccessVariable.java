package r.nodes;

import r.data.*;

@Precedence(Precedence.MAX)
public class SimpleAccessVariable extends AccessVariable {
    RSymbol name;

    public SimpleAccessVariable(RSymbol symbol) {
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

    @Override
    public String toString() {
        return name.pretty();
    }
}
