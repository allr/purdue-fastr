package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

// rewriting of vector selection nodes:
//
// *SimpleScalarIntSelection   -> SimpleScalarDoubleSelection -> GenericScalarSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> SimpleLogicalSelection -> LogicalSelection -> GenericSelection
//                             -> SimpleScalarDoubleSelection -> GenericScalarSelection -> SimpleLogicalSelection -> GenericSelection
//                             -> GenericScalarSelection -> GenericSelection
//                             -> SimpleIntSequenceSelection -> (below)
//                             -> IntSelection -> GenericSelection
//                             -
// *SimpleIntSequenceSelection -> IntSelection -> GenericSelection
//                             -> GenericSelection
//
public abstract class ReadVector extends BaseR {

    RNode lhs;
    RNode[] indexes;
    final boolean subset;

    private static final boolean DEBUG_SEL = true;

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
        NOT_LOGICAL_INDEX,
        NOT_ONE_ELEMENT,
        NA_INDEX,
        NOT_POSITIVE_INDEX,
        INDEX_OUT_OF_BOUNDS,
        NOT_SAME_LENGTH,
        NOT_SUBSET
    }

    // when the index has only one argument, which is an integer
    //   for more complicated and corner cases rewrites itself
    public static class SimpleScalarIntSelection extends ReadVector {
        public SimpleScalarIntSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
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
                if (i == RInt.NA) {
                    throw new UnexpectedResultException(Failure.NA_INDEX);
                }
                if (i <= 0) {
                    throw new UnexpectedResultException(Failure.NOT_POSITIVE_INDEX);
                }
                if (i > vrarr.size()) {
                    throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                }
                return vrarr.boxedGet(i - 1);
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarIntSelection failed: " + f);
                switch(f) {
                    case NOT_INT_INDEX:
                        SimpleScalarDoubleSelection dbl = new SimpleScalarDoubleSelection(ast, lhs, indexes, subset);
                        replace(dbl, "install SimpleScalarDoubleSelection from SimpleScalarIntSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleScalarDoubleSelection");
                        return dbl.execute(context, frame, index, vector);

                    case NOT_ONE_ELEMENT:
                        if (subset) {
                            if (index instanceof IntImpl.RIntSequence) {
                                SimpleIntSequenceSelection is = new SimpleIntSequenceSelection(ast, lhs, indexes, subset);
                                replace(is, "install SimpleIntSequenceSelection from SimpleScalarIntSelection");
                                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleIntSequenceSelection");
                                return is.execute(context, frame, index, vector);
                            } else {
                                IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                                replace(is, "install IntSelection from SimpleScalarIntSelection");
                                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                                return is.execute(context, frame, index, vector);
                            }
                        } // propagate below
                    default:
                        GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                        replace(gen, "install GenericScalarSelection from SimpleScalarIntSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                        return gen.execute(context, frame, index, vector);

                }
            }
        }
    }

    // when the index has only one argument, which is a double
    //   for more complicated and corner cases rewrites itself
    public static class SimpleScalarDoubleSelection extends ReadVector {
        public SimpleScalarDoubleSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleScalarDoubleSelection");
            try {
                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                RArray vrarr = (RArray) vector;
                if (!(index instanceof RDouble)) {
                    throw new UnexpectedResultException(Failure.NOT_DOUBLE_INDEX);
                }
                RDouble irdbl = (RDouble) index;
                if (irdbl.size() != 1) {
                    throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
                }
                int i = Convert.double2int(irdbl.getDouble(0)); // FIXME: check when the index is too large
                if (i == RInt.NA) {
                    throw new UnexpectedResultException(Failure.NA_INDEX);
                }
                if (i <= 0) {
                    throw new UnexpectedResultException(Failure.NOT_POSITIVE_INDEX);
                }
                if (i > vrarr.size()) {
                    throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                }
                return vrarr.boxedGet(i - 1);
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - SimpleScalarDoubleSelection failed: " + f);
                switch(f) {
                    case NOT_ONE_ELEMENT:
                        if (subset) {
                            IntSelection is = new IntSelection(ast, lhs, indexes, subset);
                            replace(is, "install IntSelection from SimpleScalarDoubleSelection");
                            if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with IntSelection");
                            return is.execute(context, frame, index, vector);
                        } // propagate below
                    default:
                        GenericScalarSelection gen = new GenericScalarSelection(ast, lhs, indexes, subset);
                        replace(gen, "install GenericScalarSelection from SimpleScalarDoubleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericScalarSelection");
                        return gen.execute(context, frame, index, vector);
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

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        // index must be a scalar
        public static RAny executeScalar(RContext context, RFrame frame, RArray vector, RArray index, boolean subset, ASTNode ast) {
            int i = 0;
            int size = vector.size();
            if (index instanceof RDouble) {
                RDouble idbl = (RDouble) index;
                i = Convert.double2int(idbl.getDouble(0)); // FIXME: check when the index is too large
            } else if (index instanceof RInt) {
                i = ((RInt) index).getInt(0);
            } else if (index instanceof RLogical) {
                i = ((RLogical) index).getLogical(0);
                if (subset) {
                    if (i == RLogical.TRUE) {
                        return vector;
                    }
                    if (i == RLogical.FALSE) {
                        if (vector instanceof RDouble) { // FIXME: to reduce verbosity could make "empty" a method, but this may be faster
                            return RDouble.EMPTY;
                        }
                        if (vector instanceof RInt) {
                            return RInt.EMPTY;
                        }
                        if (vector instanceof RLogical) {
                            return RLogical.EMPTY;
                        }
                        Utils.nyi("unsupported vector type");
                    }
                }
            }

            if (i > 0) { // NOTE: RInt.NA < 0
                if (i <= size) {
                    return vector.boxedGet(i - 1);
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
                if (i != 1 && i != 2) {
                    throw RError.getSelectMoreThanOne(ast);
                }
            }
            if (vector instanceof RDouble) { // FIXME: could reduce verbosity through refactoring the number factories?
                if (i == RInt.NA) {
                    return RDouble.RDoubleFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return vector;
                    }
                    return RDouble.RDoubleFactory.exclude(-i - 1, (RDouble) vector);
                }
                if (i == 0) {
                    return RDouble.EMPTY;
                }
                // i > size
                return RDouble.BOXED_NA;
            }
            if (vector instanceof RInt) {
                if (i == RInt.NA) {
                    return RInt.RIntFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return vector;
                    }
                    return RInt.RIntFactory.exclude(-i - 1, (RInt) vector);
                }
                if (i == 0) {
                    return RInt.EMPTY;
                }
                // i > size
                return RInt.BOXED_NA;
            }
            if (vector instanceof RLogical) {
                if (i == RInt.NA) {
                    return RLogical.RLogicalFactory.getNAArray(size);
                }
                if (i < 0) {
                    if (-i > size) {
                        return vector;
                    }
                    return RLogical.RLogicalFactory.exclude(-i - 1, (RLogical) vector);
                }
                if (i == 0) {
                    return RLogical.EMPTY;
                }
                // i > size
                return RLogical.BOXED_NA;
            }
            Utils.nyi("unsupported vector type for subscript");
            return null;
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
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
                return executeScalar(context, frame, vrarr, irarr, subset, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - GenericScalarSelection failed: " + f);

                if (f == Failure.NOT_ONE_ELEMENT && index instanceof RLogical && subset) {
                    SimpleLogicalSelection ls = new SimpleLogicalSelection(ast, lhs, indexes, subset);
                    replace(ls, "install SimpleLogicalSelection from GenericScalarSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleLogicalSelection");
                    return ls.execute(context, frame, index, vector);
                } else {
                    GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                    replace(gs, "install GenericSelection from GenericScalarSelection");
                    if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                    return gs.execute(context, frame, index, vector);
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
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny base = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, base);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny base) {
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
                //Utils.debug("Index is from "+sindex.min()+" to "+sindex.max());
                if (sindex.max() > size) {
                    throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                }
                // FIXME: should specialize for a particular base type
                if (abase instanceof RDouble) {
                    return new RDoubleView((RDouble) abase, sindex.from(), sindex.to(), sindex.step());
                }
                if (abase instanceof RInt) {
                    return new RIntView((RInt) abase, sindex.from(), sindex.to(), sindex.step());
                }
                if (abase instanceof RLogical) {
                    return new RLogicalView((RLogical) abase, sindex.from(), sindex.to(), sindex.step());
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
                        return is.execute(context, frame, index, base);

                    default:
                        GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                        replace(gs, "install GenericSelection from SimpleIntSequenceSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                        return gs.execute(context, frame, index, base);
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

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny base = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, base);
        }

        public static RAny executeIntVector(RContext context, RFrame frame, RInt index, RArray base, ASTNode ast) {
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

        public RAny execute(RContext context, RFrame frame, RAny index, RAny base) {
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
                return executeIntVector(context, frame, iindex, abase, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - IntSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                replace(gs, "install GenericSelection from IntSelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(context, frame, index, base);
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
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny base = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, base);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny base) {
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
                        return ls.execute(context, frame, index, base);

                    default:
                        GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                        replace(gs, "install GenericSelection from SimpleLogicalSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                        return gs.execute(context, frame, index, base);
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

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny base = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, base);
        }

        public static RAny executeLogicalVector(RContext context, RFrame frame, RLogical index, RArray base, ASTNode ast) {
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

        public RAny execute(RContext context, RFrame frame, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleLogicalSelection");
            try {
                if (!(base instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                }
                if (!(index instanceof RLogical)) {
                    throw new UnexpectedResultException(Failure.NOT_LOGICAL_INDEX);
                }
                return executeLogicalVector(context, frame, (RLogical) index, (RArray) base, ast);

            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - LogicalSelection failed: " + f);
                GenericSelection gs = new GenericSelection(ast, lhs, indexes, subset);
                replace(gs, "install GenericSelection from LogicalSelection");
                if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSelection");
                return gs.execute(context, frame, index, base);
            }
        }
    }

    // any selection, won't rewrite itself
    public static class GenericSelection extends ReadVector {
        public GenericSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny base) {
            if (DEBUG_SEL) Utils.debug("selection - executing GenericSelection");
            if (!(base instanceof RArray)) {
                Utils.nyi("unsupported base");
            }
            RArray abase = (RArray) base;
            if (!(index instanceof RArray)) {
                Utils.nyi("unsupported index");
            }
            RArray aindex = (RArray) index;
            int isize = aindex.size();
            if (isize == 1) {
                return GenericScalarSelection.executeScalar(context, frame, abase, aindex, subset, ast);
            }
            if (subset) {
                if (aindex instanceof RInt) {
                    return IntSelection.executeIntVector(context, frame, (RInt) aindex, abase, ast);
                } else if (aindex instanceof RDouble) {
                    return IntSelection.executeIntVector(context, frame, aindex.asInt(), abase, ast);
                } else if (aindex instanceof RLogical) {
                    return LogicalSelection.executeLogicalVector(context, frame, (RLogical) aindex, abase, ast);
                }
            } else {
                if (isize > 1) {
                    throw RError.getSelectMoreThanOne(ast);
                } else {
                    throw RError.getSelectLessThanOne(ast);
                }
            }
            Utils.nyi("vector in generic selection");
            return null;
        }
    }


}
