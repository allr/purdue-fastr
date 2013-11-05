package r.fusion;

/** Builds the materialize method.
 *
 * The materialize method has always the same structure. First the result primitive vector is created. Then the
 * loop header starts looping over indices from 0 to the length of the result vector. For any inputs that do not
 * have the length of the result, their own indices are created.
 *
 * Inside the loop elements of all non scalar inputs are read into their designated variables. The computation
 * then commences followed by the NA checks on the result.
 *
 * The loop footer increments all non-result indices and performs the wrap around if applicable. Finally RArray
 * is created out of the result array and is returned.
 *
 * TODO Attributes should be handled here.
 */
public class MaterializeCodeBuilder {

    int freeTemp = 0;
    StringBuilder sb = new StringBuilder();
    private FusionBuilder fb;

    private MaterializeCodeBuilder(FusionBuilder fusionBuilder) {
        this.fb = fusionBuilder;
    }

    public static String emit(FusionBuilder fb) {
        MaterializeCodeBuilder b = new MaterializeCodeBuilder(fb);
        b.buildMaterializeMethod();
        return b.sb.toString();
    }


    public final String buildInput(Node.Input input) {
        return (input.size == Node.SCALAR ? "input" : "in") + input.index;
    }


    public final void addResultDeclaration(char type, String result) {
        switch (type) {
            case Node.DOUBLE:
                sb.append("double "+result+";\n");
                break;
            case Node.INT:
                sb.append("int "+result+";\n");
                break;
            case Node.COMPLEX:
                throw new InvalidSignatureError("Type " + type + " is not supported yet");
            default:
                throw new InvalidSignatureError("Unsupported operator result type " + type);
        }
    }

    public final String buildBinary(Node.Binary binary) {
        String result = "t" + freeTemp;
        ++freeTemp;
        String left = binary.left.build(this);
        String right = binary.right.build(this);
        addResultDeclaration(binary.type, result);
        switch (binary.op) {
            case Node.ADD:
                add(binary, result, left, right);
                break;
            case Node.SUB:
                sub(binary, result, left, right);
                break;
            case Node.MUL:
                mul(binary, result, left, right);
                break;
            case Node.DIV:
                div(binary, result, left, right);
                break;
            case Node.MOD:
                mod(binary, result, left, right);
                break;
            default:
                throw new InvalidSignatureError("Unsupported binary operator " + binary.op);
        }
        return result;
    }

    public final String buildUnary(Node.Unary unary) {
        String result = "t" + freeTemp;
        ++freeTemp;
        String operand = unary.operand.build(this);
        addResultDeclaration(unary.type, result);
        switch (unary.op) {
            case Node.SUB:
                minus(unary, result, operand);
                break;
            default:
                throw new InvalidSignatureError("Unsupported unary operator " + unary.op);
        }
        return result;
    }


    // TODO complete the operations for the different types and operators



    public final void add(Node.Binary node, String result, String left, String right) {
        if ((node.left.type == Node.DOUBLE || node.left.type == Node.INT) && (node.right.type == Node.DOUBLE || node.right.type == Node.INT)) {
            sb.append(result + " = " + left + " + " + right + ";\n");
        } else {
            throw new InvalidSignatureError("Unsupported combination of add operand types " + node.left.type + ", " + node.right.type);
        }
    }

    public final void sub(Node.Binary node, String result, String left, String right) {
        if ((node.left.type == Node.DOUBLE || node.left.type == Node.INT) && (node.right.type == Node.DOUBLE || node.right.type == Node.INT)) {
            sb.append(result + " = " + left + " - " + right + ";\n");
        } else {
            throw new InvalidSignatureError("Unsupported combination of sub operand types " + node.left.type + ", " + node.right.type);
        }

    }

    public final void mul(Node.Binary node, String result, String left, String right) {
        if ((node.left.type == Node.DOUBLE || node.left.type == Node.INT) && (node.right.type == Node.DOUBLE || node.right.type == Node.INT)) {
            sb.append(result + " = " + left + " * " + right + ";\n");
        } else {
            throw new InvalidSignatureError("Unsupported combination of mul operand types " + node.left.type + ", " + node.right.type);
        }

    }

    public final void div(Node.Binary node, String result, String left, String right) {
        sb.append(result + " = " + left + " / " + right + ";\n");


    }

