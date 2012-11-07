package r.nodes.truffle;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.runtime.*;

public abstract class MatrixOperation extends BaseR {

    @Stable RNode left;
    @Stable RNode right;

    public MatrixOperation(ASTNode ast, RNode left, RNode right) {
        super(ast);
        this.left = updateParent(left);
        this.right = updateParent(right);
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RAny l = (RAny) left.execute(context, frame);
        RAny r = (RAny) right.execute(context, frame);
        return execute(context, l, r);
    }

    public abstract Object execute(RContext context, RAny l, RAny r);

    // FIXME: unoptimized
    public static class MatrixProduct extends MatrixOperation {
        public MatrixProduct(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
        }

        public static RDouble dotProduct(ASTNode ast, RDouble l, RDouble r) {
            int m = l.size();
            if (m != r.size()) {
                throw RError.getNonConformableArgs(ast);
            }
            double res = 0;
            for (int i = 0; i < m; i++) {
                res += l.getDouble(i) * r.getDouble(i);
            }
            return RDouble.RDoubleFactory.getMatrixFor(new double[] {res}, 1, 1);
        }

        @Override
        public Object execute(RContext context, RAny l, RAny r) {

            if (!((l instanceof RDouble || l instanceof RInt || l instanceof RLogical) &&
                            (r instanceof RDouble || r instanceof RInt || r instanceof RLogical))) {
                throw RError.getNumericComplexMatrixVector(ast);
            }
            RDouble ld = l.asDouble().materialize();
            RDouble rd = r.asDouble().materialize();
            int[] ldims = ld.dimensions();
            int[] rdims = rd.dimensions();

            if (ldims == null && rdims == null) {
                return dotProduct(ast, ld, rd);
            }

            Utils.nyi("case not yet implemented");
            return null;
        }
    }
}
