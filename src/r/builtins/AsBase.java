package r.builtins;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

import java.lang.Integer;//conflict with the local integer

// FIXME: only partial implementation, particularly of as.character   (as.character in R deparses lists, etc)
// FIXME: There is no warning when NAs are introduced; this could be fixed in case of lists (below), but not with lazy casts (views)

/** Casts and conversions. */
abstract class AsBase extends CallFactory {

    AsBase(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final ConversionStatus warn = new ConversionStatus(); // WARNING: calls not re-entrant

    abstract RAny genericCast(ASTNode ast, RAny arg);

    abstract RAny getEmpty();

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 0) { return new Builtin.Builtin0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return getEmpty();
            }
        }; }
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                return genericCast(ast, arg);
            }
        };
    }

    // list
    static RAny genericAsList(ASTNode ast, RAny arg) {
        throw Utils.nyi();
    }

    static boolean isRecursive(RList list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (list.getRAny(i) instanceof RList) { return true; }
        }
        return false;
    }

    // string
    static String deparse(RAny v, boolean quote) {
        if (v instanceof RNull) { return "NULL"; }
        if (!(v instanceof RArray)) { throw Utils.nyi("unsupported type"); }
        RArray a = (RArray) v;
        int size = a.size();
        if (size == 0) {
            return v.pretty(); // e.g. "character(0)"
        } else if (size == 1) {
            if (!quote || !(a instanceof RString)) {
                return v.pretty(); // e.g. 1L
            } else {
                return "\\\"" + ((RString) a).getString(0) + "\\\""; // FIXME: quote also the string content
            }
        } else {
            if (v instanceof RInt) {
                RInt ival = (RInt) v;
                int from = ival.getInt(0);
                int last = from;
                boolean isSequence = true;
                for (int i = 1; i < size; i++) {
                    int n = ival.getInt(i);
                    if (n - 1 == last) {
                        last = n;
                    } else {
                        isSequence = false;
                        break;
                    }
                }
                if (isSequence) { return Integer.toString(from) + ":" + Integer.toString(last); }
            }
            StringBuilder str = new StringBuilder();
            str.append("c(");
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    str.append(", ");
                }
                RAny e = ((RArray) v).boxedGet(i); // FIXME: boxing
                if (!(e instanceof RString)) {
                    str.append(e.pretty());
                } else {
                    str.append("\\\"");
                    str.append(((RString) e).getString(0));
                    str.append("\\\"");
                }
            }
            str.append(")");
            return str.toString();
        }
    }

    static void listAsString(ASTNode ast, StringBuilder str, RList list, boolean isRecursive) {
        int size = list.size();
        if (!isRecursive) {
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    str.append(", ");
                }
                RAny e = list.getRAny(i);
                str.append(deparse(e, true));
            }
        } else {
            str.append("list(");
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    str.append(", ");
                }
                RAny e = list.getRAny(i);
                if (e instanceof RList) {
                    RList child = (RList) e;
                    listAsString(ast, str, child, isRecursive(child));
                } else {
                    str.append(deparse(e, true));
                }
            }
            str.append(")");
        }
    }

    static RString genericAsString(ASTNode ast, RAny arg) {
        if (!(arg instanceof RList)) {
            return (RString) arg.asString().stripAttributes();
        } else {
            RList list = (RList) arg;
            if (!isRecursive(list)) {
                int size = list.size();
                String[] content = new String[size];
                for (int i = 0; i < size; i++) {
                    RAny e = list.getRAny(i);
                    content[i] = deparse(e, false);
                }
                return RString.RStringFactory.getFor(content);
            } else {
                StringBuilder str = new StringBuilder();
                listAsString(ast, str, list, true);
                return RString.RStringFactory.getScalar(str.toString());
            }
        }
    }

    // complex
    static RAny genericAsComplex(ASTNode ast, RAny arg) {
        if (!(arg instanceof RList)) { return Convert.coerceToComplexWarning(arg, ast).stripAttributes(); }
        warn.naIntroduced = false;
        RList l = (RList) arg;
        double[] content = new double[2 * l.size()];
        for (int i = 0; i < l.size(); i++) {
            RArray a = (RArray) l.getRAny(i);
            if (a.size() == 1) {
                if (a instanceof RList) {
                    content[2 * i] = RDouble.NA;
                    content[2 * i + 1] = RDouble.NA;
                } else {
                    RComplex ca = a.asComplex(warn);
                    content[2 * i] = ca.getReal(0);
                    content[2 * i + 1] = ca.getImag(0);
                }
            } else {
                if (a.size() > 1) { throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "complex")); }
                content[i] = RDouble.NA;
            }
        }
        if (warn.naIntroduced) {
            RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
        }
        return RComplex.RComplexFactory.getFor(content); // drop attributes
    }

    /**
     * Given an RList arg, return an vector of doubles. The RList must consist of scalar values. NAs will be introduced
     * for any non-coerceable value encountered. An error will be thrown if a non-scalar vector is found.
     */
    static RAny genericAsDouble(ASTNode ast, RAny arg) {
        if (!(arg instanceof RList)) { return Convert.coerceToDoubleWarning(arg, ast).stripAttributes(); }
        warn.naIntroduced = false;
        RList l = (RList) arg;
        double[] content = new double[l.size()];
        for (int i = 0; i < l.size(); i++) {
            RArray a = (RArray) l.getRAny(i);
            if (a.size() == 1) {
                content[i] = a instanceof RList ? RDouble.NA : a.asDouble(warn).getDouble(0); // FIXME error handling - NA + warning
            } else {
                if (a.size() > 1) { throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "numeric")); }
                content[i] = RDouble.NA;
            }
        }
        if (warn.naIntroduced) {
            RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
        }
        return RDouble.RDoubleFactory.getFor(content); // drop attributes
    }

    /**
     * Given an RList arg, return an vector of integers. The RList must consist of scalar values. NAs will be introduced
     * for any non-coercible value encountered. An error will be thrown if a non-scalar vector is found.
     */
    static RAny genericAsInt(ASTNode ast, RAny arg) {
        if (!(arg instanceof RList)) { return Convert.coerceToIntWarning(arg, ast).stripAttributes(); }
        warn.naIntroduced = false;
        RList l = (RList) arg;
        int[] content = new int[l.size()];
        for (int i = 0; i < l.size(); i++) {
            RArray a = (RArray) l.getRAny(i);
            if (a.size() == 1) {
                content[i] = a instanceof RList ? RInt.NA : a.asInt(warn).getInt(0);
            } else {
                if (a.size() > 1) { throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "integer")); }
                content[i] = RInt.NA;
            }
        }
        if (warn.naIntroduced) {
            RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
        }
        return RInt.RIntFactory.getFor(content); // drop attributes
    }

    /**
     * Given an RList arg, return an vector of logicals. The RList must consist of scalar values. NAs will be introduced
     * for any non-coercible value encountered. An error will be thrown if a non-scalar vector is found.
     */
    static RAny genericAsLogical(ASTNode ast, RAny arg) {
        if (!(arg instanceof RList)) { return arg.asLogical().stripAttributes(); }// note: coercion to logical produces no warnings
        RList l = (RList) arg;
        int[] content = new int[l.size()];
        for (int i = 0; i < l.size(); i++) {
            RArray a = (RArray) l.getRAny(i);
            if (a.size() == 1) {
                content[i] = a instanceof RList ? RInt.NA : a.asLogical().getLogical(0);
            } else {
                if (a.size() > 1) { throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "logical")); }
                content[i] = RLogical.NA;
            }
        }
        return RLogical.RLogicalFactory.getFor(content); // drop attributes
    }

    // raw
    static RAny genericAsRaw(ASTNode ast, RAny arg) {
        if (!(arg instanceof RList)) { return Convert.coerceToRawWarning(arg, ast).stripAttributes(); }
        warn.outOfRange = false;
        warn.naIntroduced = false;
        RList l = (RList) arg;
        byte[] content = new byte[l.size()];
        for (int i = 0; i < l.size(); i++) {
            RArray a = (RArray) l.getRAny(i);
            if (a.size() == 1) {
                if (a instanceof RList) {
                    content[i] = RRaw.ZERO;
                    warn.outOfRange = true;
                } else {
                    content[i] = a.asRaw(warn).getRaw(0); // FIXME error handling - NA + warning
                }
            } else {
                if (a.size() > 1) { throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "raw")); }
                content[i] = RRaw.ZERO;
                warn.outOfRange = true;
            }
        }
        if (warn.naIntroduced) {
            RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
        }
        if (warn.naIntroduced) {
            RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
            RContext.warning(ast, RError.OUT_OF_RANGE);
        } else if (warn.outOfRange) {
            RContext.warning(ast, RError.OUT_OF_RANGE);
        }
        return RRaw.RRawFactory.getFor(content); // drop attributes
    }

    static RAny genericAsVector(ASTNode ast, RAny arg0, RAny arg1) {
        if (!(arg1 instanceof RString)) { throw RError.getInvalidMode(ast); }
        RString ms = (RString) arg1;
        if (ms.size() != 1) { throw RError.getInvalidMode(ast); }
        String mode = ms.getString(0);
        if (mode.equals("any")) { return arg0 instanceof RList ? arg0 : arg0.stripAttributes(); }// is it a bug of GNU-R that list attributes are not stripped?
        if (mode.equals("integer")) { return genericAsInt(ast, arg0); }
        if (mode.equals("numeric") || mode.equals("double")) { return genericAsDouble(ast, arg0); }
        if (mode.equals("logical")) { return genericAsLogical(ast, arg0); }
        if (mode.equals("list")) { return genericAsList(ast, arg0); }
        if (mode.equals("character")) { return genericAsString(ast, arg0); }
        if (mode.equals("raw")) { return genericAsRaw(ast, arg0); }
        throw Utils.nyi("unsupported mode");
    }
}
