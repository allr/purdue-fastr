package r.builtins;

import java.io.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

// FIXME: not quite the GNU-R output particularly for numbers; re-visit if some code could be removed once we have fully GNU-R string representation of numerics
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
        return Convert.pretty(Convert.double2string(d));
    }

    public static String catElement(RInt v, int i) {
        int n = v.getInt(i);
        return Convert.pretty(Convert.int2string(n));
    }

    public static String catElement(RLogical v, int i) {
        int n = v.getLogical(i);
        return Convert.pretty(Convert.logical2string(n));
    }

    public static String catElement(RString v, int i) {
        return v.getString(i);
    }

    public static void genericCat(PrintWriter out, RAny[] args, int sepArgPos, ASTNode ast) {

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
        int nprinted = 0;
        for (int i = 0; i < args.length; i++) {
            if (i == sepArgPos) {
                continue;
            }
            if (nprinted > 0 && !lastWasNull) {
                if (sep != null) {
                    out.print(sep.getString(si++));
                    if (si == ssize) {
                        si = 0;
                    }
                } else {
                    out.print(" ");
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
                            out.print(sep.getString(si++));
                            if (si == ssize) {
                                si = 0;
                            }
                        } else {
                            out.print(" ");
                        }
                    }
                    nprinted++;
                    out.print(catElement(va, j));
                }
            }
        }
        out.flush();
    }

    // speculates on that all arguments are strings and separator is an empty string
    // the empty separator is a usual thing in R programs
    // all args strings is inspired by fasta
    public static void catStrings(PrintWriter out, RAny[] args, int sepArgPos, ASTNode ast) throws UnexpectedResultException {
        if (sepArgPos == -1) {
            throw new UnexpectedResultException(null);
        }
        StringBuilder str = new StringBuilder();
        int argslen = args.length;
        for (int j = 0; j < argslen; j++) {
            RAny arg = args[j];
            if (!(arg instanceof RString)) {
                throw new UnexpectedResultException(null);
            }
            RString rs = (RString) arg;
            int size = rs.size();
            if (j != sepArgPos) {
                for (int i = 0; i < size; i++) {
                    str.append(rs.getString(i));
                }
            } else {
                if (size != 1 || rs.getString(0).length() > 0) {
                    throw new UnexpectedResultException(null);
                }
            }
        }
        out.append(str); // FIXME: this creates a copy of the string, internally
        out.flush();
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
            final PrintWriter stdOut = new PrintWriter(System.out, true); // stdout buffering, important for fasta
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                    // assume we are only printing strings and separator is an empty (single-element) string
                    try {
                        catStrings(stdOut, params, sepPosition, ast);
                    } catch (UnexpectedResultException e) {
                        RNode generic = new BuiltIn(ast, argNames, argExprs) {
                            @Override
                            public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                                genericCat(stdOut, params, sepPosition, ast);
                                return RNull.getNull();
                            }
                        };
                        replace(generic, "install Cat.Generic from Cat.Strings.NoSep");
                        genericCat(stdOut, params, sepPosition, ast);
                    }
                    return RNull.getNull();
                }

            };
        }
    };

}

