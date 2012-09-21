package r.nodes;

public interface Visitor {
    void visit(If iff);
    void visit(Repeat repeat);
    void visit(While wh1le);
    void visit(Sequence sequence);

    void visit(EQ eq);
    void visit(NE ne);
    void visit(LE le);
    void visit(GE ge);
    void visit(LT lt);
    void visit(GT gt);
    void visit(Mult mult);
    void visit(Add add);
    void visit(Sub sub);
    void visit(Colon col);

    void visit(Not n);

    void visit(Constant constant);
    void visit(SimpleAccessVariable readVariable);
    void visit(FieldAccess fieldAccess);

    void visit(SimpleAssignVariable assign);
    void visit(UpdateVector update);

    void visit(Function function);
    void visit(FunctionCall functionCall);
    void visit(AccessVector accessVector);
}
