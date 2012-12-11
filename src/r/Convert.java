package r;

import r.data.*;
import r.errors.*;
import r.nodes.*;

public class Convert {

    // NOTE: conversion functions do not clear naIntroduced, they only set it if non-null and NA has been introduced
    // indeed this could also be a static field, but hopefully this is faster when no NAs are introduced

    public static class NAIntroduced {
        public boolean naIntroduced;
    }

    public static class OutOfRange { // not set when NAIntroduced (in that case out of range warning should always be produced)
        public boolean outOfRange;
    }

    public static double string2double(String v) {
        return string2double(v, null);
    }

    public static double string2double(String v, NAIntroduced naIntroduced) {
        if (v != RString.NA) {
            // FIXME use R rules
            try {
                return Double.parseDouble(v);
            } catch (NumberFormatException e) {
                if (v.startsWith("0x")) {
                    try {
                        return int2double(Integer.decode(v));
                    } catch (NumberFormatException ein) {
                    }
                }
                if (naIntroduced != null) {
                    naIntroduced.naIntroduced = true;
                }
            }
        }
        return RDouble.NA;
    }

    @SuppressWarnings("cast")
    public static String double2string(double d) {
        if (!RDouble.RDoubleUtils.isNA(d)) {
            // FIXME use R rules
            if (!RContext.debuggingFormat()) {
                if (RDouble.RDoubleUtils.fitsRInt(d) && (((double) ((int) d)) ==  d)) {
                    return int2string((int) d); // a hack to get rid of ".0" in "1.0"
                }
            }
            return Double.toString(d);
        }
        return RString.NA;
    }

    public static int string2int(String s) {
        return string2int(s, null);
    }

    public static int string2int(String s, NAIntroduced naIntroduced) {
        if (s != RString.NA) {
            // FIXME use R rules
            try {
                return Integer.decode(s);  // decode supports hex constants
            } catch (NumberFormatException e) {
                if (naIntroduced != null) {
                    naIntroduced.naIntroduced = true;
                }
            }
        }
        return RInt.NA;
    }

    public static String int2string(int i) {
        if (i == RInt.NA) {
            return RString.NA;
        }
        // FIXME use R rules
        if (!RContext.debuggingFormat()) {
            return Integer.toString(i);
        } else {
            return Integer.toString(i) + "L";
        }
    }

    public static int string2logical(String s) {
        return string2logical(s, null);
    }

    public static int string2logical(String s, NAIntroduced naIntroduced) {
        if (s != RString.NA) {
            if (s.equals("TRUE") || s.equals("T")) {
                return RLogical.TRUE;
            }
            if (s.equals("FALSE") || s.equals("F")) {
                return RLogical.FALSE;
            }
            if (s.equals("True") || s.equals("true")) {
                return RLogical.TRUE;
            }
            if (s.equals("False") || s.equals("false")) {
                return RLogical.FALSE;
            }
            if (naIntroduced != null) {
                naIntroduced.naIntroduced = true;
            }
        }
        return RLogical.NA;
    }

    public static String logical2string(int i) {
        switch (i) {
            case RLogical.FALSE:
                return "FALSE";
            case RLogical.NA:
                return RString.NA;
            default:
                return "TRUE";
        }
    }

    public static byte string2raw(String s) {
        return string2raw(s, null, null);
    }

