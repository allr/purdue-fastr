package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.Frame;
import com.oracle.truffle.runtime.Stable;
import com.oracle.truffle.runtime.ContentStable;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

// FIXME: add check for the number of dimensions in index
// FIXME: probably should also specialize for base types
// FIXME: get get a bit more performance by joining failure modes when distinction is not necessary (and particularly when that makes the checks easier)
//        i've done this with SimpleScalarDoubleSelection, and it helped a bit

// FIXME: consider adding Select specialization to SimpleIntScalarSelection, but now disabled even in SimpleDoubleScalarSelection as it is not good for the
//        binarytrees benchmark

// FIXME: add more support for constant vector indices

// rewriting of vector selection nodes:
//
// *SimpleScalarIntSelection   -> SimpleScalarDoubleSelection -> GenericScalarSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> SimpleLogicalSelection -> LogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> SimpleLogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> Subscript -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> Subscript -> GenericSelection
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

    @Stable RNode lhs;
    @ContentStable @Stable RNode[] indexes;
    final boolean subset;

    private static final boolean DEBUG_SEL = false;

    ReadVector(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
        super(ast);
        this.lhs = updateParent(lhs);
        this.indexes = updateParent(indexes);
        this.subset = subset;
    }

    enum Failure {
        NOT_ARRAY_BASE,
        NOT_ARRAY_INDEX,
        NOT_INT_INDEX,
        NOT_INT_SEQUENCE_INDEX,
        NOT_DOUBLE_INDEX,
        NOT_ALL_POSITIVE_INDEX,
        NOT_INT_OR_DOUBLE_INDEX,
        NOT_INT_DOUBLE_OR_LOGICAL_INDEX,
        NOT_LOGICAL_INDEX,
        NOT_ONE_ELEMENT,
        NA_INDEX,
        NOT_POSITIVE_INDEX,
        INDEX_OUT_OF_BOUNDS,
        NOT_SAME_LENGTH,
        NOT_SUBSET,
        UNSPECIFIED
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RAny base = (RAny) lhs.execute(context, frame);  // note: order is important
        RAny index = (RAny) indexes[0].execute(context, frame);
        return execute(context, index, base);
    }

    abstract RAny execute(RContext context, RAny index, RAny vector);

    private abstract static class Select {
        abstract RAny select(RAny vector, int index) throws UnexpectedResultException;
    }

    private static Select createScalarSelect(boolean subset, RAny vectorTemplate) {
        if (subset) {
            return new Select() {
                @Override
                final RAny select(RAny vector, int index) throws UnexpectedResultException {
                    if (!(vector instanceof RArray)) {
                        throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                    }
                    RArray vrarr = (RArray) vector;
                    if (index > vrarr.size()) {
                        throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                    }
                    return vrarr.boxedGet(index - 1);
                }
            };
        } else if (!(vectorTemplate instanceof RList)) {
            return new Select() {
                @Override
                final RAny select(RAny vector, int index) throws UnexpectedResultException {
                    if (vector instanceof RList || !(vector instanceof RArray)) {
                        throw new UnexpectedResultException(Failure.UNSPECIFIED);
                    }
                    RArray vrarr = (RArray) vector;
                    if (index > vrarr.size()) {
                        throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                    }
                    return vrarr.boxedGet(index - 1);
                }
            };
        } else {
            return new Select() {
                @Override
                final RAny select(RAny vector, int index) throws UnexpectedResultException {
                    if (!(vector instanceof RList)) {
                        throw new UnexpectedResultException(Failure.UNSPECIFIED);
                    }
                    RList vlist = (RList) vector;
                    if (index > vlist.size()) {
                        throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                    }
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

        @Override
        public RAny execute(RContext context, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleScalarIntSelection");
            try {
                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray vrarr = (RArray) vector;
                if (!(index instanceof RInt)) {
                    throw new UnexpectedResultException(Failure.NOT_INT_INDEX);
                }
                RInt irint = (RInt) index;
                if (irint.size() != 1) {
                    throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
                }
                int i = irint.getInt(0);
                if (i <= 0 || i > vrarr.size()) {
                    throw new UnexpectedResultException(Failure.UNSPECIFIED); // includes NA_INDEX, NOT_POSITIVE_INDEX, INDEX_OUT_OF_BOUNDS
                }
                if (subset || !(vrarr instanceof RList)) {
                    return vrarr.boxedGet(i - 1);
                } else {
                    return ((RList) vrarr).getRAny(i - 1);
                }
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarIntSelection failed: " + f);
                switch(f) {
                    case NOT_INT_INDEX:
                        SimpleScalarDoubleSelection dbl = new SimpleScalarDoubleSelection(ast, lhs, indexes, subset, vector);
                        replace(dbl, "install SimpleScalarDoubleSelection from SimpleScalarIntSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleScalarDoubleSelection");
                        return dbl.execute(context, index, vector);

                    case NOT_ONE_ELEMENT:
                        if (subset) {
                            if (index instanceof IntImpl.RIntSequence) {
                                SimpleIntSequenceSelection is = new SimpleIntSequenceSelection(ast, lhs, indexes, subset);
                                replace(is, "install SimpleIntSequenceSelection from SimpleScalarIntSelection");
                                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleIntSequenceSelection");
                                return is.execute(context, index, vector);
                            } else {
                                IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                                replace(is, "install IntSelection from SimpleScalarIntSelection");
                                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                                return is.execute(context, index, vector);
                            }
                        } else {
                            Subscript s = new Subscript(ast, lhs, indexes, subset);
                            replace(s, "install Subscript from SimpleScalarIntSelection");
                            if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                            return s.execute(context, index, vector);
                        }

                    default:
                        GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                        replace(gen, "install GenericScalarSelection from SimpleScalarIntSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                        return gen.execute(context, index, vector);

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

        @Override
        public Object execute(RContext context, Frame frame) {
            RAny base = (RAny) lhs.execute(context, frame);
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray vrarr = (RArray) base;
                if (index > vrarr.size()) {
                    throw new UnexpectedResultException(Failure.UNSPECIFIED); // includes NA_INDEX, NOT_POSITIVE_INDEX, INDEX_OUT_OF_BOUNDS
                }
                if (subset || !(vrarr instanceof RList)) {
                    return vrarr.boxedGet(index - 1);
                } else {
                    return ((RList) vrarr).getRAny(index - 1);
                }
            } catch (UnexpectedResultException e) {
                GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                replace(gen, "");
                return gen.execute(context, (RAny) indexes[0].execute(context, frame), base);
            }
        }

        @Override
        public RAny execute(RContext context, RAny index, RAny vector) {
            return null;
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

        @Override
        public RAny execute(RContext context, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleScalarDoubleSelection");
            try {
                if (!(index instanceof RDouble)) {
                    throw new UnexpectedResultException(Failure.NOT_DOUBLE_INDEX);
                }
                RDouble irdbl = (RDouble) index;
                if (irdbl.size() != 1) {
                    throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
                }
                int i = Convert.double2int(irdbl.getDouble(0)); // FIXME: check when the index is too large

                // FIXME: surprisingly using select did not work well in binarytrees benchmark, where the base alternates between list and integer
//                if (i <= 0) {
//                    throw new UnexpectedResultException(Failure.NOT_POSITIVE_INDEX); // includes NA_INDEX
//                }
//                return select.select(vector, i);

                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray vrarr = (RArray) vector;
                if (i <= 0 || i > vrarr.size()) {
                    throw new UnexpectedResultException(Failure.UNSPECIFIED); // includes NA_INDEX, NOT_POSITIVE_INDEX, INDEX_OUT_OF_BOUNDS
                }
                if (subset || !(vrarr instanceof RList)) {
                    return vrarr.boxedGet(i - 1);
                } else {
                    return ((RList) vrarr).getRAny(i - 1);
                }
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarDoubleSelection failed: " + f);
                switch(f) {
                    case NOT_ONE_ELEMENT:
                        if (subset) {
                            IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                            replace(is, "install IntSelection from SimpleScalarDoubleSelection");
                            if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                            return is.execute(context, index, vector);
                        } else {
                            Subscript s = new Subscript(ast, lhs, indexes, subset);
                            replace(s, "install Subscript from SimpleScalarDoubleSelection");
                            if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                            return s.execute(context, index, vector);
                        }
                    default:
                        GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                        replace(gen, "install GenericScalarSelection from SimpleScalarDoubleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                        return gen.execute(context, index, vector);
                }
            }
        }
    }

    // any case when the index has only one argument (a scalar)
    //   rewrite itself when index is not a single scalar
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
                    if (i == RLogical.TRUE) {
                        return base;
                    }
                    if (i == RLogical.FALSE) {
                        return Utils.createEmptyArray(base);
                    }
                }
            }

            if (i > 0) { // NOTE: RInt.NA < 0
                if (i <= size) {
                    if (subset || !(base instanceof RList)) {
                        return base.boxedGet(i - 1);
                    } else {
                        return ((RList) base).getRAny(i - 1);
                    }
                }
            }

            if (size == 0) {
                return RNull.getNull();
            }
            if (!subset) {
                if (i == 0) {
                    throw RError.getSelectLessThanOne(ast);
                }
                if (i > 0 || i == RInt.NA) { // means also i > size
                    throw RError.getSubscriptBounds(ast);
                }
                // i < 0
                if (size > 2) {
                    throw RError.getSelectMoreThanOne(ast);
                }
                if (size == 1) {
                    throw RError.getSelectLessThanOne(ast);
                }
                // size == 2
                if (i != -1 && i != -2) {
                    throw RError.getSelectMoreThanOne(ast);
                }
            }
            if (base instanceof RDouble) { // FIXME: could reduce verbosity through refactoring the number factories?
                if (i == RInt.NA) {
                    return RDouble.RDoubleFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return base;
                    }
                    return RDouble.RDoubleFactory.exclude(-i - 1, (RDouble) base);
                }
                if (i == 0) {
                    return RDouble.EMPTY;
                }
                // i > size
                return RDouble.BOXED_NA;
            }
            if (base instanceof RInt) {
                if (i == RInt.NA) {
                    return RInt.RIntFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return base;
                    }
                    return RInt.RIntFactory.exclude(-i - 1, (RInt) base);
                }
                if (i == 0) {
                    return RInt.EMPTY;
                }
                // i > size
                return RInt.BOXED_NA;
            }
            if (base instanceof RLogical) {
                if (i == RInt.NA) {
                    return RLogical.RLogicalFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return base;
                    }
                    return RLogical.RLogicalFactory.exclude(-i - 1, (RLogical) base);
                }
                if (i == 0) {
                    return RLogical.EMPTY;
                }
                // i > size
                return RLogical.BOXED_NA;
            }
            if (base instanceof RList) {
                if (i == RInt.NA) {
                    return RList.RListFactory.getNullArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return base;
                    }
                    return RList.RListFactory.exclude(-i - 1, (RList) base);
                }
                if (i == 0) {
                    return RList.EMPTY;
                }
                // i > size
                return RList.NULL;
            }
            if (base instanceof RString) {
                if (i == RInt.NA) {
                    return RString.RStringFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return base;
                    }
                    return RString.RStringFactory.exclude(-i - 1, (RString) base);
                }
                if (i == 0) {
                    return RString.EMPTY;
                }
                // i > size
                return RString.BOXED_NA;
            }
            Utils.nyi("unsupported vector type for subscript");
            return null;
        }

        @Override
        public RAny execute(RContext context, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing GenericScalarSelection");
            try {
                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray vrarr = (RArray) vector;
                if (!(index instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_INDEX);
                }
                RArray irarr = (RArray) index;
                if (irarr.size() != 1) {
                    throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
                }
                return executeScalar(vrarr, irarr, subset, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - GenericScalarSelection failed: " + f);

                if (f == Failure.NOT_ONE_ELEMENT && index instanceof RLogical && subset) {
                    SimpleLogicalSelection ls = new SimpleLogicalSelection(ast, lhs, indexes, subset);
                    replace(ls, "install SimpleLogicalSelection from GenericScalarSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleLogicalSelection");
                    return ls.execute(context, index, vector);
                } else {
                    if (!subset) {
                        Subscript s = new Subscript(ast, lhs, indexes, subset);
                        replace(s, "install Subscript from GenericScalarSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with Subscript");
                        return s.execute(context, index, vector);
                    } else {
                        GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                        replace(gs, "install GenericSelection from GenericScalarSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                        return gs.execute(context, index, vector);
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

        @Override
        public RAny execute(RContext context, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleIntSequenceSelection");
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray abase = (RArray) base;
                if (!(index instanceof IntImpl.RIntSequence)) { // FIXME: this goes directly to Int implementation, not terribly nice
                    throw new UnexpectedResultException(Failure.NOT_INT_SEQUENCE_INDEX);
                }
                IntImpl.RIntSequence sindex = (IntImpl.RIntSequence) index;

                if (!sindex.isPositive()) {
                    throw new UnexpectedResultException(Failure.NOT_ALL_POSITIVE_INDEX);
                }
                int size = abase.size();
                if (sindex.max() > size) {
                    throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                }
                // FIXME: should specialize for a particular base type, or have a type hierarchy on factories
                if (abase instanceof RDouble) {
                    return new RDoubleView((RDouble) abase, sindex.from(), sindex.to(), sindex.step());
                }
                if (abase instanceof RInt) {
                    return new RIntView((RInt) abase, sindex.from(), sindex.to(), sindex.step());
                }
                if (abase instanceof RLogical) {
                    return new RLogicalView((RLogical) abase, sindex.from(), sindex.to(), sindex.step());
                }
                if (abase instanceof RList) {
                    return new RListView((RList) abase, sindex.from(), sindex.to(), sindex.step());
                }
                if (abase instanceof RString) {
                    return new RStringView((RString) abase, sindex.from(), sindex.to(), sindex.step());
                }
                Utils.nyi("unsupported base vector type");
                return null;
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleIntSequenceSelection failed: " + f);
                switch(f) {
                    case NOT_INT_SEQUENCE_INDEX:
                    case NOT_ALL_POSITIVE_INDEX:
                    case INDEX_OUT_OF_BOUNDS:
                        IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                        replace(is, "install IntSelection from SimpleIntSequenceSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                        return is.execute(context, index, base);

                    default:
                        GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                        replace(gs, "install GenericSelection from SimpleIntSequenceSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                        return gs.execute(context, index, base);
                }
            }
        }

        static class RIntView extends View.RIntView implements RInt {
            final RInt base;
            final int from;
            final int to;
            final int step;

            final int size;

            public RIntView(RInt base, int from, int to, int step) {
                this.base = base;
                this.from = from;
                this.to = to;
                this.step = step;

                int absstep = (step > 0) ? step : -step;
                if (from <= to) {
                    size = (to - from + 1) / absstep;
                } else {
                    size = (from - to + 1) / absstep;
                }
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public int getInt(int i) {
                Utils.check(i < size, "bounds check");
                Utils.check(i >= 0, "bounds check");
                return base.getInt(from + i * step - 1);
            }

            @Override
            public boolean isSharedReal() {
                return base.isShared();
            }

            @Override
            public void ref() {
                base.ref();
            }
        }

        static class RDoubleView extends View.RDoubleView implements RDouble {
            final RDouble base;
            final int from;
            final int to;
            final int step;

            final int size;

            public RDoubleView(RDouble base, int from, int to, int step) {
                this.base = base;
                this.from = from;
                this.to = to;
                this.step = step;

                int absstep = (step > 0) ? step : -step;
                if (from <= to) {
                    size = (to - from + 1) / absstep;
                } else {
                    size = (from - to + 1) / absstep;
                }
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public double getDouble(int i) {
                Utils.check(i < size, "bounds check");
                Utils.check(i >= 0, "bounds check");
                return base.getDouble(from + i * step - 1);
            }

            @Override
            public boolean isSharedReal() {
                return base.isShared();
            }

            @Override
            public void ref() {
                base.ref();
            }
        }

        static class RLogicalView extends View.RLogicalView implements RLogical {
            final RLogical base;
            final int from;
            final int to;
            final int step;

            final int size;

            public RLogicalView(RLogical base, int from, int to, int step) {
                this.base = base;
                this.from = from;
                this.to = to;
                this.step = step;

                int absstep = (step > 0) ? step : -step;
                if (from <= to) {
                    size = (to - from + 1) / absstep;
                } else {
                    size = (from - to + 1) / absstep;
                }
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public int getLogical(int i) {
                Utils.check(i < size, "bounds check");
                Utils.check(i >= 0, "bounds check");
                return base.getLogical(from + i * step - 1);
            }

            @Override
            public boolean isSharedReal() {
                return base.isShared();
            }

            @Override
            public void ref() {
                base.ref();
            }
        }

        static class RListView extends View.RListView implements RList {
            final RList base;
            final int from;
            final int to;
            final int step;

            final int size;

            public RListView(RList base, int from, int to, int step) {
                this.base = base;
                this.from = from;
                this.to = to;
                this.step = step;

                int absstep = (step > 0) ? step : -step;
                if (from <= to) {
                    size = (to - from + 1) / absstep;
                } else {
                    size = (from - to + 1) / absstep;
                }
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public RAny getRAny(int i) {
                Utils.check(i < size, "bounds check");
                Utils.check(i >= 0, "bounds check");
                return base.getRAny(from + i * step - 1);
            }

            @Override
            public boolean isSharedReal() {
                return base.isShared();
            }

            @Override
            public void ref() {
                base.ref();
            }
        }

        static class RStringView extends View.RStringView implements RString {
            final RString base;
            final int from;
            final int to;
            final int step;

            final int size;

            public RStringView(RString base, int from, int to, int step) {
                this.base = base;
                this.from = from;
                this.to = to;
                this.step = step;

                int absstep = (step > 0) ? step : -step;
                if (from <= to) {
                    size = (to - from + 1) / absstep;
                } else {
                    size = (from - to + 1) / absstep;
                }
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public String getString(int i) {
                Utils.check(i < size, "bounds check");
                Utils.check(i >= 0, "bounds check");
                return base.getString(from + i * step - 1);
            }

            @Override
            public boolean isSharedReal() {
                return base.isShared();
            }

            @Override
            public void ref() {
                base.ref();
            }
        }

    }

    // when the index is a vector of integers (selection by index)
    //   casts double index to integer
    //   rewrites itself for other and corner cases
    public static class IntSelection extends ReadVector { // FIXME: create yet another node without the negatives and zero crap
        public IntSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
            Utils.check(subset);
        }

        public static RAny executeIntVector(RInt index, RArray base, ASTNode ast) {
            int nzeros = 0;
            boolean hasNegative = false;
            boolean hasPositive = false;
            boolean hasNA = false;
            int bsize = base.size();
            int isize = index.size();
            boolean[] omit = null;
            int nomit = 0;

            for (int i = 0; i < isize; i++) {  // FIXME: does that really pay off creating a view (not materializing) given that we have to
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
                if (v < 0) {
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
            }
            boolean hasZero = nzeros > 0;

            if (!hasNegative) {
                if (!hasZero) {
                    return base.subset(index);
                }
                // positive and zero indexes (and perhaps NAs)
                int nsize = isize - nzeros;
                RArray res = Utils.createArray(base, nsize);
                int j = 0;
                for (int i = 0; i < isize; i++) {
                    int v = index.getInt(i);
                    if (v == 0) {
                        continue;
                    }
                    if (v == RInt.NA) {
                        Utils.setNA(res, j++);
                        continue;
                    }
                    if (v <= bsize) {
                        res.set(j++, base.get(v - 1));
                        continue;
                    }
                    Utils.setNA(res, j++);
                }
                return res;
            } else { // hasNegative == true
                if (hasPositive || hasNA) {
                    throw RError.getOnlyZeroMixed(ast);
                }
                // negative and zero indexes
                int nsize = bsize - nomit;
                RArray res = Utils.createArray(base, nsize);
                int j = 0;
                for (int i = 0; i < bsize; i++) {
                    if (!omit[i]) {
                        res.set(j++, base.get(i));
                    }
                }
                return res;
            }
        }

        @Override
        public RAny execute(RContext context, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing IntSelection");
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
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
                return gs.execute(context, index, base);
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

        @Override
        public RAny execute(RContext context, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleLogicalSelection");
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray abase = (RArray) base;
                if (!(index instanceof RLogical)) {
                    throw new UnexpectedResultException(Failure.NOT_LOGICAL_INDEX);
                }
                RLogical lindex = (RLogical) index;
                int isize = lindex.size();
                int bsize = abase.size();
                if (isize != bsize) {
                    throw new UnexpectedResultException(Failure.NOT_SAME_LENGTH);
                }
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
                switch(f) {
                    case NOT_SAME_LENGTH:
                        LogicalSelection ls = new LogicalSelection(ast, lhs, indexes, subset);
                        replace(ls, "install LogicalSelection from SimpleLogicalSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with LogicalSelection");
                        return ls.execute(context, index, base);

                    default:
                        GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                        replace(gs, "install GenericSelection from SimpleLogicalSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                        return gs.execute(context, index, base);
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

            if (isize >= bsize) {
                // no re-use of index, but index can be longer than base
                int nsize = 0;
                for (int i = 0; i < isize; i++) {
                    if (index.getLogical(i) != RLogical.FALSE) {
                        nsize++;
                    }
                }
                RArray res = Utils.createArray(base, nsize);
                int j = 0;
                int i = 0;
                for (; i < bsize; i++) {
                    int l = index.getLogical(i);
                    if (l == RLogical.TRUE) {
                        res.set(j++, base.get(i));
                    } else if (l == RLogical.NA) {
                        Utils.setNA(res, j++);
                    }
                }
                for (; i < isize; i++) {
                    int l = index.getLogical(i);
                    if (l != RLogical.FALSE) {
                        Utils.setNA(res, j++);
                    }
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
                RArray res = Utils.createArray(base, nsize);
                j = 0;
                int k = 0;
                for (int i = 0; i < bsize; i++) {
                    int l = index.getLogical(j);
                    if (l == RLogical.TRUE) {
                        res.set(k++, base.get(i));
                    } else if (l == RLogical.NA) {
                        Utils.setNA(res, k++);
                    }
                    j++;
                    if (j == isize) {
                        j = 0;
                    }
                }
                return res;
            }
        }

        @Override
        public RAny execute(RContext context, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleLogicalSelection");
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                if (!(index instanceof RLogical)) {
                    throw new UnexpectedResultException(Failure.NOT_LOGICAL_INDEX);
                }
                return executeLogicalVector((RLogical) index, (RArray) base);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - LogicalSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                replace(gs, "install GenericSelection from LogicalSelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(context, index, base);
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

            if (bsize > 2) {
                throw RError.getSelectMoreThanOne(ast);
            }
            if (bsize < 2) {
                throw RError.getSelectLessThanOne(ast);
            }
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
                    throw RError.getGenericError(ast, String.format(RError.NO_SUCH_INDEX, iindex + 1));
                }
            } else {
                if (indexv == RInt.NA) {
                    throw RError.getGenericError(ast, String.format(RError.NO_SUCH_INDEX, iindex + 1));
                }
                isel = convertNegativeNonNAIndex(indexv, bsize, ast);
            }
            return isel;
        }

        public static RAny executeSubscript(RInt index, RArray base, ASTNode ast) {
            final int isize = index.size();
            if (isize == 0) {
                throw RError.getSelectLessThanOne(ast);
            }
            int i = 0;
            RAny b = base;

            if (isize > 1) {
                // the upper levels of recursive indexes have to be treated differently from the lowest level (error semantics is different)
                // also, we know that upper levels must be lists
                for (; i < isize - 1; i++) {
                    if (!(b instanceof RList)) {
                        throw RError.getSelectMoreThanOne(ast);
                    }
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
                Utils.nyi("unuspported base type");
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

        @Override
        public RAny execute(RContext context, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing Subscript");
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray abase = (RArray) base;
                RInt iindex;
                if (index instanceof RInt) {
                    iindex = (RInt) index;
                } else if (index instanceof RDouble || index instanceof RLogical) {
                    iindex = index.asInt();
                } else {
                    throw new UnexpectedResultException(Failure.NOT_INT_DOUBLE_OR_LOGICAL_INDEX);
                }
                return executeSubscript(iindex, abase, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - Subscript failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset); // rewriting itself only to handle the error, there is no way to recover
                replace(gs, "install GenericSelection from Subscript");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(context, index, base);
            }
        }
    }

    // any selection, won't rewrite itself
    public static class GenericSelection extends ReadVector {
        public GenericSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public RAny execute(RContext context, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing GenericSelection");
            if (!(base instanceof RArray)) {
                Utils.nyi("unsupported base");
            }
            RArray abase = (RArray) base;
            if (!(index instanceof RArray) || index instanceof RList) {
                Utils.nyi("unsupported index");
            }
            RArray aindex = (RArray) index;
            int isize = aindex.size();
            if (isize == 1) {
                return GenericScalarSelection.executeScalar(abase, aindex, subset, ast);
            }
            if (subset) {
                if (aindex instanceof RInt) {
                    return IntSelection.executeIntVector((RInt) aindex, abase, ast);
                } else if (aindex instanceof RDouble) {
                    return IntSelection.executeIntVector(aindex.asInt(), abase, ast);
                } else if (aindex instanceof RLogical) {
                    return LogicalSelection.executeLogicalVector((RLogical) aindex, abase);
                } else if (aindex instanceof RNull) {
                    return Utils.createEmptyArray(abase);
                }
            } else {
                return Subscript.executeSubscript(aindex.asInt(), abase, ast);
            }
            Utils.nyi("vector in generic selection");
            return null;
        }
    }
}
