package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

// FIXME: can we avoid copying in some cases? E.g. when representation of a vector is explicit.

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
        NOT_ARRAY_INDEX,
        INDEX_OUT_OF_BOUNDS,
        NOT_ONE_ELEMENT_VALUE,
        NOT_ARRAY_VALUE,
        UNEXPECTED_TYPE
    }

    // for a numeric (int, double) scalar index
    //   first installs an uninitialized node
    //   this node rewrites itself to type-specialized nodes for simple assignment, or to a generic node
    //   the specialized nodes can rewrite themselves to the generic node
    public static class ScalarNumericSelection extends UpdateVector {

        public ScalarNumericSelection(ASTNode ast, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
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
            if (DEBUG_UP) Utils.debug("update - executing ScalarNumericSelection (uninitialized)");
            Specialized sn = createSimple(base, value);
            if (sn != null) {
                replace(sn, "specialize ScalarNumericSelection");
                if (DEBUG_UP) Utils.debug("update - replaced and re-executing with ScalarNumericSelection.Simple");
                return sn.execute(context, frame, base, index, value);
            } else {
                sn = createGeneric();
                replace(sn, "specialize ScalarNumericSelection");
                if (DEBUG_UP) Utils.debug("update - replaced and re-executing with ScalarNumericSelection.Generic");
                return sn.execute(context, frame, base, index, value);
            }
        }

        abstract class ValueCopy {
            abstract RAny copy(RArray base, int pos, RAny value) throws UnexpectedResultException;
        }

        public Specialized createSimple(RAny baseTemplate, RAny valueTemplate) {
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
                            if (pos < 1 || pos > bsize) {
                                throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                            }
                            int zpos = pos - 1;
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
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
                            if (pos < 1 || pos > bsize) {
                                throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                            }
                            int zpos = pos - 1;
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
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
                            if (pos < 1 || pos > bsize) {
                                throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                            }
                            int zpos = pos -1;
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            content[i++] = ((RDouble) value).getDouble(0);
                            for (; i < bsize; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RDouble,RDouble>");
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
                            if (pos < 1 || pos > bsize) {
                                throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                            }
                            int zpos = pos - 1;
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            content[i++] = Convert.int2double(((RInt) value).getInt(0));
                            for (; i < bsize; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RDouble,RInt>");
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
                            if (pos < 1 || pos > bsize) {
                                throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                            }
                            int zpos = pos -1;
                            double[] content = new double[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            content[i++] = Convert.logical2double(((RLogical) value).getLogical(0));
                            for (; i < bsize; i++) {
                                content[i] = dbase.getDouble(i);
                            }
                            return RDouble.RDoubleFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RDouble,RLogical>");
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
                            if (pos < 1 || pos > bsize) {
                                throw new UnexpectedResultException(Failure.INDEX_OUT_OF_BOUNDS);
                            }
                            int zpos = pos - 1;
                            int[] content = new int[bsize];
                            int i = 0;
                            for (; i < zpos; i++) {
                                content[i] = lbase.getLogical(i);
                            }
                            content[i++] = ((RLogical) value).getLogical(0);
                            for (; i < bsize; i++) {
                                content[i] = lbase.getLogical(i);
                            }
                            return RLogical.RLogicalFactory.getForArray(content);
                        }
                    };
                    return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<RLogical,RLogical>");
                }
            }
            return null;
        }

        public static RAny genericUpdate(RArray base, int pos, RAny value, boolean subset, ASTNode ast) {
            RArray typedBase;
            RArray typedValue;
            if (base instanceof RDouble || value instanceof RDouble) {
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
            int bsize = base.size();
            if (pos > 0) {
                if (pos <= bsize) {
                    int zpos = pos - 1;
                    RArray res = Utils.createArray(typedBase, bsize);
                    int i = 0;
                    for (; i < zpos; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    res.set(i++, typedValue.get(0));
                    for (; i < bsize; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    return res;
                } else {
                    int zpos = pos - 1;
                    int nsize = zpos + 1;
                    RArray res = Utils.createArray(typedBase, nsize);
                    int i = 0;
                    for (; i < bsize; i++) {
                        res.set(i, typedBase.get(i));
                    }
                    for (; i < zpos; i++) {
                        Utils.setNA(res, i);
                    }
                    res.set(i, typedValue.get(0));
                    return res;
                }
            } else { // pos < 0
                if (pos == RInt.NA) {
                    return base;
                }
                if (!subset) {
                    if (bsize <= 1) {
                        throw RError.getSelectLessThanOne(ast);
                    }
                    if (bsize > 2) {
                        throw RError.getSelectMoreThanOne(ast);
                    }
                    // bsize == 2
                    if (pos != -1 && pos != -2) {
                        throw RError.getSelectMoreThanOne(ast);
                    }
                }
                int keep = -pos - 1;
                RArray res = Utils.createArray(typedBase, bsize);
                int i = 0;
                Object v = typedValue.get(0);
                for (; i < keep; i++) {
                    res.set(i, v);
                }
                res.set(i, typedBase.get(i));
                i++;
                for (; i < bsize; i++) {
                    res.set(i, v);
                }
                return res;
            }
        }

        public Specialized createGeneric() {
            ValueCopy cpy = new ValueCopy() {
                @Override
                RAny copy(RArray base, int pos, RAny value) {
                    return genericUpdate(base, pos, value, subset, ast);
                }
            };
            return new Specialized(ast, lhs, indexes, rhs, subset, cpy, "<Generic>");
        }


        // specialized for type combinations (base vector, value written)
        class Specialized extends ScalarNumericSelection {
            final ValueCopy copy;
            final String dbg;

            Specialized(ASTNode ast, RNode lhs, RNode[] indexes, RNode rhs, boolean subset, ValueCopy copy, String dbg) {
                super(ast, lhs, indexes, rhs, subset);
                this.copy = copy;
                this.dbg = dbg;
            }

            @Override
            public RAny execute(RContext context, RFrame frame, RAny base, RAny index, RAny value) {
                if (DEBUG_UP) Utils.debug("update - executing ScalarNumericSelection" + dbg);
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
                    return copy.copy(abase, pos, avalue);

                } catch (UnexpectedResultException e) {
                    Failure f = (Failure) e.getResult();
                    if (DEBUG_UP) Utils.debug("update - ScalarNumericSelection" + dbg + " failed: " + f);
                    switch(f) {
                        case INDEX_OUT_OF_BOUNDS:
                        case UNEXPECTED_TYPE:
                            Specialized sn = createGeneric();
                            replace(sn, "specialize ScalarNumericSelection");
                            if (DEBUG_UP) Utils.debug("update - replaced and re-executing with ScalarNumericSelection.Generic");
                            return sn.execute(context, frame, base, index, value);

                        default:
                            GenericScalarSelection gs = new GenericScalarSelection(ast, lhs, indexes, rhs, subset);
                            replace(gs, "install GenericScalarSelection from ScalarNumericSelection");
                            if (DEBUG_UP) Utils.debug("update - replaced and re-executing with GenericScalarSelection");
                            return gs.execute(context, frame, base, index, value);
                    }
                }
            }
        }
    }

    public static class GenericScalarSelection extends UpdateVector {

        public GenericScalarSelection(ASTNode ast, RNode lhs, RNode[] indexes, RNode rhs, boolean subset) {
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
            if (DEBUG_UP) Utils.debug("update - executing GenericScalarSelection");
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
                if (!(index instanceof RArray)) {
                    throw new UnexpectedResultException(Failure.NOT_ARRAY_INDEX);
                }
                RArray aindex = (RArray) index;
                int isize = aindex.size();

                if (isize == 0) {
                    throw RError.getReplacementZero(ast);
                }
                if (isize > 1) {
                    if (subset) {
                        context.warning(ast, RError.NOT_MULTIPLE_REPLACEMENT);
                    } else {
                        throw RError.getMoreElementsSupplied(ast);
                    }
                }
                if (index instanceof RInt) {
                    return ScalarNumericSelection.genericUpdate(abase, ((RInt) index).getInt(0), avalue, subset, ast);
                } else if (index instanceof RDouble) {
                    return ScalarNumericSelection.genericUpdate(abase, Convert.double2int(((RDouble) index).getDouble(0)), avalue, subset, ast);
                } else if (index instanceof RLogical) {
                    int l = ((RLogical) index).getLogical(0);
                    if (l == RLogical.FALSE) {
                        if (!subset) {
                            throw RError.getSelectLessThanOne(ast);
                        }
                        return abase;
                    }
                    if (l == RLogical.NA) {
                        if (!subset) {
                            throw RError.getSelectMoreThanOne(ast);
                        }
                        return abase;
                    }
                    return ScalarNumericSelection.genericUpdate(abase, RLogical.TRUE, avalue, subset, ast);
                }
                Utils.nyi("unsupported type in vector update");
                return null;
            } catch (UnexpectedResultException e) {
                Failure f = (Failure) e.getResult();
                if (DEBUG_UP) Utils.debug("update - GenericScalarSelection failed: " + f);
                Utils.nyi("unsupported update");
                return null;
            }
        }
    }
}

