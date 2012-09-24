package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.nodes.control.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;


public abstract class Loop extends BaseR {

    RNode body;

    private static final boolean DEBUG_LO = true;

    Loop(ASTNode ast, RNode body) {
        super(ast);
        this.body = updateParent(body);
    }

    public static class Break extends BaseR {
        public Break(ASTNode ast) {
            super(ast);
        }

        @Override
        public RAny execute(RContext context, RFrame frame) {
            throw new BreakException();
        }
    }

    public static class Next extends BaseR {
        public Next(ASTNode ast) {
            super(ast);
        }

        @Override
        public RAny execute(RContext context, RFrame frame) {
            throw new ContinueException();
        }
    }

    public static class Repeat extends Loop {
        public Repeat(ASTNode ast, RNode body) {
            super(ast, body);
        }

        @Override
        public RAny execute(RContext context, RFrame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering repeat loop");
                for (;;) {
                    try {
                        body.execute(context, frame);
                    } catch (ContinueException ce) {
                        if (DEBUG_LO) Utils.debug("loop - repeat loop received continue exception");
                    }

                }
            } catch (BreakException be) {
                if (DEBUG_LO) Utils.debug("loop - repeat loop received break exception");
            }
            return RNull.getNull();
        }
    }

    public static class While extends Loop {

        RNode cond;
        public While(ASTNode ast, RNode cond, RNode body) {
            super(ast, body);
            this.cond = cond;
        }

        @Override
        public RAny execute(RContext context, RFrame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering while loop");
                for (;;) {
                    try {
                        int condVal;
                        try {
                            condVal = cond.executeLogicalOne(context, frame);
                        } catch (UnexpectedResultException e) {
                            RAny result = (RAny) e.getResult();
                            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
                            replaceChild(cond, castNode);
                            condVal = castNode.executeLogicalOne(context, frame, result);
                        }
                        if (condVal == RLogical.FALSE) {
                            break;
                        }
                        if (condVal == RLogical.NA) {
                            throw RError.getUnexpectedNA(ast);
                        }
                        body.execute(context, frame);
                    } catch (ContinueException ce) {
                        if (DEBUG_LO) Utils.debug("loop - while loop received continue exception");
                    }

                }
            } catch (BreakException be) {
                if (DEBUG_LO) Utils.debug("loop - while loop received break exception");
            }
            return RNull.getNull();
        }
    }


}
