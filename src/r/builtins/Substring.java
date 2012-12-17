package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert.ConversionStatus;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Substring {

    private static final String[] substringParamNames = new String[]{"text", "first", "last"};

    private static final int ITEXT = 0;
    private static final int IFIRST = 1;
    private static final int ILAST = 2;

    static final ConversionStatus warn = new ConversionStatus(); // WARNING: calls not re-entrant

    public static final CallFactory SUBSTRING_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, substringParamNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[ITEXT]) {
                BuiltIn.missingArg(call, substrParamNames[ITEXT]);
            }
            if (!provided[IFIRST]) {
                BuiltIn.missingArg(call, substrParamNames[IFIRST]);
            }
            if (names.length == 3) {
                if (!provided[ILAST]) {
                    BuiltIn.missingArg(call, substrParamNames[ILAST]);
                }
            }
            final RDouble defaultLast = RDouble.RDoubleFactory.getScalar(1000000); // FIXME slow, but perhaps the default is not used, anyway
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    RString text = args[paramPositions[ITEXT]].asString();
                    warn.naIntroduced = false;
                    RDouble first = args[paramPositions[IFIRST]].asDouble(warn);
                    RDouble last;
                    if (provided[ILAST]) {
                        last = args[paramPositions[ILAST]].asDouble(warn);
                    } else {
                        last = defaultLast;
                    }

                    RString res = substring(text, first, last, context, ast);
                    if (warn.naIntroduced) {
                        context.warning(ast, RError.NA_INTRODUCED_COERCION);
                    }
                    return res;
                }
            };
        }
    };

    private static final String[] substrParamNames = new String[]{"x", "start", "stop"};

    private static final int IX = 0;
    private static final int ISTART = 1;
    private static final int ISTOP = 2;

    public static final CallFactory SUBSTR_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, substrParamNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, substrParamNames[IX]);
            }
            if (!provided[ISTART]) {
                BuiltIn.missingArg(call, substrParamNames[ISTART]);
            }
            if (!provided[ISTOP]) {
                BuiltIn.missingArg(call, substrParamNames[ISTOP]);
            }

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    RString x = args[paramPositions[IX]].asString();
                    warn.naIntroduced = false;
                    RDouble start = args[paramPositions[ISTART]].asDouble(warn);
                    RDouble stop = args[paramPositions[ISTOP]].asDouble(warn);

                    RString res = substr(x, start, stop, context, ast);
                    if (warn.naIntroduced) {
                        context.warning(ast, RError.NA_INTRODUCED_COERCION);
                    }
                    return res;
                }
            };

        }
    };

    public static RString substr(RString x, RDouble start, RDouble stop, RContext context, ASTNode ast) {
        int xsize = x.size();
        int startSize = start.size();
        int stopSize = stop.size();
        int startIndex = 0;
        int stopIndex = 0;

        if (xsize == 0) {
            return RString.EMPTY;
        }
        if (startSize == 0) {
            throw RError.getInvalidArgument(ast, "start"); // not exactly R-warning
        }
        if (stopSize == 0) {
            throw RError.getInvalidArgument(ast, "stop"); // not exactly R-warning
        }

        String[] content = new String[xsize];
        for (int i = 0; i < xsize; i++) {
            double nstart = start.getDouble(startIndex++);
            if (startIndex == startSize) {
                startIndex = 0;
            }
            double nstop = stop.getDouble(stopIndex++);
            if (stopIndex == stopSize) {
                stopIndex = 0;
            }
            String str = x.getString(i);
            if (!RDouble.RDoubleUtils.isNAorNaN(nstart) && !RDouble.RDoubleUtils.isNAorNaN(nstop) && str != RString.NA) {
                int stp = (int) nstop;
                int len = str.length();
                if (stp > len) {
                    stp = len;
                }
                content[i] = str.substring(((int) nstart) - 1, stp);
            } else {
                content[i] = RString.NA;
            }
        }
        return RString.RStringFactory.getFor(content);
    }

    public static RString substring(RString text, RDouble first, RDouble last, RContext context, ASTNode ast) {
        int textSize = text.size();
        int firstSize = first.size();
        int lastSize = last.size();
        int textIndex = 0;
        int firstIndex = 0;
        int lastIndex = 0;

        if (textSize == 0) {
            return RString.EMPTY;
        }
        if (firstSize == 0) {
            throw RError.getInvalidArgument(ast, "first"); // not exactly R-warning
        }
        if (lastSize == 0) {
            throw RError.getInvalidArgument(ast, "last"); // not exactly R-warning
        }

        int n = Math.max(textSize, Math.max(firstSize, lastSize));

        String[] content = new String[n];
        for (int i = 0; i < n; i++) {
            double nfirst = first.getDouble(firstIndex++);
            if (firstIndex == firstSize) {
                firstIndex = 0;
            }
            double nlast = last.getDouble(lastIndex++);
            if (lastIndex == lastSize) {
                lastIndex = 0;
            }
            String str = text.getString(textIndex++);
            if (textIndex == textSize) {
                textIndex = 0;
            }
            if (!RDouble.RDoubleUtils.isNAorNaN(nfirst) && !RDouble.RDoubleUtils.isNAorNaN(nlast) && str != RString.NA) {
                int stp = (int) nlast;
                int len = str.length();
                if (stp > len) {
                    stp = len;
                }
                content[i] = str.substring(((int) nfirst) - 1, stp);
            } else {
                content[i] = RString.NA;
            }
        }
        return RString.RStringFactory.getFor(content);
    }
}
