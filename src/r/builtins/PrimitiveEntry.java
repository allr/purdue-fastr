package r.builtins;

import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

public final class PrimitiveEntry {

    static final int PREFIX = 0;
    static final int INFIX = 1;

    final int minArgs;
    final int maxArgs;
    final int prettyPrint;
    final RSymbol name;
    final CallFactory factory;
    final RBuiltIn builtIn;

    PrimitiveEntry(RSymbol name, int minArgs, int maxArgs, CallFactory bodyFactory, int prettyPrint) {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.factory = bodyFactory;
        this.builtIn = new BuiltInImpl(factory);
        this.prettyPrint = prettyPrint;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    @Override public String toString() {
        return "PrimitiveEntry[" + name + "," + minArgs + "/" + maxArgs + "," + factory + "," + builtIn;
    }

}
