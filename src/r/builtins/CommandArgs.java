package r.builtins;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

public class CommandArgs {

    private static final String[] paramNames = new String[]{"trailingOnly"};

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            if (a.providedParams.length == 1) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, Frame frame) {
                        return RString.RStringFactory.getFor(Console.trailingArgs);
                    }
                };
            }

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny x) {
                    int trailingOnly;
                    if (x instanceof RLogical) {
                        trailingOnly = ((RLogical) x).getLogical(0);
                    } else {
                        trailingOnly = x.asLogical().getLogical(0);
                    }
                    if (trailingOnly == RLogical.TRUE) {
                        return RString.RStringFactory.getFor(Console.trailingArgs);
                    } else {
                        return RString.RStringFactory.getFor(Console.commandArgs);
                    }
                }
            };
        }
    };
}
