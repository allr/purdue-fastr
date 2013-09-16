package r.builtins;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

import java.lang.Integer;

/**
 * "sum"
 *
 * <pre>
 * ... -- numeric or complex or logical vectors.
 * na.rm -- logical. Should missing values (including NaN) be removed?
 * </pre>
 */
// FIXME: optimize for single argument

// NOTE: GNU-R currently does not require distinction of NA and NaN in sum, na.rm works for both; if na.rm is false and the input contains either,
// the result can contain either; being stricter than GNU-R does have performance overhead

final class Sum extends CallFactory {

    static final CallFactory _ = new Sum("sum", new String[]{"...", "na.rm"}, new String[]{});

    Sum(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static void sum(RComplex v, boolean narm, double[] res) {
        int size = v.size();
        double rreal = 0;
        double rimag = 0;
        for (int i = 0; i < size; i++) {
            double real = v.getReal(i);  // FIXME: this will be very slow for complex arithmetic views
            double imag = v.getImag(i);
            if (narm) {
                if (RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
                    continue;
                }
            }
            rreal += real;
            rimag += imag;
        }
        res[0] = rreal;
        res[1] = rimag;
    }

    public static double sum(RDouble v, boolean narm) {
        int size = v.size();
        double res = 0;
        for (int i = 0; i < size; i++) {
            double d = v.getDouble(i);
            if (narm) {
                if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                    continue;
                }
            }
            res += d;
        }
        return res;
    }

    public static double sum(RInt v, boolean narm) {
        int size = v.size();
        double res = 0;
        for (int i = 0; i < size; i++) {
            int l = v.getInt(i);
            if (l == RInt.NA) {
                if (narm) {
                    continue;
                } else {
                    return RInt.NA;
                }
            } else {
                res += l;
            }
        }
        return res;
    }


    // TODO: this optimization should be done more thoroughly, it could help much more with lazy comparison

    // for expressions " sum(x) relop const " or " const relop sum(x) ", return the value of the constant
    // only if the constant is a scalar numeric non-negative value that fits into integer
    // (this is for optimizations where x is a logical vector)

    private static int relationOperatorAgainst(ASTNode ast) {
        // optimize for "sum(logical) == constant"
        ASTNode sumParent = ast.getParent();
        if (sumParent != null && sumParent instanceof r.nodes.BinaryOperation) {
            r.nodes.BinaryOperation op = (r.nodes.BinaryOperation) sumParent;
            ASTNode other;
            if (op.getRHS() == ast) {
                other = op.getLHS();
            } else {
                other = op.getRHS();
            }
            if (other instanceof r.nodes.Constant) {
                RAny res = ((r.nodes.Constant) other).getValue();
                if ((res instanceof RDouble || res instanceof RInt || res instanceof RLogical) && ((RArray)res).size() == 1) {
                    int cvalue = res.asInt().getInt(0);
                    if (cvalue >= 0) {
                        return cvalue;
                    }
                }

            }
        }
        return -1;
    }

