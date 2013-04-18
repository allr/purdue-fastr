package r.builtins;

import java.util.regex.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "regexpr"
 * 
 * <pre>
 * pattern -- character string containing a regular expression (or character string for fixed = TRUE) to 
 *           be matched in the given character vector. Coerced by as.character to a character string if possible.
 *           If a character vector of length 2 or more is supplied, the first element is used with a warning. 
 *           Missing values are allowed except for regexpr and gregexpr.
 * x, text -- a character vector where matches are sought, or an object which can be coerced by as.character to a 
 *           character vector.
 * ignore.case -- if FALSE, the pattern matching is case sensitive and if TRUE, case is ignored during matching.
 * perl -- logical. Should perl-compatible regexps be used?
 * fixed -- logical. If TRUE, pattern is a string to be matched as is. Overrides all conflicting arguments.
 * useBytes -- logical. If TRUE the matching is done byte-by-byte rather than character-by-character.
 * </pre>
 */
// FIXME: does not fill in attributes yet (TODO this when custom attributes are supported)
// FIXME: like SubStr and Sub, ignores "perl" and "useBytes", regexps are not quite like in R
class Regexpr extends CallFactory {
    static final CallFactory _ = new Regexpr("regexpr", new String[]{"pattern", "text", "ignore.case", "perl", "fixed", "useBytes"}, new String[]{"pattern", "text"}, false);

    Regexpr(String name, String[] params, String[] required, boolean global) {
        super(name, params, required);
        this.global = global;
    }

    boolean global;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posUseBytes = ia.position("useBytes");
        final int posPattern = ia.position("pattern");
        final int posText = ia.position("text");
        final int posPerl = ia.position("perl");
        final int posFixed = ia.position("fixed");
        final int posIgnoreCase = ia.position("ignore.case");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (posUseBytes != -1) {
                    RContext.warning(ast, "Ignoring useBytes.");
                }
                String pattern = Sub.parseScalarString(ast, args[posPattern], "pattern");
                RString text = Convert.coerceToStringError(args[posText], ast);
                boolean ignoreCase = posIgnoreCase != -1 ? Convert.checkFirstLogical(args[posIgnoreCase], RLogical.TRUE) : false;
                boolean perl = posPerl != -1 ? Convert.checkFirstLogical(args[posPerl], RLogical.TRUE) : false;
                boolean fixed = posFixed != -1 ? Convert.checkFirstLogical(args[posFixed], RLogical.TRUE) : false;
                if (pattern == RString.NA) { throw RError.getInvalidArgument(ast, "pattern"); }
                if (!perl) {
                    RContext.warning(ast, "Using a Perl-like regular expression syntax (non-Perl not implemented yet).");
                }
                Pattern p = Pattern.compile(pattern, (ignoreCase ? Pattern.CASE_INSENSITIVE : 0) | (fixed ? Pattern.LITERAL : 0));
                return global ? gregexpr(p, text) : regexpr(p, text);
            }
        };
    }

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
}
