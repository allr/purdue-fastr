package r.nodes.exec;

import r.*;
import r.data.*;
import r.data.RArray.Names;
import r.data.RLogical.RLogicalFactory;
import r.data.internal.*;
import r.data.internal.View.RLogicalView;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

// FIXME: update debugs for new specializations
// FIXME: add more scalar specializations
// TODO: add node rewriting to scalar/vector and vector/scalar specializations

public class Comparison extends BaseR {

    final ValueComparison cmp;
    @Child RNode left;
    @Child RNode right;


    private static final boolean LAZY_COMPARISON_IN_VECTOR_INDEX = false;
    // surprisingly, this is not helping, it prevents allocation but overall is more expensive

    private static final boolean DEBUG_CMP = false;

    public Comparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
        this.cmp = cmp;
    }

    @Override
    public final int executeScalarLogical(Frame frame) throws SpecializationException {
        assert Utils.check(getNewNode() == null);
        RNode oldleft = left;
        RAny lexpr = (RAny) left.execute(frame);
        if (getNewNode() != null) {
            return ((Comparison) getNewNode()).executeScalarLogicalWithLeft(frame, lexpr);
        }
        return executeScalarLogicalWithLeft(frame, lexpr);
    }

    public final int executeScalarLogicalWithLeft(Frame frame, RAny lexpr) throws SpecializationException {
        RAny rexpr = (RAny) right.execute(frame);
        if (getNewNode() != null) {
            return ((Comparison) getNewNode()).executeScalarLogical(lexpr, rexpr);
        }
        return executeScalarLogical(lexpr, rexpr);
    }

    public int executeScalarLogical(RAny lexpr, RAny rexpr) throws SpecializationException {
        try {
            throw new SpecializationException(null);
        } catch (SpecializationException e) {
            ScalarComparison sc = ScalarComparison.createSpecialized(lexpr, rexpr, ast, left, right, cmp);
            replace(sc, "install ScalarComparison.Specialized from Comparison");
            return sc.executeScalarLogical(lexpr, rexpr);
        }
    }

    @Override
    public final Object execute(Frame frame) {
        try {
            return RLogical.RLogicalFactory.getScalar(executeScalarLogical(frame));
        } catch (SpecializationException e) {
            return e.getResult();
        }
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (left == oldNode) {
            left = newNode;
            return adoptInternal(newNode);
        }
        if (right == oldNode) {
            right = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    public Object execute(RAny lexpr, RAny rexpr) {
        try {
            return RLogical.RLogicalFactory.getScalar(executeScalarLogical(lexpr, rexpr));
        } catch (SpecializationException e) {
            return e.getResult();
        }
    }

    static class ScalarComparison extends Comparison {
        final Comparator comp;

        public ScalarComparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp, Comparator comp) {
            super(ast, left, right, cmp);
            this.comp = comp;
        }

        public abstract static class Comparator {
            public abstract int compare(RAny lexpr, RAny rexpr) throws SpecializationException;
        }

        enum Transition {
            COMMON_SCALAR,
            VECTOR_SCALAR
        }

        public static ScalarComparison createSpecialized(RAny leftTemplate, RAny rightTemplate, ASTNode ast, RNode left, RNode right, final ValueComparison cmp) {
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(Transition.COMMON_SCALAR);
                        }
                        double l = ((ScalarDoubleImpl) lexpr).getDouble();
                        double r = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (RDouble.RDoubleUtils.isNAorNaN(r) || RDouble.RDoubleUtils.isNAorNaN(l)) {
                            return RLogical.NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarIntImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new SpecializationException(Transition.COMMON_SCALAR);
                        }
                        int l = ((ScalarIntImpl) lexpr).getInt();
                        int r = ((ScalarIntImpl) rexpr).getInt();
                        if (l == RInt.NA || r == RInt.NA) {
                            return RLogical.NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarIntImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new SpecializationException(Transition.COMMON_SCALAR);
                        }
                        double l = ((ScalarDoubleImpl) lexpr).getDouble();
                        int r = ((ScalarIntImpl) rexpr).getInt();
                        if (RDouble.RDoubleUtils.isNAorNaN(l) || r == RInt.NA) {
                            return RLogical.NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new SpecializationException(Transition.COMMON_SCALAR);
                        }
                        int l = ((ScalarIntImpl) lexpr).getInt();
                        double r = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (l == RInt.NA || RDouble.RDoubleUtils.isNAorNaN(r)) {
                            return RLogical.NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof ScalarIntImpl && rightTemplate instanceof ScalarLogicalImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarLogicalImpl)) {
                            throw new SpecializationException(Transition.COMMON_SCALAR);
                        }
                        int l = ((ScalarIntImpl) lexpr).getInt();
                        int r = ((ScalarLogicalImpl) rexpr).getLogical();
                        if (l == RInt.NA || r == RLogical.NA) {
                            return RLogical.NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof ScalarLogicalImpl && rightTemplate instanceof ScalarIntImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                        if (!(lexpr instanceof ScalarLogicalImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new SpecializationException(Transition.COMMON_SCALAR);
                        }
                        int l = ((ScalarLogicalImpl) lexpr).getLogical();
                        int r = ((ScalarIntImpl) rexpr).getInt();
                        if (l == RLogical.NA || r == RInt.NA) {
                            return RLogical.NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            // other type combinations handled in generic case
            return createGeneric(ast, left, right, cmp);
        }

        public static int generic(RAny lexpr, RAny rexpr, ValueComparison cmp, ASTNode ast) throws SpecializationException {
            if (DEBUG_CMP) Utils.debug("comparison - assuming scalar numbers");

            if (lexpr instanceof ScalarStringImpl) { // note: could make this shorter if we didn't care about Java-level boxing
                String lstr = ((ScalarStringImpl) lexpr).getString();
                if (lstr == RString.NA) {
                    return RLogical.NA;
                }
                if (rexpr instanceof ScalarStringImpl) {
                    String rstr = ((ScalarStringImpl) rexpr).getString();
                    if (rstr == RString.NA) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(lstr, rstr) ? RLogical.TRUE : RLogical.FALSE;
                }
            } else if (lexpr instanceof ScalarDoubleImpl) {
                double ldbl = ((ScalarDoubleImpl) lexpr).getDouble();
                if (rexpr instanceof ScalarDoubleImpl) {
                    double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                    if (RDouble.RDoubleUtils.isNAorNaN(ldbl) || RDouble.RDoubleUtils.isNAorNaN(rdbl)) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(ldbl, rdbl) ? RLogical.TRUE : RLogical.FALSE;
                } else if (rexpr instanceof ScalarIntImpl) {
                    int rint = ((ScalarIntImpl) rexpr).getInt();
                    if (RDouble.RDoubleUtils.isNAorNaN(ldbl) || rint == RInt.NA) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(ldbl, rint) ? RLogical.TRUE : RLogical.FALSE;
                }
            } else if (lexpr instanceof ScalarIntImpl) {
                int lint = ((ScalarIntImpl) lexpr).getInt();
                if (rexpr instanceof ScalarIntImpl) {
                    int rint = ((ScalarIntImpl) rexpr).getInt();
                    if (lint == RInt.NA || rint == RInt.NA) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(lint, rint) ? RLogical.TRUE : RLogical.FALSE;
                } else if (rexpr instanceof ScalarDoubleImpl) {
                    double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                    if (lint == RInt.NA || RDouble.RDoubleUtils.isNAorNaN(rdbl)) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(lint, rdbl) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            throw new SpecializationException(Transition.VECTOR_SCALAR);
        }

        public static ScalarComparison createGeneric(final ASTNode ast, RNode left, RNode right, final ValueComparison cmp) {
            Comparator c = new Comparator() {
                @Override
                public int compare(RAny lexpr, RAny rexpr) throws SpecializationException {
                    return generic(lexpr, rexpr, cmp, ast);
                }
            };
            return new ScalarComparison(ast, left, right, cmp, c);
        }

        @Override
        public final int executeScalarLogical(RAny lexpr, RAny rexpr) throws SpecializationException {
            try {
                return comp.compare(lexpr, rexpr);
            } catch (SpecializationException e) {
                Transition t = (Transition) e.getResult();
                if (t == Transition.COMMON_SCALAR) {
                    ScalarComparison sc = createGeneric(ast, left, right, cmp);
                    replace(sc, "install CommonScalar from Comparison.Scalar");
                    return sc.executeScalarLogical(lexpr, rexpr);
                } else {
                    if (DEBUG_CMP) Utils.debug("comparison - optimistic comparison failed, values are not scalar numbers");
                    if (LAZY_COMPARISON_IN_VECTOR_INDEX && isPartOfArrayIndex(ast)) {
                        LazyComparison ln = new LazyComparison(ast, left, right, cmp);
                        replace(ln, "installLazyComparison");
                        throw new SpecializationException(ln.execute(lexpr, rexpr));
                    }
                    VectorScalarComparison vs = new VectorScalarComparison(ast, left, right, cmp);
                    replace(vs, "specializeNumericVectorScalarComparison");
                    Object res = vs.execute(lexpr, rexpr);
                    throw new SpecializationException(res);
                }
            }
        }
    }

    static class VectorScalarComparison extends Comparison {

        public VectorScalarComparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp) {
            super(ast, left, right, cmp);
        }

        @Override
        public Object execute(RAny lexpr, RAny rexpr) {
            // FIXME: some of these checks should be rewritten as we now enforce scalar representation
            try {  // FIXME: perhaps should create different nodes for the cases below
                if (DEBUG_CMP) Utils.debug("comparison - assuming numeric (int,double) vector and scalar");
                // we assume that double vector against double scalar is the most common case
                if (lexpr instanceof RDouble) {
                    RDouble ldbl = (RDouble) lexpr;
                    if (rexpr instanceof RDouble) {
                        RDouble rdbl = (RDouble) rexpr;
                        if (rdbl.size() == 1) {
                            if (ldbl.size() >= 1 && rdbl.dimensions() == null) {
                                return cmp.cmp(ldbl, rdbl.getDouble(0));
                            } else {
                                throw new SpecializationException(null);
                            }
                        } else {
                            if (rdbl.size() > 1 && ldbl.size() == 1 && ldbl.dimensions() == null) {
                                return cmp.cmp(ldbl.getDouble(0), rdbl);
                            } else {
                                throw new SpecializationException(null);
                            }
                        }
                    }
                }
                // we assume that integer vector against integer scalar is the second most common case
                if (lexpr instanceof RInt) {
                    RInt lint = (RInt) lexpr;
                    if (rexpr instanceof RInt) {
                        RInt rint = (RInt) rexpr;
                        if (rint.size() == 1) {
                            if (lint.size() >= 1 && rint.dimensions() == null) {
                                return cmp.cmp(lint, rint.getInt(0));
                            } else {
                                throw new SpecializationException(null);
                            }
                        } else {
                            if (rint.size() > 1 && lint.size() == 1 && lint.dimensions() == null) {
                                return cmp.cmp(lint.getInt(0), rint);
                            } else {
                                throw new SpecializationException(null);
                            }
                        }
                    }
                }
                // now we know that one of the argument is not double and one is not integer
                if (lexpr instanceof RString || rexpr instanceof RString) {
                    RString lstr = lexpr.asString();
                    RString rstr = rexpr.asString();
                    if (rstr.size() == 1 && rstr.dimensions() == null) {
                        return cmp.cmp(lstr, rstr.getString(0));
                    } else if (lstr.size() == 1 && lstr.dimensions() == null) {
                        return cmp.cmp(lstr.getString(0), rstr);
                    } else {
                        throw new SpecializationException(null);
                    }
                }
                if (lexpr instanceof RComplex || rexpr instanceof RComplex) {
                    throw new SpecializationException(null); // we assume complex comparisons are rare, so use generic case
                }
                if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                    RDouble ldbl = lexpr.asDouble();
                    RDouble rdbl = rexpr.asDouble();
                    if (rdbl.size() == 1 && rdbl.dimensions() == null) {
                        return cmp.cmp(ldbl, rdbl.getDouble(0));
                    } else if (ldbl.size() == 1 && ldbl.dimensions() == null) {
                        return cmp.cmp(ldbl.getDouble(0), rdbl);
                    } else {
                        throw new SpecializationException(null);
                    }
                }
                if (lexpr instanceof RInt || rexpr instanceof RInt) {
                    RInt lint = lexpr.asInt();
                    RInt rint = rexpr.asInt();
                    if (rint.size() == 1 && rint.dimensions() == null) {
                        return cmp.cmp(lint,  rint.getInt(0));
                    } else if (lint.size() == 1 && lint.dimensions() == null) {
                        return cmp.cmp(lint.getInt(0), rint);
                    } else {
                        throw new SpecializationException(null);
                    }
                }
                // logicals and raws are expected to be less frequent, hence handled in GenericComparison
                throw new SpecializationException(null);

            } catch (SpecializationException e) {
                if (DEBUG_CMP) Utils.debug("comparison - 2nd level comparison failed (not int,double scalar and vector)");
                GenericComparison vs = new GenericComparison(ast, left, right, cmp);
                replace(vs, "genericComparison");
                return vs.execute(lexpr, rexpr);
            }
        }
    }

    public static boolean isPartOfArrayIndex(ASTNode ast) {
        ASTNode n = ast;
        while(n != null) {
            ASTNode pn = n.getParent();
            if (pn instanceof AccessVector) {
                AccessVector av = (AccessVector) pn;
                if (av.getVector() != n) {
                    //return true;
                    return false;
                }
            }
            n = pn;
        }
        return false;
    }

    abstract static class LogicalView extends RLogicalView {

        final RArray a; // FIXME: the views have each of the operand twice, templates were observed slow for this in e.g. Arithmetic
        final RArray b;
        final int n;
        final int[] dimensions;
        final Names names;

        final ValueComparison cmp;
        final ASTNode ast;

        public LogicalView(RArray a, RArray b, int n, ValueComparison cmp, ASTNode ast) {
            this.a = a;
            this.b = b;
            this.dimensions =  Arithmetic.resultDimensions(ast, a, b);
            this.names =  Arithmetic.resultNames(ast, a, b);
            this.n = n;
            this.cmp = cmp;
            this.ast = ast;
        }

        @Override
        public final int size() {
            return n;
        }

        @Override
        public final boolean isSharedReal() {
            return a.isShared() || b.isShared();
        }

        @Override
        public final void ref() {
            a.ref();
            b.ref();
        }

        @Override
        public final int[] dimensions() {
            return dimensions;
        }

        @Override
        public final Names names() {
            return names;
        }

        @Override
        public final boolean dependsOn(RAny value) {
            return a.dependsOn(value) || b.dependsOn(value);
        }
    }

    static class LazyComparison extends Comparison {
        public LazyComparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp) {
            super(ast, left, right, cmp);
        }

        @Override
        public Object execute(RAny lexpr, RAny rexpr) {
            try {
                if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                    final RDouble adbl = lexpr.asDouble();
                    final RDouble bdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned

                    int na = adbl.size();
                    int nb = bdbl.size();
                    if (na > 1 && nb == 1) {

                        final double bconst = bdbl.getDouble(0);
                        if (RDouble.RDoubleUtils.isNAorNaN(bconst)) {
                            return RLogicalFactory.getNAArray(na, adbl.dimensions());
                        }
                        if (cmp.resultForNaN() == false) {
                            return new LogicalView(adbl, bdbl, na, cmp, ast) {

                                @Override
                                public int getLogical(int i) {
                                    double aval = adbl.getDouble(i);
                                    if (cmp.cmp(aval, bconst)) {
                                        return RLogical.TRUE;
                                    } else {
                                        return RDouble.RDoubleUtils.isNAorNaN(aval) ? RLogical.NA : RLogical.FALSE;
                                    }
                                }
                            };
                        }

                    }
                }
                throw new SpecializationException(null);
            } catch(SpecializationException e) {
                GenericComparison vs = new GenericComparison(ast, left, right, cmp);
                replace(vs, "genericComparison");
                return vs.execute(lexpr, rexpr);
            }
        }
    }

    static class GenericComparison extends Comparison {

        public GenericComparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp) {
            super(ast, left, right, cmp);
        }

        @Override
        public Object execute(RAny lexpr, RAny rexpr) {
            if (DEBUG_CMP) Utils.debug("comparison - the most generic case");
            if (lexpr instanceof RString || rexpr instanceof RString) {
                RString lstr = lexpr.asString();
                RString rstr = rexpr.asString();
                return cmp.cmp(lstr, rstr, ast);
            }
            if (lexpr instanceof RComplex || rexpr instanceof RComplex) {
                RComplex lcmp = lexpr.asComplex();
                RComplex rcmp = rexpr.asComplex();  // if the cast fails, a zero-length array is returned
                return cmp.cmp(lcmp, rcmp, ast);
            }
            if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                RDouble ldbl = lexpr.asDouble();
                RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                return cmp.cmp(ldbl, rdbl, ast);
            }
            if (lexpr instanceof RInt || rexpr instanceof RInt) {
                RInt lint = lexpr.asInt();
                RInt rint = rexpr.asInt();
                return cmp.cmp(lint, rint, ast);
            }
            if (lexpr instanceof RLogical || rexpr instanceof RLogical) {
                RLogical llog = lexpr.asLogical();
                RLogical rlog = rexpr.asLogical();
                return cmp.cmp(llog, rlog, ast);
            }
            if (lexpr instanceof RRaw || rexpr instanceof RRaw) {
                RRaw lraw = lexpr.asRaw();
                RRaw rraw = rexpr.asRaw();
                return cmp.cmp(lraw, rraw, ast);
            }
            Utils.nyi("unsupported case for comparison");
            return null;
        }

    }

    public abstract static class ValueComparison {
        public abstract boolean cmp(byte a, byte b);
        public abstract boolean cmp(int a, int b);
        public abstract boolean cmp(double a, double b);
        public abstract boolean cmp(double areal, double aimag, double breal, double bimag);
        public abstract boolean cmp(String a, String b);
        public abstract boolean resultForNaN();

        public boolean cmp(int a, double b) {
            return cmp((double) a, b);
        }
        public boolean cmp(double a, int b) {
            return cmp(a, (double) b);
        }

        public RLogical cmp(RString a, String b) {
            int n = a.size();
            if (b == RString.NA) {
                return RLogicalFactory.getNAArray(n, a.dimensions());
            }
            int[] content = new int[n];
            for (int i = 0; i < n; i++) {
                String astr = a.getString(i);
                if (astr == RString.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(astr, b) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            return RLogical.RLogicalFactory.getFor(content, a.dimensions(), a.names());
        }
        public RLogical cmp(String a, RString b) {
            int n = b.size();
            if (a == RString.NA) {
                return RLogicalFactory.getNAArray(n, b.dimensions());
            }
            int[] content = new int[n];
            for (int i = 0; i < n; i++) {
                String bstr = b.getString(i);
                if (bstr == RString.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(a, bstr) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            return RLogical.RLogicalFactory.getFor(content, b.dimensions(), b.names());
        }
        public RLogical cmp(RDouble a, double b) {
            int n = a.size();
            if (RDouble.RDoubleUtils.isNAorNaN(b)) {
                return RLogicalFactory.getNAArray(n, a.dimensions());
            }
            int[] content = new int[n];
            if (resultForNaN() == false) {
                for (int i = 0; i < n; i++) {
                    double adbl = a.getDouble(i);
                    if (cmp(adbl, b)) {
                        content[i] = RLogical.TRUE;
                    } else {
                        // FIXME: it is ridiculous but it helps to hand-inline this (e.g. b25-prog3)
                        content[i] = /* RDouble.RDoubleUtils.isNAorNaN(adbl) */ adbl != adbl ? RLogical.NA : RLogical.FALSE;
                    }
                }
            } else {
                for (int i = 0; i < n; i++) {
                    double adbl = a.getDouble(i);
                    if (cmp(adbl, b)) {
                        content[i] = /* RDouble.RDoubleUtils.isNAorNaN(adbl) */ adbl != adbl ? RLogical.NA : RLogical.TRUE;
                    } else {
                        content[i] = RLogical.FALSE;
                    }
                }

            }
            return RLogical.RLogicalFactory.getFor(content, a.dimensions(), a.names());
        }
        public RLogical cmp(double a, RDouble b) {
            int n = b.size();
            if (RDouble.RDoubleUtils.isNAorNaN(a)) {
                return RLogicalFactory.getNAArray(n, b.dimensions());
            }
            int[] content = new int[n];
            if (resultForNaN() == false) {
                for (int i = 0; i < n; i++) {
                    double bdbl = b.getDouble(i);
                    if (cmp(a, bdbl)) {
                        content[i] = RLogical.TRUE;
                    } else {
                        content[i] = /* RDouble.RDoubleUtils.isNAorNaN(bdbl) */ bdbl != bdbl ? RLogical.NA : RLogical.FALSE;
                    }
                }
            } else {
                for (int i = 0; i < n; i++) {
                    double bdbl = b.getDouble(i);
                    if (cmp(a, bdbl)) {
                        content[i] = /* RDouble.RDoubleUtils.isNAorNaN(bdbl) */ bdbl != bdbl ? RLogical.NA : RLogical.TRUE;
                    } else {
                        content[i] = RLogical.FALSE;
                    }
                }

            }
            return RLogical.RLogicalFactory.getFor(content, b.dimensions(), b.names());
        }
        public RLogical cmp(RInt a, int b) {
            int n = a.size();
            if (b == RInt.NA) {
                return RLogicalFactory.getNAArray(n, a.dimensions());
            }
            int[] content = new int[n];
            for (int i = 0; i < n; i++) {
                int aint = a.getInt(i);
                if (aint == RInt.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(aint, b) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            return RLogical.RLogicalFactory.getFor(content, a.dimensions(), a.names());
        }
        public RLogical cmp(int a, RInt b) {
            int n = b.size();
            if (a == RInt.NA) {
                return RLogicalFactory.getNAArray(n, b.dimensions());
            }
            int[] content = new int[n];
            for (int i = 0; i < n; i++) {
                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(a, bint) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            return RLogical.RLogicalFactory.getFor(content, b.dimensions(), b.names());
        }

        public RLogical cmp(RString a, RString b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);
            if (na == 0 || nb == 0) {
                return RLogical.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            int[] content = new int[n];
            int ai = 0;
            int bi = 0;

            for (int i = 0; i < n; i++) {
                String astr = a.getString(ai++);
                if (ai == na) {
                    ai = 0;
                }
                String bstr = b.getString(bi++);
                if (bi == nb) {
                    bi = 0;
                }

                if (astr == RString.NA || bstr == RString.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(astr, bstr) ? RLogical.TRUE : RLogical.FALSE;
                }
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }
        public RLogical cmp(RComplex a, RComplex b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);
            if (na == 0 || nb == 0) {
                return RLogical.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            int[] content = new int[n];
            int ai = 0;
            int bi = 0;

            for (int i = 0; i < n; i++) {
                double areal = a.getReal(ai);
                double aimag = a.getImag(ai);
                ai++;
                if (ai == na) {
                    ai = 0;
                }
                double breal = b.getReal(bi);
                double bimag = b.getImag(bi);
                bi++;
                if (bi == nb) {
                    bi = 0;
                }

                if (RComplex.RComplexUtils.eitherIsNA(areal,  aimag) || RComplex.RComplexUtils.eitherIsNA(breal, bimag)) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(areal, aimag, breal, bimag) ? RLogical.TRUE : RLogical.FALSE;
                }
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }

        public RLogical cmp(RDouble a, RDouble b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);
            if (na == 0 || nb == 0) {
                return RLogical.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            int[] content = new int[n];
            int ai = 0;
            int bi = 0;

            if (resultForNaN() == false) {
                for (int i = 0; i < n; i++) {
                    double adbl = a.getDouble(ai++);
                    if (ai == na) {
                        ai = 0;
                    }
                    double bdbl = b.getDouble(bi++);
                    if (bi == nb) {
                        bi = 0;
                    }

                    if (cmp(adbl, bdbl)) {
                        content[i] = RLogical.TRUE;
                    } else {
                        content[i] = /* RDouble.RDoubleUtils.isNAorNaN(bdbl) */ bdbl != bdbl ? RLogical.NA : RLogical.FALSE;
                    }
                }
            } else {
                for (int i = 0; i < n; i++) {
                    double adbl = a.getDouble(ai++);
                    if (ai == na) {
                        ai = 0;
                    }
                    double bdbl = b.getDouble(bi++);
                    if (bi == nb) {
                        bi = 0;
                    }

                    if (cmp(adbl, bdbl)) {
                        content[i] = /* RDouble.RDoubleUtils.isNAorNaN(bdbl) */ bdbl != bdbl ? RLogical.NA : RLogical.TRUE;
                    } else {
                        content[i] = RLogical.FALSE;
                    }
                }
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }
        public RLogical cmp(RInt a, RInt b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);

            if (na == 0 || nb == 0) {
                return RLogical.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            int[] content = new int[n];
            int ai = 0;
            int bi = 0;

            for (int i = 0; i < n; i++) {
                int aint = a.getInt(ai++);
                if (ai == na) {
                    ai = 0;
                }
                int bint = b.getInt(bi++);
                if (bi == nb) {
                    bi = 0;
                }

                if (aint == RInt.NA || bint == RInt.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(aint, bint) ? RLogical.TRUE : RLogical.FALSE;
                }
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }
        public RLogical cmp(RLogical a, RLogical b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);

            if (na == 0 || nb == 0) {
                return RLogical.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            int[] content = new int[n];
            int ai = 0;
            int bi = 0;

            for (int i = 0; i < n; i++) {
                int alog = a.getLogical(ai++);
                if (ai == na) {
                    ai = 0;
                }
                int blog = b.getLogical(bi++);
                if (bi == nb) {
                    bi = 0;
                }

                if (alog == RLogical.NA || blog == RLogical.NA) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(alog, blog) ? RLogical.TRUE : RLogical.FALSE;
                }
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }
        public RLogical cmp(RRaw a, RRaw b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);

            if (na == 0 || nb == 0) {
                return RLogical.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            int[] content = new int[n];
            int ai = 0;
            int bi = 0;

            for (int i = 0; i < n; i++) {
                byte araw = a.getRaw(ai++);
                if (ai == na) {
                    ai = 0;
                }
                byte braw = b.getRaw(bi++);
                if (bi == nb) {
                    bi = 0;
                }
                content[i] = cmp(araw, braw) ? RLogical.TRUE : RLogical.FALSE;
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }
    }

    public static ValueComparison getEQ() {
        return new ValueComparison() {
            @Override
            public boolean cmp(byte a, byte b) {
                return a == b;
            }
            @Override
            public boolean cmp(int a, int b) {
                return a == b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a == b;
            }
            @Override
            public boolean cmp(double areal, double aimag, double breal, double bimag) {
                return areal == breal && aimag == bimag;
            }
            @Override
            public boolean cmp(String a, String b) {
                return a.compareTo(b) == 0; // FIXME: intern?
            }
            @Override
            public boolean resultForNaN() {
                return false;
            }
        };
    }
    public static ValueComparison getNE() {
        return new ValueComparison() {
            @Override
            public boolean cmp(byte a, byte b) {
                return a != b;
            }
            @Override
            public boolean cmp(int a, int b) {
                return a != b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a != b;
            }
            @Override
            public boolean cmp(double areal, double aimag, double breal, double bimag) {
                return areal != breal || aimag != bimag;
            }
            @Override
            public boolean cmp(String a, String b) {
                return a.compareTo(b) != 0; // FIXME: intern?
            }
            @Override
            public boolean resultForNaN() {
                return true;
            }
        };
    }
    public static ValueComparison getLE() {
        return new ValueComparison() {
            @Override
            public boolean cmp(byte a, byte b) {
                return Convert.byteToUnsigned(a) <= Convert.byteToUnsigned(b);
            }
            @Override
            public boolean cmp(int a, int b) {
                return a <= b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a <= b;
            }
            @Override
            public boolean cmp(double areal, double aimag, double breal, double bimag) {
                Utils.nyi();
                return false;
            }
            @Override
            public RLogical cmp(RComplex a, RComplex b, ASTNode ast) {
                throw RError.getComparisonComplex(ast);
            }
            @Override
            public boolean cmp(String a, String b) {
                return a.compareTo(b) <= 0;
            }
            @Override
            public boolean resultForNaN() {
                return false;
            }
        };
    }
    public static ValueComparison getGE() {
        return new ValueComparison() {
            @Override
            public boolean cmp(byte a, byte b) {
                return Convert.byteToUnsigned(a) >= Convert.byteToUnsigned(b);
            }
            @Override
            public boolean cmp(int a, int b) {
                return a >= b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a >= b;
            }
            @Override
            public boolean cmp(double areal, double aimag, double breal, double bimag) {
                Utils.nyi();
                return false;
            }
            @Override
            public RLogical cmp(RComplex a, RComplex b, ASTNode ast) {
                throw RError.getComparisonComplex(ast);
            }
            @Override
            public boolean cmp(String a, String b) {
                return a.compareTo(b) >= 0;
            }
            @Override
            public boolean resultForNaN() {
                return false;
            }
        };
    }
    public static ValueComparison getLT() {
        return new ValueComparison() {
            @Override
            public boolean cmp(byte a, byte b) {
                return Convert.byteToUnsigned(a) < Convert.byteToUnsigned(b);
            }
            @Override
            public boolean cmp(int a, int b) {
                return a < b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a < b;
            }
            @Override
            public boolean cmp(double areal, double aimag, double breal, double bimag) {
                Utils.nyi();
                return false;
            }
            @Override
            public RLogical cmp(RComplex a, RComplex b, ASTNode ast) {
                throw RError.getComparisonComplex(ast);
            }
            @Override
            public boolean cmp(String a, String b) {
                return a.compareTo(b) < 0;
            }
            @Override
            public boolean resultForNaN() {
                return false;
            }
        };
    }
    public static ValueComparison getGT() {
        return new ValueComparison() {
            @Override
            public boolean cmp(byte a, byte b) {
                return Convert.byteToUnsigned(a) > Convert.byteToUnsigned(b);
            }
            @Override
            public boolean cmp(int a, int b) {
                return a > b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a > b;
            }
            @Override
            public boolean cmp(double areal, double aimag, double breal, double bimag) {
                Utils.nyi();
                return false;
            }
            @Override
            public RLogical cmp(RComplex a, RComplex b, ASTNode ast) {
                throw RError.getComparisonComplex(ast);
            }
            @Override
            public boolean cmp(String a, String b) {
                return a.compareTo(b) > 0;
            }
            @Override
            public boolean resultForNaN() {
                return false;
            }
        };
    }
}
