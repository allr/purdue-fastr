package r.nodes.truffle;

import java.util.*;
import r.Truffle.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

// FIXME: do more performance optimizations
// FIXME: probably could have distinct types for non-failing and failing selectors
public class ReadMatrix extends BaseR {

    @Child RNode matrixExpr;
    @Child SelectorNode selIExpr;
    @Child SelectorNode selJExpr;
    @Child OptionNode dropExpr;
    @Child OptionNode exactExpr;
    final boolean subset;

    @Override public void replace0(RNode o, RNode n) {
        if (matrixExpr == o) matrixExpr = n;
        if (selIExpr == o) selIExpr = (SelectorNode) n;
        if (selJExpr == o) selJExpr = (SelectorNode) n;
        if (dropExpr == o) dropExpr = (OptionNode) n;
        if (exactExpr == o) exactExpr = (OptionNode) n;
    }

    private static final boolean DEBUG_M = false;

    public ReadMatrix(ASTNode ast, boolean subset, RNode matrixExpr, SelectorNode selIExpr, SelectorNode selJExpr, OptionNode dropExpr, OptionNode exactExpr) {
        super(ast);
        this.subset = subset;
        this.matrixExpr = adoptChild(matrixExpr);
        this.selIExpr = (SelectorNode) adoptChild(selIExpr);
        this.selJExpr = (SelectorNode) adoptChild(selJExpr);
        this.dropExpr = (OptionNode) adoptChild(dropExpr);
        this.exactExpr = (OptionNode) adoptChild(exactExpr);
    }

    @Override public Object execute(Frame frame) {
        RAny matrix = (RAny) matrixExpr.execute(frame);
        Selector selI = selIExpr.executeSelector(frame);
        Selector selJ = selJExpr.executeSelector(frame);
        int drop = dropExpr.executeLogical(frame); // FIXME: what is the correct execution order of these args?
        int exact = exactExpr.executeLogical(frame);

        return execute(matrix, selI, selJ, drop, exact);
    }

    // FIXME: could be specialized by matrix type to remove some boxing, virtual calls
    public Object execute(RAny matrix, Selector selI, Selector selJ, int drop, int exact) {
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

        Selector selectorI = selI;
        Selector selectorJ = selJ;

        for (;;) {
            try {
                return execute(base, m, n, selectorI, selectorJ, drop, exact);
            } catch (UnexpectedResultException e) {
                Selector failedSelector = (Selector) e.getResult();
                if (failedSelector == selectorI) {
                    if (DEBUG_M) Utils.debug("Selector I failed in ReadMatrix.execute, replacing.");
                    RAny index = selectorI.getIndex();
                    replaceChild(selIExpr, createSelectorNode(ast, subset, index, selIExpr.child, false, selectorI.getTransition()));
                    selectorI = selIExpr.executeSelector(index);
                } else {
                    // failedSelector == selectorJ
                    if (DEBUG_M) Utils.debug("Selector J failed in ReadMatrix.execute, replacing.");
                    RAny index = selectorJ.getIndex();
                    replaceChild(selJExpr, createSelectorNode(ast, subset, index, selJExpr.child, false, selectorJ.getTransition()));
                    selectorJ = selJExpr.executeSelector(index);
                }
                continue;
            }
        }
    }

