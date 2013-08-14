package r;

import javassist.*;
import r.analysis.codegen.FastrLoader;

import java.lang.reflect.*;

/** fastR main class.
 *
 * Injects the special loader that adds deep copy capabilities to the node hierarchies and then runs the r.Console. This
 * is the main class to be used with the analysis enabled.
 */
public class fastr {

    public static final boolean DEBUG_FLAG = false;

    public static final String MAIN_CLASS = "r.Console";

    public static void DEBUG(String s) {
        if (DEBUG_FLAG)
            System.out.println(s);
    }

    /** Injects the node patching class loader and then launches the r.Console main method. The patched class loader
     * makes sure to inject copy() methods and copy constructors to all nodes (that is descendants of
     * truffle.api.Nodes.Node.
     */
    public static void main(String[] args) {
        try {
            Loader cl = new FastrLoader();
            Class c = cl.loadClass(MAIN_CLASS);
            Method m = c.getMethod("main",String[].class);
            m.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
