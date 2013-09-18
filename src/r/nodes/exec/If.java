package r.nodes.exec;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;


public class If extends BaseR {
    @Child RNode cond;
    @Child RNode trueBranch;
    @Child RNode falseBranch;

    private static final boolean DEBUG_IF = false;

    public If(ASTNode ast, RNode cond, RNode trueBranch, RNode falseBranch) {
        super(ast);
        this.cond = adoptChild(cond);
        this.trueBranch = adoptChild(trueBranch);
        this.falseBranch = adoptChild(falseBranch);
    }

    // The condition is treated as follows:
    //   - no special node for a 1-value logical argument
    //   - a special intermediate conversion node for multi-value logical argument, another for multi-value integer argument
    //   - a generic conversion node that can convert anything
    @Override
    public final Object execute(Frame frame) {
        int ifVal;

        try {
            if (DEBUG_IF) Utils.debug("executing condition");
            ifVal = cond.executeScalarLogical(frame);
            if (DEBUG_IF) Utils.debug("condition got expected result");
        } catch (SpecializationException e) {
            if (DEBUG_IF) Utils.debug("condition got unexpected result, inserting 2nd level cast node");
            RAny result = (RAny) e.getResult();
            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
            cond.replace(castNode);
            ifVal = castNode.executeScalarLogical(result);
        }

        if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
            return trueBranch.execute(frame);
        } else if (ifVal == RLogical.FALSE) {
            return falseBranch.execute(frame);
        }
        throw RError.getUnexpectedNA(getAST());
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (cond == oldNode) {
            cond = newNode;
            return adoptInternal(newNode);
        }
        if (trueBranch == oldNode) {
            trueBranch = newNode;
            return adoptInternal(newNode);
        }
        if (falseBranch == oldNode) {
            falseBranch = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    public static class IfNoElse extends BaseR {
        @Child RNode cond;
        @Child RNode trueBranch;

        public IfNoElse(ASTNode ast, RNode cond, RNode trueBranch) {
            super(ast);
            this.cond = adoptChild(cond);
            this.trueBranch = adoptChild(trueBranch);
        }

        @Override
        public final Object execute(Frame frame) {
            int ifVal;

            try {
                ifVal = cond.executeScalarNonNALogical(frame);
            } catch (SpecializationException e) {
                RAny result = (RAny) e.getResult();
                ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
                ifVal = castNode.executeScalarLogical(result);

                If ifnode = new If(ast, castNode, trueBranch, r.nodes.exec.Constant.getNull());
                replace(ifnode, "install generic If from IfNoElse");
                if (ifVal == RLogical.NA) {
                    throw RError.getUnexpectedNA(getAST());
                }
            }

            if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
                return trueBranch.execute(frame);
            } else {
                return RNull.getNull();
            }
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (cond == oldNode) {
                cond = newNode;
                return adoptInternal(newNode);
            }
            if (trueBranch == oldNode) {
                trueBranch = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }

    public static class IfElse extends BaseR {
        @Child RNode cond;
        @Child RNode trueBranch;
        @Child RNode falseBranch;

        public IfElse(ASTNode ast, RNode cond, RNode trueBranch, RNode falseBranch) {
            super(ast);
            this.cond = adoptChild(cond);
            this.trueBranch = adoptChild(trueBranch);
            this.falseBranch = adoptChild(falseBranch);
        }

        @Override
        public final Object execute(Frame frame) {
            int ifVal;

            try {
                ifVal = cond.executeScalarNonNALogical(frame);
            } catch (SpecializationException e) {
                RAny result = (RAny) e.getResult();
                ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
                ifVal = castNode.executeScalarLogical(result);

                If ifnode = new If(ast, castNode, trueBranch, falseBranch);
                replace(ifnode, "install generic If from IfNoElse");
                if (ifVal == RLogical.NA) {
                    throw RError.getUnexpectedNA(getAST());
                }
            }

            if (ifVal == RLogical.TRUE) { // Is it the right ordering ?
                return trueBranch.execute(frame);
            } else {
                return falseBranch.execute(frame);
            }
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (cond == oldNode) {
                cond = newNode;
                return adoptInternal(newNode);
            }
            if (trueBranch == oldNode) {
                trueBranch = newNode;
                return adoptInternal(newNode);
            }
            if (falseBranch == oldNode) {
                falseBranch = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }

    // scalar comparison against a constant
    public static class IfConst extends BaseR {
        @Child RNode cond;
        @Child RNode expr;
        @Child RNode trueBranch;
        @Child RNode falseBranch;
        final RAny constant;

        public IfConst(ASTNode ast, RNode cond, RNode expr, RNode trueBranch, RNode falseBranch, RAny constant) {
            super(ast);
            this.cond = adoptChild(cond);
            this.expr = adoptChild(expr);
            this.trueBranch = adoptChild(trueBranch);
            this.falseBranch = adoptChild(falseBranch);
            this.constant = constant;
        }

        @Override
        public final Object execute(Frame frame) {
            RAny value = (RAny) expr.execute(frame);
            return execute(frame, value);
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (cond == oldNode) {
                cond = newNode;
                return adoptInternal(newNode);
            }
            if (expr == oldNode) {
                expr = newNode;
                return adoptInternal(newNode);
            }
            if (trueBranch == oldNode) {
                trueBranch = newNode;
                return adoptInternal(newNode);
            }
            if (falseBranch == oldNode) {
                falseBranch = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

        public Object execute(Frame frame, RAny value) {
            try {
                throw new SpecializationException(null);
            } catch (SpecializationException e) {
                Specialized s = Specialized.create(ast, cond, expr, trueBranch, falseBranch, constant, value);
                if (s != null) {
                    replace(s, "install Specialized from IfConst");
                    return s.execute(frame);
                } else {
                    If in = new If(ast, cond, trueBranch, falseBranch);
                    replace(in, "install If from IfConst");
                    return in.execute(frame);
                }
            }
        }

        public abstract static class Comparison {
            public abstract int cmp(RAny value) throws SpecializationException;
        }

        public static class Specialized extends IfConst {
            final Comparison cmp;

            public Specialized(ASTNode ast, RNode cond, RNode expr, RNode trueBranch, RNode falseBranch, RAny constant, Comparison cmp) {
                super(ast, cond, expr, trueBranch, falseBranch, constant);
                this.cmp = cmp;
            }

            public static Specialized create(final ASTNode ast, RNode cond, RNode expr, RNode trueBranch, RNode falseBranch, RAny constant, RAny valueTemplate) {
                // FIXME: the comparison functions could treat the NAs directly

                if (valueTemplate instanceof ScalarDoubleImpl) {
                    if (!(constant instanceof ScalarDoubleImpl || constant instanceof ScalarIntImpl || constant instanceof ScalarLogicalImpl)) {
                        return null;
                    }
                    RDouble dc = constant.asDouble();
                    final double c = dc.getDouble(0);
                    final boolean cIsNAorNaN = RDouble.RDoubleUtils.isNAorNaN(c);
                    Comparison cmp = new Comparison() {
                        @Override
                        public int cmp(RAny value) throws SpecializationException {
                            if (!(value instanceof ScalarDoubleImpl)) {
                                throw new SpecializationException(null);
                            }
                            double v = ((ScalarDoubleImpl) value).getDouble();
                            if (!cIsNAorNaN && !RDouble.RDoubleUtils.isNAorNaN(v)) {
                                return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                            } else {
                                throw RError.getUnexpectedNA(ast);
                            }
                        }
                    };
                    return new Specialized(ast, cond, expr, trueBranch, falseBranch, constant, cmp);
                }
                if (valueTemplate instanceof ScalarIntImpl) {
                    Comparison cmp = null;
                    if (constant instanceof ScalarDoubleImpl) {
                        final double c = ((ScalarDoubleImpl) constant).getDouble();
                        final boolean cIsNAorNaN = RDouble.RDoubleUtils.isNAorNaN(c);
                        cmp = new Comparison() {
                            @Override
                            public int cmp(RAny value) throws SpecializationException {
                                if (!(value instanceof ScalarIntImpl)) {
                                    throw new SpecializationException(null);
                                }
                                double v = Convert.int2double(((ScalarIntImpl) value).getInt());
                                if (!cIsNAorNaN && !RDouble.RDoubleUtils.isNAorNaN(v)) {
                                    return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                                } else {
                                    throw RError.getUnexpectedNA(ast);
                                }
                            }
                        };
                    } else if (constant instanceof ScalarIntImpl || constant instanceof ScalarLogicalImpl) {
                        RInt ic = constant.asInt();
                        final int c = ic.getInt(0);
                        final boolean cIsNA = (c == RInt.NA);
                        cmp = new Comparison() {
                            @Override
                            public int cmp(RAny value) throws SpecializationException {
                                if (!(value instanceof ScalarIntImpl)) {
                                    throw new SpecializationException(null);
                                }
                                int v = ((ScalarIntImpl) value).getInt();
                                if (!cIsNA && v != RInt.NA) {
                                    return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                                } else {
                                    throw RError.getUnexpectedNA(ast);
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
                        public int cmp(RAny value) throws SpecializationException {
                            if (!(value instanceof ScalarLogicalImpl)) {
                                throw new SpecializationException(null);
                            }
                            int v = ((ScalarLogicalImpl) value).getLogical();
                            if (!cIsNA && v != RLogical.NA) {
                                return (v == c) ? RLogical.TRUE : RLogical.FALSE;
                            } else {
                                throw RError.getUnexpectedNA(ast);
                            }
                        }
                    };
                    return new Specialized(ast, cond, expr, trueBranch, falseBranch, constant, cmp);
                }
                // FIXME: add a generic comparison against constant?
                return null;
            }

            @Override
            public final Object execute(Frame frame, RAny value) {
                try {
                    int ifVal = cmp.cmp(value);
                    if (ifVal == RLogical.TRUE) {
                        return trueBranch.execute(frame);
                    } else {
                        assert Utils.check(ifVal == RLogical.FALSE);
                        return falseBranch.execute(frame);
                    }
                 } catch (SpecializationException e) {
                     If in = new If(ast, cond, trueBranch, falseBranch);
                     replace(in, "install If from IfConst.Specialized");
                     return in.execute(frame);
                 }
            }

        }

    }
}
