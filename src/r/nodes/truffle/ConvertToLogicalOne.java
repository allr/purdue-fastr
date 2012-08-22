package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.RLogical.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

public abstract class ConvertToLogicalOne extends RNode {

    RNode input;

    private ConvertToLogicalOne() {
    }

    public void setInput(RNode expr) {
        input = updateParent(expr);
    }

    @SuppressWarnings("unused")
    private int performChecks(Context context, int len, int val) {
        if (len == 1) {
            return val;
        }
        if (len > 1) {
            ((RContext) context).warning(getAST(), RError.LENGTH_GT_1); // TODO we do not have the ASTNode anymore
            return val;
        }
        throw RError.getNulLength(null);
    }

    @Override
    public RAny execute(RContext context, RFrame frame) {
        try {
            return RLogicalFactory.getArray(executeLogical(context, frame));
        } catch (UnexpectedResultException e) {
            return replace(factory.fromGeneric((RAny) input.execute(context, frame)), null).execute(context, frame);
        }
    }

    @Override
    public int executeLogical(RContext context, RFrame frame) throws UnexpectedResultException {
        return input.executeLogical(context, frame);
    }

    public static RNode createNode(RNode expr, Object obj) {
        ConvertToLogicalOne node = ((RAny) obj).callNodeFactory(factory);
        node.setInput(expr);
        return node;
    }

    static OperationFactory<ConvertToLogicalOne> factory = new OperationFactory<ConvertToLogicalOne>() {

        @Override
        public ConvertToLogicalOne fromLogical(RLogical obj) {
            return new ConvertToLogicalOne() {

                @Override
                public int executeLogical(RContext context, RFrame frame) throws UnexpectedResultException {
                    Object val = input.execute(context, frame);
                    if (!(val instanceof RLogical)) {
                        throw new UnexpectedResultException(input);
                    }
                    RLogical intArray = ((RLogical) val);
                    if (intArray.size() == 1) {
                        return intArray.getLogical(0);
                    }
                    if (intArray.size() > 1) {
                        context.warning(getAST(), RError.LENGTH_GT_1);
                        return intArray.getLogical(0);
                    }
                    throw RError.getNulLength(null);
                }
            };
        }

        @Override
        public ConvertToLogicalOne fromInt(RInt obj) {
            return new ConvertToLogicalOne() {

                @Override
                public int executeLogical(RContext context, RFrame frame) throws UnexpectedResultException {
                    Object val = input.execute(context, frame);
                    if (!(val instanceof RInt)) {
                        throw new UnexpectedResultException(input);
                    }
                    RInt intArray = ((RInt) val);
                    if (intArray.size() == 1) {
                        return intArray.getInt(0);
                    }
                    if (intArray.size() > 1) {
                        context.warning(getAST(), RError.LENGTH_GT_1);
                        return intArray.getInt(0);
                    }
                    throw RError.getNulLength(null);
                }
            };
        }

        @Override
        public ConvertToLogicalOne fromGeneric(RAny obj) {
            return new ConvertToLogicalOne() {

                @Override
                public int executeLogical(RContext context, RFrame frame) {
                    RAny val = (RAny) input.execute(context, frame);
                    RLogical logVal = val.asLogical();
                    if (logVal.size() == 1) { // FIXME try to call perform check
                        return logVal.getLogical(0);
                    }
                    if (logVal.size() > 1) {
                        return logVal.getLogical(0);
                    }
                    throw RError.getNulLength(null);
                }
            };
        }

    };
}
