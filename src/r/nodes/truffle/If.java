package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;


public class If extends BaseR {
    @Stable RNode cond;
    @Stable RNode trueBranch;
    @Stable RNode falseBranch;

    private static final boolean DEBUG_IF = false;

    public If(ASTNode ast, RNode cond, RNode trueBranch, RNode falseBranch) {
        super(ast);
        this.cond = updateParent(cond);
        this.trueBranch = updateParent(trueBranch);
        this.falseBranch = updateParent(falseBranch);
    }

    // The condition is treated as follows:
    //   - no special node for a 1-value logical argument
    //   - a special intermediate conversion node for multi-value logical argument, another for multi-value integer argument
    //   - a generic conversion node that can convert anything
    @Override
    public Object execute(RContext context, Frame frame) {
        int ifVal;

        try {
            if (DEBUG_IF) Utils.debug("executing condition");
            ifVal = cond.executeLogicalOne(context, frame);
            if (DEBUG_IF) Utils.debug("condition got expected result");
        } catch (UnexpectedResultException e) {
            if (DEBUG_IF) Utils.debug("condition got unexpected result, inserting 2nd level cast node");
            RAny result = (RAny) e.getResult();
            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
            replaceChild(cond, castNode);
            ifVal = castNode.executeLogicalOne(context, result);
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            Object v = trueBranch.execute(context, frame);
            return v;
        } else if (ifVal == RLogical.FALSE) {
            return falseBranch.execute(context, frame);
        }
        throw RError.getUnexpectedNA(getAST());
    }
}
