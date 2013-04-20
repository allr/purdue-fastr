package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.View;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

// FIXME: scalar optimizations
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
                return new View.RDoubleProxy<RDouble>(typedArg) {
                    @Override public double getDouble(int i) {
                        double d = orig.getDouble(i);
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
                };
            }
        };
    }
}
