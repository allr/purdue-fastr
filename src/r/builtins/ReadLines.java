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

import r.Truffle.*;

// TODO: this is incomplete implementation of R semantics
final class ReadLines extends CallFactory {
    static final CallFactory _ = new ReadLines("readLines", new String[]{"con", "n", "ok", "warn", "encoding"}, new String[]{});

    private ReadLines(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("encoding")) { throw Utils.nyi(); }
        final int posCon = ia.position("con");
        final int posN = ia.position("n");
        final int posOk = ia.position("ok");
        final int posWarn = ia.position("warn");
        final ConnectionMode defaultMode = ConnectionMode.get("rt");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                final int n = posN == -1 ? -1 : parseN(args[posN], ast);
                final boolean ok = posOk == -1 ? true : parseLogicalScalar(args[posOk], ast, "ok");
                @SuppressWarnings("unused")
                final boolean warn = posWarn == -1 ? true : parseLogicalScalar(args[posWarn], ast, "warn");
                Connection con = null;
                boolean wasOpen = false;
                if (posCon == -1) { throw Utils.nyi("stdin"); } // FIXME: this is common code, extract? (e.g. also in scan)
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
                        if (!mode.read()) { throw RError.getCannotReadConnection(ast); }
                        Utils.check(mode.text()); // TODO: GNU-R seems to be happily reading from binary connections, too
                        wasOpen = true;
                    } else {
                        con.open(defaultMode, ast);
                    }
                }

                try {
                    BufferedReader reader = con.reader(ast);
                    ArrayList<String> buf = new ArrayList();
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
