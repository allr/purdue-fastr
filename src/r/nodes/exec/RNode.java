package r.nodes.exec;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import r.*;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.tools.*;
import r.runtime.*;

public abstract class RNode {

    public static final boolean CLEAR_PARENT_POINTERS = true;  // when removing a node from the tree, set its pointer to a parent to null
    public static final boolean CLEAR_CHILD_POINTERS = true;   // when removing a node from the tree, clear its parent's pointer to that removed node
    public static final boolean CHECKED_REPLACE_CHILD = true;
    public static final boolean CHECKED_REPLACE = true;

    public static final boolean DEBUG_REWRITING = false;

    protected RNode parent;
    protected RNode replacedByNode;

    public RNode() {
        assert Utils.check(checkReplaceChild(this.getClass()));
        if (DEBUG_REWRITING) {
            registerConstructors();
        }
    }

    public ASTNode getAST() {
        return getParent().getAST();
    }

    public RNode getParent() {
        return parent;
    }

    public RNode getNewNode() {
        return replacedByNode;
    }

    public boolean inTree() {
        // this assumes clearing of parent pointers
        // and indeed means that a root node is never assumed to be in a tree
        return parent != null;
    }

    public abstract Object execute(Frame frame);

    public int executeScalarLogical(Frame frame) throws SpecializationException {
        return RValueConversion.expectScalarLogical((RAny) execute(frame));
    }

    public int executeScalarInteger(Frame frame) throws SpecializationException {
        return RValueConversion.expectScalarInteger((RAny) execute(frame));
    }

    public int executeScalarNonNALogical(Frame frame) throws SpecializationException {
        return RValueConversion.expectScalarNonNALogical((RAny) execute(frame));
    }

    public Object executeVoid(Frame frame) {
        execute(frame);
        return RNull.getNull();
    }

    public static class PushbackNode extends BaseR {
        @Child RNode realChildNode;
        final Object nextValue;

        public PushbackNode(ASTNode ast, RNode realChildNode, Object nextValue) {
            super(ast);
            realChildNode.replace(this);
            this.realChildNode = adoptChild(realChildNode);
            this.nextValue = nextValue;
        }

        @Override
        public Object execute(Frame frame) {
            try {
                throw new SpecializationException(null);
            } catch (SpecializationException e) {
                replace(realChildNode);
                return nextValue;
            }
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (realChildNode == oldNode) {
                realChildNode = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }
    }

    public <T extends RNode> void pushBack(T childNode, Object value) {
        new PushbackNode(childNode.getAST(), childNode, value);
    }

