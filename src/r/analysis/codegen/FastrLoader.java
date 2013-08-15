package r.analysis.codegen;

import javassist.*;

import static r.fastr.DEBUG;

/** Code generating class loader that augments truffle nodes with analysis visitors and deep copying.
 *
 */
public class FastrLoader extends Loader implements Translator {

    private static final boolean REPORT_DEEPCOPYABLE_CLASS = true;
    private static final boolean REPORT_EXISTING_DEEPCOPY = true;
    private static final boolean REPORT_EXISTING_COPY_OR_MOVE_CONSTRUCTOR = true;

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

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        DEBUG("Patcher is ENABLED");
    }


    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass cls = pool.get(classname);
        if (implementsDeepCopyable(cls)) {
            if (REPORT_DEEPCOPYABLE_CLASS)
                DEBUG("loading deep copyable class " + cls.getName());
            if (!hasDeepOrShallowConstructor(cls)) {
                addDeepOrShallowConstructor(cls);
            } else {
                if (REPORT_EXISTING_COPY_OR_MOVE_CONSTRUCTOR)
                    DEBUG("  copy or move constructor is already defined, skipping...");
            }
            if (!hasDeepCopyMethod(cls)) {
                addDeepCopyMethod(cls);
            } else {
                if (REPORT_EXISTING_DEEPCOPY)
                    DEBUG("  deepCopy() method is already defined, skipping...");
            }
        }
    }

    private boolean implementsDeepCopyable(CtClass cls) throws NotFoundException {
        return !cls.isInterface() && isDeepCopyable(cls);
    }

    private boolean isDeepCopyable(CtClass cls) throws NotFoundException {
        return cls.subtypeOf(pool.get("r.analysis.codegen.DeepCopyable"));
    }

    private boolean isNode(CtClass cls) throws NotFoundException {
        return cls.subclassOf(pool.get("com.oracle.truffle.api.nodes.Node"));
    }

    private boolean hasDeepOrShallowConstructor(CtClass cls) {
        try {
            cls.getDeclaredConstructor(new CtClass[] { cls, pool.get("boolean") });
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private boolean hasDeepCopyMethod(CtClass cls) {
        try {
            cls.getDeclaredMethod("deepCopy");
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /** For a given class loads all its superclasses. This is important to revert class loading to be able to deal
     * with anonymous classes.
     */
    private final void loadSuperclasses(CtClass cls) throws NotFoundException, ClassNotFoundException {
        cls = cls.getSuperclass();
        while (cls != null) {
            loadClass(cls.getName());
            cls = cls.getSuperclass();
        }
    }

    private void addDeepOrShallowConstructor(CtClass cls) throws NotFoundException {
        try {
            loadSuperclasses(cls);
            CtField[] fields = cls.getDeclaredFields();
            StringBuilder sb = new StringBuilder("{\n");
            sb.append("super($1, $2);\n");
            for (CtField field : fields) {
                CtClass ftype = field.getType();
                String fname = field.getName();
                String ftypeName = ftype.getName();
                // now based on different types, build the copy constructor
                String code;
                if (ftype.isPrimitive() || field.hasAnnotation(Shared.class)) {
                    // primitives are simple, just copy them, do so for the shared fields
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
                                "    FNAME = $2 ? (FTYPENAME) $1.FNAME.clone(): $1.FNAME;\n";
                    } else {
                        // it is an array of objects
                        loadClass(compType.getName());
                        if (isNode(compType)) {
                            // if it is array of nodes, deep copy each one and adopt it as a child
                            code = "if ($1.FNAME == null) {" +
                                    "  FNAME = null;" +
                                    "} else if ($2) {" +
                                    "    FNAME = new COMPTYPENAME[$1.FNAME.length];\n" +
                                    "    for (int i = 0; i < FNAME.length; ++i)\n" +
                                    "        FNAME[i] = $1.FNAME[i] == null ? null : (COMPTYPENAME) adoptChild((COMPTYPENAME)$1.FNAME[i].deepCopy());\n" +
                                    "} else {" +
                                    "    FNAME = $1.FNAME;\n" +
                                    "    adoptChildren($1.FNAME);\n" +
                                    "}";
                            code = code.replace("COMPTYPENAME",compType.getName());
                        } else if (isDeepCopyable(compType)) {
                            // if the object implements DeepCopyable (and is not node for which deepCopy() methods
                            // are autogenerated, use its deepcopy method
                            code = "if ($1.FNAME == null) {\n" +
                                    "    FNAME = null;\n" +
                                    "} else if ($2) {\n" +
                                    "    FNAME = new COMPTYPENAME[$1.FNAME.length];\n" +
                                    "    for (int i = 0; i < FNAME.length; ++i)\n" +
                                    "        if ($1.FNAME[i] == null)\n" +
                                    "            FNAME[i] = null;\n" +
                                    "        else\n" +
                                    "            FNAME[i] = (COMPTYPENAME) $1.FNAME[i].deepCopy();\n" +
                                    "} else {\n" +
                                    "    FNAME = $1.FNAME;\n" +
                                    "}";
                            code = code.replace("COMPTYPENAME",compType.getName());
                        } else {
                            // for arrays of objects, just shallow copy the array itself.
                            code = "if ($1.FNAME == null)\n" +
                                    "    FNAME = null;\n" +
                                    "else\n" +
                                    "    FNAME = $2 ? (FTYPENAME) $1.FNAME.clone() : $1.FNAME;\n";
                        }
                    }
                } else {
                    // it is a single object, first load its type
                    loadClass(ftypeName);
                    if (isNode(ftype))
                        // if it is a node, adopt its deepcopy, if the node is not null
                        code = "if ($1.FNAME == null)\n" +
                                "    FNAME = null;\n" +
                                "else\n" +
                                "    FNAME = (FTYPENAME) adoptChild($2 ?  (FTYPENAME) $1.FNAME.deepCopy() : $1.FNAME);\n";
                    else if (isDeepCopyable(ftype))
                        // if it is not a node, but implements DeepCopyable, store a deep copy if not null.
                        code = "if ($1.FNAME == null)\n" +
                                "    FNAME = null;\n" +
                                "else\n" +
                                "    FNAME = $2 ? (FTYPENAME) $1.FNAME.deepCopy() : $1.FNAME;\n";
                    else
                        // otherwise just get the same object as it can be shared
                        code = "FNAME = $1.FNAME;\n";
                }
                sb.append(code.replace("FNAME",fname).replace("FTYPENAME", ftypeName));
            }
            sb.append("}\n");
            try {
                CtConstructor c = CtNewConstructor.make(new CtClass[] { cls, pool.get("boolean") }, null, sb.toString(), cls);
                cls.addConstructor(c);
            } catch (CannotCompileException e) {
                // TODO meaningful error here - automatic generation does not compile
                System.err.println("Error during code generation for class "+cls.getName());
                System.err.println(sb.toString());
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            // TODO meaningful error here - user code error
            e.printStackTrace();
        }
    }

    private void addDeepCopyMethod(CtClass cls) throws CannotCompileException {
        // Javassist is not happy with covariant return types so deepCopy is returning the interface
        CtMethod m = CtNewMethod.make("public r.analysis.codegen.DeepCopyable deepCopy() { return new "+cls.getName()+"(this, true); }",cls);
        m.setModifiers(Modifier.PUBLIC);
        cls.addMethod(m);
    }

}

