package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.RArray.Names;
import r.data.internal.*;
import r.data.internal.IntImpl.RIntSequence;
import r.errors.*;
import r.nodes.*;

// FIXME: add check for the number of dimensions in index
// FIXME: probably should also specialize for base types
// FIXME: get get a bit more performance by joining failure modes when distinction is not necessary (and particularly when that makes the checks easier)
//        i've done this with SimpleScalarDoubleSelection, and it helped a bit

// FIXME: consider adding Select specialization to SimpleIntScalarSelection, but now disabled even in SimpleDoubleScalarSelection as it is not good for the
//        binarytrees benchmark

// FIXME: add more support for constant vector indices
// FIXME: check for scalar types instead of type & size (in SimpleScalarXXX)

// rewriting of vector selection nodes:
//
// DoubleBaseSimpleSelection.ScalarDoubleSelection -> DoubleBaseSimpleSelection.ScalarIntSelection -> (ctd below)
//
// >SimpleScalarIntSelection   -> SimpleScalarDoubleSelection -> GenericScalarSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> SimpleLogicalSelection -> LogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> SimpleLogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> Subscript -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> Subscript -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> SimpleScalarStringSelection -> GenericScalarSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> SimpleScalarStringSelection -> GenericScalarSelection -> SimpleLogicalSelection -> LogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> SimpleScalarStringSelection -> GenericScalarSelection -> SimpleLogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> SimpleScalarStringSelection -> GenericScalarSelection -> Subscript -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> SimpleScalarStringSelection -> Subscript -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> SimpleScalarStringSelection -> StringSelection
//                             -> GenericScalarSelection -> Subscript -> GenericSelection
//                             -> GenericScalarSelection -> GenericSelection
//                             -> SimpleIntSequenceSelection -> (below)
//                             -> IntSelection -> GenericSelection
//                             -> Subscript -> GenericSelection
//                             -
// *SimpleIntSequenceSelection -> IntSelection -> GenericSelection
//                             -> GenericSelection
//
public abstract class ReadVector extends BaseR {

    @Child RNode lhs;
    @Children RNode[] indexes;
    final boolean subset;

    private static final boolean DEBUG_SEL = false;

    ReadVector(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
        super(ast);
        this.lhs = adoptChild(lhs);
        this.indexes = adoptChildren(indexes);
        this.subset = subset;
    }

    enum Failure {
        NOT_ARRAY_BASE,
        NOT_ARRAY_INDEX,
        NOT_INT_INDEX,
        NOT_INT_SEQUENCE_INDEX,
        NOT_DOUBLE_INDEX,
        NOT_STRING_INDEX,
        NOT_ALL_POSITIVE_INDEX,
        NOT_INT_OR_DOUBLE_INDEX,
        NOT_LOGICAL_INDEX,
        NOT_ONE_ELEMENT,
        NA_INDEX,
        NOT_POSITIVE_INDEX,
        INDEX_OUT_OF_BOUNDS,
        NOT_SAME_LENGTH,
        NOT_SUBSET,
        BASE_HAS_NAMES,
        UNSPECIFIED
    }

    @Override public Object execute(Frame frame) {
        RAny base = (RAny) lhs.execute(frame); // note: order is important
        RAny index = (RAny) indexes[0].execute(frame);
        return execute(index, base);
    }

    abstract RAny execute(RAny index, RAny vector);

    // NOTE: currently not used
    private abstract static class Select {
        abstract RAny select(RAny vector, int index) throws UnexpectedResultException;
    }

