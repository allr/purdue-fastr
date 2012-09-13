package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.FunctionCall;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class Primitives {

    private static Map<RSymbol, PrimitiveEntry> map;
    static {
        map = new HashMap<>();
        add(":", 2, Sequence.FACTORY);
        add("c", -1, Combine.FACTORY);
    }

    public static CallFactory getCallFactory(final FunctionCall call, final RFunction enclosing) {
        RSymbol name = call.getName();
        final PrimitiveEntry pe = Primitives.get(name, enclosing);
        if (pe == null) {
            return null;
        }
        return new CallFactory() {

            @Override
            public RNode create(FunctionCall fcall, RSymbol[] names, RNode[] exprs) {
                int count = pe.getArgsCount();

                if (!(count >= 0 ? count == exprs.length : exprs.length >= (-count - 1))) {
                    throw RError.getGenericError(fcall, "Wrong number of arguments for call to BuiltIn (" + PrettyPrinter.prettyPrint(call) + ")");
                }

                return pe.factory.create(fcall, names, exprs);
            }
        };
    }

    public static PrimitiveEntry get(RSymbol name, RFunction fun) {
        PrimitiveEntry pe = get(name);
        if (pe != null && fun != null && fun.isInWriteSet(name)) {
            Utils.nyi(); // TODO case were a primitive is shadowed by a local symbol
        }
        return pe;
    }

    public static PrimitiveEntry get(RSymbol name) {
        return map.get(name);
    }

    private static void add(String name, int nbArgs, CallFactory body) {
        add(name, nbArgs, body, PrimitiveEntry.PREFIX);
    }

    private static void add(String name, int nbArgs, CallFactory body, int pp) {
        RSymbol sym = RSymbol.getSymbol(name);
        assert Utils.check(!map.containsKey(sym));
        map.put(sym, new PrimitiveEntry(sym, nbArgs, body, pp));
    }
}
