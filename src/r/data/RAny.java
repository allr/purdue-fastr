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

    String TYPE_STRING = "any";
    String typeOf();

    Attributes attributes();
    RAny setAttributes(Attributes attributes);
    RAttributes getAttributes(); // FIXME: remove
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
        boolean shared;
        LinkedHashMap<RSymbol, RAny> map;

        public Attributes() {
            map = new LinkedHashMap<RSymbol, RAny>();
            shared = false;
        }

        public boolean areShared() {
            return shared;
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
                // TODO: do we need deep copy? probably not, should use reference counts instead
                // TODO: a similar issue applies to Utils.copyArray for RList (in ListImpl)
                nmap.put(entry.getKey(), Utils.copyAny(entry.getValue()));
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
