package r.nodes;

public class If extends Node {
    Node cond;
    Node trueCase;
    Node falseCase;

    If(Node cond, Node truecase, Node falsecase) {
        setCond(cond);
        setTrueCase(truecase);
        setFalseCase(falsecase);
    }

    public Node getCond() {
        return cond;
    }

    public Node getTrueCase() {
        return trueCase;
    }

    public Node getFalseCase() {
        return falseCase;
    }

    public void setCond(Node cond) {
        this.cond = updateParent(cond);
    }

    public void setTrueCase(Node trueCase) {
        this.trueCase = updateParent(trueCase);
    }

    public void setFalseCase(Node falseCase) {
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
        getFalseCase().accept(v);
    }

    public static If create(Node cond, Node trueBranch) {
        return create(cond, trueBranch, null);
    }

    public static If create(Node cond, Node trueBranch, Node falseBranch) {
        return new If(cond, trueBranch, falseBranch);
    }
}
