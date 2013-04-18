package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.FileConnection;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "file"
 * 
 * <pre>
 * description -- character string. A description of the connection
 * open -- character. A description of how to open the connection (if it should be opened initially). 
 * blocking --logical. 
 * encoding -- The name of the encoding to be used.
 * raw -- logical. If true, a raw interface is used which will be more suitable for arguments which are not regular
 *     files, e.g. character devices. This suppresses the check for a compressed file when opening for text-mode reading,
 * and asserts that the file may not be seekable
 */
final class File extends CallFactory {

    static final CallFactory _ = new File("file", new String[]{"description", "open", "blocking", "encoding", "raw"}, new String[]{});

    private File(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("blocking") || ia.provided("encoding") || ia.provided("raw")) { throw Utils.nyi(); }
        final int posDescription = ia.position("description");
        final int posOpen = ia.position("open");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                String description = posDescription != -1 ? getScalarString(args[posDescription], ast, "description") : "";
                String open = posOpen != -1 ? getScalarString(args[posOpen], ast, "open") : "";
                return OPEN_FILE.open(description, open, ast);
            }
        };
    }

    public static String getScalarString(RAny arg, ASTNode ast, String argName) {
        if (!(arg instanceof RString)) { throw RError.getInvalidArgument(ast, argName); }
        RString s = (RString) arg;
        if (s.size() == 1) { return s.getString(0); }
        throw RError.getInvalidArgument(ast, argName);
    }

    static final ConnectionMode defaultMode = ConnectionMode.get("rt");

    public abstract static class OpenConnection {
        public abstract Connection createUnopened(String description, ConnectionMode defaultMode);

        public abstract Connection createOpened(String description, ConnectionMode mode, ASTNode ast);

        public RInt open(String description, String open, ASTNode ast) {
            if (description.length() == 0) { throw Utils.nyi("temporary file"); }
            Connection con = null;
            if (open.length() == 0) {
                con = createUnopened(description, defaultMode);
            } else {
                ConnectionMode mode = ConnectionMode.get(open);
                if (mode == null) { throw RError.getInvalidArgument(ast, "open"); }// not exactly same as the GNU R error message
                con = createOpened(description, mode, ast);
            }
            int handle = RContext.allocateConnection(con);
            if (handle != -1) { return RInt.RIntFactory.getScalar(handle); }// TODO: set proper attributes, class            
            throw RError.getAllConnectionsInUse(ast);
        }
    }

    private static final OpenConnection OPEN_FILE = new OpenConnection() {
        @Override public Connection createUnopened(String description, ConnectionMode defaultMode) {
            return FileConnection.createUnopened(description, defaultMode);
        }

        @Override public Connection createOpened(String description, ConnectionMode mode, ASTNode ast) {
            return FileConnection.createOpened(description, mode, ast);
        }

    };
}
