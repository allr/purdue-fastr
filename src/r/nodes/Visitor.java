package r.nodes;

import r.data.Function.Closure;

public interface Visitor {
    void visit(Sequence sequence);
    void visit(If iff);
    void visit(Add add);
    void visit(Not n);

    void visit(Closure closure);
    void visit(Constant constant);
    void visit(VariableAccess readVariable);
    void visit(FieldAccess fieldAccess);
}
