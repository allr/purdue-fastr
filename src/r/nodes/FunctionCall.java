package r.nodes;

import r.data.*;

public class FunctionCall extends Call {

    RSymbol name; // FIXME: LHS of a call does not need to be a symbol, it can be a lambda expression
    boolean isAssignment;
    boolean isSuper;

    public FunctionCall(RSymbol funName, ArgumentList args) {
        super(args);
        name = funName;
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
