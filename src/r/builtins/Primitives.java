package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.nodes.FunctionCall;
import r.nodes.truffle.*;

public class Primitives {

    private static Map<RSymbol, PrimitiveEntry> map;
    static {
        map = new HashMap<>();
        add(":", 2, new Sequence.SequenceFactory());
    }

    public static RNode getNode(FunctionCall call, RFunction enclosing, RSymbol[] names, RNode[] exprs) {
        RSymbol name = call.getName();
        final PrimitiveEntry pe = Primitives.get(name, enclosing);
        if (pe == null) {
            return null;
        }
        return pe.create(call, names, exprs);
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
