package r.nodes.internal;

import r.*;
import r.data.*;
import r.errors.*;

import com.oracle.truffle.*;
import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

public abstract class ConvertToLogicalOne extends Node {

    Node input;

    public ConvertToLogicalOne(com.oracle.truffle.Node expr) {
        input = updateParent(expr);
    }

    private int performChecks(Context context, int len, int val) {
        if (len == 1) {
            return val;
        }
        if (len > 1) {
            ((RContext) context).warning(null, RError.LENGTH_GT_1); // TODO we do not have the ASTNode anymore
            return val;
        }
        throw RError.getNulLength(null);
    }

    @Override
    public Object execute(Context context, Frame frame) {
        try {
            return executeInt(context, frame);
        } catch (UnexpectedResultException e) {
            return replace(convertGeneric(input), null).execute(context, frame);
        }
    }

    public static ConvertToLogicalOne convertGeneric(Node expr) {
        return new ConvertToLogicalOne(expr) {

            @Override
            public int executeInt(Context context, Frame frame) {
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

    public static ConvertToLogicalOne convertInt(Node expr) {
        return new ConvertToLogicalOne(expr) {

            @Override
            public int executeInt(Context context, Frame frame) throws UnexpectedResultException {
                RAny val = (RAny) input.execute(context, frame);
                if (!(val instanceof RInt)) {
                    throw new UnexpectedResultException(input);
                }
                RInt intArray = ((RInt) val);
                if (intArray.size() == 1) {
                    return intArray.getInt(0);
                }
                if (intArray.size() > 1) {
                    ((RContext) context).warning(null, RError.LENGTH_GT_1); // TODO we do not have the ASTNode anymore
                    return intArray.getInt(0);
                }
                throw RError.getNulLength(null);
            }
        };
    }
}
