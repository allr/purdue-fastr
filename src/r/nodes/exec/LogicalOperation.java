package r.nodes.exec;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

// FIXME: we probably could get some performance improvement by specializing for pairs of types,
// thus avoiding the cast nodes

public abstract class LogicalOperation extends BaseR {

    @Child RNode left;
    @Child RNode right;

    public LogicalOperation(ASTNode ast, RNode left, RNode right) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
    }

    @Override
    public final Object execute(Frame frame) {
        return RLogical.RLogicalFactory.getScalar(executeScalarLogical(frame));
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

    @Override
    public abstract int executeScalarLogical(Frame frame);

    public final int extractLeftValue(Frame frame) {
        for (;;) {
            try {
                return left.executeScalarLogical(frame);
            } catch (SpecializationException e) {
                createAndInsertCastNode(left.getAST(),(RAny) e.getResult(), left);
                continue;
            }
        }
    }

    public final int extractRightValue(Frame frame) {
        for (;;) {
            try {
                return right.executeScalarLogical(frame);
            } catch (SpecializationException e) {
                createAndInsertCastNode(right.getAST(),(RAny) e.getResult(), right);
                continue;
            }
        }
    }

//    public static final int extractValue(Frame frame, RNode node) {
//        RNode curNode = node;
//        for (;;) {
//            try {
//                return curNode.executeScalarLogical(frame);
//            } catch (SpecializationException e) {
//                curNode = createAndInsertCastNode(node.getAST(), node, (RAny) e.getResult(), curNode);
//                continue;
//            }
//        }
//    }

    public static class Or extends LogicalOperation {
        public Or(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
        }

        @Override
        public int executeScalarLogical(Frame frame) {
            int leftValue = extractLeftValue(frame);
            if (leftValue == RLogical.TRUE) {
                return RLogical.TRUE;
            }
            return extractRightValue(frame);
        }
    }

    public static class And extends LogicalOperation {
        public And(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
        }

        @Override
        public int executeScalarLogical(Frame frame) {
            int leftValue = extractLeftValue(frame);
            if (leftValue == RLogical.TRUE) {
                return extractRightValue(frame);
            }
            if (leftValue == RLogical.FALSE) {
                return RLogical.FALSE;
            }
            // leftValue == RLogical.NA
            int rightValue = extractRightValue(frame);
            if (rightValue == RLogical.FALSE) {
                return RLogical.FALSE;
            }
            return RLogical.NA;
        }
    }

    // note: we can't use ConvertToLogicalOne, because of different error handling
    // FIXME: this could have more optimizations
    public abstract static class CastNode extends BaseR {
        @Child RNode child;
        int iteration;


        public CastNode(ASTNode ast, RNode child, int iteration, RNode failedNode) {
            super(ast);
            failedNode.replace(this);
            this.child = adoptChild(child);
            this.iteration = iteration;
        }

        @Override
        public final Object execute(Frame frame) {
            Utils.nyi("unreachable");
            return null;
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (child == oldNode) {
                child = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

        @Override
        public int executeScalarLogical(Frame frame) throws SpecializationException {
            RAny value = (RAny) child.execute(frame);
            return extract(value);
        }

        abstract int extract(RAny value) throws SpecializationException;
    }

    public static CastNode createAndInsertCastNode(ASTNode ast, RAny template, RNode failedNode) {

        int iteration = -1;
        RNode child;
        if (failedNode instanceof CastNode) {
            iteration = ((CastNode) failedNode).iteration;
            child = ((CastNode) failedNode).child;
        } else {
            child = failedNode;
        }
        if (iteration < 0) {
            if (template instanceof ScalarDoubleImpl) {
                return new CastNode(ast, child, iteration + 1, failedNode) {
                    @Override
                    int extract(RAny value) throws SpecializationException {
                        if (value instanceof ScalarDoubleImpl) {
                            return Convert.double2logical(((ScalarDoubleImpl) value).getDouble());
                        }
                        throw new SpecializationException(value);
                    }
                };
            }
            if (template instanceof ScalarIntImpl) {
                return new CastNode(ast, child, iteration + 1, failedNode) {
                    @Override
                    int extract(RAny value) throws SpecializationException {
                        if (value instanceof ScalarIntImpl) {
                            return Convert.int2logical(((ScalarIntImpl) value).getInt());
                        }
                        throw new SpecializationException(value);
                    }
                };
            }
        }
        if (iteration < 1) {
            if (template instanceof RLogical) {
                return new CastNode(ast, child, iteration + 1, failedNode) {
                    @Override
                    int extract(RAny value) throws SpecializationException {
                        if (value instanceof RLogical) {
                            RLogical v = (RLogical) value;
                            if (v.size() > 0) {
                                return v.getLogical(0);
                            } else {
                                return RLogical.NA;
                            }
                        }
                        throw new SpecializationException(value);
                    }
                };
            }
            if (template instanceof RDouble) {
                return new CastNode(ast, child, iteration + 1, failedNode) {
                    @Override
                    int extract(RAny value) throws SpecializationException {
                        if (value instanceof RDouble) {
                            RDouble v = (RDouble) value;
                            if (v.size() > 0) {
                                return Convert.double2logical(v.getDouble(0));
                            } else {
                                return RLogical.NA;
                            }
                        }
                        throw new SpecializationException(value);
                    }
                };
            }
            if (template instanceof RInt) {
                return new CastNode(ast, child, iteration + 1, failedNode) {
                    @Override
                    int extract(RAny value) throws SpecializationException {
                        if (value instanceof RInt) {
                            RInt v = (RInt) value;
                            if (v.size() > 0) {
                                return Convert.int2logical(v.getInt(0));
                            } else {
                                return RLogical.NA;
                            }
                        }
                        throw new SpecializationException(value);
                    }
                };
            }
        }
        return new CastNode(ast, child, iteration + 1, failedNode) {
            @Override
            int extract(RAny value) {
                if (value instanceof RLogical || value instanceof RInt || value instanceof RDouble || value instanceof RComplex) {
                    RLogical l = value.asLogical();
                    if (l.size() > 0) {
                        return l.getLogical(0);
                    } else {
                        return RLogical.NA;
                    }
                }

                BinaryOperation parentOp = (BinaryOperation) ast.getParent();
                String operator;
                if (parentOp.getLHS() == ast) {
                    operator = "x";
                } else {
                    operator = "y";
                }
                throw RError.getInvalidTypeIn(parentOp, operator, parentOp.getPrettyOperator());
            }
        };
    }
}
