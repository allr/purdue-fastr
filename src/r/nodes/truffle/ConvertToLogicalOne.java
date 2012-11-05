package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.errors.*;

import com.oracle.truffle.nodes.*;

public abstract class ConvertToLogicalOne extends RNode {

    @Stable RNode input;

    private static final boolean DEBUG_C = false;

    private ConvertToLogicalOne(RNode input) {
        this.input = updateParent(input);
    }

    @Override
    public final Object execute(RContext context, Frame frame) {
        return RLogicalFactory.getScalar(executeScalarLogical(context, frame));
    }

    @Override
    public final int executeScalarLogical(RContext context, Frame frame) {
        return executeScalarLogical(context, (RAny) input.execute(context, frame));
    }

    // The execute methods are use by intermediate cast nodes - those assuming an array of logicals or ints
    public int executeScalarLogical(RContext context, RAny condValue) {
        try {
            if (DEBUG_C) Utils.debug("executing 2nd level cast");
            return cast(condValue, context);
        } catch (UnexpectedResultException e) {
            if (DEBUG_C) Utils.debug("2nd level cast failed, replacing by generic");
            ConvertToLogicalOne castNode = replace(fromGeneric(input), "installGenericConvertToLogical from cast node");
            return castNode.executeScalarLogical(context, condValue);
        }
    }

    public static ConvertToLogicalOne createNode(RNode input, RAny value) {

        if (value instanceof RLogical) {
            return fromLogical(input);
        } else if (value instanceof RInt) {
            return fromInt(input);
        } else {
            return fromGeneric(input);
        }
    }

    public abstract int cast(RAny value, RContext context) throws UnexpectedResultException;

    public static ConvertToLogicalOne fromLogical(RNode input) {
        return new ConvertToLogicalOne(input) {

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

    public static ConvertToLogicalOne fromInt(RNode input) {
        return new ConvertToLogicalOne(input) {

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

    public static ConvertToLogicalOne fromGeneric(RNode input) {
        return new ConvertToLogicalOne(input) {

            @Override
            public int cast(RAny value, RContext context) {
                if (DEBUG_C) Utils.debug("casting generic to one logical");
                RLogical logicalArray = value.asLogical();
                if (logicalArray.size() == 1) {
                    return logicalArray.getLogical(0);
                }
                if (logicalArray.size() > 1) {
                    context.warning(getAST(), RError.LENGTH_GT_1);
                    return logicalArray.getLogical(0);
                }
                throw RError.getNulLength(null);
            }

            @Override
            public int executeScalarLogical(RContext context, RAny condValue) {
                return cast(condValue, context);
            }
        };
    }
}
