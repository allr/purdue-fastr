package r.nodes;

import java.util.*;

import r.data.*;
import r.data.internal.*;


public final class Symbol extends BaseObject implements RAny {
    final String name;
    static final Map<String, Symbol> symbolTable = new IdentityHashMap<>();

    private Symbol(String id) {
        name = id;
    }

    public static Symbol getSymbol(String name) {
        Symbol id = symbolTable.get(name);
        if (id == null) {
            symbolTable.put(name, id = new Symbol(name));
        }
        return id;
    }

    @Override
    public String pretty() {
        // TODO put `` when needed
        return name;
    }
}
