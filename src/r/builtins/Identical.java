package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

// FIXME: not full R semantics (some options unimplemented, but perhaps unimportant)
// FIXME: could improve performance e.g. using direct access to arrays and memcmp/Arrays.equals, if needed
// FIXME: add support for language objects

public class Identical extends CallFactory {

    static final CallFactory _ = new Identical("identical", new String[]{"x", "y", "num.eq", "single.NA", "attrib.as.set", "ignore.bytecode"}, new String[] {"x", "y"});

    private Identical(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posX = ia.position("x");
        final int posY = ia.position("y");
        final int posNumEq = ia.position("num.eq");
        final int posSingleNA = ia.position("single.NA");
        final int posAttribAsSet = ia.position("attrib.as.set");
        final int posIgnoreBytecode = ia.position("ignore.bytecode");

        if (posNumEq != -1 || posSingleNA != -1 || posAttribAsSet != -1 || posIgnoreBytecode != -1) {
            Utils.nyi("Unsupported option");
            return null;
            // TODO: add some of these options(?), but it's hard to believe they could be practically useful
        }

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny xArg = args[posX];
                RAny yArg = args[posY];

                return identical(xArg, yArg) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
            }

        };
    }

    public static boolean identical(RAny x, RAny y) {
        if (x == y) {
            return true;
        }
        boolean xArray = x instanceof RArray;
        boolean yArray = y instanceof RArray;
        if (xArray != yArray) {
            return false;
        }
        if (xArray) {
            if (!identicalDimensions((RArray) x, (RArray) y)) {
                return false;
            }
            if (!identicalNames((RArray) x, (RArray) y)) {
                return false;
            }
        }
        if (!identicalAttributes(x, y)) {
            return false;
        }
        if (x instanceof RList) {
            if (y instanceof RList) {
                return identical((RList) x, (RList) y);
            }
            return false;
        }
        if (x instanceof RString) {
            if (y instanceof RString) {
                return identical((RString) x, (RString) y);
            }
            return false;
        }
        if (x instanceof RComplex) {
            if (y instanceof RComplex) {
                return identical((RComplex) x, (RComplex) y);
            }
            return false;
        }
        if (x instanceof RDouble) {
            if (y instanceof RDouble) {
                return identical((RDouble) x, (RDouble) y);
            }
            return false;
        }
        if (x instanceof RInt) {
            if (y instanceof RInt) {
                return identical((RInt) x, (RInt) y);
            }
            return false;
        }
        if (x instanceof RLogical) {
            if (y instanceof RLogical) {
                return identical((RLogical) x, (RLogical) y);
            }
            return false;
        }
        if (x instanceof RRaw) {
            if (y instanceof RRaw) {
                return identical((RRaw) x, (RRaw) y);
            }
            return false;
        }
        if (x instanceof RNull) {
            return y instanceof RNull;
        }
        // FIXME: add other types, often the result will be false
        Utils.nyi("unsupported type");
        return false;
    }

    public static boolean identicalDimensions(RArray x, RArray y) {
        return Arrays.equals(x.dimensions(), y.dimensions());
    }

    public static boolean identicalNames(RArray x, RArray y) {
        RSymbol[] xnames = x.names() == null ? null : x.names().sequence();
        RSymbol[] ynames = y.names() == null ? null : y.names().sequence();

        if (xnames == ynames) {
            return true;
        }
        if (xnames == null || ynames == null) {
            return false;
        }
        int len = xnames.length;
        if (len != ynames.length) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (xnames[i] != ynames[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean identicalAttributes(RAny x, RAny y) {
        RAny.Attributes xattr = x.attributes();
        RAny.Attributes yattr = y.attributes();

        if (xattr == yattr) {
            return true;
        }
        if (xattr == null) {
            return yattr.map().size() == 0;
        }
        if (yattr == null) {
            return xattr.map().size() == 0;
        }
        LinkedHashMap<RSymbol, RAny> xmap = xattr.map();
        LinkedHashMap<RSymbol, RAny> ymap = yattr.map();

        if (xmap.size() != ymap.size()) { // NOTE: deleting an attribute means real deletion from the map, not mapping to null
            return false;
        }

        for(Map.Entry<RSymbol, RAny> xentry : xmap.entrySet()) {
            RSymbol symbol = xentry.getKey();
            RAny xvalue = xentry.getValue();
            RAny yvalue = ymap.get(symbol);
            if (yvalue == null || !identical(xvalue, yvalue)) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(RRaw x, RRaw y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (x.getRaw(i) != y.getRaw(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(RLogical x, RLogical y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (x.getLogical(i) != y.getLogical(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(RInt x, RInt y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (x.getInt(i) != y.getInt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(RDouble x, RDouble y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            double xd = x.getDouble(i);
            double yd = y.getDouble(i);
            if (!identical(xd, yd)) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(RComplex x, RComplex y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            double xr = x.getReal(i);
            double yr = y.getReal(i);
            double xi = x.getImag(i);
            double yi = y.getImag(i);

            if (!identical(xr, yr) || !identical(xi, yi)) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(double x, double y) {
        boolean xIsNAorNaN = RDouble.RDoubleUtils.isNAorNaN(x);
        boolean yIsNAorNaN = RDouble.RDoubleUtils.isNAorNaN(y);
        if (!xIsNAorNaN && !yIsNAorNaN) {
            return x == y;
        }
        if (xIsNAorNaN != yIsNAorNaN) {
            return false;
        }

        boolean xIsNA = RDouble.RDoubleUtils.isNA(x);
        boolean yIsNA = RDouble.RDoubleUtils.isNA(y);
        if (xIsNA != yIsNA) {
            return false;
        }

        return true;
    }

    public static boolean identical(RString x, RString y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            String xs = x.getString(i);
            String ys = y.getString(i);
            if (xs == ys) {
                continue;
            }
            if (xs == RString.NA || ys == RString.NA) {
                return false;
            }
            if (xs.compareTo(ys) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean identical(RList x, RList y) {
        int len = x.size();
        if (len != y.size()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            RAny xa = x.getRAny(i);
            RAny ya = y.getRAny(i);
            if (!identical(xa, ya)) {
                return false;
            }
        }
        return true;
    }
}
