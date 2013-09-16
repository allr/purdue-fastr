package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "rev"
 * 
 * <pre>
 * x -- a vector or another object for which reversal is defined.
 * </pre>
 */
// FIXME: could also do lazy rev of int sequence
class Rev extends CallFactory {
    static final CallFactory _ = new Rev("rev", new String[]{"x"}, new String[]{"x"});

    Rev(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                return rev(arg);
            }
        };
    }

    public static RString rev(RString orig) {
        final int size = orig.size() - 1;
        return new View.RStringProxy<RString>(orig) {
            @Override public String getString(int i) {
                return orig.getString(size - i);
            }

            @Override public Attributes attributes() { // drop attributes
                return null;
            }
        };
    }

    public static RLogical rev(RLogical orig) {
        final int size = orig.size() - 1;
        return new View.RLogicalProxy<RLogical>(orig) {
            @Override public int getLogical(int i) {
                return orig.getLogical(size - i);
            }

            @Override public Attributes attributes() { // drop attributes
                return null;
            }
        };
    }

    public static RInt rev(RInt orig) {
        final int size = orig.size() - 1;
        return new View.RIntProxy<RInt>(orig) {

            @Override public int getInt(int i) {
                return orig.getInt(size - i);
            }

            @Override public Attributes attributes() { // drop attributes
                return null;
            }
        };
    }

    public static RDouble rev(RDouble orig) {
        final int size = orig.size() - 1;
        return new View.RDoubleProxy<RDouble>(orig) {

            @Override public double getDouble(int i) {
                return orig.getDouble(size - i);
            }

            @Override public Attributes attributes() { // drop attributes
                return null;
            }
        };
    }

    // FIXME: should do type-specialization
    public static RAny rev(RAny arg) {
        // default implementation
        if (!(arg instanceof RArray)) { throw Utils.nyi("unsupported type"); }
        RArray a = (RArray) arg;
        RArray.Names names = a.names();
        int size;
        if (names == null) {
            if (arg instanceof RDouble) { return rev((RDouble) arg); }
            if (arg instanceof RInt) { return rev((RInt) arg); }
            if (arg instanceof RLogical) { return rev((RLogical) arg); }
            if (arg instanceof RString) { return rev((RString) arg); }
            size = a.size();
        } else {
            // reverse names
            RSymbol[] symbols = names.sequence();
            size = a.size();
            assert Utils.check(size == symbols.length);
            RSymbol[] rsymbols = new RSymbol[size];
            for (int i = 0; i < size; i++) {
                rsymbols[i] = symbols[size - i - 1];
            }
            names = RArray.Names.create(rsymbols);
        }

        RArray res = Utils.createArray(a, size, null, names, null); // drop attributes
        for (int i = 0; i < size; i++) {
            res.set(i, a.getRef(size - 1 - i));
        }
        return res;
    }
}