    public static byte string2raw(String s, NAIntroduced naIntroduced, OutOfRange outOfRange) {
        if (s != RString.NA) {
            // FIXME use R rules
            int intVal;
            try {
                 intVal = Integer.decode(s);  // decode supports hex constants
            } catch (NumberFormatException e) {
                if (naIntroduced != null) {
                    naIntroduced.naIntroduced = true;
                }
                return 0;
            }
            if (intVal >= 0 && intVal <= 255) {
                return (byte) intVal;
            }
        }
        if (outOfRange != null) {
            outOfRange.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static String raw2string(byte v) {
        return rawStrings[byteToUnsigned(v)];
    }

    public static int double2int(double d) {
        return double2int(d, null);
    }

    public static int double2int(double d, NAIntroduced naIntroduced) {
        if (!RDouble.RDoubleUtils.isNAorNaN(d)) {
            if (RDouble.RDoubleUtils.fitsRInt(d)) {
                return (int) d;
            } else {
                if (naIntroduced != null) {
                    naIntroduced.naIntroduced = true;
                }
            }
        }
        return RInt.NA;
    }

    public static double int2double(int i) {
        return  i == RInt.NA ? RDouble.NA : i;
    }

    public static int double2logical(double d) {
        if (RDouble.RDoubleUtils.isNAorNaN(d)) {
            return RLogical.NA;
        }
        return d != 0 ? RLogical.TRUE : RLogical.FALSE;
    }

    public static double logical2double(int l) {
        return  l == RLogical.NA ? RDouble.NA : l;
    }

    public static byte double2raw(double d) {
        return double2raw(d, null, null);
    }

    public static byte double2raw(double d, NAIntroduced naIntroduced, OutOfRange outOfRange) {
        if (!RDouble.RDoubleUtils.isNAorNaN(d)) {
            if (d >= 0 && d < 256) {
                return (byte) d;
            }
            if (!RDouble.RDoubleUtils.fitsRInt(d)) {
                if (naIntroduced != null) {
                    naIntroduced.naIntroduced = true;
                }
                return RRaw.ZERO;
            }
        }
        if (outOfRange != null) {
            outOfRange.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static double raw2double(byte v) {
        return byteToUnsigned(v);
    }

    public static int int2logical(int i) {
        if (i == RInt.NA) {
            return RLogical.NA;
        }
        return i != 0 ? RLogical.TRUE : RLogical.FALSE;
    }

    public static int logical2int(int l) {
        return l;
    }

    public static byte int2raw(int v) {
        return int2raw(v, null);
    }

    public static byte int2raw(int v, OutOfRange outOfRange) {
        if (v >= 0 && v < 256) { // note: RInt.NA < 0
            return (byte) v;
        }
        if (outOfRange != null) {
            outOfRange.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static int raw2int(byte v) {
        return byteToUnsigned(v);
    }

    public static byte logical2raw(int v) {
        return logical2raw(v, null);
    }

    public static byte logical2raw(int v, OutOfRange outOfRange) {
        if (v != RLogical.NA) {
            return (byte) v;
        }
        if (outOfRange != null) {
            outOfRange.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static int raw2logical(byte v) {
        return byteToUnsigned(v);
    }

    public static String pretty(String s) {
        if (s != RString.NA) {
            return s;
        } else {
            return "NA";
        }
    }

    public static final NAIntroduced globalNAIntroduced = new NAIntroduced();

    public static RString coerceToStringError(RAny arg, ASTNode ast) { // WARNING: non-reentrant
        globalNAIntroduced.naIntroduced = false;
        RString res = arg.asString(globalNAIntroduced);
        if (!globalNAIntroduced.naIntroduced) {
            return res;
        } else {
            throw RError.getCannotCoerce(ast, arg.typeOf(), RString.TYPE_STRING);
        }
    }

    public static RString coerceToStringWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalNAIntroduced.naIntroduced = false;
        RString res = arg.asString(globalNAIntroduced);
        if (!globalNAIntroduced.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }

    public static RDouble coerceToDoubleWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalNAIntroduced.naIntroduced = false;
        RDouble res = arg.asDouble(globalNAIntroduced);
        if (!globalNAIntroduced.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }

    public static RInt coerceToIntWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalNAIntroduced.naIntroduced = false;
        RInt res = arg.asInt(globalNAIntroduced);
        if (!globalNAIntroduced.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }

    public static boolean checkFirstLogical(RAny arg, int value) {
        RLogical l = arg.asLogical();
        if (l.size() == 0) {
            return false;
        }
        return l.getLogical(0) == value;
    }

    public static int scalar2int(RAny v) { // FIXME: rewrite to scalar impl types if we have reliable scalarization, or remove
        if (v instanceof RInt) {
            return ((RInt) v).getInt(0);
        }
        if (v instanceof RDouble) {
            return double2int(((RDouble) v).getDouble(0));
        }
        if (v instanceof RLogical) {
            return logical2int(((RLogical) v).getLogical(0));
        }
        Utils.nyi("unsupported type");
        return -1;
    }

    public static double scalar2double(RAny v) { // FIXME: rewrite to scalar impl types if we have reliable scalarization, or remove
        if (v instanceof RInt) {
            return double2int(((RInt) v).getInt(0));
        }
        if (v instanceof RDouble) {
            return ((RDouble) v).getDouble(0);
        }
        if (v instanceof RLogical) {
            return logical2double(((RLogical) v).getLogical(0));
        }
        Utils.nyi("unsupported type");
        return -1;
    }

    static final String[] rawStrings = generateRawStrings();

    public static String[] generateRawStrings() {
        String[] res = new String[256];
        for (int i = 0; i < 256; i++) {
            res[i] = Integer.toHexString(i);
        }
        return res;
    }

    public static int byteToUnsigned(byte v) {
        return v & 0xFF;
    }
}
