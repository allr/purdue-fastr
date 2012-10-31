package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.errors.*;
import r.nodes.*;

// FIXME: update debugs for new specializations
// FIXME: add more scalar specializations

public class Comparison extends BaseR {

    final ValueComparison cmp;
    @Stable RNode left;
    @Stable RNode right;

    private static final boolean DEBUG_CMP = false;

    public Comparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp) {
        super(ast);
        this.left = updateParent(left);
        this.right = updateParent(right);
        this.cmp = cmp;
    }

    @Override
    public final Object execute(RContext context, Frame frame) {
        RAny lexpr = (RAny) left.execute(context, frame);
        RAny rexpr = (RAny) right.execute(context, frame);
        return execute(context, lexpr, rexpr);
    }

    public Object execute(RContext context, RAny lexpr, RAny rexpr) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            ScalarComparison sc = ScalarComparison.createSpecialized(lexpr, rexpr, ast, left, right, cmp);
            replace(sc, "install ScalarComparison.Specialized from Comparison");
            return sc.execute(context, lexpr, rexpr);
        }
    }

    static class ScalarComparison extends Comparison {
        final Comparator comp;

        public ScalarComparison(ASTNode ast, RNode left, RNode right, ValueComparison cmp, Comparator comp) {
            super(ast, left, right, cmp);
            this.comp = comp;
        }

        public abstract static class Comparator {
            public abstract RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException;
        }

        enum Transition {
            COMMON_SCALAR,
            VECTOR_SCALAR
        }

        private static void checkScalar(RArray v, Transition t) throws UnexpectedResultException {
            if (v.size() != 1) {
                throw new UnexpectedResultException(t);
            }
        }

        public static ScalarComparison createSpecialized(RAny leftTemplate, RAny rightTemplate, ASTNode ast, RNode left, RNode right, final ValueComparison cmp) {
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RDouble) {
                Comparator c = new Comparator() {
                    @Override
                    public final RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RDouble && rexpr instanceof RDouble)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        RDouble ld = (RDouble) lexpr;
                        RDouble rd = (RDouble) rexpr;
                        checkScalar(ld, Transition.COMMON_SCALAR);
                        checkScalar(rd, Transition.COMMON_SCALAR);
                        double l = ld.getDouble(0);
                        double r = rd.getDouble(0);
                        if (RDouble.RDoubleUtils.isNA(l) || RDouble.RDoubleUtils.isNA(r)) {
                            return RLogical.BOXED_NA;
                        }
                        Utils.debug("A");
                        return cmp.cmp(l, r) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RInt) {
                Comparator c = new Comparator() {
                    @Override
                    public final RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RInt && rexpr instanceof RInt)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        RInt li = (RInt) lexpr;
                        RInt ri = (RInt) rexpr;
                        checkScalar(li, Transition.COMMON_SCALAR);
                        checkScalar(ri, Transition.COMMON_SCALAR);
                        int l = li.getInt(0);
                        int r = ri.getInt(0);
                        if (l == RInt.NA || r == RInt.NA) {
                            return RLogical.BOXED_NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RInt) {
                Comparator c = new Comparator() {
                    @Override
                    public final RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RDouble && rexpr instanceof RInt)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        RDouble ld = (RDouble) lexpr;
                        RInt ri = (RInt) rexpr;
                        checkScalar(ld, Transition.COMMON_SCALAR);
                        checkScalar(ri, Transition.COMMON_SCALAR);
                        double l = ld.getDouble(0);
                        int r = ri.getInt(0);
                        if (RDouble.RDoubleUtils.isNAorNaN(l) || r == RInt.NA) {
                            return RLogical.BOXED_NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RDouble) {
                Comparator c = new Comparator() {
                    @Override
                    public final RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RInt && rexpr instanceof RDouble)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        RInt li = (RInt) lexpr;
                        RDouble rd = (RDouble) rexpr;
                        checkScalar(li, Transition.COMMON_SCALAR);
                        checkScalar(rd, Transition.COMMON_SCALAR);
                        int l = li.getInt(0);
                        double r = rd.getDouble(0);
                        if (l == RInt.NA || RDouble.RDoubleUtils.isNAorNaN(r)) {
                            return RLogical.BOXED_NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RLogical) {
                Comparator c = new Comparator() {
                    @Override
                    public final RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RInt && rexpr instanceof RLogical)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        RInt li = (RInt) lexpr;
                        RLogical rl = (RLogical) rexpr;
                        checkScalar(li, Transition.COMMON_SCALAR);
                        checkScalar(rl, Transition.COMMON_SCALAR);
                        int l = li.getInt(0);
                        int r = rl.getLogical(0);
                        if (l == RInt.NA || r == RLogical.NA) {
                            return RLogical.BOXED_NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            if (leftTemplate instanceof RLogical && rightTemplate instanceof RInt) {
                Comparator c = new Comparator() {
                    @Override
                    public final RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                        if (!(lexpr instanceof RLogical && rexpr instanceof RInt)) {
                            throw new UnexpectedResultException(Transition.COMMON_SCALAR);
                        }
                        RLogical ll = (RLogical) lexpr;
                        RInt ri = (RInt) rexpr;
                        checkScalar(ll, Transition.COMMON_SCALAR);
                        checkScalar(ri, Transition.COMMON_SCALAR);
                        int l = ll.getLogical(0);
                        int r = ri.getInt(0);
                        if (l == RLogical.NA || r == RInt.NA) {
                            return RLogical.BOXED_NA;
                        }
                        return cmp.cmp(l, r) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new ScalarComparison(ast, left, right, cmp, c);
            }
            return createGeneric(ast, left, right, cmp);
        }

        public static RLogical generic(RContext context, RAny lexpr, RAny rexpr, ValueComparison cmp, ASTNode ast) throws UnexpectedResultException {
            if (DEBUG_CMP) Utils.debug("comparison - assuming scalar numbers");

            if (lexpr instanceof RDouble) { // note: could make this shorter if we didn't care about Java-level boxing
                RDouble ld = (RDouble) lexpr;
                checkScalar(ld, Transition.VECTOR_SCALAR);
                double ldbl = ld.getDouble(0);
                if (RDouble.RDoubleUtils.isNA(ldbl)) {
                    return RLogical.BOXED_NA;
                }
                if (rexpr instanceof RDouble) {
                    RDouble rd = (RDouble) rexpr;
                    checkScalar(rd, Transition.VECTOR_SCALAR);
                    double rdbl = rd.getDouble(0);
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(ldbl, rdbl) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else if (rexpr instanceof RInt) {
                    RInt ri = (RInt) rexpr;
                    checkScalar(ri, Transition.VECTOR_SCALAR);
                    int rint = ri.getInt(0);
                    if (rint == RInt.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(ldbl, rint) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                }
            } else if (lexpr instanceof RInt) {
                RInt li = (RInt) lexpr;
                checkScalar(li, Transition.VECTOR_SCALAR);
                int lint = li.getInt(0);
                if (lint == RInt.NA) {
                    return RLogical.BOXED_NA;
                }
                if (rexpr instanceof RInt) {
                    RInt ri = (RInt) rexpr;
                    checkScalar(ri, Transition.VECTOR_SCALAR);
                    int rint = ri.getInt(0);
                    if (rint == RInt.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(lint, rint) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else if (rexpr instanceof RDouble) {
                    RDouble rd = (RDouble) rexpr;
                    checkScalar(rd, Transition.VECTOR_SCALAR);
                    double rdbl = rd.getDouble(0);
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(lint, rdbl) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                }
            }
            throw new UnexpectedResultException(Transition.VECTOR_SCALAR);
        }

        public static ScalarComparison createGeneric(final ASTNode ast, RNode left, RNode right, final ValueComparison cmp) {
            Comparator c = new Comparator() {
                @Override
                public RLogical compare(RContext context, RAny lexpr, RAny rexpr) throws UnexpectedResultException {
                    return generic(context, lexpr, rexpr, cmp, ast);
                }
            };
            return new ScalarComparison(ast, left, right, cmp, c);
        }

        @Override
        public final Object execute(RContext context, RAny lexpr, RAny rexpr) {
            try {
                return comp.compare(context, lexpr, rexpr);
            } catch (UnexpectedResultException e) {
                Transition t = (Transition) e.getResult();
                if (t == Transition.COMMON_SCALAR) {
                    ScalarComparison sc = createGeneric(ast, left, right, cmp);
                    replace(sc, "install CommonScalar from Comparison.Scalar");
                    return sc.execute(context, lexpr, rexpr);
                } else {
                    if (DEBUG_CMP) Utils.debug("comparison - optimistic comparison failed, values are not scalar numbers");
                    VectorScalarComparison vs = new VectorScalarComparison(ast);
                    replace(vs, "specializeNumericVectorScalarComparison");
                    return vs.execute(context,lexpr, rexpr);
                }
            }
        }
    }

    class VectorScalarComparison extends BaseR {

        public VectorScalarComparison(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
            RAny lexpr = (RAny) left.execute(context, frame);
            RAny rexpr = (RAny) right.execute(context, frame);
            return execute(context, lexpr, rexpr);
        }

        public Object execute(RContext context, RAny lexpr, RAny rexpr) {
            try {  // FIXME: perhaps should create different nodes for the cases below
                if (DEBUG_CMP) Utils.debug("comparison - assuming numeric (int,double) vector and scalar");
                // we assume that double vector against double scalar is the most common case
                if (lexpr instanceof RDouble) {
                    RDouble ldbl = (RDouble) lexpr;
                    if (rexpr instanceof RDouble) {
                        RDouble rdbl = (RDouble) rexpr;
                        if (rdbl.size() == 1) {
                            if (ldbl.size() >= 1) {
                                return Comparison.this.cmp.cmp(ldbl, rdbl.getDouble(0));
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        } else {
                            if (rdbl.size() >= 1 && ldbl.size() == 1) {
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
                            if (lint.size() >= 1) {
                                return Comparison.this.cmp.cmp(lint, rint.getInt(0));
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        } else {
                            if (rint.size() >= 1 && lint.size() == 1) {
                                return Comparison.this.cmp.cmp(lint.getInt(0), rint);
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        }
                    }
                }
                // now we know that one of the argument is not double and one is not integer
                if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                    RDouble ldbl = lexpr.asDouble();
                    RDouble rdbl = rexpr.asDouble();
                    if (rdbl.size() == 1) {
                        return Comparison.this.cmp.cmp(ldbl, rdbl.getDouble(0));
                    } else if (ldbl.size() == 1) {
                        return Comparison.this.cmp.cmp(ldbl.getDouble(0), rdbl);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                if (lexpr instanceof RInt || lexpr instanceof RInt) {
                    RInt lint = lexpr.asInt();
                    RInt rint = lexpr.asInt();
                    if (rint.size() == 1) {
                        return Comparison.this.cmp.cmp(lint,  rint.getInt(0));
                    } else if (lint.size() == 1) {
                        return Comparison.this.cmp.cmp(lint.getInt(0), rint);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                throw new UnexpectedResultException(null);

            } catch (UnexpectedResultException e) {
                if (DEBUG_CMP) Utils.debug("comparison - 2nd level comparison failed (not int,double scalar and vector)");
                GenericComparison vs = new GenericComparison(ast);
                replace(vs, "genericComparison");
                return vs.execute(context, lexpr, rexpr);
            }
        }
    }

    class GenericComparison extends BaseR {

        public GenericComparison(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
            RAny lexpr = (RAny) left.execute(context, frame);
            RAny rexpr = (RAny) right.execute(context, frame);
            return execute(context, lexpr, rexpr);
        }

        public Object execute(RContext context, RAny lexpr, RAny rexpr) {
            if (DEBUG_CMP) Utils.debug("comparison - the most generic case");
            if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                RDouble ldbl = lexpr.asDouble();
                RDouble rdbl = rexpr.asDouble();  // if the cast fails, a zero-length array is returned
                return Comparison.this.cmp.cmp(ldbl, rdbl, context, ast);
            }
            if (lexpr instanceof RInt || rexpr instanceof RInt) {
                RInt lint = lexpr.asInt();
                RInt rint = rexpr.asInt();
                return Comparison.this.cmp.cmp(lint, rint, context, ast);
            }
            if (lexpr instanceof RLogical || rexpr instanceof RLogical) {
                RLogical llog = lexpr.asLogical();
                RLogical rlog = rexpr.asLogical();
                return Comparison.this.cmp.cmp(llog, rlog, context, ast);
            }
            Utils.nyi("unsupported case for comparison");
            return null;
        }

    }

    // FIXME: check that calls to cmp are inlined, otherwise we might have to do that manually
    public abstract static class ValueComparison {
        public abstract boolean cmp(int a, int b);
        public abstract boolean cmp(double a, double b);
        public boolean cmp(int a, double b) {
            return cmp((double) a, b);
        }
        public boolean cmp(double a, int b) {
            return cmp(a, (double) b);
        }

        public RLogical cmp(RDouble a, double b) {
            int n = a.size();
            if (RDouble.RDoubleUtils.isNA(b)) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getUninitializedArray(n);
            for (int i = 0; i < n; i++) {
                double adbl = a.getDouble(i);
                if (RDouble.RDoubleUtils.isNA(adbl)) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(adbl, b) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }
        public RLogical cmp(double a, RDouble b) {
            int n = b.size();
            if (RDouble.RDoubleUtils.isNA(a)) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getUninitializedArray(n);
            for (int i = 0; i < n; i++) {
                double bdbl = b.getDouble(i);
                if (RDouble.RDoubleUtils.isNA(bdbl)) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(a, bdbl) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }
        public RLogical cmp(RInt a, int b) {
            int n = a.size();
            if (b == RInt.NA) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getUninitializedArray(n);
            for (int i = 0; i < n; i++) {
                int aint = a.getInt(i);
                if (aint == RInt.NA) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(aint, b) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }
        public RLogical cmp(int a, RInt b) {
            int n = b.size();
            if (a == RInt.NA) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getUninitializedArray(n);
            for (int i = 0; i < n; i++) {
                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(a, bint) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }

        public RLogical cmp(RDouble a, RDouble b, RContext context, ASTNode ast) {
            int na = a.size();
            int nb = b.size();

            if (na == 0 || nb == 0) {
                return RLogicalFactory.getUninitializedArray(0);
            }

            int n = (na > nb) ? na : nb;
            RLogical r = RLogicalFactory.getUninitializedArray(n);
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
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(adbl, bdbl) ? RLogical.TRUE : RLogical.FALSE);
                }
            }

            if (ai != 0 || bi != 0) {
                context.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return r;
        }
        public RLogical cmp(RInt a, RInt b, RContext context, ASTNode ast) {
            int na = a.size();
            int nb = b.size();

            if (na == 0 || nb == 0) {
                return RLogicalFactory.getUninitializedArray(0);
            }

            int n = (na > nb) ? na : nb;
            RLogical r = RLogicalFactory.getUninitializedArray(n);
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
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(aint, bint) ? RLogical.TRUE : RLogical.FALSE);
                }
            }

            if (ai != 0 || bi != 0) {
                context.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return r;
        }
        public RLogical cmp(RLogical a, RLogical b, RContext context, ASTNode ast) {
            int na = a.size();
            int nb = b.size();

            if (na == 0 || nb == 0) {
                return RLogicalFactory.getUninitializedArray(0);
            }

            int n = (na > nb) ? na : nb;
            RLogical r = RLogicalFactory.getUninitializedArray(n);
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
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(alog, blog) ? RLogical.TRUE : RLogical.FALSE);
                }
            }

            if (ai != 0 || bi != 0) {
                context.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return r;
        }
    }

    public static ValueComparison getEQ() {
        return new ValueComparison() {
            @Override
            public boolean cmp(int a, int b) {
                return a == b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a == b;
            }
        };
    }
    public static ValueComparison getNE() {
        return new ValueComparison() {
            @Override
            public boolean cmp(int a, int b) {
                return a != b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a != b;
            }
        };
    }
    public static ValueComparison getLE() {
        return new ValueComparison() {
            @Override
            public boolean cmp(int a, int b) {
                return a <= b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a <= b;
            }
        };
    }
    public static ValueComparison getGE() {
        return new ValueComparison() {
            @Override
            public boolean cmp(int a, int b) {
                return a >= b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a >= b;
            }
        };
    }
    public static ValueComparison getLT() {
        return new ValueComparison() {
            @Override
            public boolean cmp(int a, int b) {
                return a < b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a < b;
            }
        };
    }
    public static ValueComparison getGT() {
        return new ValueComparison() {
            @Override
            public boolean cmp(int a, int b) {
                return a > b;
            }
            @Override
            public boolean cmp(double a, double b) {
                return a > b;
            }
        };
    }
}
