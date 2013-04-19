package r.nodes.truffle;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.data.internal.ScalarDoubleImpl;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.ReadMatrix.OptionNode;
import r.nodes.truffle.ReadMatrix.SelectorNode;

// TODO delete this file, its functionality has been replaced by UpdateArray

/**
 * Matrix in place update
 * <p/>
 * The general case rewrites itself either to a generic writer, scalar writer, or a double constant writer special
 * cases. If the scalar writer fails, it is rewritten to the generic case.
 */
public class UpdateMatrix extends BaseR {

    @Child private RNode matrixExpr;
    @Child private ReadMatrix.SelectorNode selIExpr;
    @Child private ReadMatrix.SelectorNode selJExpr;
    @Child private ReadMatrix.OptionNode dropExpr;
    @Child private ReadMatrix.OptionNode exactExpr;
    @Child protected RNode rhs;
    private final boolean subset;

    @Override public void replace0(RNode o, RNode n) {
        if (matrixExpr == o) matrixExpr = n;
        if (selIExpr == o) selIExpr = (SelectorNode) n;
        if (selJExpr == o) selJExpr = (SelectorNode) n;
        if (dropExpr == o) dropExpr = (OptionNode) n;
        if (exactExpr == o) exactExpr = (OptionNode) n;
        if (rhs == o) rhs = n;
    }

    private static final boolean DEBUG_M = false;

    /** Creates the new matrix updater. */
    public UpdateMatrix(ASTNode ast, boolean subset, RNode matrixExpr, ReadMatrix.SelectorNode selIExpr, ReadMatrix.SelectorNode selJExpr, ReadMatrix.OptionNode dropExpr,
            ReadMatrix.OptionNode exactExpr, RNode rhs) {
        super(ast);
        this.subset = subset;
        this.matrixExpr = adoptChild(matrixExpr);
        this.selIExpr = (SelectorNode) adoptChild(selIExpr);
        this.selJExpr = (SelectorNode) adoptChild(selJExpr);
        this.dropExpr = (OptionNode) adoptChild(dropExpr);
        this.exactExpr = (OptionNode) adoptChild(exactExpr);
        this.rhs = adoptChild(rhs);
    }

    /** Copy constructor used for node replacing. */
    protected UpdateMatrix(UpdateMatrix other) {
        this(other.ast, other.subset, other.matrixExpr, other.selIExpr, other.selJExpr, other.dropExpr, other.exactExpr, other.rhs);
    }

    /** Evaluates all prerequisites and selectors but the rhs value. */
    @Override public Object execute(Frame frame) {
        RAny matrix = (RAny) matrixExpr.execute(frame);
        ReadMatrix.Selector selI = selIExpr.executeSelector(frame);
        ReadMatrix.Selector selJ = selJExpr.executeSelector(frame);
        int drop = dropExpr.executeLogical(frame); // FIXME: what is the correct execution order of these args?
        int exact = exactExpr.executeLogical(frame);
        return evaluateValueAndExecute(frame, matrix, selI, selJ, drop, exact);
    }

    private static enum Failure {
        CONSTANT_DOUBLE_SCALAR, SCALAR, GENERIC
    }

