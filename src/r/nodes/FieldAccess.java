package r.nodes;


public class FieldAccess extends Node {
    Node lhs;
    String name;

    public FieldAccess(Node value, String fieldName) {
        lhs = updateParent(value);
        name = fieldName;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        lhs.accept(v);
    }

    public static Node create(FieldOperator op, Node value, String fieldName) {
        switch (op) {
            case AT: // Since I dunno what does AT, I put this case
            case FIELD: return new FieldAccess(value, fieldName);
        }
        throw new Error("No node implemented for: '" + op + "' (" + value + ": " + fieldName + ")");
    }
}
