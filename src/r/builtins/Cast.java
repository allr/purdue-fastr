package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't handle BuiltIn1
public class Cast {

    public static RAny genericAsInt(ASTNode ast, RAny arg) {
        if (arg instanceof RList) {
            RList l = (RList) arg;
            int size = l.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                RAny v = l.getRAny(i);
                RArray a = (RArray) v;
                int asize = a.size();

                if (asize == 1) {
                    if (a instanceof RList) {
                        content[i] = RInt.NA;
                    } else {
                        content[i] = Convert.scalar2int(a);
                    }
                } else {
                    if (asize > 1 || a instanceof RList) {
                        throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "integer"));
                    }
                    // asize == 0
                    content[i] = RInt.NA;
                }
            }
            return RInt.RIntFactory.getForArray(content);
        }
        return arg.asInt();
    }
    public static final CallFactory INT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        return RInt.EMPTY;
                    }

                };
            }
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    return genericAsInt(ast, arg);
                }
            };
        }
    };

}
