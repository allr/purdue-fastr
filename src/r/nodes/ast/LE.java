package r.nodes.ast;

@PrettyName("<=")
@Precedence(Operation.COMPARE_PRECEDENCE)
public class LE extends BinaryOperation {

    public LE(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}

