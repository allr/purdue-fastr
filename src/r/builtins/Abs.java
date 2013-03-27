package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "abs"
 *
 * <pre>
 * x -- a numeric or complex vector or array.
 * </pre>
 */
// FIXME: use node rewriting to get rid of the type checks
public class Abs extends CallFactory {

    static final Abs _ = new Abs("abs", new String[]{"x"}, null);

    Abs(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static double abs(double d) {
        return Math.abs(d);
    }

    public static int abs(int v) {
        return v == RInt.NA ? RInt.NA : Math.abs(v);
    }

    public static double abs(double real, double imag) {
        return RComplex.RComplexUtils.eitherIsNA(real, imag) ? RDouble.NA : Arithmetic.chypot(real, imag);
    }

    public static RDouble abs(final RDouble orig) {

        return new View.RDoubleProxy<RDouble>(orig) {

            @Override public double getDouble(int i) {
                return abs(orig.getDouble(i));
            }
        };
    }

    public static RInt abs(final RInt orig) {

        return new View.RIntProxy<RInt>(orig) {
            @Override public int getInt(int i) {
                return abs(orig.getInt(i));
            }
        };
    }

    public static RDouble abs(final RComplex orig) {

        return new View.RDoubleProxy<RComplex>(orig) {
            @Override public double getDouble(int i) {
                return abs(orig.getReal(i), orig.getImag(i));
            }
        };
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public final RAny doBuiltIn(Frame frame, RAny arg) { // FIXME: turn this into node rewriting
                if (arg instanceof ScalarDoubleImpl) { return RDouble.RDoubleFactory.getScalar(abs(((ScalarDoubleImpl) arg).getDouble())); }
                if (arg instanceof ScalarIntImpl) { return RInt.RIntFactory.getScalar(abs(((ScalarIntImpl) arg).getInt())); }
                if (arg instanceof ScalarComplexImpl) {
                    ScalarComplexImpl c = (ScalarComplexImpl) arg;
                    return RDouble.RDoubleFactory.getScalar(abs(c.getReal(), c.getImag()));
                }
                if (arg instanceof RDouble) { return abs((RDouble) arg); }
                if (arg instanceof RInt) { return abs((RInt) arg); }
                if (arg instanceof RComplex) { return abs((RComplex) arg); }
                if (arg instanceof RLogical) { return arg.asInt(); }
                throw Utils.nyi();
            }
        };
    }
}
