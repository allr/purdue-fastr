package r.builtins;

import r.data.*;
import r.data.internal.*;
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

    RDouble calc(final ASTNode ast, final RDouble value) {
        final int size = value.size();
        if (size == 1) {
            return RDouble.RDoubleFactory.getScalar(op(ast, value.getDouble(0)), value.dimensions(), value.names(), value.attributesRef());
        } else if (size > 0) {
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
            });
        }
        return RDouble.EMPTY;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny value) {
                if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) { return calc(ast, value.asDouble()); }
                throw RError.getNonNumericMath(ast);
            }

        };
    }
}
