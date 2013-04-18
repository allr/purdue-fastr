package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

final class Typeof extends CallFactory {

    static final CallFactory _ = new Typeof("typeof", new String[]{"x"}, new String[]{"x"});

    private Typeof(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny value) {
                return RString.RStringFactory.getScalar(value.typeOf());
            }
        };
    }
}
