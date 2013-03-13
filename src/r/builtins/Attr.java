package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "attr"
 * 
 * <pre>
 *  x -- an object whose attributes are to be accessed.
 *  which -- a non-empty character string specifying which attribute is to be accessed.
 *  exact -- logical: should which be matched exactly?
 * </pre>
 * 
 * The extraction function first looks for an exact match to which amongst the attributes of x, then (unless exact =
 * TRUE) a unique partial match. (Setting options(warnPartialMatchAttr=TRUE) causes partial matches to give warnings.)
 * Some attributes (namely class, comment, dim, dimnames, names, row.names and tsp) are treated specially and have
 * restrictions on the values which can be set. The extractor function allows (and does not match) empty and missing
 * values of which. NOTE: testing reveals that if x has a single attribute, a value of "" for which will return its
 * value. If there are more than one value, "" is ambiguous.
 */
class Attr extends CallFactory {

    static final CallFactory _ = new Attr("attr", new String[]{"x", "which", "exact"}, new String[]{"x", "which"});

    private static final int PARTIAL_MAP_THRESHOLD = 256;

    Attr(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    /**
     * Given a RString, return a non-null, non-NA boolean.
     */
    static RSymbol getNonEmptySymbol(RAny arg, ASTNode ast, String argName) {
        if (!(arg instanceof RString)) { throw Utils.nyi(); }
        RString astr = (RString) arg;
        if (astr.size() == 0) { throw RError.getMustBeNonNullString(ast, argName); }
        if (astr.getString(0) != RString.NA) { return RSymbol.getSymbol(astr.getString(0)); }
        throw Utils.nyi();
    }

    /**
     * FIXME: Check that these are the R semantics with valuation of additional values.
     */
    private static boolean getBoolean(RAny arg) {
        RLogical l = arg.asLogical();
        if (l.size() == 0) { return false; }
        return l.getLogical(0) == RLogical.TRUE;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int posX = ai.position("x");
        final int posWhich = ai.position("which");
        final int posExact = ai.position("exact");

        // FIXME: should specialize for constant attribute names
        // FIXME: should specialize based on exact value
        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny x = args[posX];
                RSymbol which = Attr.getNonEmptySymbol(args[posWhich], ast, "which");
                if (which == RSymbol.NA_SYMBOL) { return convertNullToRNull(null); }
                RAny res = getExactMatchOrNull(x, which);
                if (res != null) { return res; }
                boolean exactRequested = posExact != -1 ? getBoolean(args[posExact]) : false;
                if (exactRequested) { return convertNullToRNull(null); }
                // unique partial matching
                res = RSymbol.DIM_SYMBOL.startsWith(which) ? DimensionsBase.getDim(x) : null;
                res = res == null && RSymbol.NAMES_SYMBOL.startsWith(which) ? Names.getNames(x) : null;
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
                if (which == RSymbol.DIM_SYMBOL) { return DimensionsBase.getDim(x); }
                if (which == RSymbol.NAMES_SYMBOL) { return Names.getNames(x); }
                return null;
            }
        };
    }
}
