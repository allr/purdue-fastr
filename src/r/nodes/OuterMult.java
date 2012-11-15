package r.nodes;

@PrettyName("%o%")
@Precedence(Operation.OUTER_MULT_PRECEDENCE)
public class OuterMult extends BinaryOperation {
    public OuterMult(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
