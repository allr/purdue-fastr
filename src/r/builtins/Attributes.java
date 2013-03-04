package r.builtins;

import java.util.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * Implements attributes. For performance reasons, names and dimension
 * attributes are treated specially. This has one drawback: the order of insert
 * of these special attributes is not preserved. ((FIXME: change that))
 */
class Attributes {

    private static final int PARTIAL_MAP_THRESHOLD = 256;

    /**
     * Return the count of special attributes present. There are at most two:
     * "dimensions" and "names"
     */
    private static int countSpecialAttributes(RAny a) {
        if (!(a instanceof RArray)) { return 0; }
        RArray arr = (RArray) a;
        int i = arr.dimensions() == null ? 0 : 1;
        int j = arr.names() == null ? 0 : 1;
        return i + j;
    }

    private static RInt dimensionsAsVector(int[] dimensions) {
        return RInt.RIntFactory.getFor(dimensions);
    }

    private static RString namesAsVector(RArray.Names names) {
        return RString.RStringFactory.getFor(names.asStringArray());
    }

    /** When there are no custom attributes. */
    private static RAny specialAttributesAsList(RAny value) {
        int size = countSpecialAttributes(value);
        if (size == 0) { return RNull.getNull(); } // no custom attributes
        RAny[] acontent = new RAny[size];
        RSymbol[] anames = new RSymbol[size];
        fillSpecialAttributes(acontent, anames, 0, value);
        return RList.RListFactory.getFor(acontent, null, RArray.Names.create(anames));
    }

    /**
     * This method will extract names and dimensions attributes from the value
     * argument. Argument start indicates where in the content and names arrays
     * the inserts should be made.
     */
    private static void fillSpecialAttributes(RAny[] content, RSymbol[] names, int start, RAny value) {
        if (!(value instanceof RArray)) { return; } // When is this the case?
        RArray arr = (RArray) value;
        int i = start;
        if (arr.dimensions() != null) {
            content[i] = dimensionsAsVector(arr.dimensions());
            names[i] = RSymbol.DIM_SYMBOL;
            i++;
        }
        if (arr.names() != null) {
            content[i] = namesAsVector(arr.names());
            names[i] = RSymbol.NAMES_SYMBOL;
        }
    }

