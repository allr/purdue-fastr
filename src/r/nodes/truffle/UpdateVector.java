package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.RArray.Names;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.ASTNode;

import java.util.*;
import r.Truffle.*;

// TODO: clean-up generic code using .getRef

// FIXME: the code handling the replacement of a variable could be replaced via
// ReplacementCall

// FIXME: could reduce code size by some refactoring, e.g. subclassing on
// copiers that use double, int, logical
// FIXME: some of the guards from common "exec" methods could be elided - type
// checking for RArray and then again in specialized functions for RArray
// subclasses

public abstract class UpdateVector extends BaseR {

    final RSymbol var;
    @Child RNode lhs;
    @Children RNode[] indexes;
    @Child RNode rhs;
    final boolean subset;

    @Child RNode assign; // node which will assign the whole new vector to var
    RAny newVector;
    final boolean isSuper;

    FrameSlot frameSlot = null;
    boolean slotInitialized = false;

    private static final boolean DEBUG_UP = false;

    protected UpdateVector(UpdateVector from) {
        this(from.ast, from.isSuper, from.var, from.lhs, from.indexes, from.rhs, from.subset);
    }

    UpdateVector(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
        super(ast);
        this.var = var;
        this.lhs = adoptChild(lhs); // lhs is always SimpleAccessVariable of var
        this.indexes = adoptChildren(indexes);
        this.rhs = adoptChild(rhs);
        this.subset = subset;
        this.isSuper = isSuper;

        if (isSuper) { // FIXME: turn this switch into node rewriting?
            RNode node = adoptChild(new BaseR(ast) {
                @Override public final Object execute(Frame frame) {
                    return newVector;
                }
            });
            this.assign = adoptChild(SuperWriteVariable.getUninitialized(ast, var, node));
        } else {
            // this.assign = updateParent(WriteVariable.getUninitialized(ast, var, node));
            this.assign = null;
        }
    }

    enum Failure {
        NOT_ARRAY_BASE,
        NOT_ONE_ELEMENT_INDEX,
        NOT_NUMERIC_INDEX,
        NOT_LOGICAL_INDEX,
        NOT_ARRAY_INDEX,
        NOT_INT_SEQUENCE_INDEX,
        INDEX_OUT_OF_BOUNDS,
        NOT_ONE_ELEMENT_VALUE,
        NOT_ARRAY_VALUE,
        UNEXPECTED_TYPE,
        NOT_SAME_LENGTH,
        MAYBE_VECTOR_UPDATE,
    }

    public final Object executeSuper(Frame frame) {
        RAny value = (RAny) rhs.execute(frame); // note: order is important
        RAny index = (RAny) indexes[0].execute(frame);

        RAny base;
        if (frame != null) { // FIXME: turn this guard into node rewriting, it only has to be done once
            base = (RAny) lhs.execute(RFrameHeader.enclosingFrame(frame));
        } else {
            throw RError.getUnknownVariable(ast, var);
        }

        newVector = execute(base, index, value);
        assign.execute(frame); // FIXME: may ref unnecessarily
        return value;
    }

    @Override public final Object execute(Frame frame) {
        if (isSuper) { return executeSuper(frame); }
        RAny value = (RAny) rhs.execute(frame); // note: order is important
        RAny index = (RAny) indexes[0].execute(frame);

        if (frame != null) {
            if (!slotInitialized) { // FIXME: turn this into node rewriting
                frameSlot = RFrameHeader.findVariable(frame, var);
                slotInitialized = true;
            }
            // variable has a local slot
            // note: this always has to be the case because the variable is in the write set
            // FIXME: this won't work for reflections
            assert Utils.check(frameSlot != null);
            RAny base = (RAny) frame.getObject(frameSlot);
            if (base != null) {
                RAny newBase = execute(base, index, value);
                if (newBase != base) {
                    RFrameHeader.writeAtRef(frame, frameSlot, newBase);
                }
            } else { // this should be uncommon
                base = RFrameHeader.readViaWriteSetSlowPath(frame, var);
                if (base == null) { throw RError.getUnknownVariable(getAST()); }
                base.ref(); // reading from parent, hence need to copy on update
                // ref once will make it shared unless it is stateless (like int sequence)
                RAny newBase = execute(base, index, value);
                Utils.check(base != newBase);
                RFrameHeader.writeAtRef(frame, frameSlot, newBase);
            }
        } else {
            // variable is top-level
            RAny base = var.getValue();
            if (base == null) { throw RError.getUnknownVariable(getAST()); }
            RAny newBase = execute(base, index, value);
            if (newBase != base) {
                RFrameHeader.writeToTopLevelRef(var, newBase);
            }
        }
        return value;
    }

    abstract RAny execute(RAny base, RAny index, RAny value);

    // FIXME: move these to some other file?
    public static Names expandNames(Names names, int newSize) {
        RSymbol[] oldSymbols = names.sequence();
        RSymbol[] symbols = new RSymbol[newSize];
        System.arraycopy(oldSymbols, 0, symbols, 0, oldSymbols.length);
        Arrays.fill(symbols, oldSymbols.length, newSize, RSymbol.EMPTY_SYMBOL);
        return Names.create(symbols);
    }

    public static Names removeName(Names names, int removeIndex) {
        RSymbol[] oldSymbols = names.sequence();
        int nsize = oldSymbols.length - 1;
        RSymbol[] symbols = new RSymbol[nsize];
        System.arraycopy(oldSymbols, 0, symbols, 0, removeIndex);
        if (removeIndex < nsize) {
            System.arraycopy(oldSymbols, removeIndex + 1, symbols, removeIndex, nsize - removeIndex);
        }
        return Names.create(symbols);
    }

    public static Names appendName(Names names, RSymbol newName) {
        RSymbol[] oldSymbols = names.sequence();
        int size = oldSymbols.length;
        RSymbol[] symbols = new RSymbol[size + 1];
        System.arraycopy(oldSymbols, 0, symbols, 0, size);
        symbols[size] = newName;
        return Names.create(symbols);
    }

    // for an update of a materialized double private vector using a double scalar,
    // indexed by a scalar (only simple cases)
    public abstract static class DoubleBaseSimpleSelection extends UpdateVector {
        public DoubleBaseSimpleSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
        }

        public static class ScalarIntSelection extends DoubleBaseSimpleSelection {
            public ScalarIntSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
                super(ast, isSuper, var, lhs, indexes, rhs, subset);
            }

            @Override public RAny execute(RAny base, RAny index, RAny value) {
                try {
                    if (!(index instanceof ScalarIntImpl)) { throw new UnexpectedResultException(null); }
                    int i = (((ScalarIntImpl) index).getInt()) - 1;
                    if (!(base instanceof DoubleImpl)) { throw new UnexpectedResultException(null); }
                    DoubleImpl dibase = (DoubleImpl) base;
                    if (dibase.isShared()) { throw new UnexpectedResultException(null); }
                    double[] dbase = dibase.getContent();
                    if (i < 0 || i >= dbase.length) { throw new UnexpectedResultException(null); }
                    if (!(value instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(null); }
                    double dvalue = ((ScalarDoubleImpl) value).getDouble();

                    dbase[i] = dvalue;

                    return dibase;
                } catch (UnexpectedResultException e) {
                    ScalarDoubleSelection ns = new ScalarDoubleSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                    replace(ns, "install DoubleBaseSimpleSelection.ScalarDoubleSelection from DoubleBaseSimpleSelection.ScalarIntSelection");
                    return ns.execute(base, index, value);
                }
            }
        }

        public static class ScalarDoubleSelection extends DoubleBaseSimpleSelection {
            public ScalarDoubleSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
                super(ast, isSuper, var, lhs, indexes, rhs, subset);
            }

            @Override public RAny execute(RAny base, RAny index, RAny value) {
                try {
                    if (!(index instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(null); }
                    int i = Convert.double2int(((ScalarDoubleImpl) index).getDouble()) - 1;

                    if (!(base instanceof DoubleImpl)) { // FIXME: extract to a static method? (without performance overhead)
                        throw new UnexpectedResultException(null);
                    }
                    DoubleImpl dibase = (DoubleImpl) base;
                    if (dibase.isShared()) { throw new UnexpectedResultException(null); }
                    double[] dbase = dibase.getContent();
                    if (i < 0 || i >= dbase.length) { throw new UnexpectedResultException(null); }
                    if (!(value instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(null); }
                    double dvalue = ((ScalarDoubleImpl) value).getDouble();

                    dbase[i] = dvalue;

                    return dibase;
                } catch (UnexpectedResultException e) {
                    ScalarDoubleWithAttributesSelection ns = new ScalarDoubleWithAttributesSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                    replace(ns, "install ScalarDoubleWithAttributesSelection from DoubleBaseSimpleSelection.ScalarIntSelection");
                    return ns.execute(base, index, value);
                }
            }
        }

        public static class ScalarDoubleWithAttributesSelection extends DoubleBaseSimpleSelection {
            public ScalarDoubleWithAttributesSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
                super(ast, isSuper, var, lhs, indexes, rhs, subset);
            }

            @Override public RAny execute(RAny base, RAny index, RAny value) {
                try {
                    if (!(index instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(null); }
                    int i = Convert.double2int(((ScalarDoubleImpl) index).getDouble()) - 1;

                    if (!(base instanceof DoubleImpl)) { // FIXME: extract to a static method? (without performance overhead)
                        throw new UnexpectedResultException(null);
                    }
                    DoubleImpl dibase = (DoubleImpl) base;
                    if (dibase.isShared()) { throw new UnexpectedResultException(null); }
                    double[] dbase = dibase.getContent();
                    if (i < 0 || i >= dbase.length) { throw new UnexpectedResultException(null); }
                    if (!(value instanceof RDouble)) { throw new UnexpectedResultException(null); }
                    RDouble dblvalue = (RDouble) value;
                    if (dblvalue.size() != 1) { throw new UnexpectedResultException(null); }
                    dbase[i] = dblvalue.getDouble(0);
                    return dibase;
                } catch (UnexpectedResultException e) {
                    ScalarNumericSelection ns = new ScalarNumericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                    replace(ns, "install ScalarNumericSelection from DoubleBaseSimpleSelection.ScalarIntSelection");
                    return ns.execute(base, index, value);
                }
            }
        }
    }

    // for a numeric (int, double) scalar index, first installs an uninitialized node
    // this node rewrites itself to type-specialized nodes for simple assignment, or to a generic node
    // the specialized nodes can rewrite themselves to the generic node
    // rewrites to GenericScalarSelection when types change or otherwise needed
    public static class ScalarNumericSelection extends UpdateVector {

