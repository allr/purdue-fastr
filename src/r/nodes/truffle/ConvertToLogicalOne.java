package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.errors.*;

public abstract class ConvertToLogicalOne extends RNode {

    @Child RNode input;

    private static final boolean DEBUG_C = false;

    private ConvertToLogicalOne(RNode input) {
        this.input = adoptChild(input);
    }

    @Override
    public final Object execute(Frame frame) {
        return RLogicalFactory.getScalar(executeScalarLogical(frame));
    }

    @Override
    public final int executeScalarLogical(Frame frame) {
        return executeScalarLogical((RAny) input.execute(frame));
    }

    // The execute methods are use by intermediate cast nodes - those assuming an array of logicals or ints
    public int executeScalarLogical(RAny condValue) {
        try {
            if (DEBUG_C) Utils.debug("executing 2nd level cast");
            return cast(condValue);
        } catch (UnexpectedResultException e) {
            if (DEBUG_C) Utils.debug("2nd level cast failed, replacing by generic");
            ConvertToLogicalOne castNode = replace(fromGeneric(input), "installGenericConvertToLogical from cast node");
            return castNode.executeScalarLogical(condValue);
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

    public abstract int cast(RAny value) throws UnexpectedResultException;

    public static ConvertToLogicalOne fromLogical(RNode input) {
        return new ConvertToLogicalOne(input) {

            @Override
            public int cast(RAny value) throws UnexpectedResultException {
                if (DEBUG_C) Utils.debug("casting logical to one logical");
                if (!(value instanceof RLogical)) {
                    throw new UnexpectedResultException(input);
                }
                RLogical logicalArray = ((RLogical) value);
                if (logicalArray.size() == 1) {
                    return logicalArray.getLogical(0);
                }
                if (logicalArray.size() > 1) {
                    RContext.warning(getAST(), RError.LENGTH_GT_1);
                    return logicalArray.getLogical(0);
                }
                throw RError.getLengthZero(null);
            }
        };
    }

    public static ConvertToLogicalOne fromInt(RNode input) {
        return new ConvertToLogicalOne(input) {

            @Override
            public int cast(RAny value) throws UnexpectedResultException {
                if (DEBUG_C) Utils.debug("casting integer to one logical");
                if (!(value instanceof RInt)) {
                    throw new UnexpectedResultException(input);
                }
                RInt intArray = ((RInt) value);
                int intValue;
                if (intArray.size() == 1) {
                    intValue = intArray.getInt(0);
                } else if (intArray.size() > 1) {
                    RContext.warning(getAST(), RError.LENGTH_GT_1);
                    intValue = intArray.getInt(0);
                } else {
                    throw RError.getLengthZero(null);
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
            public int cast(RAny value) {
                if (DEBUG_C) Utils.debug("casting generic to one logical");
                RLogical logicalArray = value.asLogical();
                int asize = logicalArray.size();
                int logicalValue;
                if (asize == 1) {
                    logicalValue = logicalArray.getLogical(0);
                } else if (asize > 1) {
                    logicalValue = logicalArray.getLogical(0);
                    RContext.warning(getAST(), RError.LENGTH_GT_1);
                } else {
                    assert Utils.check(asize == 0);
                    throw RError.getLengthZero(input.getAST());
                }
                if (logicalValue == RLogical.NA && !(value instanceof RLogical)) {
                    throw RError.getArgumentNotInterpretableLogical(input.getAST());
                }
                return logicalValue;
            }

            @Override
            public int executeScalarLogical(RAny condValue) {
                return cast(condValue);
            }
        };
    }
}
