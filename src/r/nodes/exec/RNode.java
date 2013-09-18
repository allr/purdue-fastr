package r.nodes.exec;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import r.*;
import r.data.*;
import r.nodes.ast.*;
import r.runtime.*;

public abstract class RNode {

    protected RNode parent;

    public RNode() {
        assert Utils.check(checkReplaceChild(this.getClass()));
    }

    public ASTNode getAST() {
        return getParent().getAST();
    }

    public RNode getParent() {
        return parent;
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

    public final <N extends RNode> N replace(N newNode, String msg) {
        RNode oldParent = getParent();
        if (oldParent == null) {
            // replacing root node
            return newNode;
        }
        N res = oldParent.replaceChild(this, newNode);
        this.parent = null;
        assert Utils.check(newNode.getParent() == oldParent);
        return res;
    }

    public final <N extends RNode> N replace(N newNode) {
        return replace(newNode, "");
    }

    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        suggestReplaceChild(oldNode, newNode);
        assert Utils.check(false, "missing/incomplete replaceChild method in " + this.getClass());
        return null;
    }

    public final <N extends RNode> N adoptChild(N childNode) {
        if (childNode != null) {
            childNode.parent = this;
        }
        return childNode;
    }

    public final <N extends RNode> N[] adoptChildren(N[] childNodes) {
        for (RNode n : childNodes) {
            if (n != null) {
                n.parent = this;
            }
        }
        return childNodes;
    }

    public final <N extends RNode> N adoptInternal(N childNode) {
        if (childNode != null) {
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

}
