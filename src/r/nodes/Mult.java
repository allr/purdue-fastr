package r.nodes;

@PrettyName("*")
@Precedence(Operation.MULT_PRECEDENCE)
public class Mult extends BinaryOperation {
    public Mult(Node l, Node r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
