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
        if (type instanceof RString) {
            return RString.RStringFactory.getUninitializedArray(size);
        }
        if (type instanceof RRaw) {
            return RRaw.RRawFactory.getUninitializedArray(size);
        }
        if (type instanceof RComplex) {
            return RComplex.RComplexFactory.getUninitializedArray(size);
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static RArray createNAArray(RAny type, int size) {
        if (type instanceof RInt) {
            return RInt.RIntFactory.getNAArray(size);
        }
        if (type instanceof RDouble) {
            return RDouble.RDoubleFactory.getNAArray(size);
        }
        if (type instanceof RLogical) {
            return RLogical.RLogicalFactory.getNAArray(size);
        }
        if (type instanceof RList) {
            return RList.RListFactory.getNullArray(size);
        }
        if (type instanceof RString) {
            return RString.RStringFactory.getNAArray(size);
        }
        if (type instanceof RRaw) {
            return RRaw.RRawFactory.getZeroArray(size);
        }
        if (type instanceof RComplex) {
            return RComplex.RComplexFactory.getNAArray(size);
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static RArray createArray(RAny type, int size, int[] dimensions) {
        if (type instanceof RInt) {
            return RInt.RIntFactory.getUninitializedArray(size, dimensions);
        }
        if (type instanceof RDouble) {
            return RDouble.RDoubleFactory.getUninitializedArray(size, dimensions);
        }
        if (type instanceof RLogical) {
            return RLogical.RLogicalFactory.getUninitializedArray(size, dimensions);
        }
        if (type instanceof RList) {
            return RList.RListFactory.getUninitializedArray(size, dimensions);
        }
        if (type instanceof RString) {
            return RString.RStringFactory.getUninitializedArray(size, dimensions);
        }
        if (type instanceof RRaw) {
            return RRaw.RRawFactory.getUninitializedArray(size, dimensions);
        }
        if (type instanceof RComplex) {
            return RComplex.RComplexFactory.getUninitializedArray(size, dimensions);
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static RArray createArray(RAny type, int size, boolean named) {
        if (!named) {
            return createArray(type, size);
        }
        if (type instanceof RInt) {
            return RInt.RIntFactory.getUninitializedNonScalarArray(size);
        }
        if (type instanceof RDouble) {
            return RDouble.RDoubleFactory.getUninitializedNonScalarArray(size);
        }
        if (type instanceof RLogical) {
            return RLogical.RLogicalFactory.getUninitializedNonScalarArray(size);
        }
        if (type instanceof RList) {
            return RList.RListFactory.getUninitializedNonScalarArray(size);
        }
        if (type instanceof RString) {
            return RString.RStringFactory.getUninitializedNonScalarArray(size);
        }
        if (type instanceof RRaw) {
            return RRaw.RRawFactory.getUninitializedNonScalarArray(size);
        }
        if (type instanceof RComplex) {
            return RComplex.RComplexFactory.getUninitializedNonScalarArray(size);
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
        if (type instanceof RString) {
            return RString.EMPTY;
        }
        if (type instanceof RRaw) {
            return RRaw.EMPTY;
        }
        if (type instanceof RComplex) {
            return RComplex.EMPTY;
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static RArray createNamedEmptyArray(RAny type) {
        if (type instanceof RInt) {
            return RInt.EMPTY_NAMED_NA;
        }
        if (type instanceof RDouble) {
            return RDouble.EMPTY_NAMED_NA;
        }
        if (type instanceof RLogical) {
            return RLogical.EMPTY_NAMED_NA;
        }
        if (type instanceof RList) {
            return RList.EMPTY_NAMED_NA;
        }
        if (type instanceof RString) {
            return RString.EMPTY_NAMED_NA;
        }
        if (type instanceof RRaw) {
            return RRaw.EMPTY_NAMED_NA;
        }
        if (type instanceof RComplex) {
            return RComplex.EMPTY_NAMED_NA;
        }
        Utils.nyi("unsupported array type");
        return null;
    }

    public static RArray createEmptyArray(RAny type, boolean named) {
        if (!named) {
            return createEmptyArray(type);
        } else {
            return createNamedEmptyArray(type);
        }
    }

    public static RArray getBoxedNA(RArray arr) {
        if (arr instanceof RInt) {
            return RInt.BOXED_NA;
        } else if (arr instanceof RDouble) {
            return RDouble.BOXED_NA;
        } else if (arr instanceof RLogical) {
            return RLogical.BOXED_NA;
        } else if (arr instanceof RList) {
            return RList.NULL;
        } else if (arr instanceof RString) {
            return RString.BOXED_NA;
        } else if (arr instanceof RComplex) {
            return RComplex.BOXED_NA;
        } else if (arr instanceof RRaw) {
            return RRaw.BOXED_ZERO;
        } else {
            Utils.nyi("unsupported array type");
            return null;
        }
    }

    public static RArray getNamedNA(RArray arr) {
        if (arr instanceof RInt) {
            return RInt.NA_NAMED_NA;
        } else if (arr instanceof RDouble) {
            return RDouble.NA_NAMED_NA;
        } else if (arr instanceof RLogical) {
            return RLogical.NA_NAMED_NA;
        } else if (arr instanceof RList) {
            return RList.NULL_NAMED_NA;
        } else if (arr instanceof RString) {
            return RString.NA_NAMED_NA;
        } else if (arr instanceof RComplex) {
            return RComplex.NA_NAMED_NA;
        } else if (arr instanceof RRaw) {
            return RRaw.ZERO_NAMED_NA;
        } else {
            Utils.nyi("unsupported array type");
            return null;
        }
    }

    public static void setNA(RArray arr, int index) { // FIXME: !!! should find better design to get rid of these
        if (arr instanceof RInt) {
            arr.set(index, RInt.NA);
        } else if (arr instanceof RDouble) {
            arr.set(index, RDouble.NA);
        } else if (arr instanceof RLogical) {
            arr.set(index, RLogical.NA);
        } else if (arr instanceof RList) {
            arr.set(index, RList.NULL);
        } else if (arr instanceof RString) {
            arr.set(index, RString.NA);
        } else if (arr instanceof RComplex) {
            arr.set(index, RComplex.COMPLEX_BOXED_NA);
        } else if (arr instanceof RRaw) {
            arr.set(index,  RRaw.ZERO);
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

    public static RArray copyArray(RArray arr) {  // FIXME: should be a method of RArray for (hopefully) faster dispatch
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
        if (arr instanceof RString) {
            return RString.RStringFactory.copy((RString) arr);
        }
        if (arr instanceof RRaw) {
            return RRaw.RRawFactory.copy((RRaw) arr);
        }
        if (arr instanceof RComplex) {
            return RComplex.RComplexFactory.copy((RComplex) arr);
        }
        Utils.nyi("unuspported array type");
        return null;
    }

    public static void ref(RAny[] values) {
        for (RAny v : values) {
            v.ref();
        }
    }

    public static void refIfRAny(Object o) {
        if (o instanceof RAny) {
            ((RAny) o).ref();
        }
    }

    public static void strAppend(StringBuilder b, String s, int width) {
        int spaces = width - s.length();
        Utils.check(spaces >= 0);
        for (int i = 0; i < spaces; i++) {
            b.append(' ');
        }
        b.append(s);
    }
}
