package r.nodes;


public class Add extends BinaryOperation implements Node {
    public static final String OPERATOR = "+";

    public Add(Node l, Node r) {
        super(l, r);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String getPrettyOperator() {
        return Add.OPERATOR;
    }
}