    /** Evaluates the RHS value and based on it specializes to the generic case, scalar update, or a constant. */
    protected Object evaluateValueAndExecute(Frame frame, RAny matrix, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact) {
        RAny value = (RAny) rhs.execute(frame);
        if (!(value instanceof RArray)) {
            Utils.nyi("only array based rhs can be stored in a matrix");
        }
        RArray ary = (RArray) value;
        try {
            if (ary instanceof ScalarDoubleImpl) { // size == 1, type == double
                if (rhs instanceof Constant) { throw new UnexpectedResultException(Failure.CONSTANT_DOUBLE_SCALAR); }
                throw new UnexpectedResultException(Failure.SCALAR);
            }
            throw new UnexpectedResultException(Failure.GENERIC);
        } catch (UnexpectedResultException e) {
            UpdateMatrix u;
            switch ((Failure) e.getResult()) {
            case CONSTANT_DOUBLE_SCALAR:
                u = new UpdateMatrixWithDoubleConstant(this, (Double) ary.get(0));
                this.replace(u, "double scalar constant");
                return u.execute(matrix, selI, selJ, drop, exact, null);
            case SCALAR:
                u = new UpdateMatrixWithScalar(this);
                this.replace(u, "scalar value");
                return u.execute(matrix, selI, selJ, drop, exact, ary);
            default:
                u = new UpdateMatrixGeneric(this);
                this.replace(u, "scalar value");
                return u.execute(matrix, selI, selJ, drop, exact, ary);
            }
        }
    }

    protected Object execute(RAny matrix, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact, RArray value) {
        if (!(matrix instanceof RArray)) {
            Utils.nyi("unsupported base");
            // TODO: ERROR object of type 'XXX' is not subsettable
            return null;
        }
        RArray base = (RArray) matrix;
        int[] dim = base.dimensions();
        if (dim == null || dim.length != 2) { throw RError.getIncorrectDimensions(ast); }
        int m = dim[0];
        int n = dim[1];

        ReadMatrix.Selector selectorI = selI;
        ReadMatrix.Selector selectorJ = selJ;

        for (;;) {
            try {
                return execute(base, m, n, selectorI, selectorJ, drop, exact, value);
            } catch (UnexpectedResultException e) {
                ReadMatrix.Selector failedSelector = (ReadMatrix.Selector) e.getResult();
                if (failedSelector == selectorI) {
                    if (DEBUG_M) Utils.debug("Selector I failed in UpdateMatrix.execute, replacing.");
                    RAny index = selectorI.getIndex();
                    replaceChild(selIExpr, ReadMatrix.createSelectorNode(ast, subset, index, selIExpr.child, false, selectorI.getTransition()));
                    selectorI = selIExpr.executeSelector(index);
                } else {
                    // failedSelector == selectorJ
                    if (DEBUG_M) Utils.debug("Selector J failed in UpdateMatrix.execute, replacing.");
                    RAny index = selectorJ.getIndex();
                    replaceChild(selJExpr, ReadMatrix.createSelectorNode(ast, subset, index, selJExpr.child, false, selectorJ.getTransition()));
                    selectorJ = selJExpr.executeSelector(index);
                }
            }
        }
    }

    protected Object execute(RArray base, int m, int n, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact, RArray value) throws UnexpectedResultException {
        throw new Error("UNREACHABLE");
    }

    /** An update of the matrix with a scalar value, requires no looping over the rhs values. */
    public static class UpdateMatrixWithScalar extends UpdateMatrix {

        protected UpdateMatrixWithScalar(UpdateMatrix other) {
            super(other);
        }

        @Override protected Object evaluateValueAndExecute(Frame frame, RAny matrix, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact) {
            RAny value = (RAny) rhs.execute(frame);
            if (!(value instanceof RArray)) {
                Utils.nyi("only array based rhs can be stored in a matrix");
            }
            RArray ary = (RArray) value;
            try {
                if ((ary.size() != 1) || (!(ary instanceof RDouble))) { // we are expecting double scalar
                    throw new UnexpectedResultException(null);
                }
                return super.execute(matrix, selI, selJ, drop, exact, ary);
            } catch (UnexpectedResultException e) {
                UpdateMatrix u = new UpdateMatrixGeneric(this);
                this.replace(u, "expected scalar failed");
                return u.execute(matrix, selI, selJ, drop, exact, ary);
            }
        }

