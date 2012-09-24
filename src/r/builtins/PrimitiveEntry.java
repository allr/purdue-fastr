package r.builtins;

import r.data.*;

public final class PrimitiveEntry {

    static final int PREFIX = 0;
    static final int INFIX = 1;

    final int minArgs;
    final int maxArgs;
    final int prettyPrint;
    final RSymbol name;
    final CallFactory factory;

    PrimitiveEntry(RSymbol name, int minArgs, int maxArgs, CallFactory factory, int pp) {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.factory = factory;
        this.prettyPrint = pp;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }
}
