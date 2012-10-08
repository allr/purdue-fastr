package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ASTNode;
import r.nodes.truffle.*;

// FIXME: Truffle can't optimize BuiltIn2, so using BuiltIn
public class Colon {

    // a simple version that eagerly creates the vector, create(int, int) and (double, double) below are more efficient
    public static RAny createEager(int left, int right) {
        if (left <= right) {
            int len = right - left + 1;
            int[] data = new int[len];
            int val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val++;
            }
            return RInt.RIntFactory.getForArray(data);
        } else {
            int len = left - right + 1;
            int[] data = new int[len];
            int val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val--;
            }
            return RInt.RIntFactory.getForArray(data);
        }
    }

    public static RAny create(int left, int right) {
        int step = (left <= right) ? 1 : -1;
        return RInt.RIntFactory.forSequence(left, right, step);
    }

    public static RAny create(double left, double right) {
        if (left <= right) {
            int len = (int) (right - left + 1);  // FIXME: probably should check for a too long vector
            double[] data = new double[len];
            double val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val;
                val = val + 1.0;
            }
            return RDouble.RDoubleFactory.getForArray(data);
        } else {
            int len = (int) (left - right + 1); // FIXME: probably should check for a too long vector
            double[] data = new double[len];
            double val = left;
            for (int i = 0; i < len; i++) {
                data[i] = val;
                val = val - 1.0;
            }
            return RDouble.RDoubleFactory.getForArray(data);
        }
    }

    public static void checkScalar(RArray a, ASTNode ast, RContext context) {
      int n = a.size();
      if (n == 0) {
          throw RError.getNulLength(ast);
      }
      if (n > 1) {
          context.warning(ast, String.format(RError.ONLY_FIRST_USED, n));
      }
    }

    public static void checkNAandNaN(double d, ASTNode ast) {
        if (RDouble.RDoubleUtils.isNAorNaN(d)) {
            throw RError.getNAorNaN(ast);
        }
    }

    public static void checkNA(int i, ASTNode ast) {
        if (i == RInt.NA) {
            throw RError.getNAorNaN(ast);
        }
    }

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn(call, names, exprs) {
                @SuppressWarnings("cast")
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                    RAny arg0 = args[0];
                    RAny arg1 = args[1];
                    if (arg0 instanceof RInt) {
                        RInt a0rint = (RInt) arg0;
                        Colon.checkScalar(a0rint, ast, context);
                        int a0 = a0rint.getInt(0);
                        Colon.checkNA(a0, ast);
                        if (arg1 instanceof RInt) {
                            RInt a1rint = (RInt) arg1;
                            Colon.checkScalar(a1rint, ast, context);
                            int a1 = a1rint.getInt(0);
                            Colon.checkNA(a1, ast);
                            return Colon.create(a0, a1);
                        }
                        if (arg1 instanceof RDouble) {
                            RDouble a1rdbl = (RDouble) arg1;
                            Colon.checkScalar(a1rdbl, ast, context);
                            double a1 = a1rdbl.getDouble(0);
                            Colon.checkNAandNaN(a1, ast);
                            if (RDouble.RDoubleUtils.fitsRInt(a1)) {
                                return Colon.create(a0, (int) a1);
                                    // note casting to int does exactly what we want - truncate towards zero
                            } else {
                                return Colon.create(a0, a1);
                            }
                        }
                        Utils.nyi("unsupported argument type for colon operator");
                    }

                    if (arg0 instanceof RDouble) {
                        RDouble a0rdbl = (RDouble) arg0;
                        Colon.checkScalar(a0rdbl, ast, context);
                        double a0 = a0rdbl.getDouble(0);
                        Colon.checkNAandNaN(a0, ast);
                        int ia0 = (int) a0;
                        if (((double) ia0) == a0) {  // this re-casting is intentional
                            // note: nearly copy-paste from above, but we should rewrite to nodes anyway
                            if (arg1 instanceof RInt) {
                                RInt a1rint = (RInt) arg1;
                                Colon.checkScalar(a1rint, ast, context);
                                int a1 = a1rint.getInt(0);
                                Colon.checkNA(a1, ast);
                                return Colon.create(ia0, a1);
                            }
                            if (arg1 instanceof RDouble) {
                                RDouble a1rdbl = (RDouble) arg1;
                                Colon.checkScalar(a1rdbl, ast, context);
                                double a1 = a1rdbl.getDouble(0);
                                Colon.checkNAandNaN(a1, ast);
                                if (RDouble.RDoubleUtils.fitsRInt(a1)) {
                                    return Colon.create(ia0, (int) a1);
                                        // note casting to int does exactly what we want - truncate towards zero
                                } else {
                                    return Colon.create(a0, a1);
                                }
                            }
                            Utils.nyi("unsupported argument type for colon operator");

                        } else {
                            if (arg1 instanceof RDouble) {
                                RDouble a1rdbl = (RDouble) arg1;
                                Colon.checkScalar(a1rdbl, ast, context);
                                double a1 = a1rdbl.getDouble(0);
                                Colon.checkNAandNaN(a1, ast);
                                return Colon.create(a0, a1);
                            } else {
                                Utils.nyi("unsupported argument type for colon operator");
                            }
                        }
                    }
                    Utils.nyi("unsupported argument types for colon operator");
                    return null;
                }
            };
        }
    };
}
