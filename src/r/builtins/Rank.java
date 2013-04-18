package r.builtins;

import r.Truffle.*;

import r.*;
import r.builtins.Order.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import java.lang.Integer;
import java.util.*;

final class Rank extends CallFactory {

    static final CallFactory _ = new Rank("rank", new String[]{"x", "na.last", "ties.method"}, new String[]{"x"});

    private Rank(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    enum NaLast {
        TRUE, FALSE, NA, KEEP
    }

    // NOTE: the error messages are not exactly like in GNU-R, because there the implementation is in R and errors are implicit
    public static NaLast parseNaLast(RAny arg, ASTNode ast) {
        if (arg instanceof RLogical || arg instanceof RInt || arg instanceof RDouble || arg instanceof RComplex) {
            RLogical larg = arg.asLogical();
            int size = larg.size();
            if (size == 0) { throw RError.getUnexpectedNA(ast); }
            if (size > 1) {
                RContext.warning(ast, RError.LENGTH_GT_1);
            }
            // size >= 1
            switch (larg.getLogical(0)) {
            case RLogical.TRUE:
                return NaLast.TRUE;
            case RLogical.FALSE:
                return NaLast.FALSE;
            default:
                return NaLast.NA;
            }
        }
        if (arg instanceof RString) {
            RString sarg = (RString) arg;
            int size = sarg.size();
            if (size == 0) { throw RError.getUnexpectedNA(ast); }
            if (size > 1) {
                RContext.warning(ast, RError.LENGTH_GT_1);
            }
            // size >= 1
            String s = sarg.getString(0);
            if (s == RString.NA) { return NaLast.NA; }
            if (s.equals("keep")) { return NaLast.KEEP; }
        }
        throw RError.getInvalidArgument(ast, "na.last");
    }

    final static ArgumentMatch tiesMethodMatch = new ArgumentMatch(new String[]{"average", "first", "random", "max", "min"});
    final static int TM_AVERAGE = 0;
    final static int TM_FIRST = 1;
    final static int TM_RANDOM = 2;
    final static int TM_MAX = 3;
    final static int TM_MIN = 4;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        final int naLastPosition = ia.position("na.last");
        final int tiesMethodPosition = ia.position("ties.method");

        // FIXME: could statically handle constant arguments na.last, ties.method

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                NaLast naLast = naLastPosition == -1 ? NaLast.TRUE : parseNaLast(args[naLastPosition], ast);
                int tiesMethod = tiesMethodPosition == -1 ? TM_AVERAGE : tiesMethodMatch.match(args[tiesMethodPosition], ast, "ties.method");

                RAny xarg = args[xPosition]; // permitted logical, int, double, complex, string
                if (!(xarg instanceof RArray)) { throw RError.getInvalidArgument(ast, "x"); // FIXME: not an R error message
                }
                RArray x = (RArray) xarg;
                return rank(x, naLast, tiesMethod, ast);
            }
        };
    }

    public static RAny rank(RArray x, NaLast naLast, int tiesMethod, ASTNode ast) {
        int size = x.size();
        int oi = 0;
        Integer[] order = new Integer[size]; // indexes of non-NA values in x
        // TODO: remove Java boxing through primitive sort methods

        boolean[] isna = null;
        for (int i = 0; i < size; i++) {
            if (x.isNAorNaN(i)) {
                if (isna == null) {
                    isna = new boolean[size];
                }
                isna[i] = true;
            } else {
                order[oi++] = i;
            }
        }
        int nnas = size - oi;
        final ElementsComparator c = Order.createComparator(x, ast);
        Comparator<Integer> mainComparator = new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                return c.cmp(o1, o2);
            }
        };
        Arrays.sort(order, 0, oi, mainComparator);
        RArray rank;
        switch (tiesMethod) {
        case TM_AVERAGE:
            rank = rankAverage(order, oi, c);
            break;
        case TM_MAX:
            rank = rankMax(order, oi, c);
            break;
        case TM_MIN:
            rank = rankMin(order, oi, c);
            break;
        case TM_FIRST:
            rank = rankFirst(order, oi);
            break;
        default:
            Utils.nyi("unsupported ties method");
            return null; // TODO: TM_RANDOM - add when runif is implemented
        }
        RArray.Names xnames = x.names();
        if (nnas == 0) { return rank.setNames(xnames); }
        if (naLast == NaLast.NA) {
            if (xnames == null) { return rank; }
            RArray nrank = Utils.createArray(rank, oi);
            RSymbol[] symbols = xnames.sequence();
            RSymbol[] nsymbols = new RSymbol[oi];
            int j = 0;
            for (int i = 0; i < size; i++) {
                if (!isna[i]) {
                    nsymbols[j] = symbols[i];
                    nrank.set(j, rank.get(i));
                    j++;
                }
            }
            return nrank.setNames(RArray.Names.create(nsymbols));
        }
        if (naLast == NaLast.KEEP) {
            for (int i = 0; i < size; i++) {
                if (isna[i]) {
                    rank.set(i, x.get(i)); // copying to preserve NaNs (note GNU-R turns silently NaNs into NAs)
                }
            }
            return rank.setNames(xnames);
        }
        if (naLast == NaLast.TRUE) {
            if (tiesMethod == TM_AVERAGE) { // ranks are doubles
                double[] content = ((DoubleImpl) rank).getContent();
                int newRank = oi;
                for (int i = 0; i < size; i++) {
                    if (isna[i]) {
                        content[i] = ++newRank;
                    }
                }
            } else { // ranks are integers
                int[] content = ((IntImpl) rank).getContent();
                int newRank = oi;
                for (int i = 0; i < size; i++) {
                    if (isna[i]) {
                        content[i] = ++newRank; // FIXME: perhaps should check overflow?
                    }
                }
            }
            return rank.setNames(xnames);
        }
        assert Utils.check(naLast == NaLast.FALSE);
        if (tiesMethod == TM_AVERAGE) { // ranks are doubles
            double[] content = ((DoubleImpl) rank).getContent();
            int naIndex = 1;
            for (int i = 0; i < size; i++) {
                if (!isna[i]) {
                    content[i] += nnas;
                } else {
                    content[i] = naIndex++;
                }
            }
        } else { // ranks are integers
            int[] content = ((IntImpl) rank).getContent();
            int naIndex = 1;
            for (int i = 0; i < size; i++) {
                if (!isna[i]) {
                    content[i] += nnas;
                } else {
                    content[i] = naIndex++;
                }
            }
        }
        return rank.setNames(xnames);
    }

    public static RArray rankAverage(Integer[] order, int orderUsed, ElementsComparator c) {
        double[] res = new double[order.length];
        int j = 0;
        for (int i = 0; i < orderUsed; i = j + 1) {
            j = i;
            while ((j < orderUsed - 1) && c.cmp(order[j], order[j + 1]) == 0) {
                j++;
            }
            // elements i, ..., j are equal (ties)
            double value = (i + j + 2.0) / 2.0; // 1-based
            for (int k = i; k <= j; k++) {
                res[order[k]] = value;
            }
        }
        return RDouble.RDoubleFactory.getFor(res);
    }

    public static RArray rankMax(Integer[] order, int orderUsed, ElementsComparator c) {
        int[] res = new int[order.length];
        int j = 0;
        for (int i = 0; i < orderUsed; i = j + 1) {
            j = i;
            while ((j < orderUsed - 1) && c.cmp(order[j], order[j + 1]) == 0) {
                j++;
            }
            // elements i, ..., j are equal (ties)
            int value = j + 1; // 1-based
            for (int k = i; k <= j; k++) {
                res[order[k]] = value;
            }
        }
        return RInt.RIntFactory.getFor(res);
    }

    public static RArray rankMin(Integer[] order, int orderUsed, ElementsComparator c) {
        int[] res = new int[order.length];
        int j = 0;
        for (int i = 0; i < orderUsed; i = j + 1) {
            j = i;
            while ((j < orderUsed - 1) && c.cmp(order[j], order[j + 1]) == 0) {
                j++;
            }
            // elements i, ..., j are equal (ties)
            int value = i + 1; // 1-based
            for (int k = i; k <= j; k++) {
                res[order[k]] = value;
            }
        }
        return RInt.RIntFactory.getFor(res);
    }

    public static RArray rankFirst(Integer[] order, int orderUsed) {
        int[] res = new int[order.length];
        for (int i = 0; i < orderUsed; i++) {
            res[order[i]] = i + 1;
        }
        return RInt.RIntFactory.getFor(res);
    }

}