    public Object execute(RArray base, int m, int n, Selector selI, Selector selJ, int drop, int exact) throws UnexpectedResultException {
        selI.start(m, ast);
        selJ.start(n, ast);
        int nm = selI.size();
        int nn = selJ.size();
        int nsize = nm * nn;
        int[] ndim;
        if ((nm != 1 && nn != 1) || drop == RLogical.FALSE) {
            ndim = new int[]{nm, nn};
        } else {
            ndim = null;
        }
        RArray res = Utils.createArray(base, nsize, ndim, null, null); // drop attributes

        for (int ni = 0; ni < nm; ni++) {
            int i = selI.nextIndex(ast);
            if (i != RInt.NA) {
                selJ.restart();
                for (int nj = 0; nj < nn; nj++) {
                    int offset = nj * nm + ni;
                    int j = selJ.nextIndex(ast);
                    if (j != RInt.NA) {
                        Object value;
                        value = base.getRef(j * m + i); // FIXME: check overflow? (the same is at many locations, whenever indexing a matrix)
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

    public abstract static class Selector {
        public void setIndex(RAny index) {}

        public RAny getIndex() {
            return null;
        }

        public Transition getTransition() {
            return null;
        }

        public abstract void start(int dataSize, ASTNode ast) throws UnexpectedResultException;

        public abstract void restart();

        public abstract int size();

        public abstract int nextIndex(ASTNode ast) throws UnexpectedResultException;

        public abstract boolean isConstant();
    }

    // non-failing
    public static final class MissingSelector extends Selector {
        private int size = -1;
        private int last = -1;

        @Override public void start(int dataSize, ASTNode ast) {
            size = dataSize;
            last = 0;
        }

        @Override public void restart() {
            last = 0;
        }

        @Override public int size() {
            return size;
        }

        @Override public int nextIndex(ASTNode ast) { // zero-based
            return last++;
        }

        @Override public boolean isConstant() {
            return true;
        }
    }

    // non-failing
    public static final class SinglePositiveConstantIndexSelector extends Selector {
        private final int index;

        public SinglePositiveConstantIndexSelector(int value) {
            this.index = value;
        }

        @Override public void start(int dataSize, ASTNode ast) {
            if (index > dataSize) { throw RError.getSubscriptBounds(ast); }
        }

        @Override public void restart() {}

        @Override public int size() {
            return 1;
        }

        @Override public int nextIndex(ASTNode ast) {
            return index;
        }

        @Override public boolean isConstant() {
            return true;
        }
    }

    // only for positive indexes, fails otherwise
    public static final class SimpleNumericSubsetSelector extends Selector {
        RInt index;
        int dataSize;
        int offset;
        Transition transition;

        @Override public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override public RInt getIndex() {
            return index;
        }

        @Override public Transition getTransition() {
            return transition;
        }

        @Override public void start(int dataSize, ASTNode ast) {
            this.dataSize = dataSize;
            offset = 0;
            transition = null;
        }

        @Override public void restart() {
            offset = 0;
        }

        @Override public int size() {
            return index.size();
        }

        @Override public int nextIndex(ASTNode ast) throws UnexpectedResultException {
            int value = index.getInt(offset++);
            if (value > 0) {
                value--;
                if (value < dataSize) {
                    return value;
                } else {
                    throw RError.getSubscriptBounds(ast);
                }
            }
            if (value == RInt.NA) { // could also remove this
                return RInt.NA;
            }
            transition = Transition.GENERIC_SELECTION;
            throw new UnexpectedResultException(this);
        }

        @Override public boolean isConstant() {
            return false;
        }
    }

    // only for scalar positive non-NA indexes, fails otherwise
    // does not handle error cases, so it is usable both for subset and subscript
    public static final class SimpleScalarNumericSelector extends Selector {
        RInt index;
        int dataSize;
        int indexValue;
        Transition transition;

        @Override public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override public RInt getIndex() {
            return index;
        }

        @Override public Transition getTransition() {
            return transition;
        }

        @Override public void start(int dataSize, ASTNode ast) throws UnexpectedResultException {
            if (index.size() == 1) {
                int i = index.getInt(0);
                if (i > 0) {
                    i--;
                    if (i < dataSize) {
                        this.dataSize = dataSize;
                        indexValue = i;
                        return;
                    } // else bounds error - handle in the generic case
                }
            }
            transition = Transition.GENERIC_SELECTION;
            throw new UnexpectedResultException(this);
        }

        @Override public void restart() {}

        @Override public int size() {
            return 1;
        }

        @Override public int nextIndex(ASTNode ast) throws UnexpectedResultException {
            return indexValue;
        }

        @Override public boolean isConstant() {
            return false;
        }
    }

    // non-failing
    public static final class GenericSubscriptSelector extends Selector {
        RInt index;
        int dataSize;
        int indexValue;

        @Override public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override public RInt getIndex() {
            return index;
        }

        @Override public void start(int dataSize, ASTNode ast) throws UnexpectedResultException {
            int isize = index.size();
            if (isize == 1) {
                int i = index.getInt(0);
                if (i > 0) {
                    i--;
                    if (i < dataSize) {
                        this.dataSize = dataSize;
                        indexValue = i;
                        return;
                    } else {
                        throw RError.getSubscriptBounds(ast);
                    }
                } else {
                    throw RError.getSelectLessThanOne(ast);
                }
            } else {
                if (isize > 1) {
                    throw RError.getSelectMoreThanOne(ast);
                } else {
                    throw RError.getSelectLessThanOne(ast);
                }
            }
        }

        @Override public void restart() {}

        @Override public int size() {
            return 1;
        }

        @Override public int nextIndex(ASTNode ast) throws UnexpectedResultException {
            return indexValue;
        }

        @Override public boolean isConstant() {
            return false;
        }
    }

    // non-failing
    public static final class GenericNumericSubsetSelector extends Selector {

        RInt index;
        int size; // the result size, valid after a call to restart ; before restart, either the size already or number of zeros (negativeSelection)
        boolean positiveSelection; // positive indexes and NA and zeros

        int offset;
        boolean[] omit;

        @Override public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override public void start(int dataSize, ASTNode ast) {

            boolean hasNA = false;
            boolean hasNegative = false;
            boolean hasPositive = false;

            int nzero = 0;
            int isize = index.size();
            for (int i = 0; i < isize; i++) {
                int value = index.getInt(i);
                if (value > 0) {
                    hasPositive = true;
                    if (value - 1 > dataSize) { throw RError.getSubscriptBounds(ast); }
                    continue;
                }
                if (value == 0) {
                    nzero++;
                    continue;
                }
                if (value == RInt.NA) {
                    hasNA = true;
                    continue;
                }
                // value < 0
                if (!hasNegative) {
                    hasNegative = true;
                    size = dataSize;
                    if (omit != null) {
                        if (omit.length < dataSize) {
                            omit = new boolean[dataSize];
                        } else {
                            Arrays.fill(omit, false);
                        }
                    } else {
                        omit = new boolean[dataSize];
                    }
                }
                int e = -value - 1;
                if (e < dataSize && !omit[e]) {
                    omit[e] = true;
                    size--;
                }
            }
            if (hasPositive) {
                if (!hasNegative) {
                    size = isize - nzero;
                    positiveSelection = true;
                } else {
                    throw RError.getOnlyZeroMixed(ast);
                }
            } else {
                // no positive values
                if (hasNegative) {
                    if (!hasNA) {
                        positiveSelection = false;
                        // all elements are negative, selection size will depend on the data size
                    } else {
                        throw RError.getOnlyZeroMixed(ast);
                    }
                } else {
                    if (hasNA) {
                        positiveSelection = true;
                        size = isize;
                    } else {
                        // empty
                        size = 0;
                    }
                }
            }
            offset = 0;
        }

        @Override public void restart() {
            offset = 0;
        }

        @Override public int size() {
            return size;
        }

        @Override public int nextIndex(ASTNode ast) {
            if (positiveSelection) {
                int i = index.getInt(offset++);
                while (i == 0) {
                    i = index.getInt(offset++);
                }
                if (i != RInt.NA) { // FIXME: double-checking for NA, the outer loop checks again
                    return i - 1;
                } else {
                    return RInt.NA;
                }
            } else {
                // negative selection
                while (omit[offset]) {
                    offset++;
                }
                return offset++;
            }
        }

        @Override public boolean isConstant() {
            return false;
        }
    }

    // non-failing
    public static final class GenericLogicalSubsetSelector extends Selector {

        RLogical index;
        int indexSize;
        int size;
        int offset;
        int indexOffset;

        boolean reuse;

        @Override public void setIndex(RAny index) {
            this.index = (RLogical) index;
            indexSize = this.index.size();
        }

        @Override public void start(int dataSize, ASTNode ast) {
            offset = 0;
            indexOffset = 0;

            int isize = indexSize;
            int nnonfalse = RLogical.RLogicalUtils.nonFalsesInRange(index, 0, isize);

            if (isize == dataSize) {
                size = nnonfalse;
                reuse = false;
                return;
            }
            if (isize > dataSize) { throw RError.getLogicalSubscriptLong(ast); }
            reuse = true;
            int times = dataSize / isize;
            int extra = dataSize - times * isize;

            if (extra == 0) {
                size = nnonfalse * times;
            } else {
                size = nnonfalse * times + RLogical.RLogicalUtils.nonFalsesInRange(index, 0, extra);
            }
        }

        @Override public void restart() {
            offset = 0;
            indexOffset = 0;
        }

        @Override public int size() {
            return size;
        }

        @Override public int nextIndex(ASTNode ast) {
            for (;;) {
                int v = index.getLogical(indexOffset);
                if (!reuse) {
                    if (v == RLogical.TRUE) { return indexOffset++; }
                    indexOffset++;
                    if (v == RLogical.FALSE) {
                        continue;
                    } else {
                        return RLogical.NA;
                    }
                } else {
                    indexOffset++;
                    if (indexOffset == indexSize) {
                        indexOffset = 0;
                    }
                    if (v == RLogical.TRUE) { return offset++; }
                    offset++;
                    if (v == RLogical.FALSE) {
                        continue;
                    } else {
                        return RLogical.NA;
                    }
                }
            }
        }

        @Override public boolean isConstant() {
            return false;
        }

    }

    public static SelectorNode createConstantSelectorNode(ASTNode ast, RNode child, final Selector selector) {
        return new SelectorNode(ast, child) {

            @Override public Selector executeSelector(Frame frame) {
                return selector;
            }

            @Override public Selector executeSelector(RAny index) {
                return selector;
            }
        };
    }

    public static SelectorNode createSelectorNode(ASTNode ast, final boolean subset, final RNode node) {
        if (node == null) {
            if (subset) {
                return createConstantSelectorNode(ast, node, new MissingSelector());
            } else {
                throw RError.getInvalidSubscriptType(ast, "symbol");
            }
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny index = (RAny) node.execute(null);
            return createSelectorNode(ast, subset, index, node, true, null);
        }
        return new SelectorNode(ast, node) {
            @Override public Selector executeSelector(RAny index) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    SelectorNode sn = createSelectorNode(ast, subset, index, child, false, null);
                    replace(sn, "install Selector from Uninitialized");
                    return sn.executeSelector(index);
                }
            }

        };
    }

    public enum Transition { // FIXME: this is not really used now as it has one value only, but future optimizations will likely need it
        GENERIC_SELECTION;
    }

    public static final class AnalyzedIndex {
        final boolean hasNA;
        final boolean hasNegative;
        final boolean hasPositive;
        final boolean hasZero;

        public AnalyzedIndex(boolean hasNA, boolean hasNegative, boolean hasPositive, boolean hasZero) {
            this.hasNA = hasNA;
            this.hasNegative = hasNegative;
            this.hasPositive = hasPositive;
            this.hasZero = hasZero;
        }
    }

    public static AnalyzedIndex analyzeIndex(RInt index) {
        boolean hasNA = false;
        boolean hasNegative = false;
        boolean hasPositive = false;
        boolean hasZero = false;

        for (int i = 0; i < index.size(); i++) {
            int value = index.getInt(i);
            if (value > 0) {
                hasPositive = true;
                continue;
            }
            if (value == 0) {
                hasZero = true;
                continue;
            }
            if (value == RInt.NA) {
                hasNA = true;
                continue;
            }
            hasNegative = true;
            continue;
        }
        return new AnalyzedIndex(hasNA, hasNegative, hasPositive, hasZero);
    }

    public static SelectorNode createSelectorNode(ASTNode ast, boolean subset, RAny template, RNode child, boolean isConstant, Transition transition) {
        if (transition == null) {
            if (template instanceof RDouble || template instanceof RInt) {
                RInt index = template.asInt().materialize();
                AnalyzedIndex a = analyzeIndex(index);
                if (a.hasPositive && !a.hasNegative && !a.hasNA && !a.hasZero) {
                    if (isConstant) {
                        if (index.size() == 1) { return createConstantSelectorNode(ast, child, new SinglePositiveConstantIndexSelector(index.getInt(0) - 1)); }
                        // FIXME: handle more cases? e.g. set of positive integer indexes
                    }
                    if (subset) {
                        if (index.size() == 1) {
                            return createSimpleScalarNumericSubsetSelectorNode(ast, child);
                        } else {
                            return createSimpleNumericSubsetSelectorNode(ast, child);
                        }
                    } else {
                        return createSimpleSubscriptSelectorNode(ast, child);
                    }
                }
            }
        }
        if (subset) {
            return createGenericSubsetSelectorNode(ast, child);
        } else {
            return createGenericSubscriptSelectorNode(ast, child);
        }
    }

    public static SelectorNode createSimpleNumericSubsetSelectorNode(ASTNode ast, RNode child) {
        final SimpleNumericSubsetSelector selector = new SimpleNumericSubsetSelector();
        return new SelectorNode(ast, child) {

            @Override public Selector executeSelector(RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble) { // FIXME: can get rid of this through type-specialization
                        selector.setIndex(index.asInt());
                        return selector;
                    }
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_M) Utils.debug("SimpleNumericSubsetSelector failed in Selector.execute (unexpected type), replacing.");
                    SelectorNode gn = createGenericSubsetSelectorNode(ast, child);
                    replace(gn, "install GenericSubsetSelectorNode from SimpleNumericSubsetSelectorNode");
                    return gn.executeSelector(index);
                }
            }
        };
    }

