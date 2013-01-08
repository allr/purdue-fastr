package r.nodes.tools;

import r.nodes.*;

public class BasicVisitor implements Visitor {

    /**
     * @param n
     */
    public void visit(ASTNode n) {
        n.visit_all(this);
    }

    @Override
    public void visit(Sequence n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(If n) {
        visit((ASTNode) n);
    }

    public void visit(Loop n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(Repeat n) {
        visit((Loop) n);
    }

    @Override
    public void visit(While n) {
        visit((Loop) n);
    }

    @Override
    public void visit(For n) {
        visit((Loop) n);
    }

    public void visit(ControlStatement n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(Next n) {
        visit((ControlStatement) n);
    }

    @Override
    public void visit(Break n) {
        visit((ControlStatement) n);
    }

    public void visit(BinaryOperation op) {
        op.visit_all(this);
        visit((ASTNode) op); // FIXME: why? isn't this in op.visit_all already?
    }

    @Override
    public void visit(EQ op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(NE op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(LE op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(GE op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(LT op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(GT op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Add op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Sub op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Mult op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(MatMult op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(OuterMult op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(IntegerDiv op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(In op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Mod op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Pow op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Div op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(Colon col) {
        visit((BinaryOperation) col);
    }

    @Override
    public void visit(And and) {
        visit((BinaryOperation) and);
    }

    @Override
    public void visit(ElementwiseAnd and) {
        visit((BinaryOperation) and);
    }

    @Override
    public void visit(Or or) {
        visit((BinaryOperation) or);
    }

    @Override
    public void visit(ElementwiseOr or) {
        visit((BinaryOperation) or);
    }

    public void visit(UnaryOperation op) {
        visit((ASTNode) op);
    }

    @Override
    public void visit(Not op) {
        visit((UnaryOperation) op);
    }

    @Override
    public void visit(UnaryMinus op) {
        visit((UnaryOperation) op);
    }

    @Override
    public void visit(Constant c) {
        visit((ASTNode) c);
    }

    @Override
    public void visit(SimpleAccessVariable n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(FieldAccess n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(SimpleAssignVariable n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(UpdateVector u) {
        visit((ASTNode) u);
    }

    public void visit(Call n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(FunctionCall n) {
        visit((Call) n);
    }

    @Override
    public void visit(AccessVector a) {
        a.visit_all(this);
    }

    @Override
    public void visit(Function n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(ArgumentList.Default.DefaultEntry entry) {
        visit((ASTNode) entry);
    }

}
