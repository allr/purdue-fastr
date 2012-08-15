package r.nodes;

@PrettyName("+")
@Precedence(Operation.ADD_PRECEDENCE)
public class Add extends BinaryOperation {
    public Add(Node l, Node r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
