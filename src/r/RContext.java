package r;

import com.oracle.truffle.compiler.*;
import com.oracle.truffle.debug.*;
import com.oracle.truffle.runtime.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class RContext implements Context {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    ManageError errorManager;

    Truffleize truffleize;

    RContext() {
        init();
    }

    public RAny eval(ASTNode expr) {
        try {
            return (RAny) truffleize.createTree(expr).execute(this, topLevel());
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            } else {
                error(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
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

    public RFrame topLevel() {
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
