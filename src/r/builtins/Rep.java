package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.data.RComplex.RComplexFactory;
import r.data.RDouble.RDoubleFactory;
import r.data.RInt.RIntFactory;
import r.data.RLogical.RLogicalFactory;
import r.data.RRaw.RRawFactory;
import r.data.RString.RStringFactory;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "rep"
 * 
 * <pre>
 * x  -- a vector (of any mode including a list) or a pairlist or a factor or (except for rep.int) a POSIXct or POSIXlt or 
 *        date object; or also, an S4 object containing a vector of the above kind.
 * ... -- further arguments to be passed to or from other methods. For the internal default method these can include:
 *      times -- A integer vector giving the (non-negative) number of times to repeat each element if of length length(x), or to 
 *                repeat the whole vector if of length 1. Negative or NA values are an error.
 *      length.out -- non-negative integer. The desired length of the output vector. Other inputs will be coerced to an integer 
 *                    vector and the first element taken. Ignored if NA or invalid.
 *      each -- non-negative integer. Each element of x is repeated each times. Other inputs will be coerced to an integer 
 *              vector and the first element taken. Treated as 1 if NA or invalid.
 * </pre>
 */
// FIXME: Truffle can't handle BuiltIn2
// TODO: support non-scalar times argument
// TODO: support list argument
class Rep extends CallFactory {

    static final CallFactory _ = new Rep("rep", new String[]{"x", "..."}, new String[]{"x"});

    Rep(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static final boolean EAGER = true; // eager is important when rep is used to initialize e.g. a double vector, then passed to vector operations

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (names.length == 2) {
            int otherPos = ia.position("x") == 0 ? 1 : 0;
            RSymbol otherName = names[otherPos];
            if (otherName == null || otherName == RSymbol.TIMES_SYMBOL) { return RepInt._.create(call, names, exprs); }
            if (otherName == RSymbol.getSymbol("length.out")) {
                final boolean xfirst = ia.position("x") == 0;
                return new BuiltIn.BuiltIn2(call, names, exprs) {
                    @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                        return genericRepLengthOut(ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
                    }
                };

            }
        }
        throw Utils.nyi("unsupported rep arguments");
    }

    public static void checkScalar(RArray a, ASTNode ast) {
        int n = a.size();
        if (n != 1) { throw RError.getInvalidTimes(ast); }
    }

    public static RSymbol[] rep(RSymbol value, int size) {
        RSymbol[] res = new RSymbol[size];
        Arrays.fill(res, value);
        return res;
    }

    public static RSymbol[] rep(RSymbol[] origArray, int origSize, int newSize) {
        RSymbol[] newArray = new RSymbol[newSize];
        int start = 0;
        for (;;) {
            int end = start + origSize;
            if (end <= newSize) {
                System.arraycopy(origArray, 0, newArray, start, origSize);
                start = end;
            } else {
                break;
            }
        }
        System.arraycopy(origArray, 0, newArray, start, newSize - start);
        return newArray;
    }

    // FIXME copy-paste, as one cannot allocate generic array (or do generics on primitive types)
    public static byte[] rep(byte value, int size) {
        byte[] res = new byte[size];
        Arrays.fill(res, value);
        return res;
    }

    public static byte[] rep(byte[] origArray, int origSize, int newSize) {
        byte[] newArray = new byte[newSize];
        int start = 0;
        for (;;) {
            int end = start + origSize;
            if (end <= newSize) {
                System.arraycopy(origArray, 0, newArray, start, origSize);
                start = end;
            } else {
                break;
            }
        }
        System.arraycopy(origArray, 0, newArray, start, newSize - start);
        return newArray;
    }

    // FIXME copy-paste, as one cannot allocate generic array (or do generics on primitive types)
    public static int[] rep(int value, int size) {
        int[] res = new int[size];
        Arrays.fill(res, value);
        return res;
    }

    public static int[] rep(int[] origArray, int origSize, int newSize) {
        int[] newArray = new int[newSize];
        int start = 0;
        for (;;) {
            int end = start + origSize;
            if (end <= newSize) {
                System.arraycopy(origArray, 0, newArray, start, origSize);
                start = end;
            } else {
                break;
            }
        }
        System.arraycopy(origArray, 0, newArray, start, newSize - start);
        return newArray;
    }

    // FIXME copy-paste, as one cannot allocate generic array (or do generics on primitive types)
    public static double[] rep(double value, int size) {
        double[] res = new double[size];
        Arrays.fill(res, value);
        return res;
    }

    public static double[] rep(double[] origArray, int origSize, int newSize) {
        double[] newArray = new double[newSize];
        int start = 0;
        for (;;) {
            int end = start + origSize;
            if (end <= newSize) {
                System.arraycopy(origArray, 0, newArray, start, origSize);
                start = end;
            } else {
                break;
            }
        }
        System.arraycopy(origArray, 0, newArray, start, newSize - start);
        return newArray;
    }

    // FIXME copy-paste, as one cannot allocate generic array (or do generics on primitive types)
    public static String[] rep(String value, int size) {
        String[] res = new String[size];
        Arrays.fill(res, value);
        return res;
    }

