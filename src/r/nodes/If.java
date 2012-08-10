package r.nodes;


public class If implements Node {
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

    public void setCond(Node cond) {
        this.cond = cond;
    }

    public Node getTrueCase() {
        return trueCase;
    }

    public void setTrueCase(Node trueCase) {
        this.trueCase = trueCase;
    }

    public Node getFalseCase() {
        return falseCase;
    }

    public void setFalseCase(Node falseCase) {
        this.falseCase = falseCase;
    }
}