    public <T extends RNode> Object replace(T childNode, Object childValue, RNode newNode, Frame frame) {
        pushBack(childNode, childValue);
        return replace(newNode).execute(frame);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Children {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Child {}

    private static void unlinkChildNode(RNode childNode) {
        // be defensive to detect errors in tree rewriting

        RNode parentNode = childNode.getParent();
        if (parentNode != null) {
            if (CLEAR_CHILD_POINTERS) {
                parentNode.replaceChild(childNode, null);
            }
            if (CLEAR_PARENT_POINTERS) {
                childNode.parent = null;
            }
        }
    }

    private static void unlinkArrayElementChildNode(RNode childNode) {
        // be defensive to detect errors in tree rewriting

        RNode parentNode = childNode.getParent();
        if (parentNode != null) {
            // we cannot delete the child from the old parent, because it is in an array, which is shared possibly among multiple nodes
            if (CLEAR_PARENT_POINTERS) {
                childNode.parent = null;
            }
        }
    }

    public final <N extends RNode> N replace(N newNode, String msg) {
        if (DEBUG_REWRITING) {
            System.err.println("REPLACE: " + msg + " current node " + this + " by new node " + newNode + ", parent is " + getParent());
            printCurrentTree();
        }
        assert Utils.check(newNode != this, "replacing a node by itself.. why?");
        this.replacedByNode = newNode;
        unlinkChildNode(newNode);
        RNode oldParent = getParent();
        if (oldParent == null) {
            // replacing root node
            return newNode;
        }
        newNode.replacedByNode = null;
        N res = oldParent.replaceChild(this, newNode);
        if (CLEAR_PARENT_POINTERS) {
            this.parent = null;
        }
        if (CHECKED_REPLACE) {
            assert Utils.check(newNode.getParent() == oldParent);
        }
        return res;
    }

    public final <N extends RNode> N insert(N childNode, String msg) {
        assert Utils.check(childNode != this);

        RNode parentNode = childNode.getParent();
        if (DEBUG_REWRITING) {
            System.err.println("INSERT: " + msg + " current node " + this + " between parent node " + parentNode + " and child node " + childNode);
            printCurrentTree();
        }
        assert Utils.check(parentNode != this);
        assert Utils.check(parentNode != childNode);
        assert Utils.check(parentNode != null);
        assert Utils.check(childNode != null);

        parentNode.replaceChild(childNode, this);
        this.parent = parentNode;
        childNode.parent = this;
        return childNode;
    }

    public final <N extends RNode> N replace(N newNode) {
        return replace(newNode, "");
    }

    // replaceChild should never be called directly by user program (from nodes, except from replaceChild itself)
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        if (CHECKED_REPLACE_CHILD) {
            suggestReplaceChild(oldNode, newNode);
            assert Utils.check(false, "missing/incomplete replaceChild method in " + this.getClass());
            return null;
        } else {
            return newNode;
        }
    }

    public final <N extends RNode> N adoptChild(N childNode) {
        if (childNode != null) {
            unlinkChildNode(childNode);
            childNode.parent = this;
            assert Utils.check(replacedByNode == null);
            childNode.replacedByNode = null;
        }
        return childNode;
    }

    public final <N extends RNode> N[] adoptChildren(N[] childNodes) {

        boolean detectedArraySharing = false;

        for (int i = 0; i < childNodes.length; i++) {
            N n = childNodes[i];
            if (n != null) {
                // unlink the node from parent (to discover rewriting errors easier)

                RNode pn = n.getParent();
                if (CLEAR_CHILD_POINTERS && pn != null && !detectedArraySharing) {
                    pn.replaceChild(n, null);
                    if (childNodes[i] == null) {
                        // the array of nodes is being shared (!)
                        childNodes[i] = n; // fix it
                        detectedArraySharing = true;
                    }
                }
                n.parent = this;
            }
        }
        return childNodes;
    }

    public final <N extends RNode> N adoptInternal(N childNode) {
        if (childNode != null) {
            if (CLEAR_PARENT_POINTERS) {
                assert Utils.check(childNode.parent == null); // not necessary now when unlinking child nodes
            }
            childNode.replacedByNode = null;
            childNode.parent = this;
        }
        return childNode;
    }

    private static HashSet<Class> suggestedForClass = new HashSet<Class>();

    private void suggestReplaceChild(RNode oldNode, RNode newNode) {
        suggestReplaceChild(this.getClass(), oldNode, newNode);
    }

    private void suggestReplaceChild(Class nodeClass, RNode oldNode, RNode newNode) {

        Field[] fields = nodeClass.getDeclaredFields();

        int nchildren = 0;
        boolean sawOldNode = false;

        for (Field f : fields) {
            if (f.isSynthetic()) {
                continue;
            }
            Class fieldClass = f.getType();
            if (RNode.class.isAssignableFrom(fieldClass)) {
                nchildren++;
                try {
                    f.setAccessible(true);
                    Object n = f.get(this);
                    if (n == oldNode) {
                        assert sawOldNode == false;
                        sawOldNode = true;
                    }
                } catch (IllegalAccessException e) {
                    System.err.println("can't read a node field" + e);
                    assert Utils.check(false, "can't read a node field");
                }
                continue;
            }
            if (fieldClass.isArray()) {
                Class componentClass = fieldClass.getComponentType();
                if (RNode.class.isAssignableFrom(componentClass)) {
                    try {
                        f.setAccessible(true);
                        Object nodesArray = f.get(this);
                        int length = Array.getLength(nodesArray);
                        for (int i = 0; i < length; i++) {
                            Object n = Array.get(nodesArray, i);
                            nchildren++;
                            if (n == oldNode) {
                                assert sawOldNode == false;
                                sawOldNode = true;
                            }
                        }

                    } catch (IllegalAccessException e) {
                        System.err.println("can't read a node field (array field)" + e);
                        assert Utils.check(false, "can't read a node field (array field)");
                    }
                }
            }
        }

        if (nchildren > 0) {
            suggestReplaceChildForClass(nodeClass);
        }

        if (!sawOldNode) {
            Class nodeSuperClass = nodeClass.getSuperclass();
            if (nodeSuperClass != RNode.class) {
                suggestReplaceChild(nodeSuperClass, oldNode, newNode);
            } else {
                System.err.println("Cannot suggest a missing replaceChild - did not find the node to replace, but there is no super-class - something is wrong");
                Thread.dumpStack();
            }
        }
    }

    private static boolean suggestReplaceChildForClass(Class nodeClass) {
        if (!suggestedForClass.contains(nodeClass)) {
            suggestedForClass.add(nodeClass);

            Field[] fields = nodeClass.getDeclaredFields();
            System.err.println("Suggested missing replaceChild for " + nodeClass);

            System.err.println();
            System.err.println("@Override");
            System.err.println("protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {");
            System.err.println("\tassert oldNode != null;");

            for (Field f : fields) {
                if (f.isSynthetic()) {
                    continue;
                }
                Class fieldClass = f.getType();
                if (RNode.class.isAssignableFrom(fieldClass)) {
                    System.err.println("\tif (" + f.getName() + " == oldNode) {");
                    System.err.print("\t\t" + f.getName() + " = ");
                    if (fieldClass != RNode.class) {
                        System.err.print("(" + fieldClass.getCanonicalName() + ") ");
                    }
                    System.err.println("newNode;");
                    System.err.println("\t\treturn adoptInternal(newNode);");
                    System.err.println("\t}");
                }
                if (fieldClass.isArray()) {
                    Class componentClass = fieldClass.getComponentType();
                    if (RNode.class.isAssignableFrom(componentClass)) {
                        System.err.println("\tif (" + f.getName() + " != null) {");
                        System.err.println("\t\tfor(int i = 0; i < " + f.getName() + ".length; i++) {");
                        System.err.println("\t\t\tif (" + f.getName() + "[i] == oldNode) {");
                        System.err.print("\t\t\t\t" + f.getName() + "[i] = ");
                        if (componentClass != RNode.class) {
                            System.err.print("(" + componentClass.getCanonicalName() + ") ");
                        }
                        System.err.println("newNode;");
                        System.err.println("\t\t\t\treturn adoptInternal(newNode);");
                        System.err.println("\t\t\t}");
                        System.err.println("\t\t}");
                        System.err.println("\t}");
                    }
                }
            }
            System.err.println("\treturn super.replaceChild(oldNode, newNode);");
            System.err.println("}");
            return true;
        }
        return false;
    }

    public static boolean checkReplaceChild(Class nodeClass) {

        Field[] fields = nodeClass.getDeclaredFields();

        ArrayList<Field> childNodes = new ArrayList<Field>();
        for (Field f : fields) {
            if (f.isSynthetic()) {
                continue;
            }
            Class fieldClass = f.getType();
            if (RNode.class.isAssignableFrom(fieldClass)) {
                childNodes.add(f);
                continue;
            }
            if (fieldClass.isArray()) {
                Class componentClass = fieldClass.getComponentType();
                if (RNode.class.isAssignableFrom(componentClass)) {
                    childNodes.add(f);
                }
            }
        }

        if (childNodes.size() > 0) {

            try {
                nodeClass.getDeclaredMethod("replaceChild", new Class[] { RNode.class, RNode.class } );
                return true;
            } catch (NoSuchMethodException e) {
                if (suggestReplaceChildForClass(nodeClass)) {
                    System.err.println("ERROR: Missing replaceChild in class (suggestion above)");
                    System.err.print("Child nodes may be in fields:");
                    for(Field f : childNodes) {
                        System.err.print(" " + f.getName());
                    }
                    System.err.println();
                }

                return false;
            }
        }
        Class superClass = nodeClass.getSuperclass();
        if (superClass != RNode.class) {
            return checkReplaceChild(superClass);
        }
        return true;
    }

    private RNode currentRoot() {
        assert Utils.check(getNewNode() == null);

        RNode n = this;
        while(n.getParent() != null) {
            n = n.getParent();
        }
        return n;
    }

    private void printCurrentTree() {
        RNode root = currentRoot();
        printTree(root, 0, System.err);
    }

    private static void printIndent(int indent, PrintStream ps) {
        for(int i = 0; i < indent; i++) {
            ps.print(" ");
        }
    }

    private static Field[] getAllFields(Class cls) {
        ArrayList<Field> res = new ArrayList<Field>();
        Class c = cls;
        while (c != RNode.class) {
            assert Utils.check(c != null);
            res.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        return res.toArray(new Field[res.size()]);
    }

    private static void printTree(RNode root, int indent, PrintStream ps) {

        if (root == null) {
            ps.println("."); // marks a leaf node
            return;
        }

        Class nodeClass = root.getClass();
        String className = nodeClass.getName();
        ps.print("->" + className);
        String info = knownConstructors.get(className);
        if (info != null) {
            ps.print(" (" + info + ") ");
        }
        ps.println(" [" + PrettyPrinter.prettyPrint(root.getAST()) + "]");

        Field[] fields = getAllFields(nodeClass);
        for(Field f : fields) {
            if (f.isSynthetic()) {
                continue;
            }
            Class fieldClass = f.getType();
            if (RNode.class.isAssignableFrom(fieldClass)) {
                printIndent(indent + 2, ps);
                ps.print(f.getName());
                RNode child;
                try {
                    f.setAccessible(true);
                    child = (RNode) f.get(root);
                    printTree(child, indent + 4, ps);
                } catch (IllegalAccessException e) {
                    assert Utils.check(false, "can't read a node field " + e);
                }
            }
            if (fieldClass.isArray()) {
                Class componentClass = fieldClass.getComponentType();
                if (RNode.class.isAssignableFrom(componentClass)) {
                    try {
                        f.setAccessible(true);
                        Object nodesArray = f.get(root);
                        int length = Array.getLength(nodesArray);
                        for (int i = 0; i < length; i++) {
                            RNode child = (RNode) Array.get(nodesArray, i);
                            printIndent(indent + 2, ps);
                            ps.print(f.getName() + "[" + i + "]");
                            printTree(child, indent + 4, ps);
                        }
                    } catch (IllegalAccessException e) {
                        assert Utils.check(false, "can't read a node field (array field) " + e);
                    }
                }
            }
        }

    }

    private static HashMap<String, String> knownConstructors = new HashMap<String,String>();
    private static void registerConstructors() {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        for(StackTraceElement e : traces) {
            if ("<init>".equals(e.getMethodName())) {
                String className = e.getClassName();
                if (!knownConstructors.containsKey(className)) {
                    String info = e.getFileName() + ":" + e.getLineNumber();
                    knownConstructors.put(className, info);
                }
            }
        }

    }

}
