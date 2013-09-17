package r.builtins;

import java.util.*;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "attributes(obj)"
 * 
 * <pre>
 * obj -- an object
 * </pre>
 * 
 * The names of a pairlist are not stored as attributes, but are reported as if they were.
 */
class Attributes extends CallFactory {

    static final CallFactory _ = new Attributes("attributes", new String[]{"obj"}, null);

    Attributes(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    /**
     * Return the count of special attributes present. There are at most two: "dimensions" and "names"
     */
    private static int countSpecialAttributes(RAny a) {
        if (!(a instanceof RArray)) { return 0; }
        RArray arr = (RArray) a;
        int i = arr.dimensions() == null ? 0 : 1;
        int j = arr.names() == null ? 0 : 1;
        return i + j;
    }

    private static RInt dimensionsAsVector(int[] dimensions) {
        return RInt.RIntFactory.getFor(dimensions);
    }

    private static RString namesAsVector(RArray.Names names) {
        return RString.RStringFactory.getFor(names.asStringArray());
    }

    /** When there are no custom attributes. */
    private static RAny specialAttributesAsList(RAny value) {
        int size = countSpecialAttributes(value);
        if (size == 0) { return RNull.getNull(); } // no custom attributes
        RAny[] acontent = new RAny[size];
        RSymbol[] anames = new RSymbol[size];
        fillSpecialAttributes(acontent, anames, 0, value);
        return RList.RListFactory.getFor(acontent, null, RArray.Names.create(anames));
    }

    /**
     * This method will extract names and dimensions attributes from the value argument. Argument start indicates where
     * in the content and names arrays the inserts should be made.
     */
    private static void fillSpecialAttributes(RAny[] content, RSymbol[] names, int start, RAny value) {
        if (!(value instanceof RArray)) { return; } // When is this the case?
        RArray arr = (RArray) value;
        int i = start;
        if (arr.dimensions() != null) {
            content[i] = dimensionsAsVector(arr.dimensions());
            names[i] = RSymbol.DIM_SYMBOL;
            i++;
        }
        if (arr.names() != null) {
            content[i] = namesAsVector(arr.names());
            names[i] = RSymbol.NAMES_SYMBOL;
        }
    }

    /**
     * Add all non-special attributes to content and names.
     */
    private static void fillAttributes(RAny[] content, RSymbol[] names, int start, Map<RSymbol, RAny> map) {
        int i = start;
        for (Map.Entry<RSymbol, RAny> entry : map.entrySet()) {
            content[i] = entry.getValue();
            names[i] = entry.getKey();
            i++;
        }
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public final RAny doBuiltIn(Frame frame, RAny arg) {
                RAny.Attributes attr = arg.attributes();
                if (attr == null) { return specialAttributesAsList(arg); }
                Map<RSymbol, RAny> map = attr.map();
                int nspecial = countSpecialAttributes(arg);
                int ncustom = map.size();
                int size = nspecial + ncustom;
                RAny[] acontent = new RAny[size];
                RSymbol[] anames = new RSymbol[size];
                fillSpecialAttributes(acontent, anames, 0, arg);
                fillAttributes(acontent, anames, nspecial, map);
                return RList.RListFactory.getFor(acontent, null, RArray.Names.create(anames));
            }
        };
    }
}
