package r.builtins;

import java.util.*;

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
final class Attr extends CallFactory {

    static final CallFactory _ = new Attr("attr", new String[]{"x", "which", "exact"}, new String[]{"x", "which"});

    private static final int PARTIAL_MAP_THRESHOLD = 256;

    private Attr(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    static RSymbol parseWhich(RAny arg, ASTNode ast) {
        if (!(arg instanceof RString)) {
            throw RError.getMustBeCharacter(ast, "which");
        }
        RString astr = (RString) arg;
        if (astr.size() != 1) {
            throw RError.getExactlyOneWhich(ast);
        }
        return RSymbol.getSymbol(astr.getString(0));
    }

    private static boolean parseExact(RAny arg) {
        RLogical l = arg.asLogical();
        if (l.size() >= 1) {
            if (l.getLogical(0) == RLogical.TRUE) {
                return true;
            }
        }
        return false;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int posX = ai.position("x");
        final int posWhich = ai.position("which");
        final int posExact = ai.position("exact");

        // FIXME: should specialize for constant attribute names
        // FIXME: should specialize based on exact value
        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny x = args[posX];
                RSymbol which = parseWhich(args[posWhich], ast);

                // exact matching
                RAny res = getExactMatchOrNull(x, which);
                if (res != null) {
                    return res;

                }
                boolean exactRequested = posExact != -1 ? parseExact(args[posExact]) : false;
                if (exactRequested) {
                    return RNull.getNull();
                }

                // unique partial matching
                // note - names do not have common prefix with dim
                if (RSymbol.DIM_SYMBOL.startsWith(which)) {
                    res = DimensionsBase.getDim(x);
                } else if (RSymbol.NAMES_SYMBOL.startsWith(which)) {
                    res = Names.getNames(x);
                }

                RAny.Attributes attr = x.attributes();
                if (attr == null) {
                    return convertNullToRNull(res);
                }
                if (which == RSymbol.NA_SYMBOL) { // partial map cannot hold RSymbol.NA_SYMBOL
                    return RNull.getNull();
                }

                if (attr.hasPartialMap()) {
                    RSymbol fullName = attr.partialFind(which);
                    // nothing found, return what we have
                    if (fullName == null) { return convertNullToRNull(res); }
                    // ambiguity, return null
                    if (res != null) { return RNull.getNull(); }
                    // return attribute
                    return convertNullToRNull(attr.map().get(fullName));
                } else {
                    Map<RSymbol, RAny> map = attr.map();
                    if (map.size() > PARTIAL_MAP_THRESHOLD) {
                        attr.createPartialMap();
                    }
                    for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
                        String sentry = entry.getKey().name();
                        if (sentry.startsWith(which.name())) {
                            if (res != null) { return RNull.getNull(); } // ambiquity
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
                if (which == RSymbol.DIM_SYMBOL) { return DimensionsBase.getDim(x); }
                if (which == RSymbol.NAMES_SYMBOL) { return Names.getNames(x); }

                RAny.Attributes attr = x.attributes();
                if (attr == null) {
                    return null;
                } else {
                    Map<RSymbol, RAny> map = attr.map();
                    return map.get(which);
                }
            }
        };
    }
}
