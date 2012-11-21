package r.nodes;

import r.*;
import r.data.*;
import r.nodes.ArgumentList.Entry;

public abstract class Call extends ASTNode {

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

    public static ASTNode create(ASTNode call, ArgumentList args) {
        if (call instanceof SimpleAccessVariable) {
            SimpleAccessVariable ccall = (SimpleAccessVariable) call;
            return create(ccall.getSymbol(), args);
        }
        return null;
    }

    public static ASTNode create(RSymbol funName, ArgumentList args) {
        return new FunctionCall(funName, args);
    }

    public static ASTNode create(CallOperator op, ASTNode lhs, ArgumentList args) {
        return new AccessVector(lhs, args, op == CallOperator.SUBSET);
    }

    public enum CallOperator {
        SUBSET, SUBSCRIPT
    }
}
