package r.nodes.ast;

@Precedence(Operation.SIGN_PRECEDENCE)
@PrettyName("-")
public class UnaryMinus extends UnaryOperation {

    public UnaryMinus(ASTNode operand) {
        super(operand);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
