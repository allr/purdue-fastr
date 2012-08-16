package r.data;

import java.util.*;

import r.data.internal.*;

public final class RSymbol extends BaseObject implements RAny {
    final String name;
    private static final SymbolTable symbolTable = new SymbolTable();

    private RSymbol(String id) {
        name = id;
    }

    public static RSymbol getSymbol(String name) {
        return symbolTable.get(name);
    }

    @Override
    public String pretty() {
        // TODO put `` when needed
        return name;
    }

    private static class SymbolTable {
        // TODO A less stupid implementation for symbol table
        // i.e., close to a set implementation with linear probing
        final Map<String, RSymbol> table = new HashMap<>();

        private RSymbol get(String name) {
            RSymbol sym = table.get(name);
            if (sym == null) {
                table.put(name, sym = new RSymbol(name));
            }
            return sym;
        }
    }
}
