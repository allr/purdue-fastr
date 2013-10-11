package r.nodes.exec;

import r.data.*;
import r.data.internal.*;
import r.data.internal.View.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

// FIXME: scalar tests using instanceof

public abstract class UnaryMinus extends BaseR {
    @Child RNode lhs;

    UnaryMinus(ASTNode ast, RNode lhs) {
        super(ast);
        this.lhs = adoptChild(lhs);
    }

    @Override
    public final Object execute(Frame frame) {
        RAny value = (RAny) lhs.execute(frame);
        return execute(value);
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (lhs == oldNode) {
            lhs = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    abstract RAny execute(RAny value);

    enum Failure {
        NOT_ONE_ELEMENT,
        UNEXPECTED_TYPE,
    }

    // when the argument is a numeric scalar
    public static class NumericScalar extends UnaryMinus {
        public NumericScalar(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        abstract class Minus {
            abstract RAny minus(RAny value) throws SpecializationException;
        }

        private static RComplex forComplex(RComplex cvalue) throws SpecializationException {
            if (cvalue.size() != 1) {
                throw new SpecializationException(Failure.NOT_ONE_ELEMENT);
            }
            double real = cvalue.getReal(0);
            double imag = cvalue.getImag(0);
            double nreal;
            double nimag;
            if (!RDouble.RDoubleUtils.isNAorNaN(real)) {
                nreal = -real;
            } else {
                nreal = real;
            }
            if (!RDouble.RDoubleUtils.isNAorNaN(imag)) {
                nimag = -imag;
            } else {
                nimag = imag;
            }
            return RComplex.RComplexFactory.getScalar(nreal, nimag);
        }


        private static RDouble forDouble(RDouble dvalue) throws SpecializationException {
            if (dvalue.size() != 1) {
                throw new SpecializationException(Failure.NOT_ONE_ELEMENT);
            }
            double d = dvalue.getDouble(0);
            if (RDouble.RDoubleUtils.arithIsNA(d)) {
                return RDouble.BOXED_NA;
            } else {
                return RDouble.RDoubleFactory.getScalar(-d);
            }
        }

        private static RInt forInt(RInt ivalue) throws SpecializationException {
            if (ivalue.size() != 1) {
                throw new SpecializationException(Failure.NOT_ONE_ELEMENT);
            }
            int i = ivalue.getInt(0);
            return RInt.RIntFactory.getScalar(-i); // NOTE: this also works for NA
        }

        public Specialized createSimple(RAny valueTemplate) {
            if (valueTemplate instanceof RComplex) {
                Minus minus = new Minus() {
                    @Override
                    RAny minus(RAny value) throws SpecializationException {
                        if (!(value instanceof RComplex)) {
                            throw new SpecializationException(Failure.UNEXPECTED_TYPE);
                        }
                        return forComplex((RComplex) value);
                    }
                };
                return new Specialized(ast, lhs, minus, "NumericScalar<Double>");
            }
            if (valueTemplate instanceof RDouble) {
                Minus minus = new Minus() {
                    @Override
                    RAny minus(RAny value) throws SpecializationException {
                        if (!(value instanceof RDouble)) {
                            throw new SpecializationException(Failure.UNEXPECTED_TYPE);
                        }
                        return forDouble((RDouble) value);
                    }
                };
                return new Specialized(ast, lhs, minus, "NumericScalar<Double>");
            }
            if (valueTemplate instanceof RInt) {
                Minus minus = new Minus() {
                    @Override
                    RAny minus(RAny value) throws SpecializationException {
                        if (!(value instanceof RInt)) {
                            throw new SpecializationException(Failure.UNEXPECTED_TYPE);
                        }
                        return forInt((RInt) value);
                    }
                };
                return new Specialized(ast, lhs, minus, "NumericScalar<Double>");
            }
            // note: unary minus for logical should not be common
            return null;
        }

        public Specialized createGeneric() {
            Minus minus = new Minus() {
                @Override
                RAny minus(RAny value) throws SpecializationException {
                    if (value instanceof RComplex) {
                        return forComplex((RComplex) value);
                    }
                    if (value instanceof RDouble) {
                        return forDouble((RDouble) value);
                    }
                    if (value instanceof RInt) {
                        return forInt((RInt) value);
                    }
                    if (value instanceof RLogical) {
                        return forInt(value.asInt());
                    }
                    throw RError.getInvalidArgTypeUnary(ast);
                }
            };
            return new Specialized(ast, lhs, minus, "NumericScalar<Generic>");
        }

        @Override
        public RAny execute(RAny value) {
            Specialized sn = createSimple(value);
            if (sn != null) {
                replace(sn, "specialize Scalar");
                return sn.execute(value);
            } else {
                sn = createGeneric();
                replace(sn, "specialize Scalar");
                return sn.execute(value);
            }
        }

        class Specialized extends NumericScalar {
            final Minus minus;
            final String dbg;

            Specialized(ASTNode ast, RNode lhs, Minus minus, String dbg) {
                super(ast, lhs);
                this.minus = minus;
                this.dbg = dbg;
            }

            @Override
            public RAny execute(RAny value) {
                try {
                    return minus.minus(value);
                } catch (SpecializationException e) {
                    Failure f = (Failure) e.getResult();
                    if (f == Failure.UNEXPECTED_TYPE) {
                        Specialized sn = createGeneric();
                        replace(sn, "install Scalar.Generic from Scalar.Simple" + dbg);
                        return sn.execute(value);
                    } else {
                        GenericMinus n = new GenericMinus(ast, lhs);
                        replace(n, "install GenericMinus from Scalar" + dbg);
                        return n.execute(value);
                    }
                }
            }
        }
    }

    public static class GenericMinus extends UnaryMinus {
        public GenericMinus(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        @Override
        RAny execute(RAny value) {

            if (value instanceof RComplex) {
                final RComplex cvalue = (RComplex) value;
                return TracingView.ViewTrace.trace(new View.RComplexProxy<RComplex>(cvalue) {

                    @Override
                    public double getReal(int i) {
                        double d = cvalue.getReal(i);
                        if (!RDouble.RDoubleUtils.isNAorNaN(d)) {
                            return -d;
                        } else {
                            return d;
                        }
                    }

                    @Override
                    public double getImag(int i) {
                        double d = cvalue.getImag(i);
                        if (!RDouble.RDoubleUtils.isNAorNaN(d)) {
                            return -d;
                        } else {
                            return d;
                        }
                    }
                });
            }
            if (value instanceof RDouble) {
                final RDouble dvalue = (RDouble) value;
                return TracingView.ViewTrace.trace(new View.RDoubleProxy<RDouble>(dvalue) {

                    @Override
                    public double getDouble(int i) {
                        double d = dvalue.getDouble(i);
                        if (RDouble.RDoubleUtils.arithIsNA(d)) {
                            return RDouble.NA;
                        } else {
                            return -d;
                        }
                    }
                });
            }
            if (value instanceof RInt || value instanceof RLogical) {
                final RInt ivalue = value.asInt();
                return TracingView.ViewTrace.trace(new View.RIntProxy<RInt>(ivalue) {

                    @Override
                    public int getInt(int i) {
                        int v = orig.getInt(i);
                        return -v; // NOTE: this also works for NA
                    }

                    @Override
                    public void materializeInto(int[] resContent) {
                        if (orig instanceof IntImpl) {
                            uminus(orig.getContent(), resContent);
                        } else if (orig instanceof RIntView) {
                            ((RIntView) orig).materializeInto(resContent);
                            uminus(resContent, resContent);
                        } else  {
                            super.materializeInto(resContent);
                        }
                    }

                    @Override
                    public void materializeIntoOnTheFly(int[] resContent) {
                        if (orig instanceof IntImpl) {
                            uminus(orig.getContent(), resContent);
                        } else  {
                            super.materializeInto(resContent);
                        }
                    }
                });
            }
            throw RError.getInvalidArgTypeUnary(ast);
        }
    }

    public static void uminus(int[] x, int[] res) {
        for (int i = 0; i < x.length; i++) {
            res[i] = -x[i];  // NOTE: this also works for NA
        }
    }
}
