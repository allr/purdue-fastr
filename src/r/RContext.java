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

    private final TruffleCompiler compiler;

    ManageError errorManager;
    Truffleize truffleize;

    RContext(int compilerThreshold) {
        init();
        TruffleCompiler cmp;
        try {
            cmp = new TruffleCompilerImpl(compilerThreshold);
        } catch (UnsatisfiedLinkError le) {
            System.out.println("Not using the Truffle compiler as it is not available.");
            cmp = null;
        }
        this.compiler = cmp;
    }

    RContext(TruffleCompiler compiler) {
        init();
        this.compiler = compiler;
    }

    RContext() {
        this(null);
    }

    public RAny eval(ASTNode expr) {
        try {
            return (RAny) truffleize.createLazyRootTree(expr).execute(this, topLevel());
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
}
