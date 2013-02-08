package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.RComplex.RComplexUtils;
import r.data.RDouble.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class ColumnsRowsStats {
    private static final String[] paramNames = new String[]{"x", "na.rm", "dims"};

    private static final int IX = 0;
    private static final int INA_RM = 1;
    private static final int IDIMS = 2;

    public abstract static class Stats {
        public abstract double[] stat(RComplex x, int m, int n, boolean naRM);
        public abstract double[] stat(RDouble x, int m, int n, boolean naRM);
        public abstract double[] stat(RInt x, int m, int n, boolean naRM);
    }

    public static void checkDimensions(ASTNode ast, int[] dimensions) {
        if (dimensions == null || dimensions.length < 2) {
            throw RError.getXArrayTwo(ast);
        }
        if (dimensions.length > 2) {
            Utils.nyi("unimplemented for arrays of more than 2 dimensions");
        }
    }

    public static final class StatsFactory extends CallFactory {

        private final Stats stats;

        public StatsFactory(Stats stats) {
            this.stats = stats;
        }

        public RAny stat(RContext context, ASTNode ast, RComplex x, boolean naRM) {
            int[] dim = x.dimensions();
            checkDimensions(ast, dim);
            double[] content = stats.stat(x, dim[0], dim[1], naRM); // real, imag, real, imag, ...
            return RComplex.RComplexFactory.getFor(content);
        }

        public RAny stat(RContext context, ASTNode ast, RDouble x, boolean naRM) {
            int[] dim = x.dimensions();
            checkDimensions(ast, dim);
            double[] content = stats.stat(x, dim[0], dim[1], naRM);
            return RDouble.RDoubleFactory.getFor(content);
        }

        public RAny stat(RContext context, ASTNode ast, RInt x, boolean naRM) {
            int[] dim = x.dimensions();
            checkDimensions(ast, dim);
            double[] content = stats.stat(x, dim[0], dim[1], naRM);
            return RDouble.RDoubleFactory.getFor(content);
        }

        public RAny stat(RContext context, ASTNode ast, RAny x, boolean naRM) {
            if (x instanceof RDouble) {
                return stat(context, ast, (RDouble) x, naRM);
            }
            if (x instanceof RInt) {
                return stat(context, ast, (RInt) x, naRM);
            }
            if (x instanceof RLogical) {
                return stat(context, ast, ((RLogical) x).asInt(), naRM);
            }
            if (x instanceof RComplex) {
                return stat(context, ast, (RComplex) x, naRM);
            }
            throw RError.getXNumeric(ast);
        }

        public RAny stat(RContext context, ASTNode ast, RAny x, RAny naRM) {
            if (naRM instanceof RLogical) {
                RLogical lnaRM = (RLogical) naRM;
                if (lnaRM.size() == 1) {
                    int l = lnaRM.getLogical(0);
                    if (l == RLogical.TRUE) {
                        return stat(context, ast, x, true);
                    }
                    if (l == RLogical.FALSE) {
                        return stat(context, ast, x, false);
                    }
                }
            } else  if (naRM instanceof RDouble || naRM instanceof RInt) {
                return stat(context, ast, x, naRM.asLogical());
            }
            throw RError.getInvalidArgument(ast, "na.rm");
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }
            if (provided[IDIMS]) {
                Utils.nyi("unimplemented argument");
            }
            if (names.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, Frame frame, RAny x) {
                        return stat(context, ast, x, false);
                    }
                };
            }

            boolean maybeNARm = false;
            if (provided[INA_RM]) {
                if (!BuiltIn.isLogicalConstant(exprs[paramPositions[INA_RM]], RLogical.FALSE)) {
                    maybeNARm = true;
                }
            }

            if (names.length == 2) {
                if (!maybeNARm) { // FIXME: is it overkill to optimize for this?
                    return new BuiltIn.BuiltIn2(call, names, exprs) {

                        @Override
                        public RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1) {
                            return stat(context, ast, (paramPositions[IX] == 0) ? arg0 : arg1, false);
                        }
                    };
                } else {
                    return new BuiltIn.BuiltIn2(call, names, exprs) {

                        @Override
                        public RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1) {
                            RAny x;
                            RAny naRM;
                            if (paramPositions[IX] == 0) {
                                x = arg0;
                                naRM = arg1;
                            } else {
                                x = arg1;
                                naRM = arg0;
                            }
                            return stat(context, ast, x, naRM);
                        }
                    };
                }
            }
            return null;
        }
    }

    // FIXME: we might get better performance by hand-inlining the functions below

    static double[] colSumsMeans(RComplex c, int m, int n, boolean mean, boolean naRM) {
        double[] content = new double[2 * n];
        outerLoop:
        for (int j = 0; j < n; j++) {
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
        outerLoop:
        for (int j = 0; j < n; j++) {
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
        outerLoop:
        for (int j = 0; j < n; j++) {
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

    public static Stats getColSums() {
        return new Stats() {
            @Override
            public double[] stat(RComplex x, int m, int n, boolean naRM) {
                return colSumsMeans(x, m, n, false, naRM);
            }
            @Override
            public double[] stat(RDouble x, int m, int n, boolean naRM) {
                return colSumsMeans(x, m, n, false, naRM);
            }

            @Override
            public double[] stat(RInt x, int m, int n, boolean naRM) {
                return colSumsMeans(x, m, n, false, naRM);
            }
        };
    }

    public static Stats getColMeans() {
        return new Stats() {
            @Override
            public double[] stat(RComplex x, int m, int n, boolean naRM) {
                return colSumsMeans(x, m, n, true, naRM);
            }
            @Override
            public double[] stat(RDouble x, int m, int n, boolean naRM) {
                return colSumsMeans(x, m, n, true, naRM);
            }

            @Override
            public double[] stat(RInt x, int m, int n, boolean naRM) {
                return colSumsMeans(x, m, n, true, naRM);
            }
        };
    }

    public static Stats getRowSums() {
        return new Stats() {
            @Override
            public double[] stat(RComplex x, int m, int n, boolean naRM) {
                return rowSumsMeans(x, m, n, false, naRM);
            }
            @Override
            public double[] stat(RDouble x, int m, int n, boolean naRM) {
                return rowSumsMeans(x, m, n, false, naRM);
            }

            @Override
            public double[] stat(RInt x, int m, int n, boolean naRM) {
                return rowSumsMeans(x, m, n, false, naRM);
            }
        };
    }

    public static Stats getRowMeans() {
        return new Stats() {
            @Override
            public double[] stat(RComplex x, int m, int n, boolean naRM) {
                return rowSumsMeans(x, m, n, true, naRM);
            }
            @Override
            public double[] stat(RDouble x, int m, int n, boolean naRM) {
                return rowSumsMeans(x, m, n, true, naRM);
            }

            @Override
            public double[] stat(RInt x, int m, int n, boolean naRM) {
                return rowSumsMeans(x, m, n, true, naRM);
            }
        };
    }

    public static final CallFactory COLSUMS_FACTORY = new StatsFactory(getColSums());
    public static final CallFactory ROWSUMS_FACTORY = new StatsFactory(getRowSums());
    public static final CallFactory COLMEANS_FACTORY = new StatsFactory(getColMeans());
    public static final CallFactory ROWMEANS_FACTORY = new StatsFactory(getRowMeans());

}
