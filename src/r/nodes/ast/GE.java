package r.nodes.ast;

@PrettyName(">=")
@Precedence(Operation.COMPARE_PRECEDENCE)
public class GE extends BinaryOperation {

    public GE(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}
