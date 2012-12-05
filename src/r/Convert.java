package r;

import r.data.*;

public class Convert {

    // NOTE: conversion functions do not clear naIntroduced, they only set it if non-null and NA has been introduced
    // indeed this could also be a static field, but hopefully this is faster when no NAs are introduced

    public static class NAIntroduced {
        public boolean naIntroduced;
    }

    public static double string2double(String v) {
        return string2double(v, null);
    }

    public static double string2double(String v, NAIntroduced naIntroduced) {
        if (!v.equals("NA")) {
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

    public static String double2string(double d) {
        if (!RDouble.RDoubleUtils.isNA(d)) {
            // FIXME use R rules
            if (!RContext.debuggingFormat()) {
                if (RDouble.RDoubleUtils.fitsRInt(d)) {
                    return int2string((int) d); // a hack to get rid of ".0" in "1.0"
                }
            }
            return Double.toString(d);
        }
        return "NA";
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
                return "NA";
            default:
                return "TRUE";
        }
    }

    public static String int2string(int i) {
        if (i == RInt.NA) {
            return "NA";
        }
        // FIXME use R rules
        if (!RContext.debuggingFormat()) {
            return Integer.toString(i);
        } else {
            return Integer.toString(i) + "L";
        }
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

    public static int double2int(double d) {
        return double2int(d, null);
    }

    public static int double2int(double d, NAIntroduced naIntroduced) {
        if (!RDouble.RDoubleUtils.isNA(d)) {
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

    public static double logical2double(int l) {
        return  l == RLogical.NA ? RDouble.NA : l;
    }

    public static double int2double(int i) {
        return  i == RInt.NA ? RDouble.NA : i;
    }

    public static int double2logical(double d) {
        if (RDouble.RDoubleUtils.isNA(d)) {
            return RLogical.NA;
        }
        return d != 0 ? RLogical.TRUE : RLogical.FALSE;
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
}
