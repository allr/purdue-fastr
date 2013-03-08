package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: use node rewriting to get rid of the type checks
public class Abs {
    public static double abs(double d) {
        if (!RDouble.RDoubleUtils.isNA(d)) {
            return Math.abs(d);
        } else {
            return RDouble.NA;
        }
    }
    public static int abs(int v) {
        if (v != RInt.NA) {
            return Math.abs(v);
        } else {
            return RInt.NA;
        }
    }
    public static double abs(double real, double imag) {
        if (!RComplex.RComplexUtils.eitherIsNA(real, imag)) {
            if (!Double.isInfinite(real) && !Double.isInfinite(imag)) {
                return Math.sqrt(real * real + imag * imag);
            } else {
                return RDouble.POS_INF;
            }
        } else {
            return RDouble.NA;
        }
    }

    public static RDouble abs(final RDouble orig) {

        return new View.RDoubleProxy<RDouble>(orig) {

            @Override
            public double getDouble(int i) {
                return abs(orig.getDouble(i));
            }
        };
    }

    public static RInt abs(final RInt orig) {

        return new View.RIntProxy<RInt>(orig) {
            @Override
            public int getInt(int i) {
                return abs(orig.getInt(i));
            }
        };
    }

    public static RDouble abs(final RComplex orig) {

        return new View.RDoubleProxy<RComplex>(orig) {

            @Override
            public double getDouble(int i) {
                return abs(orig.getReal(i), orig.getImag(i));
            }
        };
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg) { // FIXME: turn this into node rewriting
                    if (arg instanceof ScalarDoubleImpl) {
                        return RDouble.RDoubleFactory.getScalar(abs(((ScalarDoubleImpl) arg).getDouble()));
                    }
                    if (arg instanceof ScalarIntImpl) {
                        return RInt.RIntFactory.getScalar(abs(((ScalarIntImpl) arg).getInt()));
                    }
                    if (arg instanceof ScalarComplexImpl) {
                        ScalarComplexImpl c = (ScalarComplexImpl) arg;
                        return RDouble.RDoubleFactory.getScalar(abs(c.getReal(), c.getImag()));
                    }
                    if (arg instanceof RDouble) {
                        return abs((RDouble) arg);
                    }
                    if (arg instanceof RInt) {
                        return abs((RInt) arg);
                    }
                    if (arg instanceof RComplex) {
                        return abs((RComplex) arg);
                    }
                    Utils.nyi();
                    return null;
                }
            };
        }
    };
}