    public static String[] rep(String[] origArray, int origSize, int newSize) {
        String[] newArray = new String[newSize];
        int start = 0;
        for (;;) {
            int end = start + origSize;
            if (end <= newSize) {
                System.arraycopy(origArray, 0, newArray, start, origSize);
                start = end;
            } else {
                break;
            }
        }
        System.arraycopy(origArray, 0, newArray, start, newSize - start);
        return newArray;
    }

    public static byte[] repValues(RRaw origArray, int origSize, int newSize) {
        if (origSize == 1) { // NOTE: branch not needed as long as we don't have a scalar type for raw
            return rep(origArray.getRaw(0), newSize);
        } else {
            return rep(((RawImpl) origArray.materialize()).getContent(), origSize, newSize);
        }
    }

    public static int[] repValues(RInt origArray, int origSize, int newSize) {
        if (origSize == 1) {
            return rep(origArray.getInt(0), newSize);
        } else {
            return rep(((IntImpl) origArray.materialize()).getContent(), origSize, newSize);
        }
    }

    public static int[] repValues(RLogical origArray, int origSize, int newSize) {
        if (origSize == 1) {
            return rep(origArray.getLogical(0), newSize);
        } else {
            return rep(((LogicalImpl) origArray.materialize()).getContent(), origSize, newSize);
        }
    }

    public static double[] repValues(RDouble origArray, int origSize, int newSize) {
        if (origSize == 1) {
            return rep(origArray.getDouble(0), newSize);
        } else {
            return rep(((DoubleImpl) origArray.materialize()).getContent(), origSize, newSize);
        }
    }

    public static double[] repValues(RComplex origArray, int origSize, int newSize) {
        if (origSize == 1) {
            return rep(new double[]{origArray.getReal(0), origArray.getImag(0)}, 2, newSize * 2);
        } else {
            return rep(((ComplexImpl) origArray.materialize()).getContent(), origSize * 2, newSize * 2);
        }
    }

    public static String[] repValues(RString origArray, int origSize, int newSize) {
        if (origSize == 1) {
            return rep(origArray.getString(0), newSize);
        } else {
            return rep(((StringImpl) origArray.materialize()).getContent(), origSize, newSize);
        }
    }

    public static RArray.Names repNames(RArray.Names origNames, int origSize, int size) {
        if (origNames == null) { return null; }
        return RArray.Names.create(rep(origNames.sequence(), origSize, size));
    }

    public static RRaw repInt(final RRaw orig, final int origSize, final int size) {

        RArray.Names names = orig.names();
        if (!EAGER && names == null) {
            return new View.RRawProxy<RRaw>(orig) {

                @Override public RArray.Names names() {
                    return null;
                }

                @Override public int size() {
                    return size;
                }

                @Override public byte getRaw(int i) {
                    return orig.getRaw(i % origSize);
                }

                @Override public Attributes attributes() { // drop attributes
                    return null;
                }

            };
        } else {
            return RRawFactory.getFor(repValues(orig, origSize, size), null, repNames(names, origSize, size));
        }
    }

    public static RLogical repInt(final RLogical orig, final int origSize, final int size) {
        RArray.Names names = orig.names();

        if (!EAGER && names == null) {
            return new View.RLogicalProxy<RLogical>(orig) {

                @Override public RArray.Names names() {
                    return null;
                }

                @Override public int size() {
                    return size;
                }

                @Override public int getLogical(int i) {
                    return orig.getLogical(i % origSize);
                }

                @Override public Attributes attributes() { // drop attributes
                    return null;
                }
            };
        } else {
            return RLogicalFactory.getFor(repValues(orig, origSize, size), null, repNames(names, origSize, size));
        }
    }

    public static RInt repInt(final RInt orig, final int origSize, final int size) {
        RArray.Names names = orig.names();

        if (!EAGER && names == null) {
            return new View.RIntProxy<RInt>(orig) {

                @Override public RArray.Names names() {
                    return null;
                }

                @Override public int size() {
                    return size;
                }

                @Override public int getInt(int i) {
                    return orig.getInt(i % origSize);
                }

                @Override public Attributes attributes() { // drop attributes
                    return null;
                }
            };
        } else {
            return RIntFactory.getFor(repValues(orig, origSize, size), null, repNames(names, origSize, size));
        }
    }

    public static RDouble repInt(final RDouble orig, final int origSize, final int size) {
        RArray.Names names = orig.names();

        if (!EAGER && names == null) {
            return new View.RDoubleProxy<RDouble>(orig) {

                @Override public RArray.Names names() {
                    return null;
                }

                @Override public int size() {
                    return size;
                }

                @Override public double getDouble(int i) {
                    return orig.getDouble(i % origSize);
                }

                @Override public Attributes attributes() { // drop attributes
                    return null;
                }

            };
        } else {
            return RDoubleFactory.getFor(repValues(orig, origSize, size), null, repNames(names, origSize, size));
        }
    }

