package r.nodes;

import r.data.*;


public class For extends Loop {

    RSymbol cvar;
    ASTNode range;

    public For(RSymbol cvar, ASTNode range, ASTNode body) {
        super(body);
        this.cvar = cvar;
        this.range = range;
    }

    public ASTNode getRange() {
        return range;
    }

    public RSymbol getCVar() {
        return cvar;
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
        range.accept(v);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
