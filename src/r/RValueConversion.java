package r;

import r.data.*;
import r.data.internal.*;

import com.oracle.truffle.nodes.*;

public class RValueConversion {

    public static int expectLogicalOne(RAny value) throws UnexpectedResultException {
        if (value instanceof ScalarLogicalImpl) {
            return ((ScalarLogicalImpl) value).getLogical();
        }
        if (value instanceof RLogical) { // FIXME: guaranteed normalization to logical scalar would help here
            RLogical logical = (RLogical) value;
            if (logical.size() == 1) {
                return logical.getLogical(0);
            }
        }
        throw new UnexpectedResultException(value);
    }
    public static RArray expectArrayOne(RAny value) throws UnexpectedResultException {
        if (value instanceof RArray) {
            RArray array = (RArray) value;
            if (array.size() == 1) {
                return array;
            }
        }
        throw new UnexpectedResultException(value);
    }
}
