package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "rep.int"
 * 
 * <pre>
 * x  -- a vector (of any mode including a list) or a pairlist or a factor or (except for rep.int) a POSIXct or POSIXlt or 
 *        date object; or also, an S4 object containing a vector of the above kind.
 * times -- A integer vector giving the (non-negative) number of times to repeat each element if of length length(x), or to 
 *                repeat the whole vector if of length 1. Negative or NA values are an error.
 * </pre>
 */
class RepInt extends Rep {

    static final CallFactory _ = new RepInt("rep.int", new String[]{"x", "times"}, new String[]{"x", "times"});

    RepInt(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final boolean xfirst = ia.position("x") == 0;
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                return genericRepInt(ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
            }
        };
    }
}