    public final void mod(Node.Binary node, String result, String left, String right) {

    }

    public final void minus(Node.Unary node, String result, String operand) {

    }


    private void buildMaterializeMethod() {
        sb.append("public r.data.RArray materialize_() {\n");
        buildResultDefinition();
        buildLoopHeader();
        fb.ast.build(this);
        buildNACheck();
        sb.append("        result[i] = t0;\n");
        buildLoopFooter();
        buildResultWrapup();
        sb.append("}\n");
    }



    /** Creates the code for the result definition.
     *
     * Result can be double, integer, or complex vector.
     *
     * TODO ? other result types
     */
    private void buildResultDefinition() {
        if (fb.ast.size != Node.VECTOR)
            throw new InvalidSignatureError("Result of the view must be a vector. Scalars are not supported.");
        switch (fb.ast.type) {
            case Node.DOUBLE:
                sb.append("    double[] result = new double[input" + fb.resultSize + ".length];\n");
                break;
            case Node.INT:
                sb.append("    int[] result = new int[input" + fb.resultSize + ".length];\n");
                break;
            case Node.COMPLEX:
                // TODO complex results
                throw new InvalidSignatureError("Complex type is not supported yet");
            default:
                throw new InvalidSignatureError("Result type of the view not supported (Double, Integer, Complex)");
        }
    }

    private void buildResultWrapup() {
        if (fb.ast.size != Node.VECTOR)
            throw new InvalidSignatureError("Result of the view must be a vector. Scalars are not supported.");
        switch (fb.ast.type) {
            case Node.DOUBLE:
                sb.append("    return r.data.RDouble.RDoubleFactory.getFor(result);\n");
                break;
            case Node.INT:
                sb.append("    return r.data.RInt.RIntFactory.getFor(result);\n");
                break;
            case Node.COMPLEX:
                // TODO complex results
                throw new InvalidSignatureError("Complex type is not supported yet");
            default:
                throw new InvalidSignatureError("Result type of the view not supported (Double, Integer, Complex)");
        }
    }

    private void buildLoopHeader() {
        // generate indices for inputs that are not of the same size as the result
        for (Node.Input i : fb.inputs) {
            if (i.sizeSameAsResult || i.size == Node.SCALAR)
                continue;
            sb.append("    int idx" + i.index + " = 0;\n");
        }
        // generate the loop header itself
        sb.append("    for (int i = 0; i < result.length; ++i) {\n");
        // load all the indices
        for (Node.Input i : fb.vectorInputs) {
            switch (i.type) {
                case Node.DOUBLE:
                    sb.append("        double in"+i.index+" = input"+i.index+"["+ (i.sizeSameAsResult ? "i" : "idx"+i.index) + "];\n");
                    break;
                case Node.INT:
                    sb.append("        int in"+i.index+" = input"+i.index+"["+ (i.sizeSameAsResult ? "i" : "idx"+i.index) + "];\n");
                    break;
                default:
                    throw new InvalidSignatureError("Type " + i.type + " is not supported yet");
            }
        }
    }

    private void buildLoopFooter() {
        //
        for (Node.Input i : fb.inputs) {
            if (i.sizeSameAsResult || i.size == Node.SCALAR)
                continue;
            sb.append("        ++idx" + i.index + ";\n");
            sb.append("        if (idx" + i.index + " == input" + i.index +".length)\n");
            sb.append("            idx" + i.index + " = 0;\n");
        }
        sb.append("    }\n");
    }

    String rType(char type) {
        switch (type) {
            case Node.DOUBLE:
                return "r.data.RDouble";
            case Node.INT:
                return "r.data.RInt";
            case Node.LOGICAL:
                return "r.data.RLogical";
            default:
                throw new InvalidSignatureError("Type " + type + " is not supported yet");
        }
    }

    private void buildNACheck() {
        assert (fb.vectorInputs.size() > 0);
        sb.append("        boolean isNA = false;\n");
        for (Node.Input i : fb.inputs) {
            if (i.type != Node.INT)
                continue;
            sb.append("        isNA = isNA || (" + (i.size == Node.VECTOR ? "in" : "input") + i.index + " == r.data.RInt.NA);\n");
        }
        sb.append("        if (isNA)\n");
        sb.append("            t0 = " + rType(fb.ast.type) + ".NA;\n");
    }





}
