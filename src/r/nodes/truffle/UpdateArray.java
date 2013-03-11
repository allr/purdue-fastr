package r.nodes.truffle;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.Selector.SelectorNode;

/** Array update AST and its specializations. */
public class UpdateArray extends UpdateArrayAssignment.AssignmentNode {

    static final boolean DEBUG_UP = false;

    /** determines whether [] or [[]] operators were used (subset == []). */
    final boolean subset;
    final boolean column;

    /** Selector nodes for respective dimensions. These are likely to be rewritten. */
    @Children SelectorNode[] selectorExprs;

    final Selector[] selectorVals;
    final int[] selSizes;
    final int[] offsets;

    /** Returns the array update node, or if the peephole chain optimizations are enabled returns the update node
     * prefixed with the optimizer node.
     *
     * // TODO peepholer is currently not used as all optimizations are visible from the first execution.
     */
    public static UpdateArrayAssignment.AssignmentNode create(ASTNode ast, SelectorNode[] selectorExprs, boolean subset, boolean column) {
        return new UpdateArray(ast, selectorExprs, subset, column);
    }


    /** Constructor from scratch. Use the static method create so that the peephole chain optimizer can be injected to
     * the update tree if required. */
    protected UpdateArray(ASTNode ast, SelectorNode[] selectorExprs, boolean subset, boolean column) {
        super(ast);
        this.subset = subset;
        this.column = column;
        this.selectorExprs = adoptChildren(selectorExprs);
        selectorVals = new Selector[selectorExprs.length];

        selSizes = new int[selectorExprs.length];
        offsets = new int[selectorExprs.length + 1];
    }

    /** Copy constructor used in node replacements. */
    protected UpdateArray(UpdateArray other) {
        this(other.ast, other.selectorExprs, other.subset, other.column);
    }

    /**
     * Returns true if the given type (from) is implicitly convertible to the other type. So for
     * example logical type is always convertible and string is only convertible to string itself.
     */
    protected static boolean isConvertible(RAny from, RAny to) {
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
     *
     * If the direct optimizations are enabled they are tested and the respective nodes are created.
     * <p/>
     * At the moment, this is very simple calculation: we do not copy the left hand side only if it
     * is not shared, and if the rhs is a scalar of the same type as the lhs, or of an
     * easily convertible type.
     * <p/>
     * In all other cases the copy node is first injected to the code.
     */
    @Override
    public RAny execute(Frame frame, RAny lhs, RAny rhs) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            if (Configuration.ARRAY_UPDATE_DIRECT_SPECIALIZATIONS && subset && !column) {
                if (!lhs.isShared()) {
                    if ((lhs instanceof IntImpl) && (rhs instanceof IntImpl)) {
                        return replace(new IntToIntDirect(this)).execute(frame, lhs, rhs);
                    } else if (lhs instanceof DoubleImpl) {
                        if (rhs instanceof IntImpl) {
                            return replace(new IntToDoubleDirect(this)).execute(frame, lhs, rhs);
                        } else if (rhs instanceof DoubleImpl) {
                            return replace(new DoubleToDoubleDirect(this)).execute(frame, lhs, rhs);
                        }
                    } else if (lhs instanceof ComplexImpl) {
                        if (rhs instanceof IntImpl) {
                            return replace(new IntToComplexDirect(this)).execute(frame, lhs, rhs);
                        } else if (rhs instanceof DoubleImpl) {
                            return replace(new DoubleToComplexDirect(this)).execute(frame, lhs, rhs);
                        } else if (rhs instanceof ComplexImpl) {
                            return replace(new ComplexToComplexDirect(this)).execute(frame, lhs, rhs);
                        }
                    }
                }
            }
            if (!lhs.isShared()
                    && isConvertible(rhs, lhs)
                    && rhs instanceof RArray
                    && ((RArray) rhs).size() == 1) {
                if (DEBUG_UP) Utils.debug("UpdateArray -> RHSCompatible (no need of LHS copy)");
                return replace(new RHSCompatible(this)).execute(frame, lhs, rhs);
            }
            if (DEBUG_UP) Utils.debug("UpdateArray -> CopyLHS");
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
        // which is never shared
        try {
            if (lhs.isShared()) {
                throw new UnexpectedResultException(null);
            }
            for (int i = 0; i < selectorVals.length; ++i) {
                selectorVals[i] = selectorExprs[i].executeSelector(frame);
            }
            while (true) {
                try {
                    return update(lhs, rhs);
                } catch (UnexpectedResultException e) {
                    Selector failedSelector = (Selector) e.getResult();
                    for (int i = 0; i < selectorVals.length; ++i) {
                        if (selectorVals[i] == failedSelector) {
                            RAny index = failedSelector.getIndex();
                            SelectorNode newSelector = Selector.createSelectorNode(ast, subset, index, selectorExprs[i], false, failedSelector.getTransition());
                            replaceChild(selectorExprs[i], newSelector);
                            assert (selectorExprs[i] == newSelector);
                            selectorVals[i] = newSelector.executeSelector(index);
                            if (DEBUG_UP) Utils.debug("Selector " + i + " changed...");
                        }
                    }
                }
            }
        } catch (UnexpectedResultException e) {
            if (DEBUG_UP) Utils.debug(getClass().getSimpleName() + " -> Generalized");
            return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
        }
    }

    /**
     * A basic in-place update of the selection.
     * <p/>
     * Updates the lhs array with the rhs array information using given selectors. Override this
     * method for different array manipulation.
     */

    protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
        int[] lhsDim = lhs.dimensions();
        checkDimensions(lhsDim, selectorExprs.length, ast);
        boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
        int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
        int replacementSize = rhs.size();
        checkReplacementSize(itemsToReplace, replacementSize, subset, ast);

