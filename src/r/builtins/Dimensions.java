package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Dimensions {

    private static final String[] paramNames = new String[]{"x"};
    private static final int IX = 0;

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final boolean[] provided = a.providedParams;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny x) {
                    if (!(x instanceof RArray)) {
                        return RNull.getNull();
                    }
                    RArray ax = (RArray) x;
                    int[] dim = ax.dimensions();
                    if (dim == null) {
                        return RNull.getNull();
                    }
                    return RInt.RIntFactory.getArray(dim);
                }
            };
        }
    };
 }
