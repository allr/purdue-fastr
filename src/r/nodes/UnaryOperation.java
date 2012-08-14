package r.nodes;

public abstract class UnaryOperation implements Node {
    Node operand;

    public UnaryOperation(Node op) {
        setOperand(op);
    }

    public Node getOperand() {
        return operand;
    }

    public void setOperand(Node operand) {
        this.operand = operand;
    }

    @Override
    public void accept(Visitor v) {
        getOperand().accept(v);
    }

    public abstract String getPrettyOperator();
}
