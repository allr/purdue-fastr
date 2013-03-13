package r.builtins;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.*;

/**
 * "aperm". Transpose an array by permuting its dimensions and optionally resizing it.
 * 
 * <pre>
 * a -- the array to be transposed.
 * perm -- the subscript permutation vector, usually a permutation of the integers 1:n, where n is the number of 
 *         dimensions of a. When a has named dimnames, it can be a character vector of length n giving a permutation of 
 *         those names. The default (used whenever perm has zero length) is to reverse the order of the dimensions.
 * resize -- a flag indicating whether the vector should be resized as well as having its elements reordered (default TRUE).
 * </pre>
 */
final class Aperm extends CallFactory {

    static final CallFactory _ = new Aperm("aperm", new String[]{"a", "perm", "resize"}, new String[]{"a"});

    private Aperm(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        int posPerm = ia.position("perm");// can be -1
        int posA = ia.position("a");
        int posResize = ia.position("resize");
        return Configuration.BUILTIN_APERM_TYPED_DIRECT_ACCESS ? new ApermImpl(call, names, exprs, posA, posPerm, posResize) : new Generalized(call, names, exprs, posA, posPerm, posResize);
    }

    /**
     * Generalized aperm implementation with object getters and setters.
     */
    protected static class Generalized extends BuiltIn {

        int posPerm;
        int posA;
        int posResize;

        public Generalized(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, int posA, int posPerm, int posResize) {
            super(orig, argNames, argExprs);
            this.posA = posA;
            this.posPerm = posPerm;
            this.posResize = posResize;
        }

        protected Generalized(Generalized other) {
            super(other.ast, other.argNames, other.argExprs);
            this.posA = other.posA;
            this.posPerm = other.posPerm;
            this.posResize = other.posResize;
        }

        /**
         * Returns the array on which to make the permutation.
         */
        protected final RArray parseArray(RAny[] params) {
            RArray ary = (RArray) params[posA];
            if (ary.dimensions() == null) { throw RError.getFirstArgMustBeArray(ast); }
            return ary;
        }

        /**
         * Parses the permutation given. If no permutation is given the transpose is used. Note that the returned value
         * is 0 indexed.
         */
        protected final int[] parsePermutation(int[] aryDim, RAny[] params) {
            int[] result = new int[aryDim.length];
            if (posPerm == -1) { // default parm argument is reverse order of the dimensions
                for (int i = 0; i < result.length; ++i) {
                    result[i] = result.length - i - 1;
                }
                return result;
            } else {
                boolean[] usedIndices = new boolean[result.length];
                RArray perm = (RArray) params[posPerm];
                if (perm.size() != aryDim.length) { throw RError.getValueIsOfWrongLength(ast, "perm"); }
                if (perm instanceof RComplex) {
                    RContext.warning(ast, RError.IMAGINARY_PARTS_DISCARDED_IN_COERCION);
                }
                RInt p = perm.asInt();
                for (int i = 0; i < result.length; ++i) {
                    int x = p.getInt(i);
                    if ((x < 1) || (x > aryDim.length)) { throw RError.getValueOutOfRange(ast, "perm"); }
                    --x;
                    result[i] = x;
                    if (usedIndices[x] == true) {
                        throw RError.getInvalidArgument(ast, "perm");
                    } else {
                        usedIndices[x] = true;
                    }
                }
            }
            return result;
        }

        /**
         * Returns the resize argument of the call.
         */
        protected final boolean parseResize(RAny[] params) {
            if (posResize == -1) { return true; }
            return params[posResize].asLogical().getLogical(0) != RLogical.FALSE;
        }

        /**
         * Using given permutation, permutes source and stores the result in dest.
         */
        protected static final void perm(int[] source, int[] dest, int[] perm) {
            for (int i = 0; i < perm.length; ++i) {
                dest[i] = source[perm[i]];
            }
        }

        /**
         * Increments the given index using the dimensions provided. The least significant value is the one on the left
         * (index 0). Does not check for overflow.
         */
        protected static final void increment(int[] idx, int[] dim) {
            int i = 0;
            while (i < idx.length) {
                ++idx[i];
                if (idx[i] < dim[i]) {
                    break;
                }
                idx[i] = 0;
                ++i;
            }
        }

        /**
         * Given the curent index and precomputed multipliers of the respective index positions calculates the offset to
         * a vector from the index into dimensions.
         */
        protected static final int offset(int[] idx, int[] mults) {
            int result = 0;
            for (int i = 0; i < idx.length; ++i) {
                result += idx[i] * mults[i];
            }
            return result;
        }

        /**
         * Calculates the dimension multipliers that are used later in the offset method.
         */
        protected static final int[] createDimMults(int[] dim) {
            int[] result = new int[dim.length];
            result[0] = 1;
            for (int i = 1; i < result.length; ++i) {
                result[i] = dim[i - 1] * result[i - 1];
            }
            return result;
        }

        /**
         * Calculates the resized dimensions for the aperm builtin.
         */
        protected static final int[] calculateResizedDimension(int[] aryDim, int[] perm) {
            int[] result = new int[aryDim.length];
            for (int i = 0; i < perm.length; ++i) {
                result[i] = aryDim[perm[i]];
            }
            return result;
        }

        /**
         * Parses the arguments and calls the aperm method that computes the permutation and might possibly specialize.
         */
        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            RArray ary = parseArray(params);
            int[] aryDim = ary.dimensions();
            int[] perm = parsePermutation(aryDim, params);
            boolean resize = parseResize(params);
            return aperm(ary, perm, resize);
        }

