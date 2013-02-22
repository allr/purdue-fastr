package r.nodes.truffle;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.*;
import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.ASTNode;

/** Array update AST and its specializations. */
public class UpdateArray extends UpdateVariable.AssignmentNode {

    static final boolean DEBUG_UP = false;

    /** determines whether [] or [[]] operators were used (subset == []). */
    final boolean subset;

    /** Selector nodes for respective dimensions. These are likely to be rewritten. */
    @Children Selector.SelectorNode[] selectors;

    final Selector[] selectorsVal;
    final int[] selSizes;
    final int[] idx;
    final int[] selIdx;

    /** Constructor from scratch. */
    public UpdateArray(ASTNode ast, Selector.SelectorNode[] selectors, boolean subset) {
        super(ast);
        this.subset = subset;
        this.selectors = new Selector.SelectorNode[selectors.length];
        for (int i = 0; i < selectors.length; ++i) {
            this.selectors[i] = adoptChild(selectors[i]);
        }
        selectorsVal = new Selector[selectors.length];
        selSizes = new int[selectors.length];
        idx = new int[selectors.length];
        selIdx = new int[selectors.length];
    }

    /** Copy constructor used in node replacements. */
    public UpdateArray(UpdateArray other) {
        super(other.ast);
        subset = other.subset;
        selectors = new Selector.SelectorNode[other.selectors.length];
        for (int i = 0; i < selectors.length; ++i) {
            selectors[i] = adoptChild(other.selectors[i]);
        }
        selectorsVal = other.selectorsVal;
        selSizes = other.selSizes;
        idx = other.idx;
        selIdx = other.selIdx;
    }

    /**
     * Returns true if the given type (from) is implicitly convertible to the other type. So for
     * example logical type is always convertible and string is only convertible to string itself.
     */
    protected final boolean isConvertible(RAny from, RAny to) {
        if (from.getClass() == to.getClass()) { // same types are always convertible.
            return true;
        }
        if ((to instanceof RDouble) && (from instanceof RInt || from instanceof RDouble || from instanceof RLogical)) {
            return true;
        }
        if ((to instanceof RInt) && (from instanceof RInt || from instanceof RLogical)) {
            return true;
        }
        if ((to instanceof RComplex) && (from instanceof RInt || from instanceof RDouble || from instanceof RComplex || from instanceof RLogical)) {
            return true;
        }
        if (to instanceof RString) { // everything is convertible to a string
            return true;
        }
        if ((to instanceof RLogical) && (from instanceof RLogical)) {
            return true;
        }
        return false;
    }

