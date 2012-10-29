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

    public static String catElement(RArray v, int i) {
        if (v instanceof RDouble) {
            return catElement((RDouble) v, i);
        }
        if (v instanceof RInt) {
            return catElement((RInt) v, i);
        }
        if (v instanceof RLogical) {
            return catElement((RLogical) v, i);
        }
        if (v instanceof RString) {
            return catElement((RString) v, i);
        }
        Utils.nyi("unsupported type");
        return null;
    }

    public static String catElement(RDouble v, int i) {
        double d = v.getDouble(i);
        if (RDouble.RDoubleUtils.isNA(d)) {
            return "NA";
        } else {
            return (new Double(d)).toString();
        }
    }

    public static String catElement(RInt v, int i) {
        int n = v.getInt(i);
        if (n == RInt.NA) {
            return "NA";
        } else {
            return (new Integer(n)).toString();
        }
    }

    public static String catElement(RLogical v, int i) {
        int n = v.getLogical(i);
        if (n == RLogical.NA) {
            return "NA";
        } else {
            if (n == RLogical.TRUE) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
    }

    public static String catElement(RString v, int i) {
        return v.getString(i);
    }

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
        boolean lastWasNull = false;
        for (int i = 0; i < args.length; i++) {
            if (i == sepArgPos) {
                continue;
            }
            if (i > 0 && !lastWasNull) {
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
                lastWasNull = true;
                continue;
            }
            lastWasNull = false;
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
                    System.out.print(catElement(va, j));
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

