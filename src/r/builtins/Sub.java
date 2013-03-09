package r.builtins;

import java.util.regex.*;

import r.*;
import r.builtins.BuiltIn.AnalyzedArguments;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// FIXME: this does not really implement the R's regular expressions semantics (not even mentioning that there are 2 semantics supported
//        by R, one perl and one non-perl
public class Sub {
    private static final String[] paramNames = new String[]{"pattern", "replacement", "x", "ignore.case", "perl", "fixed", "useBytes"};

    private static final int IPATTERN = 0;
    private static final int IREPLACEMENT = 1;
    private static final int IX = 2;
    private static final int IIGNORE_CASE = 3;
    private static final int IPERL = 4;
    private static final int IFIXED = 5;
    private static final int IUSE_BYTES = 6;

    public static String parseScalarString(ASTNode ast, RAny value, String argName) {
        RString rstring = Convert.coerceToStringError(value, ast);
        int size = rstring.size();
        if (size == 1) { return rstring.getString(0); }
        if (size > 1) {
            RContext.warning(ast, String.format(RError.ARGUMENT_ONLY_FIRST, argName));
        }
        throw RError.getInvalidArgument(ast, argName);
    }

    public static RString sub(ASTNode ast, String pattern, String replacement, RString x, boolean ignoreCase, boolean perl, boolean fixed, boolean global) {
        if (pattern == RString.NA || replacement == RString.NA) { return RString.RStringFactory.getNAArray(x.size()); }
        if (!perl) {
            RContext.warning(ast, "Using a Perl-like regular expression syntax (non-Perl not implemented yet).");
        }
        if (!fixed) {
            return subRE(pattern, replacement, x, ignoreCase, global);
        } else {
            return subFixed(ast, pattern, replacement, x, ignoreCase, global);
        }
    }

    public static String convertReplacement(String replacementArg) { // FIXME: handle octal constants correctly when no groups exist
        return replacementArg.replaceAll("\\\\([1-9])", "\\$$1");
    }

    public static RString subRE(String pattern, String replacementArg, RString x, boolean ignoreCase, boolean global) {
        Pattern p = Pattern.compile(pattern, ignoreCase ? Pattern.CASE_INSENSITIVE : 0); // FIXME: can add UNICODE_CASE
        int size = x.size();
        String[] content = new String[size];
        String replacement = convertReplacement(replacementArg);
        for (int i = 0; i < size; i++) {
            String s = x.getString(i);
            if (s != RString.NA) {
                Matcher m = p.matcher(s);
                if (global) {
                    content[i] = m.replaceAll(replacement);
                } else {
                    content[i] = m.replaceFirst(replacement);
                }
            } else {
                content[i] = RString.NA;
            }
        }
        return RString.RStringFactory.getFor(content, x.dimensions(), x.names());
    }

    // FIXME: wouldn't it be just faster & simpler to use Java's regexes with Pattern.LITERAL flag?
    // FIXME: this could use a better algorithm (same case is strsplit)
    public static RString subFixed(ASTNode ast, String patternArg, String replacement, RString xArg, boolean ignoreCase, boolean global) {
        int size = xArg.size();
        String[] content = new String[size];
        if (patternArg.length() == 0) { throw RError.getZeroLengthPattern(ast); }
        String pattern = !ignoreCase ? patternArg : patternArg.toLowerCase();
        int pLen = pattern.length();

        for (int i = 0; i < size; i++) {
            String xstr = xArg.getString(i);
            if (xstr == RString.NA) {
                content[i] = RString.NA;
                continue;
            }
            String x = !ignoreCase ? xstr : xstr.toLowerCase();
            int pStart = x.indexOf(pattern);
            if (pStart != -1) {
                String init = xstr.substring(0, pStart);
                StringBuilder b = new StringBuilder(init);
                b.append(replacement);
                int xLen = xstr.length();
                int searchStart = pStart + pLen;
                if (global) {
                    for (;;) {
                        pStart = x.indexOf(pattern, searchStart);
                        if (pStart != -1) {
                            b.append(xstr.substring(searchStart, pStart));
                            b.append(replacement);
                            searchStart = pStart + pLen;
                        } else {
                            break;
                        }
                    }
                }
                b.append(xstr.substring(searchStart, xLen));
                content[i] = b.toString();
            } else {
                content[i] = xstr;
            }
        }
        return RString.RStringFactory.getFor(content, xArg.dimensions(), xArg.names());
    }

    public static class SubCallFactory extends CallFactory {

        boolean global;

        SubCallFactory(boolean global) {
            this.global = global;
        }

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            ArgumentInfo a = BuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IPATTERN]) {
                BuiltIn.missingArg(call, paramNames[IPATTERN]);
            }
            if (!provided[IREPLACEMENT]) {
                BuiltIn.missingArg(call, paramNames[IREPLACEMENT]);
            }
            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }

            return new BuiltIn(call, names, exprs) {

                @Override public final RAny doBuiltIn(Frame frame, RAny[] args) {
                    if (provided[IUSE_BYTES]) {
                        RContext.warning(ast, "Ignoring useBytes.");
                    }
                    String pattern = parseScalarString(ast, args[paramPositions[IPATTERN]], paramNames[IPATTERN]);
                    String replacement = parseScalarString(ast, args[paramPositions[IREPLACEMENT]], paramNames[IREPLACEMENT]);
                    RString x = Convert.coerceToStringError(args[paramPositions[IX]], ast);
                    boolean ignoreCase = provided[IIGNORE_CASE] ? Convert.checkFirstLogical(args[paramPositions[IIGNORE_CASE]], RLogical.TRUE) : false;
                    boolean perl = provided[IPERL] ? Convert.checkFirstLogical(args[paramPositions[IPERL]], RLogical.TRUE) : false;
                    boolean fixed = provided[IFIXED] ? Convert.checkFirstLogical(args[paramPositions[IFIXED]], RLogical.TRUE) : false;

                    return sub(ast, pattern, replacement, x, ignoreCase, perl, fixed, global);
                }
            };
        }
    }

    public static final CallFactory GSUB_FACTORY = new SubCallFactory(true);
    public static final CallFactory SUB_FACTORY = new SubCallFactory(false);

}
