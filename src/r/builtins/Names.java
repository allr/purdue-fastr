package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "names"
 *
 * <pre>
 * x -- an R object.
 * </pre>
 */
final class Names extends CallFactory {
    static final CallFactory _ = new Names("names", new String[]{"x"}, new String[]{"x"});

    private Names(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
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
        throw Utils.nyi("unsupported argument");
    }
}
