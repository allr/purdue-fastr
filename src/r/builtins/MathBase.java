package r.builtins;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// TODO: complex numbers
abstract class MathBase extends CallFactory {

    MathBase(String name, Operation op) {
        super(name, new String[]{"x"}, null);
        this.op = op;
    }

    public abstract static class Operation {
        public abstract double op(ASTNode ast, double value);
    }

    private final Operation op;

    public RDouble calc(final ASTNode ast, final RDouble value) {
        final int size = value.size();
        if (size == 1) {
            return RDouble.RDoubleFactory.getScalar(op.op(ast, value.getDouble(0)), value.dimensions(), value.names(), value.attributesRef());
        } else if (size > 0) { return new View.RDoubleProxy<RDouble>(value) {
            @Override public int size() {
                return size;
            }

            @Override public double getDouble(int i) {
                return op.op(ast, value.getDouble(i));
            }
        }; }
        return RDouble.EMPTY;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny value) {
                if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) { return calc(ast, value.asDouble()); }
                throw RError.getNonNumericMath(ast);
            }

        };
    }
}