        public ScalarNumericSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing ScalarNumericSelection (uninitialized)");

            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized sn = createSimple(base, value);
                if (sn != null) {
                    replace(sn, "specialize ScalarNumericSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with ScalarNumericSelection.Simple");
                    return sn.execute(base, index, value);
                } else {
                    sn = createGeneric();
                    replace(sn, "specialize ScalarNumericSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with ScalarNumericSelection.Generic");
                    return sn.execute(base, index, value);
                }
            }
        }

        abstract class ValueCopy {
            abstract RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException;
        }

        public Specialized createSimple(RAny baseTemplate, RAny valueTemplate) {
            if (baseTemplate instanceof RInt) {
                if (valueTemplate instanceof ScalarIntImpl) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RInt) || !(value instanceof ScalarIntImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RInt ibase = (RInt) base;
                            int bsize = ibase.size();
                            if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int zpos = pos - 1;
                            if (!ibase.isShared()) {
                                return ibase.set(zpos, ((ScalarIntImpl) value).getInt());
                            } else {
                                int[] content = new int[bsize];
                                int i = 0;
                                for (; i < zpos; i++) {
                                    content[i] = ibase.getInt(i);
                                }
                                content[i++] = ((ScalarIntImpl) value).getInt();
                                for (; i < bsize; i++) {
                                    content[i] = ibase.getInt(i);
                                }
                                return RInt.RIntFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                            }
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RInt,ScalarInt>");
                }
                if (valueTemplate instanceof ScalarLogicalImpl) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RInt) || !(value instanceof ScalarLogicalImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RInt ibase = (RInt) base;
                            int bsize = ibase.size();
                            if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int zpos = pos - 1;
                            if (!ibase.isShared()) {
                                return ibase.set(zpos, ((ScalarLogicalImpl) value).getLogical());
                            } else {
                                int[] content = new int[bsize];
                                int i = 0;
                                for (; i < zpos; i++) {
                                    content[i] = ibase.getInt(i);
                                }
                                content[i++] = ((ScalarLogicalImpl) value).getLogical();
                                for (; i < bsize; i++) {
                                    content[i] = ibase.getInt(i);
                                }
                                return RInt.RIntFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                            }
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RInt,ScalarLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RDouble) {
                if (valueTemplate instanceof ScalarDoubleImpl) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble) || !(value instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RDouble dbase = (RDouble) base;
                            int bsize = dbase.size();
                            if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int zpos = pos - 1;
                            if (!dbase.isShared()) {
                                return dbase.set(zpos, ((ScalarDoubleImpl) value).getDouble());
                            } else {
                                double[] content = new double[bsize];
                                int i = 0;
                                for (; i < zpos; i++) {
                                    content[i] = dbase.getDouble(i);
                                }
                                content[i++] = ((ScalarDoubleImpl) value).getDouble();
                                for (; i < bsize; i++) {
                                    content[i] = dbase.getDouble(i);
                                }
                                return RDouble.RDoubleFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                            }
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RDouble,ScalarDouble>");
                }
                if (valueTemplate instanceof ScalarIntImpl) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble) || !(value instanceof ScalarIntImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RDouble dbase = (RDouble) base;
                            int bsize = dbase.size();
                            if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int zpos = pos - 1;
                            if (!dbase.isShared()) {
                                return dbase.set(zpos, Convert.int2double(((ScalarIntImpl) value).getInt()));
                            } else {
                                double[] content = new double[bsize];
                                int i = 0;
                                for (; i < zpos; i++) {
                                    content[i] = dbase.getDouble(i);
                                }
                                content[i++] = Convert.int2double(((ScalarIntImpl) value).getInt());
                                for (; i < bsize; i++) {
                                    content[i] = dbase.getDouble(i);
                                }
                                return RDouble.RDoubleFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                            }
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RDouble,ScalarInt>");
                }
                if (valueTemplate instanceof ScalarLogicalImpl) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble) || !(value instanceof ScalarLogicalImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RDouble dbase = (RDouble) base;
                            int bsize = dbase.size();
                            if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int zpos = pos - 1;
                            if (!dbase.isShared()) {
                                return dbase.set(zpos, Convert.logical2double(((ScalarLogicalImpl) value).getLogical()));
                            } else {
                                double[] content = new double[bsize];
                                int i = 0;
                                for (; i < zpos; i++) {
                                    content[i] = dbase.getDouble(i);
                                }
                                content[i++] = Convert.logical2double(((ScalarLogicalImpl) value).getLogical());
                                for (; i < bsize; i++) {
                                    content[i] = dbase.getDouble(i);
                                }
                                return RDouble.RDoubleFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                            }
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RDouble,ScalarLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RLogical) {
                if (valueTemplate instanceof ScalarLogicalImpl) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RLogical) || !(value instanceof ScalarLogicalImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RLogical lbase = (RLogical) base;
                            int bsize = lbase.size();
                            if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int zpos = pos - 1;
                            if (!lbase.isShared()) {
                                return lbase.set(zpos, ((ScalarLogicalImpl) value).getLogical());
                            } else {
                                int[] content = new int[bsize];
                                int i = 0;
                                for (; i < zpos; i++) {
                                    content[i] = lbase.getLogical(i);
                                }
                                content[i++] = ((ScalarLogicalImpl) value).getLogical();
                                for (; i < bsize; i++) {
                                    content[i] = lbase.getLogical(i);
                                }
                                return RLogical.RLogicalFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                            }
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RLogical,ScalarLogical>");
                }
            }
            if (baseTemplate instanceof RList && !subset && !(valueTemplate instanceof RNull)) {
                ValueCopy cpy = new ValueCopy() {
                    @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                        if (!(base instanceof RList) || (value instanceof RNull)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                        RList lbase = (RList) base;
                        int bsize = lbase.size();
                        if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                        int zpos = pos - 1;
                        value.ref();
                        if (!lbase.isShared()) {
                            return lbase.set(zpos, value);
                        } else {
                            RAny[] content = new RAny[bsize];
                            int i = 0;
                            for (; i < zpos; i++) { // shallow copy
                                content[i] = lbase.getRAny(i);
                            }
                            content[i++] = value;
                            for (; i < bsize; i++) { // shallow copy
                                content[i] = lbase.getRAny(i);
                            }
                            return RList.RListFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                        }
                    }
                };
                return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RList,?>");
            }
            if (baseTemplate instanceof RString && valueTemplate instanceof ScalarStringImpl) {
                ValueCopy cpy = new ValueCopy() {
                    @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                        if (!(base instanceof RString) || !(value instanceof ScalarStringImpl)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                        RString sbase = (RString) base;
                        int bsize = sbase.size();
                        if (pos < 1 || pos > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                        int zpos = pos - 1;
                        if (!sbase.isShared()) {
                            return sbase.set(zpos, ((ScalarStringImpl) value).getString());
                        } else {
                            String[] content = new String[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
                                content[i] = sbase.getString(i);
                            }
                            content[i++] = ((ScalarStringImpl) value).getString();
                            for (; i < bsize; i++) {
                                content[i] = sbase.getString(i);
                            }
                            return RString.RStringFactory.getFor(content, base.dimensions(), base.names(), base.attributesRef());
                        }
                    }
                };
                return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RString,ScalarString>");
            }
            return null;
        }

        // FIXME: the asXXX functions will allocate boxes, probably should create asXXXScalar() casts
        public static RAny genericUpdate(RArray base, int pos, RAny value, boolean subset, ASTNode ast) {
            // FIXME: avoid some copying here but careful about lists
            RArray typedBase;
            Object rawValue;
            int[] dimensions = base.dimensions();
            Names names = base.names();

            if (value instanceof RList) {
                if (base instanceof RList) {
                    typedBase = base;
                } else {
                    typedBase = base.asList();
                    dimensions = null;
                }
                RAny v = subset ? ((RList) value).getRAny(0) : value;
                v.ref();
                rawValue = v;
            } else if (base instanceof RList) {
                typedBase = base;
                rawValue = value;
                value.ref();
            } else if (base instanceof RString || value instanceof RString) {
                typedBase = base.asString();
                rawValue = value.asString().get(0);
            } else if (base instanceof RComplex || value instanceof RComplex) {
                typedBase = base.asComplex();
                rawValue = value.asComplex().get(0);
            } else if (base instanceof RDouble || value instanceof RDouble) {
                typedBase = base.asDouble();
                rawValue = value.asDouble().get(0);
            } else if (base instanceof RInt || value instanceof RInt) {
                typedBase = base.asInt();
                rawValue = value.asInt().get(0);
            } else if (base instanceof RLogical || value instanceof RLogical) {
                typedBase = base.asLogical();
                rawValue = value.asLogical().get(0);
            } else {
                Utils.nyi("unsupported vector types");
                return null;
            }
            int bsize = base.size();
            if (pos > 0) {
                if (pos <= bsize) {
                    int zpos = pos - 1;
                    if (base == typedBase && !base.isShared()) {
                        base.set(zpos, rawValue);
                        return base;
                    }
                    RArray res = Utils.createArray(typedBase, bsize, dimensions, names, base.attributesRef());
                    int i = 0;
                    for (; i < zpos; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    res.set(i++, rawValue);
                    for (; i < bsize; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    return res;
                } else {
                    int zpos = pos - 1;
                    int nsize = zpos + 1;
                    RArray res = Utils.createArray(typedBase, nsize, names != null).setAttributes(base.attributesRef()); // drop
                                                                                                                         // dimensions
                    int i = 0;
                    for (; i < bsize; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    for (; i < zpos; i++) {
                        Utils.setNA(res, i);
                    }
                    res.set(i, rawValue);
                    if (names != null) {
                        res = res.setNames(expandNames(names, zpos + 1));
                    }
                    return res;
                }
            } else { // pos < 0
                if (pos == RInt.NA) { return base; }
                if (!subset) {
                    if (bsize <= 1) { throw RError.getSelectLessThanOne(ast); }
                    if (bsize > 2) { throw RError.getSelectMoreThanOne(ast); }
                    // bsize == 2
                    if (pos != -1 && pos != -2) { throw RError.getSelectMoreThanOne(ast); }
                }
                int keep = -pos - 1;
                Utils.refIfRAny(rawValue); // ref once again to make sure it is treated as shared
                RArray res = Utils.createArray(typedBase, bsize, dimensions, names, base.attributesRef());
                int i = 0;
                for (; i < keep; i++) {
                    res.set(i, rawValue);
                }
                res.set(i, typedBase.get(i));
                i++;
                for (; i < bsize; i++) {
                    res.set(i, rawValue);
                }
                return res;
            }
        }

        public Specialized createGeneric() {
            ValueCopy cpy = new ValueCopy() {
                @Override RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                    if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                    RArray avalue = (RArray) value;
                    int vsize = avalue.size();
                    if (vsize != 1) { throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT_VALUE); }
                    return genericUpdate(base, pos, value, subset, ast);
                }
            };
            return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<Generic>");
        }

        class Specialized extends ScalarNumericSelection {
            final ValueCopy copy;
            final String dbg;

            Specialized(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset, ValueCopy copy, String dbg) {
                super(ast, isSuper, var, lhs, indexes, rhs, subset);
                this.copy = copy;
                this.dbg = dbg;
            }

            @Override public RAny execute(RAny base, RAny index, RAny value) {
                if (DEBUG_UP) Utils.debug("update - executing ScalarNumericSelection" + dbg);
                try {
                    if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                    RArray abase = (RArray) base;
                    int pos;
                    if (index instanceof ScalarIntImpl) {
                        pos = ((ScalarIntImpl) index).getInt();
                    } else if (index instanceof ScalarDoubleImpl) {
                        pos = Convert.double2int(((ScalarDoubleImpl) index).getDouble());
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                    return copy.copy(abase, pos, value);

                } catch (UnexpectedResultException e) {
                    Failure f = (Failure) e.getResult();
                    if (f == null) {
                        if ((index instanceof RArray) && (((RArray) index).size() != 1)) {
                            f = Failure.NOT_ONE_ELEMENT_INDEX;
                        } else {
                            f = Failure.NOT_NUMERIC_INDEX;
                        }
                    }
                    if (DEBUG_UP) Utils.debug("update - ScalarNumericSelection" + dbg + " failed: " + f);
                    switch (f) {
                    case INDEX_OUT_OF_BOUNDS:
                    case UNEXPECTED_TYPE:
                        Specialized sn = createGeneric();
                        replace(sn, "specialize ScalarNumericSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with ScalarNumericSelection.Generic");
                        return sn.execute(base, index, value);

                    case NOT_ONE_ELEMENT_INDEX:
                        if (!subset) {
                            Subscript s = new Subscript(ast, isSuper, var, lhs, indexes, rhs, subset);
                            replace(s, "install Subscript from ScalarNumericSelection");
                            if (DEBUG_UP) Utils.debug("update - replaced and re-executing with Subscript");
                            return s.execute(base, index, value);
                        }
                        // propagate below

                    default:
                        GenericScalarSelection gs = new GenericScalarSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                        replace(gs, "install GenericScalarSelection from ScalarNumericSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with GenericScalarSelection");
                        return gs.execute(base, index, value);
                    }
                }
            }
        }
    }

    public static class ScalarStringSelection {
        public static RArray genericUpdate(RArray base, String index, RAny value, boolean subset, ASTNode ast) {
            RArray typedBase;
            Object rawValue;
            int[] dimensions = base.dimensions();
            Names names = base.names();

            if (value instanceof RList) { // FIXME: this code gets copied around a few times, could it be refactored without a performance penalty?
                if (base instanceof RList) {
                    typedBase = base;
                } else {
                    typedBase = base.asList();
                    dimensions = null;
                }
                RAny v = subset ? ((RList) value).getRAny(0) : value;
                v.ref();
                rawValue = v;
            } else if (base instanceof RList) {
                typedBase = base;
                rawValue = value;
                value.ref();
            } else if (base instanceof RString || value instanceof RString) {
                typedBase = base.asString();
                rawValue = value.asString().get(0);
            } else if (base instanceof RDouble || value instanceof RDouble) {
                typedBase = base.asDouble();
                rawValue = value.asDouble().get(0);
            } else if (base instanceof RInt || value instanceof RInt) {
                typedBase = base.asInt();
                rawValue = value.asInt().get(0);
            } else if (base instanceof RLogical || value instanceof RLogical) {
                typedBase = base.asLogical();
                rawValue = value.asLogical().get(0);
            } else {
                Utils.nyi("unsupported vector types");
                return null;
            }
            int bsize = base.size();
            int pos = -1;
            RSymbol symbol = RSymbol.getSymbol(index);
            if (names != null) {
                pos = names.map(symbol);
            }
            if (pos != -1) {
                // updating
                if (base == typedBase && !base.isShared()) {
                    base.set(pos, rawValue);
                    return base;
                }
                RArray res = Utils.createArray(typedBase, bsize, dimensions, names, base.attributesRef());
                int i = 0;
                for (; i < pos; i++) {
                    res.set(i, typedBase.get(i));
                }
                res.set(i++, rawValue);
                for (; i < bsize; i++) {
                    res.set(i, typedBase.get(i));
                }
                return res;
            }
            // appending, if names are empty, create them - this is for appending to empty lists and vectors
            if (names == null) {
                names = RArray.Names.create(bsize);
            }
            RArray res = Utils.createArray(typedBase, bsize + 1, dimensions, appendName(names, symbol), base.attributesRef());
            for (int i = 0; i < bsize; i++) {
                res.set(i, typedBase.get(i));
            }
            res.set(bsize, rawValue);
            return res;
        }
    }

    // any update when the selector is a scalar
    // includes deletion of list elements (FIXME: perhaps could move that out into a special node?)
    // rewrites itself if the update is in fact vector-like (subset with logical
    // index, multi-value subset with negative number index)
    // rewrites for other cases (vector selection)
    // so the contract is that this can handle any subscript with a single-value index
    public static class GenericScalarSelection extends UpdateVector {

        public GenericScalarSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
        }

        public static RAny deleteElement(RList base, int i) {
            return deleteElement(base, i, base.size());
        }

        public static RAny deleteElement(RList base, int index, int size) {
            int j = 0;
            int i = index;
            int nsize = size - 1;
            RAny[] content = new RAny[nsize];
            for (; j < i; j++) { // shallow copy
                content[j] = base.getRAny(j);
            }
            i++;
            for (; j < nsize; j++) { // shallow copy
                content[j] = base.getRAny(i++);
            }
            Names bnames = base.names();
            return RList.RListFactory.getFor(content, null, bnames == null ? null : removeName(bnames, index)); // drop dimensions
        }

        public static RAny deleteElement(RList base, int i, ASTNode ast, boolean subset) {
            int size = base.size();
            if (i > 0) {
                int zi = i - 1; // zero-based
                if (i <= size) {
                    // remove element i
                    return deleteElement(base, zi, size);
                } else if (subset && i > size) {
                    // note that we could have this branch just for "i > size + 1", however, not quite, because
                    // when i == size + 1, subset drops dimensions
                    int j = 0;
                    int nsize = i - 1;
                    RAny[] content = new RAny[nsize];
                    for (; j < size; j++) { // shallow copy
                        content[j] = base.getRAny(j);
                    }
                    for (; j < nsize; j++) {
                        content[j] = RList.NULL;
                    }
                    Names bnames = base.names();
                    return RList.RListFactory.getFor(content, null, bnames == null ? null : expandNames(bnames, nsize)); // drop
                                                                                                                         // dimensions
                } else {
                    return base;
                }
            }
            if (i == 0 || i == RInt.NA) {
                if (subset) {
                    return base;
                } else {
                    throw RError.getSelectLessThanOne(ast);
                }
            }
            // i < 0
            if (!subset) {
                if (size <= 1) { throw RError.getSelectLessThanOne(ast); }
                if (size > 2) { throw RError.getSelectMoreThanOne(ast); }
                if (i != -1 && i != -2) { throw RError.getSelectMoreThanOne(ast); }
            }
            int keep = -i - 1;
            return RList.RListFactory.getScalar(base.getRAny(keep)); // shallow
                                                                     // copy
        }

        public static RAny deleteElement(RList base, String index) {
            Names names = base.names();
            int deleteIndex = -1;
            if (names != null) {
                deleteIndex = names.map(RSymbol.getSymbol(index));
            }
            if (deleteIndex != -1) {
                return deleteElement(base, deleteIndex);
            } else {
                return base;
            }
        }

        public static RAny deleteElement(RArray base, RArray index, ASTNode ast, boolean subset) {
            if (!(base instanceof RList)) { throw RError.getReplacementZero(ast); }
            RList l = (RList) base;
            int i;
            if (index instanceof RInt) {
                i = ((RInt) index).getInt(0);
                return deleteElement(l, i, ast, subset);
            } else if (index instanceof RDouble) {
                i = Convert.double2int(((RDouble) index).getDouble(0));
                return deleteElement(l, i, ast, subset);
            } else if (index instanceof RLogical) {
                i = Convert.logical2int(((RLogical) index).getLogical(0));
                if (subset) {
                    if (i == RLogical.TRUE) {
                        return RList.EMPTY;
                    } else {
                        return base;
                    }
                }
                return deleteElement(l, i, ast, subset);
            } else if (index instanceof RString) {
                return deleteElement(l, ((RString) index).getString(0));
            } else if (index instanceof RNull) {
                return l;
            } else {
                Utils.nyi("unsupported type");
                return null;
            }
        }

        public static RAny update(RArray base, RArray index, RArray value, ASTNode ast, boolean subset) throws UnexpectedResultException {
            int vsize = value.size();
            if (vsize == 0) { throw RError.getReplacementZero(ast); }
            if (index instanceof RString) { return ScalarStringSelection.genericUpdate(base, ((RString) index).getString(0), value, subset, ast); }
            if (index instanceof RLogical) {
                if (subset) { throw new UnexpectedResultException(Failure.MAYBE_VECTOR_UPDATE); }
                if (vsize > 1 && !(base instanceof RList)) { throw RError.getMoreElementsSupplied(ast); }
                int l = ((RLogical) index).getLogical(0);
                if (l == RLogical.FALSE) { throw RError.getSelectLessThanOne(ast); }
                if (l == RLogical.NA) { throw RError.getSelectMoreThanOne(ast); }
                return ScalarNumericSelection.genericUpdate(base, RLogical.TRUE, value, subset, ast);
            }
            int i;
            if (index instanceof RInt) {
                i = ((RInt) index).getInt(0);
            } else if (index instanceof RDouble) {
                i = Convert.double2int(((RDouble) index).getDouble(0));
            } else {
                Utils.nyi("unsupported index type in vector update");
                return null;
            }
            if (i >= 0 || i == RInt.NA || !subset) {
                if (vsize > 1) {
                    if (subset) {
                        RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                    } else {
                        if (!(base instanceof RList)) { throw RError.getMoreElementsSupplied(ast); }
                    }
                }
                return ScalarNumericSelection.genericUpdate(base, i, value, subset, ast);
            } else {
                // subset with negative index
                if (vsize == 1) {
                    return ScalarNumericSelection.genericUpdate(base, i, value, subset, ast);
                } else {
                    throw new UnexpectedResultException(Failure.MAYBE_VECTOR_UPDATE);
                }
            }
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing GenericScalarSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (!(index instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_INDEX); }
                RArray aindex = (RArray) index;
                int isize = aindex.size();
                if (isize != 1) { throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT_INDEX); }
                if (value instanceof RNull) { return deleteElement(abase, aindex, ast, subset); }
                if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                RArray avalue = (RArray) value;
                return update(abase, aindex, avalue, ast, subset);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_UP) Utils.debug("update - GenericScalarSelection failed: " + f);
                switch (f) {
                case MAYBE_VECTOR_UPDATE:
                case NOT_ONE_ELEMENT_INDEX:
                    if (subset) {
                        if (index instanceof IntImpl.RIntSequence) {
                            IntSequenceSelection is = new IntSequenceSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                            replace(is, "install IntSequenceSelection from GenericScalarSelection");
                            if (DEBUG_UP) Utils.debug("update - replaced and re-executing with IntSequenceSelection");
                            return is.execute(base, index, value);
                        }
                        if (index instanceof RInt || index instanceof RDouble) {
                            NumericSelection ns = new NumericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                            replace(ns, "install NumericSelection from GenericScalarSelection");
                            if (DEBUG_UP) Utils.debug("update - replaced and re-executing with NumericSelection");
                            return ns.execute(base, index, value);
                        }
                        if (index instanceof RLogical) {
                            LogicalSelection ls = new LogicalSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                            replace(ls, "install LogicalSelection from GenericScalarSelection");
                            if (DEBUG_UP) Utils.debug("update - replaced and re-executing with LogicalSelection");
                            return ls.execute(base, index, value);
                        }
                    } else {
                        Subscript s = new Subscript(ast, isSuper, var, lhs, indexes, rhs, subset);
                        replace(s, "install Subscript from GenericScalarSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with Subscript");
                        return s.execute(base, index, value);
                    }
                    // propagate below

                default:
                    GenericSelection gs = new GenericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                    replace(gs, "install GenericSelection from GenericScalarSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with GenericSelection");
                    return gs.execute(base, index, value);
                }
            }
        }
    }

    // for updates where the index is an int sequence
    // specializes for types (base, value) in simple cases
    // handles also some simple cases when types change or when type-conversion
    // of base is needed
    // rewrites itself for more complicated cases
    public static class IntSequenceSelection extends UpdateVector {

        public IntSequenceSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
            Utils.check(subset);
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing IntSequenceSelection (uninitialized)");

            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized sn = createSimple(base, value);
                if (sn != null) {
                    replace(sn, "specialize IntSequenceSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with IntSequenceSelection.Simple");
                    return sn.execute(base, index, value);
                } else {
                    sn = createExtended();
                    replace(sn, "specialize IntSequenceSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with IntSequenceSelection.Extended");
                    return sn.execute(base, index, value);
                }
            }
        }

        abstract class ValueCopy {
            abstract RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException;
        }

        // specialized for type combinations (base vector, value written)
        public Specialized createSimple(RAny baseTemplate, RAny valueTemplate) {
            // FIXME: could reduce copying when value is not shared
            if (baseTemplate instanceof RList) {
                if (valueTemplate instanceof RList || valueTemplate instanceof RDouble || valueTemplate instanceof RInt || valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RList)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RList typedBase = (RList) base;
                            RList typedValue;
                            if (value instanceof RList) {
                                typedValue = (RList) value;
                            } else if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) {
                                typedValue = value.asList();
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int imin = index.min();
                            int imax = index.max();
                            if (imin < 1 || imax > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            imin--;
                            imax--; // convert to 0-based
                            int isize = index.size();
                            int vsize = typedValue.size();
                            if (isize != vsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                            RAny[] content = new RAny[bsize];
                            int i = 0;
                            for (; i < imin; i++) { // shallow copy
                                content[i] = typedBase.getRAny(i);
                            }
                            i = index.from() - 1; // -1 for 0-based
                            int step = index.step();
                            int astep;
                            int delta;
                            if (step > 0) {
                                astep = step;
                                delta = 1;
                            } else {
                                astep = -step;
                                delta = -1;
                            }
                            for (int steps = 0; steps < isize; steps++) { // shallow copy
                                content[i] = typedValue.getRAnyRef(steps);
                                i += delta;
                                for (int j = 1; j < astep; j++) { // shallow copy
                                    content[i] = typedBase.getRAny(i);
                                    i += delta;
                                }
                            }
                            for (i = imax + 1; i < bsize; i++) { // shallow copy
                                content[i] = typedBase.getRAny(i);
                            }
                            return RList.RListFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RList,RList|RDouble|RInt|RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RDouble) {
                if (valueTemplate instanceof RDouble || valueTemplate instanceof RLogical || valueTemplate instanceof RInt) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RDouble typedBase = (RDouble) base;
                            RDouble typedValue;
                            if (value instanceof RDouble) {
                                typedValue = (RDouble) value;
                            } else if (value instanceof RInt || value instanceof RLogical) {
                                typedValue = value.asDouble();
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int imin = index.min();
                            int imax = index.max();
                            if (imin < 1 || imax > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            imin--;
                            imax--; // convert to 0-based
                            int isize = index.size();
                            int vsize = typedValue.size();
                            if (isize != vsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < imin; i++) {
                                content[i] = typedBase.getDouble(i);
                            }
                            i = index.from() - 1; // -1 for 0-based
                            int step = index.step();
                            int astep;
                            int delta;
                            if (step > 0) {
                                astep = step;
                                delta = 1;
                            } else {
                                astep = -step;
                                delta = -1;
                            }
                            for (int steps = 0; steps < isize; steps++) {
                                content[i] = typedValue.getDouble(steps);
                                i += delta;
                                for (int j = 1; j < astep; j++) {
                                    content[i] = typedBase.getDouble(i);
                                    i += delta;
                                }
                            }
                            for (i = imax + 1; i < bsize; i++) {
                                content[i] = typedBase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RDouble,RDouble|RInt|RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RInt) {
                if (valueTemplate instanceof RInt || valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RInt)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RInt typedBase = (RInt) base;
                            RInt typedValue;
                            if (value instanceof RInt) {
                                typedValue = (RInt) value;
                            } else if (value instanceof RLogical) {
                                typedValue = value.asInt();
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int imin = index.min();
                            int imax = index.max();
                            if (imin < 1 || imax > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            imin--;
                            imax--; // convert to 0-based
                            int isize = index.size();
                            int vsize = typedValue.size();
                            if (isize != vsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < imin; i++) {
                                content[i] = typedBase.getInt(i);
                            }
                            i = index.from() - 1; // -1 for 0-based
                            int step = index.step();
                            int astep;
                            int delta;
                            if (step > 0) {
                                astep = step;
                                delta = 1;
                            } else {
                                astep = -step;
                                delta = -1;
                            }
                            for (int steps = 0; steps < isize; steps++) {
                                content[i] = typedValue.getInt(steps);
                                i += delta;
                                for (int j = 1; j < astep; j++) {
                                    content[i] = typedBase.getInt(i);
                                    i += delta;
                                }
                            }
                            for (i = imax + 1; i < bsize; i++) {
                                content[i] = typedBase.getInt(i);
                            }
                            return RInt.RIntFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RInt,RInt|RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RLogical) {
                if (valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RLogical && value instanceof RLogical)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RLogical typedBase = (RLogical) base;
                            RLogical typedValue = (RLogical) value;
                            int bsize = base.size();
                            int imin = index.min();
                            int imax = index.max();
                            if (imin < 1 || imax > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            imin--;
                            imax--; // convert to 0-based
                            int isize = index.size();
                            int vsize = typedValue.size();
                            if (isize != vsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < imin; i++) {
                                content[i] = typedBase.getLogical(i);
                            }
                            i = index.from() - 1; // -1 for 0-based
                            int step = index.step();
                            int astep;
                            int delta;
                            if (step > 0) {
                                astep = step;
                                delta = 1;
                            } else {
                                astep = -step;
                                delta = -1;
                            }
                            for (int steps = 0; steps < isize; steps++) {
                                content[i] = typedValue.getLogical(steps);
                                i += delta;
                                for (int j = 1; j < astep; j++) {
                                    content[i] = typedBase.getLogical(i);
                                    i += delta;
                                }
                            }
                            for (i = imax + 1; i < bsize; i++) {
                                content[i] = typedBase.getLogical(i);
                            }
                            return RLogical.RLogicalFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RLogical,RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RString) {
                if (valueTemplate instanceof RString) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RString && value instanceof RString)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RString typedBase = (RString) base;
                            RString typedValue = (RString) value;
                            int bsize = base.size();
                            int imin = index.min();
                            int imax = index.max();
                            if (imin < 1 || imax > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            imin--;
                            imax--; // convert to 0-based
                            int isize = index.size();
                            int vsize = typedValue.size();
                            if (isize != vsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                            String[] content = new String[bsize];
                            int i = 0;
                            for (; i < imin; i++) {
                                content[i] = typedBase.getString(i);
                            }
                            i = index.from() - 1; // -1 for 0-based
                            int step = index.step();
                            int astep;
                            int delta;
                            if (step > 0) {
                                astep = step;
                                delta = 1;
                            } else {
                                astep = -step;
                                delta = -1;
                            }
                            for (int steps = 0; steps < isize; steps++) {
                                content[i] = typedValue.getString(steps);
                                i += delta;
                                for (int j = 1; j < astep; j++) {
                                    content[i] = typedBase.getString(i);
                                    i += delta;
                                }
                            }
                            for (i = imax + 1; i < bsize; i++) {
                                content[i] = typedBase.getString(i);
                            }
                            return RString.RStringFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RString,RString>");
                }
                return null;
            }
            return null;
        }

        // handles type conversion of base
        public Specialized createExtended() {
            ValueCopy cpy = new ValueCopy() {
                @Override RAny copy(RArray base, IntImpl.RIntSequence index, RAny value) throws UnexpectedResultException {
                    RArray typedBase;
                    RArray typedValue;
                    RList listValue = null;
                    int[] dimensions;
                    if (value instanceof RList) {
                        typedValue = null;
                        listValue = (RList) value;
                        if (base instanceof RList) {
                            typedBase = base;
                            dimensions = listValue.dimensions();
                        } else {
                            typedBase = base.asList();
                            dimensions = null;
                        }
                    } else {
                        if (base instanceof RList) {
                            typedBase = base;
                            typedValue = value.asList();
                        } else if (base instanceof RString || value instanceof RString) {
                            typedBase = base.asString();
                            typedValue = value.asString();
                        } else if (base instanceof RDouble || value instanceof RDouble) {
                            typedBase = base.asDouble();
                            typedValue = value.asDouble();
                        } else if (base instanceof RInt || value instanceof RInt) {
                            typedBase = base.asInt();
                            typedValue = value.asInt();
                        } else if (base instanceof RLogical || value instanceof RLogical) {
                            typedBase = base.asLogical();
                            typedValue = value.asLogical();
                        } else {
                            Utils.nyi("unsupported vector types");
                            return null;
                        }
                        dimensions = typedValue.dimensions();
                    }
                    int bsize = base.size();
                    int imin = index.min();
                    int imax = index.max();
                    if (imin < 1 || imax > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                    imin--;
                    imax--; // convert to 0-based
                    int isize = index.size();
                    int vsize = typedValue != null ? typedValue.size() : listValue.size();
                    if (isize != vsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                    RArray res = Utils.createArray(typedBase, bsize, dimensions, base.names(), base.attributesRef());
                    int i = 0;
                    for (; i < imin; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    i = index.from() - 1; // -1 for 0-based
                    int step = index.step();
                    int astep;
                    int delta;
                    if (step > 0) {
                        astep = step;
                        delta = 1;
                    } else {
                        astep = -step;
                        delta = -1;
                    }
                    if (typedValue != null) {
                        for (int steps = 0; steps < isize; steps++) {
                            res.set(i, typedValue.get(steps));
                            i += delta;
                            for (int j = 1; j < astep; j++) {
                                res.set(i, typedBase.get(i));
                                i += delta;
                            }
                        }
                    } else {
                        for (int steps = 0; steps < isize; steps++) {
                            res.set(i, listValue.getRAnyRef(steps));
                            i += delta;
                            for (int j = 1; j < astep; j++) {
                                res.set(i, typedBase.get(i));
                                i += delta;
                            }
                        }
                    }
                    for (i = imax + 1; i < bsize; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    return res;
                }
            };
            return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<Extended>");
        }

        class Specialized extends IntSequenceSelection {
            final ValueCopy copy;
            final String dbg;

            Specialized(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset, ValueCopy copy, String dbg) {
                super(ast, isSuper, var, lhs, indexes, rhs, subset);
                this.copy = copy;
                this.dbg = dbg;
            }

            @Override public RAny execute(RAny base, RAny index, RAny value) {
                if (DEBUG_UP) Utils.debug("update - executing IntSequenceSelection" + dbg);
                try {
                    if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                    RArray abase = (RArray) base;
                    if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                    RArray avalue = (RArray) value;
                    if (!(index instanceof IntImpl.RIntSequence)) { throw new UnexpectedResultException(Failure.NOT_INT_SEQUENCE_INDEX); }
                    return copy.copy(abase, (IntImpl.RIntSequence) index, avalue);

                } catch (UnexpectedResultException e) {
                    Failure f = (Failure) e.getResult();
                    if (DEBUG_UP) Utils.debug("update - IntSequenceSelection" + dbg + " failed: " + f);
                    switch (f) {
                    case UNEXPECTED_TYPE:
                        Specialized sn = createExtended();
                        replace(sn, "specialize IntSequenceSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with IntSequenceSelection.Extended");
                        return sn.execute(base, index, value);

                    default:
                        NumericSelection ns = new NumericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                        replace(ns, "install NumericSelection from IntSequenceSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with NumericSelection");
                        return ns.execute(base, index, value);
                    }
                }
            }
        }
    }

    // for updates where the index is a numeric (int, double) vector
    public static class NumericSelection extends UpdateVector {

        public NumericSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
            Utils.check(subset);
        }

        public static RArray deleteElements(RList base, RInt index, ASTNode ast, boolean subset) {
            Utils.check(subset);
            boolean hasNegative = false;
            boolean hasPositive = false;
            boolean hasNA = false;
            int bsize = base.size();
            int isize = index.size();
            boolean[] selected = new boolean[bsize];
            int maxIndex = 0;
            int ntrue = 0;

            for (int i = 0; i < isize; i++) {
                int v = index.getInt(i);
                if (v > maxIndex) {
                    maxIndex = v;
                }
                if (v == RInt.NA) {
                    hasNA = true;
                    continue;
                }
                if (v == 0) {
                    continue;
                }
                int vi;
                if (v > 0) {
                    hasPositive = true;
                    vi = v - 1;
                } else {
                    hasNegative = true;
                    vi = -v - 1;
                }
                if (vi < selected.length) {
                    if (!selected[vi]) {
                        ntrue++;
                        selected[vi] = true;
                    }
                }
            }
            Names names = base.names();
            RSymbol[] symbols = names == null ? null : names.sequence();

            if (!hasNegative) {
                int nullsToAdd = 0;
                if (maxIndex > (bsize + 1)) {
                    // there were indexes "above" the base vector, but perhaps not all were mentioned
                    // for all non-mentioned we have to add NULL at the end of the new list
                    final int aboveSize = maxIndex - bsize;
                    boolean[] aboveSelected = new boolean[aboveSize];
                    int natrue = 0;

                    for (int i = 0; i < isize; i++) {
                        int v = index.getInt(i);
                        if (v > bsize) { // note RInt.NA < 0, bsize >= 0
                            int vi = v - bsize - 1;
                            if (!aboveSelected[vi]) {
                                aboveSelected[vi] = true;
                                natrue++;
                            }
                        }
                    }
                    nullsToAdd = aboveSize - natrue;
                }
                int nsize = (bsize - ntrue) + nullsToAdd;
                RAny[] content = new RAny[nsize];
                RSymbol[] nsymbols = symbols == null ? null : new RSymbol[nsize];
                int j = 0;

                for (int i = 0; i < bsize; i++) {
                    if (!selected[i]) { // shallow copy
                        content[j] = base.getRAny(i);
                        if (symbols != null) {
                            nsymbols[j] = symbols[i];
                        }
                        j++;
                    }
                }
                for (int i = 0; i < nullsToAdd; i++) {
                    content[j] = RList.NULL;
                    if (symbols != null) {
                        nsymbols[j] = RSymbol.EMPTY_SYMBOL;
                    }
                    j++;
                }
                int[] dimensions = null;
                if (nsize == bsize && maxIndex <= bsize) {
                    dimensions = base.dimensions();
                }
                return RList.RListFactory.getFor(content, dimensions, nsymbols == null ? null : Names.create(nsymbols));
            } else {
                // hasNegative == true
                if (hasPositive || hasNA) { throw RError.getOnlyZeroMixed(ast); }
                int nsize = ntrue;
                RAny[] content = new RAny[nsize];
                RSymbol[] nsymbols = symbols == null ? null : new RSymbol[nsize];
                int j = 0;

                for (int i = 0; i < bsize; i++) {
                    if (selected[i]) { // shallow copy
                        content[j] = base.getRAny(i);
                        if (symbols != null) {
                            nsymbols[j] = symbols[i];
                        }
                        j++;
                    }
                }
                int[] dimensions = nsize == bsize ? base.dimensions() : null;
                return RList.RListFactory.getFor(content, dimensions, nsymbols == null ? null : Names.create(nsymbols));
            }
        }

        public static RArray genericUpdate(RArray base, RInt index, RArray value, ASTNode ast, boolean subset) {
            Utils.check(subset);
            RArray typedBase;
            RArray typedValue;
            final boolean listBase = base instanceof RList;
            RList listValue = null;
            int[] dimensions;

            if (listBase && value instanceof RNull) {
                return deleteElements((RList) base, index, ast, subset);
            } else if (value instanceof RList) {
                listValue = (RList) value;
                typedValue = null;
                if (listBase) {
                    typedBase = base;
                    dimensions = base.dimensions();
                } else {
                    typedBase = base.asList();
                    dimensions = null;
                }
            } else {
                dimensions = base.dimensions();
                if (listBase) {
                    typedBase = base;
                    listValue = value.asList();
                    typedValue = null;
                } else if (base instanceof RString || value instanceof RString) {
                    typedBase = base.asString();
                    typedValue = value.asString();
                } else if (base instanceof RDouble || value instanceof RDouble) {
                    typedBase = base.asDouble();
                    typedValue = value.asDouble();
                } else if (base instanceof RInt || value instanceof RInt) {
                    typedBase = base.asInt();
                    typedValue = value.asInt();
                } else if (base instanceof RLogical || value instanceof RLogical) {
                    typedBase = base.asLogical();
                    typedValue = value.asLogical();
                } else {
                    Utils.nyi("unsupported vector types");
                    return null;
                }
            }

            boolean hasNegative = false;
            boolean hasPositive = false;
            boolean hasNA = false;
            int bsize = typedBase.size();
            int isize = index.size();
            boolean[] omit = null;
            int maxIndex = 0;

            for (int i = 0; i < isize; i++) {
                int v = index.getInt(i);
                if (v > maxIndex) {
                    maxIndex = v;
                }
                if (v == RInt.NA) {
                    hasNA = true;
                    continue;
                }
                if (v > 0) {
                    hasPositive = true;
                    continue;
                }
                if (v < 0) {
                    if (!hasNegative) {
                        hasNegative = true;
                        omit = new boolean[bsize];
                    }
                    int vi = -v - 1;
                    if (vi < omit.length) {
                        if (!omit[vi]) {
                            omit[vi] = true;
                        }
                    }
                }
            }
            int vsize = typedValue != null ? typedValue.size() : listValue.size();
            if (!hasNegative) {
                int nsize = maxIndex;
                Names names = base.names();
                boolean expanding = false;
                RArray res;
                if (nsize <= bsize) {
                    nsize = bsize;
                    res = Utils.createArray(typedBase, nsize, dimensions, names, base.attributesRef());
                } else {
                    expanding = true;
                    // drop dimensions
                    res = Utils.createArray(typedBase, nsize, names != null).setAttributes(base.attributesRef());
                }

                // FIXME: this may lead to unnecessary computation and copying if the base is a complex view
                int i = 0;
                for (; i < bsize; i++) {
                    res.set(i, typedBase.get(i));
                }
                for (; i < nsize; i++) {
                    Utils.setNA(res, i);
                }
                int j = 0;
                for (i = 0; i < isize; i++) {
                    int v = index.getInt(i);
                    if (v != 0 && v != RInt.NA) {
                        if (typedValue != null) {
                            res.set(v - 1, typedValue.get(j++));
                        } else {
                            res.set(v - 1, listValue.getRAnyRef(j++));
                        }
                        if (j == vsize) {
                            j = 0;
                        }
                    }
                }
                if (j != 0) {
                    RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                }
                if (expanding && names != null) {
                    res = res.setNames(expandNames(names, nsize));
                }
                return res;
            } else {
                // hasNegative == true
                if (hasPositive || hasNA) { throw RError.getOnlyZeroMixed(ast); }
                RArray res = Utils.createArray(typedBase, bsize, dimensions, base.names(), base.attributesRef());
                int j = 0;
                for (int i = 0; i < bsize; i++) {
                    if (omit[i]) {
                        res.set(i, typedBase.get(i));
                    } else {
                        if (typedValue != null) {
                            res.set(i, typedValue.get(j++));
                        } else {
                            res.set(i, listValue.getRAnyRef(j++));
                        }
                        if (j == vsize) {
                            j = 0;
                        }
                    }
                }
                return res;
            }
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing NumericSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                RArray avalue = (RArray) value;
                RInt iindex;
                if (index instanceof RInt) {
                    iindex = (RInt) index;
                } else if (index instanceof RDouble) {
                    iindex = index.asInt();
                } else {
                    throw new UnexpectedResultException(Failure.NOT_NUMERIC_INDEX);
                }
                return genericUpdate(abase, iindex, avalue, ast, subset);
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_UP) Utils.debug("update - NumericSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                replace(gs, "install GenericSelection from NumericSelection");
                if (DEBUG_UP) Utils.debug("update - replaced and re-executing with GenericSelection");
                return gs.execute(base, index, value);
            }
        }
    }

    // for updates where the index is a logical sequence
    // specializes for types (base, value) in simple cases
    // handles also corner cases and when the type of the base changes
    public static class LogicalSelection extends UpdateVector {

        public LogicalSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
            Utils.check(subset);
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing LogicalSelection (uninitialized)");
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized sn = createSimple(base, value);
                if (sn != null) {
                    replace(sn, "specialize LogicalSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with LogicalSelection.Simple");
                    return sn.execute(base, index, value);
                } else {
                    sn = createGeneric();
                    replace(sn, "specialize LogicalSelection");
                    if (DEBUG_UP) Utils.debug("update - replaced and re-executing with LogicalSelection.Generic");
                    return sn.execute(base, index, value);
                }
            }
        }

        abstract class ValueCopy {
            abstract RAny copy(RArray base, RLogical index, RAny value) throws UnexpectedResultException;
        }

        public Specialized createSimple(RAny baseTemplate, RAny valueTemplate) {
            if (baseTemplate instanceof RList) {
                if (valueTemplate instanceof RList || valueTemplate instanceof RDouble || valueTemplate instanceof RLogical || valueTemplate instanceof RInt) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, RLogical index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RList)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RList typedBase = (RList) base;
                            RList typedValue;
                            if (value instanceof RList) {
                                typedValue = (RList) value;
                            } else if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) {
                                typedValue = value.asList();
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int isize = index.size();
                            if (isize > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int vsize = typedValue.size();
                            RAny[] content = new RAny[bsize];
                            int ii = 0;
                            int vi = 0;
                            boolean hasNA = false;
                            for (int bi = 0; bi < bsize; bi++) {
                                int v = index.getLogical(ii);
                                ii++;
                                if (ii == isize) {
                                    ii = 0;
                                }
                                if (v == RLogical.TRUE) { // shallow copy
                                    content[bi] = typedValue.getRAnyRef(vi);
                                    vi++;
                                    if (vi == vsize) {
                                        vi = 0;
                                    }
                                    continue;
                                }
                                if (v == RLogical.NA) {
                                    hasNA = true;
                                }
                                content[bi] = typedBase.getRAny(bi); // shallow copy
                            }
                            if (hasNA && vsize >= 2) { throw RError.getNASubscripted(ast); }
                            if (vi != 0) {
                                RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                            }
                            return RList.RListFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RDouble,RList|RDouble|RInt|RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RDouble) {
                if (valueTemplate instanceof RDouble || valueTemplate instanceof RLogical || valueTemplate instanceof RInt) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, RLogical index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RDouble typedBase = (RDouble) base;
                            RDouble typedValue;
                            if (value instanceof RDouble) {
                                typedValue = (RDouble) value;
                            } else if (value instanceof RInt || value instanceof RLogical) {
                                typedValue = value.asDouble();
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int isize = index.size();
                            if (isize > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int vsize = typedValue.size();
                            double[] content = new double[bsize];
                            int ii = 0;
                            int vi = 0;
                            boolean hasNA = false;
                            for (int bi = 0; bi < bsize; bi++) {
                                int v = index.getLogical(ii);
                                ii++;
                                if (ii == isize) {
                                    ii = 0;
                                }
                                if (v == RLogical.TRUE) {
                                    content[bi] = typedValue.getDouble(vi);
                                    vi++;
                                    if (vi == vsize) {
                                        vi = 0;
                                    }
                                    continue;
                                }
                                if (v == RLogical.NA) {
                                    hasNA = true;
                                }
                                content[bi] = typedBase.getDouble(bi);
                            }
                            if (hasNA && vsize >= 2) { throw RError.getNASubscripted(ast); }
                            if (vi != 0) {
                                RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                            }
                            return RDouble.RDoubleFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RDouble,RDouble|RInt|RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RInt) {
                if (valueTemplate instanceof RLogical || valueTemplate instanceof RInt) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, RLogical index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RInt)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RInt typedBase = (RInt) base;
                            RInt typedValue;
                            if (value instanceof RInt) {
                                typedValue = (RInt) value;
                            } else if (value instanceof RLogical) {
                                typedValue = value.asInt();
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int isize = index.size();
                            if (isize > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int vsize = typedValue.size();
                            int[] content = new int[bsize];
                            int ii = 0;
                            int vi = 0;
                            boolean hasNA = false;
                            for (int bi = 0; bi < bsize; bi++) {
                                int v = index.getLogical(ii);
                                ii++;
                                if (ii == isize) {
                                    ii = 0;
                                }
                                if (v == RLogical.TRUE) {
                                    content[bi] = typedValue.getInt(vi);
                                    vi++;
                                    if (vi == vsize) {
                                        vi = 0;
                                    }
                                    continue;
                                }
                                if (v == RLogical.NA) {
                                    hasNA = true;
                                }
                                content[bi] = typedBase.getInt(bi);
                            }
                            if (hasNA && vsize >= 2) { throw RError.getNASubscripted(ast); }
                            if (vi != 0) {
                                RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                            }
                            return RInt.RIntFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RInt,RInt|RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RLogical) {
                if (valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, RLogical index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RLogical)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RLogical typedBase = (RLogical) base;
                            RLogical typedValue;
                            if (value instanceof RLogical) {
                                typedValue = (RLogical) value;
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int isize = index.size();
                            if (isize > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int vsize = typedValue.size();
                            int[] content = new int[bsize];
                            int ii = 0;
                            int vi = 0;
                            boolean hasNA = false;
                            for (int bi = 0; bi < bsize; bi++) {
                                int v = index.getLogical(ii);
                                ii++;
                                if (ii == isize) {
                                    ii = 0;
                                }
                                if (v == RLogical.TRUE) {
                                    content[bi] = typedValue.getLogical(vi);
                                    vi++;
                                    if (vi == vsize) {
                                        vi = 0;
                                    }
                                    continue;
                                }
                                if (v == RLogical.NA) {
                                    hasNA = true;
                                }
                                content[bi] = typedBase.getLogical(bi);
                            }
                            if (hasNA && vsize >= 2) { throw RError.getNASubscripted(ast); }
                            if (vi != 0) {
                                RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                            }
                            return RLogical.RLogicalFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RLogical,RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RString) {
                if (valueTemplate instanceof RString) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override RAny copy(RArray base, RLogical index, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RString)) { throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE); }
                            RString typedBase = (RString) base;
                            RString typedValue;
                            if (value instanceof RString) {
                                typedValue = (RString) value;
                            } else {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            int bsize = base.size();
                            int isize = index.size();
                            if (isize > bsize) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                            int vsize = typedValue.size();
                            String[] content = new String[bsize];
                            int ii = 0;
                            int vi = 0;
                            boolean hasNA = false;
                            for (int bi = 0; bi < bsize; bi++) {
                                int v = index.getLogical(ii);
                                ii++;
                                if (ii == isize) {
                                    ii = 0;
                                }
                                if (v == RLogical.TRUE) {
                                    content[bi] = typedValue.getString(vi);
                                    vi++;
                                    if (vi == vsize) {
                                        vi = 0;
                                    }
                                    continue;
                                }
                                if (v == RLogical.NA) {
                                    hasNA = true;
                                }
                                content[bi] = typedBase.getString(bi);
                            }
                            if (hasNA && vsize >= 2) { throw RError.getNASubscripted(ast); }
                            if (vi != 0) {
                                RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                            }
                            return RString.RStringFactory.getFor(content, base.dimensions(), base.names());
                        }
                    };
                    return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<RString,RString>");
                }
                return null;
            }
            return null;
        }

        public static RAny deleteElements(RList base, RLogical index, ASTNode ast) {
            int bsize = base.size();
            int isize = index.size();

            Names names = base.names();
            RSymbol[] symbols = names == null ? null : names.sequence();
            if (isize == bsize) {
                int ntrue = RLogical.RLogicalUtils.truesInRange(index, 0, isize);
                int nsize = bsize - ntrue;
                RAny[] content = new RAny[nsize];
                RSymbol[] nsymbols = symbols == null ? null : new RSymbol[nsize];
                int j = 0;
                for (int i = 0; i < bsize; i++) {
                    int l = index.getLogical(i);
                    if (l != RLogical.TRUE) { // shallow copy
                        content[j] = base.getRAny(i);
                        if (symbols != null) {
                            nsymbols[j] = symbols[i];
                        }
                        j++;
                    }
                }
                return RList.RListFactory.getFor(content, ntrue != 0 ? null : base.dimensions(), nsymbols == null ? null : Names.create(nsymbols));
            }
            if (isize > bsize) {
                // for each "non-TRUE" element above base vector size we have to add NULL to the vector
                int ntrue = RLogical.RLogicalUtils.truesInRange(index, 0, bsize);
                int natrue = RLogical.RLogicalUtils.truesInRange(index, bsize, isize);
                int nullsToAdd = isize - bsize - natrue;
                int nsize = (bsize - ntrue) + nullsToAdd;
                RAny[] content = new RAny[nsize];
                RSymbol[] nsymbols = symbols == null ? null : new RSymbol[nsize];
                int j = 0;
                for (int i = 0; i < bsize; i++) {
                    int l = index.getLogical(i);
                    if (l != RLogical.TRUE) { // shallow copy
                        content[j] = base.getRAny(i);
                        if (symbols != null) {
                            nsymbols[j] = symbols[i];
                        }
                        j++;
                    }
                }
                for (int i = 0; i < nullsToAdd; i++) {
                    content[j] = RList.NULL;
                    if (symbols != null) {
                        nsymbols[j] = RSymbol.EMPTY_SYMBOL;
                    }
                    j++;
                }
                return RList.RListFactory.getFor(content, bsize != nsize ? null : base.dimensions(), nsymbols == null ? null : Names.create(nsymbols));
            }
            // isize < bsize
            if (isize == 0) { return base; }
            int rep = bsize / isize;
            int lsize = bsize - rep * isize;
            int ntrue = RLogical.RLogicalUtils.truesInRange(index, 0, isize);
            int nltrue = RLogical.RLogicalUtils.truesInRange(index, 0, lsize); // TRUEs in the last cycle of index over base

            int nsize = bsize - (ntrue * rep + nltrue);
            RAny[] content = new RAny[nsize];
            RSymbol[] nsymbols = symbols == null ? null : new RSymbol[nsize];
            int ii = 0;
            int ci = 0;
            for (int bi = 0; bi < bsize; bi++) {
                int l = index.getLogical(ii++);
                if (ii == isize) {
                    ii = 0;
                }
                if (l != RLogical.TRUE) { // shallow copy
                    content[ci] = base.getRAny(bi);
                    if (symbols != null) {
                        nsymbols[ci] = symbols[bi];
                    }
                    ci++;
                }
            }
            return RList.RListFactory.getFor(content, bsize != nsize ? null : base.dimensions(), nsymbols == null ? null : Names.create(nsymbols));
        }

        public static RAny genericUpdate(RArray base, RLogical index, RAny value, ASTNode ast) {
            RArray typedBase;
            RArray typedValue;
            RList listValue = null;
            int[] dimensions;

            if (base instanceof RList && value instanceof RNull) {
                return deleteElements((RList) base, index, ast);
            } else if (value instanceof RList) {
                listValue = (RList) value;
                typedValue = null;
                if (base instanceof RList) {
                    typedBase = base;
                    dimensions = base.dimensions();
                } else {
                    typedBase = base.asList();
                    dimensions = null;
                }
            } else {
                dimensions = base.dimensions();
                if (base instanceof RList) {
                    typedBase = base;
                    listValue = value.asList();
                    typedValue = null;
                } else if (base instanceof RString || value instanceof RString) {
                    typedBase = base.asString();
                    typedValue = value.asString();
                } else if (base instanceof RDouble || value instanceof RDouble) {
                    typedBase = base.asDouble();
                    typedValue = value.asDouble();
                } else if (base instanceof RInt || value instanceof RInt) {
                    typedBase = base.asInt();
                    typedValue = value.asInt();
                } else if (base instanceof RLogical || value instanceof RLogical) {
                    typedBase = base.asLogical();
                    typedValue = value.asLogical();
                } else {
                    throw Utils.nyi("unsupported vector types");
                }
            }
            int bsize = base.size();
            int isize = index.size();
            int vsize = typedValue != null ? typedValue.size() : listValue.size();
            int nsize;
            RArray res;
            Names names = base.names();
            boolean expanding;
            if (isize <= bsize) {
                nsize = bsize;
                expanding = false;
                res = Utils.createArray(typedBase, nsize, dimensions, names, base.attributesRef());
            } else {
                expanding = true;
                // drop dimensions
                nsize = isize;
                res = Utils.createArray(typedBase, nsize, names != null).setAttributes(base.attributesRef());
            }
            int ii = 0;
            int vi = 0;
            boolean hasNA = false;
            for (int ni = 0; ni < nsize; ni++) {
                int v = index.getLogical(ii);
                ii++;
                if (ii == isize) {
                    ii = 0;
                }
                if (v == RLogical.TRUE) {
                    if (typedValue != null) {
                        res.set(ni, typedValue.get(vi));
                    } else {
                        res.set(ni, listValue.get(vi));
                    }
                    vi++;
                    if (vi == vsize) {
                        vi = 0;
                    }
                    continue;
                }
                if (v == RLogical.NA) {
                    hasNA = true;
                }
                if (ni < bsize) {
                    res.set(ni, typedBase.get(ni));
                } else {
                    Utils.setNA(res, ni);
                }
            }
            if (hasNA && vsize >= 2) { throw RError.getNASubscripted(ast); }
            if (vi != 0) {
                RContext.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
            }
            if (expanding && names != null) {
                res = res.setNames(expandNames(names, nsize));
            }
            return res;
        }

        public Specialized createGeneric() {
            ValueCopy cpy = new ValueCopy() {
                @Override RAny copy(RArray base, RLogical index, RAny value) {
                    return genericUpdate(base, index, value, ast);
                }
            };
            return new Specialized(ast, isSuper, var, lhs, indexes, rhs, subset, cpy, "<Generic>");
        }

        class Specialized extends LogicalSelection {
            final ValueCopy copy;
            final String dbg;

            Specialized(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset, ValueCopy copy, String dbg) {
                super(ast, isSuper, var, lhs, indexes, rhs, subset);
                this.copy = copy;
                this.dbg = dbg;
            }

            @Override public RAny execute(RAny base, RAny index, RAny value) {
                if (DEBUG_UP) Utils.debug("update - executing LogicalSelection" + dbg);
                try {
                    if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                    RArray abase = (RArray) base;
                    if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                    RArray avalue = (RArray) value;
                    if (!(index instanceof RLogical)) { throw new UnexpectedResultException(Failure.NOT_LOGICAL_INDEX); }
                    RLogical lindex = (RLogical) index;
                    return copy.copy(abase, lindex, avalue);
                } catch (UnexpectedResultException e) {
                    Failure f = (Failure) e.getResult();
                    if (DEBUG_UP) Utils.debug("update - LogicalSelection" + dbg + " failed: " + f);
                    switch (f) {
                    case INDEX_OUT_OF_BOUNDS:
                    case UNEXPECTED_TYPE:
                        Specialized sn = createGeneric();
                        replace(sn, "specialize LogicalSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with LogicalSelection.Generic");
                        return sn.execute(base, index, value);

                    default:
                        GenericSelection gs = new GenericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                        replace(gs, "install GenericSelection from LogicalSelection");
                        if (DEBUG_UP) Utils.debug("update - replaced and re-executing with GenericSelection");
                        return gs.execute(base, index, value);
                    }
                }
            }
        }
    }

    public static class StringSelection {

        public static RAny deleteElements(RList base, RString index, ASTNode ast) {
            Names bnames = base.names();
            if (bnames == null) { return base; }
            int bsize = base.size();
            int isize = index.size();
            boolean[] remove = new boolean[bsize];
            int nremove = 0;

            for (int i = 0; i < isize; i++) {
                RSymbol s = RSymbol.getSymbol(index.getString(i));
                int v = bnames.map(s);
                if (v != -1) {
                    if (!remove[v]) {
                        remove[v] = true;
                        nremove++;
                    }
                }
            }

            if (nremove == 0) { return base; }
            int nsize = bsize - nremove;
            RSymbol[] bsymbols = bnames.sequence();
            RSymbol[] nsymbols = new RSymbol[nsize];
            RAny[] content = new RAny[nsize];
            int j = 0;
            for (int i = 0; i < bsize; i++) {
                if (!remove[i]) { // shallow copy
                    content[j] = base.getRAny(i);
                    nsymbols[j] = bsymbols[i];
                    j++;
                }
            }
            return RList.RListFactory.getFor(content, null, Names.create(nsymbols));
        }

        public static RAny genericUpdate(RArray base, RString index, RAny value, ASTNode ast) {

            int isize = index.size();
            if (isize == 1) {
                // this version is faster
                return ScalarStringSelection.genericUpdate(base, index.getString(0), value, true, ast);
            }
            RArray typedBase;
            RArray typedValue;
            RList listValue = null;

            if (base instanceof RList && value instanceof RNull) {
                return deleteElements((RList) base, index, ast);
            } else if (value instanceof RList) {
                listValue = (RList) value;
                typedValue = null;
                if (base instanceof RList) {
                    typedBase = base;
                } else {
                    typedBase = base.asList();
                }
            } else {
                if (base instanceof RList) {
                    typedBase = base;
                    listValue = value.asList();
                    typedValue = null;
                } else if (base instanceof RString || value instanceof RString) {
                    typedBase = base.asString();
                    typedValue = value.asString();
                } else if (base instanceof RDouble || value instanceof RDouble) {
                    typedBase = base.asDouble();
                    typedValue = value.asDouble();
                } else if (base instanceof RInt || value instanceof RInt) {
                    typedBase = base.asInt();
                    typedValue = value.asInt();
                } else if (base instanceof RLogical || value instanceof RLogical) {
                    typedBase = base.asLogical();
                    typedValue = value.asLogical();
                } else {
                    Utils.nyi("unsupported vector types");
                    return null;
                }
            }
            int bsize = base.size();
            int vsize = typedValue != null ? typedValue.size() : listValue.size();

            // note that the base can have duplicate names, but these cannot be
            // created using update vector (like here),
            // only e.g. using names<-

            Names bnames = base.names();
            RSymbol[] bsymbols;
            HashMap<RSymbol, Integer> nmap;
            if (bnames == null) {
                nmap = new HashMap<>(bsize);
                bsymbols = null;
            } else {
                nmap = bnames.getMap();
                if (bnames.keepsMap()) {
                    nmap = new HashMap<RSymbol, Integer>(nmap);
                }
                bsymbols = bnames.sequence();
            }

            RSymbol[] addSymbols = new RSymbol[isize];
            int j = 0;
            int firstOverwrite = -1;
            int noverwrites = 0;
            int[] targetOffsets = null;
            for (int i = 0; i < isize; i++) {
                RSymbol name = RSymbol.getSymbol(index.getString(i));
                Integer prevOffset = nmap.get(name);
                if (prevOffset == null) {
                    nmap.put(name, i);
                    addSymbols[j++] = name;
                } else {
                    if (firstOverwrite == -1) {
                        firstOverwrite = i;
                        targetOffsets = new int[isize - i];
                    }
                    noverwrites++;
                    targetOffsets[i - firstOverwrite] = prevOffset.intValue();
                }
            }

            int addSize = isize - noverwrites;
            int nsize = bsize + addSize;

            Names nnames;
            if (bsymbols == null) {
                nnames = Names.create(addSymbols, nmap);
            } else {
                if (addSize == 0) {
                    nnames = bnames;
                } else {
                    RSymbol[] nsymbols = new RSymbol[nsize];
                    System.arraycopy(bsymbols, 0, nsymbols, 0, bsize);
                    System.arraycopy(addSymbols, 0, nsymbols, bsize, addSize);
                    nnames = Names.create(nsymbols, nmap);
                }
            }
            RArray res = Utils.createArray(typedBase, nsize, null, nnames, base.attributesRef());
            for (int bi = 0; bi < bsize; bi++) {
                res.set(bi, typedBase.get(bi));
            }

            int vi = 0;
            int ii = 0;
            if (noverwrites == 0) {
                for (; ii < isize; ii++) {
                    // TODO will the ? check be lifted out of the loop, or
                    // should it be done explicitly
                    res.set(bsize + ii, typedValue != null ? typedValue.get(vi) : listValue.get(vi));
                    vi++;
                    if (vi == vsize) {
                        vi = 0;
                    }
                }
            } else {
                // some overwrites (either update of an existing field, or a duplicate name in the update vector)
                for (; ii < isize; ii++) {
                    int ni;
                    if (ii < firstOverwrite) {
                        ni = bsize + ii;
                    } else {
                        ni = targetOffsets[ii - firstOverwrite];
                    }
                    // TODO will the ? check be lifted out of the loop, or should it be done explicitly
                    res.set(ni, typedValue != null ? typedValue.get(vi) : listValue.get(vi));
                    vi++;
                    if (vi == vsize) {
                        vi = 0;
                    }
                }
            }
            return res;
        }
    }

    // when the index is a vector of integers (selection by index)
    // and the base can be recursive
    // and the mode is subscript ([[.]])
    public static class Subscript extends UpdateVector {
        public Subscript(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
            Utils.check(!subset);
        }

        public static RAny executeSubscript(RInt index, RArray base, RArray value, ASTNode ast) {
            // FIXME: check handling of dimensions here
            final int isize = index.size();
            if (isize == 0) { throw RError.getSelectLessThanOne(ast); }
            int i = 0;
            RAny b = base;
            RAny res = null;
            RList parent = null;
            int parentIndex = -1;
            boolean onSharedPath = false;
            if (isize > 1) {
                for (; i < isize - 1; i++) { // shallow copy
                    if (!(b instanceof RList)) { throw RError.getMoreElementsSupplied(ast); }
                    RList l = (RList) b;
                    int indexv = index.getInt(i);
                    int bsize = l.size();
                    int isel = ReadVector.Subscript.convertDereferencingIndex(indexv, i, bsize, ast);

                    if (l.isShared()) {
                        onSharedPath = true;
                    }
                    if (onSharedPath) {
                        RAny[] content = new RAny[bsize];
                        int k = 0;
                        int j = 0;
                        for (; j < isel; j++) { // shallow copy
                            content[k++] = l.getRAnyRef(j);
                        }
                        j++; // skip
                        k++;
                        for (; j < bsize; j++) { // shallow copy
                            content[k++] = l.getRAnyRef(j);
                        }
                        RList newList = RList.RListFactory.getFor(content, l.dimensions(), l.names());
                        // FIXME: this copy can be unnecessary
                        if (parent != null) {
                            parent.set(parentIndex, newList);
                        } else {
                            res = newList;
                        }
                        parent = newList;
                        b = l.getRAnyRef(isel); // shallow copy
                    } else {
                        if (parent == null) {
                            res = l;
                        }
                        parent = l;
                        b = l.getRAny(isel); // shallow copy
                    }
                    parentIndex = isel;

                }
            }
            // selection at the last level
            int indexv = index.getInt(i);
            if (!(b instanceof RArray)) {
                Utils.nyi("unuspported base type");
            }
            RArray a = (RArray) b;
            if (value instanceof RNull) {
                if (a instanceof RList) {
                    b = GenericScalarSelection.deleteElement((RList) a, indexv, ast, false);
                } else {
                    throw RError.getMoreElementsSupplied(ast);
                }
            } else {
                if (value.size() > 1) {
                    throw RError.getMoreElementsSupplied(ast);
                } else {
                    b = ScalarNumericSelection.genericUpdate(a, indexv, value, false, ast);
                }
            }
            if (parent == null) {
                return b;
            } else {
                parent.set(parentIndex, b);
                return res;
            }
        }

        public static RAny executeSubscript(RString index, RArray base, RArray value, ASTNode ast) {
            final int isize = index.size();
            if (isize == 0) { throw RError.getSelectLessThanOne(ast); }
            int i = 0;
            RAny b = base;
            RAny res = null;
            RList parent = null;
            int parentIndex = -1;
            if (isize > 1) {
                for (; i < isize - 1; i++) { // shallow copy
                    if (!(b instanceof RList)) { throw RError.getMoreElementsSupplied(ast); }
                    RList l = (RList) b;
                    Names names = l.names();
                    if (names == null) { throw RError.getNoSuchIndexAtLevel(ast, i + 1); }
                    RSymbol s = RSymbol.getSymbol(index.getString(i));
                    int indexv = names.map(s);
                    if (indexv == RInt.NA) { throw RError.getNoSuchIndexAtLevel(ast, i + 1); }
                    int bsize = l.size();
                    RAny[] content = new RAny[bsize];
                    int k = 0;
                    int j = 0;
                    for (; j < indexv; j++) { // shallow copy
                        content[k++] = l.getRAny(j);
                    }
                    j++; // skip
                    k++;
                    for (; j < bsize; j++) { // shallow copy
                        content[k++] = l.getRAny(j);
                    }
                    RList newList = RList.RListFactory.getFor(content, l.dimensions(), l.names());
                    if (parent != null) {
                        parent.set(parentIndex, newList);
                    } else {
                        res = newList;
                    }
                    parent = newList;
                    parentIndex = indexv;
                    b = l.getRAny(indexv); // shallow copy
                }
            }
            // selection at the last level
            if (!(b instanceof RArray)) { throw Utils.nyi("unuspported base type"); }
            RArray a = (RArray) b;
            if (value instanceof RNull) {
                if (a instanceof RList) {
                    b = GenericScalarSelection.deleteElement((RList) a, index.getString(i));
                    // TODO: call directly a method for string index
                } else {
                    throw RError.getMoreElementsSupplied(ast);
                }
            } else {
                if (value.size() > 1) {
                    throw RError.getMoreElementsSupplied(ast);
                } else {
                    b = ScalarStringSelection.genericUpdate(a, index.getString(i), value, false, ast);
                    // FIXME: ScalarNumericSelection.genericUpdate is  unnecessarily heavy-weight for a valid positive index
                }
            }
            if (parent == null) {
                return b;
            } else {
                parent.set(parentIndex, b);
                return res;
            }
        }

        public static RAny executeSubscript(RAny index, RArray base, RArray value, ASTNode ast) {
            if (index instanceof RInt || index instanceof RDouble || index instanceof RLogical) { return executeSubscript(index.asInt(), base, value, ast); }
            if (index instanceof RString) { return executeSubscript((RString) index, base, value, ast); }
            throw RError.getInvalidSubscriptType(ast, index.typeOf());
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing Subscript");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                RArray avalue = (RArray) value;
                return executeSubscript(index, abase, avalue, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_UP) Utils.debug("update - Subscript failed: " + f);
                GenericSelection gs = new GenericSelection(ast, isSuper, var, lhs, indexes, rhs, subset);
                // rewriting itself only to handle the error, there is no way to recover
                replace(gs, "install GenericSelection from Subscript");
                if (DEBUG_UP) Utils.debug("update - replaced and re-executing with GenericSelection");
                return gs.execute(base, index, value);
            }
        }
    }

    // handles any update, won't rewrite itself
    public static class GenericSelection extends UpdateVector {

        public GenericSelection(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, isSuper, var, lhs, indexes, rhs, subset);
        }

        @Override public RAny execute(RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing GenericSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (!(index instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_INDEX); }
                RArray aindex = (RArray) index;
                int isize = aindex.size();
                if (value instanceof RNull) {
                    if (isize == 1) {
                        return GenericScalarSelection.deleteElement(abase, aindex, ast, subset);
                    } else {
                        if (!(abase instanceof RList)) { throw RError.getReplacementZero(ast); }
                        RList lbase = (RList) abase;
                        if (aindex instanceof RDouble || aindex instanceof RInt) {
                            return NumericSelection.deleteElements(lbase, aindex.asInt(), ast, true);
                        } else if (aindex instanceof RLogical) {
                            return LogicalSelection.deleteElements(lbase, index.asLogical(), ast);
                        } else if (aindex instanceof RString) {
                            return StringSelection.deleteElements(lbase, (RString) index, ast);
                        } else if (aindex instanceof RNull) {
                            return lbase;
                        } else {
                            throw Utils.nyi("unsupported update");
                        }
                    }
                }
                if (!(value instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE); }
                RArray avalue = (RArray) value;
                if (!subset && isize == 1) { return GenericScalarSelection.update(abase, aindex, avalue, ast, subset); }

                if (subset) {
                    if (aindex instanceof RDouble || aindex instanceof RInt) {
                        return NumericSelection.genericUpdate(abase, aindex.asInt(), avalue, ast, true);
                    } else if (aindex instanceof RLogical) {
                        return LogicalSelection.genericUpdate(abase, index.asLogical(), avalue, ast);
                    } else if (aindex instanceof RString) {
                        return StringSelection.genericUpdate(abase, (RString) index, avalue, ast);
                    } else {
                        throw Utils.nyi("unsupported update");
                    }
                } else {
                    return Subscript.executeSubscript(aindex, abase, avalue, ast);
                }
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_UP) Utils.debug("update - GenericSelection failed: " + f);
                throw Utils.nyi("unsupported update");
            }
        }
    }

    // Dollar selection operator
    // ---------------------------------------------------------------------------------------

    /**
     * Base class for all dollar selection ($) assignments. Defines final methods for updates, updates in place and
     * appends of lists, typecasts to lists, position checking, etc. This class does not define the execute method and
     * only acts as a common codebase for its descendants, where the DollarListUpdate is the root of the hierarchy.
     */
    public abstract static class DollarUpdateBase extends UpdateVector {

        DollarUpdateBase(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode index, RNode rhs) {
            super(ast, isSuper, var, lhs, new RNode[]{index}, rhs, false);
        }

        DollarUpdateBase(DollarUpdateBase from) {
            super(from);
        }

        /**
         * Converts the given base to list, emitting the warning about coercion for R compatibility.
         */
        protected final RList convertToList(RAny base) {
            RContext.warning(ast, RError.COERCING_LHS_TO_LIST);
            return base.asList();
        }

        /**
         * Returns the position of the given symbol in the specified array names, or -1 if no such name exists in the
         * array.
         */
        protected static int elementPos(RArray.Names names, RSymbol idx) {
            return (names == null) ? -1 : names.map(idx);
        }

        /**
         * Appends the given value to the list under specified name.
         */
        protected static RAny appendToList(RArray base, RArray.Names names, int size, RAny value, RSymbol idx) {
            // if names not empty, create them
            RArray.Names myNames = names;
            if (myNames == null) {
                myNames = RArray.Names.create(size);
            }
            RArray res = Utils.createArray(base, size + 1, base.dimensions(), UpdateVector.appendName(myNames, idx), base.attributesRef());
            for (int i = 0; i < size; ++i) {
                res.set(i, base.get(i));
            }
            res.set(size, value);
            return res;
        }

        /**
         * Creates a copy of the given list and then updates the specified position in it.
         */
        protected static RAny updateList(RArray base, RArray.Names names, int size, RAny value, int pos) {
            RArray res = Utils.createArray(base, size, base.dimensions(), names, base.attributesRef());
            for (int i = 0; i < pos; ++i) {
                res.set(i, base.get(i));
            }
            for (int i = pos + 1; i < size; ++i) {
                res.set(i, base.get(i));
            }
            return updateListInPlace(base, value, pos);
        }

        /**
         * Updates the given list in place - its specified position is rewritten to the supplied value and the same list
         * is returned.
         */
        protected static RAny updateListInPlace(RArray base, RAny value, int pos) {
            base.set(pos, value);
            return base;
        }
    }

    /**
     * Fast update of a non shared list. This class assumes that it has (a) a list, (b) it must update the list, not
     * append to it, and (c) the list is not shared, in which case performs the operation. Otherwise rewrites itself to
     * either DollarUpdate if not a list, DollarSharedListUpdate if not shared update and DollarListAppend.
     */
    public static class DollarListUpdate extends DollarUpdateBase {

        static enum Failure {
            NOT_A_LIST, SHARED_UPDATE, NOT_AN_UPDATE,
        }

        public DollarListUpdate(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode index, RNode rhs) {
            super(ast, isSuper, var, lhs, index, rhs);
        }

        public DollarListUpdate(DollarUpdateBase from) {
            super(from);
        }

        /**
         * Performs in place update of a list, or rewrites itself to the appropriate nodes.
         */
        @Override RAny execute(RAny base, RAny index, RAny value) {
            assert (index instanceof ScalarStringImpl) : "this assumes we always have a constant";
            try {
                if (!(base instanceof RList)) { throw new UnexpectedResultException(Failure.NOT_A_LIST); }
                RList list = (RList) base;
                RArray.Names names = list.names();
                RSymbol idx = RSymbol.getSymbol(((RString) index).getString(0));
                int pos = elementPos(names, idx);
                if (pos == -1) { throw new UnexpectedResultException(Failure.NOT_AN_UPDATE); }
                if (list.isShared()) { throw new UnexpectedResultException(Failure.SHARED_UPDATE); }
                return updateListInPlace(list, value, pos);
            } catch (UnexpectedResultException e) {
                DollarUpdateBase x;
                switch ((Failure) e.getResult()) {
                case NOT_A_LIST:
                    x = new DollarUpdate(this);
                    replace(x, "not a list in assignment");
                    return x.execute(base, index, value);
                case SHARED_UPDATE:
                    x = new DollarSharedListUpdate(this);
                    replace(x, "update of a shared list");
                    return x.execute(base, index, value);
                case NOT_AN_UPDATE:
                    x = new DollarListAppend(this);
                    replace(x, "list append");
                    return x.execute(base, index, value);
                }
            }
            assert (false);
            return null;
        }
    }

    /**
     * Performs an update of a shared list, or an update of a non-shared list without rewriting itself, rewrites to
     * append instead of update, or to perform the general operation with coercion to list.
     */
    public static class DollarSharedListUpdate extends DollarUpdateBase {

        static enum Failure {
            NOT_A_LIST, NOT_AN_UPDATE,
        }

        public DollarSharedListUpdate(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode index, RNode rhs) {
            super(ast, isSuper, var, lhs, index, rhs);
        }

        public DollarSharedListUpdate(DollarUpdateBase from) {
            super(from);
        }

        /**
         * Updates shared list while first copying it, or non-shared list in place. Rewrites to general case or to
         * append instead of update.
         */
        @Override RAny execute(RAny base, RAny index, RAny value) {
            assert (index instanceof ScalarStringImpl) : "this assumes we always have a constant";
            try {
                if (!(base instanceof RList)) { throw new UnexpectedResultException(Failure.NOT_A_LIST); }
                RList list = (RList) base;
                RArray.Names names = list.names();
                RSymbol idx = RSymbol.getSymbol(((RString) index).getString(0));
                int size = list.size();
                int pos = elementPos(names, idx);
                if (pos == -1) { throw new UnexpectedResultException(Failure.NOT_AN_UPDATE); }
                if (list.isShared()) {
                    return updateList(list, names, size, value, pos);
                } else {
                    return updateListInPlace(list, value, pos);
                }
            } catch (UnexpectedResultException e) {
                DollarUpdateBase x;
                switch ((Failure) e.getResult()) {
                case NOT_A_LIST:
                    x = new DollarUpdate(this);
                    replace(x, "not a list in assignment");
                    return x.execute(base, index, value);
                case NOT_AN_UPDATE:
                    x = new DollarListAppend(this);
                    replace(x, "list append");
                    return x.execute(base, index, value);
                }
            }
            assert (false);
            return null;
        }
    }

    /**
     * Appends given list (shared or non shared). If not append, or not a list rewrites to the general case
     * DollarUpdate.
     */
    public static class DollarListAppend extends DollarUpdateBase {

        public DollarListAppend(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode index, RNode rhs) {
            super(ast, isSuper, var, lhs, index, rhs);
        }

        public DollarListAppend(DollarUpdateBase from) {
            super(from);
        }

        /**
         * Performs the update or overwrites itself to the general case.
         */
        @Override RAny execute(RAny base, RAny index, RAny value) {
            assert (index instanceof ScalarStringImpl) : "this assumes we always have a constant";
            try {
                if (!(base instanceof RList)) { throw new UnexpectedResultException(null); }
                RList list = (RList) base;
                RArray.Names names = list.names();
                RSymbol idx = RSymbol.getSymbol(((RString) index).getString(0));
                int size = list.size();
                int pos = elementPos(names, idx);
                if (pos != -1) { throw new UnexpectedResultException(null); }
                return appendToList(list, names, size, value, idx);
            } catch (UnexpectedResultException e) {
                DollarUpdateBase x = new DollarUpdate(this);
                replace(x, "not a list or not append in assignment");
                return x.execute(base, index, value);
            }
        }
    }

    /**
     * General update/append on a list/vector. Coerces the input type to a list if required.
     */
    public static class DollarUpdate extends DollarUpdateBase {

        public DollarUpdate(ASTNode ast, boolean isSuper, RSymbol var, RNode lhs, RNode index, RNode rhs) {
            super(ast, isSuper, var, lhs, index, rhs);
        }

        public DollarUpdate(DollarUpdateBase from) {
            super(from);
        }

        // / TODO Are the specializations for the fast stuff worth it? This code
        // looks smaller than the code with many rewrite possibilities
        @Override RAny execute(RAny base, RAny index, RAny value) {
            assert (index instanceof ScalarStringImpl) : "this assumes we always have a constant";
            RArray list = (base instanceof RList) ? (RList) base : convertToList(base);
            RArray.Names names = list.names();
            RSymbol idx = RSymbol.getSymbol(((RString) index).getString(0));
            int size = list.size();
            int pos = elementPos(names, idx);
            if (pos == -1) {
                return appendToList(list, names, size, value, idx);
            } else {
                if (base.isShared()) {
                    return updateList(list, names, size, value, pos);
                } else {
                    return updateListInPlace(list, value, pos);
                }
            }
        }
    }
}
