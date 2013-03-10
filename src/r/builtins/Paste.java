package r.builtins;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "paste"
 * 
 * <pre>
 * ... -- one or more R objects, to be converted to character vectors.
 * sep -- a character string to separate the terms. Not NA_character_.
 * collapse -- an optional character string to separate the results. Not NA_character_.
 * </pre>
 */
// FIXME: this is an unoptimized version
final class Paste extends CallFactory {
    static final CallFactory _ = new Paste("paste", new String[]{"...", "sep", "collapse"}, new String[]{});

    private Paste(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 0) { return new BuiltIn.BuiltIn0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return RString.EMPTY;
            }
        }; }
        ArgumentInfo ia = check(call, names, exprs);

        final int sepPosition = ia.provided("sep") ? ia.position("sep") : -1;
        final int collapsePosition = ia.provided("collapse") ? ia.position("collapse") : -1;
        int args = exprs.length;
        if (sepPosition != -1) {
            args--;
        }
        if (collapsePosition != -1) {
            args--;
        }
        final int realArgs = args;
        return new BuiltIn(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                return paste(params, realArgs, sepPosition, collapsePosition, ast);
            }
        };
    }

    public static String parseSeparator(ASTNode ast, RAny arg) {
        if (arg instanceof RString) {
            RString s = (RString) arg;
            if (s.size() > 0) {
                String str = s.getString(0);
                if (str != RString.NA) { return str; }
            }
        }
        throw RError.getInvalidSeparator(ast);
    }

    public static String parseCollapse(ASTNode ast, RAny arg) {
        if (arg instanceof RNull) { return null; }
        if (!(arg instanceof RString)) { throw RError.getInvalidArgument(ast, "collapse"); }
        RString s = (RString) arg;
        if (s.size() > 0) {
            String str = s.getString(0);
            if (str != RString.NA) { return str; }
        }
        throw RError.getInvalidArgument(ast, "collapse");
    }

    public static RString paste(RAny[] args, int realArgs, int sepPosition, int collapsePosition, ASTNode ast) {

        String separator = null;
        if (sepPosition == -1) {
            separator = " ";
        } else {
            separator = parseSeparator(ast, args[sepPosition]);
        }

        String collapse = null;
        if (collapsePosition != -1) {
            collapse = parseCollapse(ast, args[collapsePosition]);
        }

        RString[] stringArgs = new RString[realArgs];
        int j = 0;
        int maxLength = 0;
        for (int i = 0; i < args.length; i++) {
            if (i == sepPosition || i == collapsePosition) {
                continue;
            }
            RString s = AsBase.genericAsString(ast, args[i]); // FIXME: can we remove R-level boxing?
            stringArgs[j++] = s;
            int ssize = s.size();
            if (ssize > maxLength) {
                maxLength = ssize;
            }
        }

        if (collapse == null) {
            String[] content = new String[maxLength];
            for (int i = 0; i < maxLength; i++) {
                StringBuilder str = new StringBuilder();
                for (j = 0; j < realArgs; j++) {
                    if (j > 0) {
                        str.append(separator);
                    }
                    RString s = stringArgs[j];
                    str.append(s.getString(i % s.size()));
                }
                content[i] = str.toString();
            }
            return RString.RStringFactory.getFor(content);
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < maxLength; i++) {
                if (i > 0) {
                    str.append(collapse);
                }
                for (j = 0; j < realArgs; j++) {
                    if (j > 0) {
                        str.append(separator);
                    }
                    RString s = stringArgs[j];
                    str.append(s.getString(i % s.size()));
                }
            }
            return RString.RStringFactory.getScalar(str.toString());
        }
    }

}
