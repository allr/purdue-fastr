package r.builtins;

import r.Convert.ConversionStatus;
import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "substring"
 * 
 * <pre>
 * text --a character vector.
 * first-- integer. The first element to be replaced.
 * last -- integer. The last element to be replaced.
 * </pre>
 */
class Substring extends CallFactory {

    static final CallFactory _ = new Substring("substring", new String[]{"text", "first", "last"}, new String[]{"text", "first"});

    Substring(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final ConversionStatus warn = new ConversionStatus(); // WARNING: calls not re-entrant

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        final ArgumentInfo ia = check(call, names, exprs);
        final int posText = ia.position("text");
        final int posLast = ia.position("last");
        final int posFirst = ia.position("first");
        final RDouble defaultLast = RDouble.RDoubleFactory.getScalar(1000000); // FIXME slow, but perhaps the default is not used, anyway
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RString text = args[posText].asString();
                warn.naIntroduced = false;
                RDouble first = args[posFirst].asDouble(warn);
                RDouble last;
                last = posLast != -1 ? args[posLast].asDouble(warn) : defaultLast;

                RString res = substring(text, first, last, ast);
                if (warn.naIntroduced) {
                    RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
                }
                return res;
            }
        };
    }

    public static RString substring(RString text, RDouble first, RDouble last, ASTNode ast) {
        int textSize = text.size();
        int firstSize = first.size();
        int lastSize = last.size();
        int textIndex = 0;
        int firstIndex = 0;
        int lastIndex = 0;
        if (textSize == 0) { return RString.EMPTY; }
        if (firstSize == 0) { throw RError.getInvalidArgument(ast, "first"); }// not exactly R-warning        
        if (lastSize == 0) { throw RError.getInvalidArgument(ast, "last"); } // not exactly R-warning        

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
