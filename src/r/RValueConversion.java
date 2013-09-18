package r;

import r.data.*;
import r.data.internal.*;
import r.runtime.*;

public class RValueConversion {

    public static int expectScalarLogical(RAny value) throws SpecializationException {
        if (value instanceof ScalarLogicalImpl) {
            return ((ScalarLogicalImpl) value).getLogical();
        }
        throw new SpecializationException(value);
    }

    public static int expectScalarNonNALogical(RAny value) throws SpecializationException {
        if (value instanceof ScalarLogicalImpl) {
            int res = ((ScalarLogicalImpl) value).getLogical();
            if (res != RLogical.NA) {
                return res;
            }
        }
        throw new SpecializationException(value);
    }

    public static int expectScalarInteger(RAny value) throws SpecializationException {
        if (value instanceof ScalarIntImpl) {
            return ((ScalarIntImpl) value).getInt();
        }
        throw new SpecializationException(value);
    }

    public static RArray expectScalar(RAny value) throws SpecializationException {
        if (value instanceof RArray) {
            RArray array = (RArray) value;
            if (array.size() == 1) {
                return array;
            }
        }
        throw new SpecializationException(value);
    }
}
