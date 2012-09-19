package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;


public abstract class UpdateVector extends BaseR {

    RNode lhs;
    RNode[] indexes;
    RNode rhs;
    final boolean subset;

    private static final boolean DEBUG_UP = true;

    UpdateVector(ASTNode ast, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
        super(ast);
        this.lhs = updateParent(lhs);
        this.indexes = updateParent(indexes);
        this.rhs = updateParent(rhs);
        this.subset = subset;
    }

    enum Failure {
        NOT_ARRAY_BASE,
        NOT_ONE_ELEMENT_INDEX,
        NOT_NUMERIC_INDEX,
        INDEX_OUT_OF_BOUNDS,
        NOT_ONE_ELEMENT_VALUE,
        NOT_ARRAY_VALUE,
        UNEXPECTED_TYPE
    }

    // for a numeric (int, double) scalar index and common case
    //   specializes itself for types of index,value
    //   only handles simple cases (no type conversion of base, no extension of base)
    public static class SimpleScalarNumericSelection extends UpdateVector {

        public SimpleScalarNumericSelection(ASTNode ast, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
            super(ast, lhs, indexes, rhs, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny base = (RAny) lhs.execute(context, frame);
            RAny value = (RAny) rhs.execute(context, frame);
            return execute(context, frame, base, index, value);
        }

        public RAny execute(RContext context, RFrame frame, RAny base, RAny index, RAny value) {
            if (DEBUG_UP) Utils.debug("update - executing SimpleScalarNumericSelection (uninitialized)");
            Specialized sn = createSpecialized(base, value);
            if (sn != null) {
                replace(sn, "specialize SimpleScalarNumericSelection");
                if (DEBUG_UP) Utils.debug("update - replaced and re-executing with SimpleScalarNumericSelection.Specialized");
                return sn.execute(context, frame, base, index, value);
            } else {
                Utils.nyi("unsupported vector update");
                return null;
            }
        }

        abstract class ValueCopy {
            abstract RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException;
        }

        public Specialized createSpecialized(RAny baseTemplate, RAny valueTemplate) {
            if (baseTemplate instanceof RInt) {
                if (valueTemplate instanceof RInt) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override
                        RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RInt) || !(value instanceof RInt)) {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            RInt ibase = (RInt) base;
                            int bsize = ibase.size();
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < pos; i++) {
                                content[i] = ibase.getInt(i);
                            }
                            content[i++] = ((RInt) value).getInt(0);
                            for (; i < bsize; i++) {
                                content[i] = ibase.getInt(i);
                            }
                            return RInt.RIntFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RInt,RInt>");
                }
                if (valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override
                        RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException  {
                            if (!(base instanceof RInt) || !(value instanceof RLogical)) {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            RInt ibase = (RInt) base;
                            int bsize = ibase.size();
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < pos; i++) {
                                content[i] = ibase.getInt(i);
                            }
                            content[i++] = ((RLogical) value).getLogical(0);
                            for (; i < bsize; i++) {
                                content[i] = ibase.getInt(i);
                            }
                            return RInt.RIntFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RInt,RLogical>");
                }
                return null;
            }
            if (baseTemplate instanceof RDouble) {
                if (valueTemplate instanceof RDouble) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override
                        RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble) || !(value instanceof RDouble)) {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            RDouble dbase = (RDouble) base;
                            int bsize = dbase.size();
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < pos; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            content[i++] = ((RDouble) value).getDouble(0);
                            for (; i < bsize; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RInt,RInt>");
                }
                if (valueTemplate instanceof RInt) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override
                        RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble) || !(value instanceof RInt)) {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            RDouble dbase = (RDouble) base;
                            int bsize = dbase.size();
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < pos; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            content[i++] = Convert.int2double(((RInt) value).getInt(0));
                            for (; i < bsize; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RInt,RInt>");
                }
                if (valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override
                        RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RDouble) || !(value instanceof RLogical)) {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            RDouble dbase = (RDouble) base;
                            int bsize = dbase.size();
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < pos; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            content[i++] = Convert.logical2double(((RLogical) value).getLogical(0));
                            for (; i < bsize; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RInt,RInt>");
                }
                return null;
            }
            if (baseTemplate instanceof RLogical) {
                if (valueTemplate instanceof RLogical) {
                    ValueCopy cpy = new ValueCopy() {
                        @Override
                        RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException {
                            if (!(base instanceof RLogical) || !(value instanceof RLogical)) {
                                throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                            }
                            RLogical lbase = (RLogical) base;
                            int bsize = lbase.size();
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < pos; i++) {
                                content[i] = lbase.getLogical(i);
                            }
                            content[i++] = ((RLogical) value).getLogical(0);
                            for (; i < bsize; i++) {
                                content[i] = lbase.getLogical(i);
                            }
                            return RLogical.RLogicalFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RInt,RInt>");
                }
            }
            Utils.nyi();
            return null;
        }

        // specialized for type combinations (base vector, value written)
        class Specialized extends SimpleScalarNumericSelection {
            final ValueCopy copy;
            final String dbg;

            Specialized(ASTNode ast, RNode lhs, RNode[] indexes, RNode rhs, boolean subset, ValueCopy copy, String dbg) {
                super(ast, lhs, indexes, rhs, subset);
                this.copy = copy;
                this.dbg = dbg;
            }

            @Override
            public RAny execute(RContext context, RFrame frame, RAny base, RAny index, RAny value) {
                if (DEBUG_UP) Utils.debug("update - executing SimpleScalarNumericSelection" + dbg);
                try {
                    if (!(base instanceof RArray)) {
                        throw new UnexpectedResultException(Failure.NOT_ARRAY_BASE);
                    }
                    RArray abase = (RArray) base;
                    if (!(value instanceof RArray)) {
                        throw new UnexpectedResultException(Failure.NOT_ARRAY_VALUE);
                    }
                    RArray avalue = (RArray) value;
                    int vsize = avalue.size();
                    if (vsize != 1) {
                        throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT_VALUE);
                    }
                    int pos;
                    RArray aindex;
                    if (index instanceof RInt) {
                        RInt iindex = (RInt) index;
                        pos = iindex.getInt(0);
                        aindex = iindex;
                    } else if (index instanceof RDouble) {
                        RDouble dindex = (RDouble) index;
                        pos = Convert.double2int(dindex.getDouble(0));
                        aindex = dindex;
                    } else {
                        throw new UnexpectedResultException(Failure.NOT_NUMERIC_INDEX);
                    }
                    int isize = aindex.size();
                    if (isize != 1) {
                        throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT_INDEX);
                    }

                    int bsize = abase.size();
                    if (pos < 1 || pos > bsize) {
                        throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                    }
                    pos--;
                    return copy.copy(abase, pos, avalue);

                } catch (UnexpectedResultException e) {
                    Failure f = (Failure) e.getResult();
                    if (DEBUG_UP) Utils.debug("update - SimpleScalarNumericSelection"+dbg+" failed: " + f);
                    Utils.nyi("unsupported update");
                    return null;
                }
            }
        }
    }
}
