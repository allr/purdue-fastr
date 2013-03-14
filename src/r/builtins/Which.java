package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "which"
 * 
 * <pre>
 * x  --  logical vector or array. NAs are allowed and omitted (treated as if FALSE).
 * arr.ind -- logical; should array indices be returned when x is an array?
 * useNames -- logical indicating if the value of arrayInd() should have (non-null) dimnames at all.
 * 
 * <pre>
 */
// FIXME: implements only part of R semantics
final class Which extends CallFactory {
    static final CallFactory _ = new Which("which", new String[]{"x", "arr.ind", "useNames"}, new String[]{"x"});

    Which(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    // a version with extra allocation (this is also how GNU R does it)
    // indeed could be also done with two passes but no extra allocation
    // FIXME: could get additional performance by providing multiple nodes and rewriting based on feedback on arguments
    public static RInt which(RLogical l) {
        RLogical input = l.materialize();
        int size = input.size();
        int[] tmp = new int[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (input.getLogical(i) == RLogical.TRUE) {
                tmp[j++] = i + 1;
            }
        }
        int nsize = j;
        int[] content = new int[nsize];
        System.arraycopy(tmp, 0, content, 0, nsize);
        RArray.Names inames = l.names();
        RArray.Names names;
        if (inames != null) {
            RSymbol[] symbols = new RSymbol[nsize];
            RSymbol[] isymbols = inames.sequence();
            j = 0;
            for (int i = 0; i < size; i++) {
                if (input.getLogical(i) == RLogical.TRUE) {
                    symbols[j++] = isymbols[i];
                }
            }
            names = RArray.Names.create(symbols);
        } else {
            names = null;
        }
        return RInt.RIntFactory.getFor(content, null, names); // drops dimensions, preserves names
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("arr.ind") || ia.provided("useNames")) { throw Utils.nyi("arguments not yet implemented"); }
        if (names.length == 1) { return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RLogical) { return which((RLogical) arg); }
                throw RError.getArgumentWhichNotLogical(ast);
            }
        }; }
        throw Utils.nyi();
    }
}
