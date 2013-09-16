package r.nodes.truffle;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.ASTNode;
import r.runtime.*;

import java.util.Arrays;

// TODO: real support for "exact"
public abstract class Selector {


// only used in naive implementation (perhaps remove)
//
//    /** Initializes the selectors to their respective values using given source array.
//     *
//     * Returns the selector sizes array.
//     */
//    public static void initializeSelectors(RArray source, Selector[] selectors, ASTNode ast, int[] selSizes) throws UnexpectedResultException {
//        int[] sourceDim = source.dimensions();
//        for (int i = 0; i < selectors.length; ++i) {
//            selectors[i].start(sourceDim[i], ast);
//            selSizes[i] = selectors[i].size();
//        }
//    }
//

    /** Calculates the result dimensions given the selector sizes. If the drop argument is true any dimension of size
     * 1 is dropped and if the final result has only one dimension even that one is dropped (a vector will be returned
     * in this case).
     */
    public static int[] calculateDestinationDimensions(int[] selSizes, boolean drop) {

        if (!drop) {
            return selSizes;
        }
        int nones = 0;
        for (int s : selSizes) {
            if (s == 1) {
                nones++;
            }
        }
        if (nones == 0) {
            return selSizes;
        }
        if (nones >= selSizes.length - 1) {
            return null;
        }
        int[] res = new int[selSizes.length - nones];
        int i = 0;
        for (int s : selSizes) {
            if (s != 1) {
                res[i++] = s;
            }
        }
        return res;
    }

    /** Calculates the selection size from selector sizes (their product).
     */
    public static int calculateSizeFromSelectorSizes(int[] selectorSizes) {

        int result = 1;
        for (int i : selectorSizes) {
            result *= i;
        }
        return result;
    }

// a naive implementation (perhaps remove)
//
//    /** Given the index vector, selector indices, sizes and the selectors themselves, increments the index vector by one
//     * returning true on overflow.
//     *
//     * Increments in different order starting from left to right so that the destination offset does not have to be
//     * recalculated each time.
//     *
//     * @param idx The index vector. Contains as many elements as the selectors and each element is the 0based index to
//     *            the source array as specified by the current selector.
//     * @param selSizes Size of the selector.
//     * @param selectors Selectors to be used in the increment.
//     */
//    public static boolean increment(int[] idx, int[] selSizes, Selector[] selectors, ASTNode ast) throws UnexpectedResultException {
//        for (int i = 0; i < idx.length; ++i) {
//            if (selectors[i].isExhausted()) {
//                selectors[i].restart();
//                idx[i] = selectors[i].nextIndex(ast);
//            } else {
//                idx[i] = selectors[i].nextIndex(ast);
//                return false; // no overflow
//            }
//        }
//        return true; // overflow
//    }
//
//    /** Calculates the source index from given index vector. If any of the index values is NA, then the result is NA
//     * itself.
//     */
//    public static int calculateSourceOffset(RArray source, int[] idx) {
//        int result = 0;
//        int m = 1;
//        int[] dims = source.dimensions();
//        for (int i = 0; i < idx.length; ++i) {
//            if (idx[i] == RInt.NA) {
//                return RInt.NA;
//            }
//            result += idx[i] * m;
//            m *= dims[i];
//        }
//        return result;
//    }

    public static void partialToFullOffsets(int[] offsets, int from) {
        int add = 0;
        for (int i = from; i >= 0; i--) {
            int o = offsets[i];
            if (o != RInt.NA) {
                add += o;
                offsets[i] = add;
            } else {
                for (int k = 0; k < i; k++) {
                    offsets[k] = RInt.NA;
                }
                return;
            }
        }
    }

    public static void partialToFullOffsetsNoNA(int[] offsets, int from) {
        int add = 0;
        for (int i = from; i >= 0; i--) {
            add += offsets[i];
            offsets[i] = add;
        }
    }

