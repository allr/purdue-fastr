package r.nodes.ast;

@PrettyName("*")
@Precedence(Operation.POW_PRECEDENCE)
public class Pow extends BinaryOperation {
    public Pow(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
