package r.builtins;

import r.*;
import r.data.*;
import r.data.RComplex.RComplexUtils;
import r.data.RDouble.RDoubleUtils;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * Base
 * 
 * <pre>
 * x -- an array of two or more dimensions, containing numeric, complex, integer or logical values, or a numeric data frame.
 * na.rm -- logical. Should missing values (including NaN) be omitted from the calculations?
 * dims -- integer: Which dimensions are regarded as rows or columns to sum over. For row*, the sum or mean is over 
 *         dimensions dims+1, ...; for col* it is over dimensions 1:dims.
 * </pre>
 */
abstract class ColRowBase extends CallFactory {

    private final Stats stats;

    ColRowBase(String name, Stats stats) {
        super(name, new String[]{"x", "na.rm", "dims"}, new String[]{"x"});
        this.stats = stats;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        final ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("dims")) { throw Utils.nyi("unimplemented argument"); }
        if (names.length == 1) { return new BuiltIn.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x) {
                return stat(ast, x, false);
            }
        }; }

        boolean maybeNARm = false;
        if (ia.provided("na.rm")) {
            if (!BuiltIn.isLogicalConstant(exprs[ia.position("na.rm")], RLogical.FALSE)) {
                maybeNARm = true;
            }
        }

        if (names.length == 2) {
            if (!maybeNARm) { // FIXME: is it overkill to optimize for this?
                return new BuiltIn.BuiltIn2(call, names, exprs) {
                    @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                        return stat(ast, (ia.position("x") == 0) ? arg0 : arg1, false);
                    }
                };
            } else {
                return new BuiltIn.BuiltIn2(call, names, exprs) {
                    @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                        return ia.position("x") == 0 ? stat(ast, arg0, arg1) : stat(ast, arg1, arg0);
                    }
                };
            }
        }
        throw Utils.nyi();
    }

    public abstract static class Stats {
        public abstract double[] stat(RComplex x, int m, int n, boolean naRM);

        public abstract double[] stat(RDouble x, int m, int n, boolean naRM);

        public abstract double[] stat(RInt x, int m, int n, boolean naRM);

        public abstract int[] getResultDimension(int[] sourceDim);
    }

    public static int[] checkDimensions(ASTNode ast, int[] dimensions) {
        if (dimensions == null || dimensions.length < 2) { throw RError.getXArrayTwo(ast); }
        if (dimensions.length > 2) {
            int[] result = new int[2];
            result[0] = dimensions[0];
            result[1] = 1;
            for (int i = 1; i < dimensions.length; ++i) {
                result[1] *= dimensions[i];
            }
            return result;
        }
        return dimensions;
    }

    public RAny stat(ASTNode ast, RComplex x, boolean naRM) {
        int[] dim = x.dimensions();
        int[] d = checkDimensions(ast, dim);
        double[] content = stats.stat(x, d[0], d[1], naRM); // real, imag, real, imag, ...
        return RComplex.RComplexFactory.getFor(content, dim == d ? null : stats.getResultDimension(dim), null);
    }

    public RAny stat(ASTNode ast, RDouble x, boolean naRM) {
        int[] dim = x.dimensions();
        int[] d = checkDimensions(ast, dim);
        double[] content = stats.stat(x, d[0], d[1], naRM);
        return RDouble.RDoubleFactory.getFor(content, dim == d ? null : stats.getResultDimension(dim), null);
    }

    public RAny stat(ASTNode ast, RInt x, boolean naRM) {
        int[] dim = x.dimensions();
        int[] d = checkDimensions(ast, dim);
        double[] content = stats.stat(x, d[0], d[1], naRM);
        return RDouble.RDoubleFactory.getFor(content, dim == d ? null : stats.getResultDimension(dim), null);
    }

    public RAny stat(ASTNode ast, RAny x, boolean naRM) {
        if (x instanceof RDouble) { return stat(ast, (RDouble) x, naRM); }
        if (x instanceof RInt) { return stat(ast, (RInt) x, naRM); }
        if (x instanceof RLogical) { return stat(ast, x.asInt(), naRM); }
        if (x instanceof RComplex) { return stat(ast, (RComplex) x, naRM); }
        throw RError.getXNumeric(ast);
    }

    public RAny stat(ASTNode ast, RAny x, RAny naRM) {
        if (naRM instanceof RLogical) {
            RLogical lnaRM = (RLogical) naRM;
            if (lnaRM.size() == 1) {
                int l = lnaRM.getLogical(0);
                if (l == RLogical.TRUE) { return stat(ast, x, true); }
                if (l == RLogical.FALSE) { return stat(ast, x, false); }
            }
        } else if (naRM instanceof RDouble || naRM instanceof RInt) { return stat(ast, x, naRM.asLogical()); }
        throw RError.getInvalidArgument(ast, "na.rm");
    }

    // FIXME: we might get better performance by hand-inlining the functions below

    static double[] colSumsMeans(RComplex c, int m, int n, boolean mean, boolean naRM) {
        double[] content = new double[2 * n];
        outerLoop: for (int j = 0; j < n; j++) {
            double sumreal = 0;
            double sumimag = 0;
            int excluded = 0;
            for (int i = 0; i < m; i++) {
                int index = j * m + i;
                double real = c.getReal(index);
                double imag = c.getImag(index);
                if (!RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
                    sumreal += real;
                    sumimag += imag;
                } else {
                    if (!naRM) {
                        content[2 * j] = RDouble.NA;
                        content[2 * j + 1] = RDouble.NA;
                        continue outerLoop;
                    } else {
                        excluded++;
                    }
                }
            }
            if (mean) {
                double denom = m - excluded;
                content[2 * j] = sumreal / denom;
                content[2 * j + 1] = sumimag / denom;
            } else {
                content[2 * j] = sumreal;
                content[2 * j + 1] = sumimag;
            }
        }
        return content;
    }

    static double[] colSumsMeans(RDouble d, int m, int n, boolean mean, boolean naRM) {
        double[] content = new double[n];
        outerLoop: for (int j = 0; j < n; j++) {
            double sum = 0;
            int excluded = 0;
            for (int i = 0; i < m; i++) {
                double v = d.getDouble(j * m + i);
                if (!RDoubleUtils.isNAorNaN(v)) {
                    sum += v;
                } else {
                    if (!naRM) {
                        content[j] = RDouble.NA;
                        continue outerLoop;
                    } else {
                        excluded++;
                    }
                }
            }
            content[j] = mean ? sum / (m - excluded) : sum;
        }
        return content;
    }

    static double[] colSumsMeans(RInt in, int m, int n, boolean mean, boolean naRM) {
        double[] content = new double[n];
        outerLoop: for (int j = 0; j < n; j++) {
            double sum = 0;
            int excluded = 0;
            for (int i = 0; i < m; i++) {
                int v = in.getInt(j * m + i);
                if (v != RInt.NA) {
                    sum += v;
                } else {
                    if (!naRM) {
                        content[j] = RDouble.NA;
                        continue outerLoop;
                    } else {
                        excluded++;
                    }
                }
            }
            content[j] = mean ? sum / (m - excluded) : sum;
        }
        return content;
    }

    static double[] rowSumsMeans(RComplex c, int m, int n, final boolean mean, final boolean naRM) {
        double[] content = new double[2 * m];
        int[] excluded = null;
        boolean[] isNA = null;
        if (mean) {
            excluded = new int[m];
        }
        if (!naRM) {
            isNA = new boolean[m];
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                int index = j * m + i;
                double real = c.getReal(index);
                double imag = c.getImag(index);
                if (!RComplexUtils.eitherIsNAorNaN(real, imag)) {
                    content[2 * i] += real;
                    content[2 * i + 1] += imag;
                } else {
                    if (!naRM) {
                        isNA[i] = true;
                    } else {
                        if (mean) {
                            excluded[i]++;
                        }
                    }
                }
            }
        }
        if (!naRM || mean) {
            for (int i = 0; i < m; i++) {
                if (!naRM) {
                    if (isNA[i]) {
                        content[2 * i] = RDouble.NA;
                        content[2 * i + 1] = RDouble.NA;
                    } else {
                        if (mean) {
                            content[2 * i] /= n;
                            content[2 * i + 1] /= n;
                        }
                    }
                } else {
                    if (mean) {
                        double denom = n - excluded[i];
                        content[2 * i] /= denom;
                        content[2 * i + 1] /= denom;
                    }
                }
            }
        }
        return content;
    }

    static double[] rowSumsMeans(RDouble d, int m, int n, final boolean mean, final boolean naRM) {
        double[] content = new double[m];
        int[] excluded = null;
        boolean[] isNA = null;
        if (mean) {
            excluded = new int[m];
        }
        if (!naRM) {
            isNA = new boolean[m];
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                double v = d.getDouble(j * m + i);
                if (!RDoubleUtils.isNAorNaN(v)) {
                    content[i] += v;
                } else {
                    if (!naRM) {
                        isNA[i] = true;
                    } else {
                        if (mean) {
                            excluded[i]++;
                        }
                    }
                }
            }
        }
        if (!naRM || mean) {
            for (int i = 0; i < m; i++) {
                if (!naRM) {
                    if (isNA[i]) {
                        content[i] = RDouble.NA;
                    } else {
                        if (mean) {
                            content[i] /= n;
                        }
                    }
                } else {
                    if (mean) {
                        content[i] /= (n - excluded[i]);
                    }
                }
            }
        }
        return content;
    }

    static double[] rowSumsMeans(RInt in, int m, int n, final boolean mean, final boolean naRM) {
        double[] content = new double[m];
        int[] excluded = null;
        boolean[] isNA = null;
        if (mean) {
            excluded = new int[m];
        }
        if (!naRM) {
            isNA = new boolean[m];
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                int v = in.getInt(j * m + i);
                if (v != RInt.NA) {
                    content[i] += v;
                } else {
                    if (!naRM) {
                        isNA[i] = true;
                    } else {
                        if (mean) {
                            excluded[i]++;
                        }
                    }
                }
            }
        }
        if (!naRM || mean) {
            for (int i = 0; i < m; i++) {
                if (!naRM) {
                    if (isNA[i]) {
                        content[i] = RDouble.NA;
                    } else {
                        if (mean) {
                            content[i] /= n;
                        }
                    }
                } else {
                    if (mean) {
                        content[i] /= (n - excluded[i]);
                    }
                }
            }
        }
        return content;
    }
}
