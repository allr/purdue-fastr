package r.nodes;

public interface Visitor {
    void visit(If iff);
    void visit(Repeat repeat);
    void visit(While wh1le);
    void visit(Sequence sequence);

    void visit(EQ eq);
    void visit(LE le);
    void visit(Mult mult);
    void visit(Add add);
    void visit(Sub sub);

    void visit(Not n);

    void visit(Constant constant);
    void visit(SimpleAccessVariable readVariable);
    void visit(FieldAccess fieldAccess);

    void visit(SimpleAssignVariable assign);

    void visit(Function function);
    void visit(FunctionCall functionCall);
}
