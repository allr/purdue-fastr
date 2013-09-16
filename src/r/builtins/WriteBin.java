package r.builtins;

import java.io.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.FileConnection;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "writebin"
 * 
 * <pre>
 * con --A connection object or a character string naming a file or a raw vector.
 * size-- integer. The number of bytes per element in the byte stream. The default, NA_integer_, uses the natural size. Size changing is not supported for raw and complex vectors.
 * endian -- The endian-ness ("big" or "little" of the target system for the file. Using "swap" will force swapping endian-ness.
 * object -- An R object to be written to the connection.
 * 
 * <pre>
 */
//FIXME: implements only part of R semantics
final class WriteBin extends CallFactory {
    static final CallFactory _ = new WriteBin("writeBin", new String[]{"object", "con", "size", "endian", "useBytes"}, new String[]{"object", "con"});

    WriteBin(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static void write(RRaw arg, OutputStream output) throws IOException {
        int size = arg.size();
        for (int i = 0; i < size; i++) {
            output.write(arg.getRaw(i));
        }
    }

    public static void write(RAny arg, OutputStream output) throws IOException {
        if (arg instanceof RRaw) {
            write((RRaw) arg, output);
            return;
        }
        throw Utils.nyi("unsupported argument"); // FIXME support more types
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("size") || ia.provided("endian") || ia.provided("useBytes")) {
            Utils.nyi("argument of writeBin not yet implemented");
        }
        final ConnectionMode defaultMode = ConnectionMode.get("wb");
        final int posCon = ia.position("con");
        final int posObject = ia.position("object");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                Connection con = null;
                boolean wasOpen = false;
                RAny conArg = args[posCon];
                if (conArg instanceof RString) {
                    String description = File.getScalarString(conArg, ast, "description");
                    con = FileConnection.createOpened(description, defaultMode, ast);
                } else if (conArg instanceof RInt) {
                    // FIXME: check if it is a connection once attributes are implemented
                    RInt iarg = (RInt) conArg;
                    if (iarg.size() != 1) { throw RError.getNotConnection(ast, "con"); }
                    int handle = iarg.getInt(0);
                    con = RContext.getConnection(handle);
                    Utils.check(con != null);
                    if (con.isOpen()) {
                        ConnectionMode mode = con.currentMode();
                        if (!mode.binary()) { throw RError.getWriteOnlyBinary(ast); }
                        if (!mode.write()) { throw RError.getCannotWriteConnection(ast); }
                        wasOpen = true;
                    } else {
                        con.open(defaultMode, ast);
                    }
                }
                try {
                    BufferedOutputStream output = new BufferedOutputStream(con.output(ast));
                    try {
                        write(args[posObject], output);
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
}