    public static RComplex repInt(final RComplex orig, final int origSize, final int size) {
        RArray.Names names = orig.names();

        if (!EAGER && names == null) {
            return new View.RComplexProxy<RComplex>(orig) {

                @Override public RArray.Names names() {
                    return null;
                }

                @Override public int size() {
                    return size;
                }

                @Override public double getReal(int i) {
                    return orig.getReal(i % origSize);
                }

                @Override public double getImag(int i) {
                    return orig.getImag(i % origSize);
                }

                @Override public Attributes attributes() { // drop attributes
                    return null;
                }
            };
        } else {
            return RComplexFactory.getFor(repValues(orig, origSize, size), null, repNames(names, origSize, size));
        }
    }

    public static RString repInt(final RString orig, final int origSize, final int size) {
        RArray.Names names = orig.names();

        if (!EAGER && names == null) {
            return new View.RStringProxy<RString>(orig) {

                @Override public RArray.Names names() {
                    return null;
                }

                @Override public int size() {
                    return size;
                }

                @Override public String getString(int i) {
                    return orig.getString(i % origSize);
                }

                @Override public Attributes attributes() { // drop attributes
                    return null;
                }
            };
        } else {
            return RStringFactory.getFor(repValues(orig, origSize, size), null, repNames(names, origSize, size));
        }
    }

    // TODO: support non-scalar times argument
    public static RAny genericRepInt(ASTNode ast, RAny arg0, RAny arg1) {

        int times = -1;
        if (arg1 instanceof RDouble) {
            RDouble da = (RDouble) arg1;
            checkScalar(da, ast);
            double d = da.getDouble(0);
            // FIXME: perhaps fitsRInt => isFinite ?
            if (!RDouble.RDoubleUtils.isFinite(d) || d < 0 || !RDouble.RDoubleUtils.fitsRInt(d)) { throw RError.getInvalidTimes(ast); }
            times = (int) d;
        } else if (arg1 instanceof RInt) {
            RInt ia = (RInt) arg1;
            checkScalar(ia, ast);
            int i = ia.getInt(0);
            if (i < 0 || i == RInt.NA) { throw RError.getInvalidTimes(ast); }
            times = i;
        } else if (arg1 instanceof RLogical) {
            RLogical la = (RLogical) arg1;
            checkScalar(la, ast);
            int l = la.getLogical(0);
            if (l == RLogical.TRUE) { return arg0; }
            if (l == RLogical.FALSE) {
                times = 0; // NOTE: we won't simply return an empty array because of names handling (if arg0 is named, the empty array should be too)
            } else {
                // l == NA
                throw RError.getInvalidTimes(ast);
            }
        } else {
            Utils.nyi("unsupported times argument");
        }
        if (arg0 instanceof RInt) {
            RInt orig = (RInt) arg0;
            int origSize = orig.size();
            return repInt(orig, origSize, origSize * times);
        }
        if (arg0 instanceof RDouble) {
            RDouble orig = (RDouble) arg0;
            int origSize = orig.size();
            return repInt(orig, origSize, origSize * times);
        }
        if (arg0 instanceof RLogical) {
            RLogical orig = (RLogical) arg0;
            int origSize = orig.size();
            return repInt(orig, origSize, origSize * times);
        }
        if (arg0 instanceof RString) {
            RString orig = (RString) arg0;
            int origSize = orig.size();
            return repInt(orig, origSize, origSize * times);
        }
        if (arg0 instanceof RComplex) {
            RComplex orig = (RComplex) arg0;
            int origSize = orig.size();
            return repInt(orig, origSize, origSize * times);
        }
        if (arg0 instanceof RRaw) {
            RRaw orig = (RRaw) arg0;
            int origSize = orig.size();
            return repInt(orig, origSize, origSize * times);
        }
        Utils.nyi("unsupported base type for rep"); // TODO: support list type
        return null;
    }

    public static RAny genericRepLengthOut(ASTNode ast, RAny argX, RAny argLengthOut) {
        RInt ilengthOut = Convert.coerceToIntWarning(argLengthOut, ast); // FIXME: not exactly R semantics, R will not produce warnings on coercion for arguments at indexes 2 and higher
        int len;
        if (ilengthOut.size() == 1) {
            len = ilengthOut.getInt(0);
            if (len < 0) {
                if (len != RInt.NA) {
                    throw RError.getInvalidArgument(ast, "length.out");
                } else {
                    return argX;
                }
            }
        } else {
            return argX;
        }
        if (argX instanceof RDouble) {
            RDouble x = (RDouble) argX;
            return repInt(x, x.size(), len);
        }
        if (argX instanceof RInt) {
            RInt x = (RInt) argX;
            return repInt(x, x.size(), len);
        }
        if (argX instanceof RLogical) {
            RLogical x = (RLogical) argX;
            return repInt(x, x.size(), len);
        }
        if (argX instanceof RString) {
            RString x = (RString) argX;
            return repInt(x, x.size(), len);
        }
        if (argX instanceof RComplex) {
            RComplex x = (RComplex) argX;
            return repInt(x, x.size(), len);
        }
        throw Utils.nyi("unsupported base type for rep");
    }
}
