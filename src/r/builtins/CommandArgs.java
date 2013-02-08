package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

public class CommandArgs {

    private static final String[] paramNames = new String[]{"trailingOnly"};

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            if (names.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(Frame frame) {
                        return RString.RStringFactory.getFor(Console.trailingArgs);
                    }
                };
            }

            BuiltIn.ensureArgName(call, paramNames[0], names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(Frame frame, RAny x) {
                    RLogical l;
                    if (x instanceof RLogical) {
                        l = (RLogical) x;
                    } else {
                        l = x.asLogical();
                    }
                    int size = l.size();
                    if (size == 0) {
                        throw RError.getLengthZero(ast);
                    }
                    if (size > 1) {
                        RContext.warning(ast, RError.LENGTH_GT_1);
                    }
                    int trailingOnly = l.getLogical(0);
                    if (trailingOnly == RLogical.TRUE) {
                        return RString.RStringFactory.getArray(Console.trailingArgs);
                    } else if (trailingOnly == RLogical.FALSE) {
                        return RString.RStringFactory.getArray(Console.commandArgs);
                    } else {
                        throw RError.getUnexpectedNA(ast); // not always the same error message as with GNU-R
                    }
                }
            };
        }
    };
}
