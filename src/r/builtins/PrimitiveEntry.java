package r.builtins;

import r.data.*;

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
}
