package r.nodes.ast;

import r.data.*;

public class FunctionCall extends Call {

    RSymbol name; // this node is only for calls that have a symbol on the LHS
    boolean isAssignment; // this node only supports non-reflective replacement calls
    boolean isSuper;

    public FunctionCall(RSymbol funName, ArgumentList args) {
        super(args);
        name = funName;
    }

    public FunctionCall(RSymbol funName, ArgumentList args, boolean isAssignment, boolean isSuper) {
        super(args);
        name = funName;
        this.isAssignment = isAssignment;
        this.isSuper = isSuper;
    }

    public RSymbol getName() {
        return name;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public boolean isAssignment() {
        return isAssignment;
    }

    public void isSuper(boolean value) {
        this.isSuper = value;
    }

    public void isAssignment(boolean value) {
        this.isAssignment = value;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
    }
}
