package r.builtins;

import java.util.regex.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// FIXME: does not fill in attributes yet (TODO this when custom attributes are supported)
// FIXME: like SubStr and Sub, ignores "perl" and "useBytes", regexps are not quite like in R
public class RegExpr {
    private static final String[] paramNames = new String[]{"pattern", "text", "ignore.case", "perl", "fixed", "useBytes"};

    private static final int IPATTERN = 0;
    private static final int ITEXT = 1;
    private static final int IIGNORE_CASE = 2;
    private static final int IPERL = 3;
    private static final int IFIXED = 4;
    private static final int IUSE_BYTES = 5;

    public static RInt regexpr(Pattern p, RString text) {
        int size = text.size();
        int[] content = new int[size];
        for (int i = 0; i < size; i++) {
            String s = text.getString(i);
            if (s != RString.NA) {
                Matcher m = p.matcher(s);
                if (m.find()) {
                    content[i] = m.start() + 1;
                    // FIXME: add length attribute
                } else {
                    content[i] = -1;
                }
            } else {
                content[i] = RInt.NA;
            }
        }
        return RInt.RIntFactory.getFor(content); // drops dimensions
    }

    public static RList gregexpr(Pattern p, RString text) {
        int size = text.size();
        RAny[] content = new RAny[size];
        int[] buf = new int[size]; // FIXME: would be cool and have a growable Vector/ArrayList of primitives
        int bufUsed = 0;
        for (int i = 0; i < size; i++) {
            String s = text.getString(i);
            if (s != RString.NA) {
                Matcher m = p.matcher(s);
                bufUsed = 0;
                while (m.find()) {
                    if (bufUsed == buf.length) {
                        int[] oldbuf = buf;
                        buf = new int[bufUsed * 2];
                        System.arraycopy(oldbuf, 0, buf, 0, bufUsed);
                    }
                    buf[bufUsed++] = m.start() + 1;
                    // FIXME: add length attribute
                }
                if (bufUsed > 0) {
                    int[] econtent = new int[bufUsed];
                    System.arraycopy(buf, 0, econtent, 0, bufUsed);
                    content[i] = RInt.RIntFactory.getFor(econtent);
                } else {
                    content[i] = RInt.RIntFactory.getScalar(-1);
                }
            } else {
                content[i] = RInt.BOXED_NA;
            }
        }
        return RList.RListFactory.getFor(content); // drops dimensions
    }

    public static class RegExprCallFactory extends CallFactory {

        boolean global;

        RegExprCallFactory(boolean global) {
            this.global = global;
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IPATTERN]) {
                BuiltIn.missingArg(call, paramNames[IPATTERN]);
            }
            if (!provided[ITEXT]) {
                BuiltIn.missingArg(call, paramNames[ITEXT]);
            }

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] args) {
                    if (provided[IUSE_BYTES]) {
                        RContext.warning(ast, "Ignoring useBytes.");
                    }
                    String pattern = Sub.parseScalarString(ast, args[paramPositions[IPATTERN]], paramNames[IPATTERN]);
                    RString text = Convert.coerceToStringError(args[paramPositions[ITEXT]], ast);
                    boolean ignoreCase = provided[IIGNORE_CASE] ? Convert.checkFirstLogical(args[paramPositions[IIGNORE_CASE]], RLogical.TRUE) : false;
                    boolean perl = provided[IPERL] ? Convert.checkFirstLogical(args[paramPositions[IPERL]], RLogical.TRUE) : false;
                    boolean fixed = provided[IFIXED] ? Convert.checkFirstLogical(args[paramPositions[IFIXED]], RLogical.TRUE) : false;

                    if (pattern == RString.NA) {
                        throw RError.getInvalidArgument(ast, paramNames[IPATTERN]);
                    }
                    if (!perl) {
                        RContext.warning(ast, "Using a Perl-like regular expression syntax (non-Perl not implemented yet).");
                    }
                    Pattern p = Pattern.compile(pattern, (ignoreCase ? Pattern.CASE_INSENSITIVE : 0) | (fixed ? Pattern.LITERAL : 0));

                    if (global) {
                        return gregexpr(p, text);
                    } else {
                        return regexpr(p, text);
                    }
                }
            };
        }
    }

    public static final CallFactory GREGEXPR_FACTORY = new RegExprCallFactory(true);
    public static final CallFactory REGEXPR_FACTORY = new RegExprCallFactory(false);

}