    public static SelectorNode createSimpleSubscriptSelectorNode(ASTNode ast, RNode child) {
        final SimpleScalarNumericSelector selector = new SimpleScalarNumericSelector();
        return new SelectorNode(ast, child) {

            @Override public Selector executeSelector(RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble || index instanceof RLogical) { // FIXME: can get rid of this through type-specialization
                        selector.setIndex(index.asInt());
                        return selector;
                    }
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_M) Utils.debug("SimpleSubscriptSelector failed in Selector.execute (unexpected type), replacing.");
                    SelectorNode gn = createGenericSubscriptSelectorNode(ast, child);
                    replace(gn, "install GenericSubscriptSelectorNode from SimpleSubscriptSelectorNode");
                    return gn.executeSelector(index);
                }
            }
        };
    }

    public static SelectorNode createSimpleScalarNumericSubsetSelectorNode(ASTNode ast, RNode child) {
        final SimpleScalarNumericSelector selector = new SimpleScalarNumericSelector();
        return new SelectorNode(ast, child) {

            @Override public Selector executeSelector(RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble) { // FIXME: can get rid of this through type-specialization
                        selector.setIndex(index.asInt());
                        return selector;
                    }
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_M) Utils.debug("SimpleScalarNumericSelector failed in Selector.execute (unexpected type), replacing.");
                    SelectorNode gn = createSimpleNumericSubsetSelectorNode(ast, child);
                    replace(gn, "install SimpleNumericSubsetSelectorNode from SimpleScalarNumericSubsetSelectorNode");
                    return gn.executeSelector(index);
                }
            }
        };
    }

    public static SelectorNode createGenericSubsetSelectorNode(ASTNode ast, RNode child) {
        return new SelectorNode(ast, child) {

            final GenericNumericSubsetSelector numericSelector = new GenericNumericSubsetSelector();
            final GenericLogicalSubsetSelector logicalSelector = new GenericLogicalSubsetSelector();

            @Override public Selector executeSelector(RAny index) {
                if (index instanceof RInt || index instanceof RDouble) {
                    numericSelector.setIndex(index.asInt());
                    return numericSelector;
                }
                if (index instanceof RLogical) {
                    logicalSelector.setIndex(index);
                    return logicalSelector;
                }
                Utils.nyi("unsupported index type");
                return null;
            }
        };
    }

    public static SelectorNode createGenericSubscriptSelectorNode(ASTNode ast, RNode child) {
        final GenericSubscriptSelector selector = new GenericSubscriptSelector();

        return new SelectorNode(ast, child) {

            @Override public Selector executeSelector(RAny index) {
                if (index instanceof RInt || index instanceof RDouble || index instanceof RLogical) {
                    selector.setIndex(index.asInt());
                    return selector;
                }
                Utils.nyi("unsupported index type");
                return null;
            }
        };
    }

    public abstract static class SelectorNode extends BaseR {
        @Child RNode child;

        public SelectorNode(ASTNode ast, RNode child) {
            super(ast);
            this.child = adoptChild(child);
        }

        @Override public Object execute(Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public Selector executeSelector(Frame frame) {
            RAny index = (RAny) child.execute(frame);
            return executeSelector(index);

        }

        @Override public void replace0(RNode o, RNode n) {
            if (child == o) child = n;
        }

        public abstract Selector executeSelector(RAny index);
    }

    public static OptionNode createConstantOptionNode(final ASTNode ast, final int value) {
        return new OptionNode(ast) {

            @Override public int executeLogical(Frame frame) {
                return value;
            }
        };
    }

    public static OptionNode createOptionNode(final ASTNode ast, final RNode node, int defaultValue) {
        if (node == null) { return createConstantOptionNode(ast, defaultValue); }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny value = (RAny) node.execute(null);
            return createConstantOptionNode(ast, value.asLogical().getLogical(0));
        }
        return new OptionNode(ast) {

            @Child RNode child = adoptChild(node);

            @Override public int executeLogical(Frame frame) {
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

        @Override public Object execute(Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public abstract int executeLogical(Frame frame);

        @Override public void replace0(RNode o, RNode n) {}
    }
}
