package r.nodes.ast;

@PrettyName("+")
@Precedence(Operation.ADD_PRECEDENCE)
public class Add extends BinaryOperation {
    public Add(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
