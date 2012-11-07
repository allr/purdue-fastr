package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;


public class If extends BaseR {
    @Stable RNode cond;
    @Stable RNode trueBranch;
    @Stable RNode falseBranch;

    private static final boolean DEBUG_IF = false;

    public If(ASTNode ast, RNode cond, RNode trueBranch, RNode falseBranch) {
        super(ast);
        this.cond = updateParent(cond);
        this.trueBranch = updateParent(trueBranch);
        this.falseBranch = updateParent(falseBranch);
    }

    // The condition is treated as follows:
    //   - no special node for a 1-value logical argument
    //   - a special intermediate conversion node for multi-value logical argument, another for multi-value integer argument
    //   - a generic conversion node that can convert anything
    @Override
    public final Object execute(RContext context, Frame frame) {
        int ifVal;

        try {
            if (DEBUG_IF) Utils.debug("executing condition");
            ifVal = cond.executeScalarLogical(context, frame);
            if (DEBUG_IF) Utils.debug("condition got expected result");
        } catch (UnexpectedResultException e) {
            if (DEBUG_IF) Utils.debug("condition got unexpected result, inserting 2nd level cast node");
            RAny result = (RAny) e.getResult();
            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
            replaceChild(cond, castNode);
            ifVal = castNode.executeScalarLogical(context, result);
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            return trueBranch.execute(context, frame);
        } else if (ifVal == RLogical.FALSE) {
            return falseBranch.execute(context, frame);
        }
        throw RError.getUnexpectedNA(getAST());
    }

    // scalar comparison against a constant
    public static class IfConst extends BaseR {
        @Stable RNode cond;
        @Stable RNode expr;
        @Stable RNode trueBranch;
        @Stable RNode falseBranch;
        final RAny constant;

        public IfConst(ASTNode ast, RNode cond, RNode expr, RNode trueBranch, RNode falseBranch, RAny constant) {
            super(ast);
            this.cond = updateParent(cond);
            this.expr = updateParent(expr);
            this.trueBranch = updateParent(trueBranch);
            this.falseBranch = updateParent(falseBranch);
            this.constant = constant;
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
            RAny value = (RAny) expr.execute(context, frame);
            return execute(context, frame, value);
        }

        public Object execute(RContext context, Frame frame, RAny value) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized s = Specialized.create(ast, cond, expr, trueBranch, falseBranch, constant, value);
                if (s != null) {
                    replace(s, "install Specialized from IfConst");
                    return s.execute(context, frame);
                } else {
                    If in = new If(ast, cond, trueBranch, falseBranch);
                    replace(in, "install If from IfConst");
                    return in.execute(context, frame);
                }
            }
        }

        public abstract static class Comparison {
            public abstract int cmp(RAny value) throws UnexpectedResultException;
        }

        public static class Specialized extends IfConst {
            final Comparison cmp;

            public Specialized(ASTNode ast, RNode cond, RNode expr, RNode trueBranch, RNode falseBranch, RAny constant, Comparison cmp) {
                super(ast, cond, expr, trueBranch, falseBranch, constant);
                this.cmp = cmp;
            }

            public static Specialized create(ASTNode ast, RNode cond, RNode expr, RNode trueBranch, RNode falseBranch, RAny constant, RAny valueTemplate) {
                if (valueTemplate instanceof ScalarDoubleImpl) {
                    if (!(constant instanceof ScalarDoubleImpl || constant instanceof ScalarIntImpl || constant instanceof ScalarLogicalImpl)) {
                        return null;
                    }
                    RDouble dc = constant.asDouble();
                    final double c = dc.getDouble(0);
                    final boolean cIsNA = RDouble.RDoubleUtils.isNA(c);
                    Comparison cmp = new Comparison() {
                        @Override
                        public int cmp(RAny value) throws UnexpectedResultException {
                            if (!(value instanceof ScalarDoubleImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            double v = ((ScalarDoubleImpl) value).getDouble();
                            if (!cIsNA && !RDouble.RDoubleUtils.isNA(v)) {
                                return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                            } else {
                                return RLogical.NA;
                            }
                        }
                    };
                    return new Specialized(ast, cond, expr, trueBranch, falseBranch, constant, cmp);
                }
                if (valueTemplate instanceof ScalarIntImpl) {
                    Comparison cmp = null;
                    if (constant instanceof ScalarDoubleImpl) {
                        final double c = ((ScalarDoubleImpl) constant).getDouble();
                        final boolean cIsNA = RDouble.RDoubleUtils.isNAorNaN(c);
                        cmp = new Comparison() {
                            @Override
                            public int cmp(RAny value) throws UnexpectedResultException {
                                if (!(value instanceof ScalarIntImpl)) {
                                    throw new UnexpectedResultException(null);
                                }
                                double v = Convert.int2double(((ScalarIntImpl) value).getInt());
                                if (!cIsNA && !RDouble.RDoubleUtils.isNA(v)) {
                                    return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                                } else {
                                    return RLogical.NA;
                                }
                            }
                        };
                    } else if (constant instanceof ScalarIntImpl || constant instanceof ScalarLogicalImpl) {
                        RInt ic = constant.asInt();
                        final int c = ic.getInt(0);
                        final boolean cIsNA = (c == RInt.NA);
                        cmp = new Comparison() {
                            @Override
                            public int cmp(RAny value) throws UnexpectedResultException {
                                if (!(value instanceof ScalarIntImpl)) {
                                    throw new UnexpectedResultException(null);
                                }
                                int v = ((ScalarIntImpl) value).getInt();
                                if (!cIsNA && v != RInt.NA) {
                                    return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                                } else {
                                    return RLogical.NA;
                                }
                            }
                        };
                    } else {
                        return null;
                    }
                    return new Specialized(ast, cond, expr, trueBranch, falseBranch, constant, cmp);
                }
                if (valueTemplate instanceof ScalarLogicalImpl) {
                    if (!(constant instanceof ScalarLogicalImpl || constant instanceof ScalarIntImpl)) {
                        return null;
                    }
                    final int c = constant.asLogical().getLogical(0);
                    final boolean cIsNA = (c == RLogical.NA);
                    Comparison cmp = new Comparison() {
                        @Override
                        public int cmp(RAny value) throws UnexpectedResultException {
                            if (!(value instanceof ScalarLogicalImpl)) {
                                throw new UnexpectedResultException(null);
                            }
                            int v = ((ScalarLogicalImpl) value).getLogical();
                            if (!cIsNA && v != RLogical.NA) {
                                return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                            } else {
                                return RLogical.NA;
                            }
                        }
                    };
                    return new Specialized(ast, cond, expr, trueBranch, falseBranch, constant, cmp);
                }
                // FIXME: add a generic comparison against constant?
                return null;
            }

            @Override
            public final Object execute(RContext context, Frame frame, RAny value) {
                try {
                    int ifVal = cmp.cmp(value);
                    if (ifVal == RLogical.TRUE) {
                        return trueBranch.execute(context, frame);
                    } else if (ifVal == RLogical.FALSE) {
                        return falseBranch.execute(context, frame);
                    }
                    throw RError.getUnexpectedNA(getAST());
                 } catch (UnexpectedResultException e) {
                     If in = new If(ast, cond, trueBranch, falseBranch);
                     replace(in, "install If from IfConst.Specialized");
                     return in.execute(context, frame);
                 }
            }

        }

    }
}
