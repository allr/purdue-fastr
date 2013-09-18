package r.nodes.ast;

@PrettyName("%/%")
@Precedence(Operation.INTEGER_DIV_PRECEDENCE)
public class IntegerDiv extends BinaryOperation {

    public IntegerDiv(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}
