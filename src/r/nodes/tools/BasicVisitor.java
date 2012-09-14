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

    public void visit(BinaryOperation op) {
        op.visit_all(this);
        visit((ASTNode) op);
    }

    @Override
    public void visit(EQ op) {
        visit((BinaryOperation) op);
    }

    @Override
    public void visit(LE op) {
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
    public void visit(Colon col) {
        visit((BinaryOperation) col);
    }

    public void visit(UnaryOperation op) {
        visit((ASTNode) op);
    }

    @Override
    public void visit(Not op) {
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

    public void visit(Call n) {
        visit((ASTNode) n);
    }

    @Override
    public void visit(FunctionCall n) {
        visit((Call) n);
    }

    @Override
    public void visit(Function n) {
        visit((ASTNode) n);
    }
}
