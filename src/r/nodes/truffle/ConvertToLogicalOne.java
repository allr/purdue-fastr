package r.nodes.truffle;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;

public abstract class ConvertToLogicalOne extends RNode {

    RNode input;

    private ConvertToLogicalOne() {
    }

    public void setInput(RNode expr) {
        input = updateParent(expr);
    }

    @Override
    public RAny execute(RContext context, RFrame frame) {
        Utils.check(false, "ConvertToLogicalOne.execute should not be called");
        return null;
    }

    // This is used for intermediate cast nodes - those assuming an array of logicals or ints
    @Override
    public int executeLogicalOne(RContext context, RFrame frame) {
        return executeLogicalOne(context, frame, (RAny) input.execute(context, frame));
    }

    public int executeLogicalOne(RContext context, RFrame frame, RAny condValue) {
        try {
            Utils.debug("executing 2nd level cast");
            return cast(condValue, context);
        } catch (UnexpectedResultException e) {
            Utils.debug("2nd level cast failed, replacing by generic");
            ConvertToLogicalOne castNode = replace(createGenericNode(condValue), "installGenericConvertToLogical from cast node");
            return castNode.executeLogicalOne(context, frame, condValue);
        }
    }

    public static ConvertToLogicalOne createNode(RNode expr, RAny value) {
        ConvertToLogicalOne node = value.callNodeFactory(factory);
        node.setInput(expr);
        return node;
    }

    public static ConvertToLogicalOne createGenericNode(RAny value) {
        return factory.fromGeneric(value);
    }

    public abstract int cast(RAny value, RContext context) throws UnexpectedResultException;

    static OperationFactory<ConvertToLogicalOne> factory = new OperationFactory<ConvertToLogicalOne>() {

        @Override
        public ConvertToLogicalOne fromLogical(RLogical obj) {
            return new ConvertToLogicalOne() {

                @Override
                public int cast(RAny value, RContext context) throws UnexpectedResultException {
                    Utils.debug("casting logical to one logical");
                    if (!(value instanceof RLogical)) {
                        throw new UnexpectedResultException(input);
                    }
                    RLogical logicalArray = ((RLogical) value);
                    if (logicalArray.size() == 1) {
                        return logicalArray.getLogical(0);
                    }
                    if (logicalArray.size() > 1) {
                        context.warning(getAST(), RError.LENGTH_GT_1);
                        return logicalArray.getLogical(0);
                    }
                    throw RError.getNulLength(null);
                }
            };
        }

        @Override
        public ConvertToLogicalOne fromInt(RInt obj) {
            return new ConvertToLogicalOne() {

                @Override
                public int cast(RAny value, RContext context) throws UnexpectedResultException {
                    Utils.debug("casting integer to one logical");
                    if (!(value instanceof RInt)) {
                        throw new UnexpectedResultException(input);
                    }
                    RInt intArray = ((RInt) value);
                    int intValue;
                    if (intArray.size() == 1) {
                        intValue = intArray.getInt(0);
                    } else if (intArray.size() > 1) {
                        context.warning(getAST(), RError.LENGTH_GT_1);
                        intValue = intArray.getInt(0);
                    } else {
                        throw RError.getNulLength(null);
                    }

                    if (intValue == RLogical.TRUE || intValue == RLogical.NA) {
                        return intValue;
                    } else {
                        return RLogical.FALSE;
                    }
                }
            };
        }

        @Override
        public ConvertToLogicalOne fromGeneric(RAny obj) {
            return new ConvertToLogicalOne() {

                @Override
                public int cast(RAny value, RContext context) {
                    Utils.debug("casting generic to one logical");
                    RLogical logicalArray = value.asLogical();
                    if (logicalArray.size() == 1) { // FIXME try to call perform check
                        return logicalArray.getLogical(0);
                    }
                    if (logicalArray.size() > 1) {
                        context.warning(getAST(), RError.LENGTH_GT_1);
                        return logicalArray.getLogical(0);
                    }
                    throw RError.getNulLength(null);
                }

                @Override
                public int executeLogicalOne(RContext context, RFrame frame, RAny condValue) {
                    return cast(condValue, context);
                }
            };
        }
    };
}
