package r.nodes;

@PrettyName("|")
@Precedence(Operation.OR_PRECEDENCE)
public class ElementwiseOr extends BinaryOperation {

    public ElementwiseOr(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}