    /**
     * Add all non-special attributes to content and names.
     */
    private static void fillAttributes(RAny[] content, RSymbol[] names, int start, Map<RSymbol, RAny> map) {
        int i = start;
        for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
            content[i] = entry.getValue();
            names[i] = entry.getKey();
            i++;
        }
    }

    /**
     * "attributes(obj)"
     * 
     * <pre>
     * obj -- an object
     * </pre>
     * 
     * The names of a pairlist are not stored as attributes, but are reported as
     * if they were.
     */
    public static final CallFactory ATTRIBUTES_FACTORY = new CallFactory() {

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "obj", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {
                @Override public final RAny doBuiltIn(Frame frame, RAny arg) {
                    RAny.Attributes attr = arg.attributes();
                    if (attr == null) { return specialAttributesAsList(arg); }
                    Map<RSymbol, RAny> map = attr.map();
                    int nspecial = countSpecialAttributes(arg);
                    int ncustom = map.size();
                    int size = nspecial + ncustom;
                    RAny[] acontent = new RAny[size];
                    RSymbol[] anames = new RSymbol[size];
                    fillSpecialAttributes(acontent, anames, 0, arg);
                    fillAttributes(acontent, anames, nspecial, map);
                    return RList.RListFactory.getFor(acontent, null, RArray.Names.create(anames));
                }
            };
        }
    };
    /**
     * "attributes(obj) <- value"
     * 
     * <pre>
     * obj -- an object
     * value -- an appropriate named list of attributes, or NULL.
     * </pre>
     * 
     * Unlike attr it is possible to set attributes on a NULL object: it will
     * first be coerced to an empty list. Note that some attributes (namely
     * class, comment, dim, dimnames, names, row.names and tsp) are treated
     * specially and have restrictions on the values which can be set. (Note
     * that this is not true of levels which should be set for factors via the
     * levels replacement function.) Attributes are not stored internally as a
     * list and should be thought of as a set and not a vector. They must have
     * unique names (and NA is taken as "NA", not a missing value). Assigning
     * attributes first removes all attributes, then sets any dim attribute and
     * then the remaining attributes in the order given: this ensures that
     * setting a dim attribute always precedes the dimnames attribute. The names
     * of a pairlist are not stored as attributes, but are reported as if they
     * were (and can be set by the replacement form of attributes).
     */
    public static final CallFactory ATTRIBUTES_REPLACEMENT_FACTORY = new CallFactory() {

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "obj", names[0]);
            return new BuiltIn.BuiltIn2(call, names, exprs) {
                @Override public RAny doBuiltIn(Frame frame, RAny obj, RAny value) {
                    RAny.Attributes attr = new RAny.Attributes();
                    obj = obj.setAttributes(attr);
                    if (value == RNull.getNull()) { return obj; }
                    if (!(value instanceof RList)) { throw new Error("FIXME"); }
                    RList val = (RList) value;
                    if (val.names() == null) { throw new Error("no names"); }
                    RSymbol[] vnames = val.names().sequence();
                    for (int i = 0; i < val.size(); i++) {
                        RAny v = val.getRAny(i);
                        RSymbol s = vnames[i];
                        if (s == RSymbol.NAMES_SYMBOL) {
                            Names.replaceNames(obj, v, ast);
                        } else if (s == RSymbol.DIM_SYMBOL) {
                            throw new Error("NY");
                        } else {
                            v.ref();
                            attr.put(s, v);
                        }
                    }
                    return obj;
                }
            };
        }
    };

    private static String IERR = "Internal Error";

    /**
     * Given a RString, return a non-null, non-NA boolean.
     */
    private static RSymbol getNonEmptySymbol(RAny arg, ASTNode ast, String argName) {
        if (!(arg instanceof RString)) { throw new Error(IERR); }
        RString astr = (RString) arg;
        if (astr.size() == 0) { throw RError.getMustBeNonNullString(ast, argName); }
        if (astr.getString(0) != RString.NA) { return RSymbol.getSymbol(astr.getString(0)); }
        throw new Error(IERR);
    }

    /**
     * FIXME: Check that these are the R semantics with valuation of additional
     * values.
     */
    private static boolean getBoolean(RAny arg) {
        RLogical l = arg.asLogical();
        if (l.size() == 0) { return false; }
        return l.getLogical(0) == RLogical.TRUE;
    }

    static final CallFactory ATTR_FACTORY = new AttrFactory();

    /**
     * "attr"
     * 
     * <pre>
     *  x -- an object whose attributes are to be accessed.
     *  which -- a non-empty character string specifying which attribute is to be accessed.
     *  exact -- logical: should which be matched exactly?
     * </pre>
     * 
     * The extraction function first looks for an exact match to which amongst
     * the attributes of x, then (unless exact = TRUE) a unique partial match.
     * (Setting options(warnPartialMatchAttr=TRUE) causes partial matches to
     * give warnings.) Some attributes (namely class, comment, dim, dimnames,
     * names, row.names and tsp) are treated specially and have restrictions on
     * the values which can be set. The extractor function allows (and does not
     * match) empty and missing values of which. NOTE: testing reveals that if x
     * has a single attribute, a value of "" for which will return its value. If
     * there are more than one value, "" is ambiguous.
     */
    static final class AttrFactory extends CallFactory {

        private static final String[] PNAMES = new String[] { "x", "which", "exact" };
        private static final int X = 0;
        private static final int WHICH = 1;
        private static final int EXACT = 2;

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, PNAMES);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;
            checkArgumentIsPresent(call, provided, PNAMES, X);
            checkArgumentIsPresent(call, provided, PNAMES, WHICH);

            // FIXME: should specialize for constant attribute names
            // FIXME: should specialize based on exact value
            return new BuiltIn(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                    RAny x = args[paramPositions[X]];
                    RSymbol which = getNonEmptySymbol(args[paramPositions[WHICH]], ast, PNAMES[WHICH]);
                    if (which == RSymbol.NA_SYMBOL) { return convertNullToRNull(null); }
                    RAny res = getExactMatchOrNull(x, which);
                    if (res != null) { return res; }
                    boolean exactRequested = provided[EXACT] ? getBoolean(args[paramPositions[EXACT]]) : false;
                    if (exactRequested) { return convertNullToRNull(null); }
                    // unique partial matching
                    res = RSymbol.DIM_SYMBOL.name().startsWith(which.name()) ? Dimensions.getDim(x) : null;
                    res = res == null && RSymbol.NAMES_SYMBOL.name().startsWith(which.name()) ? Names.getNames(x) : null;
                    // note - names do not have common prefix with dim // prefix
                    RAny.Attributes attr = x.attributes();
                    if (attr == null) { return convertNullToRNull(res); }
                    if (attr.hasPartialMap()) {
                        RSymbol fullName = attr.partialFind(which);
                        // nothing found, return what we have
                        if (fullName == null) { return convertNullToRNull(res); }
                        // ambiguity, return null
                        if (res != null) { return convertNullToRNull(null); }
                        // return attribute
                        return convertNullToRNull(attr.map().get(fullName));
                    } else {
                        Map<RSymbol, RAny> map = attr.map();
                        if (map.size() > PARTIAL_MAP_THRESHOLD) {
                            attr.createPartialMap();
                        }
                        // TODO: fix this, not really working yet
                        for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
                            String sentry = entry.getKey().name();
                            if (sentry.startsWith(which.name())) {
                                if (res != null) { return convertNullToRNull(null); }
                                res = entry.getValue();
                            }
                        }
                        return convertNullToRNull(res);
                    }
                }

                private RAny convertNullToRNull(RAny res) {
                    return res != null ? res : RNull.getNull();
                }

                private RAny getExactMatchOrNull(RAny x, RSymbol which) {
                    RAny.Attributes attr = x.attributes();
                    if (attr != null) {
                        Map<RSymbol, RAny> map = attr.map();
                        RAny res = map.get(which);
                        if (res != null) { return res; }
                    }
                    if (which == RSymbol.DIM_SYMBOL) { return Dimensions.getDim(x); }
                    if (which == RSymbol.NAMES_SYMBOL) { return Names.getNames(x); }
                    return null;
                }
            };
        }
    }

    static final CallFactory ATTR_REPLACEMENT_FACTORY = new AttrReplacementFactory();

    /**
     * "attr<-" Arguments are:
     * 
     * <pre>
     *  x -- an object whose attributes are to be accessed.
     *  which -- a non-empty character string specifying which attribute is to be accessed.
     *  value -- an object, the new value of the attribute, or NULL to remove the attribute.
     * </pre>
     * 
     * The function only uses exact matches. The function does not allow missing
     * values of which.
     */
    // FIXME: attr<- does not print its output in R.
    static final class AttrReplacementFactory extends CallFactory {

        private static final String[] PNAMES = new String[] { "x", "which" };
        private static final int X = 0;
        private static final int WHICH = 1;
        private static final int VALUE = 2;

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, PNAMES);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;
            checkArgumentIsPresent(call, provided, PNAMES, X);
            checkArgumentIsPresent(call, provided, PNAMES, WHICH);

            // FIXME: should specialize for constant attribute names
            // TODO: handle not-allowed lhs (non-language object, etc)
            return new BuiltIn(call, names, exprs) {

                @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                    RAny x = args[paramPositions[X]];
                    RSymbol which = getNonEmptySymbol(args[paramPositions[WHICH]], ast, PNAMES[WHICH]);
                    RAny value = args[VALUE];
                    if (which == RSymbol.NAMES_SYMBOL) { return Names.replaceNames(x, value, ast); }
                    if (which == RSymbol.DIM_SYMBOL) { throw new Error("NOT YET"); }
                    // TODO: dimensions, other special attributes custom
                    // attributes
                    value.ref();
                    // NOTE: when x is shared, attributes always have to be
                    // copied no matter what is their reference count
                    RAny.Attributes attr = x.attributes();
                    if (!x.isShared() && attr != null && !attr.areShared()) {
                        attr.put(which, value);
                        return x;
                    }
                    x = x.isShared() ? Utils.copyAny(x) : x;
                    // does not deep copy or mark attributes
                    attr = attr == null ? new RAny.Attributes() : attr.copy();
                    attr.put(which, value);
                    return x.setAttributes(attr);
                }
            };
        }
    }
}
