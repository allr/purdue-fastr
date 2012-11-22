package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: implements only part of R semantics

public class Which {

    private static final String[] paramNames = new String[]{"x", "arr.ind", "useNames"};
    private static final int IX = 0;
    private static final int IARR_IND = 1;
    private static final int IUSE_NAMES = 2;

    // a version with extra allocation (this is also how GNU R does it)
    // indeed could be also done with two passes but no extra allocation
    // FIXME: could get additional performance by providing multiple nodes and rewriting based on feedback on arguments
    public static RInt which(RLogical l) {
        RLogical input = l.materialize();
        int size = input.size();
        int[] tmp = new int[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (input.getLogical(i) == RLogical.TRUE) {
                tmp[j++] = i + 1;
            }
        }
        int nsize = j;
        int[] content = new int[nsize];
        System.arraycopy(tmp, 0, content, 0, nsize);
        return RInt.RIntFactory.getFor(content); // drops dimensions
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (provided[IARR_IND] || provided[IUSE_NAMES]) {
                Utils.nyi("arguments not yet implemented");
            }

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }

            if (names.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                        if (arg instanceof RLogical) {
                            return which((RLogical) arg);
                        } else {
                            throw RError.getArgumentWhichNotLogical(ast);
                        }
                    }
                };
            }
            Utils.nyi();
            return null;
        }
    };
}

