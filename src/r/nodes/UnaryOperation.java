package r.nodes;

public abstract class UnaryOperation extends Operation {
    public UnaryOperation(ASTNode op) {
        super(op);
    }

    @Override
    public void accept(Visitor v) {
        getLHS().accept(v);
    }

    public static ASTNode create(UnaryOperator op, ASTNode operand) {
        switch (op) {
            case PLUS: return new Not(operand);
            case MINUS: return new UnaryMinus(operand);
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
