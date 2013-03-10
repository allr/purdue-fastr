package r.builtins;

import java.util.*;
import java.util.regex.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "strsplit"
 * 
 * <pre>
 * x -- character vector, each element of which is to be split. Other inputs, including a factor, will give an error.
 * split -- character vector (or object which can be coerced to such) containing regular expression(s) (unless fixed = TRUE)
 *           to use for splitting. If empty matches occur, in particular if split has length 0, x is split into single 
 *           characters. If split has length greater than 1, it is re-cycled along x.
 * fixed -- logical. If TRUE match split exactly, otherwise use regular expressions. Has priority over perl.
 * perl -- logical. Should perl-compatible regexps be used?
 * useBytes -- logical. If TRUE the matching is done byte-by-byte rather than character-by-character, and inputs with marked 
 *         encodings are not converted. This is forced (with a warning) if any input is found which is marked as "bytes".
 * </pre>
 */
// FIXME: this implementation is very slow and is only partial
// the supported regular expressions may not be exactly like in GNU-R
final class StrSplit extends CallFactory {
    static final CallFactory _ = new StrSplit("strsplit", new String[]{"x", "split", "fixed", "perl", "useBytes"}, new String[]{"x"});

    private StrSplit(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        final ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("useBytes")) { throw Utils.nyi(); }

        return new BuiltIn(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RString x = Convert.coerceToStringError(args[ia.position("x")], ast);
                RString split = Convert.coerceToStringError(args[ia.position("split")], ast);
                boolean fixed = ia.provided("fixed") ? Convert.checkFirstLogical(args[ia.position("fixed")], RLogical.TRUE) : false;
                boolean perl = ia.provided("perl") ? Convert.checkFirstLogical(args[ia.position("perl")], RLogical.TRUE) : false;

                return strsplit(ast, x, split, fixed, perl);
            }
        };
    }

    // FIXME: could get rid of ArrayList (also fixed matching below)
    // FIXME: could cache compiled regular expression evaluators, and particularly so when the regular expression is a constant
    public static RAny strSplitRE(RString x, RString split) {
        int splitIndex = 0;
        int splitSize = split.size();
        Pattern[] patterns = new Pattern[splitSize];
        int xsize = x.size();
        RAny[] content = new RAny[xsize];
        ArrayList<String> buf = new ArrayList<String>();
        for (int i = 0; i < xsize; i++) {
            Pattern p = patterns[splitIndex];
            if (p == null) {
                String separator = split.getString(splitIndex);
                p = Pattern.compile(separator);
                patterns[splitIndex] = p;
                splitIndex++;
                if (splitIndex == splitSize) {
                    splitIndex = 0;
                }
            }
            String str = x.getString(i);
            int strLen = str.length();
            Matcher m = p.matcher(str);
            buf.clear();
            int j = 0;
            while (j < strLen) {
                if (m.find()) {
                    if (m.end() > 0) {
                        buf.add(str.substring(j, m.start()));
                    }
                    j = m.end();
                } else {
                    if (j < strLen) {
                        buf.add(str.substring(j));
                    }
                    break;
                }
            }
            String[] econtent = new String[buf.size()];
            content[i] = RString.RStringFactory.getFor(buf.toArray(econtent));
        }
        return RList.RListFactory.getFor(content);
    }

    // FIXME: wouldn't it be just faster & simpler to use Java's regexes with Pattern.LITERAL flag?

    // FIXME: this could be optimized by getting rid of ArrayList (R does two passes, one to count number of occurrences, then allocates, then another)
    //        we could speculate that elements of x will have always the same number of matches (e.g. lines of input in fixed format)
    // FIXME: this could also be optimized by using a better algorithm to search text, e.g. KMG
    //        who knows if using Java's regex wouldn't be faster than this implementation
    public static RAny strSplitFixed(RString x, RString split) {
        int xsize = x.size();
        RAny[] content = new RAny[xsize];
        int splitIndex = 0;
        int splitSize = split.size();
        ArrayList<String> buf = new ArrayList<String>();
        for (int i = 0; i < xsize; i++) {
            String separator = split.getString(splitIndex++);
            int separatorLength = separator.length();
            if (splitIndex == splitSize) {
                splitIndex = 0;
            }
            String str = x.getString(i);
            if (separatorLength > 0) {
                int strLen = str.length();
                int j = 0;
                buf.clear();
                while (j < strLen) {
                    int separatorStart = str.indexOf(separator, j);
                    if (separatorStart != -1) {
                        buf.add(str.substring(j, separatorStart));
                        j = separatorStart + separatorLength;
                    } else {
                        if (j < strLen) {
                            buf.add(str.substring(j));
                        }
                        break;
                    }
                }
                String[] econtent = new String[buf.size()];
                content[i] = RString.RStringFactory.getFor(buf.toArray(econtent));
            } else {
                content[i] = RString.RStringFactory.getFor(stringToChars(str));
            }

        }
        return RList.RListFactory.getFor(content);
    }

    public static String[] stringToChars(String str) {
        int strLen = str.length();
        String[] chars = new String[strLen];
        for (int j = 0; j < strLen; j++) {
            chars[j] = String.valueOf(str.charAt(j)); // ?? would substring be faster?
        }
        return chars;
    }

    public static RAny strSplitChars(RString x) {
        int xsize = x.size();
        RAny[] content = new RAny[xsize];
        for (int i = 0; i < xsize; i++) {
            content[i] = RString.RStringFactory.getFor(stringToChars(x.getString(i)));
        }
        return RList.RListFactory.getFor(content);
    }

    public static RAny strsplit(ASTNode ast, RString x, RString split, boolean fixed, boolean perl) {
        int splitSize = split.size();
        if (splitSize == 0) { return strSplitChars(x); }
        if (splitSize == 1) {
            if (split.getString(0).length() == 0) { return strSplitChars(x); }
        }
        if (!fixed) {
            if (!perl) {
                RContext.warning(ast, "Using a Perl-like regular expression syntax (non-Perl not implemented yet).");
            }
            return strSplitRE(x, split);
        } else {
            return strSplitFixed(x, split);
        }
    }
}