    // the maximum value it makes sense to keep summing up to with a logical vector as argument (note: logicals have only positive non-na values)
    private static int maxLogicalSum(ASTNode ast) {
       int res = relationOperatorAgainst(ast);
       ASTNode sumParent = ast.getParent();
       if (res != -1 && (sumParent instanceof r.nodes.GT || sumParent instanceof r.nodes.GE || sumParent instanceof r.nodes.EQ ||
               sumParent instanceof r.nodes.NE || sumParent instanceof r.nodes.LT || sumParent instanceof r.nodes.LE)) {
           return res;
       }
       return -1;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 0) { return new Builtin.Builtin0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return RInt.BOXED_ZERO;
            }
        }; }
        ArgumentInfo ia = check(call, names, exprs);

        final int narmPosition = ia.position("na.rm");

        // we keep this -1 whenever there is more than one argument, because in that case there could be
        // so many logical values to add that we could get logical overflow

        // TODO: revisit this when supporting long vectors
        final int maxLogicalSum = (names.length > 2 || narmPosition != -1) ? -1 : maxLogicalSum(call);

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                boolean naRM = false;
                if (narmPosition != -1) {
                    RAny v = args[narmPosition];
                    if (v instanceof RLogical) { // FIXME: use/create some method for this instead
                        RLogical l = (RLogical) v;
                        naRM = l.size() == 0 || l.getLogical(0) != RLogical.FALSE;
                    } else if (v instanceof RInt) {
                        RInt i = (RInt) v;
                        naRM = i.size() == 0 || i.getInt(0) != 0;
                    } else if (v instanceof RDouble) {
                        RDouble d = (RDouble) v;
                        naRM = d.size() == 0 || d.getDouble(0) != 0;
                    } else {
                        naRM = true;
                    }
                }
                boolean hasDouble = false;
                boolean hasInt = false;
                boolean hasComplex = false;

                for (int i = 0; i < args.length; i++) {
                    if (i == narmPosition) {
                        continue;
                    }
                    RAny v = args[i];
                    if (v instanceof RDouble) {
                        hasDouble = true;
                    } else if (v instanceof RComplex) {
                        hasComplex = true;
                    } else if (v instanceof RInt) {
                        hasInt = true;
                    } else if (v instanceof RLogical || v instanceof RNull) {
                        // ok
                    } else {
                        throw RError.getInvalidTypeArgument(ast, v.typeOf());
                    }
                }

                if (hasComplex) {
                    double[] tmp = new double[2];
                    double rreal = 0;
                    double rimag = 0;
                    for (int i = 0; i < args.length; i++) {
                        if (i == narmPosition) {
                            continue;
                        }
                        RAny v = args[i];
                        if (v instanceof RNull) {
                            continue;
                        }
                        sum(v.asComplex(), naRM, tmp);
                        double real = tmp[0];
                        double imag = tmp[1];
                        if (RComplex.RComplexUtils.eitherIsNAorNaN(real, imag)) {
                            // FIXME: this is to retain NA vs NaN distinction, but indeed would have overhead in common case
                            rreal = real;
                            rimag = imag;
                            break;
                        } else {
                            rreal += real;
                            rimag += imag;
                        }
                    }
                    return RComplex.RComplexFactory.getScalar(rreal, rimag);
                } else if (hasDouble) {
                    double res = 0;
                    for (int i = 0; i < args.length; i++) {
                        if (i == narmPosition) {
                            continue;
                        }
                        RAny v = args[i];
                        if (v instanceof RNull) {
                            continue;
                        }
                        double d = sum(v.asDouble(), naRM);
                        if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                            // FIXME: this is to retain NA vs NaN distinction, but indeed would have overhead in common case
                            res = d;
                            break;
                        } else {
                            res += d;
                        }
                    }
                    return RDouble.RDoubleFactory.getScalar(res);
                } else if (hasInt || maxLogicalSum == -1) {
                    double res = 0;
                    for (int i = 0; i < args.length; i++) {
                        if (i == narmPosition) {
                            continue;
                        }
                        RAny v = args[i];
                        if (v instanceof RNull) {
                            continue;
                        }
                        res += sum(v.asInt(), naRM);
                    }
                    if (!(res < Integer.MIN_VALUE || res > Integer.MAX_VALUE)) {
                        // FIXME: this may not rigorously reflect R semantics, check if the
                        //        range should be checked for individual elements or not
                        return RInt.RIntFactory.getScalar((int) res);
                    } else {
                        return RInt.BOXED_NA;
                    }
                } else {
                    // sum(logical) cmpop const
                    int argi = narmPosition == 0 ? 1 : 0;
                    RLogical v = (RLogical) args[argi];
                    int size = v.size();
                    int res = 0;
                    int i = 0;
                    for (; i < size; i++) {
                        int l = v.getLogical(i);
                        if (l == RInt.NA) {
                            if (naRM) {
                                continue;
                            } else {
                                return RInt.BOXED_NA;
                            }
                        } else {
                            res += l;
                            if (res > maxLogicalSum) {
                                break;
                            }
                        }
                    }
                    if (!naRM) {
                        for (; i < size; i++) { // still have to check for NAs
                            int l = v.getLogical(i);
                            if (l == RInt.NA) {
                                return RInt.BOXED_NA;
                            }
                        }
                    }
                    if (res < 0) { // overflow
                        return RInt.BOXED_NA;
                    } else {
                        return RInt.RIntFactory.getScalar(res);
                    }
                }
            }
        };
    }
}