    // NOTE: currently not used
    private static Select createScalarSelect(boolean subset, RAny vectorTemplate) {
        if (subset) {
            return new Select() {
                @Override final RAny select(RAny vector, int index) throws UnexpectedResultException {
                    if (!(vector instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                    RArray vrarr = (RArray) vector;
                    if (index > vrarr.size()) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                    return vrarr.boxedGet(index - 1);
                }
            };
        } else if (!(vectorTemplate instanceof RList)) {
            return new Select() {
                @Override final RAny select(RAny vector, int index) throws UnexpectedResultException {
                    if (vector instanceof RList || !(vector instanceof RArray)) { throw new UnexpectedResultException(Failure.UNSPECIFIED); }
                    RArray vrarr = (RArray) vector;
                    if (index > vrarr.size()) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                    return vrarr.boxedGet(index - 1);
                }
            };
        } else {
            return new Select() {
                @Override final RAny select(RAny vector, int index) throws UnexpectedResultException {
                    if (!(vector instanceof RList)) { throw new UnexpectedResultException(Failure.UNSPECIFIED); }
                    RList vlist = (RList) vector;
                    if (index > vlist.size()) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                    return vlist.getRAny(index - 1);
                }
            };
        }
    }

    // when the index has only one argument, which is an integer
    //   for more complicated and corner cases rewrites itself
    public static class SimpleScalarIntSelection extends ReadVector {

        public SimpleScalarIntSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override public RAny execute(RAny index, RAny vector) {
            try {
                if (!(vector instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                if (!(index instanceof RInt)) { throw new UnexpectedResultException(Failure.NOT_INT_INDEX); }
                RArray vrarr = (RArray) vector;
                if (vrarr.names() != null) { throw new UnexpectedResultException(Failure.UNSPECIFIED); }
                RInt irint = (RInt) index;
                if (irint.size() != 1) { throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT); }
                int i = irint.getInt(0);
                if (i <= 0 || i > vrarr.size()) { throw new UnexpectedResultException(Failure.UNSPECIFIED); }// includes NA_INDEX, NOT_POSITIVE_INDEX, INDEX_OUT_OF_BOUNDS
                if (subset || !(vrarr instanceof RList)) {
                    return vrarr.boxedGet(i - 1);
                } else {
                    return ((RList) vrarr).getRAny(i - 1);
                }
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarIntSelection failed: " + f);
                switch (f) {
                case NOT_INT_INDEX:
                    SimpleScalarDoubleSelection dbl = new SimpleScalarDoubleSelection(ast, lhs, indexes, subset, vector);
                    replace(dbl, "install SimpleScalarDoubleSelection from SimpleScalarIntSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleScalarDoubleSelection");
                    return dbl.execute(index, vector);

                case NOT_ONE_ELEMENT:
                    if (subset) {
                        if (index instanceof IntImpl.RIntSequence) {
                            SimpleIntSequenceSelection is = new SimpleIntSequenceSelection(ast, lhs, indexes, subset);
                            replace(is, "install SimpleIntSequenceSelection from SimpleScalarIntSelection");
                            if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleIntSequenceSelection");
                            return is.execute(index, vector);
                        } else {
                            IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                            replace(is, "install IntSelection from SimpleScalarIntSelection");
                            if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                            return is.execute(index, vector);
                        }
                    } else {
                        Subscript s = new Subscript(ast, lhs, indexes, subset);
                        replace(s, "install Subscript from SimpleScalarIntSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                        return s.execute(index, vector);
                    }

                default:
                    GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                    replace(gen, "install GenericScalarSelection from SimpleScalarIntSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                    return gen.execute(index, vector);

                }
            }
        }
    }

    // when the index has only one argument, which is a constant integer (so can use for double, too, with a cast)
    //   for more complicated and corner cases rewrites itself
    public static class SimpleConstantScalarIntSelection extends ReadVector {

        final int index;

        public SimpleConstantScalarIntSelection(ASTNode ast, RNode lhs, RNode[] indexes, int index, boolean subset) {
            super(ast, lhs, indexes, subset);
            this.index = index;
        }

        @Override public Object execute(Frame frame) {
            RAny base = (RAny) lhs.execute(frame);
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray vrarr = (RArray) base;
                if (index > vrarr.size()) { throw new UnexpectedResultException(Failure.UNSPECIFIED); // includes NA_INDEX, NOT_POSITIVE_INDEX, INDEX_OUT_OF_BOUNDS
                }
                return getWithName(vrarr, index - 1, subset);
            } catch (UnexpectedResultException e) {
                GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                replace(gen, "");
                return gen.execute((RAny) indexes[0].execute(frame), base);
            }
        }

        @Override public RAny execute(RAny index, RAny vector) {
            return null;
        }
    }

    // when the base is DoubleImpl and the index is a scalar*impl (and a simple case, e.g. within bounds)
    public abstract static class DoubleBaseSimpleSelection extends ReadVector {
        public DoubleBaseSimpleSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        public static class ScalarIntSelection extends DoubleBaseSimpleSelection {
            public ScalarIntSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
                super(ast, lhs, indexes, subset);
            }

            @Override public RAny execute(RAny index, RAny vector) {
                try {
                    if (!(index instanceof ScalarIntImpl)) { throw new UnexpectedResultException(null); }
                    int i = (((ScalarIntImpl) index).getInt()) - 1;
                    if (!(vector instanceof DoubleImpl)) { throw new UnexpectedResultException(null); }
                    DoubleImpl dbase = (DoubleImpl) vector;
                    double[] base = dbase.getContent();
                    if (i < 0 || i >= base.length || dbase.names() != null) { throw new UnexpectedResultException(null); }
                    return RDouble.RDoubleFactory.getScalar(base[i]);

                } catch (UnexpectedResultException e) {
                    ScalarDoubleSelection ns = new ScalarDoubleSelection(ast, lhs, indexes, subset);
                    replace(ns, "install DoubleBaseSimpleSelection.ScalarDoubleSelection from DoubleBaseSimpleSelection.ScalarIntSelection");
                    return ns.execute(index, vector);
                }
            }
        }

        public static class ScalarDoubleSelection extends DoubleBaseSimpleSelection {
            public ScalarDoubleSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
                super(ast, lhs, indexes, subset);
            }

            @Override public RAny execute(RAny index, RAny vector) {
                try {
                    if (!(index instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(null); }
                    int i = Convert.double2int(((ScalarDoubleImpl) index).getDouble()) - 1;
                    if (!(vector instanceof DoubleImpl)) { throw new UnexpectedResultException(null); }
                    DoubleImpl dbase = (DoubleImpl) vector;
                    double[] base = dbase.getContent();
                    if (i < 0 || i >= base.length || dbase.names() != null) { throw new UnexpectedResultException(null); }
                    return RDouble.RDoubleFactory.getScalar(base[i]);

                } catch (UnexpectedResultException e) {
                    SimpleScalarIntSelection is = new SimpleScalarIntSelection(ast, lhs, indexes, subset);
                    replace(is, "install SimpleScalarIntSelection from DoubleBaseSimpleSelection.ScalarIntSelection");
                    return is.execute(index, vector);
                }
            }
        }
    }

    // when the index has only one argument, which is a double
    //   for more complicated and corner cases rewrites itself
    public static class SimpleScalarDoubleSelection extends ReadVector {
        //final Select select;
        public SimpleScalarDoubleSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset, RAny vectorTemplate) {
            super(ast, lhs, indexes, subset);
            //this.select = createScalarSelect(subset, vectorTemplate);
        }

        @Override public RAny execute(RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleScalarDoubleSelection");
            try {
                if (!(index instanceof RDouble)) { throw new UnexpectedResultException(Failure.NOT_DOUBLE_INDEX); }
                RDouble irdbl = (RDouble) index;
                if (irdbl.size() != 1) { throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT); }
                int i = Convert.double2int(irdbl.getDouble(0)); // FIXME: check when the index is too large

                // FIXME: surprisingly using select did not work well in binarytrees benchmark, where the base alternates between list and integer
                //                if (i <= 0) {
                //                    throw new UnexpectedResultException(Failure.NOT_POSITIVE_INDEX); // includes NA_INDEX
                //                }
                //                return select.select(vector, i);

                if (!(vector instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray vrarr = (RArray) vector;
                if (i <= 0 || i > vrarr.size() || vrarr.names() != null) {
                    throw new UnexpectedResultException(Failure.UNSPECIFIED);
                    // includes NA_INDEX, NOT_POSITIVE_INDEX, INDEX_OUT_OF_BOUNDS, has names
                }
                if (subset || !(vrarr instanceof RList)) {
                    return vrarr.boxedGet(i - 1);
                } else {
                    return ((RList) vrarr).getRAny(i - 1);
                }
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarDoubleSelection failed: " + f);
                switch (f) {
                case NOT_ONE_ELEMENT:
                    if (subset) {
                        IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                        replace(is, "install IntSelection from SimpleScalarDoubleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                        return is.execute(index, vector);
                    } else {
                        Subscript s = new Subscript(ast, lhs, indexes, subset);
                        replace(s, "install Subscript from SimpleScalarDoubleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                        return s.execute(index, vector);
                    }
                case NOT_DOUBLE_INDEX:
                    if (index instanceof RString) {
                        SimpleScalarStringSelection ss = new SimpleScalarStringSelection(ast, lhs, indexes, subset, vector);
                        replace(ss, "install SimpleScalarStringSelection from SimpleScalarDoubleSelection");
                        return ss.execute(index, vector);
                    }
                default:
                    GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                    replace(gen, "install GenericScalarSelection from SimpleScalarDoubleSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                    return gen.execute(index, vector);
                }
            }
        }
    }

    // when the index has only one argument, which is a string
    public static class SimpleScalarStringSelection extends ReadVector {
        public SimpleScalarStringSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset, RAny vectorTemplate) {
            super(ast, lhs, indexes, subset);
        }

        @Override public RAny execute(RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleScalarStringSelection");
            try {
                if (!(index instanceof RString)) { throw new UnexpectedResultException(Failure.NOT_STRING_INDEX); }
                RString irstr = (RString) index;
                if (irstr.size() != 1) { throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT); }
                if (!(vector instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray vrarr = (RArray) vector;
                Names names = vrarr.names();
                if (names == null) { throw new UnexpectedResultException(Failure.UNSPECIFIED); }
                RSymbol symbol = RSymbol.getSymbol(irstr.getString(0));
                int i = names.map(symbol);
                if (i == -1) { throw new UnexpectedResultException(Failure.UNSPECIFIED); }
                return getWithName(vrarr, i, subset);
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarStringSelection failed: " + f);
                switch (f) {
                case NOT_ONE_ELEMENT:
                    if (subset) {
                        StringSelection is = new StringSelection(ast, lhs, indexes, subset);
                        replace(is, "install StringSelection from SimpleScalarStringSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                        return is.execute(index, vector);
                    } else {
                        Subscript s = new Subscript(ast, lhs, indexes, subset);
                        replace(s, "install Subscript from SimpleScalarStringSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                        return s.execute(index, vector);
                    }
                default:
                    GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                    replace(gen, "install GenericScalarSelection from SimpleScalarStringSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                    return gen.execute(index, vector);
                }
            }
        }
    }

    private static RAny getWithName(RArray base, int i, boolean subset) {
        if (subset) {
            return base.boxedNamedGet(i);
        } else if (base instanceof RList) {
            return ((RList) base).getRAny(i); // list subscript does not preserve names
        } else {
            return base.boxedGet(i); // non-list subscript does not preserve names, either
        }
    }

    // any case when the index has only one argument (a scalar)
    //   rewrites itself when index is not a single scalar
    public static class GenericScalarSelection extends ReadVector {
        public GenericScalarSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        // index must be a scalar
        public static RAny executeScalar(RArray base, RArray index, boolean subset, ASTNode ast) {
            int i = 0;
            int size = base.size();
            if (index instanceof RDouble) {
                RDouble idbl = (RDouble) index;
                i = Convert.double2int(idbl.getDouble(0)); // FIXME: check when the index is too large
            } else if (index instanceof RInt) {
                i = ((RInt) index).getInt(0);
            } else if (index instanceof RLogical) {
                i = ((RLogical) index).getLogical(0);
                if (subset) {
                    if (i == RLogical.TRUE) { return base; }
                    if (i == RLogical.FALSE) { return Utils.createEmptyArray(base); }
                }
            } else if (index instanceof RString) {
                RSymbol name = RSymbol.getSymbol(((RString) index).getString(0));
                Names bnames = base.names();
                if (bnames == null) { return Utils.getBoxedNA(base); }
                i = bnames.map(name);
                if (i != -1) {
                    return getWithName(base, i, subset);
                } else {
                    return Utils.getNamedNA(base);
                }
            } else {
                throw RError.getInvalidSubscriptType(ast,index.typeOf());
            }

            if (i > 0) { // NOTE: RInt.NA < 0
                if (i <= size) { return getWithName(base, i - 1, subset); }
            }

            if (size == 0) { return Utils.createNA(base); }
            if (!subset) {
                if (i == 0) { throw RError.getSelectLessThanOne(ast); }
                if (i > 0 || i == RInt.NA) { // means also i > size
                    throw RError.getSubscriptBounds(ast);
                }
                // i < 0
                if (size > 2) { throw RError.getSelectMoreThanOne(ast); }
                if (size == 1) { throw RError.getSelectLessThanOne(ast); }
                // size == 2
                if (i != -1 && i != -2) { throw RError.getSelectMoreThanOne(ast); }
            }
            if (base instanceof RDouble) { // FIXME: could reduce verbosity through refactoring the number factories?
                if (i == RInt.NA) { return RDouble.RDoubleFactory.getNAArray(size); }
                if (i < 0) {
                    if (-i > size) { return base.stripAttributesKeepNames(); }
                    return RDouble.RDoubleFactory.exclude(-i - 1, (RDouble) base);
                }
                if (i == 0) { return RDouble.RDoubleFactory.getEmpty(base.names() != null); }
                // i > size
                return RDouble.RDoubleFactory.getNA(base.names() != null);
            }
            if (base instanceof RInt) {
                if (i == RInt.NA) { return RInt.RIntFactory.getNAArray(size); }
                if (i < 0) {
                    if (-i > size) { return base.stripAttributesKeepNames(); }
                    return RInt.RIntFactory.exclude(-i - 1, (RInt) base);
                }
                if (i == 0) { return RInt.RIntFactory.getEmpty(base.names() != null); }
                // i > size
                return RInt.RIntFactory.getNA(base.names() != null);
            }
            if (base instanceof RLogical) {
                if (i == RInt.NA) { return RLogical.RLogicalFactory.getNAArray(size); }
                if (i < 0) {
                    if (-i > size) { return base.stripAttributesKeepNames(); }
                    return RLogical.RLogicalFactory.exclude(-i - 1, (RLogical) base);
                }
                if (i == 0) { return RLogical.RLogicalFactory.getEmpty(base.names() != null); }
                // i > size
                return RLogical.RLogicalFactory.getNA(base.names() != null);
            }
            if (base instanceof RList) {
                if (i == RInt.NA) { return RList.RListFactory.getNullArray(size); }
                if (i < 0) {
                    if (-i > size) { return base.stripAttributesKeepNames(); }
                    return RList.RListFactory.exclude(-i - 1, (RList) base);
                }
                if (i == 0) { return RList.RListFactory.getEmpty(base.names() != null); }
                // i > size
                return RList.RListFactory.getNull(base.names() != null);
            }
            if (base instanceof RString) {
                if (i == RInt.NA) { return RString.RStringFactory.getNAArray(size); }
                if (i < 0) {
                    if (-i > size) { return base.stripAttributesKeepNames(); }
                    return RString.RStringFactory.exclude(-i - 1, (RString) base);
                }
                if (i == 0) { return RString.RStringFactory.getEmpty(base.names() != null); }
                // i > size
                return RString.RStringFactory.getNA(base.names() != null);
            }
            if (base instanceof RComplex) {
                if (i == RInt.NA) { return RComplex.RComplexFactory.getNAArray(size); }
                if (i < 0) {
                    if (-i > size) { return base.stripAttributesKeepNames(); }
                    return RComplex.RComplexFactory.exclude(-i - 1, (RComplex) base);
                }
                if (i == 0) { return RComplex.RComplexFactory.getEmpty(base.names() != null); }
                // i > size
                return RComplex.RComplexFactory.getNA(base.names() != null);
            }
            assert Utils.check(base instanceof RRaw);
            if (i < 0) {
                if (-i > size) { return base.stripAttributesKeepNames(); }
                return RRaw.RRawFactory.exclude(-i - 1, (RRaw) base);
            }
            if (i == 0) { return RRaw.RRawFactory.getEmpty(base.names() != null); }
            // i > size
            return RRaw.RRawFactory.getZero(base.names() != null);
        }

        @Override public RAny execute(RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing GenericScalarSelection");
            try {
                if (!(vector instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray vrarr = (RArray) vector;
                if (!(index instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_INDEX); }
                RArray irarr = (RArray) index;
                if (irarr.size() != 1) { throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT); }
                return executeScalar(vrarr, irarr, subset, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - GenericScalarSelection failed: " + f);

                if (f == Failure.NOT_ONE_ELEMENT && index instanceof RLogical && subset) {
                    SimpleLogicalSelection ls = new SimpleLogicalSelection(ast, lhs, indexes, subset);
                    replace(ls, "install SimpleLogicalSelection from GenericScalarSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleLogicalSelection");
                    return ls.execute(index, vector);
                } else {
                    if (!subset) {
                        Subscript s = new Subscript(ast, lhs, indexes, subset);
                        replace(s, "install Subscript from GenericScalarSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                        return s.execute(index, vector);
                    } else {
                        GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                        replace(gs, "install GenericSelection from GenericScalarSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                        return gs.execute(index, vector);
                    }
                }
            }
        }
    }

    // when the index is a sequence of integers (e.g. created using the colon operator)
    //   rewrites itself for other and corner cases
    public static class SimpleIntSequenceSelection extends ReadVector {
        public SimpleIntSequenceSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(subset);
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleIntSequenceSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (abase.names() != null) { throw new UnexpectedResultException(Failure.BASE_HAS_NAMES); // FIXME: lazy names?
                }
                if (!(index instanceof IntImpl.RIntSequence)) { // FIXME: this goes directly to Int implementation, not terribly nice
                    throw new UnexpectedResultException(Failure.NOT_INT_SEQUENCE_INDEX);
                }
                IntImpl.RIntSequence sindex = (IntImpl.RIntSequence) index;

                if (!sindex.isPositive()) { throw new UnexpectedResultException(Failure.NOT_ALL_POSITIVE_INDEX); }
                int size = abase.size();
                if (sindex.max() > size) { throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS); }
                // FIXME: should specialize for a particular base type, or have a type hierarchy on factories
                if (abase instanceof RDouble) { return new RDoubleView((RDouble) abase, sindex.from(), sindex.to(), sindex.step()); }
                if (abase instanceof RInt) { return new RIntView((RInt) abase, sindex.from(), sindex.to(), sindex.step()); }
                if (abase instanceof RLogical) { return new RLogicalView((RLogical) abase, sindex.from(), sindex.to(), sindex.step()); }
                if (abase instanceof RList) { return new RListView((RList) abase, sindex.from(), sindex.to(), sindex.step()); }
                if (abase instanceof RString) { return new RStringView((RString) abase, sindex.from(), sindex.to(), sindex.step()); }
                if (abase instanceof RRaw) { return new RRawView((RRaw) abase, sindex.from(), sindex.to(), sindex.step()); }
                assert Utils.check(abase instanceof RComplex);
                return new RComplexView((RComplex) abase, sindex.from(), sindex.to(), sindex.step());
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleIntSequenceSelection failed: " + f);
                switch (f) {
                case NOT_INT_SEQUENCE_INDEX:
                case NOT_ALL_POSITIVE_INDEX:
                case INDEX_OUT_OF_BOUNDS:
                case BASE_HAS_NAMES:
                    IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                    replace(is, "install IntSelection from SimpleIntSequenceSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                    return is.execute(index, base);

                default:
                    GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                    replace(gs, "install GenericSelection from SimpleIntSequenceSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                    return gs.execute(index, base);
                }
            }
        }

        static class RRawView extends View.RRawProxy<RRaw> implements RRaw {
            final int from;
            final int to;
            final int step;
            final int size;

            public RRawView(RRaw base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public byte getRaw(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getRaw(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }

        static class RLogicalView extends View.RLogicalProxy<RLogical> implements RLogical {
            final int from;
            final int to;
            final int step;
            final int size;

            public RLogicalView(RLogical base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public int getLogical(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getLogical(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }

        static class RIntView extends View.RIntProxy<RInt> implements RInt {
            final int from;
            final int to;
            final int step;
            final int size;

            public RIntView(RInt base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public int getInt(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getInt(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }

        static class RDoubleView extends View.RDoubleProxy<RDouble> implements RDouble {
            final int from;
            final int to;
            final int step;
            final int size;

            public RDoubleView(RDouble base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public double getDouble(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getDouble(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }

        static class RComplexView extends View.RComplexProxy<RComplex> implements RComplex {
            final int from;
            final int to;
            final int step;
            final int size;

            public RComplexView(RComplex base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public double getReal(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getReal(from + i * step - 1);
            }

            @Override public double getImag(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getImag(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }

        static class RStringView extends View.RStringProxy<RString> implements RString {
            final int from;
            final int to;
            final int step;
            final int size;

            public RStringView(RString base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public String getString(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getString(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }

        static class RListView extends View.RListProxy<RList> implements RList {
            final int from;
            final int to;
            final int step;
            final int size;

            public RListView(RList base, int from, int to, int step) {
                super(base);
                this.from = from;
                this.to = to;
                this.step = step;
                this.size = RIntSequence.sequenceSize(from, to, step);
            }

            @Override public int size() {
                return size;
            }

            @Override public RAny getRAny(int i) {
                assert Utils.check(i < size, "bounds check");
                assert Utils.check(i >= 0, "bounds check");
                return orig.getRAny(from + i * step - 1);
            }

            @Override public int[] dimensions() { // drop dimensions
                return null;
            }
        }
    }

    // when the index is a vector of integers or doubles (selection by numeric index)
    //   casts double index to integer
    //   rewrites itself for other cases
    public static class IntSelection extends ReadVector { // FIXME: create yet another node without the negatives and zero crap
        public IntSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(subset);
        }

        public static RAny executeIntVector(RInt index, RArray base, ASTNode ast) {

            Names names = base.names();
            RSymbol[] symbols = (names == null) ? null : names.sequence();
            RSymbol[] newSymbols = null;
            int nzeros = 0;
            boolean hasNegative = false;
            boolean hasPositive = false;
            boolean hasNA = false;
            int bsize = base.size();
            int isize = index.size();
            boolean[] omit = null;
            int nomit = 0;

            for (int i = 0; i < isize; i++) { // FIXME: does that really pay off creating a view (not materializing) given that we have to
                                              //        traverse the index vector anyway?
                int v = index.getInt(i);
                if (v == RInt.NA) {
                    hasNA = true;
                    continue;
                }
                if (v == 0) {
                    nzeros++;
                    continue;
                }
                if (v > 0) {
                    hasPositive = true;
                    continue;
                }
                // v < 0
                if (!hasNegative) {
                    hasNegative = true;
                    omit = new boolean[bsize];
                }
                int vi = -v - 1;
                if (vi < omit.length) {
                    if (!omit[vi]) {
                        omit[vi] = true;
                        nomit++;
                    }
                }
            }
            boolean hasZero = nzeros > 0;

            if (!hasNegative) {
                if (!hasZero && symbols == null && !hasNA) { return base.subset(index); }
                // positive and zero indexes (and perhaps NAs)
                int nsize = isize - nzeros;
                if (symbols != null) {
                    newSymbols = new RSymbol[nsize];
                }
                RArray res = Utils.createArray(base, nsize, symbols != null);
                int j = 0;
                for (int i = 0; i < isize; i++) {
                    int v = index.getInt(i);
                    if (v > 0 && v <= bsize) { // note: RInt.NA < 0
                        res.set(j, base.get(v - 1));
                        if (symbols != null) {
                            newSymbols[j] = symbols[v - 1];
                        }
                        j++;
                        continue;
                    }
                    if (v == 0) {
                        continue;
                    }
                    Utils.setNA(res, j);
                    if (symbols != null) {
                        newSymbols[j] = RSymbol.NA_SYMBOL;
                    }
                    j++;
                }
                if (symbols != null) {
                    res = res.setNames(Names.create(newSymbols));
                }
                return res;
            } else { // hasNegative == true
                if (hasPositive || hasNA) { throw RError.getOnlyZeroMixed(ast); }
                // negative and zero indexes
                int nsize = bsize - nomit;
                RArray res = Utils.createArray(base, nsize, symbols != null);
                if (symbols != null) {
                    newSymbols = new RSymbol[nsize];
                }
                int j = 0;
                for (int i = 0; i < bsize; i++) {
                    if (!omit[i]) {
                        res.set(j, base.get(i));
                        if (symbols != null) {
                            newSymbols[j] = symbols[i];
                        }
                        j++;
                    }
                }
                if (symbols != null) {
                    res = res.setNames(Names.create(newSymbols));
                }
                return res;
            }
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing IntSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                RInt iindex;
                if (index instanceof RInt) {
                    iindex = (RInt) index;
                } else if (index instanceof RDouble) {
                    iindex = index.asInt();
                } else {
                    throw new UnexpectedResultException(Failure.NOT_INT_OR_DOUBLE_INDEX);
                }
                return executeIntVector(iindex, abase, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - IntSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                replace(gs, "install GenericSelection from IntSelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(index, base);
            }
        }
    }

    // for selections like d[x == c], where c is a scalar double constant and x is a double and d is a double ; and where d has the same size as x
    // FIXME: make this more generic, supporting any type of base "d" should not be (much?) slower
    public static class LogicalEqualitySelection extends BaseR {
        final RNode lhs;
        final RNode xExpr;
        final double c;

        public LogicalEqualitySelection(ASTNode ast, RNode lhs, RNode xExpr, double c) {
            // only for subset
            super(ast);
            this.lhs = adoptChild(lhs);
            this.xExpr = adoptChild(xExpr);
            assert Utils.check(RDouble.RDoubleUtils.isFinite(c));
            this.c = c;
        }

        @Override
        public Object execute(Frame frame) {
            RAny base = (RAny) lhs.execute(frame); // note: order is important
            RAny x = (RAny) xExpr.execute(frame);
            return execute(base, x);
        }

        public RAny execute(RAny base, RAny xArg) {
            try {
                if (!(base instanceof DoubleImpl && xArg instanceof DoubleImpl)) { // FIXME: also could materialize
                    throw new UnexpectedResultException(null);
                }
                // NOTE: in this case, it is very important for performance to access the arrays directly (found out experimentally)
                // it is indeed puzzling as e.g. with matrix multiply, going through .getDouble did cost nothing
                DoubleImpl bdi = (DoubleImpl) base;
                double[] b = ((DoubleImpl) base).getContent();
                double[] x = ((DoubleImpl) xArg).getContent();
                int size = b.length;
                if (x.length != size || bdi.names() != null) {
                    throw new UnexpectedResultException(null);
                }
                if (size > 1000) {  // TODO: a tuning parameter, what is the right cutoff???
                    // NOTE: this is faster at least for larger vectors, I suppose because it is more cache friendly
                    // TODO: similar optimizations will likely help elsewhere
                    // TODO: this is yet another case for "growable" vector types, perhaps even non-contiguous
                    // TODO: this wastes memory, too
                    double[] tmp = new double[size];
                    int j = 0;
                    for (int i = 0; i < size; i++) {
                        double d = x[i];
                        if (d == c) {
                            tmp[j++] = b[i];
                        } else if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                            tmp[j++] = RDouble.NA;
                        }
                    }
                    if (j == size) {
                        return RDouble.RDoubleFactory.getFor(tmp); // we are lucky
                    } else {
                        double[] content = new double[j];
                        System.arraycopy(tmp, 0, content, 0, j);
                        return RDouble.RDoubleFactory.getFor(content);
                    }

                } else {
                    int nsize = 0;
                    for (int i = 0; i < size; i++) {
                        double d = x[i];
                        if (d == c || RDouble.RDoubleUtils.isNAorNaN(d)) {
                            nsize ++;
                        }
                    }
                    double[] content = new double[nsize];
                    int j = 0;
                    for (int i = 0; i < size; i++) {
                        double d = x[i];
                        if (d == c) {
                            content[j++] = b[i];
                        } else if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                            content[j++] = RDouble.NA;
                        }
                    }
                    return RDouble.RDoubleFactory.getFor(content);
                }

            } catch(UnexpectedResultException e) {
                AccessVector av = (AccessVector) ast;
                EQ eq = (EQ) av.getArgs().first().getValue();
                RDouble boxedC = RDouble.RDoubleFactory.getScalar(c);

                Comparison indexExpr = new Comparison(eq, xExpr, new Constant(eq.getRHS(), boxedC),
                        r.nodes.truffle.Comparison.getEQ());

                LogicalSelection ls = new LogicalSelection(ast, lhs, new RNode[] { indexExpr }, true);
                replace(ls, "install LogicalSelection from LogicalEqualitySelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with LogicalSelection");

                // index
                return ls.execute((RAny) indexExpr.execute(xArg, boxedC), base);

            }
        }
    }

    // when the index is a logical vector of the same length as the base
    //   rewrites itself for other cases
    public static class SimpleLogicalSelection extends ReadVector {
        public SimpleLogicalSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(subset);
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleLogicalSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (!(index instanceof RLogical)) { throw new UnexpectedResultException(Failure.NOT_LOGICAL_INDEX); }
                if (abase.names() != null) { throw new UnexpectedResultException(Failure.BASE_HAS_NAMES); }
                RLogical lindex = (RLogical) index;
                int isize = lindex.size();
                int bsize = abase.size();
                if (isize != bsize) { throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH); }
                int nsize = 0;
                for (int i = 0; i < isize; i++) {
                    if (lindex.getLogical(i) != RLogical.FALSE) {
                        nsize++;
                    }
                }
                RArray res = Utils.createArray(base, nsize);
                int j = 0;
                for (int i = 0; i < isize; i++) {
                    int l = lindex.getLogical(i);
                    if (l == RLogical.TRUE) {
                        res.set(j++, abase.get(i));
                    } else if (l == RLogical.NA) {
                        Utils.setNA(res, j++);
                    }
                }
                return res;
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleLogicalSelection failed: " + f);
                switch (f) {
                case NOT_SAME_LENGTH:
                case BASE_HAS_NAMES:
                    LogicalSelection ls = new LogicalSelection(ast, lhs, indexes, subset);
                    replace(ls, "install LogicalSelection from SimpleLogicalSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with LogicalSelection");
                    return ls.execute(index, base);

                default:
                    GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                    replace(gs, "install GenericSelection from SimpleLogicalSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                    return gs.execute(index, base);
                }
            }
        }
    }

    // when the index is a logical vector
    //   rewrites itself for other cases
    public static class LogicalSelection extends ReadVector {
        public LogicalSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(subset);
        }

        public static RAny executeLogicalVector(RLogical index, RArray base) {
            int isize = index.size();
            int bsize = base.size();
            Names names = base.names();
            if (isize == 0) { return Utils.createEmptyArray(base, names != null); }
            RSymbol[] symbols = (names == null) ? null : names.sequence();
            RSymbol[] newSymbols = null;

            if (isize >= bsize) {
                // no re-use of index, but index can be longer than base
                int nsize = 0;
                for (int i = 0; i < isize; i++) {
                    if (index.getLogical(i) != RLogical.FALSE) {
                        nsize++;
                    }
                }
                RArray res = Utils.createArray(base, nsize, symbols != null);
                if (symbols != null) {
                    newSymbols = new RSymbol[nsize];
                }
                int j = 0;
                int i = 0;
                for (; i < bsize; i++) {
                    int l = index.getLogical(i);
                    if (l == RLogical.TRUE) {
                        res.set(j, base.get(i));
                        if (symbols != null) {
                            newSymbols[j] = symbols[i];
                        }
                        j++;
                    } else if (l == RLogical.NA) {
                        Utils.setNA(res, j);
                        if (symbols != null) {
                            newSymbols[j] = RSymbol.NA_SYMBOL;
                        }
                        j++;
                    }
                }
                for (; i < isize; i++) {
                    int l = index.getLogical(i);
                    if (l != RLogical.FALSE) {
                        Utils.setNA(res, j);
                        if (symbols != null) {
                            newSymbols[j] = RSymbol.NA_SYMBOL;
                        }
                        j++;
                    }
                }
                if (symbols != null) {
                    res = res.setNames(Names.create(newSymbols));
                }
                return res;
            } else {
                // index is re-used and is shorter than base
                int nsize = 0;
                int j = 0;
                for (int i = 0; i < bsize; i++) {
                    if (index.getLogical(j) != RLogical.FALSE) {
                        nsize++;
                    }
                    j++;
                    if (j == isize) {
                        j = 0;
                    }
                }
                RArray res = Utils.createArray(base, nsize, symbols != null);
                if (symbols != null) {
                    newSymbols = new RSymbol[nsize];
                }
                j = 0;
                int k = 0;
                for (int i = 0; i < bsize; i++) {
                    int l = index.getLogical(j);
                    if (l == RLogical.TRUE) {
                        res.set(k, base.get(i));
                        if (symbols != null) {
                            newSymbols[k] = symbols[i];
                        }
                        k++;
                    } else if (l == RLogical.NA) {
                        Utils.setNA(res, k);
                        if (symbols != null) {
                            newSymbols[k] = RSymbol.NA_SYMBOL;
                        }
                        k++;
                    }
                    j++;
                    if (j == isize) {
                        j = 0;
                    }
                }
                if (symbols != null) {
                    res = res.setNames(Names.create(newSymbols));
                }
                return res;
            }
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing LogicalSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                if (!(index instanceof RLogical)) { throw new UnexpectedResultException(Failure.NOT_LOGICAL_INDEX); }
                return executeLogicalVector((RLogical) index, (RArray) base);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - LogicalSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                replace(gs, "install GenericSelection from LogicalSelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(index, base);
            }
        }
    }

    // when the index is a vector of strings (selection by name)
    //   rewrites itself for other cases
    public static class StringSelection extends ReadVector {
        public StringSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(subset);
        }

        public static RAny executeStringVector(RString index, RArray base, ASTNode ast) {
            int isize = index.size();
            if (isize == 0) { return Utils.createEmptyArray(base, base.names() != null); }
            Names baseNames = base.names();
            if (baseNames == null) { return Utils.createNAArray(base, isize); }
            RSymbol[] symbols = new RSymbol[isize];
            RArray res = Utils.createArray(base, isize, true);
            for (int i = 0; i < isize; i++) {
                RSymbol symbol = RSymbol.getSymbol(index.getString(i));
                int v = baseNames.map(symbol);
                if (v != -1) {
                    res.set(i, base.get(v));
                    symbols[i] = symbol;
                } else {
                    Utils.setNA(res, i);
                    symbols[i] = RSymbol.NA_SYMBOL;
                }
            }
            return res.setNames(Names.create(symbols));
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing StringSelection");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                if (!(index instanceof RString)) { throw new UnexpectedResultException(Failure.NOT_STRING_INDEX); }
                return executeStringVector((RString) index, abase, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - StringSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                replace(gs, "install GenericSelection from StringSelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(index, base);
            }
        }
    }

    // when the index is a vector of integers (selection by index)
    //   and the base can be recursive
    //   and the mode is subscript ([[.]])
    public static class Subscript extends ReadVector {
        public Subscript(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(!subset);
        }

        public static int convertNegativeNonNAIndex(int indexv, int bsize, ASTNode ast) {
            int res;

            if (bsize > 2) { throw RError.getSelectMoreThanOne(ast); }
            if (bsize < 2) { throw RError.getSelectLessThanOne(ast); }
            // bsize == 2
            if (indexv == -1) {
                res = 1;
            } else if (indexv == -2) {
                res = 0;
            } else if (indexv == 0) {
                throw RError.getSelectLessThanOne(ast);
            } else {
                throw RError.getSelectMoreThanOne(ast);
            }
            return res;
        }

        public static int convertDereferencingIndex(int indexv, int iindex, int bsize, ASTNode ast) {
            int isel;
            if (indexv > 0) {
                if (indexv <= bsize) { // NOTE: RInt.NA < 0
                    isel = indexv - 1;
                } else {
                    throw RError.getNoSuchIndexAtLevel(ast, iindex + 1);
                }
            } else {
                if (indexv == RInt.NA) { throw RError.getNoSuchIndexAtLevel(ast, iindex + 1); }
                isel = convertNegativeNonNAIndex(indexv, bsize, ast);
            }
            return isel;
        }

        // note: subscript does not preserve names in the base, so no name handling (of base) is needed here
        public static RAny executeSubscript(RInt index, RArray base, ASTNode ast) {
            final int isize = index.size();
            if (isize == 0) { throw RError.getSelectLessThanOne(ast); }
            int i = 0;
            RAny b = base;

            if (isize > 1) {
                // the upper levels of recursive indexes have to be treated differently from the lowest level (the error semantics is different)
                // also, we know that the upper levels must be lists
                for (; i < isize - 1; i++) {
                    if (!(b instanceof RList)) { throw RError.getSelectMoreThanOne(ast); }
                    RList l = (RList) b;
                    int indexv = index.getInt(i);
                    int bsize = l.size();
                    int isel = convertDereferencingIndex(indexv, i, bsize, ast);
                    b = l.getRAny(isel);
                }
            }
            // selection at the last level
            int indexv = index.getInt(i);
            if (!(b instanceof RArray)) {
                // TODO: support language objects
                if (indexv == 1) {
                   throw RError.getInvalidTypeLength(ast, b.typeOf(), 1); // FIXME: a very obscure error message but what GNU-R returns
                }
                if (indexv > 1) {
                   throw RError.getSubscriptBounds(ast);
                }
                throw RError.getSelectLessThanOne(ast);
            }
            RArray a = (RArray) b;
            int bsize = a.size();
            boolean isList = a instanceof RList;

            if (indexv > 0) {
                if (indexv <= bsize) {
                    if (isList) {
                        return ((RList) a).getRAny(indexv - 1);
                    } else {
                        return a.boxedGet(indexv - 1);
                    }
                } else {
                    throw RError.getSubscriptBounds(ast);
                }
            } else {
                if (indexv == RInt.NA) {
                    if (isList) {
                        return RList.NULL;
                    } else {
                        throw RError.getSubscriptBounds(ast);
                    }
                }
                int fromIndex = convertNegativeNonNAIndex(indexv, bsize, ast);
                if (isList) {
                    return ((RList) a).getRAny(fromIndex);
                } else {
                    return a.boxedGet(fromIndex);
                }
            }
        }

        public static RAny executeSubscript(RString index, RArray base, ASTNode ast) {
            final int isize = index.size();
            if (isize == 0) { throw RError.getSelectLessThanOne(ast); }
            int i = 0;
            RAny b = base;

            if (isize > 1) {
                // the upper levels of recursive indexes have to be treated differently from the lowest level (the error semantics is different)
                // also, we know that the upper levels must be lists
                for (; i < isize - 1; i++) {
                    if (!(b instanceof RList)) { throw RError.getSelectMoreThanOne(ast); }
                    RList l = (RList) b;
                    Names names = l.names();
                    if (names == null) { throw RError.getNoSuchIndexAtLevel(ast, i + 1); }
                    RSymbol s = RSymbol.getSymbol(index.getString(i));
                    int indexv = names.map(s);
                    if (indexv == -1) { throw RError.getNoSuchIndexAtLevel(ast, i + 1); }
                    b = l.getRAny(indexv);
                }
            }
            // selection at the last level
            if (!(b instanceof RArray)) {
                throw RError.getSubscriptBounds(ast); // NOTE: this makes more sense than the error message with integer index
                    // (both are to mimic GNU-R)
            }
            RArray a = (RArray) b;
            Names names = a.names();
            int indexv = -1;
            if (names != null) {
                RSymbol s = RSymbol.getSymbol(index.getString(i));
                indexv = names.map(s);
            }
            boolean isList = a instanceof RList;
            if (indexv != -1) {
                if (isList) {
                    return ((RList) a).getRAny(indexv);
                } else {
                    return a.boxedGet(indexv);
                }
            } else {
                if (isList) {
                    return RList.NULL;
                } else {
                    throw RError.getSubscriptBounds(ast);
                }
            }
        }

        public static RAny executeSubscript(RAny index, RArray base, ASTNode ast) {
            if (index instanceof RInt || index instanceof RDouble || index instanceof RLogical) { return executeSubscript(index.asInt(), base, ast); }
            if (index instanceof RString) { return executeSubscript((RString) index, base, ast); }
            throw invalidSubscript(index, ast);
        }

        public static RError invalidSubscript(RAny index, ASTNode ast) {
            if (index instanceof RList) {
                int lsize = ((RList) index).size();
                if (lsize == 1) {
                    throw RError.getInvalidSubscriptType(ast, index.typeOf());
                }
                if (lsize == 0) {
                    throw RError.getSelectLessThanOne(ast);
                }
                throw RError.getSelectMoreThanOne(ast);
            }
            if (index instanceof RNull) {
                throw RError.getSelectLessThanOne(ast);
            }
            throw RError.getInvalidSubscriptType(ast, index.typeOf());
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing Subscript");
            try {
                if (!(base instanceof RArray)) { throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE); }
                RArray abase = (RArray) base;
                return executeSubscript(index, abase, ast);
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - Subscript failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset); // rewriting itself only to handle the error, there is no way to recover
                replace(gs, "install GenericSelection from Subscript");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(index, base);
            }
        }
    }

    // any selection, won't rewrite itself
    public static class GenericSelection extends ReadVector {
        public GenericSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override public RAny execute(RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing GenericSelection");
            if (!(base instanceof RArray)) {
                throw RError.getObjectNotSubsettable(ast, base.typeOf());
            }
            assert Utils.check(subset);
            RArray abase = (RArray) base;
            if (!(index instanceof RArray) || index instanceof RList) {
                throw RError.getInvalidSubscriptType(ast, index.typeOf());
            }
            RArray aindex = (RArray) index;
            int isize = aindex.size();
            if (isize == 1) { return GenericScalarSelection.executeScalar(abase, aindex, subset, ast); }
            if (aindex instanceof RInt) {
                return IntSelection.executeIntVector((RInt) aindex, abase, ast);
            } else if (aindex instanceof RDouble) {
                return IntSelection.executeIntVector(aindex.asInt(), abase, ast);
            } else if (aindex instanceof RLogical) {
                return LogicalSelection.executeLogicalVector((RLogical) aindex, abase);
            } else if (aindex instanceof RString) {
                return StringSelection.executeStringVector((RString) aindex, abase, ast);
            } else if (aindex instanceof RNull) { return Utils.createEmptyArray(abase); }
            throw RError.getInvalidSubscriptType(ast, aindex.typeOf());
        }
    }

    /**
     * Read access to a list using the dollar selector. Works only on lists, fails otherwise (compatible with R >= 2.6).
     * Because only string literals and symbols are allowed, the symbol creation and lookup is precached and the field
     * access only does the hashmap search in the names property of the list. If the list is empty, null element is
     * returned, as if when the desired field is not present.
     */
    public abstract static class FieldSelection extends BaseR {

        @Child RNode lhs;
        final RSymbol index;

        protected FieldSelection(ASTNode orig, RNode lhs, RSymbol index) {
            super(orig);
            this.lhs = adoptChild(lhs);
            this.index = index;
        }

        @Override public Object execute(Frame frame) {
            RAny base = (RAny) lhs.execute(frame);
            return execute(base);
        }

        /** As per R reference, should fail if not list or pairlist, otherwise the element should be returned. */
        abstract RAny execute(RAny base);

        public static class UninitializedSelection extends FieldSelection {

            public UninitializedSelection(ASTNode parent, RNode lhs, RSymbol index) {
                super(parent, lhs, index);
            }

            @Override RAny execute(RAny base) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    if (base instanceof RList) {
                        RList list = (RList) base;
                        RArray.Names names = list.names();

                        if (names != null) {
                            int pos = names.map(index);
                            if (pos == -1) {
                                pos = names.mapPartial(index);
                            }
                            if (pos != -1) {
                                FixedPositionSelection fp = new FixedPositionSelection(ast, lhs, index, pos, names);
                                replace(fp, "install FixedPositionSelection from UninitializedSelection (Field)");
                                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with FixedPositionSelection");
                                return fp.execute(base);
                            }
                        }
                    }
                    GenericSelection gs = new GenericSelection(ast, lhs, index);
                    replace(gs, "install GenericSelection from SimpleSelection (Field)");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                    return gs.execute(base);
                }
            }
        }

        public static class FixedPositionSelection extends FieldSelection {

            final int fixedPosition;
            final Names fixedNames;

            public FixedPositionSelection(ASTNode parent, RNode lhs, RSymbol index, int fixedPosition, Names fixedNames) {
                super(parent, lhs, index);
                this.fixedPosition = fixedPosition;
                this.fixedNames = fixedNames;
            }

            @Override RAny execute(RAny base) {
                try {
                    if (base instanceof RList) {
                        RList list = (RList) base;
                        RArray.Names names = list.names();
                        if (names == fixedNames) { return list.getRAny(fixedPosition); }
                    }
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    GenericSelection gs = new GenericSelection(ast, lhs, index);
                    replace(gs, "install GenericSelection from FixedPositionSelection (Field)");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                    return gs.execute(base);
                }
            }
        }

        public static class GenericSelection extends FieldSelection {

            int lastPosition = -1;
            Names lastNames = null;

            public GenericSelection(ASTNode parent, RNode lhs, RSymbol index) {
                super(parent, lhs, index);
            }

            @Override RAny execute(RAny base) {
                if (!(base instanceof RList)) { throw RError.getDollarAtomicVectors(ast); }
                RList list = (RList) base;
                RArray.Names names = list.names();
                int pos;
                if (names != lastNames) {
                    pos = names.map(index);
                    if (pos == -1) {
                        pos = names.mapPartial(index);
                    }
                    lastPosition = pos;
                    lastNames = names;
                } else {
                    pos = lastPosition;
                }
                if (pos == -1) { return RNull.getNull(); }
                return list.getRAny(pos); // list subscript does not preserve names
            }
        }
    }
}
