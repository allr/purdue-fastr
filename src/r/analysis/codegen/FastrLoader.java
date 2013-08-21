package r.analysis.codegen;

import javassist.*;
import r.analysis.codegen.annotations.*;

import java.util.*;

import static r.fastr.DEBUG;

/** Code generating class loader.
 *
 * For the purposes of the analysis, this loader provides code generation facilities for relevant classes. The code is
 * generated at runtime using javassist from source code, so the startup code will suffer. An optimization will be to
 * use javassist bytecode facilities. Since the generated code is very simple and repetitive, this should be hard, but
 * I am keeping the source code version for the time being as modifications are far easier.
 *
 * When executing fastr, using fastr as main class will always correctly initialize the loader. However, when executing
 * the JUnit tests, it is important to specify the loader manually by specifying the VM argument:
 *
 * -Djava.system.class.loader=r.analysis.codegen.FastrLoader
 *
 * The following are the code augmentations provided:
 *
 * Node deep copying
 * -----------------
 *
 * Each class implementing DeepCopyable interface is extended with its own implementation of deep or shallow constructor
 * and a deepCopy() method returning a deeply copied object. Since RNode implements DeepCopyable itself, this applies
 * notably to all truffle nodes.
 *
 * If the class contains the deep or shallow constructor and the deepCopy method, it will not be augmented.
 *
 * The deep or shallow constructor is a copying constructor with additional boolean argument determining whether the
 * copy should be deep, or shallow. This might seem as overly complicated, but certain nodes already have copy
 * constructors with possibly different semantics, so a rather unlikely constructor signature was selected. Furthermore
 * if the class contains the deep or shallow copy constructor and does *not* contain the deepCopy() method, an error is
 * produced to avoid mismanagement.
 *
 * The deep or shallow constructor works with primitive types, arrays of primitives, deep copyable types and their
 * arrays and finally nodes and their arrays (for which it correctly uses the adoptChild truffle mechanism). If any
 * field is marked with @Shared annotation, it will be copied as a shallow reference to the new object. The signature
 * of the deep or shallow constructor is:
 *
 * public T(T other, boolean deep) where T is the type to be deep copied.
 *
 * Node visitor
 * ============
 *
 * Any subclass of RNode inherits the accept() method defined by the RNode class. Calling this calls the visit() method
 * of the visitor for the node itself and if the visit() call does not return false, calls the accept() method for all
 * its fields subclassing RNode.
 *
 * The fields can be marked with DoNotVisit annotation if they should not be visited (that is, if they are not part of
 * the actual executable tree). The VisitOrder annotation can be used to determine the order in which the fields will
 * be visited within a single class. This annotation is added because the Java standard does not specify any given order
 * of the fields in the class. Unannotated fields have the visit order equivalent to 0.
 *
 * (Note that there are no guarantees on the position of more fields with the same visit order).
 */
public class FastrLoader extends Loader implements Translator {

    /** If enabled, reports each deepcopyable class it loads. */
    private static final boolean REPORT_DEEPCOPYABLE_CLASS = true;
    /** If enabled, reports classes for which the deep copy code is not generated, i.e. they define both the deep or
     * shallow constructor and the deepCopy method. */
    private static final boolean REPORT_EXISTING_DEEPCOPY = true;
    /** If enabled, RNodes with existing accept() methods will be reported. */
    private static final boolean REPORT_EXISTING_ACCEPT = true;

    static ClassPool pool;

    /** Creates the loader with given base loader.
     *
     * This method is called when using the loader with tests, selecting it by the VM argument:
     *
     * -Djava.system.class.loader=r.analysis.codegen.FastrLoader
     */
    public FastrLoader(ClassLoader loader) {
        super(loader, pool = ClassPool.getDefault());
        try {
            addTranslator(pool, this);
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
            System.err.println("Unable to instantiate the loader. Exitting.");
            System.exit(-1);
        }
        doDelegation = true;
        // TODO possibly more classes should be added here
        delegateLoadingOf("r.analysis.codegen.annotations.");
    }

