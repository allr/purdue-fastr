package r.fusion;

import javassist.*;

import java.util.Vector;

/** Builds the fused operator from given view signature.
 *
 * The view signatures can consist of the following items:
 *
 * - inputs : _ SIZE TYPE
 * - unary operators: U OP SIZE TYPE operand
 * - binary operators: B OP SIZE TYPE RESULT_SIZE left right
 *
 * where the size is either V for vector, or S for scalar, the type is either D for double, I for integer, C for
 * complex, or L for logical and the result size is either E for two vectors of equal length, A for vector of same
 * size as left operand, B for same size as right operand and S for scalar (the scalar value is likely not used).
 * The OP then stands for the type of the operand (+, - , *, / ).
 *
 */
public class FusionBuilder {

    public static Fusion.Prototype build(String signature) {
        try {
            FusionBuilder fb = new FusionBuilder(signature);
            if (Fusion.DEBUG)
                System.out.println(fb);
            Class fopClass = fb.createFusedOperatorClass();


            Fusion.Prototype fop = (Fusion.Prototype) fopClass.newInstance();


            return fop;
        } catch (InvalidSignatureError | InstantiationException | IllegalAccessException e) {
            if (Fusion.DEBUG) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    Vector<Node.Input> inputs = new Vector<>();
    Vector<Node.Input> vectorInputs = new Vector<>();
    Node ast;

    final String signature;
    /** Index of input that is the same size as the result.
     */
    int resultSize;

    static final ClassPool pool;

    static final CtClass prototype;

    static {
        pool = ClassPool.getDefault();
        CtClass p = null;
        CtClass f = null;
        try {
            p = pool.get("r.fusion.Fusion$Prototype");
        } catch (NotFoundException e) {
            e.printStackTrace();
            System.err.println("Unable to initialize fusion runtime. Exitting.");
            System.exit(-1);
        }
        prototype = p;
    }


    /** Adds the input fields to the fused operator.
     *
     * Each input (numbered input0...n) is either a primitive type, or an array if the input is vector.
     *
     * TODO the full R datatypes should be stored as well to have a place from which the attributes should be taken.
     */
    private void addInputFields(CtClass fop) throws CannotCompileException, NotFoundException {
        for (Node.Input i : inputs) {
            CtField f;
            switch (i.type) {
                case Node.DOUBLE:
                    f = new CtField(pool.get("double" + (i.size == Node.VECTOR ? "[]" : "")), "input" + i.index, fop);
                    break;
                case Node.INT:
                    f = new CtField(pool.get("int" + (i.size == Node.VECTOR ? "[]" : "")), "input" + i.index, fop);
                    break;
                default:
                    throw new InvalidSignatureError("Input type " + i.type + " not supported");
            }
            assert (f != null);
            fop.addField(f);
        }
    }

    /** Adds the free() method to the fused operator.
     *
     * The free method simply sets all array inputs to null so that they can be garbage collected if they are not
     * referenced anywhere else.
     *
     * TODO this should also delete the full R types once they are added (see TODO in addInputFields())
     */
    private void addFreeMethod(CtClass fop) throws CannotCompileException {
        StringBuilder sb = new StringBuilder();
        sb.append("public void free() {\n");
        for (Node.Input i : vectorInputs)
            sb.append("    input" + i.index + " = null; \n");
        sb.append("}");
        fop.addMethod(CtNewMethod.make(sb.toString(), fop));
    }

    private void addVisitorMethods(CtClass fop) throws CannotCompileException, NotFoundException {
        StringBuilder sbd = new StringBuilder();
        sbd.append("public void visitLeaf(r.data.RDouble element) {\n");
        sbd.append("    switch (idx) {\n");
        StringBuilder sbi = new StringBuilder();
        sbi.append("public void visitLeaf(r.data.RInt element) {\n");
        sbi.append("    switch (idx) {\n");
        for (Node.Input i : inputs) {
            switch (i.type) {
                case Node.DOUBLE:
                    sbd.append("        case " + i.index + ":\n");
                    if (i.size == Node.VECTOR)
                        sbd.append("            input"+i.index+" = element.getContent();\n");
                    else
                        sbd.append("            input"+i.index+" = element.getDouble(0); assert_ (element.size() == 1);\n");
                    break;
                case Node.INT:
                    sbi.append("        case " + i.index + ":\n");
                    if (i.size == Node.VECTOR)
                        sbi.append("            input"+i.index+" = element.getContent();\n");
                    else
                        sbi.append("            input"+i.index+" = element.getInt(0); assert_ (element.size() == 1);\n");
                    break;
                case Node.COMPLEX:
                    // TODO add complex numbers too
                    throw new InvalidSignatureError("Complex input types not supported");
                default:
                    throw new InvalidSignatureError("Input type " + i.type + " not supported");
            }
        }
        sbd.append("        default:\n            assert_ (false);\n");
        sbi.append("        default:\n            assert_ (false);\n");
        sbd.append("    }\n    ++idx;\n}");
        sbi.append("    }\n    ++idx;\n}");
        fop.addMethod(CtNewMethod.make(sbd.toString(), fop));
        fop.addMethod(CtNewMethod.make(sbi.toString(), fop));
    }

    private void addMaterializeMethod(CtClass fop) throws CannotCompileException {
        String src = MaterializeCodeBuilder.emit(this);
        System.out.println(src);
        fop.addMethod(CtNewMethod.make(src, fop));
    }


    private Class createFusedOperatorClass() {
        try {
            CtClass fop = pool.makeClass(signature);
            fop.setSuperclass(prototype);
            addInputFields(fop);
            addVisitorMethods(fop);
            addFreeMethod(fop);
            addMaterializeMethod(fop);
            return fop.toClass();
        } catch (CannotCompileException e) {
            if (Fusion.DEBUG)
                e.printStackTrace();
            throw new InvalidSignatureError("Javassist CannotCompileException raised");
        } catch (NotFoundException e) {
            if (Fusion.DEBUG)
                e.printStackTrace();
            throw new InvalidSignatureError("Javassist NotFound raised");
        }
    }





    private FusionBuilder(String signature) {
        this.signature = signature;
        ast = SignatureParser.parse(this);
        resultSize = ast.calculateResultSize();
        ast.setSizeSameAsResult();
    }


    /** Debug display method that shows the FusedBuilder's internal structures.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Signature:      " + signature + "\n");
        sb.append("AST signature:  " + ast.signature() + "\n");
        sb.append("Simplified AST: " + ast.toString() + "\n");
        sb.append("Inputs:         (" + inputs.size() + ")\n");
        sb.append("Result size:    " + resultSize + "\n");
        for (Node.Input i : inputs)
            sb.append("                " + i.index + ": " + i.signature() + (i.sizeSameAsResult ? " (result size)" : "") +"\n");
        return sb.toString();
    }


    public String buildMaterializeMethod() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }



}
