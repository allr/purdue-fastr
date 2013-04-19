package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.Selector.SelectorNode;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

/**
 * A multi-dimensional read.
 *
 * arrayname '[' [first index] , [second index] { , [ other index]} [ , drop = ] ']'
 *
 * There are no node rewrites, but the selection operator nodes have their selector nodes which tend
 * to be overwritten.
 *
 * The special case for matrices is determined statically when # of dimensions is 2.
 */
public abstract class ReadArray extends BaseR {

    final boolean subset;

    @Child RNode lhs;
    @Child OptionNode dropExpr;
    @Child OptionNode exactExpr;

    public ReadArray(ASTNode ast, boolean subset, RNode lhs, OptionNode dropExpr, OptionNode exactExpr) {
        super(ast);
        this.subset = subset;
        this.lhs = adoptChild(lhs);
        this.dropExpr = adoptChild(dropExpr);
        this.exactExpr = adoptChild(exactExpr);
    }

    public ReadArray(ReadArray other) {
        this(other.ast, other.subset, other.lhs, other.dropExpr, other.exactExpr);
    }


    // =================================================================================================================
    // OptionNode
    // =================================================================================================================

    public static OptionNode createConstantOptionNode(final ASTNode ast, final int value) {
        return new OptionNode(ast) {

            @Override
            public int executeLogical(Frame frame) {
                return value;
            }
        };
    }

    public static OptionNode createOptionNode(final ASTNode ast, final RNode node, int defaultValue) {
        if (node == null) {
            return createConstantOptionNode(ast, defaultValue);
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny value = (RAny) node.execute(null);
            return createConstantOptionNode(ast, value.asLogical().getLogical(0));
        }
        return new OptionNode(ast) {

            @Child RNode child = adoptChild(node);

            @Override
            public int executeLogical(Frame frame) {
                RAny value = (RAny) child.execute(frame);
                return value.asLogical().getLogical(0);
            }

        };
    }

    public static OptionNode createDropOptionNode(ASTNode ast, RNode node) {
        return createOptionNode(ast, node, RLogical.TRUE);
    }

    public static OptionNode createExactOptionNode(ASTNode ast, RNode node) {
        return createOptionNode(ast, node, RLogical.NA);
    }

    public abstract static class OptionNode extends BaseR {

        public OptionNode(ASTNode ast) {
            super(ast);
        }

        @Override
        public Object execute(Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public abstract int executeLogical(Frame frame);
    }

    // =================================================================================================================
    // Specialized forms
    // =================================================================================================================

    /**
     * Generalized selector operator that works with arrays of arbitraty number of dimensions.
     *
     * Uses the selector index mechanism and reverse incrementing to create the selection result.
     *
     * At the moment does not perform any rewriting as the matrix - array distinction is known
     * static time from the parser.
     */
    public static class GenericRead extends ReadArray {

        final int[] offsets;
        final int[] selSizes;
        @Children SelectorNode[] selectorExprs;
        final Selector[] selectorVals;

        public GenericRead(ASTNode ast, boolean subset, RNode lhs, SelectorNode[] selectorExprs, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, subset, lhs, dropExpr, exactExpr);
            this.selectorExprs = adoptChildren(selectorExprs);
            this.offsets = new int[selectorExprs.length + 1];
            this.selSizes = new int[selectorExprs.length];
            this.selectorVals = new Selector[selectorExprs.length];
        }

        @Override
        public Object execute(Frame frame) {
            RAny lhsVal = (RAny) lhs.execute(frame);
            for (int i = 0; i < selectorVals.length; ++i) {
                selectorVals[i] = selectorExprs[i].executeSelector(frame);
            }
            boolean dropVal = dropExpr.executeLogical(frame) != 0;  // FIXME: what is the correct execution order of these args?
            int exactVal = exactExpr.executeLogical(frame);

            if (!(lhsVal instanceof RArray)) {
                throw RError.getObjectNotSubsettable(ast, lhsVal.typeOf());
            }
            RArray array = (RArray) lhsVal;
            int[] dim = array.dimensions();
            if (dim == null || dim.length != selectorExprs.length) {
                throw RError.getIncorrectDimensions(getAST());
            }
            return executeLoop(array, dropVal, exactVal);
        }

