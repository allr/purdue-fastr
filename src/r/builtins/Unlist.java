package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "unlist"
 * 
 * <pre>
 * x -- an R object, typically a list or vector.
 * recursive -- logical. Should unlisting be applied to list components of x?
 * use.names -- logical. Should names be preserved?
 * </pre>
 */
// TODO: add optimized nodes, node-rewriting
// FIXME: some of this code should be refactored into more general classes (e.g. finding a common subtype, cast mixins
final class Unlist extends CallFactory {

    static final CallFactory _ = new Unlist("unlist", new String[]{"x", "recursive", "use.names"}, new String[]{"x"});

    Unlist(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseLogical(RAny arg) {
        // FIXME: most likely not exactly R semantics with evaluation of additional values
        RLogical larg = arg.asLogical();
        int size = larg.size();
        if (size > 0) {
            int v = larg.getLogical(0);
            if (v == RLogical.TRUE) { return true; }
        }
        return false;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posX = ia.position("x");
        final int posRecursive = ia.provided("recursive") ? ia.position("recursive") : -1;
        final int posUseNames = ia.provided("use.names") ? ia.position("use.names") : -1;
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny x = args[posX];
                boolean recursive = posRecursive != -1 ? parseLogical(args[posRecursive]) : true;
                boolean useNames = posUseNames != -1 ? parseLogical(args[posUseNames]) : true;
                return genericUnlist(x, recursive, useNames, ast);
            }
        };
    }

    public static RAny genericUnlist(RAny x, boolean recursive, boolean useNames, ASTNode ast) {
        if (x instanceof RList) { return genericUnlist((RList) x, recursive, useNames); }
        if (x instanceof RArray || x instanceof RNull) { return x; }
        throw RError.getArgumentNotList(ast);
    }

    public static class AnalyzeList {

        public boolean hasList;
        public boolean hasNull; // maybe not needed
        public boolean hasNames;

        public boolean hasString;
        public boolean hasComplex;
        public boolean hasDouble;
        public boolean hasInt;
        public boolean hasLogical;
        public boolean hasRaw;
        public int size;

        private boolean recursive;
        private boolean useNames;
        private static final AnalyzeList instance = new AnalyzeList();

        public static AnalyzeList create(boolean recursive, boolean useNames) {
            instance.hasList = false;
            instance.hasNull = false;
            instance.hasNames = false;
            instance.hasString = false;
            instance.hasComplex = false;
            instance.hasDouble = false;
            instance.hasInt = false;
            instance.hasLogical = false;
            instance.hasRaw = false;
            instance.size = 0;
            instance.recursive = recursive;
            instance.useNames = useNames;
            return instance;
        }

        public static AnalyzeList analyze(RList x, boolean recursive, boolean useNames) {
            AnalyzeList res = AnalyzeList.create(recursive, useNames);
            res.analyze(x);
            return res;
        }

        private void analyze(RList x) { // FIXME: the code is very similar to that in Combine
            int xsize = x.size();
            for (int i = 0; i < xsize; i++) {
                RAny v = x.getRAny(i);

                if (v instanceof RNull) {
                    hasNull = true;
                    continue; // NOTE: size not incremented
                }
                if (v instanceof RList) {
                    if (recursive) {
                        RList l = (RList) v;
                        if (useNames && l.names() != null) {
                            hasNames = true;
                        }
                        analyze(l);
                        continue;
                    }
                    hasList = true;
                } else if (v instanceof RString) {
                    hasString = true;
                } else if (v instanceof RComplex) {
                    hasComplex = true;
                } else if (v instanceof RDouble) {
                    hasDouble = true;
                } else if (v instanceof RInt) {
                    hasInt = true;
                } else if (v instanceof RLogical) {
                    hasLogical = true;
                } else if (v instanceof RRaw) {
                    hasRaw = true;
                } else {
                    Utils.nyi("unsupported type");
                    return;
                }
                RArray a = (RArray) v;
                size += a.size();
                if (useNames && a.names() != null) {
                    hasNames = true;
                }
            }
        }
    }

    public abstract static class Cast { // FIXME: this could go somewhere else
        public abstract RArray cast(RAny src);

        public static final Cast STRING = new Cast() {
            @Override public RArray cast(RAny src) {
                return src.asString();
            }
        };

        public static final Cast COMPLEX = new Cast() {
            @Override public RArray cast(RAny src) {
                return src.asComplex();
            }
        };

        public static final Cast DOUBLE = new Cast() {
            @Override public RArray cast(RAny src) {
                return src.asDouble();
            }
        };

        public static final Cast INT = new Cast() {
            @Override public RArray cast(RAny src) {
                return src.asInt();
            }
        };

        public static final Cast LOGICAL = new Cast() {
            @Override public RArray cast(RAny src) {
                return src.asLogical();
            }
        };

        public static final Cast RAW = new Cast() {
            @Override public RArray cast(RAny src) {
                return src.asRaw();
            }
        };
    }

    // returns the number of things added to content
    public static void fill(RArray target, RSymbol[] targetSymbols, RAny x, Cast cast) {
        if (targetSymbols == null) {
            fill(target, 0, x, cast);
        } else {
            fill(target, targetSymbols, "", 0, x, cast);
        }
    }

    public static int fill(RArray target, int offset, RAny x, Cast cast) {
        if (x instanceof RNull) { return offset; }
        int noffset = offset;
        if (x instanceof RList) {
            RList l = (RList) x;
            int lsize = l.size();
            for (int i = 0; i < lsize; i++) {
                RAny v = l.getRAnyRef(i);
                noffset = fill(target, noffset, v, cast); // recursion
            }
            return noffset;
        }
        RArray typedX = cast.cast(x);
        int xsize = typedX.size();
        for (int i = 0; i < xsize; i++) {
            target.set(noffset++, typedX.get(i));
        }
        return noffset;
    }

    public static int fill(RArray target, RSymbol[] targetSymbols, String prefix, int offset, RAny x, Cast cast) {
        if (x instanceof RNull) { return offset; }
        int noffset = offset;
        RArray.Names names = ((RArray) x).names();
        RSymbol[] symbols = names == null ? null : names.sequence();
        boolean emptyPrefix = prefix.length() == 0;

        if (x instanceof RList) {
            RList l = (RList) x;
            int lsize = l.size();
            for (int i = 0; i < lsize; i++) {
                RAny v = l.getRAnyRef(i);
                String nprefix;
                if (symbols == null || symbols[i] == RSymbol.EMPTY_SYMBOL) { // could extract to a function
                    if (!emptyPrefix && lsize > 1) {
                        nprefix = prefix + (i + 1);
                    } else {
                        nprefix = prefix;
                    }
                } else {
                    if (!emptyPrefix) {
                        nprefix = prefix + "." + Convert.prettyNA(symbols[i].pretty());
                    } else {
                        nprefix = Convert.prettyNA(symbols[i].pretty());
                    }
                }
                noffset = fill(target, targetSymbols, nprefix, noffset, v, cast); // recursion
            }
            return noffset;
        }
        RArray typedX = cast.cast(x);
        int xsize = typedX.size();
        for (int i = 0; i < xsize; i++) {
            target.set(noffset, typedX.get(i));
            String sname;
            if (symbols == null || symbols[i] == RSymbol.EMPTY_SYMBOL) { // could extract to a function
                if (!emptyPrefix && xsize > 1) {
                    sname = prefix + (i + 1);
                } else {
                    sname = prefix;
                }
            } else {
                if (!emptyPrefix) {
                    sname = prefix + "." + Convert.prettyNA(symbols[i].pretty());
                } else {
                    sname = Convert.prettyNA(symbols[i].pretty());
                }
            }
            targetSymbols[noffset] = RSymbol.getSymbol(sname);
            noffset++;
        }
        return noffset;
    }

    public static RAny genericUnlist(RList x, boolean recursive, boolean useNames) {

        RAny res = speculativeUnlist(x, useNames);
        if (res != null) { return res; }

        if (!recursive) {
            // NOTE: yes, non-recursive unlist still removes top-level lists
            return C.genericCombine(useNames ? x.names().sequence() : null, x.materialize().getContent(), !useNames);
        }

        AnalyzeList alist = AnalyzeList.analyze(x, recursive, useNames);

        // non-recursive version has no lists
        assert Utils.check(!alist.hasList);

        int size = alist.size;
        boolean needsNames = useNames && alist.hasNames;
        RSymbol[] symbols = needsNames ? new RSymbol[size] : null;

        RArray target;
        if (alist.hasString) {
            target = RString.RStringFactory.getUninitializedArray(size);
            fill(target, symbols, x, Cast.STRING);
        } else if (alist.hasComplex) {
            target = RComplex.RComplexFactory.getUninitializedArray(size);
            fill(target, symbols, x, Cast.COMPLEX);
        } else if (alist.hasDouble) {
            target = RDouble.RDoubleFactory.getUninitializedArray(size);
            fill(target, symbols, x, Cast.DOUBLE);
        } else if (alist.hasInt) {
            target = RInt.RIntFactory.getUninitializedArray(size);
            fill(target, symbols, x, Cast.INT);
        } else if (alist.hasLogical) {
            target = RLogical.RLogicalFactory.getUninitializedArray(size);
            fill(target, symbols, x, Cast.LOGICAL);
        } else if (alist.hasRaw) {
            target = RRaw.RRawFactory.getUninitializedArray(size);
            fill(target, symbols, x, Cast.RAW);
        } else {
            Utils.nyi("unsupported case");
            return null;
        }
        return needsNames ? target.setNames(RArray.Names.create(symbols)) : target;
    }

    // speculate all elements are scalars of the same (selected array) type
    public static RAny speculativeUnlist(RList x, boolean useNames) {

        int xsize = x.size();
        if (xsize == 0) { return RNull.getNull(); }
        RAny xfirst = x.getRAny(0);
        RArray.Names names = x.names();

        // FIXME: create also specialized nodes for these case to reduce the number of branches
        if (xfirst instanceof ScalarDoubleImpl) {
            double[] content = new double[xsize];
            for (int i = 0; i < xsize; i++) {
                RAny elem = x.getRAny(i);
                if (elem instanceof ScalarDoubleImpl) {
                    content[i] = ((ScalarDoubleImpl) elem).getDouble();
                } else {
                    return null;
                }
            }
            return RDouble.RDoubleFactory.getFor(content, null, useNames ? names : null);
        }
        if (xfirst instanceof ScalarIntImpl) {
            int[] content = new int[xsize];
            for (int i = 0; i < xsize; i++) {
                RAny elem = x.getRAny(i);
                if (elem instanceof ScalarIntImpl) {
                    content[i] = ((ScalarIntImpl) elem).getInt();
                } else {
                    return null;
                }
            }
            return RInt.RIntFactory.getFor(content, null, useNames ? names : null);
        }
        if (xfirst instanceof ScalarStringImpl) {
            String[] content = new String[xsize];
            for (int i = 0; i < xsize; i++) {
                RAny elem = x.getRAny(i);
                if (elem instanceof ScalarStringImpl) {
                    content[i] = ((ScalarStringImpl) elem).getString();
                } else {
                    return null;
                }
            }
            return RString.RStringFactory.getFor(content, null, useNames ? names : null);
        }
        return null; // no speculative result
    }
}
