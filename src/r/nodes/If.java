package r.nodes;

import r.*;
import r.data.*;
import r.errors.*;

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
        RLogical op = getCond().execute(global, frame).asLogical(); // FIXME asLogical is too expensive, we've to go for
// a asLogicalOne
        int size = op.size();
        if (size != 1) {
            if (size == 0) {
                throw RError.getNulLength(this);
            }
            global.warning(this, RError.LENGTH_GT_1);
        }
        int ifVal = op.getLogical(0);
        if (ifVal == RLogical.TRUE) {
            return getTrueCase().execute(global, frame);
        } else if (ifVal == RLogical.FALSE) {
            ASTNode fcase = getFalseCase();
            if (fcase == null) {
                return RNull.getNull();
            } else {
                return fcase.execute(global, frame);
            }
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
        return create(cond, trueBranch, null);
    }

    public static If create(ASTNode cond, ASTNode trueBranch, ASTNode falseBranch) {
        return new If(cond, trueBranch, falseBranch);
    }
}
