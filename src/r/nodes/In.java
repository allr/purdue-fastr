package r.nodes;

@PrettyName("%in%")
@Precedence(Operation.IN_PRECEDENCE)
public class In extends BinaryOperation {
    public In(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
