package r.nodes;

@PrettyName(">")
@Precedence(Operation.COMPARE_PRECEDENCE)
public class GT extends BinaryOperation {

    public GT(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}
