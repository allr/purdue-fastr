package r.nodes;

@PrettyName("%*%")
@Precedence(Operation.MAT_MULT_PRECEDENCE)
public class MatMult extends BinaryOperation {

    public MatMult(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
