package r.interpreter;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

public class REvaluator {

    ManageError errorManager;
    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    REvaluator global;

    REvaluator() {
        init();
    }

    public RAny eval(Node expr) {
        try {
            return expr.execute(global, null);
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            } else {
                error(e);
            }
        }
        return RNull.getNull(); // this is not quite correct, since R doesn't print anything here
        // Solutions: Maybe a black hole type could be used here
        //          : Set a flag in the context to say nothing to print
        // ... dunno ...
    }

    private void init() {
        errorManager = new ManageError(System.err);
    }

    public void close() {

    }

    public void warning(Node expr, String msg) {
        if (errorManager != null) {
            errorManager.warning(expr, msg);
        }
    }

    public void warning(RError err) {
        if (errorManager != null) {
            errorManager.warning(err);
        }
    }

    public void error(Node expr, String msg) {
        if (errorManager != null) {
            errorManager.error(expr, msg);
        }
    }

    public void error(RError err) {
        if (errorManager != null) {
            errorManager.error(err);
        }
    }
}
