package r.nodes;

import r.*;
import r.data.*;
import r.interpreter.*;

import com.oracle.truffle.runtime.*;

public class If extends Node {

    Node cond;
    Node trueCase;
    Node falseCase;

    If(Node cond, Node truecase, Node falsecase) {
        setCond(cond);
        setTrueCase(truecase);
        setFalseCase(falsecase);
    }

    @Override
    public RAny execute(RContext global, Frame frame) {
        RLogical op = getCond().execute(global, frame).asLogical(); // FIXME asLogical is too expensive, we've to go for
// a asLogicalOne
        int ifVal = op.getLogical(0);
        if (ifVal == RLogical.TRUE) {
                return getTrueCase().execute(global, frame);
        } else if (ifVal == RLogical.FALSE) {
                Node fcase = getFalseCase();
                if (fcase == null) {
                    return RNull.getNull();
                } else {
                    return fcase.execute(global, frame);
                }
        }
        Utils.nyi();
        return RNull.getNull(); // For TypeChecker
    }

    public Node getCond() {
        return cond;
    }

    public Node getTrueCase() {
        return trueCase;
    }

    public Node getFalseCase() {
        return falseCase;
    }

    public void setCond(Node cond) {
        this.cond = updateParent(cond);
    }

    public void setTrueCase(Node trueCase) {
        this.trueCase = updateParent(trueCase);
    }

    public void setFalseCase(Node falseCase) {
        this.falseCase = updateParent(falseCase);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        getCond().accept(v);
        getTrueCase().accept(v);
        getFalseCase().accept(v);
    }

    public static If create(Node cond, Node trueBranch) {
        return create(cond, trueBranch, null);
    }

    public static If create(Node cond, Node trueBranch, Node falseBranch) {
        return new If(cond, trueBranch, falseBranch);
    }
}
