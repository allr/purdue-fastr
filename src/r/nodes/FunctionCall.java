package r.nodes;


public class FunctionCall extends Call {

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
    }

}
