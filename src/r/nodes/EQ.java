package r.nodes;

@PrettyName("==")
@Precedence(Operation.EQ_PRECEDENCE)
public class EQ extends BinaryOperation {

    public EQ(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
