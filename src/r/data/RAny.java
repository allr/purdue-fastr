package r.data;

import java.util.*;

import r.Convert.ConversionStatus;
import r.*;
import r.nodes.*;
import r.nodes.truffle.*;

// NOTE: error handling with casts is tricky, because different commands do it differently
//  sometimes error is signaled by returning an NA
//  sometimes that comes with a warning that NAs have been introduced, but the warning is only given once for the whole vector even if multiple NAs are introduced
//  but sometimes there is a different warning or even an error when the conversion is not possible
//
//  also, error messages sometimes come from R itself when builtins are implemented in R, but we implement some in Java that are in GNU-R implemented in R
//    (this is not fully implemented in R)
public interface RAny {

    public static enum Mode {
        LOGICAL,
        INT,
        DOUBLE,
        COMPLEX,
        STRING,
        RAW
    }

    String TYPE_STRING = "any";
    String typeOf();

    Attributes attributes();
    Attributes attributesRef();
    RAny setAttributes(Attributes attributes);
    RAny stripAttributes();

    String pretty();
    String prettyMatrixElement();

        // casts that don't set a flag
        // FIXME: maybe could remove these and always pass the argument, but that might be slower
    RRaw asRaw();
    RLogical asLogical();
    RInt asInt();
    RDouble asDouble();
    RComplex asComplex();
    RString asString();

    RList asList();

        // casts that do set a flag when NA is introduced, out of range raw value, discarded imaginary part
    RRaw asRaw(ConversionStatus warn);
    RLogical asLogical(ConversionStatus warn);
    RInt asInt(ConversionStatus warn);
    RDouble asDouble(ConversionStatus warn);
    RComplex asComplex(ConversionStatus warn);
    RString asString(ConversionStatus warn); // FIXME: is any error ever produced? is this needed for String?

    void ref();
    boolean isShared(); // FIXME: at some point will probably need do distinguish between 0, 1, and 2

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);

    public static class Attributes {
        private boolean shared;
        private LinkedHashMap<RSymbol, RAny> map;
        private PartialEntry pmap;

        public static final boolean PROPAGATE_PARTIAL_MAP = true;

        public static class PartialEntry {
            TreeMap<Character, PartialEntry> children;
            RSymbol fullName;   // null means not present (RSymbol.NA_SYMBOL is not a valid attribute name)

            public PartialEntry(RSymbol fullName) {
                children = new TreeMap<Character, PartialEntry>();
                this.fullName = fullName;
            }

            public PartialEntry() {
                this(null);
            }
        }

        public Attributes() {
            map = new LinkedHashMap<RSymbol, RAny>();
            shared = false;
        }

        public void createPartialMap() {
            assert Utils.check(pmap == null);

            pmap = new PartialEntry();
            for (RSymbol s : map.keySet()) {
                partialAdd(s);
            }
        }

        public void put(RSymbol key, RAny value) {
            if (pmap == null) {
                map.put(key, value);
            } else {
                boolean present = map.containsKey(key);
                map.put(key, value);
                if (!present) {
                    partialAdd(key);
                }
            }
        }

        private void partialAdd(RSymbol symbol) {
            char[] key = symbol.name().toCharArray();
            PartialEntry root = pmap;
            for (int i = 0; i < key.length - 1; i++) {
                char k = key[i];
                TreeMap<Character, PartialEntry> cmap = root.children;
                root = cmap.get(k);
                if (root == null) {
                    root = new PartialEntry();
                    cmap.put(k, root);
                }
            }
            char k = key[key.length - 1];
            TreeMap<Character, PartialEntry> cmap = root.children;
            root = cmap.get(k);
            if (root == null) {
                cmap.put(k, new PartialEntry(symbol));
            } else {
                assert Utils.check(root.fullName == null);  // attributes have unique names
                root.fullName = symbol;
            }
        }

        public boolean hasPartialMap() {
            return pmap != null;
        }

        public RSymbol partialFind(RSymbol partialName) {
            assert Utils.check(pmap != null);
            assert Utils.check(partialName != RSymbol.NA_SYMBOL);

            return partialFind(partialName.name().toCharArray(), 0, pmap);
        }

        private RSymbol partialFind(char[] key, int keyStart, PartialEntry root) {
            PartialEntry pe = root.children.get(key[keyStart]);
            if (pe == null) {
                return null;
            }
            int nextStart = keyStart + 1;
            if (nextStart == key.length) {
                // found a match, check if it is unique
                return countPresentValues(pe) == 1 ? pe.fullName : null;
            }
            return partialFind(key, nextStart, pe); // recursion
        }

        private int countPresentValues(PartialEntry root) {
            int cnt = root.fullName == null ? 0 : 1;
            for (PartialEntry pe : root.children.values()) {
                cnt += countPresentValues(pe);
            }
            return cnt;
        }

        public boolean areShared() {
            return shared;
        }

        public static Attributes markShared(Attributes a) {
            if (a != null) {
                a.shared = true;
            }
            return a;
        }

        public Attributes markShared() {
            shared = true;
            return this;
        }

        public LinkedHashMap<RSymbol, RAny> map() {
            return map;
        }

        public Attributes copy() {
            Attributes nattr = new Attributes();
            LinkedHashMap<RSymbol, RAny> nmap = nattr.map();

            for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
                // TODO: do we need a deep copy? probably not, should use reference counts instead
                // TODO: a similar issue applies to Utils.copyArray for RList (in ListImpl)
                nmap.put(entry.getKey(), Utils.copyAny(entry.getValue()));
            }

            if (PROPAGATE_PARTIAL_MAP) {
                nattr.pmap = pmap;
                pmap = null;
            }

            return nattr;
        }

        public Attributes getOrCopy() {
            if (shared) {
                return copy();
            } else {
                return this;
            }
        }

        public static Attributes getOrCopy(Attributes attr) {
            if (attr == null) {
                return null;
            } else {
                return attr.getOrCopy();
            }
        }

    }
}
