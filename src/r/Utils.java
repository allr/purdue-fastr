package r;

import r.data.*;
import r.errors.*;

public final class Utils {

    public static void nyi() {
        throw RError.getNYI(null);
    }

    public static void nyi(String reason) {
        throw RError.getNYI(reason);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * From a type point of view, this works ... however all call to this function will perform the cast AFTER which
     * sucks
     */
    public static <T> T cast(Object obj, Class<T> clazz) {
        return clazz.cast(obj);
    }

    public static String getProperty(String key, String dfltValue) {
        return System.getProperty(key, dfltValue);
    }

    public static boolean getProperty(String key, boolean dfltValue) {
        return Boolean.parseBoolean(getProperty(key, dfltValue ? "true" : "false"));
    }

    public static boolean check(final boolean assertion) {
        assert assertion;
        return true;
    }

    public static boolean check(final boolean assertion, final String reason) {
        assert assertion : reason;
        return true;
    }

    public static final boolean DEBUG = true;

    public static void debug(String msg) {
        if (DEBUG) {
            System.err.println(msg);
        }
    }

    public static RArray createArray(RAny type, int size) {  // FIXME: !!! should find better design to get rid of these
        if (type instanceof RInt) {
            return RInt.RIntFactory.getUninitializedArray(size);
        }
        if (type instanceof RDouble) {
            return RDouble.RDoubleFactory.getUninitializedArray(size);
        }
        if (type instanceof RLogical) {
            return RLogical.RLogicalFactory.getUninitializedArray(size);
        }
        if (type instanceof RList) {
            return RList.RListFactory.getUninitializedArray(size);
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static RArray createEmptyArray(RAny type) {
        if (type instanceof RInt) {
            return RInt.EMPTY;
        }
        if (type instanceof RDouble) {
            return RDouble.EMPTY;
        }
        if (type instanceof RLogical) {
            return RLogical.EMPTY;
        }
        if (type instanceof RList) {
            return RList.EMPTY;
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static void setNA(RArray arr, int index) { // FIXME: !!! should find better design to get rid of these
        if (arr instanceof RInt) {
            arr.set(index, RInt.NA);
        } else if (arr instanceof RDouble) {
            arr.set(index, RDouble.NA);
        } else if (arr instanceof RLogical) {
            arr.set(index, RLogical.NA);
        } else if (arr instanceof RList) {
            arr.set(index,  RList.NULL);
        } else {
            Utils.nyi("unsupported array type");
        }
    }

    public static RAny copy(RAny a) {
        if (a instanceof RNull) {
            return a;
        }
        if (a instanceof RArray) {
            return copyArray((RArray) a);
        }
        Utils.nyi("unsupported type");
        return null;
    }

    public static RArray copyArray(RArray arr) {
        if (arr instanceof RDouble) {
            return RDouble.RDoubleFactory.copy((RDouble) arr);
        }
        if (arr instanceof RInt) {
            return RInt.RIntFactory.copy((RInt) arr);
        }
        if (arr instanceof RLogical) {
            return RLogical.RLogicalFactory.copy((RLogical) arr);
        }
        if (arr instanceof RList) {
            return RList.RListFactory.copy((RList) arr);
        }
        Utils.nyi("unuspported array type");
        return null;
    }
}
