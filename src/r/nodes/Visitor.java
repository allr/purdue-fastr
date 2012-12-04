package r.nodes;

public interface Visitor {
    void visit(If iff);
    void visit(Repeat repeat);
    void visit(While wh1le);
    void visit(For n);
    void visit(Sequence sequence);

    void visit(Break n);
    void visit(Next n);

    void visit(EQ eq);
    void visit(NE ne);
    void visit(LE le);
    void visit(GE ge);
    void visit(LT lt);
    void visit(GT gt);
    void visit(Mult mult);
    void visit(MatMult mult);
    void visit(OuterMult mult);
    void visit(IntegerDiv div);
    void visit(Mod mod);
    void visit(Pow pow);
    void visit(Div div);
    void visit(Add add);
    void visit(Sub sub);
    void visit(Colon col);
    void visit(And and);
    void visit(ElementwiseAnd and);
    void visit(Or or);
    void visit(ElementwiseOr or);

    void visit(Not n);
    void visit(UnaryMinus m);

    void visit(Constant constant);
    void visit(SimpleAccessVariable readVariable);
    void visit(FieldAccess fieldAccess);

    void visit(SimpleAssignVariable assign);
    void visit(UpdateVector update);

    void visit(Function function);
    void visit(FunctionCall functionCall);
    void visit(AccessVector accessVector);
    void visit(ArgumentList.Default.DefaultEntry entry);
}
