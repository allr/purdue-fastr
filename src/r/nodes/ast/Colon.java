package r.nodes.ast;

@PrettyName(":")
@Precedence(Operation.COLON_PRECEDENCE)
public class Colon extends BinaryOperation {
    public Colon(ASTNode l, ASTNode r) {
        super(l, r);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