    /**
     * The most general node only asks itself if the left hand side has to be copied.
     * <p/>
     * At the moment, this is very simple calculation: we do not copy the left hand side only if it
     * is not shared, and if the rhs is a constant scalar(!) of the same type as the lhs, or of an
     * easily convertible type.
     * <p/>
     * In all other cases the copy node is first injected to the code.
     */
    @Override
    public RAny execute(Frame frame, RAny lhs, RAny rhs) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            if (!lhs.isShared()
                    && isConvertible(rhs, lhs)
                    && rhs instanceof RArray
                    && ((RArray) rhs).size() == 1) {
                if (DEBUG_UP) Utils.debug("UpateArray -> RHSCompatible (no need of LHS copy)");
                return replace(new RHSCompatible(this)).execute(frame, lhs, rhs);
            }
            if (DEBUG_UP) Utils.debug("UpateArray -> CopyLHS");
            return replace(new CopyLhs(new RHSCompatible(this))).execute(frame, lhs, rhs);
        }
    }

    /**
     * Initializes the selectors and runs the update method. If the selectors fail, replaces them
     * and reruns the update method.
     * <p/>
     * This method must be called by the execute methods of the update array specifications to
     * proceed further. It assumes (without checking) that the lhs and rhs arrays are of the same
     * type.
     */
    protected final RAny executeAndUpdateSelectors(Frame frame, RArray lhs, RArray rhs) {
        // this is safe even for the non-shared variant, because here we already have the copy,
// which is always
        // not shared
        try {
            if (lhs.isShared()) {
                throw new UnexpectedResultException(null);
            }
            for (int i = 0; i < selectorsVal.length; ++i) {
                selectorsVal[i] = selectors[i].executeSelector(frame);
            }
            while (true) {
                try {
                    return update(lhs, rhs, selectorsVal);
                } catch (UnexpectedResultException e) {
                    Selector failedSelector = (Selector) e.getResult();
                    for (int i = 0; i < selectorsVal.length; ++i) {
                        if (selectorsVal[i] == failedSelector) {
                            RAny index = failedSelector.getIndex();
                            Selector.SelectorNode newSelector = Selector.createSelectorNode(ast, subset, index, selectors[i], false, failedSelector.getTransition());
                            replaceChild(selectors[i], newSelector);
                            assert (selectors[i] == newSelector);
                            selectorsVal[i] = newSelector.executeSelector(index);
                            if (DEBUG_UP) Utils.debug("Selector " + i + " changed...");
                        }
                    }
                }
            }
        } catch (UnexpectedResultException e) {
            if (DEBUG_UP) Utils.debug(getClass().getSimpleName() + " -> Generalized");
            return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
        }
    }

    /**
     * A basic in-place update of the selection.
     * <p/>
     * Updates the lhs array with the rhs array information using given selectors. Override this
     * method for different array manipulation.
     */

    protected RArray update(RArray lhs, RArray rhs, Selector[] selectors) throws UnexpectedResultException {
        Selector.initializeSelectors(lhs, selectors, ast, selSizes);
        int rhsSize = rhs.size();
        int replacementSize = Selector.calculateSizeFromDimensions(selSizes);
        if (!subset && (rhsSize > 1)) {
            throw RError.getSelectMoreThanOne(getAST());
        }
        // fill in the index vector
        for (int i = 0; i < idx.length; ++i) {
            idx[i] = selectors[i].nextIndex(ast);
            selIdx[i] = 1; // start at one so that overflow and carry works
        }
        while (replacementSize >= rhsSize) {
            // loop over the dest offset and update the index vector, store the values
            for (int rhsOffset = 0; rhsOffset < rhsSize; ++rhsOffset) {
                int lhsOffset = Selector.calculateSourceOffset(lhs, idx);
                // do nothing if lhsOffset is NA
                if (lhsOffset != RInt.NA) {
                    lhs.set(lhsOffset, rhs.getRef(rhsOffset));
                }
                Selector.increment(idx, selIdx, selSizes, selectors, ast);
            }
            replacementSize -= rhsSize;
        }
        if (replacementSize != 0) {
            throw RError.getNotMultipleReplacement(ast);
        }
        // return the lhs so that it can be updated by parent, if required
        return lhs;
    }

    // =================================================================================================================
    // Non-shared
    // =================================================================================================================

    /**
     * Node which assumes that the LHS is not shared - more precisely that it either does not need
     * to be copied, or has already been copied and it sees the copy. If the lhs and rhs types are
     * the same, the node reqrites itself to the next step which is IdenticalTypes node, otherwise
     * injects the CopyRhs node before the IdenticalTypes.
     */
    protected static class RHSCompatible extends UpdateArray {

        public RHSCompatible(UpdateArray other) {
            super(other);
        }

        /**
         * The non-shared update assumes that the lhs is a non-shared array with the same type as
         * rhs, or of a type to which the rhs can be converted, so it just determines whether to
         * replace itself with a rhs convertor node, or whether to proceed directly to the
         * IdenticalTypes node.
         */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if ((lhs instanceof RDouble && rhs instanceof RDouble) || (lhs instanceof RInt && rhs instanceof RInt) || (lhs instanceof RLogical && rhs instanceof RLogical) ||
                                (lhs instanceof RString && rhs instanceof RString) || (lhs instanceof RComplex && rhs instanceof RComplex) || (lhs instanceof RRaw && rhs instanceof RRaw)) {
                    if (DEBUG_UP) Utils.debug("RHSCompatible -> IdenticalTypes (no need of rhs copy)");
                    return replace(new IdenticalTypes(this)).execute(frame, lhs, rhs);
                }
                if (DEBUG_UP) Utils.debug("RHSCompatible -> CopyRhs (IdenticalTypes as child)");
                return replace(new CopyRhs(new IdenticalTypes(this))).execute(frame, lhs, rhs);
            }
        }
    }

    // =================================================================================================================
    // IdenticalTypes
    // =================================================================================================================

    /**
     * Node at which the lhs and rhs are of the same type and can thus be immediately updated.
     * <p/>
     * If the rhs is constant scalar, uses the ConstScalar version of the update, otherwise uses the
     * NonScalar version.
     */
    protected static class IdenticalTypes extends UpdateArray {

        public IdenticalTypes(UpdateArray other) {
            super(other);
        }

        /** Rewrites itself to either ConstScalar updater, or to the more generic NonScalar updater. */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (rhs instanceof RArray && ((RArray) rhs).size() == 1) {
                    if (DEBUG_UP) Utils.debug("IdenticalTypes -> ConstScalar");
                    return replace(new Scalar(this)).execute(frame, lhs, rhs);
                }
                if (DEBUG_UP) Utils.debug("IdenticalTypes -> NonScalar");
                return replace(new NonScalar(this)).execute(frame, lhs, rhs);
            }
        }
    }

    // =================================================================================================================
    // Generalized
    // =================================================================================================================

    /**
     * The generalized fall-back node for array update.
     * <p/>
     * Whenever the assumtions of specialized nodes in the update array node tree fail, the whole
     * tree is rewritten to this node, which does all:
     * <p/>
     * - makes copy of the LHS if required - makes copy of the RHS if required - runs the
     * generalized update method with selectors and non-const non-scalar rhs vector (this will work
     * for const scalars too, just not with the greates speeds)
     * <p/>
     * In general, this node is used whenever the type information on lhs and rhs side of the update
     * changes at runtime.
     * <p/>
     * TODO Maybe by making the general case less aggressive and allowing for instance to recompute
     * the copy LHS and copy RHS arguments better results can be achieved.
     */
    protected static class Generalized extends UpdateArray {

        public Generalized(UpdateArray other) {
            super(other);
        }

        /**
         * Replaces the whole updateArray tree of the given node by the Generalized node instance.
         * This gets rid of any UpdateArray descendants, CopyLhs or CopyRhs nodes in the tree
         * leaving in it only the Generalized node since it has all the required functionality.
         * <p/>
         * When replacing a node to the Generalized one, this method should always be used instead
         * of simple replace.
         */
        public static Generalized replaceArrayUpdateTree(UpdateArray tree) {
            Node root = tree;
            if (root.getParent() instanceof CopyRhs) {
                if (DEBUG_UP) Utils.debug("Replacing update tree - skipping copy rhs node");
                root = root.getParent();
            }
            if (root.getParent() instanceof CopyLhs) {
                if (DEBUG_UP) Utils.debug("Replacing update tree - skipping copy lhs node");
                root = root.getParent();
            }
            return root.replace(new Generalized(tree));
        }

        /**
         * This is the general case that performs all the computations at once. In the slowpath
         * makes a copy of the lhs and determines if a copy of the rhs should be made and then runs
         * the update of arrays for non-const rhs values (this will work for the const values too,
         * of course).
         */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            // 1) check if we need to copy the LHS side
            ValueCopy.Impl lhsImpl = CopyLhs.determineCopyImplementation(lhs, rhs);
            if (lhsImpl != null) {
                try {
                    lhs = lhsImpl.copy(lhs);
                } catch (UnexpectedResultException e) {
                    assert (false) : "unreachable";
                }
            }
            // now check if we need to copy the RHS
            ValueCopy.Impl rhsImpl = CopyRhs.determineCopyImplementation(lhs, rhs);
            if (rhsImpl != null) {
                try {
                    rhs = rhsImpl.copy(rhs);
                } catch (UnexpectedResultException e) {
                    assert (false) : "unreachable";
                }
            }
            // we have lhs and rhs, if everything goes well, and is implemented they will be of the
// same type.
            // TODO However because not everything is implemented as of now, I am keeping the
// checks.
            if ((lhs instanceof RInt && !(rhs instanceof RInt)) || (lhs instanceof RDouble && !(rhs instanceof RDouble)) || (lhs instanceof RLogical && !(rhs instanceof RLogical)) ||
                            (lhs instanceof RComplex && !(rhs instanceof RComplex)) || (lhs instanceof RString && !(rhs instanceof RString)) || (lhs instanceof RRaw && !(rhs instanceof RRaw))) {
                Utils.nyi("Unable to perform the update of the array - unimplemented copy");
            }
            return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
        }
    }

