package r;

import java.util.*;

import org.netlib.blas.*;
import org.netlib.lapack.*;

import com.oracle.truffle.api.*;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class RContext {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    private static boolean debuggingFormat;
    private static boolean usesTruffleOptimizer;
    private static ManageError errorManager;
    private static Truffleize truffleize;

    private static final int NCONNECTIONS = 128;
    private static final Connection[] connections = new Connection[128];

    static {
        System.err.println("Using LAPACK: " + LAPACK.getInstance().getClass().getName());
        System.err.println("Using BLAS: " + BLAS.getInstance().getClass().getName());
        initialize(false);
    }

    public static void initialize(boolean useDebuggingFormat) {
        usesTruffleOptimizer = Truffle.getRuntime().equals("Default Truffle Runtime");
        errorManager = new ManageError(System.err);
        truffleize = new Truffleize();
        debuggingFormat = useDebuggingFormat;
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
        // NOTE: cannot reset to the original value of debuggingFormat here, because usually the pretty printer is invoked on the
        //  results afterwards by the caller of eval; the pretty printer still depends on the correct setting of debugging format
    }

    public static RAny eval(ASTNode expr) {
        try {
            return (RAny) truffleize.createLazyRootTree(expr).execute(null); // null means top-level
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            error(e);
        }
        return RNull.getNull();
        // F:this is not quite correct, since R doesn't print anything here
        //
        // Solutions: Maybe a black hole type could be used here
        // : Set a flag in the context to say nothing to print
        // ... dunno ...
    }

    public static RNode createNode(ASTNode expr) {
        return truffleize.createTree(expr);
    }

    public static void warning(ASTNode expr, String msg) {
        if (errorManager != null) {
            errorManager.warning(expr, msg);
        }
    }

    public static void warning(RError err) {
        if (errorManager != null) {
            errorManager.warning(err);
        }
    }

    public static void error(ASTNode expr, String msg) {
        if (errorManager != null) {
            errorManager.error(expr, msg);
        }
    }

    public static void error(RError err) {
        if (errorManager != null) {
            errorManager.error(err);
        }
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

    public static void freeConnection(int i) {
        assert Utils.check(connections[i] != null);
        connections[i] = null;
    }

    public static Connection getConnection(int i) {
        if (i >= 0 && i < NCONNECTIONS) {
            return connections[i];
        } else {
            return null;
        }
    }
}
