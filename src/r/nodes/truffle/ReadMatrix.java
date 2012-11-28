package r.nodes.truffle;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;


public class ReadMatrix extends BaseR {

    @Stable RNode matrixExpr;
    @Stable SelectorNode selIExpr;
    @Stable SelectorNode selJExpr;
    @Stable OptionNode dropExpr;
    @Stable OptionNode exactExpr;
    final boolean subset;

    public ReadMatrix(ASTNode ast, boolean subset, RNode matrixExpr, SelectorNode selIExpr, SelectorNode selJExpr, OptionNode dropExpr, OptionNode exactExpr) {
        super(ast);
        this.subset = subset;
        this.matrixExpr = updateParent(matrixExpr);
        this.selIExpr = updateParent(selIExpr);
        this.selJExpr = updateParent(selJExpr);
        this.dropExpr = updateParent(dropExpr);
        this.exactExpr = updateParent(exactExpr);
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RAny matrix = (RAny) matrixExpr.execute(context, frame);
        Selector selI = selIExpr.executeSelector(context, frame);
        Selector selJ = selJExpr.executeSelector(context, frame);
        int drop = dropExpr.executeLogical(context, frame);  // FIXME: what is the correct execution order of these args?
        int exact = exactExpr.executeLogical(context, frame);

        return execute(context, matrix, selI, selJ, drop, exact);
    }

    // FIXME: could be specialized by matrix type to remove some boxing, virtual calls
    public Object execute(RContext context, RAny matrix, Selector selI, Selector selJ, int drop, int exact) {
        if (!(matrix instanceof RArray)) {
            Utils.nyi("unsupported base");
            // TODO: ERROR object of type 'XXX' is not subsettable
            return null;
        }
        RArray base = (RArray) matrix;
        int[] dim = base.dimensions();
        if (dim == null || dim.length != 2) {
            throw RError.getIncorrectDimensions(ast);
        }
        int m = dim[0];
        int n = dim[1];

        selI.start(m, context, ast);
        selJ.start(n, context, ast);
        int nm = selI.size();
        int nn = selJ.size();
        int nsize = nm * nn;
        int[] ndim;
        if ((nm != 1 && nn != 1) || drop == RLogical.FALSE) {
            ndim = new int[]{nm, nn};
        } else {
            ndim = null;
        }
        RArray res = Utils.createArray(matrix, nsize, ndim);

        for (int ni = 0; ni < nm; ni++) {
            int i = selI.nextIndex(context, ast);
            if (i != RInt.NA) {
                selJ.start(n, context, ast);
                for (int nj = 0; nj < nn; nj++) {
                    int j = selJ.nextIndex(context, ast);
                    int offset = nj * nm + ni;
                    if (j != RInt.NA) {
                        Object value;
                        value = base.getRef(j * m + i); // FIXME: check overflow? (the same is at many locations, whenever indexing a matrix)
                        res.set(offset, value);
                    } else {
                        Utils.setNA(res, offset);
                    }
                }
            } else {
                for (int nj = 0; nj < nn; nj++) {
                    Utils.setNA(res, nj * nm + ni);
                }
            }
        }
        return res;
    }


    public abstract static class Selector {
        public abstract void start(int dataSize, RContext context, ASTNode ast);
        public abstract int size();
        public abstract int nextIndex(RContext context, ASTNode ast);
        public abstract boolean isConstant();
    }

    public static final class MissingSelector extends Selector {
        private int size = -1;
        private int last = -1;

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            size = dataSize;
            last = 0;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            return last++;
        }

