package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import r.Utils;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;

import javax.xml.crypto.KeySelectorResult;
import java.util.Arrays;

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
     * determines whether [] or [[]] operators were used (subset == [])
     */
    final boolean subset;

    /**
     * LHS of the selection operator.
     */
    @Child RNode lhs;

    /**
     * Selector nodes for respective dimensions. These are likely to be rewritten.
     */
    @Children Selector.SelectorNode[] selectors;

    /**
     * Drop expression, assumed true or false
     */
    @Child OptionNode drop;

    @Child OptionNode exact;

    /**
     * Constructor from scratch.
     */
    public ReadArray(ASTNode ast, boolean subset, RNode lhs, Selector.SelectorNode[] selectors, OptionNode dropExpr, OptionNode exactExpr) {
        super(ast);
        this.subset = subset;
        this.lhs = adoptChild(lhs);
        this.selectors = new Selector.SelectorNode[selectors.length];
        for (int i = 0; i < selectors.length; ++i)
            this.selectors[i] = adoptChild(selectors[i]);
        this.drop = adoptChild(dropExpr);
        this.exact = adoptChild(exactExpr);
    }

    /**
     * Copy constructor used in node replacements.
     */
    public ReadArray(ReadArray other) {
        super(other.ast);
        subset = other.subset;
        lhs = adoptChild(other.lhs);
        selectors = new Selector.SelectorNode[selectors.length];
        for (int i = 0; i < selectors.length; ++i)
            selectors[i] = adoptChild(other.selectors[i]);
        this.drop = adoptChild(other.drop);
        this.exact = adoptChild(other.exact);
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
        Selector[] selectorsVal = new Selector[selectors.length];
        for (int i = 0; i < selectorsVal.length; ++i) {
            selectorsVal[i] = selectors[i].executeSelector(frame);
        }
        boolean dropVal = drop.executeLogical(frame) != 0;  // FIXME: what is the correct execution
// order of these args?
        int exactVal = exact.executeLogical(frame);

        if (!(lhsVal instanceof RArray)) {
            Utils.nyi("unsupported base");
            // TODO: ERROR object of type 'XXX' is not subsettable
            return null;
        }
        RArray array = (RArray) lhsVal;
        if (array.dimensions().length != selectors.length)
            throw RError.getIncorrectDimensions(getAST());
        while (true) {
            try {
                return execute(array, selectorsVal, dropVal, exactVal);
            } catch (UnexpectedResultException e) {
                Selector failedSelector = (Selector) e.getResult();
                for (int i = 0; i < selectorsVal.length; ++i) {
                    if (selectorsVal[i] == failedSelector) {
                        // TODO This is probably not a proper way how to replace a child in an array
// of children??
                        RAny index = failedSelector.getIndex();
                        Selector.SelectorNode newSelector = Selector.createSelectorNode(ast, subset, index, selectors[i], false, failedSelector.getTransition());
                        replaceChild(selectors[i], newSelector);
                        assert (selectors[i] == newSelector);
                        selectorsVal[i] = newSelector.executeSelector(index);
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
    public abstract Object execute(RArray source, Selector[] selectors, boolean drop, int exact) throws UnexpectedResultException;

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

        public GeneralizedRead(ASTNode ast, boolean subset, RNode lhs, Selector.SelectorNode[] selectors, OptionNode dropExpr, OptionNode exactExpr) {
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
        public Object execute(RArray source, Selector[] selectors, boolean drop, int exact) throws UnexpectedResultException {
            int[] selSizes = Selector.initializeSelectors(source, selectors, ast);
            int[] destDim = Selector.calculateDestinationDimensions(selSizes, drop);
            int destSize = Selector.calculateSizeFromDimensions(destDim);
            if (!subset && (destSize > 1))
                throw RError.getSelectMoreThanOne(getAST());
            RArray dest = Utils.createArray(source, destSize, destDim, null);
            // fill in the index vector
            int[] idx = new int[selectors.length];
            int[] selIdx = new int[selectors.length];
            for (int i = 0; i < idx.length; ++i) {
                idx[i] = selectors[i].nextIndex(ast);
                selIdx[i] = 1; // start at one so that overflow and carry works
            }
            // loop over the dest offset and update the index vector
            for (int offset = 0; offset < destSize; ++offset) {
                int sourceOffset = Selector.calculateSourceOffset(source, idx);
                if (sourceOffset == RInt.NA)
                    Utils.setNA(dest, offset);
                else
                    dest.set(offset, source.getRef(sourceOffset));
                Selector.increment(idx, selIdx, selSizes, selectors, ast);
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

        public MatrixRead(ASTNode ast, boolean subset, RNode lhs, Selector.SelectorNode[] selectors, OptionNode dropExpr, OptionNode exactExpr) {
            super(ast, subset, lhs, selectors, dropExpr, exactExpr);
        }

        public MatrixRead(ReadArray other) {
            super(other);
        }

        // TODO change order of the matrix loops so that offset does not have to be always
// calculated like it is done in
        // the generalized case.
        @Override
        public Object execute(RArray source, Selector[] selectors, boolean drop, int exact) throws UnexpectedResultException {
            Selector selI = selectors[0];
            Selector selJ = selectors[1];
            int[] ndim = source.dimensions();
            int m = ndim[0];
            int n = ndim[1];
            selI.start(m, ast);
            selJ.start(n, ast);
            int nm = selI.size();
            int nn = selJ.size();
            int nsize = nm * nn;
            if (!subset && (nsize > 1))
                throw RError.getSelectMoreThanOne(getAST());
            if ((nm != 1 && nn != 1) || !drop) {
                ndim = new int[]{nm, nn};
            } else {
                ndim = null;
            }
            RArray res = Utils.createArray(source, nsize, ndim, null);

            for (int ni = 0; ni < nm; ni++) {
                int i = selI.nextIndex(ast);
                if (i != RInt.NA) {
                    selJ.restart();
                    for (int nj = 0; nj < nn; nj++) {
                        int offset = nj * nm + ni;
                        int j = selJ.nextIndex(ast);
                        if (j != RInt.NA) {
                            Object value;
                            value = source.getRef(j * m + i); // FIXME: check overflow? (the same is
// at many locations, whenever indexing a matrix)
                            res.set(offset, value);
                        } else {
                            Utils.setNA(res, offset);
                        }
                    }
                } else {
                    for (int nj = 0; nj < nn; nj++) {
                        Utils.setNA(res, nj * nm + ni);
                    }
                }
            }
            return res;
        }
    }

}
