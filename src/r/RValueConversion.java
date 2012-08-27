package r;

import r.data.*;

import com.oracle.truffle.nodes.*;

public class RValueConversion {

    public static int expectLogicalOne(RAny value) throws UnexpectedResultException {
        if (value instanceof RLogical) {
            RLogical logical = (RLogical) value;
            if (logical.size() == 1) {
                return logical.getLogical(0);
            }
        }
        throw new UnexpectedResultException(value);
    }
}
