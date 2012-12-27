package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.View;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

// FIXME: scalar optimizations
// FIXME: Truffle can't handle BuiltIn1
public class Sqrt {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(final RContext context, Frame frame, RAny arg) {
                    RDouble typedArg;

                    if (arg instanceof RDouble) {
                        typedArg = (RDouble) arg;
                    } else if (arg instanceof RInt || arg instanceof RLogical) {
                        typedArg = arg.asDouble();
                    } else {
                        throw RError.getNonNumericMath(ast);
                    }

                    return new View.RDoubleProxy<RDouble>(typedArg) {

                        @Override
                        public double getDouble(int i) {
                            double d = orig.getDouble(i);
                            if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                                return RDouble.NA;
                            } else {
                                double res = Math.sqrt(d);
                                if (RDouble.RDoubleUtils.isNAorNaN(res)) {
                                    context.warning(ast, RError.NAN_PRODUCED);
                                }
                                return res;
                            }
                        }
                    };
                }

            };
        }
    };
}
