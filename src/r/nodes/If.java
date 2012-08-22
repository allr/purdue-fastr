package r.nodes;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.internal.*;

import com.oracle.truffle.*;
import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

public class If extends ASTNode {

    ASTNode cond;
    ASTNode trueCase;
    ASTNode falseCase;

    If(ASTNode cond, ASTNode truecase, ASTNode falsecase) {
        setCond(cond);
        setTrueCase(truecase);
        setFalseCase(falsecase);
    }

    @Override
    public RAny execute(RContext global, Frame frame) {
        int ifVal;
        Node someNode = null; // FIXME this convert has no chances to work, it's just to test
        // getCond()
        try {
            ifVal = someNode.executeInt(global, frame);
        } catch (UnexpectedResultException e) {
// someNode => getCond()
            someNode.replace(ConvertToLogicalOne.createNode(someNode, e.getResult()), "Inserted boolean conversion");
            return execute(global, frame); // Recall self ! to avoid try/catch
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            return getTrueCase().execute(global, frame);
        } else if (ifVal == RLogical.FALSE) {
            ASTNode fcase = getFalseCase();
            return fcase.execute(global, frame);
        }
        throw RError.getUnexpectedNA(this);
    }

    public ASTNode getCond() {
        return cond;
    }

    public ASTNode getTrueCase() {
        return trueCase;
    }

    public ASTNode getFalseCase() {
        return falseCase;
    }

    public void setCond(ASTNode cond) {
        this.cond = updateParent(cond);
    }

    public void setTrueCase(ASTNode trueCase) {
        this.trueCase = updateParent(trueCase);
    }

    public void setFalseCase(ASTNode falseCase) {
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

    public static If create(ASTNode cond, ASTNode trueBranch) {
        return create(cond, trueBranch, Constant.getNull());
    }

    public static If create(ASTNode cond, ASTNode trueBranch, ASTNode falseBranch) {
        return new If(cond, trueBranch, falseBranch);
    }
}
