package r.nodes.ast;

@PrettyName("&")
@Precedence(Operation.AND_PRECEDENCE)
public class ElementwiseAnd extends BinaryOperation {

    public ElementwiseAnd(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