    // offset and selSizes are output arrays and are fully overwritten
    // initializes the offset array for indexing, initializes
    public static boolean initialize(int[] offsets, Selector[] selectors, int[] dataDimensions, int[] selSizes, ASTNode ast) throws UnexpectedResultException {
        int mult = 1;
        boolean hasNA = false;
        boolean mayHaveNA = false;
        for (int i = 0; i < selectors.length; ++i) {
            Selector sel = selectors[i];
            int dim = dataDimensions[i];
            sel.start(dim, ast);
            mayHaveNA = mayHaveNA || sel.mayHaveNA();
            int size = sel.size();
            selSizes[i] = size;
            if (size == 0) {
                return true;
            }
            int next = sel.nextIndex(ast);
            if (next != RInt.NA) {
                offsets[i] = next * mult;
            } else {
                hasNA = true;
                offsets[i] = RInt.NA;
            }
            mult *= dim;
        }
        if (!hasNA) {
            partialToFullOffsetsNoNA(offsets, selectors.length - 1);
        } else {
            partialToFullOffsets(offsets, selectors.length - 1);
        }
        return mayHaveNA;
    }

    public static void restart(int[] offsets, Selector[] selectors, int[] dataDimensions, ASTNode ast, boolean mayHaveNA) throws UnexpectedResultException {
        if (!mayHaveNA) {
            restartNoNA(offsets, selectors, dataDimensions, ast);
        } else {
            restart(offsets, selectors, dataDimensions, ast);
        }
    }

    public static void restart(int[] offsets, Selector[] selectors, int[] dataDimensions, ASTNode ast) throws UnexpectedResultException {
        int mult = 1;
        for (int i = 0; i < selectors.length; ++i) {
            Selector sel = selectors[i];
            int dim = dataDimensions[i];
            sel.restart();
            int next = sel.nextIndex(ast);
            if (next != RInt.NA) {
                offsets[i] = next * mult;
            } else {
                offsets[i] = RInt.NA;
            }
            mult *= dim;
        }
        partialToFullOffsets(offsets, selectors.length - 1);
    }

    public static void restartNoNA(int[] offsets, Selector[] selectors, int[] dataDimensions, ASTNode ast) throws UnexpectedResultException {
        int mult = 1;
        for (int i = 0; i < selectors.length; ++i) {
            Selector sel = selectors[i];
            int dim = dataDimensions[i];
            sel.restart();
            int next = sel.nextIndex(ast);
            offsets[i] = next * mult;
            mult *= dim;
        }
        partialToFullOffsetsNoNA(offsets, selectors.length - 1);
    }

    // advance must not be called when all selectors are exhausted (the search is done) - restart has to be used instead if wrap-around is needed
    public static void advance(int[] offsets, int[] dataDimensions, Selector[] selectors, ASTNode ast, boolean mayHaveNA) throws UnexpectedResultException {
        if (!mayHaveNA) {
            advanceNoNA(offsets, dataDimensions, selectors, ast);
        } else {
            advance(offsets, dataDimensions, selectors, ast);
        }
    }

    public static void advance(int[] offsets, int[] dataDimensions, Selector[] selectors, ASTNode ast) throws UnexpectedResultException {
        assert Utils.check(selectors.length > 0);
        assert Utils.check(offsets.length == selectors.length + 1);
        assert Utils.check(offsets[offsets.length - 1] == 0);

        int mult = 1;
        int i = 0;
        for (;;) {
            Selector sel = selectors[i];
            if (!sel.isExhausted()) {
                int next = sel.nextIndex(ast);
                if (next != RInt.NA) {
                    offsets[i] = next * mult;
                } else {
                    offsets[i] = RInt.NA;
                }
                break;
            }
            sel.restart();
            int next = sel.nextIndex(ast);
            if (next != RInt.NA) {
                offsets[i] = next * mult;
            } else {
                offsets[i] = RInt.NA;
            }
            mult *= dataDimensions[i];
            i++;
        }
        // now we know the selector is exhausted
        // the array contains partial offsets from [0] to [i]
        // and full offsets from [i+1] to its end

        partialToFullOffsets(offsets, i + 1);
    }

