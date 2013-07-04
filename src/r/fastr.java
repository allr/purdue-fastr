package r;

import javassist.*;
import javassist.Modifier;

import java.lang.reflect.*;

public class fastr {

    public static final boolean DEBUG = true;

    public static final String MAIN_CLASS = "r.Console";
    //public static final String MAIN_CLASS = "r.FastrHelper";

    public static void println(String s) {
        if (DEBUG)
            System.out.println(s);
    }

    static Loader cl;

    /** Injects the node patching class loader and then launches the r.Console main method. The patched class loader
     * makes sure to inject copy() methods and copy constructors to all nodes (that is descendants of
     * truffle.api.Nodes.Node.
     */
    public static void main(String[] args) {
        try {
            ClassPool pool = ClassPool.getDefault();
            cl = new Loader(pool);
            cl.addTranslator(pool, new ClassPatcher());
            Class c = cl.loadClass(MAIN_CLASS);
            Method m = c.getMethod("main",String[].class);
            m.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
    }

    /** JavaAssist translator that performs the augmentation of all nodes to be deep copyable. Any subclass of RNode is
     * checked to implement the deepCopy() method. If such implementation is found, the class is not altered, otherwise
     * the class is injected with a copy constructor correctly deep copying its child nodes and other DeepCopyable
     * objects and a deepCopy method returning the copy of the object.
     */
    static class ClassPatcher implements Translator {

        private static final String DEEP_COPYABLE = "r.DeepCopyable";
        private static final String NODE_BASE = "r.nodes.truffle.RNode";
        //private static final String NODE_BASE = "r.Node";

        private CtClass nodeBase;
        private CtClass deepCopyInterface;


        @Override
        public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
            println("ClassPatcher is ENABLED");
            nodeBase = pool.get(NODE_BASE);
            deepCopyInterface = pool.get(DEEP_COPYABLE);
        }


        /** When a class is loaded, checks that the class is subclass of RNode and if so, makes sure that the deepCopy
         * method is present. If not, autogenerates the copy constructor and the deepCopy method for the class.
         */
        @Override
        public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
            CtClass cls = pool.get(classname);
            if (isNode(cls)) {
                println("loading node "+cls.getName());
                if (! hasCopyMethod(cls)) {
                    if (! hasCopyConstructor(cls))
                        injectCopyConstructor(cls);
                    CtMethod m = CtNewMethod.make("public "+NODE_BASE+" deepCopy() { return new "+cls.getName()+"(this); }",cls);
                    m.setModifiers(Modifier.PUBLIC);
                    cls.addMethod(m);
                } else {
                    println("    already contains deepCopy() method");
                }
            }
        }

        /** Returns true, if the given class is a subclass of the RNode, that is if a copy constructor and deepCopy
         * methods should be checked and implemented if missing.
         */
        protected final boolean isNode(CtClass cls) throws NotFoundException {
            return cls.subclassOf(nodeBase);
        }

        /** Returns true, if the deepCopy method is present in the given class. \
         */
        protected boolean hasCopyMethod(CtClass cls) {
            try {
                cls.getDeclaredMethod("deepCopy");
                return true;
            } catch (NotFoundException e) {
                return false;
            }
        }

        /** Returns true if the class has a copy constructor. We do not care about the visibility of the constructor as
         * even private would do (called only from the deepCopy method).
         */
        protected boolean hasCopyConstructor(CtClass cls) {
            try {
                cls.getDeclaredConstructor(new CtClass[] { cls });
                return true;
            } catch (NotFoundException e) {
                return false;
            }
        }

        /** For a given class loads all its superclasses. This is important to revert class loading to be able to deal
         * with anonymous classes.
         */
        protected final void loadSuperclasses(CtClass cls) throws NotFoundException, ClassNotFoundException {
            cls = cls.getSuperclass();
            while (cls != null) {
                cl.loadClass(cls.getName());
                cls = cls.getSuperclass();
            }
        }