        /**
         * Execute method which evaluates the lhs, selectors and optional expressions, checks that the
         * array selection can proceed and then proceeds optionally replacing the falling selectors.
         *
         * The valued variant of execute is called for the production of the result, on failure the
         * responsible selector is replaced and the valued variant is called again.
         */

        protected final Object executeLoop(RArray array, boolean dropVal, int exactVal) {
            while (true) {
                try {
                    return execute(array, dropVal, exactVal);
                } catch (UnexpectedResultException e) {
                    Selector failedSelector = (Selector) e.getResult();
                    for (int i = 0; i < selectorVals.length; ++i) {
                        if (selectorVals[i] == failedSelector) {
                            RAny index = failedSelector.getIndex();
                            SelectorNode newSelector = Selector.createSelectorNode(ast, subset, index, selectorExprs[i], false, failedSelector.getTransition());
                            replaceChild(selectorExprs[i], newSelector);
                            selectorVals[i] = newSelector.executeSelector(index);
                        }
                    }
                }
            }
        }

        /**
         * Returns the selection array or vector.
         *
         * The selSizes array contains sizes of the selectors (number of elements that will be
         * returned by it). The idx array contains the indices returned by the selectors (that is
         * the indices used to compute the source offset).
         *
         * The selIdx array contains the position in the selector (when this is equal to the
         * selector size the selector has overflown).
         */
        public Object execute(RArray source, boolean drop, int exact) throws UnexpectedResultException {
            int[] sourceDim = source.dimensions();
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, sourceDim, selSizes, ast);
            int[] destDim = Selector.calculateDestinationDimensions(selSizes, !subset || drop);
            int destSize = Selector.calculateSizeFromSelectorSizes(selSizes);

