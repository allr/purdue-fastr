package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;


public class If extends BaseR {
    RNode cond;
    RNode trueBranch;
    RNode falseBranch;

    private static final boolean DEBUG_IF = false;

    public If(ASTNode ast, RNode cond, RNode trueBranch, RNode falseBranch) {
        super(ast);
        setCond(cond);
        setTrueBranch(trueBranch);
        setFalseBranch(falseBranch);
    }

    // The condition is treated as follows:
    //   - no special node for a 1-value logical argument
    //   - a special intermediate conversion node for multi-value logical argument, another for multi-value integer argument
    //   - a generic conversion node that can convert anything
    @Override
    public Object execute(RContext context, RFrame frame) {
        int ifVal;
        RNode condNode = getCond();

        try {
            if (DEBUG_IF) Utils.debug("executing condition");
            ifVal = condNode.executeLogicalOne(context, frame);
            if (DEBUG_IF) Utils.debug("condition got expected result");
        } catch (UnexpectedResultException e) {
            if (DEBUG_IF) Utils.debug("condition got unexpected result, inserting 2nd level cast node");
            RAny result = (RAny) e.getResult();
            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(condNode, result);
            replaceChild(condNode, castNode);
            Utils.check(getCond() == castNode, "replaceChild failed");
            ifVal = castNode.executeLogicalOne(context, frame, result);
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            Object v = getTrueBranch().execute(context, frame);
            return v;
        } else if (ifVal == RLogical.FALSE) {
            return getFalseBranch().execute(context, frame);
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
