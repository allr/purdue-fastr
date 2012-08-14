package r.nodes.tools;

import r.data.Function.Closure;
import r.nodes.*;


public class BasicVisitor implements Visitor {
    /**
     * @param n
     */
    public void visit(Node n) { }

    @Override
    public void visit(Sequence n) { visit((Node) n); }

    @Override
    public void visit(If n) { visit((Node) n); }

    public void visit(BinaryOperation op) { op.visit_all(this); visit((Node) op); }
    @Override
    public void visit(Add op) { visit((BinaryOperation) op); }

    public void visit(UnaryOperation op) { visit((Node) op); }
    @Override
    public void visit(Not op) { visit((UnaryOperation) op); }

    @Override
    public void visit(Closure n) { visit((Node) n); }

    @Override
    public void visit(Constant c) { visit((Node) c); }

    @Override
    public void visit(VariableAccess n) { visit((Node) n); }

    @Override
    public void visit(FieldAccess n) { visit((Node) n); }
}