    public static void advanceNoNA(int[] offsets, int[] dataDimensions, Selector[] selectors, ASTNode ast) throws UnexpectedResultException {
        assert Utils.check(selectors.length > 0);
        assert Utils.check(offsets.length == selectors.length + 1);
        assert Utils.check(offsets[offsets.length - 1] == 0);

        int mult = 1;
        int i = 0;
        for (;;) {
            Selector sel = selectors[i];
            if (!sel.isExhausted()) {
                int next = sel.nextIndex(ast);
                offsets[i] = next * mult;
                break;
            }
            sel.restart();
            int next = sel.nextIndex(ast);
            offsets[i] = next * mult;
            mult *= dataDimensions[i];
            i++;
        }
        // now we know the selector is exhausted
        // the array contains partial offsets from [0] to [i]
        // and full offsets from [i+1] to its end

        partialToFullOffsetsNoNA(offsets, i + 1);
    }


    private static final boolean DEBUG_M = false;

    public void setIndex(RAny index) {
    }
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
    public abstract boolean isExhausted(); // the next call to nextIndex would go above selector size (always for selectors of size 1)
    public abstract boolean mayHaveNA();

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
            return Selector.createConstantOptionNode(ast, defaultValue);
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny value = (RAny) node.execute(null);
            return Selector.createConstantOptionNode(ast, value.asLogical().getLogical(0));
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
            assert Utils.check(false, "unreachable");
            return null;
        }

        public abstract int executeLogical(Frame frame);
    }

    // non-failing
    public static final class MissingSelector extends Selector {
        private int size = -1;
        private int last = -1;

        @Override
        public void start(int dataSize, ASTNode ast) {
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
        public int nextIndex(ASTNode ast) { // zero-based
            return last++;
        }

        @Override
        public boolean isExhausted() {
            return last == size;
        }

        @Override
        public boolean isConstant() {
            return true;
        }

        @Override
        public boolean mayHaveNA() {
            return false;
        }
    }

    // non-failing
    public static final class SinglePositiveConstantIndexSelector extends Selector {
        private final int index;

        public SinglePositiveConstantIndexSelector(int value) {
            this.index = value;
        }

        @Override
        public void start(int dataSize, ASTNode ast) {
            if (index >= dataSize) {
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
        public int nextIndex(ASTNode ast) {
            return index;
        }

        @Override
        public boolean isExhausted() {
            return true;
        }

        @Override
        public boolean isConstant() {
            return true;
        }

        @Override
        public boolean mayHaveNA() {
            return false;
        }
    }

    // only for positive indexes, fails otherwise
    public static final class SimpleNumericSubsetSelector extends Selector {
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
        public void start(int dataSize, ASTNode ast) {
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
        public int nextIndex(ASTNode ast) throws UnexpectedResultException {
            int value = index.getInt(offset++);
            if (value > 0) {
                value--;
                if (value < dataSize) {
                    return value;
                } else {
                    throw RError.getSubscriptBounds(ast);
                }
            }
            transition = Transition.GENERIC_SELECTION;
            throw new UnexpectedResultException(this);
        }

        @Override
        public boolean isExhausted() {
            return offset == index.size();
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public boolean mayHaveNA() {
            // on NA it would fail in nextIndex
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
        public void start(int dataSize, ASTNode ast) throws UnexpectedResultException {
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

        @Override
        public void restart() {
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int nextIndex(ASTNode ast) throws UnexpectedResultException {
            return indexValue;
        }

        @Override
        public boolean isExhausted() {
            return true;
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public boolean mayHaveNA() {
            return false;
        }
    }

    // non-failing
    public static final class GenericSubscriptSelector extends Selector {
        RInt index;
        int dataSize;
        int indexValue;

        @Override
        public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override
        public RInt getIndex() {
            return index;
        }

        @Override
        public void start(int dataSize, ASTNode ast) throws UnexpectedResultException {
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
                    if (i == 0) {
                        throw RError.getSelectLessThanOne(ast);
                    } else if (i == RInt.NA) {
                        throw RError.getSubscriptBounds(ast);
                    } else {
                        throw RError.getSelectMoreThanOne(ast);
                    }
                }
            } else {
                if (isize > 1) {
                    throw RError.getSelectMoreThanOne(ast);
                } else {
                    throw RError.getSelectLessThanOne(ast);
                }
            }
        }

        @Override
        public void restart() {
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int nextIndex(ASTNode ast) throws UnexpectedResultException {
            return indexValue;
        }

        @Override
        public boolean isExhausted() {
            return true;
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public boolean mayHaveNA() {
            return false;
        }
    }

    // non-failing
    public static final class GenericNumericSubsetSelector extends Selector {

        RInt index;
        int size; // the result size, valid after a call to restart ; before restart, either the size already or number of zeros (negativeSelection)
        int dataSize; // only needed with negative selection
        boolean positiveSelection;  // positive indexes and NA and zeros
        int indexSize; // cached index size, only needed with positive selection

        int offset;
        boolean[] omit;
        boolean hasNA;

        @Override
        public void setIndex(RAny index) {
            this.index = (RInt) index;
        }

        @Override
        public void start(int dataSize, ASTNode ast) {

            hasNA = false;
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
                    indexSize = isize;
                } else {
                    throw RError.getOnlyZeroMixed(ast);
                }
            } else {
                // no positive values
                if (hasNegative) {
                    if (!hasNA) {
                        positiveSelection = false;
                        this.dataSize = dataSize;
                        // all elements are negative, selection size will depend on the data size
                    } else {
                        throw RError.getOnlyZeroMixed(ast);
                    }
                } else {
                    positiveSelection = true;
                    indexSize = isize;
                    if (hasNA) {
                        size = isize - nzero;
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
        public int nextIndex(ASTNode ast) {
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
        public boolean isExhausted() {
            if (positiveSelection) {
                return offset == indexSize;
            } else {
                // negative selection, advancing the "offset" over elements to omit is benign
                for(;;) {
                    if (offset == dataSize) {
                        return true;
                    }
                    if (!omit[offset]) {
                        return false;
                    }
                    offset++;
                }
            }
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public boolean mayHaveNA() {
            return hasNA;
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

        @Override
        public void setIndex(RAny index) {
            this.index = (RLogical) index;
            indexSize = this.index.size();
        }

        @Override
        public void start(int dataSize, ASTNode ast) {
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
            if (isize == 0) {
                size = 0;
                return;
            }
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
        public int nextIndex(ASTNode ast) {
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
        public boolean isExhausted() {
            return indexOffset == size;
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public boolean mayHaveNA() {
            return true; // would have to do more work in start to rule out
        }

    }

    public static SelectorNode createConstantSelectorNode(ASTNode ast, RNode child, final Selector selector) {
        return new SelectorNode(ast, child) {

            @Override
            public Selector executeSelector(Frame frame) {
                return selector;
            }

            @Override
            public Selector executeSelector(RAny index) {
                return selector;
            }
        };
    }

    public static SelectorNode createSelectorNode(ASTNode ast, final boolean subset, final RNode node) {
        if (node == null) {
            if (subset) {
                return createConstantSelectorNode(ast, node, new MissingSelector());
            } else {
                // FIXME: GNU-R checks first the size of the selection - if other selectors give more values, the error
                // message is attempt to select more than one element
                throw RError.getInvalidSubscriptType(ast, "symbol");
            }
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny index = (RAny) node.execute(null);
            return createSelectorNode(ast, subset, index, node, true, null);
        }
        return new SelectorNode(ast, node) {
            @Override
            public Selector executeSelector(RAny index) {
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
                        if (index.size() == 1) {
                            return createConstantSelectorNode(ast, child, new SinglePositiveConstantIndexSelector(index.getInt(0) - 1));
                        }
                        // FIXME: handle more cases? e.g. set of positive integer indexes
                    }
                    if (subset) {
                        if (index.size() == 1) {
                            return createSimpleScalarNumericSubsetSelectorNode(ast, child);
                        } else {
                            return createSimpleNumericSubsetSelectorNode(ast, child);
                        }
                    } else {
                        return createSpecializedSimpleSubscriptSelectorNode(ast, child, template);
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

            @Override
            public Selector executeSelector(RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble) { // FIXME: can get rid of this through type-specialization
                        selector.setIndex(Convert.coerceToIntWarning(index, ast));
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

    // note: surprisingly, this kind of specialization seems not to be helping
    // so either we are still too slow for this to matter, or hotspot is very good at "instanceof" tests
    public static SelectorNode createSpecializedSimpleSubscriptSelectorNode(ASTNode ast, RNode child, RAny template) {
        final SimpleScalarNumericSelector selector = new SimpleScalarNumericSelector();
        if (template instanceof ScalarIntImpl) {


            return new SelectorNode(ast, child) {

                @Override
                public Selector executeSelector(RAny index) {
                    try {
                        if (index instanceof ScalarIntImpl) {
                            selector.setIndex(index);
                            return selector;
                        }
                        throw new UnexpectedResultException(null);
                    } catch (UnexpectedResultException e) {
                        if (DEBUG_M) Utils.debug("SpecializedSimpleSubscriptSelector failed in Selector.execute (unexpected type), replacing.");
                        SelectorNode gn = createGenericSimpleSubscriptSelectorNode(ast, child);
                        replace(gn, "install GenericSimpleSubscriptSelectorNode from SpecializedSimpleSubscriptSelectorNode");
                        return gn.executeSelector(index);
                    }
                }
            };
        }
        return createGenericSimpleSubscriptSelectorNode(ast, child);
    }

    public static SelectorNode createGenericSimpleSubscriptSelectorNode(ASTNode ast, RNode child) {
        final SimpleScalarNumericSelector selector = new SimpleScalarNumericSelector();
        return new SelectorNode(ast, child) {

            @Override
            public Selector executeSelector(RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble || index instanceof RLogical) {
                        selector.setIndex(Convert.coerceToIntWarning(index, ast));
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

            @Override
            public Selector executeSelector(RAny index) {
                try {
                    if (index instanceof RInt || index instanceof RDouble) { // FIXME: can get rid of this through type-specialization
                        selector.setIndex(Convert.coerceToIntWarning(index, ast));
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

            @Override
            public Selector executeSelector(RAny index) {
                if (index instanceof RInt || index instanceof RDouble || index instanceof RNull) {
                    numericSelector.setIndex(Convert.coerceToIntWarning(index, ast));
                    return numericSelector;
                }
                if (index instanceof RLogical) {
                    logicalSelector.setIndex(index);
                    return logicalSelector;
                }
                throw RError.getInvalidSubscriptType(ast, index.typeOf());
            }
        };
    }

    public static SelectorNode createGenericSubscriptSelectorNode(ASTNode ast, RNode child) {
        final GenericSubscriptSelector selector = new GenericSubscriptSelector();

        return new SelectorNode(ast, child) {

            @Override
            public Selector executeSelector(RAny index) {
                if (index instanceof RInt || index instanceof RDouble || index instanceof RLogical || index instanceof RNull) {
                    selector.setIndex(Convert.coerceToIntWarning(index, ast));
                    return selector;
                }
                throw RError.getInvalidSubscriptType(ast, index.typeOf());
            }
        };
    }

    public abstract static class SelectorNode extends BaseR {

        @Child RNode child;

        public SelectorNode(ASTNode ast, RNode child) {
            super(ast);
            this.child = adoptChild(child);
        }

        @Override
        public Object execute(Frame frame) {
            assert Utils.check(false, "unreachable");
            return null;
        }

        public Selector executeSelector(Frame frame) {
            RAny index = (RAny) child.execute(frame);
            return executeSelector(index);

        }

        public abstract Selector executeSelector(RAny index);

    }


}
