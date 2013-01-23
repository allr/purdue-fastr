package r.builtins;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
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
        this.factory = checkNumberOfArgs(name, minArgs, maxArgs, bodyFactory);
        this.builtIn = new BuiltInImpl(factory);
        this.prettyPrint = prettyPrint;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public static CallFactory checkNumberOfArgs(final RSymbol name, final int minArgs, final int maxArgs, final CallFactory bodyFactory) {
        return new CallFactory() {

            @Override
            public RSymbol name() {
                return name;
            }

            @Override
            public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
                if (minArgs != -1 && exprs.length < minArgs || maxArgs != -1 && exprs.length > maxArgs) {
                    throw RError.getGenericError(call, "Wrong number of arguments for call to BuiltIn (" + PrettyPrinter.prettyPrint(call) + ")");
                }

                return bodyFactory.create(call, names, exprs);
            }
        };
    }
}
