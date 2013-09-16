package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

// TODO: implement this builtin at least to support custom options
//       shooutout benchmarks set the "digits" option
final class Options extends CallFactory {
    static final CallFactory _ = new Options("options", new String[]{"..."}, null);

    private Options(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return RNull.getNull();
            }
        };
    }
}
