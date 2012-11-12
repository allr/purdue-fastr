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

                    final int size = typedArg.size();
                    final RDouble orig = typedArg;

                    return new View.RDoubleView() {

                        @Override
                        public int size() {
                            return size;
                        }

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

                        @Override
                        public boolean isSharedReal() {
                            return orig.isShared();
                        }

                        @Override
                        public void ref() {
                            orig.ref();
                        }
                    };
                }

            };
        }
    };
}
