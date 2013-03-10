package r.builtins;

import java.io.*;
import java.util.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.FileConnection;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "readLines"
 * 
 * <pre>
 * con -- a connection object or a character string.
 * n -- integer. The (maximal) number of lines to read. Negative values indicate that one should read up to the end of input 
 * on the connection.
 * ok -- logical. Is it OK to reach the end of the connection before n > 0 lines are read? If not, an error will be generated.
 * warn -- logical. Warn if a text file is missing a final EOL.
 * encoding -- encoding to be assumed for input strings. It is used to mark character strings as known to be in Latin-1 or 
 * UTF-8: it is not used to re-encode the input. To do the latter, specify the encoding as part of the connection con or via 
 * options(encoding=)
 * </pre>
 */
// TODO: this is incomplete implementation of R semantics
final class ReadLines extends CallFactory {
    static final CallFactory _ = new ReadLines("readLines", new String[]{"con", "n", "ok", "warn", "encoding"}, new String[]{});

    private ReadLines(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        final ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("encoding")) { throw Utils.nyi(); }
        final ConnectionMode defaultMode = ConnectionMode.get("rt");
        return new BuiltIn(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                final int n = !ia.provided("n") ? -1 : parseN(args[ia.position("n")], ast);
                final boolean ok = !ia.provided("ok") ? true : parseLogicalScalar(args[ia.position("ok")], ast, "ok");
                final boolean warn = !ia.provided("warn") ? true : parseLogicalScalar(args[ia.position("warn")], ast, "warn");
                Connection con = null;
                boolean wasOpen = false;
                if (!ia.provided("con")) { throw Utils.nyi("stdin"); } // FIXME: this is common code, extract? (e.g. also in scan)
                RAny conArg = args[ia.position("con")];
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
                        if (!mode.read()) { throw RError.getCannotReadConnection(ast); }
                        Utils.check(mode.text()); // TODO: GNU-R seems to be happily reading from binary connections, too
                        wasOpen = true;
                    } else {
                        con.open(defaultMode, ast);
                    }
                }

                try {
                    BufferedReader reader = con.reader(ast);
                    ArrayList<String> buf = new ArrayList<>();
                    String line = "";
                    int nlines = 0;
                    try {
                        while ((line = reader.readLine()) != null) {
                            buf.add(line);
                            nlines++;
                            if (nlines == n) {
                                break;
                            }
                        }
                    } catch (IOException e) {
                        throw RError.getGenericError(ast, e.toString());
                    }
                    if (!ok && nlines < n) { throw RError.getTooFewLinesReadLines(ast); }
                    String[] content = new String[buf.size()];
                    buf.toArray(content);
                    if (nlines > 0 && content[nlines - 1].length() != 0) {
                        // TODO detect when the file does not end with a newline, this cannot be done using readLine
                        // context.warning(ast, String.format(RError.INCOMPLETE_FINAL_LINE, con.description()));
                        // TODO: push-back of incomplete line with non-blocking connections
                    }
                    return RString.RStringFactory.getFor(content);
                } finally {
                    if (!wasOpen) {
                        con.close(ast);
                    }
                }
            }
        };
    }

    public static int parseN(RAny arg, ASTNode ast) { // FIXME: not exactly R semantics, R would ignore non-coerceable values at indexes 2 and higher
        RInt narg = Convert.coerceToIntWarning(arg, ast);
        if (narg.size() >= 1) {
            int n = narg.getInt(0);
            if (n != RInt.NA) { return n; }
        }
        throw RError.getInvalidArgument(ast, "n");
    }

    public static boolean parseLogicalScalar(RAny arg, ASTNode ast, String argName) { // FIXME: not exactly R semantics, R would ignore non-coerceable values at indexes 2 and higher
        RLogical larg = arg.asLogical();
        if (larg.size() >= 1) {
            int l = larg.getLogical(0);
            if (l == RLogical.TRUE) { return true; }
            if (l == RLogical.FALSE) { return false; }
        }
        throw RError.getInvalidArgument(ast, argName);
    }

}
