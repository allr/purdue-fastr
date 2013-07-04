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
     * makes sure to inject copy() methods and copy constructors to all nodes (that is decendants of
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

    /** JavaAssist translator that performs the augmentation of all nodes to be deep copyable.
     */
    static class ClassPatcher implements Translator {

        private static final String NODE_BASE = "r.nodes.truffle.RNode";
        //private static final String NODE_BASE = "r.Node";

        private CtClass nodeBase;


        @Override
        public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
            println("ClassPatcher is ENABLED");
            nodeBase = pool.get(NODE_BASE);
        }

        @Override
        public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
            CtClass cls = pool.get(classname);
            if (isNode(cls)) {
                println("loading "+cls.getName());
                if (! hasCopyMethod(cls)) {
                    if (! hasCopyConstructor(cls))
                      injectCopyConstructor(cls);
                    CtMethod m = CtNewMethod.make("public "+NODE_BASE+" deepCopy() { return new "+cls.getName()+"(this); }",cls);
                    m.setModifiers(Modifier.PUBLIC);
                    cls.addMethod(m);
                }
            }
        }

        protected final boolean isNode(CtClass cls) throws NotFoundException {
            return cls.subclassOf(nodeBase);
        }

        protected boolean hasCopyMethod(CtClass cls) {
            try {
                cls.getDeclaredMethod("deepCopy");
                return true;
            } catch (NotFoundException e) {
                return false;
            }
        }

        protected boolean hasCopyConstructor(CtClass cls) {
            try {
                cls.getDeclaredConstructor(new CtClass[] { cls });
                return true;
            } catch (NotFoundException e) {
                return false;
            }
        }

        protected final void loadSuperclasses(CtClass cls) throws NotFoundException, ClassNotFoundException {
            cls = cls.getSuperclass();
            while (cls != null) {
                cl.loadClass(cls.getName());
                cls = cls.getSuperclass();
            }
        }

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
                            // if the array holds nodes, create a new array, fill it in and adopt all the children by
                            // the new node
                            cl.loadClass(compType.getName());
                            if (isNode(compType)) {
                                code = "if ($1.FNAME == null) {\n" +
                                        "    FNAME = null;\n" +
                                        "} else {\n" +
                                        "    FNAME = new COMPTYPENAME[$1.FNAME.length];\n" +
                                        "    for (int i = 0; i < FNAME.length; ++i)\n" +
                                        "        FNAME[i] = $1.FNAME[i] == null ? null : (COMPTYPENAME) adoptChild($1.FNAME[i].copy());\n" +
                                        "}";
                                code = code.replace("COMPTYPENAME",compType.getName());
                            } else {
                                code = "if ($1.FNAME == null)\n" +
                                        "    FNAME = null;\n" +
                                        "else\n" +
                                        "    FNAME = (FTYPENAME) $1.FNAME.clone();\n";
                            }
                        }

                    } else {
                        // it is a single object. TODO should change to obey a deepcopyable interface or something
                        // at the moment nodes are deep copied and adopted, everything else is just ref copied
                        cl.loadClass(ftypeName);
                        if (isNode(ftype))
                            code = "if ($1.FNAME == null)\n" +
                                    "    FNAME = null;\n" +
                                    "else\n" +
                                    "    FNAME = (FTYPENAME) adoptChild($1.FNAME.copy());\n";
                        else
                            code = "FNAME = $1.FNAME;\n";
                    }
                    sb.append(code.replace("FNAME",fname).replace("FTYPENAME", ftypeName));
                }
                sb.append("}\n");
                System.out.println(cls.getName()+" >> " +sb.toString());
                try {
                    CtConstructor c = CtNewConstructor.make(new CtClass[] { cls }, null, sb.toString(), cls);
                    cls.addConstructor(c);
                } catch (CannotCompileException e) {
                    System.out.println(sb.toString());
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
