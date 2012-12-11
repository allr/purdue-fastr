package r;

import com.oracle.truffle.compiler.*;
import com.oracle.truffle.debug.*;
import com.oracle.truffle.runtime.*;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class RContext implements Context {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    private final TruffleCompiler compiler;
    private static RContext currentContext;

    private boolean debuggingFormat;

    ManageError errorManager;
    Truffleize truffleize;
    static boolean truffleCompilerNotAvailable = false;

    public RContext(int compilerThreshold, boolean debuggingFormat) {
        init();
        this.debuggingFormat = debuggingFormat;

        TruffleCompiler cmp;
        if (!truffleCompilerNotAvailable) {
            // only check once per VM instance (would need a more elaborate check to make re-checking possible)
            try {
                cmp = new TruffleCompilerImpl(compilerThreshold);
            } catch (UnsatisfiedLinkError le) {
                System.err.println("Not using the Truffle compiler as it is not available.");
                truffleCompilerNotAvailable = true;
                cmp = null;
            }
        } else {
            cmp = null;
        }
        this.compiler = cmp;
    }

    RContext(int compilerThreshold) {
        this(compilerThreshold, false);
    }

    RContext(TruffleCompiler compiler) {
        init();
        this.compiler = compiler;
    }

    RContext() {
        this(null);
    }

    public static boolean debuggingFormat() {
        return currentContext.debuggingFormat;
    }

    public RAny eval(ASTNode expr) {
        currentContext = this;
        try {
            return (RAny) truffleize.createLazyRootTree(expr).execute(this, topLevel());
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
        errorManager = new ManageError(System.err);
        truffleize = new Truffleize();
    }

    public void close() {

    }

    public RNode createNode(ASTNode expr) {
        return truffleize.createTree(expr);
    }

    public Frame topLevel() {
        return null;
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

    @Override
    public TruffleCompiler getCompiler() {
        return compiler;
    }

    @Override
    public DebugInfoProvider getDebugInfoProvider() {
        return null;
    }

    @Override
    public void enter() {
    }

    @Override
    public void leave() {
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
