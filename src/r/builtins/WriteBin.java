package r.builtins;

import java.io.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// FIXME: very incomplete implementation of R semantics

public class WriteBin {
    private static final String[] paramNames = new String[]{"object", "con", "size", "endian", "useBytes"};

    private static final int IOBJECT = 0;
    private static final int ICON = 1;
    private static final int ISIZE = 2;
    private static final int IENDIAN = 3;
    private static final int IUSE_BYTES = 4;

    public static void write(RRaw arg, OutputStream output, ASTNode ast) throws IOException {
        int size = arg.size();
        for (int i = 0; i < size; i++) {
            output.write(arg.getRaw(i));
        }
    }

    public static void write(RAny arg, OutputStream output, ASTNode ast) throws IOException {
        if (arg instanceof RRaw) {
            write((RRaw) arg, output, ast);
            return;
        }
        // FIXME support more types
        Utils.nyi("unsupported argument");
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (provided[ISIZE] || provided[IENDIAN] || provided[IUSE_BYTES]) {
                Utils.nyi("argument of writeBin not yet implemented");
            }
            if (!provided[IOBJECT]) {
                BuiltIn.missingArg(call, paramNames[IOBJECT]);
            }
            if (!provided[ICON]) {
                BuiltIn.missingArg(call, paramNames[ICON]);
            }

            final ConnectionMode defaultMode = ConnectionMode.get("wb");

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] args) {

                    Connection con = null;
                    boolean wasOpen = false;

                    RAny conArg = args[paramPositions[ICON]];
                    if (conArg instanceof RString) {
                        String description = OpenConnection.getScalarString(conArg, ast, "description");
                        con = FileConnection.createOpened(description, defaultMode, ast);
                    } else if (conArg instanceof RInt) {
                        // FIXME: check if it is a connection once attributes are implemented
                        RInt iarg = (RInt) conArg;
                        if (iarg.size() != 1) {
                            throw RError.getNotConnection(ast, paramNames[ICON]);
                        }
                        int handle = iarg.getInt(0);
                        con = RContext.getConnection(handle);
                        Utils.check(con != null);
                        if (con.isOpen()) {
                            ConnectionMode mode = con.currentMode();
                            if (!mode.binary()) {
                                throw RError.getWriteOnlyBinary(ast);
                            }
                            if (!mode.write()) {
                               throw RError.getCannotWriteConnection(ast);
                            }
                            wasOpen = true;
                        } else {
                            con.open(defaultMode, ast);
                        }
                    }

                    try {
                        BufferedOutputStream output = new BufferedOutputStream(con.output(ast));
                        try {
                            write(args[paramPositions[IOBJECT]], output, ast);
                            output.flush(); // FIXME: this flushes also the underlying file, which may not be the R semantics (?)
                        } catch (IOException e) {
                            throw RError.getGenericError(ast, e.toString());
                        }
                        return RNull.getNull();
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
