package r.nodes.tools;

import r.*;
import r.nodes.*;

// WARNING: the duplication only duplicates nodes that have child nodes (e.g. for node rewriting tricks)
// WARNING: it does not duplicate notes with no state or with a state that does not include nodes ! (see result = n below)
public class DuplicateVisitor extends BasicVisitor implements Visitor {
    protected ASTNode result;

    public ASTNode duplicate(ASTNode orig) {
        result = null;
        orig.accept(this);
        return result;
    }

    private ASTNode d(ASTNode n) {
        if (n == null) {
            return null;
        }
        n.accept(this);
        return result;
    }

    private ASTNode[] d(ASTNode[] nodes) {
        ASTNode[] newNodes = new ASTNode[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            newNodes[i] = d(nodes[i]);
        }
        return newNodes;
    }

    private AccessVector d(AccessVector n) {
        return new AccessVector(d(n.getVector()), d(n.getArgs()), n.isSubset());
    }

    protected ArgumentList d(ArgumentList l) {
        ArgumentList newList = new ArgumentList.Default();
        for(ArgumentList.Entry e : l) {
            newList.add(e.getName(), d(e.getValue()));
        }
        return newList;
    }

    @Override
    public void visit(Sequence n) {
        result = Sequence.create(d(n.getExprs()));
    }

    @Override
    public void visit(If n) {
        result = If.create(d(n.getCond()), d(n.getTrueCase()), d(n.getFalseCase()));
    }

    @Override
    public void visit(Repeat n) {
        result = new Repeat(d(n.getBody()));
    }

    @Override
    public void visit(While n) {
        result = new While(d(n.getCond()), d(n.getBody()));
    }

    @Override
    public void visit(For n) {
        result = new For(n.getCVar(), d(n.getRange()), d(n.getBody()));
    }

    @Override
    public void visit(Next n) {
        result = n;
    }

    @Override
    public void visit(Break n) {
        result = n;
    }

    @Override
    public void visit(EQ n) {
        result = new EQ(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(NE n) {
        result = new NE(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(LE n) {
        result = new LE(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(GE n) {
        result = new GE(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(LT n) {
        result = new LT(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(GT n) {
        result = new GT(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Sub n) {
        result = new Sub(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(MatMult n) {
        result = new MatMult(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(OuterMult n) {
        result = new OuterMult(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(IntegerDiv n) {
        result = new IntegerDiv(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(In n) {
        result = new In(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Mod n) {
        result = new Mod(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Pow n) {
        result = new Pow(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Div n) {
        result = new Div(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Colon n) {
        result = new Colon(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(And n) {
        result = new And(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(ElementwiseAnd n) {
        result = new ElementwiseAnd(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Or n) {
        result = new Or(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(ElementwiseOr n) {
        result = new ElementwiseOr(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Add n) {
        result = new Add(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Mult n) {
        result = new Mult(d(n.getLHS()), d(n.getRHS()));
    }

    @Override
    public void visit(Not n) {
        result = new Not(d(n.getLHS()));
    }

    @Override
    public void visit(UnaryMinus n) {
        result = new UnaryMinus(d(n.getLHS()));
    }

    @Override
    public void visit(Constant n) {
        result = n;
    }

    @Override
    public void visit(SimpleAccessVariable n) {
        result = n;
    }

    @Override
    public void visit(FieldAccess n) {
        result = new FieldAccess(d(n.lhs()), n.fieldName());
    }

    @Override
    public void visit(SimpleAssignVariable n) {
        result = new SimpleAssignVariable(n.isSuper(), n.getSymbol(), d(n.getExpr()));
    }

    @Override
    public void visit(UpdateVector n) {
        result = new UpdateVector(n.isSuper(), d(n.getVector()), d(n.getRHS()));
    }

    @Override
    public void visit(UpdateField n) {
        result = new UpdateField(n.isSuper(), (FieldAccess) d(n.getVector()), d(n.getRHS()));
    }

    @Override
    public void visit(FunctionCall n) {
        result = new FunctionCall(n.getName(), d(n.getArgs()));
    }

    @Override
    public void visit(AccessVector n) {
        result = new AccessVector(d(n.getVector()), d(n.getArgs()), n.isSubset());
    }

    @Override
    public void visit(Function n) {
        result = Function.create(d(n.getSignature()), d(n.getBody()));
    }

    @Override
    public void visit(ASTNode n) {
        Utils.nyi("todo: support "+n.getClass());
    }

}
