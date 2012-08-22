package r;

import com.oracle.truffle.compiler.*;
import com.oracle.truffle.debug.*;
import com.oracle.truffle.runtime.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;

public class RContext implements Context {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    ManageError errorManager;

    RContext global;

    RContext() {
        init();
    }

    public RAny eval(ASTNode expr) {
        try {
            return expr.execute(global, topLevel());
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            } else {
                error(e);
            }
        }
        return RNull.getNull(); // this is not quite correct, since R doesn't print anything here
        // Solutions: Maybe a black hole type could be used here
        // : Set a flag in the context to say nothing to print
        // ... dunno ...
    }

    private void init() {
        errorManager = new ManageError(System.err);
    }

    public void close() {

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
        return null;
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
}
