package r.ifc;

import r.Truffle.Frame;
import r.builtins.*;
import r.builtins.CallFactory.ArgumentInfo;
import r.data.*;
import r.data.internal.*;
import r.ifc.Interop.Invokable;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * Interface to the rest of the Java world. This class supports the registration of Java methods as R functions.
 **/
public class Interop {

    public static void initialize() {
        register(new Invokable() {
            public String name() {
                return "jan";
            }

            public String[] parameters() {
                return new String[]{"foo"};
            }

            public String[] requiredParameters() {
                return new String[0];
            }

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

    /** Takes an integer array and returns a R Int vector. */
    public static RInt asRIntVector(int[] v) {
        return new IntImpl(v);
    }

    /** Takes an R int vector and returns the corresponding int array or null. */
    public static int[] asIntArray(RAny r) {
        if (r instanceof IntImpl) {
            return ((IntImpl) r).getContent();
        } else return null;
    }

    /** Takes a double array and returns a R Double vector. */
    public static RDouble asRDoubleVector(double[] v) {
        return new DoubleImpl(v);
    }

    /** Takes an R double vector and returns the corresponding double array or null. */
    public static double[] asDoubleArray(RAny r) {
        if (r instanceof DoubleImpl) {
            return ((DoubleImpl) r).getContent();
        } else return null;
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
