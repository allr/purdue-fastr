package r.builtins;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;


public class Cat {

    private static final String[] paramNames = new String[]{"...", "sep"};
    private static final int ISEP = 1;

    public static void cat(RAny[] args, int sepArgPos, ASTNode ast) {

        RString sep = null;
        int ssize = 0;

        if (sepArgPos != -1) {
            RAny v = args[sepArgPos];
            if (v instanceof RString) {
                sep = (RString) v;
                ssize = sep.size();
            } else {
                throw RError.getInvalidSep(ast);
            }
        }

        int si = 0;
        for (int i = 0; i < args.length; i++) {
            if (i == sepArgPos) {
                continue;
            }
            if (i > 0) {
                if (sep != null) {
                    System.out.print(sep.getString(si++));
                    if (si == ssize) {
                        si = 0;
                    }
                } else {
                    System.out.print(" ");
                }
            }
            RAny v = args[i];

            if (v instanceof RNull) {
                continue;
            }
            if (v instanceof RList) {
                throw RError.getGenericError(ast, String.format(RError.CAT_ARGUMENT_LIST, i + 1));
            }
            if (v instanceof RArray) {
                RArray va = (RArray) v;
                int vsize = va.size();
                for (int j = 0; j < vsize; j++) {
                    if (j > 0) {
                        if (sep != null) {
                            System.out.print(sep.getString(si++));
                            if (si == ssize) {
                                si = 0;
                            }
                        } else {
                            System.out.print(" ");
                        }
                    }
                    System.out.print(va.get(j).toString());
                }
            }
        }
    }

    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        return RNull.getNull();
                    }

                };
            }
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            final int sepPosition = provided[ISEP] ? paramPositions[ISEP] : -1;
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                    cat(params, sepPosition, ast);
                    return RNull.getNull();
                }
            };
        }
    };

}

