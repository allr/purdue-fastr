package r.nodes.truffle;

import com.oracle.truffle.api.nodes.*;

import r.nodes.*;
import r.runtime.*;

public class Sequence extends BaseR {

    @Children final RNode[] exprs;

    public Sequence(ASTNode ast, RNode[] exprs) {
        super(ast);
        this.exprs = exprs;
        adoptChildren(exprs);
    }

    @Override
    @ExplodeLoop
    public final Object execute(Frame frame) {

        Object res = null;
        for (RNode e : exprs) {
            res = null; // NOTE: this line is important, it allows the GC to clean-up temporaries
            res = e.execute(frame);
        }
        return res;
    }

    public static class Sequence2 extends BaseR {
        @Child RNode child1;
        @Child RNode child2;

        public Sequence2(ASTNode ast, RNode[] exprs) {
            super(ast);
            this.child1 = adoptChild(exprs[0]);
            this.child2 = adoptChild(exprs[1]);
        }

        @Override
        public final Object execute(Frame frame) {
            child1.execute(frame);
            return child2.execute(frame);
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

        @Override
        public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            return child3.execute(frame);
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

        @Override
        public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            child3.execute(frame);
            return child4.execute(frame);
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

        @Override
        public final Object execute(Frame frame) {
            child1.execute(frame);
            child2.execute(frame);
            child3.execute(frame);
            child4.execute(frame);
            return child5.execute(frame);
        }
    }
}
