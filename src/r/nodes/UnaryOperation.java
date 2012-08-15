package r.nodes;

public abstract class UnaryOperation extends Operation {
    public UnaryOperation(Node op) {
        super(op);
    }

    @Override
    public void accept(Visitor v) {
        getLHS().accept(v);
    }

    public static Node create(UnaryOperator op, Node operand) {
        switch (op) {
            case PLUS: return new Not(operand);
            case MINUS: return new Not(operand);
            case NOT: return new Not(operand);
        }
        throw new Error("No node implemented for: '" + op + "' (" + operand + ")");
    }

    public enum UnaryOperator {
        REPEAT,

        PLUS,
        MINUS,
        NOT,
        MODEL
    }
}
