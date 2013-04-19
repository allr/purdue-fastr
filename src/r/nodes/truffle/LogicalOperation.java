package r.nodes.truffle;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;

// FIXME: probably could get some performance improvement by specializing for pairs of types,
// thus avoiding the cast nodes

public abstract class LogicalOperation extends BaseR {

    @Child RNode left;
    @Child RNode right;

    public LogicalOperation(ASTNode ast, RNode left, RNode right) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
    }

    @Override public void replace0(RNode o, RNode n) {
        if (left == o) left = n;
        if (right == o) right = n;
    }

    @Override public final Object execute(Frame frame) {
        return RLogical.RLogicalFactory.getScalar(executeScalarLogical(frame));
    }

    @Override public abstract int executeScalarLogical(Frame frame);

    public final int extractValue(Frame frame, RNode node) {
        RNode curNode = node;
        for (;;) {
            try {
                return curNode.executeScalarLogical(frame);
            } catch (UnexpectedResultException e) {
                RNode newNode = createCastNode(node.getAST(), node, (RAny) e.getResult(), curNode);
                replaceChild(curNode, newNode);
                curNode = newNode;
                continue;
            }
        }
    }

    public static class Or extends LogicalOperation {
        public Or(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
        }

        @Override public int executeScalarLogical(Frame frame) {
            int leftValue = extractValue(frame, left);
            if (leftValue == RLogical.TRUE) { return RLogical.TRUE; }
            return extractValue(frame, right);
        }
    }

    public static class And extends LogicalOperation {
        public And(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
        }

        @Override public int executeScalarLogical(Frame frame) {
            int leftValue = extractValue(frame, left);
            if (leftValue == RLogical.TRUE) { return extractValue(frame, right); }
            if (leftValue == RLogical.FALSE) { return RLogical.FALSE; }
            // leftValue == RLogical.NA
            int rightValue = extractValue(frame, right);
            if (rightValue == RLogical.FALSE) { return RLogical.FALSE; }
            return RLogical.NA;
        }
    }

    // note: we can't use ConvertToLogicalOne, because of different error handling
    // FIXME: this could have more optimizations
    public abstract static class CastNode extends BaseR {
        @Child RNode child;
        int iteration;

        public CastNode(ASTNode ast, RNode child, int iteration) {
            super(ast);
            this.child = adoptChild(child);
            this.iteration = iteration;
        }

        @Override public final Object execute(Frame frame) {
            Utils.nyi("unreachable");
            return null;
        }

        @Override public int executeScalarLogical(Frame frame) throws UnexpectedResultException {
            RAny value = (RAny) child.execute(frame);
            return extract(value);
        }

        abstract int extract(RAny value) throws UnexpectedResultException;

        @Override public void replace0(RNode o, RNode n) {
            if (child == o) child = n;
        }

    }

    public static CastNode createCastNode(ASTNode ast, RNode childNode, RAny template, RNode failedNode) {

        int iteration = -1;
        RNode child;
        if (failedNode instanceof CastNode) {
            iteration = ((CastNode) failedNode).iteration;
            child = ((CastNode) failedNode).child;
        } else {
            child = childNode;
        }
        if (iteration < 0) {
            if (template instanceof ScalarDoubleImpl) { return new CastNode(ast, child, iteration + 1) {
                @Override int extract(RAny value) throws UnexpectedResultException {
                    if (value instanceof ScalarDoubleImpl) { return Convert.double2logical(((ScalarDoubleImpl) value).getDouble()); }
                    throw new UnexpectedResultException(value);
                }
            }; }
            if (template instanceof ScalarIntImpl) { return new CastNode(ast, child, iteration + 1) {
                @Override int extract(RAny value) throws UnexpectedResultException {
                    if (value instanceof ScalarIntImpl) { return Convert.int2logical(((ScalarIntImpl) value).getInt()); }
                    throw new UnexpectedResultException(value);
                }
            }; }
        }
        if (iteration < 1) {
            if (template instanceof RLogical) { return new CastNode(ast, child, iteration + 1) {
                @Override int extract(RAny value) throws UnexpectedResultException {
                    if (value instanceof RLogical) {
                        RLogical v = (RLogical) value;
                        if (v.size() > 0) {
                            return v.getLogical(0);
                        } else {
                            return RLogical.NA;
                        }
                    }
                    throw new UnexpectedResultException(value);
                }
            }; }
            if (template instanceof RDouble) { return new CastNode(ast, child, iteration + 1) {
                @Override int extract(RAny value) throws UnexpectedResultException {
                    if (value instanceof RDouble) {
                        RDouble v = (RDouble) value;
                        if (v.size() > 0) {
                            return Convert.double2logical(v.getDouble(0));
                        } else {
                            return RLogical.NA;
                        }
                    }
                    throw new UnexpectedResultException(value);
                }
            }; }
            if (template instanceof RInt) { return new CastNode(ast, child, iteration + 1) {
                @Override int extract(RAny value) throws UnexpectedResultException {
                    if (value instanceof RInt) {
                        RInt v = (RInt) value;
                        if (v.size() > 0) {
                            return Convert.int2logical(v.getInt(0));
                        } else {
                            return RLogical.NA;
                        }
                    }
                    throw new UnexpectedResultException(value);
                }
            }; }
        }
        return new CastNode(ast, child, iteration + 1) {
            @Override int extract(RAny value) {
                if (value instanceof RLogical || value instanceof RInt || value instanceof RDouble) {
                    RLogical l = value.asLogical();
                    if (l.size() > 0) {
                        return l.getLogical(0);
                    } else {
                        return RLogical.NA;
                    }
                }
                Utils.nyi("unsupported logical operation argument");
                return -1;
            }
        };
    }
}