    /** Simple constructor assuming the parent loader to be the system class loader.
     */
    public FastrLoader() {
        this(getSystemClassLoader());
    }

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        DEBUG("Patcher is ENABLED");
    }


    /** Class load event as defined by javassist.
     *
     * Determines if any augmentation is necessary and performs the respective tasks if so.
     */
    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass cls = pool.get(classname);
        // deep copy
        if (implementsDeepCopyable(cls)) {
            if (REPORT_DEEPCOPYABLE_CLASS)
                DEBUG("loading deep copyable class " + cls.getName());
            if (hasDeepOrShallowConstructor(cls)) {
                if (!hasDeepCopyMethod(cls))
                    throw new Error("Class "+cls.getName()+" has a deep or shallow constructor, but not the deepCopy method. Both or neither must be present.");
                if (REPORT_EXISTING_DEEPCOPY)
                    DEBUG("  deep or shallow constructor and deepCopy method is present. Skipping...");
            } else {
                if (hasDeepCopyMethod(cls))
                    throw new Error("Class "+cls.getName()+" has a deepCopy method, but not the deep or shallow constructor. Both or neither must be present.");
                addDeepOrShallowConstructor(cls);
                addDeepCopyMethod(cls);
            }
        }
        // node visitor
        if (isRNode(cls)) {
            if (!hasVisitorAcceptMethod(cls))
                addVisitorAcceptMethod(cls);
            else
                if (REPORT_EXISTING_ACCEPT)
                    DEBUG("  existing boolean accept(NodeVisitor) found, skipping...");
        }
    }

    /** Returns true if the given class implements the DeepCopyable interface (false for the interface itself).
     */
    private boolean implementsDeepCopyable(CtClass cls) throws NotFoundException {
        return !cls.isInterface() && isDeepCopyable(cls);
    }

    /** Returns true if the given class iherits from DeepCopyable (that is if it implements it, or is the interface
     * itself).
     */
    private boolean isDeepCopyable(CtClass cls) throws NotFoundException {
        return cls.subtypeOf(pool.get("r.analysis.codegen.DeepCopyable"));
    }

    /** Returns true if the class inherits from (or is) the truffle node.
     */
    private boolean isNode(CtClass cls) throws NotFoundException {
        return cls.subclassOf(pool.get("com.oracle.truffle.api.nodes.Node"));
    }

    /** Checks if the given class implements deep or shallow constructor.
     *
     * Such constructor is a copy constructor with additional boolean argument specifying whether the copy should be
     * deep or shallow.
     */
    private boolean hasDeepOrShallowConstructor(CtClass cls) {
        try {
            cls.getDeclaredConstructor(new CtClass[] { cls, pool.get("boolean") });
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /** Checks if the given class implements the deepCopy method directly.
     *
     * Checks for such method. Throws an error if such method has any arguments.
     */
    private boolean hasDeepCopyMethod(CtClass cls) {
        try {
            CtMethod m = cls.getDeclaredMethod("deepCopy");
            if (m.getParameterTypes().length != 0)
                throw new Error("deepCopy() method for class "+cls.getName()+" does not have empty argument list.");
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

    /** Adds the deep or shallow constructor to the given class.
     *
     * The constructor specializes on deep or shallow copying, on primitive types, arrays of primitive types,
     * DeepCopyable fields and their arrays, Node fields and their arrays and shared DeepCopyable and node fields.
     */
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
                System.err.println("Error during code generation for class "+cls.getName());
                System.err.println(sb.toString());
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error during code generation for class "+cls.getName()+" -- class not found reported:");
            System.err.println("*** this should never happen, likely a loader problem ***");
            e.printStackTrace();
        }
    }

    /** Augments the class with the deepCopy method.
     */
    private void addDeepCopyMethod(CtClass cls) throws CannotCompileException {
        // Javassist is not happy with covariant return types so deepCopy is returning the interface
        CtMethod m = CtNewMethod.make("public r.analysis.codegen.DeepCopyable deepCopy() { return new "+cls.getName()+"(this, true); }",cls);
        m.setModifiers(Modifier.PUBLIC);
        cls.addMethod(m);
    }


    /** Returns true if the class inherits from (or is) the RNode.
     */
    private boolean isRNode(CtClass cls) throws NotFoundException {
        return cls.subclassOf(pool.get("r.nodes.truffle.RNode"));
    }

    /** Determines if the given class has the accept() method.
     *
     * The method should have a return type of boolean and a singe argument of type r.analysis.visitors.NodeVisitor. If
     * these conditions are not met, an error is thrown.
     */
    private boolean hasVisitorAcceptMethod(CtClass cls) {
        try {
            CtMethod m = cls.getDeclaredMethod("accept");
            CtClass[] args = m.getParameterTypes();
            if ((args.length == 1) && (args[0].getName().equals("r.analysis.visitor.NodeVisitor"))) {
                if (m.getReturnType().getName().equals("boolean"))
                    return true;
                else
                    throw new Error("accept() method for class "+cls.getName()+" does not have return type boolean");
            } else {
                throw new Error("accept() method for class "+cls.getName()+" does not have single NodeVisitor argument");
            }
        } catch (NotFoundException e) {
            return false;
        }
    }


    /** For a given field, returns its visit order.
     *
     * This is the value of the index of the VisitOrder annotation if present, or 0.
     */
    private static int getVisitOrder(CtField field) {
        try {
            if (field.hasAnnotation(VisitOrder.class))
                return ((VisitOrder) field.getAnnotation(VisitOrder.class)).index();
            else
                return 0;
        } catch (ClassNotFoundException e) {
            assert false;
            return 0;
        }
    }

    /** Augments the given class with its accept() method.
     *
     * The accept method always calls the superclass' accept method and only if this returns true, calls accept on its
     * RNode fields.
     */
    private void addVisitorAcceptMethod(CtClass cls) throws NotFoundException {
        // create the priority queue with the comparator on the linear order indices of the fields
        PriorityQueue<CtField> fields = new PriorityQueue<>(10, new Comparator<CtField>() {
            @Override
            public int compare(CtField o1, CtField o2) {
                int i1 = getVisitOrder(o1);
                int i2 = getVisitOrder(o2);
                return i1 - i2;
            }
        });
        // first order the CtFields based on their declared linear order
        for (CtField field : cls.getDeclaredFields()) {
            // check if the field is an RNode descendant
            if (isRNode(field.getType()) || (field.getType().isArray() && isRNode(field.getType().getComponentType()))) {
                // it is a field we want to visit
                if (field.hasAnnotation(DoNotVisit.class))
                    continue;
                fields.add(field);
            }
        }
        // create the accept method from the fields in the queue
        StringBuilder sb = new StringBuilder("public boolean accept(r.analysis.visitor.NodeVisitor visitor) {\n");
        sb.append("if (! super.accept(visitor))\n" +
                  "    return false;\n");
        while (!fields.isEmpty()) {
            CtField field = fields.poll();
            String code;
            if (field.getType().isArray()) {
                code = "if (FNAME != null)\n" +
                        "    for (int i = 0; i < FNAME.length; ++i)\n" +
                        "        if (FNAME[i] != null)\n" +
                        "            FNAME[i].accept(visitor);\n";
                code = code.replace("CTYPE",field.getType().getComponentType().getName());
            } else {
                code = "if (FNAME != null)\n" +
                        "    FNAME.accept(visitor);\n";
            }
            code = code.replace("FNAME",field.getName());
            sb.append(code);
        }
        sb.append("return true;\n");
        sb.append("}");
        // done writing the code, create the method
        try {
            CtMethod m = CtNewMethod.make(sb.toString(), cls);
            m.setModifiers(Modifier.PUBLIC);
            cls.addMethod(m);
        } catch (CannotCompileException e) {
            System.err.println("Unable to add the accept() method for node visitor:");
            System.err.println(sb.toString());
            e.printStackTrace();
        }
    }
}
