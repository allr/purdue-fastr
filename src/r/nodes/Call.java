package r.nodes;

import r.data.*;
import r.nodes.ArgumentList.Entry;

public abstract class Call extends Node {

    ArgumentList args;

    public Call(ArgumentList alist) {
        args = alist;
    }

    @Override
    public void visit_all(Visitor v) {
        for (Entry e : args) {
            e.getValue().accept(v);
        }
    }

    public ArgumentList getArgs() {
        return args;
    }

    public static Node create(Node call, ArgumentList args) {
        if (call instanceof VariableAccess) {
            VariableAccess ccall = (VariableAccess) call;
            return create(ccall.getName(), args);
        }
        return null;
    }

    public static Node create(RSymbol funName, ArgumentList args) {
        return new FunctionCall(funName, args);
    }

    public static Node create(CallOperator op, Node lhs, ArgumentList args) {
        return null;
    }

    public enum CallOperator {
        SUBSET, SUBSCRIPT
    }
}
