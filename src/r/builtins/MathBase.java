package r.builtins;

import r.data.*;
import r.data.internal.*;
import r.data.internal.ProfilingView.ViewProfile;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// TODO: complex numbers
// TODO: nan produced warnings and checks (note - only some of the operations implemented by subclasses can produce an NaN)
abstract class MathBase extends CallFactory {

    MathBase(String name) {
        super(name, new String[]{"x"}, null);
    }

    abstract double op(ASTNode ast, double value);
    abstract void op(ASTNode ast, double[] x, double[] res);

    final RDouble calcEager(final ASTNode ast, final RDouble value) {
        double[] content = value.getContent();
        if (value.isTemporary()) {
            op(ast, content, content);
            return value;
        }
        int size = value.size();
        double[] res = new double[size];
        op(ast, content, res);
        return RDouble.RDoubleFactory.getFor(res, value.dimensions(), value.names(), value.attributes());
    }

    private RDouble createView(final ASTNode ast, final RDouble value, final int size) {
        return TracingView.ViewTrace.trace(new View.RDoubleProxy<RDouble>(value) {
            @Override public int size() {
                return size;
            }

            @Override public double getDouble(int i) {
                return op(ast, value.getDouble(i));
            }
            @Override
            public void materializeInto(double[] resContent) {
                if (orig instanceof DoubleImpl) {
                    op(ast, orig.getContent(), resContent);
                } else if (orig instanceof RDoubleView) {
                    ((RDoubleView) orig).materializeInto(resContent);
                    op(ast, resContent, resContent);
                } else  {
                    super.materializeInto(resContent);
                }
            }
            @Override
            public void materializeIntoOnTheFly(double[] resContent) {
                if (orig instanceof DoubleImpl) {
                    op(ast, orig.getContent(), resContent);
                } else  {
                    super.materializeIntoOnTheFly(resContent);
                }
            }
            @Override
            public void accept(ValueVisitor v) {
                v.visit(this);
            }
        });
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new MathBuiltin(call, names, exprs);
    }

    public class MathBuiltin extends Builtin.Builtin1 {

        public MathBuiltin(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            super(ast, names, exprs);
        }

        @Override
        public RAny doBuiltIn(Frame frame, RAny value) {
            if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) {
                return calcInitial(value.asDouble());
            }
            throw RError.getNonNumericMath(ast);
        }

        private ViewProfile profile;

        RDouble calcInitial(final RDouble value) {
            final int size = value.size();
            if (size == 1) {
                return RDouble.RDoubleFactory.getScalar(op(ast, value.getDouble(0)), value.dimensions(), value.names(), value.attributesRef());
            } else if (size > 0) {
                if (profile == null) {
                    profile = new ViewProfile();
                    return ProfilingView.ViewProfile.profile(createView(ast, value, size), profile);
                } else {
                    if (profile.shouldBeLazy()) {
                        return replaceAndExecuteLazy(value);
                    } else {
                        return replaceAndExecuteEager(value);
                    }
                }
            }
            return RDouble.EMPTY;
        }

        RDouble replaceAndExecuteLazy(final RDouble currentValue) {
            RNode newNode = new Builtin.Builtin1(ast, argNames, argExprs) {
                @Override
                public RAny doBuiltIn(Frame frame, RAny value) {
                    if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) {
                        RDouble doubleValue = value.asDouble();
                        return createView(ast, doubleValue, doubleValue.size());
                    }
                    throw RError.getNonNumericMath(ast);
                }
            };
            replace(newNode);
            return createView(ast, currentValue, currentValue.size());
        }

        RDouble replaceAndExecuteEager(final RDouble currentValue) {
            RNode newNode = new Builtin.Builtin1(ast, argNames, argExprs) {
                @Override
                public RAny doBuiltIn(Frame frame, RAny value) {
                    if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) {
                        return calcEager(ast, value.asDouble());
                    }
                    throw RError.getNonNumericMath(ast);
                }
            };
            replace(newNode);
            return calcEager(ast, currentValue);
        }

    }
}
