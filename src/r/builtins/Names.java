package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * "names"
 * 
 * <pre>
 * x -- an R object.
 * value -- a character vector of up to the same length as x, or NULL.
 * </pre>
 */
// FIXME: Truffle can't inline BuiltIn.BuiltIn1, so using BuiltIn
final class Names extends CallFactory {
    static final CallFactory _ = new Names("names", new String[]{"xn"}, new String[]{"x"});

    private Names(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new BuiltIn.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                return getNames(arg);
            }
        };
    }

    public static RAny getNames(RAny arg) {
        if (arg instanceof RArray) {
            RArray.Names sNames = ((RArray) arg).names();
            if (sNames != null) { return RString.RStringFactory.getFor(sNames.asStringArray()); }
            return RNull.getNull();
        }
        Utils.nyi("unsupported argument");
        return null;
    }
}
