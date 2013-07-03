package r;

import javassist.*;
import javassist.Modifier;

import java.lang.reflect.*;

public class fastr {

    /** Injects the node patching class loader and then launches the r.Console main method. The patched class loader
     * makes sure to inject copy() methods and copy constructors to all nodes (that is decendants of
     * truffle.api.Nodes.Node.
     */
    public static void main(String[] args) {
        try {
            ClassPool pool = ClassPool.getDefault();
            Loader cl = new Loader(pool);
            cl.addTranslator(pool, new ClassPatcher());
            Class c = cl.loadClass("r.Console");
            Method m = c.getMethod("main",String[].class);
            m.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
    }

    /** JavaAssist translator that performs the augmentation of all nodes to be deep copyable.
     */
    static class ClassPatcher implements Translator {

        @Override
        public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {
            // really nothing important to be done
            System.out.println("ClassPatcher is ENABLED");
        }

        @Override
        public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
            CtClass cls = pool.get(classname);
            if (cls.getName().contains("$"))
                return;
            if (isNode(cls)) {
                System.out.println("loading "+cls.getName());
                if (! hasCopyMethod(cls)) {
                    if (! hasCopyConstructor(cls))
                      injectCopyConstructor(cls);
                    CtMethod m = CtNewMethod.make("public "+cls.getName()+" copy() { return new "+cls.getName()+"(this); }",cls);
                    cls.addMethod(m);
                }
            }
        }

        protected boolean isNode(CtClass cls) throws NotFoundException {
            return (cls == null) ? false : cls.getName().equals("com.oracle.truffle.api.nodes.Node") || isNode(cls.getSuperclass());
        }

        protected boolean hasCopyMethod(CtClass cls) {
            try {
                cls.getDeclaredMethod("copy");
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

        protected void injectCopyConstructor(CtClass cls) throws NotFoundException, CannotCompileException {
            CtField[] fields = cls.getDeclaredFields();
            StringBuilder sb = new StringBuilder("{");
            sb.append("super($1);");
            for (CtField field : fields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;
                if (isNode(field.getType()))
                    sb.append(field.getName() + " = adoptChild($1."+field.getName()+".copy());");
                else
                    sb.append(field.getName() + " = $1."+field.getName()+";");
            }
            sb.append("}");
            System.out.println(cls.getName()+" >> " +sb.toString());
            try {
                CtConstructor c = CtNewConstructor.make(new CtClass[] { cls }, null, sb.toString(), cls);
                cls.addConstructor(c);
            } catch (CannotCompileException e) {
                System.out.println(sb.toString());
                e.printStackTrace();
            }
        }
    }

}
