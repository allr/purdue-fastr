package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.RArray.Names;
import r.data.RLogical.RLogicalFactory;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

// FIXME: update debugs for new specializations
// FIXME: add more scalar specializations

public class Comparison extends BaseR {

    final ValueComparison cmp;
    @Child RNode left;
    @Child RNode right;

    private static final boolean DEBUG_CMP = false;

    public Comparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
        this.cmp = cmp;
    }

    @Override
    public final int executeScalarLogical(Frame frame) throws UnexpectedResultException {
        RAny lexpr = (RAny) left.execute(frame);
        RAny rexpr = (RAny) right.execute(frame);
        return executeScalarLogical(lexpr, rexpr);
    }

    public int executeScalarLogical(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            ScalarComparison sc = ScalarComparison.createSpecialized(lexpr, rexpr, ast, left, right, cmp);
            replace(sc, "install ScalarComparison.Specialized from Comparison");
            return sc.executeScalarLogical(lexpr, rexpr);
        }
    }

    @Override
    public final Object execute(Frame frame) {
        try {
            return RLogical.RLogicalFactory.getScalar(executeScalarLogical(frame));
        } catch (UnexpectedResultException e) {
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
            public abstract int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException;
        }

        enum Transition {
            COMMON_SCALAR,
            VECTOR_SCALAR
        }

        public static ScalarComparison createSpecialized(RAny leftTemplate, RAny rightTemplate, ASTNode ast, RNode left, RNode right, final ValueComparison cmp) {
            if (leftTemplate instanceof ScalarDoubleImpl && rightTemplate instanceof ScalarDoubleImpl) {
                Comparator c = new Comparator() {
                    @Override
                    public final int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        double l = ((ScalarDoubleImpl) lexpr).getDouble();
                        double r = ((ScalarDoubleImpl) rexpr).getDouble();
                        if (RDouble.RDoubleUtils.isNA(r) || RDouble.RDoubleUtils.isNA(l)) {
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
                    public final int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
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
                    public final int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarDoubleImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
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
                    public final int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarDoubleImpl)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
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
                    public final int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarIntImpl && rexpr instanceof ScalarLogicalImpl)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
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
                    public final int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof ScalarLogicalImpl && rexpr instanceof ScalarIntImpl)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
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

        public static int generic(RAny lexpr, RAny rexpr, ValueComparison cmp, ASTNode ast) throws UnexpectedResultException {
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
                if (RDouble.RDoubleUtils.isNA(ldbl)) {
                    return RLogical.NA;
                }
                if (rexpr instanceof ScalarDoubleImpl) {
                    double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(ldbl, rdbl) ? RLogical.TRUE : RLogical.FALSE;
                } else if (rexpr instanceof ScalarIntImpl) {
                    int rint = ((ScalarIntImpl) rexpr).getInt();
                    if (rint == RInt.NA) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(ldbl, rint) ? RLogical.TRUE : RLogical.FALSE;
                }
            } else if (lexpr instanceof ScalarIntImpl) {
                int lint = ((ScalarIntImpl) lexpr).getInt();
                if (lint == RInt.NA) {
                    return RLogical.NA;
                }
                if (rexpr instanceof ScalarIntImpl) {
                    int rint = ((ScalarIntImpl) rexpr).getInt();
                    if (rint == RInt.NA) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(lint, rint) ? RLogical.TRUE : RLogical.FALSE;
                } else if (rexpr instanceof ScalarDoubleImpl) {
                    double rdbl = ((ScalarDoubleImpl) rexpr).getDouble();
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RLogical.NA;
                    }
                    return cmp.cmp(lint, rdbl) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            throw new UnexpectedResultException(Transition.VECTOR_SCALAR);
        }

        public static ScalarComparison createGeneric(final ASTNode ast, RNode left, RNode right, final ValueComparison cmp) {
            Comparator c = new Comparator() {
                @Override
                public int compare(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                    return generic(lexpr, rexpr, cmp, ast);
                }
            };
            return new ScalarComparison(ast, left, right, cmp, c);
        }

        @Override
        public final int executeScalarLogical(RAny lexpr, RAny rexpr) throws UnexpectedResultException {
            try {
                return comp.compare(lexpr, rexpr);
            } catch (UnexpectedResultException e) {
                Transition t = (Transition) e.getResult();
                if (t == Transition.COMMON_SCALAR) {
                    ScalarComparison sc = createGeneric(ast, left, right, cmp);
                    replace(sc, "install CommonScalar from Comparison.Scalar");
                    return sc.executeScalarLogical(lexpr, rexpr);
                } else {
                    if (DEBUG_CMP) Utils.debug("comparison - optimistic comparison failed, values are not scalar numbers");
                    VectorScalarComparison vs = new VectorScalarComparison(ast);
                    replace(vs, "specializeNumericVectorScalarComparison");
                    Object res = vs.execute(lexpr, rexpr);
                    throw new UnexpectedResultException(res);
                }
            }
        }
    }

    class VectorScalarComparison extends BaseR {

        public VectorScalarComparison(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(Frame frame) {
            RAny lexpr = (RAny) left.execute(frame);
            RAny rexpr = (RAny) right.execute(frame);
            return execute(lexpr, rexpr);
        }

        public Object execute(RAny lexpr, RAny rexpr) { // FIXME: some of these checks should be rewritten as we now enforce scalar representation
            try {  // FIXME: perhaps should create different nodes for the cases below
                if (DEBUG_CMP) Utils.debug("comparison - assuming numeric (int,double) vector and scalar");
                // we assume that double vector against double scalar is the most common case
                if (lexpr instanceof RDouble) {
                    RDouble ldbl = (RDouble) lexpr;
                    if (rexpr instanceof RDouble) {
                        RDouble rdbl = (RDouble) rexpr;
                        if (rdbl.size() == 1) {
                            if (ldbl.size() >= 1 && rdbl.dimensions() == null) {
                                return Comparison.this.cmp.cmp(ldbl, rdbl.getDouble(0));
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        } else {
                            if (rdbl.size() > 1 && ldbl.size() == 1 && ldbl.dimensions() == null) {
                                return Comparison.this.cmp.cmp(ldbl.getDouble(0), rdbl);
                            } else {
                                throw new UnexpectedResultException(null);
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
                                return Comparison.this.cmp.cmp(lint, rint.getInt(0));
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        } else {
                            if (rint.size() > 1 && lint.size() == 1 && lint.dimensions() == null) {
                                return Comparison.this.cmp.cmp(lint.getInt(0), rint);
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        }
                    }
                }
                // now we know that one of the argument is not double and one is not integer
                if (lexpr instanceof RString || rexpr instanceof RString) {
                    RString lstr = lexpr.asString();
                    RString rstr = rexpr.asString();
                    if (rstr.size() == 1 && rstr.dimensions() == null) {
                        return Comparison.this.cmp.cmp(lstr, rstr.getString(0));
                    } else if (lstr.size() == 1 && lstr.dimensions() == null) {
                        return Comparison.this.cmp.cmp(lstr.getString(0), rstr);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                if (lexpr instanceof RComplex || rexpr instanceof RComplex) {
                    throw new UnexpectedResultException(null); // we assume complex comparisons are rare, so use generic case
                }
                if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                    RDouble ldbl = lexpr.asDouble();
                    RDouble rdbl = rexpr.asDouble();
                    if (rdbl.size() == 1 && rdbl.dimensions() == null) {
                        return Comparison.this.cmp.cmp(ldbl, rdbl.getDouble(0));
                    } else if (ldbl.size() == 1 && ldbl.dimensions() == null) {
                        return Comparison.this.cmp.cmp(ldbl.getDouble(0), rdbl);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                if (lexpr instanceof RInt || lexpr instanceof RInt) {
                    RInt lint = lexpr.asInt();
                    RInt rint = lexpr.asInt();
                    if (rint.size() == 1 && rint.dimensions() == null) {
                        return Comparison.this.cmp.cmp(lint,  rint.getInt(0));
                    } else if (lint.size() == 1 && lint.dimensions() == null) {
                        return Comparison.this.cmp.cmp(lint.getInt(0), rint);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                // logicals and raws are expected to be less frequent, hence handled in GenericComparison
                throw new UnexpectedResultException(null);

            } catch (UnexpectedResultException e) {
                if (DEBUG_CMP) Utils.debug("comparison - 2nd level comparison failed (not int,double scalar and vector)");
                GenericComparison vs = new GenericComparison(ast);
                replace(vs, "genericComparison");
                return vs.execute(lexpr, rexpr);
            }
        }
    }

    class GenericComparison extends BaseR {

        public GenericComparison(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(Frame frame) {
            RAny lexpr = (RAny) left.execute(frame);
            RAny rexpr = (RAny) right.execute(frame);
            return execute(lexpr, rexpr);
        }

        public Object execute(RAny lexpr, RAny rexpr) {
            if (DEBUG_CMP) Utils.debug("comparison - the most generic case");
            if (lexpr instanceof RString || rexpr instanceof RString) {
                RString lstr = lexpr.asString();
                RString rstr = rexpr.asString();
                return Comparison.this.cmp.cmp(lstr, rstr, ast);
            }
            if (lexpr instanceof RComplex || rexpr instanceof RComplex) {
                RComplex lcmp = lexpr.asComplex();
                RComplex rcmp = rexpr.asComplex();  // if the cast fails, a zero-length array is returned
                return Comparison.this.cmp.cmp(lcmp, rcmp, ast);
            }
            if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                RDouble ldbl = lexpr.asDouble();
                RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                return Comparison.this.cmp.cmp(ldbl, rdbl, ast);
            }
            if (lexpr instanceof RInt || rexpr instanceof RInt) {
                RInt lint = lexpr.asInt();
                RInt rint = rexpr.asInt();
                return Comparison.this.cmp.cmp(lint, rint, ast);
            }
            if (lexpr instanceof RLogical || rexpr instanceof RLogical) {
                RLogical llog = lexpr.asLogical();
                RLogical rlog = rexpr.asLogical();
                return Comparison.this.cmp.cmp(llog, rlog, ast);
            }
            if (lexpr instanceof RRaw || rexpr instanceof RRaw) {
                RRaw lraw = lexpr.asRaw();
                RRaw rraw = rexpr.asRaw();
                return Comparison.this.cmp.cmp(lraw, rraw, ast);
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
            if (RDouble.RDoubleUtils.isNA(b)) {
                return RLogicalFactory.getNAArray(n, a.dimensions());
            }
            int[] content = new int[n];
            for (int i = 0; i < n; i++) {
                double adbl = a.getDouble(i);
                if (RDouble.RDoubleUtils.isNA(adbl)) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(adbl, b) ? RLogical.TRUE : RLogical.FALSE;
                }
            }
            return RLogical.RLogicalFactory.getFor(content, a.dimensions(), a.names());
        }
        public RLogical cmp(double a, RDouble b) {
            int n = b.size();
            if (RDouble.RDoubleUtils.isNA(a)) {
                return RLogicalFactory.getNAArray(n, b.dimensions());
            }
            int[] content = new int[n];
            for (int i = 0; i < n; i++) {
                double bdbl = b.getDouble(i);
                if (RDouble.RDoubleUtils.isNA(bdbl)) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(a, bdbl) ? RLogical.TRUE : RLogical.FALSE;
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

            for (int i = 0; i < n; i++) {
                double adbl = a.getDouble(ai++);
                if (ai == na) {
                    ai = 0;
                }
                double bdbl = b.getDouble(bi++);
                if (bi == nb) {
                    bi = 0;
                }

                if (RDouble.RDoubleUtils.isNA(adbl) || RDouble.RDoubleUtils.isNA(bdbl)) {
                    content[i] = RLogical.NA;
                } else {
                    content[i] = cmp(adbl, bdbl) ? RLogical.TRUE : RLogical.FALSE;
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
        };
    }
}