        if (itemsToReplace > 0) {
            int rhsOffset = 0;
            for (;;) {
                int lhsOffset = offsets[0];
                if (lhsOffset != RInt.NA) {
                    lhs.set(lhsOffset, rhs.getRef(rhsOffset));
                }
                rhsOffset++;
                if (rhsOffset < replacementSize) {
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, lhsDim, selectorVals, ast);
                    }
                } else {
                    itemsToReplace -= replacementSize;
                    if (itemsToReplace == 0) {
                        break;
                    }
                    rhsOffset = 0;
                    if (!mayHaveNA) {
                        Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                    } else {
                        Selector.restart(offsets, selectorVals, lhsDim, ast);
                    }
                }
            }
        }
        return lhs;
    }

    // =================================================================================================================
    // Non-shared
    // =================================================================================================================

    /**
     * Node which assumes that the LHS is not shared - more precisely that it either does not need
     * to be copied, or has already been copied and it sees the copy. If the lhs and rhs types are
     * the same, the node rewrites itself to the next step which is IdenticalTypes node, otherwise
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
     * If the rhs is a scalar, uses the Scalar version of the update, otherwise uses the
     * NonScalar version.
     */
    protected static class IdenticalTypes extends UpdateArray {

        public IdenticalTypes(UpdateArray other) {
            super(other);
        }

        /** Rewrites itself to either Scalar updater, or to the more generic NonScalar updater. */
        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (!subset) {
                    if (DEBUG_UP) Utils.debug("IdenticalTypes -> Subscript");
                    return replace(Subscript.create(this, lhs, rhs)).execute(frame, lhs, rhs);
                }
                if (column) {
                    if (DEBUG_UP) Utils.debug("IdenticalTypes -> Column");
                    return replace(Column.create(this, lhs, rhs)).execute(frame, lhs, rhs);
                }
                if (rhs instanceof RArray && ((RArray) rhs).size() == 1) {
                    if (DEBUG_UP) Utils.debug("IdenticalTypes -> Scalar");
                    return replace(new Scalar(this)).execute(frame, lhs, rhs);
                }
                if (DEBUG_UP) Utils.debug("IdenticalTypes -> NonScalar");
                return replace(new NonScalar(this)).execute(frame, lhs, rhs);
            }
        }
    }

    // =================================================================================================================
    // Generic
    // =================================================================================================================

    /**
     * The generalized fall-back node for array update.
     * <p/>
     * Whenever the assumptions of specialized nodes in the update array node tree fail, the whole
     * tree is rewritten to this node, which does all:
     * <p/>
     * - makes copy of the LHS if required - makes copy of the RHS if required - runs the
     * generalized update method with selectors and non-scalar rhs vector (this will work
     * for scalars too, just not with the greatest speeds)
     * <p/>
     * In general, this node is used whenever the type information on lhs and rhs side of the update
     * changes at runtime.
     * <p/>
     * TODO Maybe by making the general case less aggressive and allowing for instance to recompute
     * the copy LHS and copy RHS arguments better results can be achieved.
     */
    protected static class GenericSubset extends Generic {

        static enum UpdateType {
            GENERALIZED,
            INT_TO_INT_DIRECT,
            INT_TO_DOUBLE_DIRECT,
            DOUBLE_TO_DOUBLE_DIRECT,
            INT_TO_COMPLEX_DIRECT,
            DOUBLE_TO_COMPLEX_DIRECT,
            COMPLEX_TO_COMPLEX_DIRECT,
        }

        UpdateType updateType;

        public GenericSubset(UpdateArray other) {
            super(other);
            updateType = UpdateType.GENERALIZED;
        }

        /**
         * This is the general case that performs all the computations at once. In the slowpath
         * makes a copy of the lhs and determines if a copy of the rhs should be made and then runs
         * the update of arrays for non-const rhs values (this will work for the const values too,
         * of course).
         */
        @Override
        public RAny execute(Frame frame, RAny lhsParam, RAny rhsParam) {
            RAny lhs = lhsParam;
            RAny rhs = rhsParam;
            if (Configuration.ARRAY_UPDATE_DIRECT_SPECIALIZATIONS_IN_GENERALIZED && !lhs.isShared()) {
                if (Configuration.ARRAY_UPDATE_DIRECT_SPECIALIZATIONS_IN_GENERALIZED_CACHE) {
                    // check if we have a reason to believe that we are specialized, and if so, just use the simple checks to
                    // confirm and proceed
                    switch (updateType) {
                        case INT_TO_INT_DIRECT:
                            if ((lhs != rhs) && lhs instanceof IntImpl && rhs instanceof IntImpl) {
                                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                            }
                            break;
                        case INT_TO_DOUBLE_DIRECT:
                            if ((lhs != rhs) && lhs instanceof DoubleImpl && rhs instanceof IntImpl) {
                                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                            }
                            break;
                        case DOUBLE_TO_DOUBLE_DIRECT:
                            if ((lhs != rhs) && lhs instanceof DoubleImpl && rhs instanceof DoubleImpl) {
                                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                            }
                            break;
                        case INT_TO_COMPLEX_DIRECT:
                            if ((lhs != rhs) && lhs instanceof ComplexImpl && rhs instanceof IntImpl) {
                                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                            }
                            break;
                        case DOUBLE_TO_COMPLEX_DIRECT:
                            if ((lhs != rhs) && lhs instanceof ComplexImpl && rhs instanceof DoubleImpl) {
                                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                            }
                            break;
                        case COMPLEX_TO_COMPLEX_DIRECT:
                            if ((lhs != rhs) && lhs instanceof ComplexImpl && rhs instanceof ComplexImpl) {
                                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                            }
                            break;
                    }
                }
                updateType = UpdateType.GENERALIZED;
                // if the cached specialized version does not apply, try if another specialized version can be used and
                // use it
                if (lhs != rhs) {
                    if (lhs instanceof IntImpl && rhs instanceof IntImpl) {
                        updateType = UpdateType.INT_TO_INT_DIRECT;
                    } else if (lhs instanceof DoubleImpl) {
                        if (rhs instanceof IntImpl) {
                            updateType = UpdateType.INT_TO_DOUBLE_DIRECT;
                        } else if (rhs instanceof DoubleImpl) {
                            updateType = UpdateType.DOUBLE_TO_DOUBLE_DIRECT;
                        }
                    } else if (lhs instanceof ComplexImpl) {
                        if (rhs instanceof IntImpl) {
                            updateType = UpdateType.INT_TO_COMPLEX_DIRECT;
                        } else if (rhs instanceof DoubleImpl) {
                            updateType = UpdateType.DOUBLE_TO_COMPLEX_DIRECT;
                        } else if (rhs instanceof ComplexImpl) {
                            updateType = UpdateType.COMPLEX_TO_COMPLEX_DIRECT;
                        }
                    }
                    if (updateType != UpdateType.GENERALIZED) {
                        return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
                    }
                }
            }
            // none of the specializations is applicable, proceed with the very general case
            return super.execute(frame, lhs, rhs);
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            if (Configuration.ARRAY_UPDATE_DIRECT_SPECIALIZATIONS_IN_GENERALIZED) {
                switch (updateType) {
                    case INT_TO_INT_DIRECT:
                        return IntToIntDirect.doUpdate(this, lhs, rhs);
                    case INT_TO_DOUBLE_DIRECT:
                        return IntToDoubleDirect.doUpdate(this, lhs, rhs);
                    case DOUBLE_TO_DOUBLE_DIRECT:
                        return DoubleToDoubleDirect.doUpdate(this, lhs, rhs);
                    case INT_TO_COMPLEX_DIRECT:
                        return IntToComplexDirect.doUpdate(this, lhs, rhs);
                    case DOUBLE_TO_COMPLEX_DIRECT:
                        return DoubleToComplexDirect.doUpdate(this, lhs, rhs);
                    case COMPLEX_TO_COMPLEX_DIRECT:
                        return ComplexToComplexDirect.doUpdate(this, lhs, rhs);
                    default:
                        return super.update(lhs, rhs);
                }
            } else {
                return super.update(lhs, rhs);
            }
        }
    }

    protected static class GenericSubscript extends Generic {

        public GenericSubscript(UpdateArray other) {
            super(other);
            assert Utils.check(!other.subset);
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return Subscript.doUpdate(lhs, rhs, selectorVals, ast);
        }
    }

    protected abstract static class Generic extends UpdateArray {

        public Generic(UpdateArray other) {
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
        public static Generic replaceArrayUpdateTree(UpdateArray tree) {
            Node root = tree;
            if (root.getParent() instanceof CopyRhs) {
                if (DEBUG_UP) Utils.debug("Replacing update tree - skipping copy rhs node");
                root = root.getParent();
            }
            if (root.getParent() instanceof CopyLhs) {
                if (DEBUG_UP) Utils.debug("Replacing update tree - skipping copy lhs node");
                root = root.getParent();
            }
            if (tree.subset) {
                return root.replace(new GenericSubset(tree));
            } else {
                return root.replace(new GenericSubscript(tree));
            }
        }

        /**
         * This is the general case that performs all the computations at once. In the slowpath
         * makes a copy of the lhs and determines if a copy of the rhs should be made and then runs
         * the update of arrays for non-const rhs values (this will work for the const values too,
         * of course).
         */
        @Override
        public RAny execute(Frame frame, RAny lhsParam, RAny rhsParam) {
            RAny lhs = lhsParam;
            RAny rhs = rhsParam;

            // 1. copy the rhs
            ValueCopy.Impl rhsImpl = CopyRhs.determineCopyImplementation(lhs, rhs); // can be null if no copy is needed
            if (rhsImpl != null) {
                try {
                    rhs = rhsImpl.copy(rhs);
                } catch (UnexpectedResultException e) {
                    assert (false) : "unreachable";
                }
            }

            // now the type of lhs <= type of rhs
            ValueCopy.Impl lhsImpl = CopyLhs.determineCopyImplementation(lhs, rhs); // will not be null, will be an upcast or a duplicate
            if (!(lhsImpl instanceof ValueCopy.Duplicate) || lhs.isShared() || rhs.dependsOn(lhs)) {
                try {
                    lhs = lhsImpl.copy(lhs);
                } catch (UnexpectedResultException e) {
                    assert (false) : "unreachable";
                }
            }
            // now the type of lhs == the type of rhs

            // TODO However because not everything is implemented as of now, I am keeping the checks.
            assert Utils.check((lhs instanceof RInt && rhs instanceof RInt) || (lhs instanceof RDouble && rhs instanceof RDouble) || (lhs instanceof RLogical && rhs instanceof RLogical) ||
                            (lhs instanceof RComplex && rhs instanceof RComplex) || (lhs instanceof RString && rhs instanceof RString) || (lhs instanceof RRaw && rhs instanceof RRaw),
                            "Unable to perform the update of the array - unimplemented copy");
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
                ValueCopy.Impl copy =  determineCopyImplementation(lhs, rhs);
                if (copy instanceof ValueCopy.Duplicate) {
                    return replace(new SpecializedDuplicate(this, (ValueCopy.Duplicate) copy)).execute(frame, lhs, rhs);
                } else {
                    return replace(new Specialized(this, copy)).execute(frame, lhs, rhs);
                }
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
            public RAny execute(Frame frame, RAny lhsParam, RAny rhs) {
                RAny lhs = lhsParam;
                try {
                    lhs = impl.copy(lhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("CopyLhs.Specialized -> Generalized");
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
                return child.execute(frame, lhs, rhs);
            }
        }

        protected static class SpecializedDuplicate extends CopyLhs {

            final ValueCopy.Duplicate impl;

            public SpecializedDuplicate(CopyLhs other, ValueCopy.Duplicate copy) {
                super(other);
                assert Utils.check(!(child instanceof CopyRhs));
                // in the current code, the child won't be copying the rhs
                // if that changes, the execute method below should be modified not to pay attention to whether the _old_ rhs
                // does depend on the lhs, because it will be copied by the child node

                this.impl = copy;
            }

            @Override
            public RAny execute(Frame frame, RAny lhsParam, RAny rhs) {
                RAny lhs;
                if (lhsParam.isShared() || rhs.dependsOn(lhsParam)) {
                    try {
                        lhs = impl.copy(lhsParam);
                    } catch (UnexpectedResultException e) {
                        if (DEBUG_UP) Utils.debug("CopyLhs.SpecializedDuplicate -> Generalized");
                        return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhsParam, rhs);
                    }
                } else {
                    lhs = lhsParam;
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
                    if (rhsMode == RAny.Mode.LOGICAL) {
                        return ValueCopy.LOGICAL_TO_INT;
                    }
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
                    return UpdateArray.GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
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
            public RAny execute(Frame frame, RAny lhs, RAny rhsParam) {
                RAny rhs = rhsParam;
                try {
                    rhs = impl.copy(rhs);
                } catch (UnexpectedResultException e) {
                    if (DEBUG_UP) Utils.debug("CopyRhs.Specialized -> Generalized");
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
                return child.execute(frame, lhs, rhs);
            }
        }

    }

    // =================================================================================================================
    // Subscript
    // =================================================================================================================

    public abstract static class TypeGuard {
        abstract void check(RAny lhs, RAny rhs) throws UnexpectedResultException;

        public static TypeGuard create(RAny leftTemplate, RAny rightTemplate) {
            if (leftTemplate instanceof RString && rightTemplate instanceof RString) {
                return new TypeGuard() {
                    @Override
                    void check(RAny lhs, RAny rhs) throws UnexpectedResultException {
                        if (!(lhs instanceof RString && rhs instanceof RString)) {
                            throw new UnexpectedResultException(null);
                        }
                    }
                };
            }
            if (leftTemplate instanceof RComplex && rightTemplate instanceof RComplex) {
                return new TypeGuard() {
                    @Override
                    void check(RAny lhs, RAny rhs) throws UnexpectedResultException {
                        if (!(lhs instanceof RComplex && rhs instanceof RComplex)) {
                            throw new UnexpectedResultException(null);
                        }
                    }
                };
            }
            if (leftTemplate instanceof RDouble && rightTemplate instanceof RDouble) {
                return new TypeGuard() {
                    @Override
                    void check(RAny lhs, RAny rhs) throws UnexpectedResultException {
                        if (!(lhs instanceof RDouble && rhs instanceof RDouble)) {
                            throw new UnexpectedResultException(null);
                        }
                    }
                };
            }
            if (leftTemplate instanceof RInt && rightTemplate instanceof RInt) {
                return new TypeGuard() {
                    @Override
                    void check(RAny lhs, RAny rhs) throws UnexpectedResultException {
                        if (!(lhs instanceof RInt && rhs instanceof RInt)) {
                            throw new UnexpectedResultException(null);
                        }
                    }
                };
            }
            if (leftTemplate instanceof RLogical && rightTemplate instanceof RInt) {
                return new TypeGuard() {
                    @Override
                    void check(RAny lhs, RAny rhs) throws UnexpectedResultException {
                        if (!(lhs instanceof RLogical && rhs instanceof RLogical)) {
                            throw new UnexpectedResultException(null);
                        }
                    }
                };
            }
            if (leftTemplate instanceof RRaw && rightTemplate instanceof RRaw) {
                return new TypeGuard() {
                    @Override
                    void check(RAny lhs, RAny rhs) throws UnexpectedResultException {
                        if (!(lhs instanceof RRaw && rhs instanceof RRaw)) {
                            throw new UnexpectedResultException(null);
                        }
                    }
                };
            }
            Utils.nyi("left " + leftTemplate + " right " + rightTemplate);
            return null;
        }
    }

    protected static final class Subscript extends UpdateArray {

        final TypeGuard guard;

        public Subscript(UpdateArray other, TypeGuard guard) {
            super(other);
            assert Utils.check(!other.subset);
            this.guard = guard;
        }

        public static Subscript create(UpdateArray other, RAny leftTemplate, RAny rightTemplate) {
            TypeGuard g = TypeGuard.create(leftTemplate, rightTemplate);
            return new Subscript(other, g);
        }

        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                guard.check(lhs, rhs);
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                if (DEBUG_UP) Utils.debug("Subscript -> Generalized");
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
            }
        }

        public static RArray doUpdate(RArray lhs, RArray rhs, Selector[] selectorVals, ASTNode ast) throws UnexpectedResultException {
            int[] dim = lhs.dimensions();
            checkDimensions(dim, selectorVals.length, ast);
            int mult = 1;
            int offset = 0;
            checkReplacementSize(lhs.size(), rhs.size(), false, ast);
            for (int i = 0; i < selectorVals.length; ++i) {
                Selector s = selectorVals[i];
                s.start(dim[i], ast); // it is ensured by subscript selectors that itemsToReplace is 1
                int k = s.nextIndex(ast);
                assert Utils.check(k != RInt.NA); // ensured by subscript selectors
                offset += k * mult;
                mult *= dim[i];
            }
            return lhs.set(offset, rhs.get(0));
        }

        @Override
        public RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(lhs, rhs, selectorVals, ast);
        }
    }

    protected static final class Column extends UpdateArray {

        final TypeGuard guard;

        public Column(UpdateArray other, TypeGuard guard) {
            super(other);
            assert Utils.check(other.subset);
            this.guard = guard;
        }

        public static Column create(UpdateArray other, RAny leftTemplate, RAny rightTemplate) {
            TypeGuard g = TypeGuard.create(leftTemplate, rightTemplate);
            return new Column(other, g);
        }

        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                guard.check(lhs, rhs);
                if (lhs.isShared()) {
                    throw new UnexpectedResultException(null);
                }
                int lastSel = selectorVals.length - 1;
                Selector columnSel = selectorExprs[lastSel].executeSelector(frame);
                while (true) {
                    try {
                        return update((RArray) lhs, (RArray) rhs, columnSel);
                    } catch (UnexpectedResultException e) {
                        RAny index = columnSel.getIndex();
                        SelectorNode newSelector = Selector.createSelectorNode(ast, subset, index, selectorExprs[lastSel], true, columnSel.getTransition());
                        replaceChild(selectorExprs[lastSel], newSelector);
                        columnSel = newSelector.executeSelector(index);
                        if (DEBUG_UP) Utils.debug("Column selector changed...");
                    }
                }
            } catch (UnexpectedResultException e) {
                if (DEBUG_UP) Utils.debug("Column -> Generalized");
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
            }
        }

        public static RArray doUpdate(RArray lhs, RArray rhs, int nSelectors, Selector columnSelector, ASTNode ast) throws UnexpectedResultException {
            int[] dim = lhs.dimensions();
            checkDimensions(dim, nSelectors, ast);
            int n = dim[nSelectors - 1];
            int m = 1; // size of one column
            for (int i = 0; i < nSelectors - 1; i++) {
                m *= dim[i];
            }
            columnSelector.start(n, ast);
            int ncolumns = columnSelector.size();
            int replacementSize = rhs.size();
            int itemsToReplace = ncolumns * m;
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (int j = 0; j < ncolumns; j++) {
                    int col = columnSelector.nextIndex(ast);
                    if (col != RInt.NA) {
                        int lhsOffset = col * m;
                        for (int i = 0; i < m; i++) {
                            lhs.set(lhsOffset + i, rhs.getRef(rhsOffset++));
                            if (rhsOffset == replacementSize) {
                                rhsOffset = 0;
                            }
                        }
                    }
                }
            }
            return lhs;
        }

        public RArray update(RArray lhs, RArray rhs, Selector columnSelector) throws UnexpectedResultException {
            return doUpdate(lhs, rhs, selectorExprs.length, columnSelector, ast);
        }

        @Override
        public RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            Utils.nyi("unreachable");
            return null;
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
                int[] lhsDim = lhs.dimensions();
                checkDimensions(lhsDim, selectorExprs.length, ast);
                boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
                int replacementSize = Selector.calculateSizeFromSelectorSizes(selSizes);
                if (replacementSize == 0) {
                    return lhs;
                }
                int[] lhsVal = ((LogicalImpl) lhs).getContent();
                int rhsVal = ((RLogical) rhs).getLogical(0);

                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    replacementSize--;
                    if (replacementSize == 0) {
                        return lhs;
                    }
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, lhsDim, selectorVals, ast);
                    }
                }
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
                int[] lhsDim = lhs.dimensions();
                checkDimensions(lhsDim, selectorExprs.length, ast);
                boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
                int replacementSize = Selector.calculateSizeFromSelectorSizes(selSizes);
                if (replacementSize == 0) {
                    return lhs;
                }
                int[] lhsVal = ((IntImpl) lhs).getContent();
                int rhsVal = ((RInt) rhs).getInt(0);

                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    replacementSize--;
                    if (replacementSize == 0) {
                        return lhs;
                    }
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, lhsDim, selectorVals, ast);
                    }
                }
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
                int[] lhsDim = lhs.dimensions();
                checkDimensions(lhsDim, selectorExprs.length, ast);
                boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
                int replacementSize = Selector.calculateSizeFromSelectorSizes(selSizes);
                if (replacementSize == 0) {
                    return lhs;
                }
                double[] lhsVal = ((DoubleImpl) lhs).getContent();
                double rhsVal = ((RDouble) rhs).getDouble(0);

                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    replacementSize--;
                    if (replacementSize == 0) {
                        return lhs;
                    }
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, lhsDim, selectorVals, ast);
                    }
                }
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
                int[] lhsDim = lhs.dimensions();
                checkDimensions(lhsDim, selectorExprs.length, ast);
                boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
                int replacementSize = Selector.calculateSizeFromSelectorSizes(selSizes);
                if (replacementSize == 0) {
                    return lhs;
                }
                double[] lhsVal = ((ComplexImpl) lhs).getContent();
                double re = ((RComplex) rhs).getReal(0);
                double im = ((RComplex) rhs).getImag(0);

                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset * 2] = re;
                        lhsVal[lhsOffset * 2 + 1] = im;
                    }
                    replacementSize--;
                    if (replacementSize == 0) {
                        return lhs;
                    }
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, lhsDim, selectorVals, ast);
                    }
                }
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }

            @Override
            protected final RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
                int[] lhsDim = lhs.dimensions();
                checkDimensions(lhsDim, selectorExprs.length, ast);
                boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
                int replacementSize = Selector.calculateSizeFromSelectorSizes(selSizes);
                if (replacementSize == 0) {
                    return lhs;
                }
                java.lang.String[] lhsVal = ((StringImpl) lhs).getContent();
                java.lang.String rhsVal = ((RString) rhs).getString(0);

                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal;
                    }
                    replacementSize--;
                    if (replacementSize == 0) {
                        return lhs;
                    }
                    if (!mayHaveNA) {
                        Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                    } else {
                        Selector.advance(offsets, lhsDim, selectorVals, ast);
                    }
                }
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
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
                    return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
                }
            }
        }
    }

    private static void checkDimensions(int[] dim, int nSelectors, ASTNode ast) {
        if (dim == null || dim.length != nSelectors) {
            if (nSelectors == 2) {
                throw RError.getIncorrectSubscriptsMatrix(ast);
            } else {
                throw RError.getIncorrectSubscripts(ast);
            }
        }
    }

    private static void checkReplacementSize(int itemsToReplace, int replacementSize, boolean subset, ASTNode ast) {
        if (itemsToReplace != replacementSize && replacementSize != 1) {
            // TODO: add these checks to all other updates - it is necessary to do before the update whenever the update is potentially running in-place,
            // because we must not modify the matrix even in case of error
            if (replacementSize == 0) {
                throw RError.getReplacementZero(ast);
            }
            if (itemsToReplace % replacementSize != 0) {
                if (subset) {
                    throw RError.getNotMultipleReplacement(ast);
                } else {
                    throw RError.getMoreElementsSupplied(ast);
                }
            }
        }
    }

    // =================================================================================================================
    // Direct specializations
    // =================================================================================================================

    /** Integer update to integer direct specialization.
     *
     * Only checks that direct access can be obtained to both lhs and to rhs, then copies the lhs if the lhs and rhs may
     * alias.
     *
     * TODO the non lhs copying node may be rewritten to a special one for truffle
     *
     * TODO I believe the lhs == rhs check for aliasing is useless for us - if we are in direct access, then we can
     * never alias and if we do alias we have the meaning less statement a[,,] = a
     */
    protected static class IntToIntDirect extends UpdateArray {


        protected IntToIntDirect(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                if (!(lhs instanceof IntImpl) || !(rhs instanceof IntImpl) || (lhs.isShared())) {
                    throw new UnexpectedResultException(null);
                }
                if (!Configuration.ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS || (lhs == rhs)) {
                    lhs = ValueCopy.INT_TO_INT_DIRECT.copy(lhs);
                }
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
            }
        }

        /** Static method so that the update can be called also from other UpdateArray nodes, notably the Generalized
         * one.
         */
        protected static RArray doUpdate(UpdateArray node, RArray lhs, RArray rhs) throws UnexpectedResultException {
            int[] offsets = node.offsets;
            Selector[] selectorVals = node.selectorVals;
            int[] selSizes = node.selSizes;
            ASTNode ast = node.ast;
            assert Utils.check(node.subset);

            int[] lhsDim = lhs.dimensions();
            checkDimensions(lhsDim, selectorVals.length, ast);
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
            int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
            int replacementSize = rhs.size();
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            int[] lhsVal = ((IntImpl) lhs).getContent();
            int[] rhsVal = ((IntImpl) rhs).getContent();

            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal[rhsOffset];
                    }
                    rhsOffset++;
                    if (rhsOffset < replacementSize) {
                        if (!mayHaveNA) {
                            Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                        } else {
                            Selector.advance(offsets, lhsDim, selectorVals, ast);
                        }
                    } else {
                        itemsToReplace -= replacementSize;
                        if (itemsToReplace == 0) {
                            break;
                        }
                        rhsOffset = 0;
                        if (!mayHaveNA) {
                            Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                        } else {
                            Selector.restart(offsets, selectorVals, lhsDim, ast);
                        }
                    }
                }
            }
            return lhs;
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(this, lhs, rhs);
        }
    }

    /** Integer update to double direct specialization.
     *
     * Only checks that direct access can be obtained to both lhs and to rhs, then copies the lhs if the lhs and rhs may
     * alias.
     *
     * TODO the non lhs copying node may be rewritten to a special one for truffle
     *
     * TODO I believe lhs and rhs can never alias here in our implementation
     */
    protected static class IntToDoubleDirect extends UpdateArray {


        protected IntToDoubleDirect(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhsParam, RAny rhs) {
            try {
                RAny lhs = lhsParam;
                if (!(lhs instanceof DoubleImpl) || !(rhs instanceof IntImpl) || (lhs.isShared())) {
                    throw new UnexpectedResultException(null);
                }
                if (!Configuration.ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS) {
                    lhs = ValueCopy.DOUBLE_TO_DOUBLE_DIRECT.copy(lhs);
                }
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhsParam, rhs);
            }
        }

        /** Static method so that the update can be called also from other UpdateArray nodes, notably the Generalized
         * one.
         */
        protected static RArray doUpdate(UpdateArray node, RArray lhs, RArray rhs) throws UnexpectedResultException {
            int[] offsets = node.offsets;
            Selector[] selectorVals = node.selectorVals;
            int[] selSizes = node.selSizes;
            ASTNode ast = node.ast;
            assert Utils.check(node.subset);

            int[] lhsDim = lhs.dimensions();
            checkDimensions(lhsDim, selectorVals.length, ast);
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
            int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
            int replacementSize = rhs.size();
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            double[] lhsVal = ((DoubleImpl) lhs).getContent();
            int[] rhsVal = ((IntImpl) rhs).getContent();

            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal[rhsOffset];
                    }
                    rhsOffset++;
                    if (rhsOffset < replacementSize) {
                        if (!mayHaveNA) {
                            Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                        } else {
                            Selector.advance(offsets, lhsDim, selectorVals, ast);
                        }
                    } else {
                        itemsToReplace -= replacementSize;
                        if (itemsToReplace == 0) {
                            break;
                        }
                        rhsOffset = 0;
                        if (!mayHaveNA) {
                            Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                        } else {
                            Selector.restart(offsets, selectorVals, lhsDim, ast);
                        }
                    }
                }
            }
            return lhs;
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(this, lhs, rhs);
        }
    }

    /** Double update to double direct specialization.
     *
     * Only checks that direct access can be obtained to both lhs and to rhs, then copies the lhs if the lhs and rhs may
     * alias.
     *
     * TODO the non lhs copying node may be rewritten to a special one for truffle
     *
     * TODO I believe the lhs == rhs check for aliasing is useless for us - if we are in direct access, then we can
     * never alias and if we do alias we have the meaning less statement a[,,] = a
     */
    protected static class DoubleToDoubleDirect extends UpdateArray {


        protected DoubleToDoubleDirect(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhsParam, RAny rhs) {
            try {
                RAny lhs = lhsParam;
                if (!(lhs instanceof DoubleImpl) || !(rhs instanceof DoubleImpl) || (lhs.isShared())) {
                    throw new UnexpectedResultException(null);
                }
                if (!Configuration.ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS || (lhs == rhs)) {
                    lhs = ValueCopy.DOUBLE_TO_DOUBLE_DIRECT.copy(lhs);
                }
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhsParam, rhs);
            }
        }

        /** Static method so that the update can be called also from other UpdateArray nodes, notably the Generalized
         * one.
         */
        protected static RArray doUpdate(UpdateArray node, RArray lhs, RArray rhs) throws UnexpectedResultException {
            int[] offsets = node.offsets;
            Selector[] selectorVals = node.selectorVals;
            int[] selSizes = node.selSizes;
            ASTNode ast = node.ast;
            assert Utils.check(node.subset);

            int[] lhsDim = lhs.dimensions();
            checkDimensions(lhsDim, selectorVals.length, ast);
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
            int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
            int replacementSize = rhs.size();
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            double[] lhsVal = ((DoubleImpl) lhs).getContent();
            double[] rhsVal = ((DoubleImpl) rhs).getContent();

            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[lhsOffset] = rhsVal[rhsOffset];
                    }
                    rhsOffset++;
                    if (rhsOffset < replacementSize) {
                        if (!mayHaveNA) {
                            Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                        } else {
                            Selector.advance(offsets, lhsDim, selectorVals, ast);
                        }
                    } else {
                        itemsToReplace -= replacementSize;
                        if (itemsToReplace == 0) {
                            break;
                        }
                        rhsOffset = 0;
                        if (!mayHaveNA) {
                            Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                        } else {
                            Selector.restart(offsets, selectorVals, lhsDim, ast);
                        }
                    }
                }
            }
            return lhs;
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(this, lhs, rhs);
        }
    }

    /** Integer update to complex direct specialization.
     *
     * Only checks that direct access can be obtained to both lhs and to rhs, then copies the lhs if the lhs and rhs may
     * alias.
     *
     * TODO the non lhs copying node may be rewritten to a special one for truffle
     *
     * TODO I believe lhs and rhs can never alias here in our implementation
     */
    protected static class IntToComplexDirect extends UpdateArray {


        protected IntToComplexDirect(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhs, RAny rhs) {
            try {
                if (!(lhs instanceof ComplexImpl) || !(rhs instanceof IntImpl) || (lhs.isShared())) {
                    throw new UnexpectedResultException(null);
                }
                if (!Configuration.ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS) {
                    lhs = ValueCopy.COMPLEX_TO_COMPLEX_DIRECT.copy(lhs);
                }
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhs, rhs);
            }
        }

        /** Static method so that the update can be called also from other UpdateArray nodes, notably the Generalized
         * one.
         */
        protected static RArray doUpdate(UpdateArray node, RArray lhs, RArray rhs) throws UnexpectedResultException {
            int[] offsets = node.offsets;
            Selector[] selectorVals = node.selectorVals;
            int[] selSizes = node.selSizes;
            ASTNode ast = node.ast;
            assert Utils.check(node.subset);

            int[] lhsDim = lhs.dimensions();
            checkDimensions(lhsDim, selectorVals.length, ast);
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
            int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
            int replacementSize = rhs.size();
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            double[] lhsVal = ((ComplexImpl) lhs).getContent();
            int[] rhsVal = ((IntImpl) rhs).getContent();

            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[2 * lhsOffset] = rhsVal[rhsOffset];
                        lhsVal[2 * lhsOffset + 1] = 0;
                    }
                    rhsOffset++;
                    if (rhsOffset < replacementSize) {
                        if (!mayHaveNA) {
                            Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                        } else {
                            Selector.advance(offsets, lhsDim, selectorVals, ast);
                        }
                    } else {
                        itemsToReplace -= replacementSize;
                        if (itemsToReplace == 0) {
                            break;
                        }
                        rhsOffset = 0;
                        if (!mayHaveNA) {
                            Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                        } else {
                            Selector.restart(offsets, selectorVals, lhsDim, ast);
                        }
                    }
                }
            }
            return lhs;
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(this, lhs, rhs);
        }
    }

    /** Double update to complex direct specialization.
     *
     * Only checks that direct access can be obtained to both lhs and to rhs, then copies the lhs if the lhs and rhs may
     * alias.
     *
     * TODO the non lhs copying node may be rewritten to a special one for truffle
     *
     * TODO I believe lhs and rhs can never alias here in our implementation
     */
    protected static class DoubleToComplexDirect extends UpdateArray {


        protected DoubleToComplexDirect(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhsParam, RAny rhs) {
            try {
                RAny lhs = lhsParam;
                if (!(lhs instanceof ComplexImpl) || !(rhs instanceof DoubleImpl) || (lhs.isShared())) {
                    throw new UnexpectedResultException(null);
                }
                if (!Configuration.ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS) {
                    lhs = ValueCopy.COMPLEX_TO_COMPLEX_DIRECT.copy(lhs);
                }
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhsParam, rhs);
            }
        }

        /** Static method so that the update can be called also from other UpdateArray nodes, notably the Generalized
         * one.
         */
        protected static RArray doUpdate(UpdateArray node, RArray lhs, RArray rhs) throws UnexpectedResultException {
            int[] offsets = node.offsets;
            Selector[] selectorVals = node.selectorVals;
            int[] selSizes = node.selSizes;
            ASTNode ast = node.ast;
            assert Utils.check(node.subset);

            int[] lhsDim = lhs.dimensions();
            checkDimensions(lhsDim, selectorVals.length, ast);
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
            int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
            int replacementSize = rhs.size();
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            double[] lhsVal = ((ComplexImpl) lhs).getContent();
            double[] rhsVal = ((DoubleImpl) rhs).getContent();

            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[2 * lhsOffset] = rhsVal[rhsOffset];
                        lhsVal[2 * lhsOffset + 1] = 0;
                    }
                    rhsOffset++;
                    if (rhsOffset < replacementSize) {
                        if (!mayHaveNA) {
                            Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                        } else {
                            Selector.advance(offsets, lhsDim, selectorVals, ast);
                        }
                    } else {
                        itemsToReplace -= replacementSize;
                        if (itemsToReplace == 0) {
                            break;
                        }
                        rhsOffset = 0;
                        if (!mayHaveNA) {
                            Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                        } else {
                            Selector.restart(offsets, selectorVals, lhsDim, ast);
                        }
                    }
                }
            }
            return lhs;
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(this, lhs, rhs);
        }
    }

    /** Complex update to complex direct specialization.
     *
     * Only checks that direct access can be obtained to both lhs and to rhs, then copies the lhs if the lhs and rhs may
     * alias.
     *
     * TODO the non lhs copying node may be rewritten to a special one for truffle
     *
     * TODO I believe the lhs == rhs check for aliasing is useless for us - if we are in direct access, then we can
     * never alias and if we do alias we have the meaning less statement a[,,] = a
     */
    protected static class ComplexToComplexDirect extends UpdateArray {


        protected ComplexToComplexDirect(UpdateArray other) {
            super(other);
        }

        @Override
        public RAny execute(Frame frame, RAny lhsParam, RAny rhs) {
            try {
                RAny lhs = lhsParam;
                if (!(lhs instanceof ComplexImpl) || !(rhs instanceof ComplexImpl) || (lhs.isShared())) {
                    throw new UnexpectedResultException(null);
                }
                if (!Configuration.ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS || (lhs == rhs)) {
                    lhs = ValueCopy.COMPLEX_TO_COMPLEX_DIRECT.copy(lhs);
                }
                return executeAndUpdateSelectors(frame, (RArray) lhs, (RArray) rhs);
            } catch (UnexpectedResultException e) {
                return GenericSubset.replaceArrayUpdateTree(this).execute(frame, lhsParam, rhs);
            }
        }

        /** Static method so that the update can be called also from other UpdateArray nodes, notably the Generalized
         * one.
         */
        protected static RArray doUpdate(UpdateArray node, RArray lhs, RArray rhs) throws UnexpectedResultException {
            int[] offsets = node.offsets;
            Selector[] selectorVals = node.selectorVals;
            int[] selSizes = node.selSizes;
            ASTNode ast = node.ast;
            assert Utils.check(node.subset);

            int[] lhsDim = lhs.dimensions();
            checkDimensions(lhsDim, selectorVals.length, ast);
            boolean mayHaveNA = Selector.initialize(offsets, selectorVals, lhsDim, selSizes, ast);
            int itemsToReplace = Selector.calculateSizeFromSelectorSizes(selSizes);
            int replacementSize = rhs.size();
            checkReplacementSize(itemsToReplace, replacementSize, true, ast);
            double[] lhsVal = ((ComplexImpl) lhs).getContent();
            double[] rhsVal = ((ComplexImpl) rhs).getContent();

            if (itemsToReplace > 0) {
                int rhsOffset = 0;
                for (;;) {
                    int lhsOffset = offsets[0];
                    if (lhsOffset != RInt.NA) {
                        lhsVal[2 * lhsOffset] = rhsVal[2 * rhsOffset];
                        lhsVal[2 * lhsOffset + 1] = rhsVal[2 * rhsOffset + 1];
                    }
                    rhsOffset++;
                    if (rhsOffset < replacementSize) {
                        if (!mayHaveNA) {
                            Selector.advanceNoNA(offsets, lhsDim, selectorVals, ast);
                        } else {
                            Selector.advance(offsets, lhsDim, selectorVals, ast);
                        }
                    } else {
                        itemsToReplace -= replacementSize;
                        if (itemsToReplace == 0) {
                            break;
                        }
                        rhsOffset = 0;
                        if (!mayHaveNA) {
                            Selector.restartNoNA(offsets, selectorVals, lhsDim, ast);
                        } else {
                            Selector.restart(offsets, selectorVals, lhsDim, ast);
                        }
                    }
                }
            }
            return lhs;
        }

        @Override
        protected RArray update(RArray lhs, RArray rhs) throws UnexpectedResultException {
            return doUpdate(this, lhs, rhs);
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

    protected abstract static class Upcast extends Impl {
    }

    protected abstract static class Duplicate extends Impl {
    }


    public static final Duplicate LOGICAL_TO_LOGICAL = new Duplicate() {

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

    public static final Upcast LOGICAL_TO_INT = new Upcast() {

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

    public static final Duplicate INT_TO_INT = new Duplicate() {

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

    public static final Duplicate INT_TO_INT_DIRECT = new Duplicate() {
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

    public static final Upcast LOGICAL_TO_DOUBLE = new Upcast() {

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

    public static final Upcast INT_TO_DOUBLE = new Upcast() {

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

    public static final Upcast INT_TO_DOUBLE_DIRECT = new Upcast() {
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

    public static final Duplicate DOUBLE_TO_DOUBLE = new Duplicate() {

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

    public static final Duplicate DOUBLE_TO_DOUBLE_DIRECT = new Duplicate() {
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

    public static final Upcast LOGICAL_TO_COMPLEX = new Upcast() {

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

    public static final Upcast INT_TO_COMPLEX = new Upcast() {

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

    public static final Upcast INT_TO_COMPLEX_DIRECT = new Upcast() {
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


    public static final Upcast DOUBLE_TO_COMPLEX = new Upcast() {

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

    public static final Upcast DOUBLE_TO_COMPLEX_DIRECT = new Upcast() {
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

    public static final Duplicate COMPLEX_TO_COMPLEX = new Duplicate() {

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

    public static final Impl COMPLEX_TO_COMPLEX_DIRECT = new Duplicate() {
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

    public static final Upcast LOGICAL_TO_STRING = new Upcast() {

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

    public static final Upcast INT_TO_STRING = new Upcast() {

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

    public static final Upcast DOUBLE_TO_STRING = new Upcast() {

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

    public static final Upcast COMPLEX_TO_STRING = new Upcast() {

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

    public static final Duplicate STRING_TO_STRING = new Duplicate() {

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
    public static final Duplicate RAW_TO_RAW = new Duplicate() {

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
