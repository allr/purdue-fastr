package r.builtins;

import java.util.*;

import r.*;
import r.builtins.LApply.CallableProvider;
import r.builtins.LApply.ValueProvider;
import r.data.*;
import r.data.internal.*;
import r.nodes.ast.*;
import r.nodes.exec.Constant;
import r.nodes.exec.*;
import r.nodes.exec.FunctionCall;
import r.runtime.*;

/**
 * "outer"
 *
 * <pre>
 * X, Y -- First and second arguments for function FUN. Typically a vector or array.
 * FUN -- a function to use on the outer products, found via match.fun (except for the special case "*").
 * ... -- optional arguments to be passed to FUN.
 * </pre>
 */
final class Outer extends CallFactory {
    static final CallFactory _ = new Outer("outer", new String[]{"X", "Y", "FUN", "..."}, new String[]{"X", "Y"});

    private Outer(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    private static final boolean EAGER = true; // NOTE: lazy is now only for integer, expand if needed, now is not faster than eager

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        boolean product = false;
        if (ia.provided("FUN")) {
            RNode fnode = exprs[ia.position("FUN")];
            if (fnode instanceof Constant) {
                RAny value = ((Constant) fnode).execute(null);
                if (value instanceof RString) {
                    RString str = (RString) value;
                    if (str.size() == 1) {
                        if (str.getString(0).equals("*")) {
                            product = true;
                        }
                    }
                }
            }
        } else {
            product = true;
        }
        if (product) { return new MatrixOperation.OuterProduct(call, exprs[ia.position("X")], exprs[ia.position("Y")]); }

        int cnArgs = 2 + names.length - 3; // "-2" because both FUN, X, Y
        RSymbol[] cnNames = new RSymbol[cnArgs];
        RNode[] cnExprs = new RNode[cnArgs];
        cnNames[0] = null;
        ValueProvider xArgProvider = new ValueProvider(call);
        cnExprs[0] = xArgProvider;
        ValueProvider yArgProvider = new ValueProvider(call);
        cnExprs[1] = yArgProvider;
        int j = 0;
        for (int i = 0; i < names.length; i++) {
            if (ia.position("X") == i || ia.position("Y") == i || ia.position("FUN") == i) {
                continue;
            }
            cnNames[2 + j] = names[i];
            cnExprs[2 + j] = exprs[i];
            j++;
        }

        final CallableProvider callableProvider = new CallableProvider(call, exprs[ia.position("FUN")]);
        final RNode callNode = FunctionCall.getFunctionCall(call, callableProvider, cnNames, cnExprs);
        final int posX = ia.position("X");
        final int posY = ia.position("Y");
        final int posFUN = ia.position("FUN");
        return new OuterBuiltIn(call, names, exprs, callNode, callableProvider, xArgProvider, yArgProvider) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return outer(frame, args[posX], args[posY], args[posFUN]);
            }
        };
    }

    public abstract static class OuterBuiltIn extends Builtin { // note: this class only exists so that we can call updateParent...
        @Child RNode callNode;
        @Child CallableProvider callableProvider;
        @Child ValueProvider xArgProvider;
        @Child ValueProvider yArgProvider;

        public OuterBuiltIn(ASTNode ast, RSymbol[] argNames, RNode[] argExprs, RNode callNode, CallableProvider callableProvider, ValueProvider xArgProvider, ValueProvider yArgProvider) {
            super(ast, argNames, argExprs);
            this.callNode = adoptChild(callNode);
            this.callableProvider = adoptChild(callableProvider);
            this.xArgProvider = adoptChild(xArgProvider);
            this.yArgProvider = adoptChild(yArgProvider);
        }

        public RAny outer(Frame frame, RAny xarg, RAny yarg, RAny farg) {
            // LICENSE: transcribed code from GNU R, which is licensed under GPL

            if (!(xarg instanceof RArray && yarg instanceof RArray)) {
                Utils.nyi("unsupported type");
                return null;
            }

            RArray x = (RArray) xarg;
            RArray y = (RArray) yarg;

            int xsize = x.size();
            int ysize = y.size();

            RArray expy;
            RArray expx;

            if (EAGER) {
                x = x.materialize(); // FIXME: probably unnecessary (both x and y), could be done on-the-fly in the expansion methods
                y = y.materialize();
                if (y instanceof DoubleImpl) {
                    expy = expandYVector((DoubleImpl) y, ysize, xsize);
                } else if (y instanceof IntImpl) {
                    expy = expandYVector((IntImpl) y, ysize, xsize);
                } else {
                    expy = expandYVector(y, ysize, xsize);
                }

                if (xsize > 0) {
                    if (x instanceof DoubleImpl) {
                        expx = expandXVector((DoubleImpl) x, xsize, ysize);
                    } else if (x instanceof IntImpl) {
                        expx = expandXVector((IntImpl) x, xsize, ysize);
                    } else {
                        expx = expandXVector(x, xsize, ysize);
                    }
                } else {
                    expx = x;
                }
            } else {
                if (y instanceof RInt) {
                    expy = lazyExpandYVector((RInt) y, ysize, xsize);
                } else {
                    throw Utils.nyi();
                }
                if (xsize > 0) {
                    if (x instanceof RInt) {
                        expx = lazyExpandXVector((RInt) x, xsize, ysize);
                    } else {
                        throw Utils.nyi();
                    }
                } else {
                    expx = x;
                }
            }

            xArgProvider.setValue(expx);
            yArgProvider.setValue(expy);
            callableProvider.matchAndSet(ast, frame, farg);
            RArray res = (RArray) callNode.execute(frame);

            int[] dimx = x.dimensions();
            int[] dimy = y.dimensions();

            int[] dim;
            if (dimx == null) {
                if (dimy == null) {
                    dim = new int[]{xsize, ysize};
                } else {
                    dim = new int[1 + dimy.length];
                    dim[0] = xsize;
                    System.arraycopy(dimy, 0, dim, 1, dimy.length);
                }
            } else {
                if (dimy == null) {
                    dim = new int[dimx.length + 1];
                    System.arraycopy(dimx, 0, dim, 0, dimx.length);
                    dim[dimx.length] = ysize;
                } else {
                    dim = new int[dimx.length + dimy.length];
                    System.arraycopy(dimx, 0, dim, 0, dimx.length);
                    System.arraycopy(dimy, 0, dim, dimx.length, dimy.length);
                }
            }
            return res.setDimensions(dim);
        }
    }

    public static RArray expandYVector(RArray y, int ysize, int count) {
        int size = ysize;
        int nsize = size * count;

        RArray res = Utils.createArray(y, nsize);
        int offset = 0;
        for (int elem = 0; elem < size; elem++) {
            Object v = y.get(elem);
            for (int i = 0; i < count; i++) {
                res.set(offset + i, v);
            }
            offset += count;
        }
        return res;
    }

    public static RDouble expandYVector(DoubleImpl yarg, int ysize, int count) {
        int size = ysize;
        int nsize = size * count;
        double[] y = yarg.getContent();

        double[] res = new double[nsize];
        int offset = 0;
        for (int elem = 0; elem < size; elem++) {
            double v = y[elem];
            Arrays.fill(res, offset, offset + count, v);
            offset += count;
        }
        return RDouble.RDoubleFactory.getFor(res);
    }

    public static RInt expandYVector(IntImpl yarg, int ysize, int count) {
        int size = ysize;
        int nsize = size * count;
        int[] y = yarg.getContent();

        int[] res = new int[nsize];
        int offset = 0;
        for (int elem = 0; elem < size; elem++) {
            int v = y[elem];
            Arrays.fill(res, offset, offset + count, v);
            offset += count;
        }
        return RInt.RIntFactory.getFor(res);
    }

    public static RInt lazyExpandYVector(RInt yarg, int ysize, final int count) {
        final int nsize = ysize * count;
        return new View.RIntProxy<RInt>(yarg) {

            @Override public int getInt(int i) {
                int j = i / count;
                return orig.getInt(j);
            }

            @Override public int size() {
                return nsize;
            }

            @Override public int[] dimensions() {
                return null;
            }

            @Override public Names names() {
                return null;
            }

            @Override public Attributes attributes() {
                return null;
            }
        };
    }

    public static RArray expandXVector(RArray x, int xsize, int count) {
        int nsize = xsize * count;

        RArray res = Utils.createArray(x, nsize);
        int offset = 0;
        for (int rep = 0; rep < count; rep++) {
            for (int i = 0; i < xsize; i++) {
                res.set(offset + i, x.get(i));
            }
            offset += xsize;
        }
        return res;
    }

    public static RDouble expandXVector(DoubleImpl xarg, int xsize, int count) {
        int nsize = xsize * count;
        double[] x = xarg.getContent();
        double[] res = new double[nsize];
        int offset = 0;
        for (int rep = 0; rep < count; rep++) {
            System.arraycopy(x, 0, res, offset, xsize);
            offset += xsize;
        }
        return RDouble.RDoubleFactory.getFor(res);
    }

    public static RInt expandXVector(IntImpl xarg, int xsize, int count) {
        int nsize = xsize * count;
        int[] x = xarg.getContent();
        int[] res = new int[nsize];
        int offset = 0;
        for (int rep = 0; rep < count; rep++) {
            System.arraycopy(x, 0, res, offset, xsize);
            offset += xsize;
        }
        return RInt.RIntFactory.getFor(res);
    }

    // an attempt for performance improvement: but does not seem faster for now
    public static RInt expandXVectorCacheFriendly(IntImpl xarg, int xsize, int count) {
        int nsize = xsize * count;
        int[] x = xarg.getContent();
        int[] res = new int[nsize];
        int offset = 0;
        int rep = 0;
        int lastOffset = 0;
        if (rep < count) {
            System.arraycopy(x, 0, res, offset, xsize);
            lastOffset = offset;
            offset += xsize;
        }
        for (rep = 1; rep < count; rep++) {
            System.arraycopy(res, lastOffset, res, offset, xsize);
            lastOffset = offset;
            offset += xsize;
        }
        return RInt.RIntFactory.getFor(res);
    }

    public static RInt lazyExpandXVector(RInt xarg, int xsize, final int count) {
        final int nsize = xsize * count;
        return new View.RIntProxy<RInt>(xarg) {

            @Override public int getInt(int i) {
                int j = i % count;
                return orig.getInt(j);
            }

            @Override public int size() {
                return nsize;
            }

            @Override public int[] dimensions() {
                return null;
            }

            @Override public Names names() {
                return null;
            }

            @Override public Attributes attributes() {
                return null;
            }
        };
    }

}
