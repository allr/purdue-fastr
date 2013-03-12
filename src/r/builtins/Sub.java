package r.builtins;

import java.util.regex.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "sub"
 * 
 * <pre>
 * ppattern -- character string containing a regular expression (or character string for fixed = TRUE) to be matched in
 *          the given character vector. Coerced by as.character to a character string if possible. If a character vector
 *          of length 2 or more is supplied, the first element is used with a warning. Missing values are allowed except for regexpr and gregexpr.
 * x -- a character vector where matches are sought, or an object which can be coerced by as.character to a character vector.
 * ignore.case -- if FALSE, the pattern matching is case sensitive and if TRUE, case is ignored during matching.
 * perl -- logical. Should perl-compatible regexps be used?
 * fixed -- logical. If TRUE, pattern is a string to be matched as is. Overrides all conflicting arguments.
 * useBytes -- logical. If TRUE the matching is done byte-by-byte rather than character-by-character.
 * </pre>
 */
// FIXME: this does not really implement the R's regular expressions semantics (not even mentioning that there are 2 semantics supported
//        by R, one perl and one non-perl
class Sub extends CallFactory {
    static final CallFactory _ = new Sub("sub", new String[]{"pattern", "replacement", "x", "ignore.case", "perl", "fixed", "useBytes"}, new String[]{"x", "pattern", "replacement"}, false);

    Sub(String name, String[] params, String[] required, boolean global) {
        super(name, params, required);
        this.global = global;
    }

    boolean global;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posPattern = ia.position("pattern");
        final int posReplacement = ia.position("replacement");
        final int posX = ia.position("x");
        final int posIgnoreCase = ia.position("ignore.case");
        final int posPerl = ia.position("perl");
        final int posFixed = ia.position("fixed");
        final int posUseBytes = ia.position("useBytes");
        return new BuiltIn(call, names, exprs) {
            @Override public final RAny doBuiltIn(Frame frame, RAny[] args) {
                if (posUseBytes != -1) {
                    RContext.warning(ast, "Ignoring useBytes.");
                }
                String pattern = parseScalarString(ast, args[posPattern], "pattern");
                String replacement = parseScalarString(ast, args[posReplacement], "replacement");
                RString x = Convert.coerceToStringError(args[posX], ast);
                boolean ignoreCase = posIgnoreCase != -1 ? Convert.checkFirstLogical(args[posIgnoreCase], RLogical.TRUE) : false;
                boolean perl = posPerl != -1 ? Convert.checkFirstLogical(args[posPerl], RLogical.TRUE) : false;
                boolean fixed = posFixed != -1 ? Convert.checkFirstLogical(args[posFixed], RLogical.TRUE) : false;
                return sub(ast, pattern, replacement, x, ignoreCase, perl, fixed, global);
            }
        };
    }

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
}