        @Override
        public boolean isConstant() {
            return true;
        }
    }

    public static final class SinglePositiveConstantIndexSelector extends Selector {
        private final int index;

        public SinglePositiveConstantIndexSelector(int value) {
            this.index = value;
        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            if (index > dataSize) {
                throw RError.getSubscriptBounds(ast);
            }
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            return index;
        }

        @Override
        public boolean isConstant() {
            return true;
        }
    }

    public static final class GenericNumericSelector extends Selector {

        RInt index;
        int size; // the result size, valid after a call to restart ; before restart, either the size already or number of zeros (negativeSelection)

        boolean positiveSelection;
        boolean negativeSelection;
        boolean emptySelection;

        public void setIndex(RInt index) {
            this.index = index;

            boolean hasNA = false;
            boolean hasNegative = false;
            boolean hasPositive = false;
            boolean hasZero = false;
            int nzero = 0;
            int isize = index.size();
            for (int i = 0; i < isize; i++) {
                int value = index.getInt(i);
                if (value > 0) {
                    hasPositive = true;
                    continue;
                }
                if (value == 0) {
                    hasZero = true;
                    nzero ++;
                    continue;
                }
                if (value == RInt.NA) {
                    hasNA = true;
                    continue;
                }
                hasNegative = true;
                continue;
            }

            if (hasPositive) {
                if (!hasNegative) {
                    size = isize - nzero;
                } else {
                    // TODO: ERROR "only 0's may be mixed with negative subscripts"
                }
            } else {
                // no positive values
                if (hasNegative) {
                    if (!hasNA) {
                        negativeSelection = true;
                        size = nzero;
                        // all elements are negative, selection size will depend on the data size
                    } else {
                        // TODO: ERROR "only 0's may be mixed with negative subscripts"
                    }
                }
            }

        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            // TODO Auto-generated method stub

        }

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isConstant() {
            // TODO Auto-generated method stub
            return false;
        }
    }

    public static final class GenericLogicalSelector extends Selector {

        RLogical index;

        public void setIndex(RLogical index) {
            this.index = index;
            // TODO: reanalyze index
        }

        @Override
        public void start(int dataSize, RContext context, ASTNode ast) {
            // TODO Auto-generated method stub

        }

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int nextIndex(RContext context, ASTNode ast) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isConstant() {
            // TODO Auto-generated method stub
            return false;
        }

    }

    public static SelectorNode createConstantSelectorNode(ASTNode ast, RNode child, final Selector selector) {
        return new SelectorNode(ast, child) {

            @Override
            public Selector executeSelector(RContext context, Frame frame) {
                return selector;
            }

            @Override
            public Selector executeSelector(RContext context, RAny index) {
                return selector;
            }
        };
    }

    public static SelectorNode createSelectorNode(ASTNode ast, final RNode node) {
        if (node == null) {
            return createConstantSelectorNode(ast, node, new MissingSelector());
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny index = (RAny) node.execute(null, null);
            return createSelectorNode(ast, index, node, true, null);
        }
        return new SelectorNode(ast, node) {
            @Override
            public Selector executeSelector(RContext context, RAny index) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    SelectorNode sn = createSelectorNode(ast, index, child, false, null);
                    replace(sn, "install SelectorNode from Uninitialized");
                    return sn.executeSelector(context, index);
                }
            }

        };
    }

    public enum Failure {

    }

    public static final class AnalyzedIndex {
        final boolean hasNA;
        final boolean hasNegative;
        final boolean hasPositive;
        final boolean hasZero;

        public AnalyzedIndex(boolean hasNA, boolean hasNegative, boolean hasPositive, boolean hasZero) {
            this.hasNA = hasNA;
            this.hasNegative = hasNegative;
            this.hasPositive = hasPositive;
            this.hasZero = hasZero;
        }
    }

    public static AnalyzedIndex analyzeIndex(RInt index) {
        boolean hasNA = false;
        boolean hasNegative = false;
        boolean hasPositive = false;
        boolean hasZero = false;

        for (int i = 0; i < index.size(); i++) {
            int value = index.getInt(i);
            if (value > 0) {
                hasPositive = true;
                continue;
            }
            if (value == 0) {
                hasZero = true;
                continue;
            }
            if (value == RInt.NA) {
                hasNA = true;
                continue;
            }
            hasNegative = true;
            continue;
        }
        return new AnalyzedIndex(hasNA, hasNegative, hasPositive, hasZero);
    }

    public static SelectorNode createSelectorNode(ASTNode ast, RAny template, RNode child, boolean isConstant, Failure lastFailure) {
        if (template instanceof RDouble || template instanceof RInt) {
            RInt index = template.asInt().materialize();
            AnalyzedIndex a = analyzeIndex(index);
            if (isConstant) {
                if (a.hasPositive && !a.hasNegative && !a.hasNA && !a.hasZero) {
                    if (index.size() == 1) {
                        return createConstantSelectorNode(ast, child, new SinglePositiveConstantIndexSelector(index.getInt(0) - 1));
                    }
                }
                // FIXME: handle more cases? e.g. set of positive integer indexes
            }
        }
        if (template instanceof RLogical) {
            // TODO
        }
        Utils.nyi();
        return null;
    }

    public static SelectorNode createGenericSelectorNode(ASTNode ast, RNode child) {
        return new SelectorNode(ast, child) {

            final GenericNumericSelector numericSelector = new GenericNumericSelector();
            final GenericLogicalSelector logicalSelector = new GenericLogicalSelector();

            @Override
            public Selector executeSelector(RContext context, RAny index) {
                if (index instanceof RInt || index instanceof RDouble) {
                    numericSelector.setIndex(index.asInt());
                    return numericSelector;
                }
                if (index instanceof RLogical) {
                    logicalSelector.setIndex((RLogical) index);
                    return logicalSelector;
                }
                Utils.nyi("unsupported index type");
                return null;
            }
        };
    }

    public abstract static class SelectorNode extends BaseR {
        @Stable RNode child;

        public SelectorNode(ASTNode ast, RNode child) {
            super(ast);
            this.child = updateParent(child);
        }

        @Override
        public Object execute(RContext context, Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public Selector executeSelector(RContext context, Frame frame) {
            RAny index = (RAny) child.execute(context, frame);
            return executeSelector(context, index);

        }

        public abstract Selector executeSelector(RContext context, RAny index);
    }

    public static OptionNode createConstantOptionNode(final ASTNode ast, final int value) {
        return new OptionNode(ast) {

            @Override
            public int executeLogical(RContext context, Frame frame) {
                return value;
            }
        };
    }

    public static OptionNode createOptionNode(final ASTNode ast, final RNode node, int defaultValue) {
        if (node == null) {
            return createConstantOptionNode(ast, defaultValue);
        }
        if (node.getAST() instanceof r.nodes.Constant) {
            RAny value = (RAny) node.execute(null, null);
            return createConstantOptionNode(ast, value.asLogical().getLogical(0));
        }
        return new OptionNode(ast) {

            @Stable RNode child = updateParent(node);

            @Override
            public int executeLogical(RContext context, Frame frame) {
                RAny value = (RAny) child.execute(context, frame);
                return value.asLogical().getLogical(0);
            }

        };
    }

    public static OptionNode createDropOptionNode(ASTNode ast, RNode node) {
        return createOptionNode(ast, node, RLogical.TRUE);
    }

    public static OptionNode createExactOptionNode(ASTNode ast, RNode node) {
        return createOptionNode(ast, node, RLogical.NA);
    }

    public abstract static class OptionNode extends BaseR {

        public OptionNode(ASTNode ast) {
            super(ast);
        }

        @Override
        public Object execute(RContext context, Frame frame) {
            Utils.check(false, "unreachable");
            return null;
        }

        public abstract int executeLogical(RContext context, Frame frame);
    }
}
