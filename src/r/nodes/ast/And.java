package r.nodes.ast;

@PrettyName("&&")
@Precedence(Operation.AND_PRECEDENCE)
public class And extends BinaryOperation {

    public And(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
