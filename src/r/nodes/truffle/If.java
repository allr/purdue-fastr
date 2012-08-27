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
    public Object execute(RContext global, RFrame frame) {
        int ifVal;
        RNode condNode = getCond();

        try {
            Utils.debug("executing condition in If");
            ifVal = condNode.executeLogicalOne(global, frame);
            Utils.debug("condition in if got expected result");
        } catch (UnexpectedResultException e) {
            // FIXME: this copies semantics from ConvertToLogical, perhaps could do better by replacing nodes in cast calls rather than in execute calls
            Utils.debug("Condition in if got unexpected result.");
            RAny result = (RAny)e.getResult();
            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(condNode, result);
            try {
                Utils.debug("Executing cast on new condition node in if.");
                ifVal = castNode.cast(result, global);
                Utils.debug("New condition node in if succeeded casting.");
            } catch (UnexpectedResultException ee) {
                castNode = ConvertToLogicalOne.createGenericNode(result);

                try {
                    Utils.debug("New condition node in if failed casting, doing a cast on the generic node.");
                    ifVal = castNode.cast(result, global);
                } catch (UnexpectedResultException eee) {
                    Utils.check(false, "Generic convertToLogical failed");
                    ifVal = -1;
                }
            }
            Utils.debug("replacing node in if");
            replaceChild(condNode, castNode);
            Utils.check( getCond() == castNode, "replaceChild failed" );
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            Object v = getTrueBranch().execute(global, frame);
            return v;
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
