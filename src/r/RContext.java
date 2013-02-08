package r;

import com.oracle.truffle.api.*;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class RContext {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    public static RContext instance;

    private static RContext currentContext;

    private boolean debuggingFormat;
    private boolean usesOptimizer;

    ManageError errorManager;
    Truffleize truffleize;

    public RContext(boolean debuggingFormat) {
        init();
        this.debuggingFormat = debuggingFormat;
        instance = this; // FIXME: get rid of this
    }

    public boolean usesTruffleOptimizer() {
       return usesOptimizer;
    }

    public static boolean debuggingFormat() {
        return currentContext.debuggingFormat;
    }

    public RAny eval(ASTNode expr) {
        currentContext = this;
        try {
            return (RAny) truffleize.createLazyRootTree(expr).execute(this, null);
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            error(e);
        }
        currentContext = null;
        return RNull.getNull(); // this is not quite correct, since R doesn't print anything here
        // Solutions: Maybe a black hole type could be used here
        // : Set a flag in the context to say nothing to print
        // ... dunno ...
    }

    private void init() {
        usesOptimizer = Truffle.getRuntime().equals("Default Truffle Runtime");
        errorManager = new ManageError(System.err);
        truffleize = new Truffleize();
    }

    public RNode createNode(ASTNode expr) {
        return truffleize.createTree(expr);
    }

    public void warning(ASTNode expr, String msg) {
        if (errorManager != null) {
            errorManager.warning(expr, msg);
        }
    }

    public void warning(RError err) {
        if (errorManager != null) {
            errorManager.warning(err);
        }
    }

    public void error(ASTNode expr, String msg) {
        if (errorManager != null) {
            errorManager.error(expr, msg);
        }
    }

    public void error(RError err) {
        if (errorManager != null) {
            errorManager.error(err);
        }
    }

    public static final int NCONNECTIONS = 128;
    public static final Connection[] connections = new Connection[128];

    public int allocateConnection(Connection connection) {
        for (int i = 0; i < NCONNECTIONS; i++) {
            if (connections[i] == null) {
                connections[i] = connection;
                return i;
            }
        }
        return -1;
    }

    public void freeConnection(int i) {
        Utils.check(connections[i] != null);
        connections[i] = null;
    }

    public Connection getConnection(int i) {
        if (i >= 0 && i < NCONNECTIONS) {
            return connections[i];
        } else {
            return null;
        }
    }
}
