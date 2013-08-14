package r.analysis.codegen;

import javassist.*;

import static r.fastr.DEBUG;

/** Code generating class loader that augments truffle nodes with analysis visitors and deep copying.
 *
 */
public class FastrLoader extends Loader implements Translator {

    static ClassPool pool;

    public FastrLoader(ClassLoader loader) {
        super(loader, pool = ClassPool.getDefault());
        try {
            addTranslator(pool, this);
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
            System.err.println("Unable to instantiate the loader. Exitting.");
            System.exit(-1);
        }
    }

    public FastrLoader() {
        this(getSystemClassLoader());
    }

    private static final String NODE_BASE = "r.nodes.truffle.RNode";

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        DEBUG("Patcher is ENABLED");
    }


    /** When a class is loaded, checks that the class is subclass of RNode and if so, makes sure that the deepCopy
     * method is present. If not, autogenerates the copy constructor and the deepCopy method for the class.
     */
    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass cls = pool.get(classname);
        if (isNode(cls)) {
            DEBUG("loading node " + cls.getName());
        }
    }

    /** Returns true, if the given class is a subclass of the RNode, that is if a copy constructor and deepCopy
     * methods should be checked and implemented if missing.
     */
    protected final boolean isNode(CtClass cls) throws NotFoundException {
        return cls.subclassOf(pool.get(NODE_BASE));
    }

}

