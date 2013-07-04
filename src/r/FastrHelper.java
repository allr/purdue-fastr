package r;


class Node {

    public Node() { }

    public Node(Node other) { }

    public Node copy() {
        System.out.println("Calling unoverriden copy of node");
        return new Node();
    }

    public <T> T adoptChild(T child) {
        return child;
    }
}

class AdvancedNode extends Node {
    public Node n;
    public AdvancedNode(Node n) {
        this.n = n;
    }
}





public class FastrHelper {

    static Node createAnonymous(final int carg) {
        return new Node() {
            int getArg() {
                return carg;
            }
        } ;
    }


    public static void main(String[] args) {
        AdvancedNode n = new AdvancedNode(createAnonymous(3));
        Node n2 = n.copy();
        System.out.println("END");
    }

}
