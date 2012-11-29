package r.nodes.truffle;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

// FIXME: do more performance optimizations
// FIXME: probably could have distinct types for non-failing and failing selectors
public class ReadMatrix extends BaseR {

    @Stable RNode matrixExpr;
    @Stable SelectorNode selIExpr;
    @Stable SelectorNode selJExpr;
    @Stable OptionNode dropExpr;
    @Stable OptionNode exactExpr;
    final boolean subset;

    private static final boolean DEBUG_M = false;

    public ReadMatrix(ASTNode ast, boolean subset, RNode matrixExpr, SelectorNode selIExpr, SelectorNode selJExpr, OptionNode dropExpr, OptionNode exactExpr) {
        super(ast);
        this.subset = subset;
        this.matrixExpr = updateParent(matrixExpr);
        this.selIExpr = updateParent(selIExpr);
        this.selJExpr = updateParent(selJExpr);
        this.dropExpr = updateParent(dropExpr);
        this.exactExpr = updateParent(exactExpr);
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RAny matrix = (RAny) matrixExpr.execute(context, frame);
        Selector selI = selIExpr.executeSelector(context, frame);
        Selector selJ = selJExpr.executeSelector(context, frame);
        int drop = dropExpr.executeLogical(context, frame);  // FIXME: what is the correct execution order of these args?
        int exact = exactExpr.executeLogical(context, frame);

        return execute(context, matrix, selI, selJ, drop, exact);
    }

    // FIXME: could be specialized by matrix type to remove some boxing, virtual calls
    public Object execute(RContext context, RAny matrix, Selector selI, Selector selJ, int drop, int exact) {
        if (!(matrix instanceof RArray)) {
            Utils.nyi("unsupported base");
            // TODO: ERROR object of type 'XXX' is not subsettable
            return null;
        }
        RArray base = (RArray) matrix;
        int[] dim = base.dimensions();
        if (dim == null || dim.length != 2) {
            throw RError.getIncorrectDimensions(ast);
        }
        int m = dim[0];
        int n = dim[1];

        Selector selectorI = selI;
        Selector selectorJ = selJ;

        for (;;) {
            try {
                return execute(context, base, m, n, selectorI, selectorJ, drop, exact);
            } catch (UnexpectedResultException e) {
                Selector failedSelector = (Selector) e.getResult();
                if (failedSelector == selectorI) {
                    if (DEBUG_M) Utils.debug("Selector I failed in ReadMatrix.execute, replacing.");
                    RAny index = selectorI.getIndex();
                    replaceChild(selIExpr, createSelectorNode(ast, index, selIExpr.child, false, selectorI.getTransition()));
                    selectorI = selIExpr.executeSelector(context, index);
                } else {
                    // failedSelector == selectorJ
                    if (DEBUG_M) Utils.debug("Selector J failed in ReadMatrix.execute, replacing.");
                    RAny index = selectorJ.getIndex();
                    replaceChild(selJExpr, createSelectorNode(ast, index, selJExpr.child, false, selectorJ.getTransition()));
                    selectorJ = selJExpr.executeSelector(context, index);
                }
                continue;
            }
        }
    }

