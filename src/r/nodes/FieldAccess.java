package r.nodes;


public class FieldAccess implements Node {
    Node lhs;
    String name;

    public FieldAccess(Node value, String fieldName) {
        lhs = value;
        name = fieldName;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

}
