package r.nodes.ast;

@PrettyName("||")
@Precedence(Operation.OR_PRECEDENCE)
public class Or extends BinaryOperation {

    public Or(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}

