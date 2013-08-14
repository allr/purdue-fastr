package r;

import javassist.*;
import javassist.Modifier;

import java.lang.reflect.*;
import java.util.*;

/** fastR main class.
 *
 * Injects the special loader that adds deep copy capabilities to the node hierarchies and then runs the r.Console. This
 * is the main class to be used with the analysis enabled.
 */
public class fastr {

    public static final boolean DEBUG = true;

    public static final String MAIN_CLASS = "r.Console";
    //public static final String MAIN_CLASS = "r.FastrHelper";

    public static void println(String s) {
        if (DEBUG)
            System.out.println(s);
    }

    static Loader cl;
    static ClassPool pool;

    /** Injects the node patching class loader and then launches the r.Console main method. The patched class loader
     * makes sure to inject copy() methods and copy constructors to all nodes (that is descendants of
     * truffle.api.Nodes.Node.
     */
    public static void main(String[] args) {
        try {
            pool = ClassPool.getDefault();
            cl = new Loader(pool);
            cl.addTranslator(pool, new ClassPatcher());
            Class c = cl.loadClass(MAIN_CLASS);
            Method m = c.getMethod("main",String[].class);
            m.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
    }

    static class ClassPatcher implements Translator {

        private static final String NODE_BASE = "r.nodes.truffle.RNode";

        private CtClass nodeBase;


        @Override
        public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
            println("ClassPatcher is ENABLED");
            nodeBase = pool.get(NODE_BASE);
        }


        /** When a class is loaded, checks that the class is subclass of RNode and if so, makes sure that the deepCopy
         * method is present. If not, autogenerates the copy constructor and the deepCopy method for the class.
         */
        @Override
        public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
            CtClass cls = pool.get(classname);
            if (isNode(cls)) {
                println("loading node "+cls.getName());
            }
        }

        /** Returns true, if the given class is a subclass of the RNode, that is if a copy constructor and deepCopy
         * methods should be checked and implemented if missing.
         */
        protected final boolean isNode(CtClass cls) throws NotFoundException {
            return cls.subclassOf(nodeBase);
        }

    }
}
