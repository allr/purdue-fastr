package r.ifc;

import r.builtins.*;
import r.builtins.CallFactory.ArgumentInfo;
import r.data.*;
import r.data.RArray.Names;
import r.data.internal.*;
import r.ifc.Interop.Invokable;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * Interface to the rest of the Java world. This class supports the registration of Java methods as R functions.
 **/
public class Interop {

    public static void initialize() {
        register(new Invokable() {
            @Override
            public String name() {
                return "jan";
            }

            @Override
            public String[] parameters() {
                return new String[]{"foo"};
            }

            @Override
            public String[] requiredParameters() {
                return new String[0];
            }

            @Override
            public RAny invoke(ArgumentInfo ai, RAny[] args) {
                String res = "jan ";
                int ix = ai.position("foo");
                if (ix != -1) res += " got a foo of " + args[ix].asString().getString(0);
                return asRString(res);

            }
        });
    }

    /** Java has to implement this interface for R to be able to call it. */
    public interface Invokable {
        /**
         * Method is called by R. The ai describes the order in which the arguments are passed. This can be different
         * from one call site to the next. Args is the actual list of argument values.
         */
        RAny invoke(ArgumentInfo ai, RAny[] args);

        String name();

        String[] requiredParameters();

        String[] parameters();
    }

    /** Add the invokeable to the global R name space. */
    public static void register(Invokable fun) {
        Primitives.add(new ExternalJavaBuiltin(fun, fun.name(), fun.parameters(), fun.requiredParameters()));
    }

    /** Takes a string and return the R object representing the same string. */
    public static RString asRString(String s) {
        return new ScalarStringImpl(s);
    }

    /** Takes an R object representing a string and returns the same string. */
    public static String asString(RAny r) {
        if (r instanceof ScalarStringImpl) {
            ScalarStringImpl s = (ScalarStringImpl) r;
            return s.getString();
        } else return null;
    }

    /** Takes an R object representing a number and returns an integer. */
    public static int asInteger(RAny r) {
        RInt ri = r.asInt();
        return ri.getInt(0);
    }

    /** Takes an R object representing a number and returns a double. */
    public static double asDouble(RAny r) {
        RDouble rd = r.asDouble();
        return rd.getDouble(0);
    }

    /** Takes an R object representing a vector of strings and returns an array of String. */
    public static String[] asStringArray(RAny r) {
        if (r instanceof StringImpl) return ((StringImpl) r).getContent();
        else if (r instanceof ScalarStringImpl) return new String[]{((ScalarStringImpl) r).getString()};
        else return null;
    }

    /** Takes an integer array and returns a R Int vector. */
    public static RInt asRIntVector(int[] v) {
        return new IntImpl(v);
    }

    /** Takes an R int vector and returns the corresponding int array or null. */
    public static int[] asIntArray(RAny r) {
        if (r == null) return null;
        else if (r instanceof IntImpl) return ((IntImpl) r).getContent();
        else return null;
    }

    /** Takes a double array and returns a R Double vector. */
    public static RDouble asRDoubleVector(double[] v) {
        return new DoubleImpl(v);
    }

    /** Takes an R double vector and returns the corresponding double array or null. */
    public static double[] asDoubleArray(RAny r) {
        if (r instanceof DoubleImpl) return ((DoubleImpl) r).getContent();
        else return null;
    }

    public static RAny makeDoubleVector(double[] res, int[] dim, String[] names) {
        RSymbol[] namesSym = new RSymbol[names.length];
        for (int i = 0; i < names.length; i++)
            namesSym[i] = RSymbol.getSymbol(names[i]);
        return new DoubleImpl(res, dim, Names.create(namesSym));
    }

    // Note :  should we copy the attribute?
    public static RAny setAttribute(RAny x, String which, String value) {
        RAny.Attributes attr = x.attributes();
        RSymbol which_ = RSymbol.getSymbol(which);
        RAny value_ = new ScalarStringImpl(value);
        if (attr != null) {
            attr.put(which_, value_);
            return x;
        } else {
            attr = new RAny.Attributes();
            attr.put(which_, value_);
            return x.setAttributes(attr);
        }
    }

    /**
     * Given an object and an attribute name returns a string object that denotes the attribute's value or null if not
     * found.
     */
    public static String getAttributeAsString(RAny x, String which) {
        RSymbol which_ = RSymbol.getSymbol(which);
        RAny.Attributes attr = x.attributes();
        if (attr == null) return null;
        RAny r = attr.map().get(which_);
        RString rs = r.asString();
        return rs.getString(0);
    }
}

/** Internal implementation: adapter for calling Java code from R. **/
class ExternalJavaBuiltin extends CallFactory {
    private final Invokable fun;

    ExternalJavaBuiltin(Invokable fun, String name, String[] parameters, String[] required) {
        super(name, parameters, required);
        this.fun = fun;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        final ArgumentInfo ai = check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny res = fun.invoke(ai, args);
                return res;
            }
        };
    }

}
