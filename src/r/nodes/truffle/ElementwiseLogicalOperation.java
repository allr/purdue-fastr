package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.RArray.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

public abstract class ElementwiseLogicalOperation extends BaseR {

    @Child RNode left;
    @Child RNode right;
    final Operation op;

    public ElementwiseLogicalOperation(ASTNode ast, RNode left, Operation op, RNode right) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
        this.op = op;
    }

    @Override
    public final  Object execute(Frame frame) {
        RAny leftValue = (RAny) left.execute(frame);
        RAny rightValue = (RAny) right.execute(frame);
        return execute(leftValue, rightValue);
    }

    public abstract RAny execute(RAny leftValue, RAny rightValue);

    public static ElementwiseLogicalOperation createUninitialized(ASTNode ast, RNode left, Operation op, RNode right) {
        return new ElementwiseLogicalOperation(ast, left, op, right) {

            @Override
            public RAny execute(RAny leftValue, RAny rightValue) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    ElementwiseLogicalOperation sn = Specialized.create(ast, left, op, right, leftValue, rightValue);
                    replace(sn, "install LogicalOperation from Uninitialized");
                    return sn.execute(leftValue, rightValue);
                }
            }

        };
    }

    public static ElementwiseLogicalOperation createGeneric(ASTNode ast, RNode left, Operation op, RNode right) {
        return new ElementwiseLogicalOperation(ast, left, op, right) {

            @Override
            public RAny execute(RAny leftValue, RAny rightValue) {
                if (leftValue instanceof RLogical && rightValue instanceof RLogical) {
                    return op.op((RLogical) leftValue, (RLogical) rightValue, ast);
                }
                if ((leftValue instanceof RInt || leftValue instanceof RDouble || leftValue instanceof RLogical) &&
                               (rightValue instanceof RInt || rightValue instanceof RDouble || rightValue instanceof RLogical)) {
                    return op.op(leftValue.asLogical(), rightValue.asLogical(), ast);
                }
                if (leftValue instanceof RRaw && rightValue instanceof RRaw) {
                    return op.op(leftValue.asRaw(), rightValue.asRaw(), ast);
                }
                Utils.nyi("unsupported types");
                return null;
            }

        };
    }

    public static class Specialized extends ElementwiseLogicalOperation {
        private final Action action;

        Specialized(ASTNode ast, RNode left, Operation op, RNode right, Action action) {
            super(ast, left, op, right);
            this.action = action;
        }

        public abstract static class Action {
            abstract RAny doFor(RAny leftValue, RAny rightValue, ASTNode ast) throws UnexpectedResultException;
        }

        public static ElementwiseLogicalOperation create(final ASTNode ast, RNode left, final Operation op, RNode right, RAny leftTemplate, RAny rightTemplate) {
            if (leftTemplate instanceof ScalarLogicalImpl && rightTemplate instanceof ScalarLogicalImpl) {
                return new Specialized(ast, left, op, right, new Action() {
                    @Override
                    RLogical doFor(RAny leftValue, RAny rightValue, ASTNode ast) throws UnexpectedResultException {
                        if ((leftValue instanceof ScalarLogicalImpl && rightValue instanceof ScalarLogicalImpl)) {
                            int l = op.op(((ScalarLogicalImpl) leftValue).getLogical(), ((ScalarLogicalImpl) rightValue).getLogical());
                            return RLogical.RLogicalFactory.getScalar(l);
                        }
                        throw new UnexpectedResultException(null);
                    }
                });
            }
            if (leftTemplate instanceof RLogical && rightTemplate instanceof RLogical) {
                return new Specialized(ast, left, op, right, new Action() {
                    @Override
                    RLogical doFor(RAny leftValue, RAny rightValue, ASTNode ast) throws UnexpectedResultException {
                        if ((leftValue instanceof RLogical && rightValue instanceof RLogical)) {
                            return op.op((RLogical) leftValue, (RLogical) rightValue, ast);
                        }
                        throw new UnexpectedResultException(null);
                    }
                });
            }
            if (leftTemplate instanceof RRaw && rightTemplate instanceof RRaw) {
                return new Specialized(ast, left, op, right, new Action() {
                    @Override
                    RRaw doFor(RAny leftValue, RAny rightValue, ASTNode ast) throws UnexpectedResultException {
                        if ((leftValue instanceof RRaw && rightValue instanceof RRaw)) {
                            return op.op((RRaw) leftValue, (RRaw) rightValue, ast);
                        }
                        throw new UnexpectedResultException(null);
                    }
                });
            }
            // TODO: more specialized nodes
            return createGeneric(ast, left, op, right);
        }

        @Override
        public RAny execute(RAny leftValue, RAny rightValue) {
            try {
                return action.doFor(leftValue, rightValue, ast);
            } catch (UnexpectedResultException e) {
                ElementwiseLogicalOperation gn = createGeneric(ast, left, op, right);
                replace(gn, "install Generic from ElementwiseLogicalOperation.Specialized");
                return gn.execute(leftValue, rightValue);
            }
        }

    }

    public abstract static class Operation {
        public abstract int op(int a, int b);
        public abstract byte op(byte a, byte b);
        public RLogical op(RLogical a, RLogical b, ASTNode ast) {
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
                content[i] = op(alog, blog);
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RLogical.RLogicalFactory.getFor(content, dimensions, names);
        }
        public RRaw op(RRaw a, RRaw b, ASTNode ast) {
            int na = a.size();
            int nb = b.size();
            int[] dimensions = Arithmetic.resultDimensions(ast, a, b);
            Names names = Arithmetic.resultNames(ast, a, b);
            if (na == 0 || nb == 0) {
                return RRaw.EMPTY;
            }

            int n = (na > nb) ? na : nb;
            byte[] content = new byte[n];
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
                content[i] = op(araw, braw);
            }

            if (ai != 0 || bi != 0) {
                RContext.warning(ast, RError.LENGTH_NOT_MULTI);
            }
            return RRaw.RRawFactory.getFor(content, dimensions, names);
        }
    }

    public static final Operation AND = new Operation() {
        @Override
        public byte op(byte a, byte b) {
            return (byte) (a & b);
        }
        @Override
        public int op(int a, int b) {
            if (a == RLogical.TRUE) {
                return b;
            }
            if (a == RLogical.FALSE) {
                return RLogical.FALSE;
            }
            // a == RLogical.NA
            if (b == RLogical.TRUE) {
                return RLogical.NA;
            } else {
                return b;
            }
        }
    };

    public static final Operation OR = new Operation() {
        @Override
        public byte op(byte a, byte b) {
            return (byte) (a | b);
        }
        @Override
        public int op(int a, int b) {
            if (a == RLogical.TRUE) {
                return RLogical.TRUE;
            }
            if (a == RLogical.FALSE) {
                return b;
            }
            // a == RLogical.NA
            if (b == RLogical.TRUE) {
                return RLogical.TRUE;
            } else {
                return RLogical.NA;
            }
        }
    };
}
