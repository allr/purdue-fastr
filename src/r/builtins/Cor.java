package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: finish this, by now only pearson/matrix/no NA filtering supported
public final class Cor extends CallFactory {

    static final CallFactory _ = new Cor("cor", new String[]{"x", "y", "use", "method"}, new String[]{"x"});

    private Cor(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    static final ArgumentMatch methodMatch = new ArgumentMatch(new String[]{"pearson", "kendall", "spearman"});
    static final int M_PEARSON = 0;
    static final int M_KENDALL = 1;
    static final int M_SPEARMAN = 2;

    // TODO: this is wrong, can't use ArgumentMatch because GNU-R uses pmatch here, "everything" is the default, NULL is not accepted
    static final ArgumentMatch useMatch = new ArgumentMatch(new String[]{"all.obs", "complete.obs", "pairwise.complete.obs", "everything", "na.or.complete"});
    static final int M_ALL_OBS = 0;
    static final int M_COMPLETE_OBS = 1;
    static final int M_PAIRWISE_COMPLETE_OBS = 2;
    static final int M_EVERYTHING = 3;
    static final int M_NA_OR_COMPLETE = 4;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);

        Utils.nyi("cor to be implemented");
        return null;
    }

    public static boolean[] columnsNAMap(double[] x, int nrow, int ncol) {
        final boolean[] res = new boolean[ncol];
        for (int j = 0; j < ncol; j++) {
            final int offset = j * nrow;
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
        final double[] res = new double[ncol];
        for (int j = 0; j < ncol; j++) {
            if (hasNA[j]) {
                res[j] = RDouble.NA;
                continue;
            }
            final int offset = j * nrow;
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
