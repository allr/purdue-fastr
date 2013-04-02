package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: finish this, by now only pearson/matrix/no NA filtering supported
public class Cor extends CallFactory {

    static final CallFactory _ = new Cor("cor", new String[]{"x", "y", "use", "method"}, new String[]{"x"});

    private Cor(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    final static ArgumentMatch methodMatch = new ArgumentMatch(new String[] {"pearson", "kendall", "spearman"});
    final static int M_PEARSON = 0;
    final static int M_KENDALL = 1;
    final static int M_SPEARMAN = 2;

    // TODO: this is wrong, can't use ArgumentMatch because GNU-R uses pmatch here, "everything" is the default, NULL is not accepted
    final static ArgumentMatch useMatch = new ArgumentMatch(new String[] {"all.obs", "complete.obs", "pairwise.complete.obs",
            "everything", "na.or.complete"});
    final static int M_ALL_OBS = 0;
    final static int M_COMPLETE_OBS = 1;
    final static int M_PAIRWISE_COMPLETE_OBS = 2;
    final static int M_EVERYTHING = 3;
    final static int M_NA_OR_COMPLETE = 4;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 1) {
            return new Builtin.Builtin1(call, names, exprs) {
                @Override public RAny doBuiltIn(Frame frame, RAny xarg) {
                    // x must be matrix-like
                    // method is pearson
                    // use is everything

                    RDouble xd;
                    if (xarg instanceof RDouble || xarg instanceof RInt || xarg instanceof RLogical) {
                        xd = xarg.asDouble().materialize();
                    } else {
                        if (xarg instanceof RArray) {
                            throw RError.getXNumeric(ast);
                        } else {
                            throw RError.getSupplyXYMatrix(ast);
                        }
                    }
                    int[] dim = xd.dimensions();
                    if (dim == null || dim.length != 2) {
                        throw RError.getSupplyXYMatrix(ast);
                    }

                    int nrow = dim[0];
                    int ncol = dim[1];
                    double[] res = new double[ncol * ncol];
                    double[] x = xd.getContent();

                    boolean[] hasNA = columnsNAMap(x, nrow, ncol);
                    double[] colMeans = columnMeans(x, nrow, ncol, hasNA);
                    int n1 = nrow - 1;

                    for (int i = 0; i < ncol ; i++) {
                        if (hasNA[i]) {
                            for (int j = 0; j <= i; j++ ) {
                                res[j * ncol + i] = RDouble.NA; // FIXME: break this into two loops?
                                res[i * ncol + j] = RDouble.NA;
                            }
                            continue;
                        }
                        double imean = colMeans[i];
                        int ioffset = i * nrow;
                        for (int j = 0; j <= i; j++) {
                            if (hasNA[j]) {
                                res[j * ncol + i] = RDouble.NA;
                                res[i * ncol + j] = RDouble.NA;
                                continue;
                            }
                            double jmean = colMeans[j];
                            int joffset = j * nrow;
                            double sum = 0;
                            for (int k = 0; k < nrow; k++) {
                                sum += (x[ioffset + k] - imean) * (x[joffset + k] - jmean);
                            }
                            double tmp = sum / n1;
                            res[j * ncol + i] = tmp;
                            res[i * ncol + j] = tmp;
                        }
                    }
                    double[] srcov = colMeans;  // colMeans no longer needed
                    for (int i = 0; i < ncol; i++) {
                        if (!hasNA[i]) {
                            srcov[i] = Math.sqrt(res[i * ncol + i]);
                        }
                    }
                    boolean sdZero = false;
                    for (int i = 0; i < ncol; i++) {
                        if (!hasNA[i]) {
                            for (int j = 0; j < i; j++) {
                                if (srcov[i] == 0 || srcov[j] == 0) {
                                    sdZero = true;
                                    res[j * ncol + i] = RDouble.NA;
                                    res[i * ncol + j] = RDouble.NA;
                                } else {
                                    double tmp = res[i * ncol + j]  / (srcov[i] * srcov[j]);
                                    if (tmp > 1) {
                                        tmp = 1;
                                    }
                                    res[j * ncol + i] = tmp;
                                    res[i * ncol + j] = tmp;
                                }
                            }
                        }
                        res[i * ncol + i] = 1;
                    }
                    if (sdZero) {
                        RContext.warning(ast, RError.SD_ZERO);
                    }
                    return RDouble.RDoubleFactory.getFor(res, new int[] {ncol, ncol}, null);
                }
            };
        }
//        final int xPosition = ia.position("x");
//        final int yPosition = ia.position("y");
//        final int usePosition = ia.position("use");
//        final int methodPosition = ia.position("method");

        Utils.nyi("finish cor");
        return null;
    }

    public static boolean[] columnsNAMap(double[] x, int nrow, int ncol) {
        boolean[] res = new boolean[ncol];
        for (int j = 0; j < ncol; j++) {
            int offset = j * nrow;
            for (int i = 0; i < nrow; i++) {
                if (RDouble.RDoubleUtils.isNAorNaN(x[offset + i])) {
                    res[j] = true;
                    break;
                }
            }
        }
        return res;
    }

    public static double[] columnMeans(double[] x, int nrow, int ncol, boolean[] hasNA) {
        double[] res = new double[ncol];
        for (int j = 0; j < ncol; j++) {
            if (hasNA[j]) {
                res[j] = RDouble.NA;
                continue;
            }
            int offset = j * nrow;
            double sum = 0;
            for (int k = 0; k < nrow; k++) {
                sum += x[offset + k];
            }
            double tmp = sum / nrow;
            if (RDouble.RDoubleUtils.isFinite(tmp)) {
                sum = 0;
                for (int k = 0; k < nrow; k++) {
                    sum += x[offset + k] - tmp;
                }
                tmp += sum / nrow;
            }
            res[j] = tmp;
        }
        return res;
    }
}
