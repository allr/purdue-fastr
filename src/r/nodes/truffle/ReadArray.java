package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import r.Utils;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.Selector.SelectorNode;

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

    /**
     * determines whether [] or [[]] operators were used (subset == []).
     */
    final boolean subset;

    /**
     * LHS of the selection operator.
     */
    @Child RNode lhs;

    /**
     * Selector nodes for respective dimensions. These are likely to be rewritten.
     */
    @Children SelectorNode[] selectorExprs;

    /**
     * Drop expression, assumed true or false.
     */
    @Child OptionNode dropExpr;

    @Child OptionNode exactExpr;

    final int[] selSizes;
    final int[] idx;
    final int[] selIdx;
    final Selector[] selectorVals;

    /**
     * Constructor from scratch.
     */
    public ReadArray(ASTNode ast, boolean subset, RNode lhs, SelectorNode[] selectorExprs, OptionNode dropExpr, OptionNode exactExpr) {
        super(ast);
        this.subset = subset;
        this.lhs = adoptChild(lhs);
        this.selectorExprs = adoptChildren(selectorExprs);
        this.dropExpr = adoptChild(dropExpr);
        this.exactExpr = adoptChild(exactExpr);
        this.selSizes = new int[selectorExprs.length];
        this.idx = new int[selectorExprs.length];
        this.selIdx = new int[selectorExprs.length];
        this.selectorVals = new Selector[selectorExprs.length];
    }

    /**
     * Copy constructor used in node replacements.
     */
    public ReadArray(ReadArray other) {
        this(other.ast, other.subset, other.lhs, other.selectorExprs, other.dropExpr, other.exactExpr);
    }

    /**
     * Execute method which evaluates the lhs, selectors and optional expressions, checks that the
     * array selection can proceed and then proceeds optionally replacing the falling selectors.
     *
     * The valued variant of execute is called for the production of the result, on failure the
     * responsible selector is replaced and the valued variant is called again.
     */
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
     * Abstract method that should create the selection of the array and return it.
     *
     * The given selectors may fail resulting in the exception being thrown, in which case they will
     * be replaced with more general ones.
     */
    public abstract Object execute(RArray source, boolean drop, int exact) throws UnexpectedResultException;

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
    public static class GeneralizedRead extends ReadArray {

        public GeneralizedRead(ASTNode ast, boolean subset, RNode lhs, SelectorNode[] selectors, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, subset, lhs, selectors, dropExpr, exactExpr);
        }

        public GeneralizedRead(ReadArray other) {
            super(other);
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
        @Override
        public Object execute(RArray source, boolean drop, int exact) throws UnexpectedResultException {
            Selector.initializeSelectors(source, selectorVals, ast, selSizes);
            int[] destDim = Selector.calculateDestinationDimensions(selSizes, !subset || drop);
            int destSize = Selector.calculateSizeFromSelectorSizes(selSizes);
            if (!subset && (destSize > 1)) {
                throw RError.getSelectMoreThanOne(getAST());
            }
            RArray dest = Utils.createArray(source, destSize, destDim, null, null); // drop attributes
            // fill in the index vector
            for (int i = 0; i < idx.length; ++i) {
                idx[i] = selectorVals[i].nextIndex(ast);
                selIdx[i] = 1; // start at one so that overflow and carry works
            }
            // loop over the dest offset and update the index vector
            for (int offset = 0; offset < destSize; ++offset) {
                int sourceOffset = Selector.calculateSourceOffset(source, idx);
                if (sourceOffset == RInt.NA) {
                    Utils.setNA(dest, offset);
                } else {
                    dest.set(offset, source.getRef(sourceOffset));
                }
                Selector.increment(idx, selIdx, selSizes, selectorVals, ast);
            }
            return dest;
        }
    }

    /**
     * Matrix specialization for the array selection.
     *
     * Works on 2D arrays. This code used to be in ReadMatrix class. No rewrites are being done at
     * the moment.
     */
    public static class MatrixRead extends ReadArray {

        public MatrixRead(ASTNode ast, boolean subset, RNode lhs, SelectorNode[] selectors, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, subset, lhs, selectors, dropExpr, exactExpr);
        }

        public MatrixRead(ReadArray other) {
            super(other);
        }

        @Override
        public Object execute(RArray source, boolean drop, int exact) throws UnexpectedResultException {
            Selector selI = selectorVals[0];
            Selector selJ = selectorVals[1];
            int[] ndim = source.dimensions();
            int m = ndim[0];
            int n = ndim[1];
            selI.start(m, ast);
            selJ.start(n, ast);
            int nm = selI.size();
            int nn = selJ.size();
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
            for (int nj = 0; nj < nn; nj++) {
                int j = selJ.nextIndex(ast);
                if (j != RInt.NA) {
                    selI.restart();
                    for (int ni = 0; ni < nm; ni++) {
                        int offset = nj * nm + ni;
                        int i = selI.nextIndex(ast);
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
            return res;
        }
    }

    public static class MatrixSubscript extends ReadArray {

        public MatrixSubscript(ASTNode ast, RNode lhs, SelectorNode[] selectors, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, false, lhs, selectors, dropExpr, exactExpr);
        }

        public MatrixSubscript(ReadArray other) {
            super(other);
            assert Utils.check(!other.subset);
        }

        @Override
        public Object execute(RArray base, boolean drop, int exact) throws UnexpectedResultException {
            Selector selI = selectorVals[0];
            Selector selJ = selectorVals[1];
            int[] ndim = base.dimensions();
            int m = ndim[0];
            int n = ndim[1];
            selI.start(m, ast);
            selJ.start(n, ast);
            int i = selI.nextIndex(ast);
            int j = selJ.nextIndex(ast);
            if (i != RInt.NA && j != RInt.NA) {
                int offset = j * m + i;
                if (!(base instanceof RList)) {
                    return base.boxedGet(offset);
                } else {
                    return ((RList) base).getRAny(offset);
                }
            } else {
                return Utils.getBoxedNA(base);
            }
        }
    }

}
