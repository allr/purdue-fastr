package r.builtins;

import r.data.*;
import r.errors.*;
import r.nodes.FunctionCall;
import r.nodes.truffle.*;

public final class PrimitiveEntry {

    static final int PREFIX = 0;
    static final int INFIX = 1;

    final int argsCount;
    final int prettyPrint;
    final RSymbol name;
    final CallFactory factory;

    PrimitiveEntry(RSymbol name, int argsCount, CallFactory factory, int pp) {
        this.name = name;
        this.argsCount = argsCount;
        this.factory = factory;
        this.prettyPrint = pp;
    }

    public int getArgsCount() {
        return argsCount;
    }

    public RNode create(FunctionCall call, RSymbol[] names, RNode[] exprs) {
        int count = getArgsCount();

        if (!(count >= 0 && count == exprs.length) || !(count < 0 && (count - 1) < exprs.length)) {
            throw RError.getGenericError(call, "Wrong number of arguments for call to BuiltIn");
        }

        return factory.create(call, names, exprs);
    }
}
