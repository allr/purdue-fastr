package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

public abstract class UnaryMinus extends BaseR {
    @Stable RNode lhs;

    UnaryMinus(ASTNode ast, RNode lhs) {
        super(ast);
        this.lhs = updateParent(lhs);
    }

    @Override
    public final Object execute(RContext context, Frame frame) {
        RAny value = (RAny) lhs.execute(context, frame);
        return execute(context, value);
    }

    abstract RAny execute(RContext context, RAny value);

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
            abstract RAny minus(RAny value) throws UnexpectedResultException;
        }

        private static RDouble forDouble(RDouble dvalue) throws UnexpectedResultException {
            if (dvalue.size() != 1) {
                throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
            }
            double d = dvalue.getDouble(0);
            if (RDouble.RDoubleUtils.isNA(d)) {
                return RDouble.BOXED_NA;
            } else {
                return RDouble.RDoubleFactory.getScalar(-d);
            }
        }

        private static RInt forInt(RInt ivalue) throws UnexpectedResultException {
            if (ivalue.size() != 1) {
                throw new UnexpectedResultException(Failure.NOT_ONE_ELEMENT);
            }
            int i = ivalue.getInt(0);
            if (i == RInt.NA) {
                return RInt.BOXED_NA;
            } else {
                return RInt.RIntFactory.getScalar(-i);
            }
        }

        public Specialized createSimple(RAny valueTemplate) {
            if (valueTemplate instanceof RDouble) {
                Minus minus = new Minus() {
                    @Override
                    RAny minus(RAny value) throws UnexpectedResultException {
                        if (!(value instanceof RDouble)) {
                            throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                        }
                        return forDouble((RDouble) value);
                    }
                };
                return new Specialized(ast, lhs, minus, "NumericScalar<Double>");
            }
            if (valueTemplate instanceof RInt) {
                Minus minus = new Minus() {
                    @Override
                    RAny minus(RAny value) throws UnexpectedResultException {
                        if (!(value instanceof RInt)) {
                            throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
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
                RAny minus(RAny value) throws UnexpectedResultException {
                    if (value instanceof RDouble) {
                        return forDouble((RDouble) value);
                    }
                    if (value instanceof RInt) {
                        return forInt((RInt) value);
                    }
                    if (value instanceof RLogical) {
                        return forInt(value.asInt());
                    }
                    throw new UnexpectedResultException(Failure.UNEXPECTED_TYPE);
                }
            };
            return new Specialized(ast, lhs, minus, "NumericScalar<Generic>");
        }

        @Override
        public RAny execute(RContext context, RAny value) {
            Specialized sn = createSimple(value);
            if (sn != null) {
                replace(sn, "specialize Scalar");
                return sn.execute(context, value);
            } else {
                sn = createGeneric();
                replace(sn, "specialize Scalar");
                return sn.execute(context, value);
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
            public RAny execute(RContext context, RAny value) {
                try {
                    return minus.minus(value);
                } catch (UnexpectedResultException e) {
                    Failure f = (Failure) e.getResult();
                    if (f == Failure.UNEXPECTED_TYPE) {
                        Specialized sn = createGeneric();
                        replace(sn, "install Scalar.Generic from Scalar.Simple" + dbg);
                        return sn.execute(context, value);
                    } else {
                        GenericMinus n = new GenericMinus(ast, lhs);
                        replace(n, "install GenericMinus from Scalar" + dbg);
                        return n.execute(context, value);
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
        RAny execute(RContext context, RAny value) {

            if (value instanceof RDouble) {
                final RDouble dvalue = (RDouble) value;
                final int vsize = dvalue.size();
                if (vsize == 0) {
                    throw RError.getInvalidArgTypeUnary(ast);
                }
                return new View.RDoubleView() {
                    @Override
                    public int size() {
                        return vsize;
                    }

                    @Override
                    public double getDouble(int i) {
                        double d = dvalue.getDouble(i);
                        if (RDouble.RDoubleUtils.isNA(d)) {
                            return RDouble.NA;
                        } else {
                            return -d;
                        }
                    }

                    @Override
                    public boolean isShared() {
                        return dvalue.isShared();
                    }

                    @Override
                    public void ref() {
                        dvalue.ref();
                    }
                };
            }
            if (value instanceof RInt || value instanceof RLogical) {
                final RInt ivalue = value.asInt();
                final int vsize = ivalue.size();
                if (vsize == 0) {
                    throw RError.getInvalidArgTypeUnary(ast);
                }
                return new View.RIntView() {
                    @Override
                    public int size() {
                        return vsize;
                    }

                    @Override
                    public int getInt(int i) {
                        int v = ivalue.getInt(i);
                        if (v == RInt.NA) {
                            return RInt.NA;
                        } else {
                            return -v;
                        }
                    }

                    @Override
                    public boolean isShared() {
                        return ivalue.isShared();
                    }

                    @Override
                    public void ref() {
                        ivalue.ref();
                    }
                };
            }
            Utils.nyi("unsupported type");
            return null;
        }
    }
}
