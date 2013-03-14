package r.builtins;

import com.oracle.truffle.api.frame.Frame;
import r.*;
import r.data.*;
import r.data.internal.View;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.Arithmetic;
import r.nodes.truffle.RNode;

/**
 * "exp"
 * 
 * <pre>
 * x -- a numeric or complex vector.
 * </pre>
 */
final class Exp extends CallFactory {
    static final CallFactory _ = new Exp("exp", new String[]{"x"}, new String[]{"x"});

    private Exp(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RDouble || arg instanceof RInt || arg instanceof RLogical) {
                    return new View.RDoubleProxy<RDouble>(arg.asDouble()) {
                        @Override public double getDouble(int i) {
                            double d = orig.getDouble(i);
                            if (RDouble.RDoubleUtils.isNAorNaN(d)) { return RDouble.NA; }
                            double res = Math.exp(d);
                            if (RDouble.RDoubleUtils.isNAorNaN(res)) {
                                RContext.warning(ast, RError.NAN_PRODUCED);
                            }
                            return res;
                        }
                    };
                } else if (arg instanceof RComplex) {
                    return Arithmetic.ComplexView.create(RComplex.BOXED_E, (RComplex) arg, Arithmetic.POW, ast);
                } else {
                    throw RError.getNonNumericMath(ast);
                }
            }
        };
    }
}
