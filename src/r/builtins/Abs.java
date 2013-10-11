package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "abs"
 *
 * <pre>
 * x -- a numeric or complex vector or array.
 * </pre>
 */
// FIXME: use node rewriting to get rid of the type checks
// FIXME: use math base?
public class Abs extends CallFactory {

    static final Abs _ = new Abs("abs", new String[]{"x"}, null);

    Abs(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static double abs(double d) {
        return RDouble.RDoubleUtils.arithIsNA(d) ? RDouble.NA : Math.abs(d);
    }

    public static void abs(double[] x, double[] res) {
        for (int i = 0; i < x.length; i++) {
            res[i] = abs(x[i]);
        }
    }

    public static int abs(int v) {
        return (v < 0) ? -v : v; // NOTE: this also works with NA, NA will remain NA
    }

    public static double abs(double real, double imag) {
        return RComplex.RComplexUtils.eitherIsNA(real, imag) ? RDouble.NA : Arithmetic.chypot(real, imag);
    }

    public static RDouble abs(final RDouble orig) {

        return TracingView.ViewTrace.trace(new View.RDoubleProxy<RDouble>(orig) {

            @Override public double getDouble(int i) {
                return abs(orig.getDouble(i));
            }

            @Override
            public void materializeInto(double[] resContent) {
                if (orig instanceof DoubleImpl) {
                    abs(orig.getContent(), resContent);
                } else if (orig instanceof RDoubleView) {
                    ((RDoubleView) orig).materializeInto(resContent);
                    abs(resContent, resContent);
                } else  {
                    super.materializeInto(resContent);
                }
            }

            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (orig instanceof DoubleImpl) {
                    abs(orig.getContent(), resContent);
                } else  {
                    super.materializeIntoOnTheFly(resContent);
                }
            }
        });
    }

    public static RInt abs(final RInt orig) {

        return TracingView.ViewTrace.trace(new View.RIntProxy<RInt>(orig) {
            @Override public int getInt(int i) {
                return abs(orig.getInt(i));
            }
        });
    }

    public static RDouble abs(final RComplex orig) {

        return TracingView.ViewTrace.trace(new View.RDoubleProxy<RComplex>(orig) {
            @Override public double getDouble(int i) {
                return abs(orig.getReal(i), orig.getImag(i));
            }
        });
    }

    @Override
    public RNode create(final ASTNode call, final RSymbol[] names, final RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public final RAny doBuiltIn(Frame frame, RAny arg) {
                try {
                    throw new SpecializationException(null);
                } catch (SpecializationException e) {
                    if (arg instanceof ScalarIntImpl) {
                        replace(createScalarInt(call, names, exprs));

                    } else {
                        replace(createGeneric(call, names, exprs));
                    }
                    return generic(arg);

                }
            }
        };
    }

    public static RNode createScalarInt(final ASTNode call, final RSymbol[] names, final RNode[] exprs) {

        return new Specialized(call, exprs[0]) {

            // use this - but when the executeScalarInteger is used widely
//            @Override
//            public  Object execute(Frame frame) {
//                return RLogical.RLogicalFactory.getScalar(executeScalarInteger(frame));
//            }

            @Override
            public Object execute(Frame frame) {
                Object val = expr.execute(frame);
                try {
                    if (!(val instanceof ScalarIntImpl)) {
                        throw new SpecializationException(null);
                    }
                    int i = ((ScalarIntImpl) val).getInt();
                    return (i < 0) ? RInt.RIntFactory.getScalar(-i) : val; // NOTE: this also works with NA, NA will remain NA
                } catch (SpecializationException e) {
                    exprs[0].replace(expr); // keep the new child
                    replace(createGeneric(call, names, exprs));
                    return generic((RAny) val);
                }
            }

            @Override
            public int executeScalarInteger(Frame frame) throws SpecializationException {
                try {
                    int i = expr.executeScalarInteger(frame);
                    return abs(i);
                } catch (SpecializationException e) {
                    replace(createGeneric(call, names, exprs));
                    throw new SpecializationException(generic((RAny) e.getResult()));
                }
            }

        };
    }

    public static RNode createGeneric(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new Builtin.Builtin1(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny arg) {
                return generic(arg);
            }

        };
    }

    public abstract static class Specialized extends BaseR {

        @Child RNode expr;

        public Specialized(ASTNode ast, RNode expr) {
            super(ast);
            this.expr = adoptChild(expr);
        }

        @Override
        public Object execute(Frame frame) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (expr == oldNode) {
                expr = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }
    }

    public static RAny generic(RAny arg) {
        if (arg instanceof ScalarDoubleImpl) { return RDouble.RDoubleFactory.getScalar(abs(((ScalarDoubleImpl) arg).getDouble())); }
        if (arg instanceof ScalarIntImpl) { return RInt.RIntFactory.getScalar(abs(((ScalarIntImpl) arg).getInt())); }
        if (arg instanceof ScalarComplexImpl) {
            ScalarComplexImpl c = (ScalarComplexImpl) arg;
            return RDouble.RDoubleFactory.getScalar(abs(c.getReal(), c.getImag()));
        }
        if (arg instanceof RDouble) { return abs((RDouble) arg); }
        if (arg instanceof RInt) { return abs((RInt) arg); }
        if (arg instanceof RComplex) { return abs((RComplex) arg); }
        if (arg instanceof RLogical) { return arg.asInt(); }
        throw Utils.nyi();
    }
}
