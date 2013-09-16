package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.PipeConnection;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

/**
 * "pipe"
 * 
 * <pre>
 * description -- character string. A description of the connection
 * open -- character. A description of how to open the connection (if it should be opened initially).
 * encoding -- The name of the encoding to be used.
 * </pre>
 */
final class Pipe extends CallFactory {

    static final CallFactory _ = new Pipe("pipe", new String[]{"description", "open", "encoding"}, new String[]{"description"});

    private Pipe(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posDescription = ia.position("description");
        final int posOpen = ia.position("open");
        if (ia.provided("encoding")) { throw Utils.nyi(); }
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                String description = posDescription != -1 ? File.getScalarString(args[posDescription], ast, "description") : "";
                String open = posOpen != -1 ? File.getScalarString(args[posOpen], ast, "open") : "";
                return OPEN_PIPE.open(description, open, ast);
            }
        };
    }

    static final File.OpenConnection OPEN_PIPE = new File.OpenConnection() {
        @Override public Connection createUnopened(String description, ConnectionMode defaultMode) {
            return PipeConnection.createUnopened(description, defaultMode);
        }

        @Override public Connection createOpened(String description, ConnectionMode mode, ASTNode ast) {
            return PipeConnection.createOpened(description, mode, ast);
        }

    };
}
