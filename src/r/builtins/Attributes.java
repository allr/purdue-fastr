package r.builtins;

import java.util.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// NOTE: GNU-R keeps the ordering of attributes based on when they've been set, including to some level the common (here "special")
// attributes like name, dim, class, etc. We do not keep the ordering of specially handled attributes, and of these in respect to
// the other attributes. Fixing this would be possible but not without a performance overhead.

// FIXME: for now we only have special attributes, which need special handling on setters and getters and which are stored outside the RAny.Attributes
//        and then custom attributes, which do not need any special handling on setters and getters, and are stored in the RAny.Attributes
//
//        but in the future, we will need also attributes with special handling, but stored in the RAny.Attributes

public class Attributes {

    public static int countSpecialAttributes(RAny a) {
        int cnt = 0;
        if (a instanceof RArray) {
            RArray arr = (RArray) a;
            if (arr.dimensions() != null) {
                cnt++;
            }
            if (arr.names() != null) {
                cnt++;
            }
        }
        return cnt;
    }

    public static RInt dimensionsAsVector(int[] dimensions) {
        return RInt.RIntFactory.getFor(dimensions);
    }

    public static RString namesAsVector(RArray.Names names) {
        return RString.RStringFactory.getFor(names.asStringArray());
    }

    // when there are no custom attributes
    public static RAny specialAttributesAsList(RAny value) {
        int size = countSpecialAttributes(value);
        if (size == 0) {
            return RNull.getNull(); // no custom attributes
        }
        RAny[] acontent = new RAny[size];
        RSymbol[] anames = new RSymbol[size];

        fillSpecialAttributes(acontent, anames, 0, value);
        return RList.RListFactory.getFor(acontent, null, RArray.Names.create(anames));
    }

    public static void fillSpecialAttributes(RAny[] content, RSymbol[] names, int start, RAny value) {

        int i = start;
        if (value instanceof RArray) {
            RArray arr = (RArray) value;
            int[] dim = arr.dimensions();
            if (dim != null) {
                content[i] = dimensionsAsVector(dim);
                names[i] = RSymbol.DIM_SYMBOL;
                i++;
            }
            RArray.Names vnames = arr.names();
            if (vnames != null) {
                content[i] = namesAsVector(vnames);
                names[i] = RSymbol.NAMES_SYMBOL;
                i++;
            }
        }
    }

