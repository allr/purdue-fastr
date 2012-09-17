package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;

public abstract class ReadVector extends BaseR {

    RNode lhs;
    RNode[] indexes;
    final boolean subset;

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
    public static class SimpleSubscript extends ReadVector {
        public SimpleSubscript(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
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
                if (f == Failure.NOT_INT_INDEX) {
                    SimpleDoubleSubscript dbl = new SimpleDoubleSubscript(ast, lhs, indexes, subset);
                    replace(dbl, "install getSimpleDoubleSubscript from getSimpleSubscript");
                    return dbl.execute(context, frame, index, vector);

                }
                Utils.nyi("nontrivial vector subscript");
                return null;
            }
        }
    }

    // when the index has only one argument, which is a double
    public static class SimpleDoubleSubscript extends ReadVector {
        public SimpleDoubleSubscript(ASTNode ast, RNode lhs, RNode[] indexes, boolean subset) {
            super(ast, lhs, indexes, subset);
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny index = (RAny) indexes[0].execute(context, frame);
            RAny vector = (RAny) lhs.execute(context, frame);
            return execute(context, frame, index, vector);
        }

        public RAny execute(RContext context, RFrame frame, RAny index, RAny vector) {
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
                Utils.nyi("nontrivial vector subscript"+e);
                return null;
            }
        }
    }

}
