package r.data;

import r.Utils;
import r.data.RAny.*;
import r.data.internal.View;

import java.util.HashMap;

// note: dimensions array should never be modified (only can be replaced, to allow sharing)
// note: names object should never be modified (only can be replaced, to allow sharing)
public interface RArray extends RAny {
    int[] SCALAR_DIMENSIONS = new int[] {1, 1};

    int size();
    int[] dimensions(); // the returned array shall not be modified
    Names names();  // the returned array shall not be modified

    Object get(int i);
    Object getRef(int i);
    RAny boxedGet(int i);
    RAny boxedNamedGet(int i);
    RArray set(int i, Object val);
    RArray setDimensions(int[] dimensions);
    RArray setNames(Names names);
    RArray setAttributes(Attributes attributes);
    boolean isNAorNaN(int i);
    int index(int i, int j);
    RArray subset(RAny keys);
    RArray subset(RInt index);
    RArray subset(RString names);
    RArray materialize();

    public static class RArrayUtils {
        public static RArray markShared(RArray a) {
            a.ref();
            a.ref();
            return a;
        }
    }

    public abstract static class Names {
        RSymbol[] names;

        protected Names() {
            this.names = new RSymbol[0];
        }

        public Names(RSymbol[] names) {
            this.names = names;
        }

        public final RSymbol[] sequence() {
            return names;
        }

        public abstract int map(RSymbol name);

        public static Names create(RSymbol[] names) {
//            if (names.length > 10) {
            return new MappedNames(names);
//            } else {
//                return new SimpleNames(names);
//            }
        }

        public static Names create(RSymbol[] names, HashMap<RSymbol, Integer> preparedMap) {
            return new MappedNames(names, preparedMap);
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

        public String[] asStringArray() {
            String[] res = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                RSymbol s = names[i];
                if (s != null) { // FIXME: or should we use RSymbol.asString ?
                    res[i] = names[i].pretty();
                } else {
                    res[i] = RString.NA;
                }
            }
            return res;
        }

        public int mapPartial(RSymbol name) {
            String sname = name.name();
            if (sname == null) {
                return -1; // FIXME: should this be allowed?
            }

            int res = -1;
            for (int i = 0; i < names.length; i++) {
                String sStored = names[i].name();
                if (sStored != null && sStored.startsWith(sname)) {
                    if (res == -1) {
                        res = i;
                    } else {
                        return -1; // ambiguous match
                    }
                }
            }
            return res;
        }

        public int size() {
            return names.length;
        }

        public abstract HashMap<RSymbol, Integer> getMap();
        public abstract boolean keepsMap();
    }

    // TODO: currently this is not used as vector update using names needs a hashmap anyway
    public static final class SimpleNames extends Names { // FIXME: implement a specialized hash map that takes less memory

        public SimpleNames(RSymbol[] names) {
            super(names);
        }

        @Override
        public int map(RSymbol name) {
            for (int i = 0; i < names.length; i++) {
                if (names[i] == name) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public HashMap<RSymbol, Integer> getMap() {
            Utils.nyi();
            return null;
        }

        @Override
        public boolean keepsMap() {
            Utils.nyi();
            return false;
        }
    }

    public static final class MappedNames extends Names { // FIXME: implement a specialized hash map that takes less memory
        HashMap<RSymbol, Integer> namesMap; // NOTE: LinkedHashMap preserves the insertion order

        public MappedNames() {
        }

        public MappedNames(int size) {
            super(createEmptySymbolArray(size));
        }

        private static RSymbol[] createEmptySymbolArray(int size) {
            RSymbol[] n = new RSymbol[size];
            for (int i = 0; i < n.length; ++i) {
                n[i] = RSymbol.EMPTY_SYMBOL;
            }
            return n;
        }

        public MappedNames(RSymbol[] names) {
            super(names);
        }

        public MappedNames(RSymbol[] names, HashMap<RSymbol, Integer> preparedMap) {
            super(names);
            this.namesMap = preparedMap;
        }

        void initMap() {
            namesMap = new HashMap<RSymbol, Integer>(names.length);
            for (int i = 0; i < names.length; i++) {
                RSymbol name = names[i];
                if (!namesMap.containsKey(name)) {
                    namesMap.put(names[i], i);
                }
            }
        }

        @Override
        public int map(RSymbol name) {
            if (namesMap == null) { // race (but we don't care)
                initMap();
            }
            Integer index = namesMap.get(name);
            if (index != null) {
                return index.intValue();
            } else {
                return -1;
            }
        }

        @Override
        public HashMap<RSymbol, Integer> getMap() {
            if (namesMap == null) { // race (but we don't care)
                initMap();
            }
            return namesMap;
        }

        @Override
        public boolean keepsMap() {
            return true;
        }
    }

    public static class RListView extends View.RListView implements RList {
        final RArray arr;

        public RListView(RArray arr) {
            this.arr = arr;
        }

        @Override
        public int size() {
            return arr.size();
        }

        @Override
        public RAny getRAny(int i) {
            return arr.boxedGet(i);
        }

        @Override
        public boolean isSharedReal() {
            return arr.isShared();
        }

        @Override
        public void ref() {
            arr.ref();
        }

        @Override
        public Names names() {
            return arr.names();
        }
    }
}
