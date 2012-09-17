package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;

public abstract class ReadVector extends BaseR {

    RNode lhs;
    RNode[] indexes;
    final boolean subset;

    private static final boolean DEBUG_SEL = false;

    ReadVector(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
        super(ast);
        this.lhs = updateParent(lhs);
        this.indexes = updateParent(indexes);
        this.subset = subset;
    }

    enum Failure {
        NOT_ARRAY,
        NOT_INT_INDEX,
        NOT_DOUBLE_INDEX,
        NOT_ONE_ELEMENT,
        NA_INDEX,
        NOT_POSITIVE_INDEX,
        INDEX_OUT_OF_BOUNDS,
    }

    // when the index has only one argument, which is an integer
    public static class SimpleSelection extends ReadVector {
        public SimpleSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleSelection");
            try {
                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY);
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
                if (DEBUG_SEL) Utils.debug("selection - SimpleSelection failed: " + f);
                switch(f) {
                    case NOT_INT_INDEX:
                        SimpleDoubleSelection dbl = new SimpleDoubleSelection(ast, lhs, indexes, subset);
                        replace(dbl, "install SimpleDoubleSubscript from SimpleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with SimpleDoubleSelection");
                        return dbl.execute(context, frame, index, vector);

                    case NOT_ARRAY:
                    case NA_INDEX:
                    case NOT_POSITIVE_INDEX:
                    case INDEX_OUT_OF_BOUNDS:
                        GenericSubscript gen = new GenericSubscript(ast, lhs, indexes, subset);
                        replace(gen, "install GenericSubscript from SimpleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSubscript");
                        return gen.execute(context, frame, index, vector);
                }
                Utils.nyi("nontrivial vector subscript");
                return null;
            }
        }
    }

    // when the index has only one argument, which is a double
    public static class SimpleDoubleSelection extends ReadVector {
        public SimpleDoubleSelection(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing SimpleDoubleSelection");
            try {
                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY);
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
                if (DEBUG_SEL) Utils.debug("selection SimpleDoubleSelection failed: " + f);
                switch(f) {
                    case NOT_DOUBLE_INDEX:
                    case NOT_ARRAY:
                    case NA_INDEX:
                    case NOT_POSITIVE_INDEX:
                    case INDEX_OUT_OF_BOUNDS:
                        GenericSubscript gen = new GenericSubscript(ast, lhs, indexes, subset);
                        replace(gen, "install GenericSubscript from SimpleDoubleSelection");
                        if (DEBUG_SEL) Utils.debug("selection - replaced and re-executing with GenericSubscript");
                        return gen.execute(context, frame, index, vector);
                }

                Utils.nyi("nontrivial vector subscript" + e);
                return null;
            }
        }
    }

    // any case when the index has only one argument (a scalar)
    public static class GenericSubscript extends ReadVector {
        public GenericSubscript(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
            if (DEBUG_SEL) Utils.debug("selection - executing GenericSubscript");
            try {
                if (!(vector instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY);
                }
                RArray irarr = (RArray) index;
                if (irarr.size() != 1) {
                    throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
                }
                int i = 0;
                RArray vrarr = (RArray) vector;
                int size = vrarr.size();
                if (index instanceof RDouble) {
                    RDouble idbl = (RDouble) irarr;
                    i = Convert.double2int(idbl.getDouble(0)); // FIXME: check when the index is too large
                } else if (index instanceof RInt) {
                    i = ((RInt) irarr).getInt(0);
                } else if (index instanceof RLogical) {
                    i = ((RLogical) irarr).getLogical(0);
                    if (i == RLogical.TRUE) {
                        return vector;
                    }
                    if (i == RLogical.FALSE) {
                        if (vrarr instanceof RDouble) { // FIXME: to reduce verbosity could make "empty" a method, but this may be faster
                            return RDouble.EMPTY;
                        }
                        if (vrarr instanceof RInt) {
                            return RInt.EMPTY;
                        }
                        if (vrarr instanceof RLogical) {
                            return RLogical.EMPTY;
                        }
                        Utils.nyi("unsupported vector type");
                    }
                }

                if (i > 0) { // NOTE: RInt.NA < 0
                    if (i <= size) {
                        return vrarr.boxedGet(i - 1);
                    }
                }

                if (vrarr instanceof RDouble) { // FIXME: could reduce verbosity through refactoring the number factories?
                    if (i < 0) {
                        return RDouble.RDoubleFactory.exclude(-i - 1, (RDouble) vrarr);
                    }
                    if (i == 0) {
                        return RDouble.EMPTY;
                    }
                    if (i == RInt.NA) {
                        return RDouble.RDoubleFactory.getNAArray(size);
                    }
                    // i > size
                    return RDouble.BOXED_NA;
                }
                if (vrarr instanceof RInt) {
                    if (i < 0) {
                        return RInt.RIntFactory.exclude(-i - 1, (RInt) vrarr);
                    }
                    if (i == 0) {
                        return RInt.EMPTY;
                    }
                    if (i == RInt.NA) {
                        return RInt.RIntFactory.getNAArray(size);
                    }
                    // i > size
                    return RInt.BOXED_NA;
                }
                if (vrarr instanceof RLogical) {
                    if (i < 0) {
                        return RLogical.RLogicalFactory.exclude(-i - 1, (RLogical) vrarr);
                    }
                    if (i == 0) {
                        return RLogical.EMPTY;
                    }
                    if (i == RInt.NA) {
                        return RLogical.RLogicalFactory.getNAArray(size);
                    }
                    // i > size
                    return RLogical.BOXED_NA;
                }
                Utils.nyi("unsupported vector type for subscript");
                return null;
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_SEL) Utils.debug("selection - GenericSubscript failed: " + f);
                Utils.nyi("nontrivial vector subscript" + e);
                return null;
            }
        }
    }
}