    public Object execute(RContext context, RArray base, int m, int n, Selector selI, Selector selJ, int drop, int exact) throws UnexpectedResultException {
        selI.start(m, context, ast);
        selJ.start(n, context, ast);
        int nm = selI.size();
        int nn = selJ.size();
        int nsize = nm * nn;
        int[] ndim;
        if ((nm != 1 && nn != 1) || drop == RLogical.FALSE) {
            ndim = new int[]{nm, nn};
        } else {
            ndim = null;
        }
        RArray res = Utils.createArray(base, nsize, ndim);

        for (int ni = 0; ni < nm; ni++) {
            int i = selI.nextIndex(context, ast);
            if (i != RInt.NA) {
                selJ.restart();
                for (int nj = 0; nj < nn; nj++) {
                    int offset = nj * nm + ni;
                    int j = selJ.nextIndex(context, ast);
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
        public void setIndex(RAny index) {
        }
        public RAny getIndex() {
            return null;
        }
        public Transition getTransition() {
            return null;
        }
        public abstract void start(int dataSize, RContext context, ASTNode ast);
        public abstract void restart();
        public abstract int size();
        public abstract int nextIndex(RContext context, ASTNode ast) throws UnexpectedResultException;
        public abstract boolean isConstant();
    }

    // non-failing
    public static final class MissingSelector extends Selector {
        private int size = -1;
        private int last = -1;

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            size = dataSize;
            last = 0;
        }

        @Override
        public void restart() {
            last = 0;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            return last++;
        }

        @Override
        public boolean isConstant() {
            return true;
        }
    }

    // non-failing
    public static final class SinglePositiveConstantIndexSelector extends Selector {
        private final int index;

        public SinglePositiveConstantIndexSelector(int value) {
            this.index = value;
        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            if (index > dataSize) {
                throw RError.getSubscriptBounds(ast);
            }
        }

        @Override
        public void restart() { }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            return index;
        }

        @Override
        public boolean isConstant() {
            return true;
        }
    }

    // only for positive indexes, fails otherwise
    public static final class SimpleNumericSelector extends Selector {
        RInt index;
        int dataSize;
        int offset;
        Transition transition;

        @Override
        public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override
        public RInt getIndex() {
            return index;
        }

        @Override
        public Transition getTransition() {
            return transition;
        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            this.dataSize = dataSize;
            offset = 0;
            transition = null;
        }

        @Override
        public void restart() {
            offset = 0;
        }

        @Override
        public int size() {
            return index.size();
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) throws UnexpectedResultException {
            int value = index.getInt(offset++);
            if (value > 0) {
                return value - 1;
            }
            if (value == RInt.NA) { // could also remove this
                return RInt.NA;
            }
            transition = Transition.GENERIC_SELECTION;
            throw new UnexpectedResultException(this);
        }

        @Override
        public boolean isConstant() {
            return false;
        }
    }

    // non-failing
    public static final class GenericNumericSelector extends Selector {

        RInt index;
        int size; // the result size, valid after a call to restart ; before restart, either the size already or number of zeros (negativeSelection)
        boolean positiveSelection;  // positive indexes and NA and zeros

        int offset;
        boolean[] omit;

        @Override
        public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {

            boolean hasNA = false;
            boolean hasNegative = false;
            boolean hasPositive = false;

            int nzero = 0;
            int isize = index.size();
            for (int i = 0; i < isize; i++) {
                int value = index.getInt(i);
                if (value > 0) {
                    hasPositive = true;
                    if (value - 1 > dataSize) {
                        throw RError.getSubscriptBounds(ast);
                    }
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

        @Override
        public void restart() {
            offset = 0;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
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

        @Override
        public boolean isConstant() {
            return false;
        }
    }

    public static final class GenericLogicalSelector extends Selector {

        RLogical index;
        int indexSize;
        int size;
        int offset;
        int indexOffset;

        boolean reuse;

        @Override
        public void setIndex(RAny index) {
            this.index = (RLogical) index;
            indexSize = this.index.size();
        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            offset = 0;
            indexOffset = 0;

            int isize = indexSize;
            int nnonfalse = RLogical.RLogicalUtils.nonFalsesInRange(index, 0, isize);

            if (isize == dataSize) {
                size = nnonfalse;
                reuse = false;
                return;
            }
            if (isize > dataSize) {
                throw RError.getLogicalSubscriptLong(ast);
            }
            reuse = true;
            int times = dataSize / isize;
            int extra = dataSize - times * isize;

            if (extra == 0) {
                size = nnonfalse * times;
            } else {
                size = nnonfalse * times + RLogical.RLogicalUtils.nonFalsesInRange(index, 0, extra);
            }
        }

        @Override
        public void restart() {
            offset = 0;
            indexOffset = 0;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            for (;;) {
                int v = index.getLogical(indexOffset);
                if (!reuse) {
                    if (v == RLogical.TRUE) {
                        return indexOffset++;
                    }
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
                    if (v == RLogical.TRUE) {
                        return offset++;
                    }
                    offset++;
                    if (v == RLogical.FALSE) {
                        continue;
                    } else {
                        return RLogical.NA;
                    }
                }
            }
        }

        @Override
        public boolean isConstant() {
            return false;
        }

    }

    public static SelectorNode createConstantSelectorNode(ASTNode ast, RNode child, final Selector selector) {
        return new SelectorNode(ast, child) {

            @Override
            public Selector executeSelector(RContext context, Frame frame) {
                return selector;
            }

            @Override
            public Selector executeSelector(RContext context, RAny index) {
                return selector;
            }
        };
    }

    public static SelectorNode createSelectorNode(ASTNode ast, final RNode node) {
        if (node == null) {
            return createConstantSelectorNode(ast, node, new MissingSelector());
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny index = (RAny) node.execute(null, null);
            return createSelectorNode(ast, index, node, true, null);
        }
        return new SelectorNode(ast, node) {
            @Override
            public Selector executeSelector(RContext context, RAny index) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    SelectorNode sn = createSelectorNode(ast, index, child, false, null);
                    replace(sn, "install SelectorNode from Uninitialized");
                    return sn.executeSelector(context, index);
                }
            }

        };
    }

    public enum Transition {
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

    public static SelectorNode createSelectorNode(ASTNode ast, RAny template, RNode child, boolean isConstant, Transition transition) {
        if (transition == null) {
            if (template instanceof RDouble || template instanceof RInt) {
                RInt index = template.asInt().materialize();
                AnalyzedIndex a = analyzeIndex(index);
                if (a.hasPositive && !a.hasNegative && !a.hasNA && !a.hasZero) {
                    if (isConstant) {
                        if (index.size() == 1) {
                            return createConstantSelectorNode(ast, child, new SinglePositiveConstantIndexSelector(index.getInt(0) - 1));
                        }
                     // FIXME: handle more cases? e.g. set of positive integer indexes
                    } else {
                        return createSimpleNumericSelectorNode(ast, child);

                    }
                }
            }
        }
        return createGenericSelectorNode(ast, child);
    }

    public static SelectorNode createSimpleNumericSelectorNode(ASTNode ast, RNode child) {
        final Selector selector = new SimpleNumericSelector();
        return new SelectorNode(ast, child) {

            @Override
            public Selector executeSelector(RContext context, RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble) {
                        selector.setIndex(index.asInt());
                        return selector;
                    }
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_M) Utils.debug("SimpleNumericSelector failed in SelectorNode.execute (unexpected type), replacing.");
                    SelectorNode gn = createGenericSelectorNode(ast, child);
                    replace(gn, "install GenericSelectorNode from SimpleNumericSelectorNode");
                    return gn.executeSelector(context, index);
                }
            }
        };
    }

    public static SelectorNode createGenericSelectorNode(ASTNode ast, RNode child) {
        return new SelectorNode(ast, child) {

            final GenericNumericSelector numericSelector = new GenericNumericSelector();
            final GenericLogicalSelector logicalSelector = new GenericLogicalSelector();

            @Override
            public Selector executeSelector(RContext context, RAny index) {
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

    public abstract static class SelectorNode extends BaseR {
        @Stable RNode child;

        public SelectorNode(ASTNode ast, RNode child) {
            super(ast);
            this.child = updateParent(child);
        }

        @Override
        public Object execute(RContext context, Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public Selector executeSelector(RContext context, Frame frame) {
            RAny index = (RAny) child.execute(context, frame);
            return executeSelector(context, index);

        }

        public abstract Selector executeSelector(RContext context, RAny index);
    }

    public static OptionNode createConstantOptionNode(final ASTNode ast, final int value) {
        return new OptionNode(ast) {

            @Override
            public int executeLogical(RContext context, Frame frame) {
                return value;
            }
        };
    }

    public static OptionNode createOptionNode(final ASTNode ast, final RNode node, int defaultValue) {
        if (node == null) {
            return createConstantOptionNode(ast, defaultValue);
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny value = (RAny) node.execute(null, null);
            return createConstantOptionNode(ast, value.asLogical().getLogical(0));
        }
        return new OptionNode(ast) {

            @Stable RNode child = updateParent(node);

            @Override
            public int executeLogical(RContext context, Frame frame) {
                RAny value = (RAny) child.execute(context, frame);
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
        public Object execute(RContext context, Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public abstract int executeLogical(RContext context, Frame frame);
    }
}
