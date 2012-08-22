package r.nodes.tools;

import r.nodes.*;
import r.nodes.If;
import r.nodes.truffle.*;
import r.nodes.Constant;


public class Truffleize implements Visitor {
    RNode result;

    public RNode createTree(ASTNode ast) {
        ast.accept(this);
        return result;
    }

    @Override
    public void visit(If iff) {
        ASTNode fbranch = iff.getFalseCase();
        result = new r.nodes.truffle.If(iff, createTree(iff.getCond()), createTree(iff.getTrueCase()), fbranch == null ? r.nodes.truffle.Constant.getNull() : createTree(fbranch));
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
    }

    @Override
    public void visit(Function function) {
    }

    @Override
    public void visit(FunctionCall functionCall) {
    }

}