        /** Injects a copy constructor to the given class.
         *
         * It is assumed the class is child of RNode. All its fields will be initialized in the copy constructor. If the
         * field is instance of RNode, it is deep copied and then adopted as a child, any field that implements the
         * interface DeepCopyable will be deep copied and then assigned. Arrays are always cloned, or deepcopied if they
         * hold DeepCopyable objects, or deep copied and adopted as children if they are RNode instances.
         */
        protected void injectCopyConstructor(CtClass cls) throws NotFoundException, CannotCompileException {
            try {
                loadSuperclasses(cls);
                CtField[] fields = cls.getDeclaredFields();
                StringBuilder sb = new StringBuilder("{\n");
                sb.append("super($1);\n");
                for (CtField field : fields) {
                    CtClass ftype = field.getType();
                    String fname = field.getName();
                    String ftypeName = ftype.getName();
                    // now based on different types, build the copy constructor
                    String code;
                    if (ftype.isPrimitive()) {
                        // primitives are simple, just copy them
                        code = "FNAME = $1.FNAME;\n";
                    } else if (ftype.isArray()) {
                        // for arrays check if they hold nodes or deep copyable objects and use the appropriate method
                        // to copy them to the newly created object
                        CtClass compType = ftype.getComponentType();
                        if (compType.isPrimitive()) {
                            // if the arrays are primitive, just copy them using the array's clone method
                            code = "if ($1.FNAME == null)\n" +
                                    "    FNAME = null;\n" +
                                    "else\n" +
                                    "    FNAME = (FTYPENAME) $1.FNAME.clone();\n";
                        } else {
                            // it is an array of objects
                            cl.loadClass(compType.getName());
                            if (isNode(compType)) {
                                // if it is array of nodes, deep copy each one and adopt it as a child
                                code = "if ($1.FNAME == null) {\n" +
                                        "    FNAME = null;\n" +
                                        "} else {\n" +
                                        "    FNAME = new COMPTYPENAME[$1.FNAME.length];\n" +
                                        "    for (int i = 0; i < FNAME.length; ++i)\n" +
                                        "        FNAME[i] = $1.FNAME[i] == null ? null : (COMPTYPENAME) adoptChild($1.FNAME[i].deepCopy());\n" +
                                        "}";
                                code = code.replace("COMPTYPENAME",compType.getName());
                            } else if (compType.subclassOf(deepCopyInterface)) {
                                // if the object implements DeepCopyable (and is not node for which deepCopy() methods
                                // are autogenerated, use its deepcopy method
                                code = "if ($1.FNAME == null) {\n" +
                                        "    FNAME = null;\n" +
                                        "} else {\n" +
                                        "    FNAME = new COMPTYPENAME[$1.FNAME.length];\n" +
                                        "    for (int i = 0; i < FNAME.length; ++i)\n" +
                                        "        if ($1.FNAME[i] == null)\n" +
                                        "            FNAME[i] = null;\n" +
                                        "        else\n" +
                                        "            FNAME[i] = (COMPTYPENAME) $1.FNAME[i].deepCopy();\n" +
                                        "}";
                                code = code.replace("COMPTYPENAME",compType.getName());
                            } else {
                                // for arrays of objects, just shallow copy the array itself.
                                code = "if ($1.FNAME == null)\n" +
                                        "    FNAME = null;\n" +
                                        "else\n" +
                                        "    FNAME = (FTYPENAME) $1.FNAME.clone();\n";
                            }
                        }

                    } else {
                        // it is a single object, first load its type
                        cl.loadClass(ftypeName);
                        if (isNode(ftype))
                            // if it is a node, adopt its deepcopy, if the node is not null
                            code = "if ($1.FNAME == null)\n" +
                                    "    FNAME = null;\n" +
                                    "else\n" +
                                    "    FNAME = (FTYPENAME) adoptChild($1.FNAME.deepCopy());\n";
                        else if (ftype.subclassOf(deepCopyInterface))
                            // if it is not a node, but implements DeepCopyable, store a deep copy if not null.
                            code = "if ($1.FNAME == null)\n" +
                                    "    FNAME = null;\n" +
                                    "else\n" +
                                    "    FNAME = (FTYPENAME) $1.FNAME.deepCopy();\n";
                        else
                            // otherwise just get the same object as it can be shared
                            code = "FNAME = $1.FNAME;\n";
                    }
                    sb.append(code.replace("FNAME",fname).replace("FTYPENAME", ftypeName));
                }
                sb.append("}\n");
                try {
                    CtConstructor c = CtNewConstructor.make(new CtClass[] { cls }, null, sb.toString(), cls);
                    cls.addConstructor(c);
                } catch (CannotCompileException e) {
                    // TODO meaningful error here - automatic generation does not compile
                    println(sb.toString());
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                // TODO meaningful error here - user code error
                e.printStackTrace();
            }
        }
    }

}
