package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

public abstract class MatrixOperation extends BaseR {

    @Child RNode left;
    @Child RNode right;

    public MatrixOperation(ASTNode ast, RNode left, RNode right) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
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

        // FIXME: make sure that the compiler can optimize these loops
        // FIXME: could use better algorithms for large/sparse matrices
        public static RDouble vectorTimesMatrix(ASTNode ast, RDouble vector, RDouble matrix) {
            int[] dim = matrix.dimensions();
            int m = dim[0];
            int n = dim[1];
            int s = vector.size();

            if (s == m) {
                // treat vector as 1 x m (row), result is 1 x n
                double[] content = new double[n];
                for (int j = 0; j < n; j++) {
                    double d = 0;
                    for (int k = 0; k < m; k++) {
                        d += vector.getDouble(k) * matrix.getDouble(j * m + k);
                    }
                    content[j] = d;
                }
                return RDouble.RDoubleFactory.getFor(content, new int[] {1, n}, null);

            } else if (m == 1) {
                // treat vector as s x 1 (column), result is s x n
                double[] content = new double[s * n];
                for (int i = 0; i < s; i++) {
                    for (int j = 0; j < n; j++) {
                        content[j * s + i] = vector.getDouble(i) * matrix.getDouble(j);
                    }
                }
                return RDouble.RDoubleFactory.getFor(content, new int[] {s, n}, null);
            } else {
                throw RError.getNonConformableArgs(ast);
            }
        }

        public static RDouble matrixTimesVector(ASTNode ast, RDouble matrix, RDouble vector) {
            int[] dim = matrix.dimensions();
            int m = dim[0];
            int n = dim[1];
            int s = vector.size();

            if (s == n) {
                // treat vector as n x 1 (column), result is m x 1
                double[] content = new double[m];
                for (int i = 0; i < m; i++) {
                    double d = 0;
                    for (int k = 0; k < n; k++) {
                        d += matrix.getDouble(k * m + i) * vector.getDouble(k);
                    }
                    content[i] = d;
                }
                return RDouble.RDoubleFactory.getFor(content, new int[] {m, 1}, null);
            } else if (n == 1) {
                // treat vector as 1 x s (row), result is m x s
                double[] content = new double[m * s];
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < s; j++) {
                        content[ j * m + i] = matrix.getDouble(i) * vector.getDouble(j);
                    }
                }
                return RDouble.RDoubleFactory.getFor(content, new int[] {m, s}, null);
            } else {
                throw RError.getNonConformableArrays(ast);
            }
        }

        public static RDouble matrixTimesMatrix(ASTNode ast, RDouble a, RDouble b) {
            int[] dima = a.dimensions();
            int[] dimb = b.dimensions();

            int m = dima[0];
            int n = dima[1];
            if (n != dimb[0]) {
                throw RError.getNonConformableArgs(ast);
            }
            int p = dimb[1];

            // a is m x n, b is n x p, result is m x p
            double[] content = new double[m * p];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < p; j++) {
                    double d = 0;
                    for (int k = 0; k < n; k++) {
                        d += a.getDouble(k * m + i) * b.getDouble(j * n + k);
                    }
                    content[j * m + i] = d;
                }
            }
            return RDouble.RDoubleFactory.getFor(content, new int[] {m, p}, null);
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

            if (ldims == null) {
                if (rdims == null) {
                    return dotProduct(ast, ld, rd);
                } else {
                    return vectorTimesMatrix(ast, ld, rd);
                }
            } else {
                if (rdims == null) {
                    return matrixTimesVector(ast, ld, rd);
                } else {
                    return matrixTimesMatrix(ast, ld, rd);
                }
            }
        }
    }

    public static class OuterProduct extends MatrixOperation {
        public OuterProduct(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
        }

        @Override
        public Object execute(RContext context, RAny l, RAny r) {

            if (!((l instanceof RDouble || l instanceof RInt || l instanceof RLogical) &&
                            (r instanceof RDouble || r instanceof RInt || r instanceof RLogical))) {
                throw RError.getNumericComplexMatrixVector(ast);
            }
            RDouble ld = l.asDouble().materialize();
            RDouble rd = r.asDouble().materialize();

            int m = ld.size();
            int n = rd.size();
            double[] content = new double[m * n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    content[j * m + i] = ld.getDouble(i) * rd.getDouble(j);
                }
            }
            int[] ldims = ld.dimensions();
            int[] rdims = rd.dimensions();
            if (ldims == null && rdims == null) {
                return RDouble.RDoubleFactory.getFor(content, new int[] {m, n}, null);
            } else {
                Utils.nyi("unsupported case");
                return  null;
            }
        }
    }
}
