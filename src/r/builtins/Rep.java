package r.builtins;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

// FIXME: Truffle can't handle BuiltIn2
public class Rep {

    private static final String[] repIntParamNames = new String[]{"x", "times"};
    private static final String[] repParamNames = new String[]{"x", "..."};

    private static final int IX = 0;
    private static final int ITIMES = 1;

    public static void checkScalar(RArray a, ASTNode ast) {
        int n = a.size();
        if (n != 1) {
            throw RError.getInvalidTimes(ast);
        }
    }

    public static RInt repInt(final RInt orig, final int origSize, final int size) {
        return new View.RIntView() {

            @Override
            public int size() {
                return size;
            }

            @Override
            public int getInt(int i) {
                return orig.getInt(i % origSize);
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }
        };
    }

    public static RDouble repInt(final RDouble orig, final int origSize, final int size) {
        return new View.RDoubleView() {

            @Override
            public int size() {
                return size;
            }

            @Override
            public double getDouble(int i) {
                return orig.getDouble(i % origSize);
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }
        };
    }

    public static RLogical repInt(final RLogical orig, final int origSize, final int size) {
        return new View.RLogicalView() {

            @Override
            public int size() {
                return size;
            }

            @Override
            public int getLogical(int i) {
                return orig.getLogical(i % origSize);
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }
        };
    }

    public static RString repInt(final RString orig, final int origSize, final int size) {
        return new View.RStringView() {

            @Override
            public int size() {
                return size;
            }

            @Override
            public String getString(int i) {
                return orig.getString(i % origSize);
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }
        };
    }

    public static RComplex repInt(final RComplex orig, final int origSize, final int size) {
        return new View.RComplexView() {

            @Override
            public int size() {
                return size;
            }

            @Override
            public double getReal(int i) {
                return orig.getReal(i % origSize);
            }

            @Override
            public double getImag(int i) {
                return orig.getImag(i % origSize);
            }

            @Override
            public boolean isSharedReal() {
                return orig.isShared();
            }

            @Override
            public void ref() {
                orig.ref();
            }
        };
    }

    public static RAny genericRepInt(ASTNode ast, RAny arg0, RAny arg1) {

        int times = -1;
        if (arg1 instanceof RDouble) {
            RDouble da = (RDouble) arg1;
            checkScalar(da, ast);
            double d = da.getDouble(0);
            if (d == 0) {
                return Utils.createEmptyArray(arg0);
            }
                // FIXME: perhaps fitsRInt => isFinite ?
            if (!RDouble.RDoubleUtils.isFinite(d) || d < 0 || !RDouble.RDoubleUtils.fitsRInt(d)) {
                throw RError.getInvalidTimes(ast);
            }
            times = (int) d;
        } else if (arg1 instanceof RInt) {
            RInt ia = (RInt) arg1;
            checkScalar(ia, ast);
            int i = ia.getInt(0);
            if (i == 0) {
                return Utils.createEmptyArray(arg0);
            }
            if (i < 0 || i == RInt.NA) {
                throw RError.getInvalidTimes(ast);
            }
            times = i;
        } else if (arg1 instanceof RLogical) {
            RLogical la = (RLogical) arg1;
            checkScalar(la, ast);
            int l = la.getLogical(0);
            if (l == RLogical.TRUE) {
                return arg0;
            }
            if (l == RLogical.FALSE) {
                return Utils.createEmptyArray(arg0);
            }
            // l == NA
            throw RError.getInvalidTimes(ast);
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
        Utils.nyi("unsupported base type for rep");
        return null;
    }

    public static RAny genericRepLengthOut(RContext context, ASTNode ast, RAny argX, RAny argLengthOut) {
        RInt ilengthOut = Convert.coerceToIntWarning(argLengthOut, context, ast); // FIXME: not exactly R semantics, R will not produce warnings on coercion for arguments at indexes 2 and higher
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
        Utils.nyi("unsupported base type for rep");
        return null;
    }

    public static final CallFactory REPINT_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, repIntParamNames);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, repIntParamNames[IX]);
            }
            if (!provided[ITIMES]) {
                BuiltIn.missingArg(call, repIntParamNames[ITIMES]);
            }
            final boolean xfirst = paramPositions[IX] == 0;
            return new BuiltIn.BuiltIn2(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1) {
                    return genericRepInt(ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
                }
            };
        }
    };

    public static final CallFactory REP_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, repParamNames);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, repIntParamNames[IX]);
            }
            if (names.length == 2) {
                int otherPos = paramPositions[IX] == 0 ? 1 : 0;
                RSymbol otherName = names[otherPos];
                if (otherName == null || otherName == RSymbol.getSymbol("times")) {
                    return REPINT_FACTORY.create(call, names, exprs);
                }
                if (otherName == RSymbol.getSymbol("length.out")) {
                    final boolean xfirst = paramPositions[IX] == 0;
                    return new BuiltIn.BuiltIn2(call, names, exprs) {
                        @Override
                        public RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1) {
                            return genericRepLengthOut(context, ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
                        }
                    };

                }
            }
            Utils.nyi("unsupported rep arguments");
            return null;
        }
    };
}
