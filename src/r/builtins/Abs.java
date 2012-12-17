package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

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
            return Math.sqrt(real * real + imag * imag);
        } else {
            return RDouble.NA;
        }
    }

    public static RDouble abs(final RDouble orig) {
        final int size = orig.size();

        return new View.RDoubleView() {

            @Override
            public double getDouble(int i) {
                return abs(orig.getDouble(i));
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }

            @Override
            public int[] dimensions() {
                return orig.dimensions();
            }

        };
    }

    public static RInt abs(final RInt orig) {
        final int size = orig.size();

        return new View.RIntView() {
            @Override
            public int getInt(int i) {
                return abs(orig.getInt(i));
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }

            @Override
            public int[] dimensions() {
                return orig.dimensions();
            }
        };
    }

    public static RDouble abs(final RComplex orig) {
        final int size = orig.size();

        return new View.RDoubleView() {

            @Override
            public double getDouble(int i) {
                return abs(orig.getReal(i), orig.getImag(i));
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }

            @Override
            public int[] dimensions() {
                return orig.dimensions();
            }
        };
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(final RContext context, Frame frame, RAny arg) { // FIXME: turn this into node rewriting
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