            RArray dest = Utils.createArray(source, destSize, destDim, null, null); // drop attributes
            if (destSize == 0) {
                return dest;
            }
            int offset = 0;
            for (;;) {
                int sourceOffset = offsets[0];
                if (sourceOffset == RInt.NA) {
                    Utils.setNA(dest, offset);
                } else {
                    dest.set(offset, source.getRef(sourceOffset));
                }
                offset++;
                if (offset < destSize) {
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, sourceDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, sourceDim, selectorVals, ast);
                    }
                } else {
                    break;
                }
            }
            return dest;
        }
    }

    // reading with the last dimension set only (e.g. x[,,i])
    public static class ArrayColumnSubset extends MatrixRead {

        @Child RNode columnExpr;
        final int nSelectors;

        public ArrayColumnSubset(ASTNode ast, RNode lhs, int nSelectors, RNode columnExpr, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, true, lhs, null, null, dropExpr, exactExpr);
            this.columnExpr = adoptChild(columnExpr);
            this.nSelectors = nSelectors;
            assert Utils.check(nSelectors > 2);  // we could support 2 as well, but there is MatrixColumnSubset for that
        }

        @Override
        public Object execute(Frame frame) {

            RAny lhsVal = (RAny) lhs.execute(frame);
            RAny colVal = (RAny) columnExpr.execute(frame);
            boolean dropVal = dropExpr.executeLogical(frame) != RLogical.FALSE;  // FIXME: what is the correct execution order of these args?
            int exactVal = exactExpr.executeLogical(frame);

            if (!(lhsVal instanceof RArray)) {
                throw RError.getObjectNotSubsettable(ast, lhsVal.typeOf());
            }
            RArray array = (RArray) lhsVal;
            int[] dim = array.dimensions();
            if (dim == null || dim.length != nSelectors) {
                throw RError.getIncorrectDimensions(getAST());
            }
            int n = dim[nSelectors - 1]; // limit for the column (last dimension)

            try {
                int col;
                if (colVal instanceof ScalarIntImpl) {
                    col = ((ScalarIntImpl) colVal).getInt();
                } else if (colVal instanceof ScalarDoubleImpl) {
                    col = Convert.double2int(((ScalarDoubleImpl) colVal).getDouble());
                } else {
                    throw new UnexpectedResultException(null);
                }
                if (col > n || col <= 0) {
                    throw new UnexpectedResultException(null);
                }

                int[] ndim;
                if (dropVal) {
                    ndim = new int[nSelectors - 1];
                } else {
                    ndim = new int[nSelectors];
                    ndim[nSelectors - 1] = 1;
                }
                int m = 1;  // size of the result
                for (int i = 0; i < ndim.length; i++) {
                    int d = dim[i];
                    ndim[i] = d;
                    m *= d;
                }

                // note: also could be lazy here
                RArray res = Utils.createArray(array, m, ndim, null, null); // drop attributes
                int offset = (col - 1) * m; // note: col is 1-based
                for (int i = 0; i < m; i++) {
                    res.set(i, array.getRef(offset + i));
                }
                return res;
            } catch (UnexpectedResultException e) {
                SelectorNode[] selectorExprs = new SelectorNode[nSelectors];
                for (int i = 0; i < nSelectors - 1; i++) {
                    selectorExprs[i] = Selector.createSelectorNode(ast, true, null);
                }
                selectorExprs[nSelectors - 1] = Selector.createSelectorNode(ast, true, columnExpr);
                GenericRead gr = new GenericRead(ast, true, lhs, selectorExprs, dropExpr, exactExpr);
                replace(gr, "install GenericRead from ArrayColumnSubset");
                for (int i = 0; i < nSelectors - 1; i++) {
                    gr.selectorVals[i] = selectorExprs[i].executeSelector(frame);
                }
                gr.selectorVals[nSelectors - 1] = selectorExprs[nSelectors - 1].executeSelector(colVal);
                return gr.executeLoop(array, dropVal, exactVal);
            }
        }
    }

    /**
     * Matrix specialization for the array selection.
     */
    public static class MatrixRead extends ReadArray {

        @Child SelectorNode selectorIExpr;
        @Child SelectorNode selectorJExpr;

        public MatrixRead(ASTNode ast, boolean subset, RNode lhs, SelectorNode selectorIExpr, SelectorNode selectorJExpr, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, subset, lhs, dropExpr, exactExpr);
            this.selectorIExpr = adoptChild(selectorIExpr);
            this.selectorJExpr = adoptChild(selectorJExpr);
        }

        public MatrixRead(ReadArray other) {
            super(other);
        }

        @Override
        public Object execute(Frame frame) {
            RAny lhsVal = (RAny) lhs.execute(frame);
            Selector selectorI = selectorIExpr.executeSelector(frame);
            Selector selectorJ = selectorJExpr.executeSelector(frame);
            boolean dropVal = dropExpr.executeLogical(frame) != 0;  // FIXME: what is the correct execution order of these args?
            int exactVal = exactExpr.executeLogical(frame);

            if (!(lhsVal instanceof RArray)) {
                throw RError.getObjectNotSubsettable(ast, lhsVal.typeOf());
            }
            RArray array = (RArray) lhsVal;
            int[] dim = array.dimensions();
            if (dim == null || dim.length != 2) {
                throw RError.getIncorrectDimensions(getAST());
            }
            return executeLoop(array, selectorI, selectorJ, dropVal, exactVal);
        }

        public Object executeLoop(RArray array, Selector selectorI, Selector selectorJ, boolean dropVal, int exactVal) {
            Selector selI = selectorI;
            Selector selJ = selectorJ;
            for (;;) {
                try {
                    return execute(array, selI, selJ, dropVal, exactVal);
                } catch (UnexpectedResultException e) {
                    Selector failedSelector = (Selector) e.getResult();
                    if (failedSelector == selI) {
                        RAny index = selI.getIndex();
                        replaceChild(selectorIExpr, Selector.createSelectorNode(ast, subset, index, selectorIExpr.child, false, selectorI.getTransition()));
                        selI = selectorIExpr.executeSelector(index);
                    } else {
                        // failedSelector == selectorJ
                        RAny index = selJ.getIndex();
                        replaceChild(selectorJExpr, Selector.createSelectorNode(ast, subset, index, selectorJExpr.child, false, selectorJ.getTransition()));
                        selJ = selectorJExpr.executeSelector(index);
                    }
                }
            }
        }

        public Object execute(RArray source, Selector selectorI, Selector selectorJ, boolean drop, int exact) throws UnexpectedResultException {
            int[] ndim = source.dimensions();
            int m = ndim[0];
            int n = ndim[1];
            selectorI.start(m, ast);
            selectorJ.start(n, ast);
            int nm = selectorI.size();
            int nn = selectorJ.size();
            boolean mayHaveNA = selectorI.mayHaveNA() || selectorJ.mayHaveNA();
            int nsize = nm * nn;
            if (!subset && (nsize > 1)) {
                throw RError.getSelectMoreThanOne(getAST());
            }
            if ((nm != 1 && nn != 1) || (subset && !drop)) {
                ndim = new int[]{nm, nn};
            } else {
                ndim = null;
            }
            RArray res = Utils.createArray(source, nsize, ndim, null, null); // drop attributes
            if (!mayHaveNA) {
                int resoffset = 0;
                for (int nj = 0; nj < nn; nj++) {
                    int j = selectorJ.nextIndex(ast);
                    int srcoffset = j * m;
                    for (int ni = 0; ni < nm; ni++) {
                        int i = selectorI.nextIndex(ast);
                        Object value = source.getRef(srcoffset + i); // FIXME: check overflow? (the same is at many locations, whenever indexing a matrix)
                        res.set(resoffset++, value);
                    }
                    selectorI.restart();
                }
            } else {
                for (int nj = 0; nj < nn; nj++) {
                    int j = selectorJ.nextIndex(ast);
                    if (j != RInt.NA) {
                        selectorI.restart();
                        for (int ni = 0; ni < nm; ni++) {
                            int offset = nj * nm + ni;
                            int i = selectorI.nextIndex(ast);
                            if (i != RInt.NA) {
                                Object value;
                                value = source.getRef(j * m + i); // FIXME: check overflow? (the same is at many locations, whenever indexing a matrix)
                                res.set(offset, value);
                            } else {
                                Utils.setNA(res, offset);
                            }
                        }
                    } else {
                        for (int ni = 0; ni < nm; ni++) {
                            Utils.setNA(res, nj * nm + ni);
                        }
                    }
                }
            }

            return res;
        }

    }

    public static class MatrixSubscript extends MatrixRead {

        public MatrixSubscript(ASTNode ast, RNode lhs, SelectorNode selectorIExpr, SelectorNode selectorJExpr, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, false, lhs, selectorIExpr, selectorJExpr, dropExpr, exactExpr);
        }

        @Override
        public Object execute(RArray base, Selector selectorI, Selector selectorJ, boolean drop, int exact) throws UnexpectedResultException {
            int[] ndim = base.dimensions();
            int m = ndim[0];
            int n = ndim[1];
            selectorI.start(m, ast);
            selectorJ.start(n, ast);
            int i = selectorI.nextIndex(ast);
            int j = selectorJ.nextIndex(ast);
            assert Utils.check(i != RInt.NA && j != RInt.NA); // ensured by subscript selectors
            int offset = j * m + i;
            if (!(base instanceof RList)) {
                return base.boxedGet(offset);
            } else {
                return ((RList) base).getRAny(offset);
            }
        }
    }

    public static class MatrixColumnSubset extends MatrixRead {

        @Child RNode columnExpr;

        public MatrixColumnSubset(ASTNode ast, RNode lhs, RNode columnExpr, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, true, lhs, null, null, dropExpr, exactExpr);
            this.columnExpr = adoptChild(columnExpr);
        }

        @Override
        public Object execute(Frame frame) {

            RAny lhsVal = (RAny) lhs.execute(frame);
            RAny colVal = (RAny) columnExpr.execute(frame);
            boolean dropVal = dropExpr.executeLogical(frame) != RLogical.FALSE;  // FIXME: what is the correct execution order of these args?
            int exactVal = exactExpr.executeLogical(frame);

            if (!(lhsVal instanceof RArray)) {
                throw RError.getObjectNotSubsettable(ast, lhsVal.typeOf());
            }
            RArray array = (RArray) lhsVal;
            int[] dim = array.dimensions();
            if (dim == null || dim.length != 2) {
                throw RError.getIncorrectDimensions(getAST());
            }
            int m = dim[0];
            int n = dim[1];

            try {
                int col;
                if (colVal instanceof ScalarIntImpl) {
                    col = ((ScalarIntImpl) colVal).getInt();
                } else if (colVal instanceof ScalarDoubleImpl) {
                    col = Convert.double2int(((ScalarDoubleImpl) colVal).getDouble());
                } else {
                    throw new UnexpectedResultException(null);
                }
                if (col > n || col <= 0) {
                    throw new UnexpectedResultException(null);
                }

                int[] ndim;
                if (dropVal) {
                    ndim = null;
                } else {
                    ndim = new int[] {m, 1};
                }

                // note: also could be lazy here
                RArray res = Utils.createArray(array, m, ndim, null, null); // drop attributes
                int offset = (col - 1) * m; // note: col is 1-based
                for (int i = 0; i < m; i++) {
                    res.set(i, array.getRef(offset + i));
                }
                return res;
            } catch (UnexpectedResultException e) {
                SelectorNode selIExpr = Selector.createSelectorNode(ast, true, null);
                SelectorNode selJExpr = Selector.createSelectorNode(ast, true, columnExpr);
                MatrixRead mr = new MatrixRead(ast, true, lhs, selIExpr, selJExpr, dropExpr, exactExpr);
                replace(mr, "install MatrixRead from MatrixColumnSubset");
                Selector selI = selIExpr.executeSelector(frame);
                Selector selJ = selJExpr.executeSelector(colVal);
                return mr.executeLoop(array, selI, selJ, dropVal, exactVal);
            }
        }
    }

    public static class MatrixRowSubset extends MatrixRead {

        @Child RNode rowExpr;

        public MatrixRowSubset(ASTNode ast, RNode lhs, RNode rowExpr, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, true, lhs, null, null, dropExpr, exactExpr);
            this.rowExpr = adoptChild(rowExpr);
        }

        @Override
        public Object execute(Frame frame) {

            RAny lhsVal = (RAny) lhs.execute(frame);
            RAny rowVal = (RAny) rowExpr.execute(frame);
            boolean dropVal = dropExpr.executeLogical(frame) != RLogical.FALSE;  // FIXME: what is the correct execution order of these args?
            int exactVal = exactExpr.executeLogical(frame);

            if (!(lhsVal instanceof RArray)) {
                throw RError.getObjectNotSubsettable(ast, lhsVal.typeOf());
            }
            RArray array = (RArray) lhsVal;
            int[] dim = array.dimensions();
            if (dim == null || dim.length != 2) {
                throw RError.getIncorrectDimensions(getAST());
            }
            int m = dim[0];
            int n = dim[1];

            try {
                int row;
                if (rowVal instanceof ScalarIntImpl) {
                    row = ((ScalarIntImpl) rowVal).getInt();
                } else if (rowVal instanceof ScalarDoubleImpl) {
                    row = Convert.double2int(((ScalarDoubleImpl) rowVal).getDouble());
                } else {
                    throw new UnexpectedResultException(null);
                }
                if (row > n || row <= 0) {
                    throw new UnexpectedResultException(null);
                }

                int[] ndim;
                if (dropVal) {
                    ndim = null;
                } else {
                    ndim = new int[] {1, n};
                }

                // note: also could be lazy here
                RArray res = Utils.createArray(array, n, ndim, null, null); // drop attributes
                int offset = row - 1;
                for (int i = 0; i < n; i++) {
                    res.set(i, array.getRef(offset));
                    offset += m;
                }
                return res;
            } catch (UnexpectedResultException e) {
                SelectorNode selIExpr = Selector.createSelectorNode(ast, true, rowExpr);
                SelectorNode selJExpr = Selector.createSelectorNode(ast, true, null);
                MatrixRead nn = new MatrixRead(ast, true, lhs, selIExpr, selJExpr, dropExpr, exactExpr);
                replace(nn, "install MatrixRead from MatrixRowSubset");
                Selector selI = selIExpr.executeSelector(rowVal);
                Selector selJ = selJExpr.executeSelector(frame);
                return nn.executeLoop(array, selI, selJ, dropVal, exactVal);
            }
        }
    }
}