    public static void fillCustomAttributes(RAny[] content, RSymbol[] names, int start, Map<RSymbol, RAny> map) {

        int i = start;
        for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
            content[i] = entry.getValue();
            names[i] = entry.getKey();
            i++;
        }
    }

    public static final CallFactory ATTRIBUTES_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "obj", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg) {
                    RAny.Attributes attr = arg.attributes();
                    if (attr == null) {
                        return specialAttributesAsList(arg);
                    } else {
                        Map<RSymbol, RAny> map = attr.map();
                        int nspecial = countSpecialAttributes(arg);
                        int ncustom = map.size();
                        int size = nspecial + ncustom;

                        RAny[] acontent = new RAny[size];
                        RSymbol[] anames = new RSymbol[size];

                        fillSpecialAttributes(acontent, anames, 0, arg);
                        fillCustomAttributes(acontent, anames, nspecial, map);

                        return RList.RListFactory.getFor(acontent, null, RArray.Names.create(anames));
                    }
                }

            };
        }

    };

    public static final CallFactory ATTRIBUTES_REPLACEMENT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "obj", names[0]);
            return new BuiltIn.BuiltIn2(call, names, exprs) {

                @Override
                public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                    Utils.nyi();
                    return null;
                }

            };
        }

    };

    public static String parseWhich(RAny arg, ASTNode ast, String argName) {
        if (arg instanceof RString) {
            RString astr = (RString) arg;
            int size = astr.size();
            if (size > 0) {
                String s = astr.getString(0);
                if (s != RString.NA) {
                    return s;
                }
            }
        }
        throw RError.getMustBeNonNullString(ast, argName);
    }

    public static boolean parseExact(RAny arg) {
        // FIXME: most likely not exactly R semantics with evaluation of additional values
        RLogical larg = arg.asLogical();
        int size = larg.size();
        if (size > 0) {
            int v = larg.getLogical(0);
            if (v == RLogical.TRUE) {
                return true;
            }
        }
        return false;
    }

    public static final CallFactory ATTR_FACTORY = new CallFactory() {

        private final String[] paramNames = new String[]{"x", "which", "exact"};

        private static final int IX = 0;
        private static final int IWHICH = 1;
        private static final int IEXACT = 2;

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }
            if (!provided[IWHICH]) {
                BuiltIn.missingArg(call, paramNames[IWHICH]);
            }

            // FIXME: should specialize for constant attribute names
            // FIXME: should specialize based on exact value
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] args) {
                    RAny x = args[paramPositions[IX]];
                    String sname = parseWhich(args[paramPositions[IWHICH]], ast, paramNames[IWHICH]);
                    RSymbol name = RSymbol.getSymbol(sname);

                    RAny.Attributes attr = x.attributes();
                    if (attr != null) {
                        Map<RSymbol, RAny> map = attr.map();
                        RAny res = map.get(name);
                        if (res != null) {
                            return res;
                        }
                    }

                    if (name == RSymbol.DIM_SYMBOL) {
                        return Dimensions.getDim(x);
                    }
                    if (name == RSymbol.NAMES_SYMBOL) {
                        return Names.getNames(x);
                    }
                    // TODO: other special attributes

                    boolean exact = provided[IEXACT] ? parseExact(args[paramPositions[IEXACT]]) : false;
                    if (exact) {
                        return RNull.getNull();
                    }

                    // unique partial matching
                    if (name == RSymbol.NA_SYMBOL) {
                        return RNull.getNull();
                    }

                    RAny res = null;
                    if (RSymbol.DIM_SYMBOL.name().startsWith(sname)) {
                        res = Names.getNames(x); // note - dim is first checked
                    }
                    if (RSymbol.NAMES_SYMBOL.name().startsWith(sname)) {
                        res = Names.getNames(x); // note - names does not have common prefix with dim
                    }

                    if (attr != null) {
                        if (attr.hasPartialMap()) {
                            RSymbol fullName = attr.partialFind(name);
                            if (fullName != null) {
                                if (res != null) {
                                    return RNull.getNull(); // not unique
                                }
                                res = attr.map().get(fullName);
                            }
                        } else {
                            Map<RSymbol, RAny> map = attr.map();
                            if (map.size() > 4096) {
                                attr.createPartialMap();
                            }
                            for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
                                String sentry = entry.getKey().name();
                                if (sentry.startsWith(sname)) {
                                    if (res != null) {
                                        return RNull.getNull(); // not unique
                                    }
                                    res = entry.getValue();
                                }
                            }
                        }
                    }
                    return res != null ? res : RNull.getNull();
                }

            };
        }

    };

    public static final CallFactory ATTR_REPLACEMENT_FACTORY = new CallFactory() {

        private final String[] paramNames = new String[]{"x", "which"};

        private static final int IX = 0;
        private static final int IWHICH = 1;
        private static final int IREPLACEMENT = 2;

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }
            if (!provided[IWHICH]) {
                BuiltIn.missingArg(call, paramNames[IWHICH]);
            }

            // FIXME: should specialize for constant attribute names
            // TODO: handle not-allowed lhs (non-language object, etc)

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] args) {
                    RAny x = args[paramPositions[IX]];
                    String sname = parseWhich(args[paramPositions[IWHICH]], ast, paramNames[IWHICH]);
                    RSymbol name = RSymbol.getSymbol(sname);
                    RAny value = args[IREPLACEMENT];

                    if (name == RSymbol.NAMES_SYMBOL) {
                        return Names.replaceNames(x, value, ast);
                    }
                    // TODO: dimensions, other special attributes

                    // custom attributes
                    value.ref();
                    boolean xshared = x.isShared();
                    RAny.Attributes attr = x.attributes();

                    if (attr == null) {
                        attr = new RAny.Attributes();
                        attr.put(name, value);

                        if (!xshared) {
                            return x.setAttributes(attr);
                        } else {
                            return Utils.copyAny(x).setAttributes(attr);
                        }
                    }

                    if (!xshared && !attr.areShared()) {
                        attr.put(name, value);
                        return x;
                    }

                    // note: when x is shared, attributes always have to be copied no matter what is their reference count
                    if (xshared) {
                        x = Utils.copyAny(x); // does not deep copy or mark attributes
                    }

                    RAny.Attributes newAttr = attr.copy();
                    newAttr.put(name, value);
                    return x.setAttributes(newAttr);
                }

            };
        }

    };
}
