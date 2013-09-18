package r.nodes.ast;


public class Next extends ControlStatement {

    public static Next create() {
        return new Next();
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
