package r.data;

import java.util.*;

import r.*;
import r.Convert.*;
import r.builtins.Primitives.PrimitiveEntry;
import r.data.internal.*;
import r.nodes.truffle.*;

public final class RSymbol extends BaseObject implements RAny {

    private static final SymbolTable symbolTable = new SymbolTable(); // TODO put in Context ??!!
    public static final RSymbol[] EMPTY_SYMBOL_ARRAY = new RSymbol[0];

    // TODO: put these symbols back to where they are needed, the re-insertion is no longer done

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

    final String name;
    // The next two fields are for the topLevel
    Object value;
    int version;
    // The next is for the builtins registration
    PrimitiveEntry primitive;

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

    public static void resetTable() {
        for (RSymbol s : symbolTable.table.values()) {
            s.value = null;
            s.version = 0;
        }
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

    public Object getValue() {
        return RPromise.force(value);
    }

    public void setValue(Object val) {
        value = val;
    }

    public int getVersion() {
        return version;
    }

    public PrimitiveEntry getPrimitiveEntry() {
        return primitive;
    }

    public void setPrimitiveEntry(PrimitiveEntry primitive) {
        this.primitive = primitive;
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
