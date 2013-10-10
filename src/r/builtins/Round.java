package r.builtins;

import r.*;
import r.data.*;
import r.data.RAny.Attributes;
import r.data.RArray.Names;
import r.data.RComplex.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.Constant;
import r.nodes.exec.Arithmetic.*;

import java.lang.Double;
import java.math.*;
import java.util.*;

// FIXME: there are some unnecessary casts of the "digits" argument (e.g. to complex or to double), GNU-R has the same problem
// FIXME: make this faster, note there is fround in nmath
// FIXME: fix error messages on non-numeric arguments
final class Round extends CallFactory {

    static final CallFactory _ = new Round("round", new String[]{"x", "digits"}, new String[]{"x"});

    private Round(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int digitsPosition = ia.position("digits");

        if (digitsPosition == -1) {
            return new Arithmetic(call,  exprs[0], new Constant(call, RDouble.BOXED_ZERO), ROUND_JAVA);
        }
        if (digitsPosition == 1) {
            return new Arithmetic(call,  exprs[0], exprs[1], ROUND_JAVA);
        }
        // digitsPosition == 0;
        return new Arithmetic(call,  exprs[1], exprs[0], ROUND_JAVA);
    }

    static final int MAX_DIGITS = 37; // FIXME: check it reflects R

    public static double round(double x, double digits) {
        // LICENSE: transcribed code from GNU R's math library, which is licensed under GPL
        int d;
        if (RDouble.RDoubleUtils.isNAorNaN(x) || RDouble.RDoubleUtils.isNAorNaN(digits)) {
            return x + digits;
        }
        if (digits == Double.NEGATIVE_INFINITY) {
            return 0;
        }
        if (digits == Double.POSITIVE_INFINITY) {
            return x;
        }
        if (digits > MAX_DIGITS) {
            d = MAX_DIGITS;
        } else {
            d = (int) Math.floor(digits + 0.5);
        }

        if (!RDouble.RDoubleUtils.isFinite(x)) {
            return x; // NOTE: BigDecimal cannot be constructed for a non-finite number
        }
        return new BigDecimal(x).setScale(d, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static void round(double[] x, double digits, double[] res) {
        // LICENSE: transcribed code from GNU R's math library, which is licensed under GPL
        int size = x.length;
        if (digits == Double.NEGATIVE_INFINITY) {
            Arrays.fill(res, 0);
            return;
        }
        if (digits == Double.POSITIVE_INFINITY) {
            if (res != x) {
                System.arraycopy(x, 0, res, 0, size);
            }
            return;
        }
        boolean naDigits =  RDouble.RDoubleUtils.isNAorNaN(digits);
        int d;
        if (naDigits) {
            d = 0;
        } else if (digits > MAX_DIGITS) {
            d = MAX_DIGITS;
        } else {
            d = (int) Math.floor(digits + 0.5);
        }

        for (int i = 0; i < size; i++) {
            double r = x[i];
            if (naDigits || RDouble.RDoubleUtils.isNAorNaN(r)) {
                res[i] = r + digits;
            } else {
                res[i] = new BigDecimal(r).setScale(d, RoundingMode.HALF_EVEN).doubleValue();
            }
        }
    }

    // FIXME: check this actually corresponds with the R semantics
    // in GNU-R, they do not use the same infrastructure for round as for arithmetic operators
    // TODO: and indeed this does not really work, because we should not be casting to the common type here (value and digits)
    // yet, it would be nice to be able to use the specializations... and views... as done for the other arithmetic operations
    public static final class RoundJava extends ValueArithmetic {

        @Override
        public double opReal(ASTNode ast, double a, double b, double c, double d) {
            return round(a,c);
        }

        @Override
        public double opImag(ASTNode ast, double a, double b, double c, double d) {
            return round(b, c);
        }

        @Override
        public double op(ASTNode ast, double a, double b) {
            return round(a, b);
        }

        @Override
        public int op(ASTNode ast, int a, int b) {
            Utils.nyi("unreachable");
            return -1;
        }

        @Override
        public void emitOverflowWarning(ASTNode ast) {
        }

        @Override
        public void opComplexEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            Utils.nyi();
        }

        @Override
        public void opComplexScalar(ASTNode ast, double[] x, double c, double d, double[] res, int size) {
            // TODO: fix this, not really a binary math operation, we should throw an error if
            // the imaginary part is non-zero _or_ if it is zero but the originally passed argument was a complex
            round(x, c, res);
        }
        @Override
        public void opScalarComplex(ASTNode ast, double a, double b, double[] y, double[] res, int size) {
            Utils.nyi();
        }

        @Override
        public void opDoubleEqualSize(ASTNode ast, double[] x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                double b = y[i];
                double c = round(a, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opDoubleScalar(ASTNode ast, double[] x, double y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double a = x[i];
                double c = round(a, y);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(a) || RDouble.RDoubleUtils.arithIsNA(y)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opScalarDouble(ASTNode ast, double x, double[] y, double[] res, int size) {
            for (int i = 0; i < size; i++) {
                double b = y[i];
                double c = round(x, b);
                if (RDouble.RDoubleUtils.arithIsNA(c)) {
                    if (RDouble.RDoubleUtils.arithIsNA(x) || RDouble.RDoubleUtils.arithIsNA(b)) {
                        res[i] = RDouble.NA;
                    }
                } else {
                    res[i] = c;
                }
            }
        }
        @Override
        public void opIntImplSequenceASized(ASTNode ast, int[] x, int yfrom, int yto, int ystep, int[] res, int size) {
            Utils.nyi();
        }
        @Override
        public boolean returnsDouble() {
            return true;
        }
    }

    public static final ValueArithmetic ROUND_JAVA = new RoundJava();

}
