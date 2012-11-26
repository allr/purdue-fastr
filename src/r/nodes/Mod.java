package r.nodes;

@PrettyName("%%")
@Precedence(Operation.MOD_PRECEDENCE)
public class Mod extends BinaryOperation {

    public Mod(ASTNode l, ASTNode r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}
