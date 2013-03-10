package r.builtins;

import java.util.*;

import r.Convert.ConversionStatus;
import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;
import java.lang.Integer;

/**
 * "sort"
 * 
 * <pre>
 * x -- for sort an R object with a class or a numeric, complex, character or logical vector. For sort.int, 
 *      a numeric, complex, character or logical vector, or a factor.
 * decreasing -- logical. Should the sort be increasing or decreasing? Not available for partial sorting.
 * ... -- arguments to be passed to or from methods or (for the default methods and objects without a class) 
 *      to sort.int.
 * </pre>
 */
final class Sort extends CallFactory {

    static final CallFactory _ = new Seq("sort", new String[]{"...", "na.last", "decreasing"}, new String[]{});

    Sort(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static final ConversionStatus warn = new ConversionStatus(); // WARNING: calls not re-entrant

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int naLastPosition = ia.provided("na.last") ? ia.position("na.last") : -1;
        final int decreasingPosition = ia.provided("decreasing") ? ia.position("decreasing") : -1;
        return new BuiltIn(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {

                int nparams = params.length;
                int nkeys = nparams;
                int naLast;
                if (naLastPosition == -1) {
                    naLast = RLogical.TRUE;
                } else {
                    naLast = parseNALast(params[naLastPosition], ast);
                    nkeys--;
                }

                boolean decreasing;
                if (decreasingPosition == -1) {
                    decreasing = false;
                } else {
                    decreasing = parseDecreasing(params[decreasingPosition], ast);
                    nkeys--;
                }

                if (nkeys > 0) {
                    RArray[] keys = new RArray[nkeys];
                    int asize = -1;
                    int j = 0;
                    for (int i = 0; i < nparams; i++) {
                        if (i == naLastPosition || i == decreasingPosition) {
                            continue;
                        }
                        RAny p = params[i];
                        if (p instanceof RArray) {
                            RArray arr = (RArray) p;
                            int size = arr.size();
                            if (size != asize) {
                                if (asize == -1) {
                                    asize = size;
                                } else {
                                    throw RError.getArgumentLengthsDiffer(ast);
                                }
                            }
                            keys[j++] = arr.materialize();
                        } else {
                            throw RError.getArgumentNotVector(ast, i);
                        }
                    }
                    return sort(keys, decreasing, naLast, ast);
                }
                return RNull.getNull();
            }
        };
    }

    public static int parseNALast(RAny arg, ASTNode ast) {
        warn.naIntroduced = false;
        RLogical a = arg.asLogical(); // will produce NAs when conversion is not possible
        int size = a.size();
        int res;
        if (size == 1) {
            res = a.getLogical(0);
            if (res != RLogical.NA) { return res; }// just an optimization
        } else {
            if (size == 0) { throw RError.getLengthZero(ast); }// not exactly R's error message            
            // size > 1
            RContext.warning(ast, RError.LENGTH_GT_1);
            res = a.getLogical(0);
        }
        if (warn.naIntroduced) { throw RError.getInvalidArgument(ast, "na.last"); } // not exactly R's error message       
        return res;
    }

    public static boolean parseDecreasing(RAny arg, ASTNode ast) {
        RLogical a = arg.asLogical();
        int size = a.size();
        if (size >= 1) {
            int l = a.getLogical(0);
            if (l == RLogical.TRUE) { return true; }
            if (l == RLogical.FALSE) { return false; }
        }
        throw RError.getDecreasingTrueFalse(ast);
    }

    public abstract static class ElementsComparator {
        public abstract int cmp(int i, int j); // for non-NA elements

        public abstract boolean isNA(int i);
    }

    public static class DoubleComparator extends ElementsComparator {
        final RDouble v;

        public DoubleComparator(RDouble v) {
            this.v = v;
        }

        @Override public int cmp(int i, int j) {
            // elements are not NaNs
            double a = v.getDouble(i);
            double b = v.getDouble(j);
            if (a > b) { return 1; }
            if (a < b) { return -1; }
            return 0;
        }

        @Override public boolean isNA(int i) {
            return RDouble.RDoubleUtils.isNAorNaN(v.getDouble(i));
        }
    }

    public static class IntComparator extends ElementsComparator {
        final RInt v;

        public IntComparator(RInt v) {
            this.v = v;
        }

        @Override public int cmp(int i, int j) {
            int a = v.getInt(i);
            int b = v.getInt(j);
            if (a > b) { return 1; }
            if (a == b) { return 0; }
            return -1;
        }

        @Override public boolean isNA(int i) {
            return v.getInt(i) == RInt.NA;
        }
    }

    public static class LogicalComparator extends ElementsComparator {
        final RLogical v;

        public LogicalComparator(RLogical v) {
            this.v = v;
        }

