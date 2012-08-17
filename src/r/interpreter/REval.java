package r.interpreter;

import r.*;
import r.data.*;
import r.nodes.*;

public class REval {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", true);

    RContext global;

    REval() {
        this(null);
    }

    REval(RContext sharedContext) {
        init(sharedContext);
    }

    public RAny eval(Node expr) {
        try {
            return expr.execute(global, null);
        } catch (RuntimeException e) {
            if (DEBUG) {
                e.printStackTrace();
            } else {
                System.err.println(e.getMessage());
            }
        }
        return RNull.getNull(); // this is not quite correct, since R doesn't print anything here
        // Solutions: Maybe a black hole type could be used here
        //          : Set a flag in the context to say nothing to print
        // ... dunno ...
    }

    private void init(RContext sharedContext) {
        global = sharedContext;
    }

    public void close() {

    }
}
