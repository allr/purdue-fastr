package r.nodes.ast;

import r.data.*;

@Precedence(Precedence.MAX)
public class SimpleAccessVariable extends AccessVariable {
    RSymbol symbol;

    public SimpleAccessVariable(RSymbol sym) {
       symbol = sym;
    }

    public RSymbol getSymbol() {
        return symbol;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) { }

    @Override
    public String toString() {
        return symbol.pretty();
    }
}
