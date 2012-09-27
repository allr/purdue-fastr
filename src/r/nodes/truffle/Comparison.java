package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.errors.*;
import r.nodes.*;


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
    public Object execute(RContext context, Frame frame) {
        RAny lexpr = (RAny) left.execute(context, frame);
        RAny rexpr = (RAny) right.execute(context, frame);
        // this version assumes comparison of two scalars (int, double, int vs. double)
        try {
            if (DEBUG_CMP) Utils.debug("comparison - assuming scalar numbers");
            RArray larr = RValueConversion.expectArrayOne(lexpr);
            RArray rarr = RValueConversion.expectArrayOne(rexpr);
              // FIXME: If we can assume that when numeric scalars are compared, their types (int, double) are stable,
              //        we might try to improve performance by splitting the code below into different truffle nodes
            if (larr instanceof RDouble) { // note: could make this shorter if we didn't care about Java-level boxing
                double ldbl = ((RDouble) larr).getDouble(0);
                if (RDouble.RDoubleUtils.isNA(ldbl)) {
                    return RLogical.BOXED_NA;
                }
                if (rarr instanceof RDouble) {
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(ldbl, rdbl) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else if (rarr instanceof RInt) {
                    int rint = ((RInt) rarr).getInt(0);
                    if (rint == RInt.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(ldbl, rint) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else {
                    throw new UnexpectedResultException(null);
                }
            } else if (larr instanceof RInt) {
                int lint = ((RInt) larr).getInt(0);
                if (lint == RInt.NA) {
                    return RLogical.BOXED_NA;
                }
                if (rarr instanceof RInt) {
                    int rint = ((RInt) rarr).getInt(0);
                    if (rint == RInt.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(lint, rint) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else if (rarr instanceof RDouble) {
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (RDouble.RDoubleUtils.isNA(rdbl)) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(lint, rdbl) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else {
                    throw new UnexpectedResultException(null);
                }
            } else {
                throw new UnexpectedResultException(null);
            }
        } catch (UnexpectedResultException e) {
            if (DEBUG_CMP) Utils.debug("comparison - optimistic comparison failed, values are not scalar numbers");
            VectorScalarComparison vs = new VectorScalarComparison(ast);
            replace(vs, "specializeNumericVectorScalarComparison");
            return vs.execute(context,lexpr, rexpr);
        }
    }

    class VectorScalarComparison extends BaseR {

        public VectorScalarComparison(ASTNode ast) {
            super(ast);
        }

        @Override
        public Object execute(RContext context, Frame frame) {
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
        public Object execute(RContext context, Frame frame) {
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