        /**
         * Calculates the permutation and returns the result. This is the generalized version that works with any values
         * because it creates a new array of the same object and then uses the getters & setters to accomplish the
         * copying.
         */
        protected RAny aperm(RArray ary, int[] perm, boolean resize) {
            int arySize = ary.size();
            int[] aryDim = ary.dimensions();
            int[] resultDim = calculateResizedDimension(aryDim, perm);
            RArray result = Utils.createArray(ary, arySize, resize ? resultDim : aryDim, ary.names(), null); // drop attributes
            int[] idx = new int[aryDim.length];
            int[] resultIdx = new int[aryDim.length];
            int[] dimMults = createDimMults(resultDim);
            for (int i = 0; i < arySize; ++i) {
                perm(idx, resultIdx, perm);
                int offset = offset(resultIdx, dimMults);
                result.set(offset, ary.get(i));
                increment(idx, aryDim);
            }
            return result;
        }
    }

    /**
     * Aperm implementation that checks if it can optimize for specific data types. Optimizations for integer, double
     * and complex arrays are supported (proper types, not views). If no such case is found, rewrites itself to the
     * Generalized case described above.
     */
    public static class ApermImpl extends Generalized {

        public ApermImpl(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, int posA, int posPerm, int posResize) {
            super(orig, argNames, argExprs, posA, posPerm, posResize);
        }

        @Override public RAny aperm(RArray ary, int[] perm, boolean resize) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (ary instanceof IntImpl) {
                    return replace(new Int(this)).aperm(ary, perm, resize);
                } else if (ary instanceof DoubleImpl) {
                    return replace(new Double(this)).aperm(ary, perm, resize);
                } else if (ary instanceof ComplexImpl) {
                    return replace(new Complex(this)).aperm(ary, perm, resize);
                } else {
                    return replace(new Generalized(this)).aperm(ary, perm, resize);
                }
            }
        }
    }

    /**
     * Integer optimized direct access aperm. If the argument is not IntImpl, rewrites to Generalized case and proceeds.
     */
    public static class Int extends Generalized {

        protected Int(Generalized other) {
            super(other);
        }

        @Override public RAny aperm(RArray ary, int[] perm, boolean resize) {
            try {
                if (!(ary instanceof IntImpl)) { throw new UnexpectedResultException(null); }
                int arySize = ary.size();
                int[] aryDim = ary.dimensions();
                int[] resultDim = calculateResizedDimension(aryDim, perm);
                int[] source = ((IntImpl) ary).getContent();
                int[] dest = new int[source.length];
                int[] idx = new int[aryDim.length];
                int[] resultIdx = new int[aryDim.length];
                int[] dimMults = createDimMults(resultDim);
                for (int i = 0; i < arySize; ++i) {
                    perm(idx, resultIdx, perm);
                    int offset = offset(resultIdx, dimMults);
                    dest[offset] = source[i];
                    increment(idx, aryDim);
                }
                return RInt.RIntFactory.getFor(dest, resize ? resultDim : aryDim, ary.names(), null); // drop attributes
            } catch (UnexpectedResultException e) {
                return replace(new Generalized(this)).aperm(ary, perm, resize);
            }
        }
    }

    /**
     * Double optimized direct access aperm. If the argument is not DoubleImpl, rewrites to Generalized case and
     * proceeds.
     */
    public static class Double extends Generalized {

        protected Double(Generalized other) {
            super(other);
        }

        @Override public RAny aperm(RArray ary, int[] perm, boolean resize) {
            try {
                if (!(ary instanceof DoubleImpl)) { throw new UnexpectedResultException(null); }
                int arySize = ary.size();
                int[] aryDim = ary.dimensions();
                int[] resultDim = calculateResizedDimension(aryDim, perm);
                double[] source = ((DoubleImpl) ary).getContent();
                double[] dest = new double[source.length];
                int[] idx = new int[aryDim.length];
                int[] resultIdx = new int[aryDim.length];
                int[] dimMults = createDimMults(resultDim);
                for (int i = 0; i < arySize; ++i) {
                    perm(idx, resultIdx, perm);
                    int offset = offset(resultIdx, dimMults);
                    dest[offset] = source[i];
                    increment(idx, aryDim);
                }
                return RDouble.RDoubleFactory.getFor(dest, resize ? resultDim : aryDim, ary.names(), null); // drop attributes
            } catch (UnexpectedResultException e) {
                return replace(new Generalized(this)).aperm(ary, perm, resize);
            }
        }
    }

    /**
     * Complex optimized direct access aperm. If the argument is not ComplexImpl, rewrites to Generalized case and
     * proceeds.
     */
    public static class Complex extends Generalized {

        protected Complex(Generalized other) {
            super(other);
        }

        @Override public RAny aperm(RArray ary, int[] perm, boolean resize) {
            try {
                if (!(ary instanceof ComplexImpl)) { throw new UnexpectedResultException(null); }
                int arySize = ary.size();
                int[] aryDim = ary.dimensions();
                int[] resultDim = calculateResizedDimension(aryDim, perm);
                double[] source = ((ComplexImpl) ary).getContent();
                double[] dest = new double[source.length];
                int[] idx = new int[aryDim.length];
                int[] resultIdx = new int[aryDim.length];
                int[] dimMults = createDimMults(resultDim);
                for (int i = 0; i < arySize; ++i) {
                    perm(idx, resultIdx, perm);
                    int offset = offset(resultIdx, dimMults);
                    dest[offset << 1] = source[i << 1];
                    dest[(offset << 1) + 1] = source[(i << 1) + 1];
                    increment(idx, aryDim);
                }
                return RDouble.RDoubleFactory.getFor(dest, resize ? resultDim : aryDim, ary.names(), null); // drop attributes
            } catch (UnexpectedResultException e) {
                return replace(new Generalized(this)).aperm(ary, perm, resize);
            }
        }
    }

}
