package r.nodes.exec;

import r.nodes.ast.*;
import r.runtime.*;

public class Sequence extends BaseR {

    @Children final RNode[] exprs;

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        adoptChildren(exprs);
    }

    @Override public final Object execute(Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = null; // NOTE: this line is important, it allows the GC to clean-up temporaries
            res = e.execute(frame);
        }
        return res;
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (exprs != null) {
            for(int i = 0; i < exprs.length; i++) {
                if (exprs[i] == oldNode) {
                    exprs[i] = newNode;
                    return adoptInternal(newNode);
                }
            }
        }
        return super.replaceChild(oldNode, newNode);
    }

    public static class Sequence2 extends BaseR {
        @Child RNode child1;
        @Child RNode child2;

        public Sequence2(ASTNode ast, RNode[] exprs) {
            super(ast);
            this.child1 = adoptChild(exprs[0]);
            this.child2 = adoptChild(exprs[1]);
        }

        @Override public final Object execute(Frame frame) {
            child1.execute(frame);
            return child2.execute(frame);
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (child1 == oldNode) {
                child1 = newNode;
                return adoptInternal(newNode);
            }
            if (child2 == oldNode) {
                child2 = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }

    public static class Sequence3 extends BaseR {
        @Child RNode child1;
        @Child RNode child2;
        @Child RNode child3;

        public Sequence3(ASTNode ast, RNode[] exprs) {
            super(ast);
            this.child1 = adoptChild(exprs[0]);
            this.child2 = adoptChild(exprs[1]);
            this.child3 = adoptChild(exprs[2]);
        }

        @Override public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            return child3.execute(frame);
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (child1 == oldNode) {
                child1 = newNode;
                return adoptInternal(newNode);
            }
            if (child2 == oldNode) {
                child2 = newNode;
                return adoptInternal(newNode);
            }
            if (child3 == oldNode) {
                child3 = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }

    public static class Sequence4 extends BaseR {
        @Child RNode child1;
        @Child RNode child2;
        @Child RNode child3;
        @Child RNode child4;

        public Sequence4(ASTNode ast, RNode[] exprs) {
            super(ast);
            this.child1 = adoptChild(exprs[0]);
            this.child2 = adoptChild(exprs[1]);
            this.child3 = adoptChild(exprs[2]);
            this.child4 = adoptChild(exprs[3]);
        }

        @Override public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            child3.execute(frame);
            return child4.execute(frame);
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (child1 == oldNode) {
                child1 = newNode;
                return adoptInternal(newNode);
            }
            if (child2 == oldNode) {
                child2 = newNode;
                return adoptInternal(newNode);
            }
            if (child3 == oldNode) {
                child3 = newNode;
                return adoptInternal(newNode);
            }
            if (child4 == oldNode) {
                child4 = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }

    public static class Sequence5 extends BaseR {
        @Child RNode child1;
        @Child RNode child2;
        @Child RNode child3;
        @Child RNode child4;
        @Child RNode child5;

        public Sequence5(ASTNode ast, RNode[] exprs) {
            super(ast);
            this.child1 = adoptChild(exprs[0]);
            this.child2 = adoptChild(exprs[1]);
            this.child3 = adoptChild(exprs[2]);
            this.child4 = adoptChild(exprs[3]);
            this.child5 = adoptChild(exprs[4]);
        }

        @Override public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            child3.execute(frame);
            child4.execute(frame);
            return child5.execute(frame);
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (child1 == oldNode) {
                child1 = newNode;
                return adoptInternal(newNode);
            }
            if (child2 == oldNode) {
                child2 = newNode;
                return adoptInternal(newNode);
            }
            if (child3 == oldNode) {
                child3 = newNode;
                return adoptInternal(newNode);
            }
            if (child4 == oldNode) {
                child4 = newNode;
                return adoptInternal(newNode);
            }
            if (child5 == oldNode) {
                child5 = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }

    public static class Sequence6 extends BaseR {
        @Child RNode child1;
        @Child RNode child2;
        @Child RNode child3;
        @Child RNode child4;
        @Child RNode child5;
        @Child RNode child6;

        public Sequence6(ASTNode ast, RNode[] exprs) {
            super(ast);
            this.child1 = adoptChild(exprs[0]);
            this.child2 = adoptChild(exprs[1]);
            this.child3 = adoptChild(exprs[2]);
            this.child4 = adoptChild(exprs[3]);
            this.child5 = adoptChild(exprs[4]);
            this.child6 = adoptChild(exprs[5]);
        }

        @Override public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            child3.execute(frame);
            child4.execute(frame);
            child5.execute(frame);
            return child6.execute(frame);
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (child1 == oldNode) {
                child1 = newNode;
                return adoptInternal(newNode);
            }
            if (child2 == oldNode) {
                child2 = newNode;
                return adoptInternal(newNode);
            }
            if (child3 == oldNode) {
                child3 = newNode;
                return adoptInternal(newNode);
            }
            if (child4 == oldNode) {
                child4 = newNode;
                return adoptInternal(newNode);
            }
            if (child5 == oldNode) {
                child5 = newNode;
                return adoptInternal(newNode);
            }
            if (child6 == oldNode) {
                child6 = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

    }
}
