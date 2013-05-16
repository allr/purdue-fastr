package r.nodes;


public class While extends Loop {
    ASTNode cond;
    public While(ASTNode cond, ASTNode expr) {
        super(expr);
        setCond(cond);
    }

    public ASTNode getCond() {
        return cond;
    }

    public void setCond(ASTNode cond) {
        this.cond = updateParent(cond);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
        getCond().accept(v);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
