package r.builtins;

import java.util.*;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Console;
import r.Convert;
import r.Convert.ConversionStatus;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.RComplex.Complex;
import r.data.internal.*;
import r.data.internal.Connection.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: a very incomplete implementation, the full method has 21 parameters
// note also that some of the current implementation will likely have to be rewritten for the full set of features
public class Scan {

    private static final String[] paramNames = new String[]{"file", "what", "nmax", "quiet"};

    private static final int IFILE = 0;
    private static final int IWHAT = 1;
    private static final int INMAX = 2;
    private static final int IQUIET = 3;

    public static int parseNMax(RAny arg, RContext context, ASTNode ast) {
        RInt narg = Convert.coerceToIntWarning(arg, context, ast);
        if (narg.size() >= 1) {
            return narg.getInt(0);
        }
        return RInt.NA;
    }

    public static boolean parseQuiet(RAny arg) {
        RLogical narg = arg.asLogical();
        if (narg.size() >= 1) {
            return narg.getLogical(0) == RLogical.TRUE;
        }
        return false;
    }

    private static ConversionStatus cs = new ConversionStatus();

    public static RString scanString(ArrayList<String> src, RContext context, ASTNode ast) {
        int size = src.size();
        String[] content = new String[size];
        return RString.RStringFactory.getFor(src.toArray(content));
    }

    public static RComplex scanComplex(ArrayList<String> src, RContext context, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        double[] content = new double[2 * size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            Complex c = Convert.string2complex(str);
            if (cs.naIntroduced) {
                throw RError.getScanUnexpected(ast, "a complex", str);
            }
            content[2 * i] = c.realValue();
            content[2 * i + 1] = c.imagValue();
        }
        return RComplex.RComplexFactory.getFor(content);
    }

    public static RDouble scanDouble(ArrayList<String> src, RContext context, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        double[] content = new double[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            if (cs.naIntroduced) {
                throw RError.getScanUnexpected(ast, "a double", str);
            }
            content[i] = Convert.string2double(str, cs);
        }
        return RDouble.RDoubleFactory.getFor(content);
    }

    public static RInt scanInt(ArrayList<String> src, RContext context, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        int[] content = new int[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            if (cs.naIntroduced) {
                throw RError.getScanUnexpected(ast, "an integer", str);
            }
            content[i] = Convert.string2int(str, cs);
        }
        return RInt.RIntFactory.getFor(content);
    }

    public static RLogical scanLogical(ArrayList<String> src, RContext context, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        int[] content = new int[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            if (cs.naIntroduced) {
                throw RError.getScanUnexpected(ast, "a logical", str);
            }
            content[i] = Convert.string2logical(str, cs);
        }
        return RLogical.RLogicalFactory.getFor(content);
    }

    public static RRaw scanRaw(ArrayList<String> src, RContext context, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        cs.outOfRange = false;
        byte[] content = new byte[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            if (cs.naIntroduced || cs.outOfRange) {
                throw RError.getScanUnexpected(ast, "an raw", str);
            }
            content[i] = Convert.string2raw(str, cs);
        }
        return RRaw.RRawFactory.getFor(content);
    }


    public static RAny scan(ArrayList<String> src, RContext context, ASTNode ast, RAny what) {
        if (what instanceof RString) {
            return scanString(src, context, ast);
        }
        if (what instanceof RDouble) {
            return scanDouble(src, context, ast);
        }
        if (what instanceof RInt) {
            return scanInt(src, context, ast);
        }
        if (what instanceof RLogical) {
            return scanLogical(src, context, ast);
        }
        if (what instanceof RRaw) {
            return scanRaw(src, context, ast);
        }
        if (what instanceof RComplex) {
            return scanComplex(src, context, ast);
        }
        Utils.nyi("unsupported type");
        return null;
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            final ConnectionMode defaultMode = ConnectionMode.get("r");

            return new BuiltIn(call, names, exprs) {
                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    RAny what = provided[IWHAT] ? args[paramPositions[IWHAT]] : RDouble.EMPTY;
                    int nmax = provided[INMAX] ? parseNMax(args[paramPositions[INMAX]], context, ast) : -1;
                    boolean quiet = provided[IQUIET] ? parseQuiet(args[paramPositions[IQUIET]]) : false;

                    if (what instanceof RList) {
                        Utils.nyi("list not yet implemented");
                    }
                    if (what instanceof RNull) {
                        throw RError.getInvalidArgument(ast, paramNames[IWHAT]);
                    }
                    Connection con = null;
                    boolean wasOpen = false;

                    if (!provided[IFILE]) { // FIXME: similar code to that in readLines, extract?
                        Utils.nyi("stdin, text");
                        return null;
                    } else {
                        RAny conArg = args[paramPositions[IFILE]];
                        if (conArg instanceof RString) {
                            String description = OpenConnection.getScalarString(conArg, context, ast, paramNames[IFILE]);
                            con = FileConnection.createOpened(description, defaultMode, ast);
                        } else if (conArg instanceof RInt) {
                            // FIXME: check if it is a connection once attributes are implemented
                            RInt iarg = (RInt) conArg;
                            if (iarg.size() != 1) {
                                throw RError.getNotConnection(ast, paramNames[IFILE]);
                            }
                            int handle = iarg.getInt(0);
                            con = context.getConnection(handle);
                            Utils.check(con != null);
                            if (con.isOpen()) {
                                ConnectionMode mode = con.currentMode();
                                if (!mode.read()) {
                                   throw RError.getCannotReadConnection(ast);
                                }
                                wasOpen = true;
                            } else {
                                con.open(defaultMode, ast);
                            }
                        }
                    }

                    try {
                        Scanner scanner = new Scanner(con.reader(ast));
                        ArrayList<String> buf = new ArrayList<String>();
                        int nread = 0;
                        while (scanner.hasNext()) {
                            buf.add(scanner.next());
                            nread++;
                            if (nread == nmax) {
                                break;
                            }
                        }
                        if (!quiet) {
                            Console.println(String.format("Read %d item%s.", nread, nread == 1 ? "" : "s"));
                        }
                        return scan(buf, context, ast, what);
                    } finally {
                        if (!wasOpen) {
                            con.close(ast);
                        }
                    }
                }
            };
        }
    };
}
