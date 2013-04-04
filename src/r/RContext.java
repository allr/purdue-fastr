package r;

import java.util.*;

import com.oracle.truffle.api.*;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class RContext {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", false);
    private static boolean debuggingFormat = false;
    private static boolean usesTruffleOptimizer = Truffle.getRuntime().equals("Default Truffle Runtime");
    private static ManageError errorManager = new ManageError(System.err);
    private static Truffleize truffleize = new Truffleize();
    private static final int NCONNECTIONS = 128;
    private static final Connection[] connections = new Connection[NCONNECTIONS];

    static {
        Arrays.fill(connections, null);
    }

    public static boolean usesTruffleOptimizer() {
        return usesTruffleOptimizer;
    }

    public static boolean debuggingFormat() {
        return debuggingFormat;
    }

    public static boolean debuggingFormat(boolean useDebuggingFormat) {
        boolean previous = debuggingFormat;
        debuggingFormat = useDebuggingFormat;
        return previous;
    }

    public static RAny eval(ASTNode expr, boolean useDebuggingFormat) {
        debuggingFormat(useDebuggingFormat);
        return eval(expr);
        // NOTE: cannot reset to the original value of debuggingFormat here, because usually the pretty printer is 
        // invoked on the results afterwards by the caller of eval; the pretty printer still depends on the correct
        // setting of debugging format
    }

    public static RAny eval(ASTNode expr) {
        try {
            return (RAny) truffleize.createLazyRootTree(expr).execute(null); // null means top-level
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            error(e); // throws an error
        }
        throw new Error("Never reached");
    }

    public static RNode createNode(ASTNode expr) {
        return truffleize.createTree(expr);
    }

    public static void warning(ASTNode expr, String msg) {
        errorManager.warning(expr, msg);
    }

    public static void warning(RError err) {
        errorManager.warning(err);
    }

    public static void error(ASTNode expr, String msg) {
        errorManager.error(expr, msg);
    }

    public static void error(RError err) {
        errorManager.error(err);
    }

    public static int allocateConnection(Connection connection) {
        for (int i = 0; i < NCONNECTIONS; i++) {
            if (connections[i] == null) {
                connections[i] = connection;
                return i;
            }
        }
        return -1;
    }

    /** Release a connection currently in use. */
    public static void freeConnection(int i) {
        assert Utils.check(connections[i] != null);
        connections[i] = null;
    }

    /** Return a connection or null. */
    public static Connection getConnection(int i) {
        return i >= 0 && i < NCONNECTIONS ? connections[i] : null;
    }

    public static boolean hasGNUR() {
        try {
            System.loadLibrary("gnurglue");
        } catch (Throwable t) {
            return false;
        }
        return true;
    }
}
