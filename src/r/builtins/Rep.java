package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

// FIXME: Truffle can't handle BuiltIn2
public class Rep {

    public static void checkScalar(RArray a, ASTNode ast) {
        int n = a.size();
        if (n != 1) {
            throw RError.getInvalidTimes(ast);
        }
    }

    public static RAny rep(ASTNode ast, RAny arg0, RAny arg1) {

        int times = -1;
        if (arg1 instanceof RDouble) {
            RDouble da = (RDouble) arg1;
            checkScalar(da, ast);
            double d = da.getDouble(0);
            if (d == 0) {
                return Utils.createEmptyArray(arg0);
            }
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
        final int ftimes = times;
        if (arg0 instanceof RInt) {
            final RInt orig = (RInt) arg0;
            final int osize = orig.size();
            final int size = osize * ftimes;

            return new View.RIntView() {

                @Override
                public int size() {
                    return size;
                }

                @Override
                public int getInt(int i) {
                    return orig.getInt(i % osize);
                }

                @Override
                public boolean isShared() {
                    return orig.isShared();
                }

                @Override
                public void ref() {
                    orig.ref();
                }
            };
        }
        if (arg0 instanceof RDouble) {
            final RDouble orig = (RDouble) arg0;
            final int osize = orig.size();
            final int size = osize * ftimes;

            return new View.RDoubleView() {

                @Override
                public int size() {
                    return size;
                }

                @Override
                public double getDouble(int i) {
                    return orig.getDouble(i % osize);
                }

                @Override
                public boolean isShared() {
                    return orig.isShared();
                }

                @Override
                public void ref() {
                    orig.ref();
                }
            };
        }
        if (arg0 instanceof RLogical) {
            final RLogical orig = (RLogical) arg0;
            final int osize = orig.size();
            final int size = osize * ftimes;

            return new View.RLogicalView() {

                @Override
                public int size() {
                    return size;
                }

                @Override
                public int getLogical(int i) {
                    return orig.getLogical(i % osize);
                }

                @Override
                public boolean isShared() {
                    return orig.isShared();
                }

                @Override
                public void ref() {
                    orig.ref();
                }
            };
        }
        Utils.nyi("unsupported base type for rep");
        return null;
    }

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn.BuiltIn2(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1) {
                    return rep(ast, arg0, arg1);
                }
            };
        }
    };
}
