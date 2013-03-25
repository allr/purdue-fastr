package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * "length"
 *
 * <pre>
 * x -- an R object. For replacement, a vector or factor.
 * </pre>
 */

final class Length extends CallFactory {

    static final CallFactory _ = new Length("length", new String[]{"x"}, new String[]{"x"});

    private Length(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RArray) { return RInt.RIntFactory.getScalar(((RArray) arg).size()); }
                throw Utils.nyi("unsupported argument");
            }
        };
    }

}
