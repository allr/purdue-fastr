package r.nodes.ast;

@PrettyName("-")
@Precedence(Operation.SUB_PRECEDENCE)
public class Sub extends BinaryOperation {
    public Sub(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
