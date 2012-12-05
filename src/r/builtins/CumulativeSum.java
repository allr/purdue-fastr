package r.builtins;

import r.*;
import r.Convert;
import r.Convert.NAIntroduced;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.RDouble.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;


public class CumulativeSum {

    private static final String[] paramNames = new String[]{"x"};
    private static final int IX = 0;

    public static RDouble cumsum(RDouble x, Context context, ASTNode ast) {
        RDouble input = x.materialize();
        int size = x.size();
        double[] content = new double[size];

        if (size > 0) {
            double accum = 0;
            for (int i = 0; i < size; i++) {
                double value = input.getDouble(i);
                if (RDoubleUtils.isNA(value)) {
                    return finishWithNAs(content, i);
                }
                accum += value;
                content[i] = accum;
            }
        }
        return RDouble.RDoubleFactory.getFor(content); // drop dimensions
    }

    private static RDouble finishWithNAs(double[] content, int fromIndex) {
        for (int i = fromIndex; i < content.length; i++) {
            content[i] = RDouble.NA;
        }
        return RDouble.RDoubleFactory.getFor(content);
    }

    public static RInt cumsum(RInt x, RContext context, ASTNode ast) {
        RInt input = x.materialize();
        int size = x.size();
        int[] content = new int[size];

        if (size > 0) {
            int accum = 0;
            for (int i = 0; i < size; i++) {
                int value = input.getInt(i);
                if (value == RInt.NA) {
                    return finishWithNAs(content, i);
                }
                accum = Arithmetic.ADD.op(context, ast, accum, value);
                if (accum == RInt.NA) {
                    context.warning(ast, RError.INTEGER_OVERFLOW);
                    return finishWithNAs(content, i);
                }
                content[i] = accum;
            }
        }
        return RInt.RIntFactory.getFor(content); // drop dimensions
    }

    private static RInt finishWithNAs(int[] content, int fromIndex) {
        for (int i = fromIndex; i < content.length; i++) {
            content[i] = RInt.NA;
        }
        return RInt.RIntFactory.getFor(content);
    }


    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final boolean[] provided = a.providedParams;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                NAIntroduced naIntroduced = new NAIntroduced();

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny x) {

                    if (x instanceof RDouble) {
                        return cumsum((RDouble) x, context, ast);
                    } else if (x instanceof RInt) {
                        return cumsum((RInt) x, context, ast);
                    } else if (x instanceof RLogical) {
                        return cumsum(((RLogical) x).asInt(), context, ast);
                    } else if (x instanceof RNull) {
                        return RDouble.EMPTY;
                    }

                    if (x instanceof RString) {
                        naIntroduced.naIntroduced = false;
                        RDouble res = cumsum(x.asDouble(naIntroduced), context, ast);
                        if (naIntroduced.naIntroduced) {
                            context.warning(ast, RError.NA_INTRODUCED_COERCION);
                        }
                        return res;
                    } else {
                        Utils.nyi("unsupported type");
                        return null;
                    }
                }
            };
        }
    };
}
