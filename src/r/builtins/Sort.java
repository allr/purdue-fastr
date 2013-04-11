package r.builtins;

import java.util.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.Order.DoubleComparator;
import r.builtins.Order.IntComparator;
import r.builtins.Order.LogicalComparator;
import r.builtins.Order.StringComparator;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import java.lang.Integer;

// NOTE: in GNU-R, the user can select between a stable version of shellsort and unstable version of quicksort, and the selection is mostly
// honored except for some cases when shellsort is silently used instead of quicksort (so "stable" instead of "unstable")
// In FastR, we treat "shell" as requirement for a stable sort and "quick" as that stability is not required, but we use the algorithms from the
// JDK.

// NOTE: sort.int is implemented in R in GNU-R

// TODO: support complex type
final class Sort extends CallFactory {
    //TODO: this is sort.int as of now, will have to extend when supporting S3
    static final CallFactory _ = new Sort("sort", new String[]{"x", "partial", "na.last", "decreasing", "method", "index.return"}, new String[]{"x"});

    private Sort(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    final static ArgumentMatch methodMatch = new ArgumentMatch(new String[] {"shell", "quick"});

    // returns true for quicksort, false for shellsort (shellsort is the default)
    public static boolean parseMethod(RAny arg, ASTNode ast) {
        int m = methodMatch.match(arg, ast, "method");
        return m == 1;
    }

    public static boolean parseIndexReturn(RAny arg, ASTNode ast) {
        if (!(arg instanceof RLogical || arg instanceof RInt || arg instanceof RDouble || arg instanceof RComplex)) {
            throw RError.getInvalidArgument(ast, "index.return"); // in GNU-R, this will appear part of if
        }
        RLogical a = arg.asLogical();
        int size = a.size();
        if (size >= 1) {
            int l = a.getLogical(0);
            if (l == RLogical.TRUE) { return true; }
            if (l == RLogical.FALSE) { return false; }
        }
        throw RError.getInvalidArgument(ast, "index.return"); // in GNU-R, this will appear part of if
    }

    public static RInt parsePartial(RAny arg, ASTNode ast) {
        if (arg instanceof RNull) {
            return null; // as if partial was not given
        }
        if (!(arg instanceof RDouble || arg instanceof RInt || arg instanceof RLogical|| arg instanceof RRaw)) {
            throw RError.getNotNumericVector(ast);
        }
        return arg.asInt();
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        final int partialPosition = ia.position("partial");
        final int naLastPosition = ia.position("na.last");
        final int decreasingPosition = ia.position("decreasing");
        final int methodPosition = ia.position("method");
        final int indexReturnPosition = ia.position("index.return");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                RAny xarg = params[xPosition];
                RArray x;
                if (xarg instanceof RArray) {
                    x = (RArray) xarg;
                } else {
                    throw RError.getMustBeAtomic(ast, "x"); // FIXME: make this more complete
                }
                RInt partial = (partialPosition == -1) ? null : parsePartial(params[partialPosition], ast);
                int naLast = (naLastPosition == -1) ? RLogical.NA : Order.parseNALast(params[naLastPosition], ast);
                boolean decreasing = (decreasingPosition == -1) ? false : Order.parseDecreasing(params[decreasingPosition], ast);
                boolean quickSort = (methodPosition == -1) ? false : parseMethod(params[methodPosition], ast);
                boolean indexReturn = (indexReturnPosition == -1) ? false : parseIndexReturn(params[indexReturnPosition], ast);

                if (partial != null) {
                    if (decreasing || methodPosition != -1 || indexReturn) {
                        throw RError.getUnsupportedPartial(ast);
                    }
                    Utils.nyi("partial sorting not implemented");
                    return null;
                }
                if (indexReturn && naLast != RInt.NA) {
                    throw RError.getIndexReturnRemoveNA(ast);
                }
                if (x.names() == null && !indexReturn && !decreasing) { // faster versions
                    if (x instanceof RDouble) {
                        double[] a = RDouble.RDoubleUtils.copyAsDoubleArray((RDouble) x);
                        // FIXME: could be in-place for temporary non-scalars with no attributes
                        Arrays.sort(a);
                        a = fixNAs(a, naLast);
                        return RDouble.RDoubleFactory.getFor(a); // drop attributes
                    }
                    if (x instanceof RInt) {
                        int[] a = RInt.RIntUtils.copyAsIntArray((RInt)x);
                        Arrays.sort(a);
                        a = fixNAs(a, naLast);
                        return RInt.RIntFactory.getFor(a); // drop attributes
                    }
                    // FIXME: could add a specialized "sort" for logicals
                }
                return sort(x, naLast, decreasing, indexReturn);
            }
        };
    }

    public static RArray sort(RArray x, int naLast, boolean decreasing, boolean indexReturn) {
        if (x instanceof RDouble) {
            return sort((RDouble) x, naLast, decreasing, indexReturn);
        }
        if (x instanceof RInt) {
            return sort((RInt) x, naLast, decreasing, indexReturn);
        }
        if (x instanceof RString) {
            return sort((RString) x, naLast, decreasing, indexReturn);
        }
        if (x instanceof RLogical) {
            return sort((RLogical) x, naLast, decreasing, indexReturn);
        }
        Utils.nyi("unsupported type for sorting");
        return null;
    }

    public static RArray sort(RDouble x, int naLast, final boolean decreasing, boolean indexReturn) {
        int size = x.size();
        Integer[] order = new Integer[size]; // TODO: remove Java boxing through primitive sort methods
        ArrayList<Integer> naorder = new ArrayList<Integer>();  // TODO: replace this by an implementation for primitives
        int[] naRemoveIndex = indexReturn ? new int[size] : null; // maps each index to index if NA/NaNs were removed
        int nnas = 0;
        int oi = 0;
        boolean naRemove = naLast == RInt.NA;
        for (int i = 0; i < size; i++) {
            if (naRemoveIndex != null) {
                naRemoveIndex[i] = i - nnas;
            }
            double d = x.getDouble(i);
            if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                if (!naRemove) {
                    naorder.add(i);
                } else {
                    nnas++;
                }
            } else {
                order[oi++] = i;
            }
        }
        final DoubleComparator cmp = new DoubleComparator(x);
        Comparator<Integer> mainComparator = new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                int res = cmp.cmp(o1, o2);
                return !decreasing ? res : -res;
            }
        };
        Arrays.sort(order, 0, oi, mainComparator);
        return buildSortResults(x, order, oi, naorder, naLast, naRemoveIndex);
    }

    public static RArray sort(RInt x, int naLast, final boolean decreasing, boolean indexReturn) {
        int size = x.size();
        Integer[] order = new Integer[size]; // TODO: remove Java boxing through primitive sort methods
        ArrayList<Integer> naorder = new ArrayList<Integer>();  // TODO: replace this by an implementation for primitives
        int[] naRemoveIndex = indexReturn ? new int[size] : null; // maps each index to index if NA/NaNs were removed
        int nnas = 0;
        int oi = 0;
        boolean naRemove = naLast == RInt.NA;
        for (int i = 0; i < size; i++) {
            if (naRemoveIndex != null) {
                naRemoveIndex[i] = i - nnas;
            }
            int v = x.getInt(i);
            if (v == RInt.NA) {
                if (!naRemove) {
                    naorder.add(i);
                } else {
                    nnas++;
                }
            } else {
                order[oi++] = i;
            }
        }
        final IntComparator cmp = new IntComparator(x);
        Comparator<Integer> mainComparator = new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                int res = cmp.cmp(o1, o2);
                return !decreasing ? res : -res;
            }
        };
        Arrays.sort(order, 0, oi, mainComparator);
        return buildSortResults(x, order, oi, naorder, naLast, naRemoveIndex);
    }

    public static RArray sort(RString x, int naLast, final boolean decreasing, boolean indexReturn) {
        int size = x.size();
        Integer[] order = new Integer[size]; // TODO: remove Java boxing through primitive sort methods
        ArrayList<Integer> naorder = new ArrayList<Integer>();  // TODO: replace this by an implementation for primitives
        int[] naRemoveIndex = indexReturn ? new int[size] : null; // maps each index to index if NA/NaNs were removed
        int nnas = 0;
        int oi = 0;
        boolean naRemove = naLast == RInt.NA;
        for (int i = 0; i < size; i++) {
            if (naRemoveIndex != null) {
                naRemoveIndex[i] = i - nnas;
            }
            String v = x.getString(i);
            if (v == RString.NA) {
                if (!naRemove) {
                    naorder.add(i);
                } else {
                    nnas++;
                }
            } else {
                order[oi++] = i;
            }
        }
        final StringComparator cmp = new StringComparator(x);
        Comparator<Integer> mainComparator = new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                int res = cmp.cmp(o1, o2);
                return !decreasing ? res : -res;
            }
        };
        Arrays.sort(order, 0, oi, mainComparator);
        return buildSortResults(x, order, oi, naorder, naLast, naRemoveIndex);
    }

    public static RArray sort(RLogical x, int naLast, final boolean decreasing, boolean indexReturn) {
        int size = x.size();
        Integer[] order = new Integer[size]; // TODO: remove Java boxing through primitive sort methods
        ArrayList<Integer> naorder = new ArrayList<Integer>();  // TODO: replace this by an implementation for primitives
        int[] naRemoveIndex = indexReturn ? new int[size] : null; // maps each index to index if NA/NaNs were removed
        int nnas = 0;
        int oi = 0;
        boolean naRemove = naLast == RInt.NA;
        for (int i = 0; i < size; i++) {
            if (naRemoveIndex != null) {
                naRemoveIndex[i] = i - nnas;
            }
            int v = x.getLogical(i);
            if (v == RLogical.NA) {
                if (!naRemove) {
                    naorder.add(i);
                } else {
                    nnas++;
                }
            } else {
                order[oi++] = i;
            }
        }
        final LogicalComparator cmp = new LogicalComparator(x);
        Comparator<Integer> mainComparator = new Comparator<Integer>() {

            @Override public int compare(Integer o1, Integer o2) {
                int res = cmp.cmp(o1, o2);
                return !decreasing ? res : -res;
            }
        };
        Arrays.sort(order, 0, oi, mainComparator);
        return buildSortResults(x, order, oi, naorder, naLast, naRemoveIndex);
    }

    private static final RArray.Names resultNames = RArray.Names.create(new RSymbol[]{RSymbol.getSymbol("x"), RSymbol.getSymbol("ix")});

    // naorder will be empty whenever naLast == RInt.NA
    public static RArray buildSortResults(RArray x, Integer[] order, int orderLen, ArrayList<Integer> naorder, int naLast, int[] naRemoveIndex) {
        int nalen = naorder.size();
        int size = x.size();

        RArray.Names names = x.names();
        RSymbol[] symbols = names == null ? null : names.sequence();

        RArray res;
        if (nalen == 0) {
            res = Utils.createArray(x, orderLen); // drop attributes
            if (symbols == null) {
                for (int i = 0; i < orderLen; i++) {
                    int xi = order[i];
                    res.set(i, x.get(xi));
                }
            } else {
                RSymbol[] nsymbols = new RSymbol[orderLen];
                for (int i = 0; i < orderLen; i++) {
                    int xi = order[i];
                    res.set(i, x.get(xi));
                    nsymbols[i] = symbols[xi];
                }
                res = res.setNames(RArray.Names.create(nsymbols));
            }
        } else {
            // nalen != 0

            int naStart;
            int dataStart;

            if (naLast == RLogical.TRUE) {
                naStart = orderLen;
                dataStart = 0;
            } else {
                assert Utils.check(naLast == RLogical.FALSE); // RLogical.NA => nalen == 0
                naStart = 0;
                dataStart = nalen;
            }

            res = Utils.createArray(x, size); // drop attributes
            RSymbol[] nsymbols = symbols == null ? null : new RSymbol[size];

            for (int i = 0; i < orderLen; i++) {
                int xi = order[i];
                int resi = i + dataStart;
                res.set(resi, x.get(xi));
                if (nsymbols != null) {
                    nsymbols[resi] = symbols[xi];
                }
            }

            for(int i = 0; i < nalen; i++) {
                int xi = naorder.get(i);
                int resi = i + naStart;
                res.set(resi, x.get(xi));
                if (nsymbols != null) {
                    nsymbols[resi] = symbols[xi];
                }
            }
            if (nsymbols != null) {
                res = res.setNames(RArray.Names.create(nsymbols));
            }
        }

        if (naRemoveIndex != null) {
            // index.return is true
            assert Utils.check(naLast == RInt.NA); // though we could support a more general case than GNU-R

            int[] ix = new int[orderLen];
            for (int i = 0; i < orderLen; i++) {
                int xi = order[i];
                int corrected = naRemoveIndex[xi];
                ix[i] = corrected + 1; // 1-based
            }
            RList list = RList.RListFactory.getFor(new RAny[] {res,  RInt.RIntFactory.getFor(ix)}, null, resultNames);
            return list;
        }

        return res;
    }

    public static double[] fixNAs(double[] a, int naLast) {
        // all NAs and NaNs will be at the end of the array
        if (naLast == RLogical.TRUE) {
            return a;
        }
        int nna = 0;
        for (int i = a.length - 1; i >= 0 ; i--) {
            if (RDouble.RDoubleUtils.isNAorNaN(a[i])) {
                nna++;
            } else {
                break;
            }
        }
        if (nna == 0) {
            return a;
        }

        if (naLast == RLogical.NA) {
            double[] res = new double[a.length - nna];
            System.arraycopy(a, 0, res, 0, res.length);
            return res;
        }
        // naLast == RLogical.FALSE)

        double[] tmp = new double[nna]; // we need tmp because we need to keep the distinction between NA and NaN
        System.arraycopy(a, a.length-nna, tmp, 0, nna);
        System.arraycopy(a, 0, a, nna, a.length - nna);
        System.arraycopy(tmp, 0, a, 0, nna);
        return a;
    }

    public static int[] fixNAs(int[] a, int naLast) {
        // all NAs will be at the beginning of the array, because RInt.NA is the smallest integer
        if (naLast == RLogical.FALSE) {
            return a;
        }
        int nna = 0;
        for (int i = 0; i < a.length ; i++) {
            if (a[i] == RInt.NA) {
                nna++;
            } else {
                break;
            }
        }
        if (nna == 0) {
            return a;
        }

        if (naLast == RLogical.NA) {
            int[] res = new int[a.length - nna];
            System.arraycopy(a, nna, res, 0, res.length);
            return res;
        }

        // naLast == RLogical.TRUE
        System.arraycopy(a, nna, a, 0, a.length - nna);
        Arrays.fill(a, a.length - nna, a.length, RInt.NA);
        return a;
    }

}
