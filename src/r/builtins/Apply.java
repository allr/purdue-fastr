package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.FunctionCall;

// FIXME: only a subset of R functionality
public class Apply {
    private static final String[] paramNames = new String[]{"X", "FUN"};

    private static final int IX = 0;
    private static final int IFUN = 1;

    public static class ValueProvider extends BaseR {
        RAny value;

        public ValueProvider(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
            return value;
        }

        public void setValue(RAny value) {
            this.value = value;
        }
    }

    public static final CallFactory LAPPLY_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final int[] paramPositions = a.paramPositions;

            final ValueProvider firstArgProvider = new ValueProvider(call);

            // lapply will create a call node, let's prepare names and expressions (first is the variable)

            final int cnArgs = 1 + names.length - 2; // "-2" because both FUN and X are required
            final RSymbol[] cnNames = new RSymbol[cnArgs];
            final RNode[] cnExprs = new RNode[cnArgs];
            cnNames[0] = null;
            cnExprs[0] = firstArgProvider;
            int j = 0;
            for (int i = 0; i < names.length; i++) {
                if (paramPositions[IX] == i || paramPositions[IFUN] == i) {
                    continue;
                }
                cnNames[1 + j] = names[i];
                cnExprs[1 + j] = exprs[i];
                j++;
            }

            // FIXME: this won't allow calling builtins or giving function by name
            final ValueProvider closureProvider = new ValueProvider(null);
            final FunctionCall cnode = FunctionCall.getFunctionCall(call, closureProvider, cnNames, cnExprs);

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    RAny argx = args[paramPositions[IX]];
                    RAny argfun = args[paramPositions[IFUN]];

                    if (!(argfun instanceof RClosure)) {
                        // FIXME: add support for builtins, variables
                        throw RError.getNotFunction(ast);
                    }
                    RClosure closure = (RClosure) argfun;
                    closureProvider.setValue(closure);

                    if (argx instanceof RList) {
                        RList x = (RList) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.getRAny(i));
                            content[i] = (RAny) cnode.execute(context, frame);
                        }
                        return RList.RListFactory.getForArray(content);
                    }

                    if (argx instanceof RArray) {
                        RArray x = (RArray) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.boxedGet(i));
                            content[i] = (RAny) cnode.execute(context, frame);
                        }
                        return RList.RListFactory.getForArray(content);
                    }
                    return null;
                }
            };
        }
    };
}
