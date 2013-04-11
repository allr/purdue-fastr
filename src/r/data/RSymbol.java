package r.data;

import java.util.*;

import r.*;
import r.Convert.*;
import r.data.internal.*;

public final class RSymbol extends BaseObject implements RAny {

    private static final SymbolTable symbolTable = new SymbolTable(); // TODO put in Context ??!!
    public static final RSymbol[] EMPTY_SYMBOL_ARRAY = new RSymbol[0];

    /*
     * Special symbols All special symbols that are stored in the global symbol table are to be defined here.
     */
    // FIXME: we should find a better way, this reinsertion just for the tests, and it is very error prone, forgetting to add
    // a symbol here or to reinsert below leads to unpredictable results when running tests

    public static final RSymbol NA_SYMBOL = RSymbol.getSymbol(RString.NA);
    public static final RSymbol EMPTY_SYMBOL = RSymbol.getSymbol("");

    // from Truffleize
    public static final RSymbol DROP_SYMBOL = RSymbol.getSymbol("drop");
    public static final RSymbol EXACT_SYMBOL = RSymbol.getSymbol("exact");

    // from AbstractCall
    public static final RSymbol THREE_DOTS_SYMBOL = RSymbol.getSymbol("...");

    // from Attributes
    public static final RSymbol NAMES_SYMBOL = RSymbol.getSymbol("names");
    public static final RSymbol DIM_SYMBOL = RSymbol.getSymbol("dim");

    // from Rep
    public static final RSymbol TIMES_SYMBOL = RSymbol.getSymbol("times");

    protected static void reinsert(RSymbol s) {
        symbolTable.table.put(s.name(), s);
    }

    /**
     * Reinserts the special symbols to the table after the table has been reset clean.
     */
    protected static void reinsertSpecialSymbols() {
        reinsert(NA_SYMBOL);
        reinsert(EMPTY_SYMBOL);
        reinsert(DROP_SYMBOL);
        reinsert(EXACT_SYMBOL);
        reinsert(THREE_DOTS_SYMBOL);
        reinsert(NAMES_SYMBOL);
        reinsert(DIM_SYMBOL);
        reinsert(TIMES_SYMBOL);
    }

    final String name;
    // The next two fields are for the topLevel
    RAny value;
    int version;

    private RSymbol(String identifier) {
        name = identifier;
    }

    public static RSymbol getSymbol(String name) {
        return symbolTable.get(name);
    }

    public static RSymbol[] listSymbols() {
        return symbolTable.list();
    }

    public static RSymbol[] getSymbols(String[] names) {
        RSymbol[] symbols = new RSymbol[names.length];
        for (int i = 0; i < names.length; i++) {
            symbols[i] = RSymbol.getSymbol(names[i]);
        }
        return symbols;
    }

    public static RSymbol[] getSymbols(RString names) {
        int size = names.size();
        RSymbol[] symbols = new RSymbol[size];
        for (int i = 0; i < size; i++) {
            symbols[i] = RSymbol.getSymbol(names.getString(i));
        }
        return symbols;
    }

    @Override public String pretty() {
        // TODO put `` when needed
        return name;
    }

    public String name() {
        return name;
    }

    public int id() { // TODO add a field for global numbering and use it !
        return hashCode(); // id = currentId++;
    }

    public int hash() { // TODO add a field for filtering!
        return hashCode(); // hash = 1 << (currentHash = currentHash + 1 % Integer.size);
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

        private RSymbol[] list() {
            RSymbol[] res = new RSymbol[table.size()];
            return table.values().toArray(res);
        }
    }

    /**
     * Resets the symbol table. First clears the symbol table completely and then reinserts the special symbols.
     */
    public static void resetTable() {
        for (RSymbol s : symbolTable.table.values()) {
            s.value = null;
            s.version = 0;
        }
        //symbolTable.table.clear();
        //reinsertSpecialSymbols();
        //Primitives.initializePrimitives();
    }

    public static Set<String> symbols() {
        return symbolTable.table.keySet();
    }

    @Override public RRaw asRaw() {
        Utils.nyi();
        return null;
    }

    @Override public RLogical asLogical() {
        Utils.nyi();
        return null;
    }

    @Override public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override public RDouble asDouble() {
        Utils.nyi();
        return null;
    }

    @Override public RComplex asComplex() {
        Utils.nyi();
        return null;
    }

    @Override public RString asString() {
        Utils.nyi();
        return null;
    }

    @Override public RList asList() {
        Utils.nyi();
        return null;
    }

    public RAny getValue() {
        return value;
    }

    public void setValue(RAny val) {
        value = val;
    }

    public int getVersion() {
        return version;
    }

    void markDirty() {
        version++;
    }

    @Override public boolean isShared() {
        return false;
    }

    @Override public void ref() {}

    @Override public RSymbol stripAttributes() {
        return this;
    }

    @Override public RRaw asRaw(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RLogical asLogical(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RInt asInt(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RDouble asDouble(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RComplex asComplex(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RString asString(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public String typeOf() {
        throw Utils.nyi();
    }

    @Override public Attributes attributes() {
        return null;
    }

    @Override public Attributes attributesRef() {
        return null;
    }

    @Override public RArray setAttributes(Attributes attributes) {
        Utils.nyi();
        return null;
    }

    @Override public boolean dependsOn(RAny value) {
        return false;
    }

    @Override public boolean isTemporary() {
        return false;
    }

    @Override public String toString() {
        return name;
    }

    public boolean startsWith(RSymbol other) {
        return name.startsWith(other.name);
    }
}
