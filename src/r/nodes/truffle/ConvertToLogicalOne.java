package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;

public abstract class ConvertToLogicalOne extends RNode {

    RNode input;

    private static final boolean DEBUG_C = false;

    private ConvertToLogicalOne() {
    }

    public void setInput(RNode expr) {
        input = updateParent(expr);
    }

    @Override
    public RAny execute(RContext context, RFrame frame) {
        return RLogicalFactory.getArray(executeLogicalOne(context, frame));
    }

    @Override
    public int executeLogicalOne(RContext context, RFrame frame) {
        return executeLogicalOne(context, frame, (RAny) input.execute(context, frame));
    }

    // The execute methods are use by intermediate cast nodes - those assuming an array of logicals or ints
    public int executeLogicalOne(RContext context, RFrame frame, RAny condValue) {
        try {
            if (DEBUG_C) Utils.debug("executing 2nd level cast");
            return cast(condValue, context);
        } catch (UnexpectedResultException e) {
            if (DEBUG_C) Utils.debug("2nd level cast failed, replacing by generic");
            ConvertToLogicalOne castNode = replace(createGenericNode(), "installGenericConvertToLogical from cast node");
            return castNode.executeLogicalOne(context, frame, condValue);
        }
    }

    public static ConvertToLogicalOne createNode(RNode expr, RAny value) {
        ConvertToLogicalOne node = value.callNodeFactory(factory);
        node.setInput(expr);
        return node;
    }

    public static ConvertToLogicalOne createGenericNode() {
        return factory.fromGeneric();
    }

    public abstract int cast(RAny value, RContext context) throws UnexpectedResultException;

    static OperationFactory<ConvertToLogicalOne> factory = new OperationFactory<ConvertToLogicalOne>() {

        @Override
        public ConvertToLogicalOne fromLogical() {
            return new ConvertToLogicalOne() {

                @Override
                public int cast(RAny value, RContext context) throws UnexpectedResultException {
                    if (DEBUG_C) Utils.debug("casting logical to one logical");
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
        public ConvertToLogicalOne fromInt() {
            return new ConvertToLogicalOne() {

                @Override
                public int cast(RAny value, RContext context) throws UnexpectedResultException {
                    if (DEBUG_C) Utils.debug("casting integer to one logical");
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

                    if (intValue == RLogical.FALSE || intValue == RLogical.NA) {
                        return intValue;
                    } else {
                        return RLogical.TRUE;
                    }
                }
            };
        }

        @Override
        public ConvertToLogicalOne fromGeneric() {
            return new ConvertToLogicalOne() {

                @Override
                public int cast(RAny value, RContext context) {
                    if (DEBUG_C) Utils.debug("casting generic to one logical");
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
