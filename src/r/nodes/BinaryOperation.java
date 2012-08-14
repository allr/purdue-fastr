package r.nodes;


public abstract class BinaryOperation implements Node {
    Node left;
    Node right;

    public BinaryOperation(Node left, Node right) {
        setLeft(left);
        setRight(right);
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void visit_all(Visitor v) {
        getLeft().accept(v);
        getRight().accept(v);
    }

    public abstract String getPrettyOperator();
}
