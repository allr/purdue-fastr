package r;

import java.util.regex.*;

import r.data.*;
import r.data.RComplex.Complex;
import r.errors.*;
import r.nodes.*;

// FIXME: will have to support printing doubles to a given number of complex digits (Double.toString uses a different algorithm)
public class Convert {

    // NOTE: conversion functions do not clear naIntroduced, they only set it if non-null and NA has been introduced
    // indeed this could also be a static field, but hopefully this is faster when no NAs are introduced

    // outOfRange is implied by naIntroduced (not set in that case)
    public static class ConversionStatus {
        public boolean naIntroduced;
        public boolean outOfRange; // when converting to raw
        public boolean imagDiscarded; // when converting from complex
    }

    static final Pattern numberSplit = Pattern.compile("^[+-]?[^e+-]+(?:e[+-]?[\\d]+)?");

    public static Complex string2complex(String v) {
        return string2complex(v, null);
    }

    // FIXME: this is slow and only an approximation of R semantics
    public static Complex string2complex(String v, ConversionStatus warn) {
        if (v != RString.NA) {
            String input = v.trim();
            Matcher m = numberSplit.matcher(input);
            if (m.find()) {
                int realEnd = m.end();
                String sreal = input.substring(m.start(), realEnd);
                double real = string2double(sreal, warn);
                double imag = 0;
                int imagEnd = input.length();
                if (realEnd != imagEnd) {
                    if (input.charAt(imagEnd - 1) == 'i') {
                        String simag = input.substring(realEnd, imagEnd - 1);
                        imag = string2double(simag, warn);
                    } else {
                        if (warn != null) {
                            warn.naIntroduced = true;
                        }
                        return Complex.NA;
                    }
                }
                return new Complex(real, imag);
            }
        }
        if (warn != null) {
            warn.naIntroduced = true;
        }
        return Complex.NA;
    }

    public static double string2double(String v) {
        return string2double(v, null);
    }

    public static double string2double(String v, ConversionStatus warn) {
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
                if (warn != null) {
                    warn.naIntroduced = true;
                }
            }
        }
        return RDouble.NA;
    }

