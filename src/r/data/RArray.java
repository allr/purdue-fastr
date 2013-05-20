package r.data;

import r.data.internal.View;

import java.util.HashMap;

/**
 * The interface for all arrays. R makes the distinction between vectors and arrays. In R, a array can be considered as
 * a multiply subscripted collection of data entries.
 * 
 * <pre>
 *  TODO: dimensions array should never be modified (only can be replaced, to allow sharing)
 *  TODO: names object should never be modified (only can be replaced, to allow sharing)
 * </pre>
 */
public interface RArray extends RAny {

    int[] SCALAR_DIMENSIONS = new int[]{1, 1};

    int size();

    /**
     * Do not modify the array returned by this function!
     */
    int[] dimensions();

    /**
     * Do not modify the value returned by this function!
     */
    Names names();

    Object get(int i);

    Object getRef(int i);

    RAny boxedGet(int i);

    RAny boxedNamedGet(int i);

    RArray set(int i, Object val);

    RArray setDimensions(int[] dimensions);

    RArray setNames(Names names);

    RArray setAttributes(Attributes attributes);

    RArray stripAttributes();

    boolean isNAorNaN(int i);

    int index(int i, int j);

    RArray subset(RAny keys);

    RArray subset(RInt index);

    RArray subset(RString names);

    RArray materialize();

    /**
     * Utility functions on Arrays.
     */
    public static class RArrayUtils {

        public static RArray markShared(RArray a) {
            a.ref();
            a.ref();
            return a;
        }
    }

    /**
     * Implementation of the names attribute. When fewer names are provided than the vector's size, they are extended by
     * character NAs to the length of x. Multiple occurrences of the same symbol are possible in names attribute for the
     * same vector. The name "" is special: it is used to indicate that there is no name associated with an element of a
     * (atomic or generic) vector. Subscripting by "" will match nothing (not even elements which have no name). A name
     * can be character NA, but such a name will never be matched and is likely to lead to confusion.
     * <p>
     * For vectors, the names are one of the attributes with restrictions on the possible values. For pairlists, the
     * names are the tags and converted to and from a character vector. For a one-dimensional array the names attribute
     * really is dimnames[[1]]. Formally classed aka S4 objects typically have slotNames() (and no names()).
     * 
     * <pre>
     * FIXME: Should we really have an object that wraps around an array. This seems slightly wasteful.
     * FIXME: Note that when using a hashmap we have to be careful. The same symbol can occur multiple times
     * the R behavior seems to be to return the first occurrence of the symbol.
     * FIXME: In R, when we set dim, the names attribute appears to be lost. (Check that we do the same)
     * </pre>
     */
    public static final class Names {

        RSymbol[] names;
        /* map is lazily initialized. */
        HashMap<RSymbol, Integer> map;

        private Names(RSymbol[] names, HashMap<RSymbol, Integer> preparedMap) {
            this.names = names;
            map = preparedMap;
        }

        public static Names create(int size) {
            RSymbol[] n = new RSymbol[size];
            for (int i = 0; i < n.length; ++i) {
                n[i] = RSymbol.EMPTY_SYMBOL;
            }
            return new Names(n, null);
        }

        public static Names create(RSymbol[] names) {
            return new Names(names, null);
        }

        public static Names create(RSymbol[] names, HashMap<RSymbol, Integer> preparedMap) {
            return new Names(names, preparedMap);
        }

        public RSymbol[] sequence() {
            return names;
        }

        public Names exclude(int i) {
            int size = names.length;
            int nsize = size - 1;
            RSymbol[] newNames = new RSymbol[nsize];
            System.arraycopy(names, 0, newNames, 0, i);
            if (i < nsize) {
                System.arraycopy(names, i + 1, newNames, i, nsize - i);
            }
            return create(newNames);
        }

        // FIXME: or should we use RSymbol.asString ?
        public String[] asStringArray() {
            String[] res = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                res[i] = names[i] != null ? names[i].pretty() : RString.NA;
            }
            return res;
        }

        /**
         * Compares two symbols to see if src is a partial match with tgt. Return false if either of them has a null
         * name().
         * 
         * <pre>
         * FIXME: Is it possible for name() to be null?
         * </pre>
         */
        static boolean partialNameMatch(RSymbol src, RSymbol tgt) {
            if (src.name() == null || tgt.name() == null) { return false; }
            return (tgt.name().startsWith(src.name()));
        }

        /**
         * Returns the offset of the symbol name or -1 one if either not present or matched multiple times.
         */
        public int mapPartial(RSymbol name) {
            int res = -1;
            int matches = 0;
            for (int i = 0; i < names.length; i++) {
                if (partialNameMatch(name, names[i])) {
                    res = i;
                    if (++matches > 1) { return -1; }
                }
            }
            return res;
        }

        public int size() {
            return names.length;
        }

        /**
         * NA and "" never match, and thus need not be put in the hashmap. For multiply occurring names we return the
         * first index.
         */
        private void initializeMapIfNeeded() {
            if (map != null) { return; }
            map = new HashMap(names.length);
            for (int i = 0; i < names.length; i++) {
                RSymbol name = names[i];
                if (name != RSymbol.EMPTY_SYMBOL && name != RSymbol.NA_SYMBOL && !map.containsKey(name)) {
                    map.put(names[i], i);
                }
            }
        }

        public int map(RSymbol name) {
            initializeMapIfNeeded();
            Integer index = map.get(name);
            return index == null ? -1 : index.intValue();
        }

        public HashMap<RSymbol, Integer> getMap() {
            initializeMapIfNeeded();
            return map;
        }

    }

    public static class RListView extends View.RListView implements RList {

        final RArray arr;

        public RListView(RArray arr) {
            this.arr = arr;
        }

        @Override public int size() {
            return arr.size();
        }

        @Override public RAny getRAny(int i) {
            return arr.boxedGet(i);
        }

        @Override public boolean isSharedReal() {
            return arr.isShared();
        }

        @Override public void ref() {
            arr.ref();
        }

        @Override public Names names() {
            return arr.names();
        }
    }
}
