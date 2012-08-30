package r.nodes;

public class If extends ASTNode {

    ASTNode cond;
    ASTNode trueCase;
    ASTNode falseCase;

    If(ASTNode cond, ASTNode truecase, ASTNode falsecase) {
        setCond(cond);
        setTrueCase(truecase);
        setFalseCase(falsecase);
    }

    public ASTNode getCond() {
        return cond;
    }

    public ASTNode getTrueCase() {
        return trueCase;
    }

    public ASTNode getFalseCase() {
        return falseCase;
    }

    public void setCond(ASTNode cond) {
        this.cond = updateParent(cond);
    }

    public void setTrueCase(ASTNode trueCase) {
        this.trueCase = updateParent(trueCase);
    }

    public void setFalseCase(ASTNode falseCase) {
        this.falseCase = updateParent(falseCase);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        getCond().accept(v);
        getTrueCase().accept(v);
        ASTNode fcase = getFalseCase();
        if (fcase != null) {
            fcase.accept(v);
        }
    }

    public static If create(ASTNode cond, ASTNode trueBranch) {
        return create(cond, trueBranch, null);
    }

    public static If create(ASTNode cond, ASTNode trueBranch, ASTNode falseBranch) {
        return new If(cond, trueBranch, falseBranch);
    }
}
