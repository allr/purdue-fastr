package r.nodes.tools;

import r.*;
import r.nodes.*;
import r.nodes.If;
import r.nodes.Constant;
import r.nodes.truffle.*;

public class Truffleize implements Visitor {

    RNode result;

    public RNode createTree(ASTNode ast) {
        ast.accept(this);
        return result;
    }

    public RNode createLazyTree(ASTNode ast) {
        return new LazyBuildNode(ast);
    }

    @Override
    public void visit(If iff) {
        ASTNode fbranch = iff.getFalseCase();
        result = new r.nodes.truffle.If(iff, createLazyTree(iff.getCond()), createLazyTree(iff.getTrueCase()), fbranch == null ? r.nodes.truffle.Constant.getNull() : createLazyTree(fbranch));
    }

    @Override
    public void visit(Repeat repeat) {
    }

    @Override
    public void visit(While wh1le) {
    }

    @Override
    public void visit(Sequence sequence) {
    }

    @Override
    public void visit(Mult mult) {
    }

    @Override
    public void visit(Add add) {
    }

    @Override
    public void visit(Not n) {
    }

    @Override
    public void visit(Constant constant) {
        result = new r.nodes.truffle.Constant(constant, constant.getValue());
    }

    @Override
    public void visit(SimpleAccessVariable readVariable) {
    }

    @Override
    public void visit(FieldAccess fieldAccess) {
    }

    @Override
    public void visit(SimpleAssignVariable assign) {
        if (assign.isSuper()) {
            Utils.nyi();
        }
        result = new r.nodes.truffle.AssignVariable(assign, assign.getSymbol(), createLazyTree(assign.getExpr()));
    }

    @Override
    public void visit(Function function) {
    }

    @Override
    public void visit(FunctionCall functionCall) {
    }

}
