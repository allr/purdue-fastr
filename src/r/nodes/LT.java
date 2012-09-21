package r.nodes;

@PrettyName("<")
@Precedence(Operation.COMPARE_PRECEDENCE)
public class LT extends BinaryOperation {

    public LT(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}