// =================================================================================================================
// CopyLhs
// =================================================================================================================

    /**
     * Special node that injects the copying of the lhs object when it is shared.
     * <p/>
     * LHS is copied whenever the RHS is non-const non-scalar due to aliasing as well as whenever
     * the lhs is shared, or when its type has to change.
     * <p/>
     * This is determined by the UpdateArray node. Here, the node only determines which copy should
     * be used as copying may also change the type of the lhs (i.e. when storing double into int
     * array, etc).
     * <p/>
     * First execution determines which copy/typecast should be used and rewrites to the specific
     * case. Subsequent calls check the type of the lhs for the copy and on failure rewrite the
     * three to the Generalized array update.
     * <p/>
     * Its child is the actual copying code (or CopyRhs)
     */
    protected static class CopyLhs extends UpdateArray {

        @Child UpdateArray child;

        /**
         * Standard constructor. The update array supplied is also used as the child (the update
         * array node that will do the update, or at least proceed further like the optional copyRhs
         * node).
         */
        public CopyLhs(UpdateArray child) {
            super(child);
            this.child = adoptChild(child);
        }

        public CopyLhs(CopyLhs other) {
            super(other);
            this.child = adoptChild(other.child);
        }

        /**
         * Determines which copy method should be used for the lhs in given rhs settings.
         * <p/>
         * When the RHS atomic type cannot fit to the LHS, then the LHS must be copied and its type
         * changed to the dominating type of the RHS. This method determines if this situation
         * occurs, or returns a simple copy of the lhs value implementation with no type changes.
         * <p/>
         * TODO: what to do with Raw values?
         */
        protected static ValueCopy.Impl determineCopyImplementation(RAny lhs, RAny rhs) {
            RAny.Mode rhsMode = ValueCopy.valueMode(rhs);
            RAny.Mode lhsMode = ValueCopy.valueMode(lhs);
            switch (rhsMode) {
                case LOGICAL: // logical rhs will always fit
                    break;
                case INT: // integer won't fit to logical
                    if (lhsMode == RAny.Mode.LOGICAL) {
                        return ValueCopy.LOGICAL_TO_INT;
                    }
                    break;
                case DOUBLE: // double does not fit to logical and int
                    switch (lhsMode) {
                        case LOGICAL:
                            return ValueCopy.LOGICAL_TO_DOUBLE;
                        case INT:
                            return (Configuration.ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS &&  lhs instanceof IntImpl) ? ValueCopy.INT_TO_DOUBLE_DIRECT : ValueCopy.INT_TO_DOUBLE;
                    }
                    break;
                case COMPLEX: // complex does not fit to logical, int and double
                    switch (lhsMode) {
                        case LOGICAL:
                            return ValueCopy.LOGICAL_TO_COMPLEX;
                        case INT:
                            return (Configuration.ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS &&  lhs instanceof IntImpl) ? ValueCopy.INT_TO_COMPLEX_DIRECT : ValueCopy.INT_TO_COMPLEX;
                        case DOUBLE:
                            return (Configuration.ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS &&  lhs instanceof DoubleImpl) ? ValueCopy.DOUBLE_TO_COMPLEX_DIRECT : ValueCopy.DOUBLE_TO_COMPLEX;
                    }
                    break;
                case STRING: // string only fits into a string
                    switch (lhsMode) {
                        case LOGICAL:
                            return ValueCopy.LOGICAL_TO_STRING;
                        case INT:
                            return ValueCopy.INT_TO_STRING;
                        case DOUBLE:
                            return ValueCopy.DOUBLE_TO_STRING;
                        case COMPLEX:
                            return ValueCopy.COMPLEX_TO_STRING;
                    }
                    break;
                default:
                    Utils.nyi("unable to determine which copy to use for LHS");
            }
            // if we are here that means rhs fits to lhs ok, but we still must make a copy, therefore make a
            // non-typecasting copy of the lhs
            switch (lhsMode) {
                case LOGICAL:
                    return ValueCopy.LOGICAL_TO_LOGICAL;
                case INT:
                    return (Configuration.ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS &&  lhs instanceof IntImpl) ? ValueCopy.INT_TO_INT_DIRECT : ValueCopy.INT_TO_INT;
                case DOUBLE:
                    return (Configuration.ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS &&  lhs instanceof DoubleImpl) ? ValueCopy.DOUBLE_TO_DOUBLE_DIRECT : ValueCopy.DOUBLE_TO_DOUBLE;
                case COMPLEX:
                    return (Configuration.ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS &&  lhs instanceof ComplexImpl) ? ValueCopy.COMPLEX_TO_COMPLEX_DIRECT : ValueCopy.COMPLEX_TO_COMPLEX;
                case STRING:
                    return ValueCopy.STRING_TO_STRING;
                default:
                    return ValueCopy.RAW_TO_RAW;
            }
        }

        /**
         * Replaces itself with the specialized node for the required copy/typecast of the lhs
         * value, which is calculated from the lhs and rhs types.
         */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
              return replace(new Specialized(this, determineCopyImplementation(lhs, rhs))).execute(frame, lhs, rhs);
            }
        }

        /**
         * Specialized copy node that expects the lhs to be of a particular type. Just calls the
         * implementation in its execute method and then calls the child's execute on the copied
         * lhs. If the copying fails, rewrites itself to the generalized copy lhs node.
         */
        protected static class Specialized extends CopyLhs {

            /** The copy/typecast method to be used. */
            final ValueCopy.Impl impl;

            /** Standard constructor. */
            public Specialized(CopyLhs other, ValueCopy.Impl copy) {
                super(other);
                this.impl = copy;
            }

            /**
             * Copies the lhs and then executes the child of the copy lhs node (the assignment
             * itself). Upon failure of the copy code reqrites the whole tree to the general case.
             */
            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    lhs = impl.copy(lhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("CopyLhs.Specialized -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
                return child.execute(frame, lhs, rhs);
            }
        }
    }

    // =================================================================================================================
    // CopyRhs
    // =================================================================================================================

    /**
     * An optional node that typecasts (copies) the rhs so that it is of the same type as the lhs.
     * This is not the most effective way, but simplifies the code greatly and since the typecasted
     * updates are not that optimized, it may still be ok. Works in the same way as the CopyLhs
     * class.
     */
    protected static class CopyRhs extends UpdateArray {

        /** Further update node that will be executed on the typecasted rhs. */
        @Child UpdateArray child;

        /** Standard constructor. The given UpdateArray object will be used as a child. */
        public CopyRhs(UpdateArray child) {
            super(child);
            this.child = adoptChild(child);
        }

        public CopyRhs(CopyRhs other) {
            super(other);
            this.child = adoptChild(other.child);
        }

        /**
         * Determines which typecast to be used on the rhs to bring it to the same type as the lhs.
         * If no such implicit conversion exists, returns null - which should be an exceptional case
         * of runtime type change.
         */
        protected static ValueCopy.Impl determineCopyImplementation(RAny lhs, RAny rhs) {
            RAny.Mode lhsMode = ValueCopy.valueMode(lhs);
            RAny.Mode rhsMode = ValueCopy.valueMode(rhs);
            switch (lhsMode) {
                case INT:
                    if (rhsMode == RAny.Mode.LOGICAL)
                        return ValueCopy.LOGICAL_TO_INT;
                    break;
                case DOUBLE:
                    switch (rhsMode) {
                        case LOGICAL:
                            return ValueCopy.LOGICAL_TO_DOUBLE;
                        case INT:
                            return (Configuration.ARRAY_UPDATE_RHS_VALUECOPY_DIRECT_ACCESS &&  rhs instanceof IntImpl) ? ValueCopy.INT_TO_DOUBLE_DIRECT : ValueCopy.INT_TO_DOUBLE;
                    }
                    break;
                case COMPLEX:
                    switch (rhsMode) {
                        case LOGICAL:
                            return ValueCopy.LOGICAL_TO_COMPLEX;
                        case INT:
                            return (Configuration.ARRAY_UPDATE_RHS_VALUECOPY_DIRECT_ACCESS &&  rhs instanceof IntImpl) ? ValueCopy.INT_TO_COMPLEX_DIRECT : ValueCopy.INT_TO_COMPLEX;
                        case DOUBLE:
                            return (Configuration.ARRAY_UPDATE_RHS_VALUECOPY_DIRECT_ACCESS &&  rhs instanceof DoubleImpl) ? ValueCopy.DOUBLE_TO_COMPLEX_DIRECT : ValueCopy.DOUBLE_TO_COMPLEX;
                    }
                    break;
                case STRING:
                    switch (rhsMode) {
                        case LOGICAL:
                            return ValueCopy.LOGICAL_TO_STRING;
                        case INT:
                            return ValueCopy.INT_TO_STRING;
                        case DOUBLE:
                            return ValueCopy.DOUBLE_TO_STRING;
                        case COMPLEX:
                            return ValueCopy.COMPLEX_TO_STRING;
                    }
                    break;
            }
            return null;
        }

        /**
         * Reqrites itself to the specialized copy rhs node which knows the typecast to run. If no
         * such typecast can be found, reqrites itself to the generalized case.
         */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                ValueCopy.Impl impl = determineCopyImplementation(lhs, rhs);
                if (impl == null) {
                    if (DEBUG_UP) Utils.debug("CopyLhs -> Generalized (not know how to copy lhs)");
                    return UpdateArray.Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
                return replace(new Specialized(this, impl)).execute(frame, lhs, rhs);
            }
        }

        /** Specialized CopyRhs version that knows the typecast to be used on the rhs. */
        protected static class Specialized extends CopyRhs {

            /** Typecast for the rhs. */
            final ValueCopy.Impl impl;

            public Specialized(CopyRhs other, ValueCopy.Impl impl) {
                super(other);
                this.impl = impl;
            }

            /**
             * Executes the child on the typecasted rhs and given lhs. If the typecast fails
             * rewrites itself to the generalized case.
             */
            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    rhs = impl.copy(rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("CopyRhs.Specialized -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
                return child.execute(frame, lhs, rhs);
            }
        }

    }


    // =================================================================================================================
    // Scalar
    // =================================================================================================================
    /** Update by a scalar variable.
     */
    public static class Scalar extends UpdateArray {

        public Scalar(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (rhs instanceof RInt) {
                    return replace(new Int(this)).execute(frame, lhs, rhs);
                } else if (rhs instanceof RDouble) {
                    return replace(new Double(this)).execute(frame, lhs, rhs);
                } else if (rhs instanceof RComplex) {
                    return replace(new Complex(this)).execute(frame, lhs, rhs);
                } else if (rhs instanceof RLogical) {
                    return replace(new Logical(this)).execute(frame, lhs, rhs);
                } else if (rhs instanceof RString) {
                    return replace(new String(this)).execute(frame, lhs, rhs);
                } else {
                    return replace(new NonScalar.Raw(this)).execute(frame, lhs, rhs);
                }
            }
        }

        /** Array update with logical scalar.
         *
         * Uses direct access and RHS one time evaluation for the array update.
         */
        public static class Logical extends Scalar {

            public Logical(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if ((!(rhs instanceof ScalarLogicalImpl)) || (!(lhs instanceof LogicalImpl))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, null);
                } catch (UnexpectedResultException e) {
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs, Selector[] selectors) throws UnexpectedResultException {
                Selector.initializeSelectors(lhs, selectors, ast, selSizes);
                int updateSize = Selector.calculateSizeFromDimensions(selSizes);
                if (!subset && (updateSize > 1)) {
                    throw RError.getSelectMoreThanOne(getAST());
                }
                // get lhs value and rhs
                int[] lhsVal = ((LogicalImpl) lhs).getContent();
                int rhsVal = ((RLogical) rhs).getLogical(0);
                // fill in the index vector
                for (int i = 0; i < idx.length; ++i) {
                    idx[i] = selectors[i].nextIndex(ast);
                    selIdx[i] = 1; // start at one so that overflow and carry works
                }
                // loop over the update size
                for (int i = 0; i < updateSize; ++i) {
                    int lhsOffset = Selector.calculateSourceOffset(lhs, idx);
                    // do nothing if lhsOffset is NA
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    Selector.increment(idx, selIdx, selSizes, selectors, ast);
                }
                // return the lhs so that it can be updated by parent, if required
                return lhs;
            }
        }

        /** Array update with int scalar.
         *
         * Uses direct access and RHS one time evaluation for the array update.
         */
        public static class Int extends Scalar {

            public Int(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if ((!(rhs instanceof ScalarIntImpl)) || (!(lhs instanceof IntImpl))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs, Selector[] selectors) throws UnexpectedResultException {
                Selector.initializeSelectors(lhs, selectors, ast, selSizes);
                int updateSize = Selector.calculateSizeFromDimensions(selSizes);
                if (!subset && (updateSize > 1)) {
                    throw RError.getSelectMoreThanOne(getAST());
                }
                // get lhs value and rhs
                int[] lhsVal = ((IntImpl) lhs).getContent();
                int rhsVal = ((RInt) rhs).getInt(0);
                // fill in the index vector
                for (int i = 0; i < idx.length; ++i) {
                    idx[i] = selectors[i].nextIndex(ast);
                    selIdx[i] = 1; // start at one so that overflow and carry works
                }
                // loop over the update size
                for (int i = 0; i < updateSize; ++i) {
                    int lhsOffset = Selector.calculateSourceOffset(lhs, idx);
                    // do nothing if lhsOffset is NA
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    Selector.increment(idx, selIdx, selSizes, selectors, ast);
                }
                // return the lhs so that it can be updated by parent, if required
                return lhs;
            }
        }

        /** Array update with double scalar.
         *
         * Uses direct access and RHS one time evaluation for the array update.
         */
        public static class Double extends Scalar {

            public Double(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if ((!(rhs instanceof ScalarDoubleImpl)) || (!(lhs instanceof DoubleImpl))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs, Selector[] selectors) throws UnexpectedResultException {
                Selector.initializeSelectors(lhs, selectors, ast, selSizes);
                int updateSize = Selector.calculateSizeFromDimensions(selSizes);
                if (!subset && (updateSize > 1)) {
                    throw RError.getSelectMoreThanOne(getAST());
                }
                // get lhs value and rhs
                double[] lhsVal = ((DoubleImpl) lhs).getContent();
                double rhsVal = ((RDouble) rhs).getDouble(0);
                // fill in the index vector
                for (int i = 0; i < idx.length; ++i) {
                    idx[i] = selectors[i].nextIndex(ast);
                    selIdx[i] = 1; // start at one so that overflow and carry works
                }
                // loop over the update size
                for (int i = 0; i < updateSize; ++i) {
                    int lhsOffset = Selector.calculateSourceOffset(lhs, idx);
                    // do nothing if lhsOffset is NA
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    Selector.increment(idx, selIdx, selSizes, selectors, ast);
                }
                // return the lhs so that it can be updated by parent, if required
                return lhs;
            }
        }

        /** Array update with complex scalar.
         *
         * Uses direct access and RHS one time evaluation for the array update.
         */
        public static class Complex extends Scalar {

            public Complex(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if ((!(rhs instanceof ScalarComplexImpl)) || (!(lhs instanceof ComplexImpl))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs, Selector[] selectors) throws UnexpectedResultException {
                Selector.initializeSelectors(lhs, selectors, ast, selSizes);
                int updateSize = Selector.calculateSizeFromDimensions(selSizes);
                if (!subset && (updateSize > 1)) {
                    throw RError.getSelectMoreThanOne(getAST());
                }
                // get lhs value and rhs
                double[] lhsVal = ((ComplexImpl) lhs).getContent();
                double re = ((RComplex) rhs).getReal(0);
                double im = ((RComplex) rhs).getImag(0);
                // fill in the index vector
                for (int i = 0; i < idx.length; ++i) {
                    idx[i] = selectors[i].nextIndex(ast);
                    selIdx[i] = 1; // start at one so that overflow and carry works
                }
                // loop over the update size
                for (int i = 0; i < updateSize; ++i) {
                    int lhsOffset = Selector.calculateSourceOffset(lhs, idx);
                    // do nothing if lhsOffset is NA
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset << 1] = re;
                        lhsVal[(lhsOffset << 1) + 1] = im;
                    }
                    Selector.increment(idx, selIdx, selSizes, selectors, ast);
                }
                // return the lhs so that it can be updated by parent, if required
                return lhs;
            }
        }

        /** Array update with String scalar.
         *
         * Uses direct access and RHS one time evaluation for the array update.
         */
        public static class String extends Scalar {

            public String(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if ((!(rhs instanceof ScalarStringImpl)) || (!(lhs instanceof StringImpl))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs, Selector[] selectors) throws UnexpectedResultException {
                Selector.initializeSelectors(lhs, selectors, ast, selSizes);
                int updateSize = Selector.calculateSizeFromDimensions(selSizes);
                if (!subset && (updateSize > 1)) {
                    throw RError.getSelectMoreThanOne(getAST());
                }
                // get lhs value and rhs
                java.lang.String[] lhsVal = ((StringImpl) lhs).getContent();
                java.lang.String rhsVal = ((RString) rhs).getString(0);
                // fill in the index vector
                for (int i = 0; i < idx.length; ++i) {
                    idx[i] = selectors[i].nextIndex(ast);
                    selIdx[i] = 1; // start at one so that overflow and carry works
                }
                // loop over the update size
                for (int i = 0; i < updateSize; ++i) {
                    int lhsOffset = Selector.calculateSourceOffset(lhs, idx);
                    // do nothing if lhsOffset is NA
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    Selector.increment(idx, selIdx, selSizes, selectors, ast);
                }
                // return the lhs so that it can be updated by parent, if required
                return lhs;
            }
        }
    }


    // =================================================================================================================
    // NonScalar
    // =================================================================================================================

    /**
     * Nonconst update method.
     * <p/>
     * Upon first execution reqrites itself to the appropriate method checking only the lhs are rhs
     * types are the same.
     */
    protected static class NonScalar extends UpdateArray {

        public NonScalar(UpdateArray other) {
            super(other);
        }

        /**
         * If rhs and lhs are the same rewrites itself to the specialized case. It should never
         * happen that in this node the lhs and rhs will be different (remember first executions
         * step through copy lhs and copy rhs nodes which would make the lhs and rhs types the same.
         */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if ((lhs instanceof RLogical) && (rhs instanceof RLogical)) {
                    if (DEBUG_UP) Utils.debug("NonScalar -> Logical");
                    return replace(new Logical(this)).execute(frame, lhs, rhs);
                }
                if ((lhs instanceof RInt) && (rhs instanceof RInt)) {
                    if (DEBUG_UP) Utils.debug("NonScalar -> Integer");
                    return replace(new Integer(this)).execute(frame, lhs, rhs);
                }
                if ((lhs instanceof RDouble) && (rhs instanceof RDouble)) {
                    if (DEBUG_UP) Utils.debug("NonScalar -> Double");
                    return replace(new Double(this)).execute(frame, lhs, rhs);
                }
                if ((lhs instanceof RComplex) && (rhs instanceof RComplex)) {
                    if (DEBUG_UP) Utils.debug("NonScalar -> Complex");
                    return replace(new Complex(this)).execute(frame, lhs, rhs);
                }
                if ((lhs instanceof RString) && (rhs instanceof RString)) {
                    if (DEBUG_UP) Utils.debug("NonScalar -> String");
                    return replace(new String(this)).execute(frame, lhs, rhs);
                }
                Utils.nyi();
                return null;
            }
        }

        /**
         * Logical non-const update. If the lhs and rhs are not both logical, rewrites the tree to
         * the general case.
         */
        protected static final class Logical extends NonScalar {

            public Logical(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if (!(lhs instanceof RLogical) || (!(rhs instanceof RLogical))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("NonScalar.Logical -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }

        /**
         * Integer non-const update. If the lhs and rhs are not both integer, rewrites the tree to
         * the general case.
         */
        protected static final class Integer extends NonScalar {

            public Integer(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if (!(lhs instanceof RInt) || (!(rhs instanceof RInt))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("NonScalar.Int -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }

        /**
         * Double non-const update. If the lhs and rhs are not both double, rewrites the tree to the
         * general case.
         */
        protected static final class Double extends NonScalar {

            public Double(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if (!(lhs instanceof RDouble) || (!(rhs instanceof RDouble))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("NonScalar.Double -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }

        /**
         * Complex non-const update. If the lhs and rhs are not both complex, rewrites the tree to
         * the general case.
         */
        protected static final class Complex extends NonScalar {

            public Complex(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if (!(lhs instanceof RComplex) || (!(rhs instanceof RComplex))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("NonScalar.Complex -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }

        /**
         * String non-const update. If the lhs and rhs are not both string, rewrites the tree to the
         * general case.
         */
        protected static final class String extends NonScalar {

            public String(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if (!(lhs instanceof RString) || (!(rhs instanceof RString))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("NonScalar.String -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }

        /**
         * Raw non-scalar update. If the lhs and rhs are not both raw, rewrites the tree to the
         * general case.
         */
        protected static final class Raw extends NonScalar {

            public Raw(UpdateArray other) {
                super(other);
            }

            @Override
            public RAny execute(Frame frame, RAny lhs, RAny rhs) {
                try {
                    if (!(lhs instanceof RRaw) || (!(rhs instanceof RRaw))) {
                        throw new UnexpectedResultException(null);
                    }
                    return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("NonScalar.Raw -> Generalized");
                    return Generalized.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }
    }

}

// =====================================================================================================================
// ValueCopy
// =====================================================================================================================

// TODO I'd rather have this in a separate file as it can be used elsewhere too

/**
 * Holds the list of all possible copies / typecasts that can be done on vector and their
 * implementations. Their names are self explanatory.
 *
 * the _DIRECT suffixed copies utilize the direct access to the source array and fail if the direct access cannot be
 * obtained. Direct access is supported only for numeric (int, double, complex) arrays and can be turned on or off for
 * either LHS or RHS by updating the flags ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS or
 * ARRAY_UPDATE_RHS_VALUECOPY_DIRECT_ACCESS.
 */
class ValueCopy {

    // TODO this should somehow be part of RAny or something

    /** Determines the mode of the given value. */
    public static RAny.Mode valueMode(RAny value) {
        if (value instanceof RInt) {
            return RAny.Mode.INT;
        } else if (value instanceof RLogical) {
            return RAny.Mode.LOGICAL;
        } else if (value instanceof RDouble) {
            return RAny.Mode.DOUBLE;
        } else if (value instanceof RComplex) {
            return RAny.Mode.COMPLEX;
        } else if (value instanceof RString) {
            return RAny.Mode.STRING;
        } else {
            return RAny.Mode.RAW;
        }
    }

    protected abstract static class Impl {

        public abstract RAny copy(RAny what) throws UnexpectedResultException;
    }

    public static final Impl LOGICAL_TO_LOGICAL = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RLogical)) {
                throw new UnexpectedResultException(null);
            }
            RLogical from = (RLogical) what;
            int[] result = new int[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = from.getLogical(i);
            }
            return RLogical.RLogicalFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl LOGICAL_TO_INT = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RLogical)) {
                throw new UnexpectedResultException(null);
            }
            RLogical from = (RLogical) what;
            int[] result = new int[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.logical2int(from.getLogical(i));
            }
            return RInt.RIntFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_INT = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RInt)) {
                throw new UnexpectedResultException(null);
            }
            RInt from = (RInt) what;
            int[] result = new int[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = from.getInt(i);
            }
            return RInt.RIntFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_INT_DIRECT = new Impl() {
        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof IntImpl)) {
                throw new UnexpectedResultException(null);
            }
            RInt old = (RInt) what;
            int[] from = ((IntImpl) what).getContent();
            int[] result = new int[from.length];
            System.arraycopy(from, 0, result, 0, result.length);
            return RInt.RIntFactory.getFor(result, old.dimensions(), old.names(), old.attributesRef());
        }
    };

    public static final Impl LOGICAL_TO_DOUBLE = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RLogical)) {
                throw new UnexpectedResultException(null);
            }
            RLogical from = (RLogical) what;
            double[] result = new double[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.logical2double(from.getLogical(i));
            }
            return RDouble.RDoubleFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_DOUBLE = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RInt)) {
                throw new UnexpectedResultException(null);
            }
            RInt from = (RInt) what;
            double[] result = new double[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.int2double(from.getInt(i));
            }
            return RDouble.RDoubleFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_DOUBLE_DIRECT = new Impl() {
        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof IntImpl)) {
                throw new UnexpectedResultException(null);
            }
            RInt old = (RInt) what;
            int[] from = ((IntImpl) what).getContent();
            double[] result = new double[from.length];
            for (int i = 0; i < result.length; ++i) {
                result[i] = from[i];
            }
            return RDouble.RDoubleFactory.getFor(result, old.dimensions(), old.names(), old.attributesRef());
        }
    };

    public static final Impl DOUBLE_TO_DOUBLE = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RDouble)) {
                throw new UnexpectedResultException(null);
            }
            RDouble from = (RDouble) what;
            double[] result = new double[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = from.getDouble(i);
            }
            return RDouble.RDoubleFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl DOUBLE_TO_DOUBLE_DIRECT = new Impl() {
        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof DoubleImpl)) {
                throw new UnexpectedResultException(null);
            }
            RDouble old = (RDouble) what;
            double[] from = ((DoubleImpl) what).getContent();
            double[] result = new double[from.length];
            System.arraycopy(from, 0, result, 0, result.length);
            return RDouble.RDoubleFactory.getFor(result, old.dimensions(), old.names(), old.attributesRef());
        }
    };

    public static final Impl LOGICAL_TO_COMPLEX = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RLogical)) {
                throw new UnexpectedResultException(null);
            }
            RLogical from = (RLogical) what;
            double[] result = new double[from.size() * 2];
            for (int i = 0; i < result.length >> 1; ++i) {
                result[i << 1] = from.getLogical(i);
                // img[i] is 0
            }
            return RComplex.RComplexFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_COMPLEX = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RInt)) {
                throw new UnexpectedResultException(null);
            }
            RInt from = (RInt) what;
            double[] result = new double[from.size() * 2];
            for (int i = 0; i < result.length >> 1; ++i) {
                result[i << 1] = from.getInt(i);
                // img[i] is 0
            }
            return RComplex.RComplexFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_COMPLEX_DIRECT = new Impl() {
        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof IntImpl)) {
                throw new UnexpectedResultException(null);
            }
            RInt old = (RInt) what;
            int[] from = ((IntImpl) what).getContent();
            double[] result = new double[from.length * 2];
            for (int i = 0; i < from.length; ++i) {
                result[i << 1] = from[i];
            }
            return RComplex.RComplexFactory.getFor(result, old.dimensions(), old.names(), old.attributesRef());
        }
    };


    public static final Impl DOUBLE_TO_COMPLEX = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RDouble)) {
                throw new UnexpectedResultException(null);
            }
            RDouble from = (RDouble) what;
            double[] result = new double[from.size() * 2];
            for (int i = 0; i < result.length >> 1; ++i) {
                result[i << 1] = from.getDouble(i);
                // img[i] is 0
            }
            return RComplex.RComplexFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl DOUBLE_TO_COMPLEX_DIRECT = new Impl() {
        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof DoubleImpl)) {
                throw new UnexpectedResultException(null);
            }
            RDouble old = (RDouble) what;
            double[] from = ((DoubleImpl) what).getContent();
            double[] result = new double[from.length * 2];
            for (int i = 0; i < from.length; ++i) {
                result[i << 1] = from[i];
            }
            return RComplex.RComplexFactory.getFor(result, old.dimensions(), old.names(), old.attributesRef());
        }
    };

    public static final Impl COMPLEX_TO_COMPLEX = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RComplex)) {
                throw new UnexpectedResultException(null);
            }
            RComplex from = (RComplex) what;
            double[] result = new double[from.size() * 2];
            for (int i = 0; i < result.length >> 1; ++i) {
                result[i << 1 ] = from.getReal(i);
                result[(i << 1) + 1] = from.getImag(i);
            }
            return RComplex.RComplexFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl COMPLEX_TO_COMPLEX_DIRECT = new Impl() {
        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof ComplexImpl)) {
                throw new UnexpectedResultException(null);
            }
            RComplex old = (RComplex) what;
            double[] from = ((ComplexImpl) what).getContent();
            double[] result = new double[from.length];
            System.arraycopy(from, 0, result, 0, result.length);
            return RComplex.RComplexFactory.getFor(result, old.dimensions(), old.names(), old.attributesRef());
        }
    };

    public static final Impl LOGICAL_TO_STRING = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RLogical)) {
                throw new UnexpectedResultException(null);
            }
            RLogical from = (RLogical) what;
            String[] result = new String[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.logical2string(from.getLogical(i));
            }
            return RString.RStringFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl INT_TO_STRING = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RInt)) {
                throw new UnexpectedResultException(null);
            }
            RInt from = (RInt) what;
            String[] result = new String[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.int2string(from.getInt(i));
            }
            return RString.RStringFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl DOUBLE_TO_STRING = new Impl() {

        @Override
       public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RDouble)) {
                throw new UnexpectedResultException(null);
            }
            RDouble from = (RDouble) what;
            String[] result = new String[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.double2string(from.getDouble(i));
            }
            return RString.RStringFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl COMPLEX_TO_STRING = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RComplex)) {
                throw new UnexpectedResultException(null);
            }
            RComplex from = (RComplex) what;
            String[] result = new String[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Convert.complex2string(from.getReal(i), from.getImag(i));
            }
            return RString.RStringFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    public static final Impl STRING_TO_STRING = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RString)) {
                throw new UnexpectedResultException(null);
            }
            RString from = (RString) what;
            String[] result = new String[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = from.getString(i);
            }
            return RString.RStringFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

    // TODO what to do with raw ??
    public static final Impl RAW_TO_RAW = new Impl() {

        @Override
        public final RAny copy(RAny what) throws UnexpectedResultException {
            if (!(what instanceof RRaw)) {
                throw new UnexpectedResultException(null);
            }
            RRaw from = (RRaw) what;
            byte[] result = new byte[from.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = from.getRaw(i);
            }
            return RRaw.RRawFactory.getFor(result, from.dimensions(), from.names(), from.attributesRef());
        }
    };

}
