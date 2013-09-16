package r.nodes.truffle;

import org.netlib.blas.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.runtime.*;

public abstract class MatrixOperation extends BaseR {

    @Child RNode left;
    @Child RNode right;

    private static final boolean USE_PRIMITIVE_ACCESS = false; // surprisingly primitive access in naive algo is not faster than getters

    public MatrixOperation(ASTNode ast, RNode left, RNode right) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
    }

    @Override
    public Object execute(Frame frame) {
        RAny l = (RAny) left.execute(frame);
        RAny r = (RAny) right.execute(frame);
        return execute(l, r);
    }

    public abstract Object execute(RAny l, RAny r);

    // TODO: optimize this
    public static RDouble dotProduct(ASTNode ast, RDouble l, RDouble r) { // a.k.a inner product, scalar product
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

    // TODO: optimize this
    public static RDouble dotProduct(RDouble x) { // a.k.a inner product, scalar product
        int m = x.size();
        double res = 0;
        for (int i = 0; i < m; i++) {
            double d = x.getDouble(i);
            res += d * d;
        }
        return RDouble.RDoubleFactory.getMatrixFor(new double[] {res}, 1, 1);
    }

    public static void checkNumeric(RAny l, RAny r, ASTNode ast) {
        // TODO: support also complex matrices
        checkNumeric(l, ast);
        checkNumeric(r, ast);
    }

    public static void checkNumeric(RAny x, ASTNode ast) {
        // TODO: support also complex matrices
        if (!(x instanceof RDouble || x instanceof RInt || x instanceof RLogical)) {
            throw RError.getNumericComplexMatrixVector(ast);
        }
    }

    public static class MatrixProduct extends MatrixOperation {
        public MatrixProduct(ASTNode ast, RNode left, RNode right) {
            super(ast, left, right);
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
                throw RError.getNonConformableArgs(ast);
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

            double[] res;
            if (RDouble.RDoubleUtils.hasNAorNaN(a) || RDouble.RDoubleUtils.hasNAorNaN(b) || m == 0 || n == 0 || p == 0) {
                if (USE_PRIMITIVE_ACCESS && a.size() > 1 && b.size() > 1) {
                    res =  matrixTimesMatrixPrimitive(((DoubleImpl) a.materialize()).getContent(), ((DoubleImpl) b.materialize()).getContent(), m, n, p);
                } else {
                    res = matrixTimesMatrixGetters(a, b, m, n, p);
                }
            } else {
                res = matrixTimesMatrixNative(((DoubleImpl) a.materialize()).getContent(), ((DoubleImpl) b.materialize()).getContent(), m, n, p);
            }

            return RDouble.RDoubleFactory.getFor(res, new int[] {m, p}, null);
        }


        public static double[] matrixTimesMatrixGetters(RDouble a, RDouble b, int m, int n, int p) {

            // a is m x n, b is n x p, result is m x p
            double[] content = new double[m * p];
            for (int j = 0; j < p; j++) {
                for (int i = 0; i < m; i++) {
                    double d = 0;
                    for (int k = 0; k < n; k++) {
                        d += a.getDouble(k * m + i) * b.getDouble(j * n + k);
                    }
                    content[j * m + i] = d;
                }
            }
            return content;
        }

        public static double[] matrixTimesMatrixPrimitive(double[] a, double[] b, int m, int n, int p) {
            // surprisingly, this is not any faster than using .getDouble

            // a is m x n, b is n x p, result is m x p
            double[] content = new double[m * p];

            for (int j = 0; j < p; j++) {
                for (int i = 0; i < m; i++) {
                    double d = 0;
                    for (int k = 0; k < n; k++) {
                        d += a[k * m + i] * b[j * n + k];
                    }
                    content[j * m + i] = d;
                }
            }
            return content;
        }

        public static double[] matrixTimesMatrixNative(double[] a, double[] b, int m, int n, int p) {

            double[] res = new double[m * p];
            BLAS.getInstance().dgemm("N", "N", m, p, n, 1.0, a, m, b, n, 0.0, res, m);
            return res;
        }

        @Override
        public Object execute(RAny l, RAny r) {

            checkNumeric(l, r, ast); // TODO: support also complex matrices
            RDouble ld = l.asDouble().materialize(); // FIXME: double materialization (again in matrixTimesMatrixNative)
            RDouble rd = r.asDouble().materialize();
            int[] ldims = ld.dimensions();
            int nldims = ldims == null ? 0 : ldims.length;
            int[] rdims = rd.dimensions();
            int nrdims = rdims == null ? 0 : rdims.length;

            if (nldims != 2) {
                if (nrdims != 2) {
                    return dotProduct(ast, ld, rd);
                } else {
                    return vectorTimesMatrix(ast, ld, rd);
                }
            } else {
                if (nrdims != 2) {
                    return matrixTimesVector(ast, ld, rd);
                } else {
                    // note - here we know that materializing ld, rd will give DoubleImpl - because both have dimensions
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
        public Object execute(RAny l, RAny r) {

            // TODO: support also complex matrices
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
