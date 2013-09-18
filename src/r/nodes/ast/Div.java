package r.nodes.ast;

@PrettyName("*")
@Precedence(Operation.MULT_PRECEDENCE)
public class Div extends BinaryOperation {
    public Div(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