//    @SuppressWarnings("cast")
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

    public static int string2int(String s, ConversionStatus warn) {
        if (s != RString.NA) {
            // FIXME use R rules
            try {
                return Integer.decode(s);  // decode supports hex constants
            } catch (NumberFormatException e) {
                if (warn != null) {
                    warn.naIntroduced = true;
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

    public static int string2logical(String s, ConversionStatus warn) {
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
            if (warn != null) {
                warn.naIntroduced = true;
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
        return string2raw(s, null);
    }

    public static byte string2raw(String s, ConversionStatus warn) {
        if (s != RString.NA) {
            // FIXME use R rules
            int intVal;
            try {
                 intVal = Integer.decode(s);  // decode supports hex constants
            } catch (NumberFormatException e) {
                if (warn != null) {
                    warn.naIntroduced = true;
                }
                return 0;
            }
            if (intVal >= 0 && intVal <= 255) {
                return (byte) intVal;
            }
        }
        if (warn != null) {
            warn.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static String raw2string(byte v) {
        return rawStrings[byteToUnsigned(v)];
    }

    public static String complex2string(double real, double imag) {
        if (!RComplex.RComplexUtils.eitherIsNA(real, imag)) {
            String sgn = (imag >= 0) ? "+" : "";
            return double2string(real) + sgn + double2string(imag) + "i"; // FIXME: could elide some NA checks through hand-inlining
        }
        return RString.NA;
    }

    public static double complex2double(double real, double imag) {
        return complex2double(real, imag, null);
    }

    public static double complex2double(double real, double imag, ConversionStatus warn) {
        if (!RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
            if (imag != 0 && warn != null) {
                warn.imagDiscarded = true;
            }
            return real;
        }
        return RDouble.NA;
    }

    public static int complex2int(double real, double imag) {
        return complex2int(real, imag, null);
    }

    public static int complex2int(double real, double imag, ConversionStatus warn) {
        if (!RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
            if (RDouble.RDoubleUtils.fitsRInt(real)) {
                if (imag != 0 && warn != null) {
                    warn.imagDiscarded = true;
                }
                return (int) real;
            } else {
                if (warn != null) {
                    warn.naIntroduced = true;
                }
            }
        }
        return RInt.NA;
    }

    public static int complex2logical(double real, double imag) {
        if (!RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
            boolean v = (real != 0 || imag != 0);
            return v ? RLogical.TRUE : RLogical.FALSE;
        } else {
            return RLogical.NA;
        }
    }

    public static byte complex2raw(double real, double imag) {
        return complex2raw(real, imag, null);
    }

    public static byte complex2raw(double real, double imag, ConversionStatus warn) {
        if (!RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
            if (real >= 0 && real < 256) {
                if (imag != 0 && warn != null) {
                    warn.imagDiscarded = true;
                }
                return (byte) real;
            }
            if (!RDouble.RDoubleUtils.fitsRInt(real)) {
                if (warn != null) {
                    warn.naIntroduced = true;
                    // warn.outOfRange = true;  -- implied
                }
                return RRaw.ZERO;
            }
            // fits an integer, but not raw - out of range
            if (imag != 0 && warn != null) {
                warn.imagDiscarded = true;
            }
        }
        if (warn != null) {
            warn.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static int double2int(double d) {
        return double2int(d, null);
    }

    public static int double2int(double d, ConversionStatus warn) {
        if (!RDouble.RDoubleUtils.isNAorNaN(d)) {
            if (RDouble.RDoubleUtils.fitsRInt(d)) {
                return (int) d;
            } else {
                if (warn != null) {
                    warn.naIntroduced = true;
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
        return double2raw(d, null);
    }

    public static byte double2raw(double d, ConversionStatus warn) {
        if (!RDouble.RDoubleUtils.isNAorNaN(d)) {
            if (d >= 0 && d < 256) {
                return (byte) d;
            }
            if (!RDouble.RDoubleUtils.fitsRInt(d)) {
                if (warn != null) {
                    warn.naIntroduced = true;
                }
                return RRaw.ZERO;
            }
        }
        if (warn != null) {
            warn.outOfRange = true;
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

    public static byte int2raw(int v, ConversionStatus warn) {
        if (v >= 0 && v < 256) { // note: RInt.NA < 0
            return (byte) v;
        }
        if (warn != null) {
            warn.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static int raw2int(byte v) {
        return byteToUnsigned(v);
    }

    public static byte logical2raw(int v) {
        return logical2raw(v, null);
    }

    public static byte logical2raw(int v, ConversionStatus warn) {
        if (v != RLogical.NA) {
            return (byte) v;
        }
        if (warn != null) {
            warn.outOfRange = true;
        }
        return RRaw.ZERO;
    }

    public static int raw2logical(byte v) {
        return byteToUnsigned(v) != 0 ? RLogical.TRUE : RLogical.FALSE;
    }

    public static String prettyNA(String s) {
        if (s != RString.NA) {
            return s;
        } else {
            return "NA";
        }
    }

    public static String prettyGTNALT(String s) {
        if (s != RString.NA) {
            return s;
        } else {
            return "<NA>";
        }
    }

    public static void prettyGTNALT(String[] s) { // re-uses array !
        for (int i = 0; i < s.length; i++) {
            if (s[i] == RString.NA) {
                s[i] = "<NA>";
            }
        }
    }

    public static final ConversionStatus globalConversionStatus = new ConversionStatus();

    public static RString coerceToStringError(RAny arg, ASTNode ast) { // WARNING: non-reentrant
        globalConversionStatus.naIntroduced = false;
        RString res = arg.asString(globalConversionStatus);
        if (!globalConversionStatus.naIntroduced) {
            return res;
        } else {
            throw RError.getCannotCoerce(ast, arg.typeOf(), RString.TYPE_STRING);
        }
    }

    public static RString coerceToStringWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalConversionStatus.naIntroduced = false;
        RString res = arg.asString(globalConversionStatus);
        if (!globalConversionStatus.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }

    public static RComplex coerceToComplexWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalConversionStatus.naIntroduced = false;
        RComplex res = arg.asComplex(globalConversionStatus);
        if (!globalConversionStatus.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }


    public static RDouble coerceToDoubleWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalConversionStatus.naIntroduced = false;
        RDouble res = arg.asDouble(globalConversionStatus);
        if (!globalConversionStatus.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }

    public static RInt coerceToIntWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalConversionStatus.naIntroduced = false;
        RInt res = arg.asInt(globalConversionStatus);
        if (!globalConversionStatus.naIntroduced) {
            return res;
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            return res;
        }
    }

    public static RRaw coerceToRawWarning(RAny arg, RContext context, ASTNode ast) { // WARNING: non-reentrant
        globalConversionStatus.naIntroduced = false;
        globalConversionStatus.outOfRange = false;
        RRaw res = arg.asRaw(globalConversionStatus);
        if (!globalConversionStatus.naIntroduced) {
            if (!globalConversionStatus.outOfRange) {
                // nothing
            } else {
                context.warning(ast, RError.OUT_OF_RANGE);
            }
        } else {
            context.warning(ast, RError.NA_INTRODUCED_COERCION);
            context.warning(ast, RError.OUT_OF_RANGE);
        }
        return res;
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

    public static String[] symbols2strings(RSymbol[] symbols) {
        int size = symbols.length;
        String[] res = new String[size];
        for (int i = 0; i < size; i++) {
            res[i] = symbols[i].pretty();
        }
        return res;
    }

    static final String[] rawStrings = generateRawStrings();

    public static String[] generateRawStrings() {
        String[] res = new String[256];
        for (int i = 0; i < 256; i++) {
            res[i] = Integer.toHexString(i / 16) + Integer.toHexString(i % 16);
        }
        return res;
    }

    public static int byteToUnsigned(byte v) {
        return v & 0xFF;
    }
}
