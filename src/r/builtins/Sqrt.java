package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.View.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "sqrt"
 *
 * <pre>
 * x -- a numeric or complex vector or array.
 * </pre>
 */
// FIXME: scalar optimizations
// FIXME: use math base?
// FIXME: NaNs produce warning should be issued only once for a vector
final class Sqrt extends CallFactory {

    static final CallFactory _ = new Sqrt("sqrt", new String[]{"x"}, new String[]{"x"});

    private Sqrt(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                RDouble typedArg;
                if (arg instanceof RDouble) {
                    typedArg = (RDouble) arg;
                } else if (arg instanceof RInt || arg instanceof RLogical) {
                    typedArg = arg.asDouble();
                } else {
                    throw RError.getNonNumericMath(ast);
                }
                if (typedArg.size() == 1) {
                    double d = typedArg.getDouble(0);
                    return RDouble.RDoubleFactory.getScalar(sqrt(d, ast));
                }
                return TracingView.ViewTrace.trace(new View.RDoubleProxy<RDouble>(typedArg) {
                    @Override
                    public double getDouble(int i) {
                        double d = orig.getDouble(i);
                        return sqrt(d, ast);
                    }

                    @Override
                    public void materializeInto(double[] resContent) {
                        if (orig instanceof DoubleImpl) {
                            sqrt(orig.getContent(), resContent, ast);
                        } else if (orig instanceof RDoubleView) {
                            ((RDoubleView) orig).materializeInto(resContent);
                            sqrt(resContent, resContent, ast);
                        } else  {
                            super.materializeInto(resContent);
                        }
                    }

                    @Override
                    public void materializeIntoOnTheFly(double[] resContent) {
                        if (orig instanceof DoubleImpl) {
                            sqrt(orig.getContent(), resContent, ast);
                        } else  {
                            super.materializeIntoOnTheFly(resContent);
                        }
                    }
                });
            }
        };
    }

    public static double sqrt(double d, ASTNode ast) {
        if (RDouble.RDoubleUtils.isNAorNaN(d)) {
            return RDouble.NA;
        } else {
            double res = Math.sqrt(d);
            if (RDouble.RDoubleUtils.isNAorNaN(res)) {
                RContext.warning(ast, RError.NAN_PRODUCED);
            }
            return res;
        }
    }

    // FIXME: could also optimize for the case that the operation does produce NaNs (although that is not so common)
    public static void sqrt(double[] x, double[] res, ASTNode ast) {
        for (int i = 0; i < x.length; i++) {
            res[i] = sqrt(x[i], ast);
        }
    }
}
