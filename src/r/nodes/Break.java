package r.nodes;


public class Break extends ControlStatement {
    public static Break create() {
        return new Break();
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
