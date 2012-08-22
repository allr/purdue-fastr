package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;


public class If extends BaseRNode {
    RNode cond;
    RNode trueBranch;
    RNode falseBranch;

    public If(ASTNode ast, RNode cond, RNode trueBranch, RNode falseBranch) {
        super(ast);
        setCond(cond);
        setTrueBranch(trueBranch);
        setFalseBranch(falseBranch);
    }


    @Override
    public Object execute(RContext global, RFrame frame) {
        int ifVal;
        RNode node = getCond();

        try {
            ifVal = node.executeLogical(global, frame);
        } catch (UnexpectedResultException e) {
//            TruffleSnippetDefinitions.deoptimize();
            setCond(ConvertToLogicalOne.createNode(node, e.getResult()));
            return execute(global, frame); // Recall self ! to avoid try/catch
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            return getTrueBranch().execute(global, frame);
        } else if (ifVal == RLogical.FALSE) {
            return getFalseBranch().execute(global, frame);
        }
        throw RError.getUnexpectedNA(getAST());
    }


    public RNode getCond() {
        return cond;
    }


    public void setCond(RNode cond) {
        this.cond = updateParent(cond);
    }


    public RNode getTrueBranch() {
        return trueBranch;
    }


    public void setTrueBranch(RNode trueBranch) {
        this.trueBranch = updateParent(trueBranch);
    }


    public RNode getFalseBranch() {
        return falseBranch;
    }


    public void setFalseBranch(RNode falseBranch) {
        this.falseBranch = updateParent(falseBranch);
    }
}
