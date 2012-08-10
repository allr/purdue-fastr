package r.nodes;

public class UnaryOperation implements Node {
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
}
