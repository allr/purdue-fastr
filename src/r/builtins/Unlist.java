package r.builtins;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// TODO: finish this, now only handles a list of scalar strings
public class Unlist {
    private static final String[] paramNames = new String[] {"x", "recursive", "use.names"};

    private static final int IX = 0;
    private static final int IRECURSIVE = 1;
    private static final int IUSE_NAMES = 2;

    public static boolean parseLogical(RAny arg) {
        // FIXME: most likely not exactly R semantics with evaluation of additional values
        RLogical larg = arg.asLogical();
        int size = larg.size();
        if (size > 0) {
            int v = larg.getLogical(0);
            if (v == RLogical.TRUE) {
                return true;
            }
        }
        return false;
    }

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }

            return new BuiltIn(call, names, exprs) {
                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] args) {
                    RAny x = args[paramPositions[IX]];
                    boolean recursive = provided[IRECURSIVE] ? parseLogical(args[paramPositions[IRECURSIVE]]) : true;
                    boolean useNames = provided[IUSE_NAMES] ? parseLogical(args[paramPositions[IUSE_NAMES]]) : true;

                    return genericUnlist(x, recursive, useNames, ast);
                }
            };
        }
    };

    public static RAny genericUnlist(RAny x, boolean recursive, boolean useNames, ASTNode ast) {
        if (x instanceof RList) {
            return genericUnlist((RList) x, recursive, useNames);
        }
        if (x instanceof RArray || x instanceof RNull) {
            return x;
        }
        throw RError.getArgumentNotList(ast);
    }

    public static RAny genericUnlist(RList x, boolean recursive, boolean useNames) {

        RAny res = speculativeUnlist(x, recursive, useNames);
        if (res != null) {
            return res;
        }
        Utils.nyi("unsupported case");
        return null;
    }

    // speculate all elements are scalars of the same (selected array) type
    public static RAny speculativeUnlist(RList x, boolean recursive, boolean useNames) {

        int xsize = x.size();
        if (xsize == 0) {
            return RNull.getNull();
        }
        RAny xfirst = x.getRAny(0);
        RArray.Names names = x.names();

        if (xfirst instanceof ScalarStringImpl) {
            String[] content = new String[xsize];
            for (int i = 0; i < xsize; i++) {
                RAny elem = x.getRAny(i);
                if (elem instanceof ScalarStringImpl) {
                    content[i] = ((ScalarStringImpl) elem).getString();
                } else {
                    return null;
                }
            }
            return RString.RStringFactory.getFor(content, null, useNames ? names : null);
        }
        return null; // no speculative result
    }
}