        @Override public int cmp(int i, int j) {
            int a = v.getLogical(i);
            int b = v.getLogical(j);
            return a - b;
        }

        @Override public boolean isNA(int i) {
            return v.getLogical(i) == RLogical.NA;
        }
    }

    public static class StringComparator extends ElementsComparator {
        final RString v;

        public StringComparator(RString v) {
            this.v = v;
        }

        @Override public int cmp(int i, int j) {
            // elements are not NaNs
            String a = v.getString(i);
            String b = v.getString(j);
            return a.compareTo(b);
        }

        @Override public boolean isNA(int i) {
            return v.getString(i) == RString.NA;
        }
    }

    public static ElementsComparator createComparator(RArray arg, ASTNode ast) {
        if (arg instanceof RDouble) { return new DoubleComparator((RDouble) arg); }
        if (arg instanceof RInt) { return new IntComparator((RInt) arg); }
        if (arg instanceof RLogical) { return new LogicalComparator((RLogical) arg); }
        if (arg instanceof RString) { return new StringComparator((RString) arg); }
        if (arg instanceof RRaw) { throw RError.getRawSort(ast); }
        Utils.nyi("unsupported type");
        return null;
    }

    public static int resultWhenFirstNA(int naLast) {
        if (naLast != RLogical.FALSE) { // both TRUE and NA
            return 1;
        } else {
            return -1;
        }
    }

    public static int resultWhenSecondNA(int naLast) {
        if (naLast != RLogical.FALSE) { // both TRUE and NA
            return -1;
        } else {
            return 1;
        }
    }

    // FIXME: could hand-inline two variants, one for naLast == NA and another for naLast != NA
    public static RInt sort(RArray[] keys, final boolean decreasing, int naLast, ASTNode ast) {
        final int nkeys = keys.length;
        int size = keys[0].size();

        final boolean removeNA = (naLast == RLogical.NA);
        int naElements = 0;
        boolean[] tmp = null;
        if (removeNA) {
            tmp = new boolean[size];
        }
        final boolean[] hasNA = tmp;

        final ElementsComparator[] comp = new ElementsComparator[nkeys];
        for (int i = 0; i < nkeys; i++) {
            ElementsComparator c = createComparator(keys[i], ast);
            comp[i] = c;
            if (removeNA) {
                for (int j = 0; j < size; j++) {
                    if (!hasNA[j] && c.isNA(j)) {
                        hasNA[j] = true;
                        naElements++;
                    }
                }
            }
        }

        Integer[] order = new Integer[size]; // FIXME: have to box integers to be able to use a Arrays.sort with a comparator;
                                             // FIXME: could specialize for 1 key only, which should be a common case
                                             // FIXME: also could have a box with the first argument, e.g. FirstKey.index and FirstKey.value
        for (int i = 0; i < size; i++) {
            order[i] = i;
        }

        final ElementsComparator firstComparator = comp[0];
        final int resFirstNA = resultWhenFirstNA(naLast);
        final int resSecondNA = resultWhenSecondNA(naLast);

        Comparator<Integer> mainComparator = new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                int i1 = o1;
                int i2 = o2;

                boolean na1 = firstComparator.isNA(i1);
                boolean na2 = firstComparator.isNA(i2);
                if (removeNA) { // elements that have NA in any key will always be put last
                                // FIXME: might be also worth removing them before sorting, if there is a lot of them and the arrays are large
                    na1 = na1 || hasNA[i1];
                    na2 = na2 || hasNA[i2];
                }

                int res = 0;
                if (!na1) {
                    if (!na2) {
                        res = firstComparator.cmp(i1, i2);
                    } else {
                        return resSecondNA;
                    }
                } else {
                    if (!na2) { return resFirstNA; }
                    // NA vs NA
                }

                int j = 1;
                while (res == 0 && j < nkeys) {
                    ElementsComparator c = comp[j];
                    na1 = c.isNA(i1);
                    na2 = c.isNA(i2);
                    if (!na1) {
                        if (!na2) {
                            res = c.cmp(i1, i2);
                        } else {
                            return resSecondNA;
                        }
                    } else {
                        if (!na2) { return resFirstNA; }
                        // NA vs NA
                    }
                }
                if (!decreasing) {
                    return res;
                } else {
                    return -res;
                }
            }

        };

        Arrays.sort(order, mainComparator);

        if (!removeNA) {
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                content[i] = order[i] + 1; // 1-based
            }
            return RInt.RIntFactory.getFor(content);
        } else {
            int nsize = size - naElements;
            int[] content = new int[nsize];
            for (int i = 0; i < nsize; i++) {
                content[i] = order[i] + 1; // 1-based, excluding the NA values at the end
            }
            return RInt.RIntFactory.getFor(content);
        }
    }

}
