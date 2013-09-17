package r.builtins;

import r.Convert.ConversionStatus;
import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "substr"
 * 
 * <pre>
 * text --a character vector.
 * first-- integer. The first element to be replaced.
 * last -- integer. The last element to be replaced.
 * </pre>
 */
class Substr extends CallFactory {

    static final CallFactory _ = new Substr("substr", new String[]{"x", "start", "stop"}, new String[]{"x", "start", "stop"});

    Substr(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final ConversionStatus warn = new ConversionStatus(); // WARNING: calls not re-entrant

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posX = ia.position("x");
        final int posStart = ia.position("start");
        final int posStop = ia.position("stop");
        return new Builtin(call, names, exprs) {
            @Override public final RAny doBuiltIn(Frame frame, RAny[] args) {
                RString x = args[posX].asString();
                warn.naIntroduced = false;
                RDouble start = args[posStart].asDouble(warn);
                RDouble stop = args[posStop].asDouble(warn);
                RString res = substr(x, start, stop, ast);
                if (warn.naIntroduced) {
                    RContext.warning(ast, RError.NA_INTRODUCED_COERCION);
                }
                return res;
            }
        };

    }

    public static RString substr(RString x, RDouble start, RDouble stop, ASTNode ast) {
        int xsize = x.size();
        int startSize = start.size();
        int stopSize = stop.size();
        int startIndex = 0;
        int stopIndex = 0;
        if (xsize == 0) { return RString.EMPTY; }
        if (startSize == 0) { throw RError.getInvalidArgument(ast, "start"); }// not exactly R-warning       
        if (stopSize == 0) { throw RError.getInvalidArgument(ast, "stop"); }// not exactly R-warning       
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
}