        @Override protected Object execute(RArray base, int m, int n, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact, RArray value) throws UnexpectedResultException {
            Object v = value.get(0);
            selI.start(m, ast);
            selJ.start(n, ast);
            int nm = selI.size();
            int nn = selJ.size();
            // not necessary, value size is 1
            //if (nm * nn % value.size() != 0)
            //    throw RError.getNotMultipleReplacement(getAST());
            for (int ni = 0; ni < nm; ni++) {
                int i = selI.nextIndex(ast);
                if (i != RInt.NA) {
                    selJ.restart();
                    for (int nj = 0; nj < nn; nj++) {
                        int j = selJ.nextIndex(ast);
                        if (j != RInt.NA) {
                            base.set(j * m + i, v);
                        } else {
                            Utils.setNA(base, j * m + i);
                        }
                    }
                } else {
                    for (int nj = 0; nj < nn; nj++) {
                        Utils.setNA(base, nj * nm + ni);
                    }
                }
            }
            return RNull.getNull();
        }
    }

    /**
     * Specialization where the matrix is updated with a double constant value. No subsequent evaluation of the rhs
     * value.
     */
    public static class UpdateMatrixWithDoubleConstant extends UpdateMatrix {

        final Double value;

        protected UpdateMatrixWithDoubleConstant(UpdateMatrix other, double value) {
            super(other);
            this.value = value;
        }

        @Override protected Object evaluateValueAndExecute(Frame frame, RAny matrix, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact) {
            return execute(matrix, selI, selJ, drop, exact, null);
        }

        @Override protected Object execute(RArray base, int m, int n, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact, RArray value) throws UnexpectedResultException {
            assert (value == null);
            selI.start(m, ast);
            selJ.start(n, ast);
            int nm = selI.size();
            int nn = selJ.size();
            for (int ni = 0; ni < nm; ni++) {
                int i = selI.nextIndex(ast);
                if (i != RInt.NA) {
                    selJ.restart();
                    for (int nj = 0; nj < nn; nj++) {
                        int j = selJ.nextIndex(ast);
                        if (j != RInt.NA) {
                            base.set(j * m + i, this.value);
                        } else {
                            Utils.setNA(base, j * m + i);
                        }
                    }
                } else {
                    for (int nj = 0; nj < nn; nj++) {
                        Utils.setNA(base, nj * nm + ni);
                    }
                }
            }
            return RNull.getNull();
        }
    }

    /** Generic case of matrix update. */
    public static class UpdateMatrixGeneric extends UpdateMatrix {

        protected UpdateMatrixGeneric(UpdateMatrix other) {
            super(other);
        }

        @Override protected Object evaluateValueAndExecute(Frame frame, RAny matrix, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact) {
            RAny value = (RAny) rhs.execute(frame);
            if (!(value instanceof RArray)) {
                Utils.nyi("only array based rhs can be stored in a matrix");
            }
            RArray ary = (RArray) value;
            return execute(matrix, selI, selJ, drop, exact, ary);
        }

        @Override protected Object execute(RArray base, int m, int n, ReadMatrix.Selector selI, ReadMatrix.Selector selJ, int drop, int exact, RArray value) throws UnexpectedResultException {
            selI.start(m, ast);
            selJ.start(n, ast);
            int nm = selI.size();
            int nn = selJ.size();
            if (nm * nn % value.size() != 0) { throw RError.getNotMultipleReplacement(getAST()); }
            int rhsIdx = 0;
            for (int ni = 0; ni < nm; ni++) {
                int i = selI.nextIndex(ast);
                if (i != RInt.NA) {
                    selJ.restart();
                    for (int nj = 0; nj < nn; nj++) {
                        int j = selJ.nextIndex(ast);
                        if (j != RInt.NA) {
                            base.set(j * m + i, value.get(rhsIdx));
                            ++rhsIdx;
                        } else {
                            Utils.setNA(base, j * m + i);
                        }
                    }
                } else {
                    for (int nj = 0; nj < nn; nj++) {
                        Utils.setNA(base, nj * nm + ni);
                    }
                }
            }
            return RNull.getNull();
        }
    }
}
