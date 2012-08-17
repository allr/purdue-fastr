package r.nodes;

@Precedence(1)
public class Not extends UnaryOperation {
    public static final String OPERATOR = "!";

    Not(Node operand) {
        super(operand);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String getPrettyOperator() {
        return Not.OPERATOR;
    }
}
