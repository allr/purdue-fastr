package r.fusion;

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

    /** Error to be thrown when unsupported view state is found.
     *
     * When this error is thrown from the build process, there should be no further attempts on building the fused
     * operator for a view with such signature.
     */
    public class InvalidSignatureError extends Error {
        final int idx;
        public InvalidSignatureError(String message) {
            super(message);
            idx = -1;
        }

        public InvalidSignatureError(String message, int idx) {
            super(message);
            this.idx = idx;

        }
    }

    Vector<Node.Input> inputs = new Vector<>();
    Vector<Node.Input> vectorInputs = new Vector<>();
    Node ast;

    final String signature;
    /** Index of input that is the same size as the result.
     */
    private int resultSize;



    public FusionBuilder(String signature) {
        this.signature = signature;
        ast = SignatureParser.parse(this);
        resultSize = ast.calculateResultSize();
        ast.setSizeSameAsResult();
    }


    // view ast nodes ----------------------------------------------------------------------------------------------


    // view ast parsing --------------------------------------------------------------------------------------------


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
    public String buildMaterializeMethod() {
        StringBuilder sb = new StringBuilder();
        sb.append("@Override public RArray materialize_() {\n");
        buildResultDefinition(sb);
        buildLoopHeader(sb);
        ComputationCodeBuilder cb = new ComputationCodeBuilder(this);
        sb.append(cb.emit());

        buildNACheck(sb);
        sb.append("        result[i] = t0;\n");
        buildLoopFooter(sb);
        sb.append("}\n");
        return sb.toString();
    }


    /** Creates the code for the result definition.
     *
     * Result can be double, integer, or complex vector.
     *
     * TODO ? other result types
     */
    private void buildResultDefinition(StringBuilder sb) {
        if (ast.size != Node.VECTOR)
            throw new InvalidSignatureError("Result of the view must be a vector. Scalars are not supported.");
        switch (ast.type) {
            case Node.DOUBLE:
                sb.append("    double[] result = new double[input" + resultSize + ".length];\n");
                break;
            case Node.INT:
                sb.append("    int[] result = new int[input" + resultSize + ".length];\n");
                break;
            case Node.COMPLEX:
                // TODO complex results
                throw new InvalidSignatureError("Complex type is not supported yet");
            default:
                throw new InvalidSignatureError("Result type of the view not supported (Double, Integer, Complex)");
        }
    }

    private void buildLoopHeader(StringBuilder sb) {
        // generate indices for inputs that are not of the same size as the result
        for (Node.Input i : inputs) {
            if (i.sizeSameAsResult || i.size == Node.SCALAR)
                continue;
            sb.append("    int idx" + i.index + " = 0;\n");
        }
        // generate the loop header itself
        sb.append("    for (int i = 0; i < result.length; ++i) {\n");
        // load all the indices
        for (Node.Input i : vectorInputs) {
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

    private void buildLoopFooter(StringBuilder sb) {
        //
        for (Node.Input i : inputs) {
            if (i.sizeSameAsResult || i.size == Node.SCALAR)
                continue;
            sb.append("        ++idx" + i.index + ";\n");
            sb.append("        if (idx" + i.index + " == input" + i.index +".length)\n");
            sb.append("            idx" + i.index + " = 0;\n");
        }
        sb.append("    }\n");
    }

    private String buildNACheck(char type, String src) {
        switch (type) {
            case Node.DOUBLE:
                return "RDouble.RDoubleUtils.arithIsNA(" + src + ")";
            case Node.INT:
                return "(" + src + " == RInt.NA)";
            case Node.LOGICAL:
                return "(" + src + " == RLogical.NA)";
            default:
                throw new InvalidSignatureError("Type " + type + " is not supported yet");
        }
    }

    String rType(char type) {
        switch (type) {
            case Node.DOUBLE:
                return "RDouble";
            case Node.INT:
                return "RInt";
            case Node.LOGICAL:
                return "RLogical";
            default:
                throw new InvalidSignatureError("Type " + type + " is not supported yet");
        }
    }

    private void buildNACheck(StringBuilder sb) {
        assert (vectorInputs.size() > 0);
        Node.Input input = vectorInputs.get(0);
        sb.append("        if ("+buildNACheck(ast.type, "t0")+" && (\n               "+buildNACheck(input.type, "in" + input.index));
        for (int i = 1; i < vectorInputs.size(); ++i) {
            input = vectorInputs.get(i);
            sb.append("\n            || "+buildNACheck(input.type, "in"+input.index));
        }
        sb.append(")\n");
        sb.append("            res = " + rType(ast.type) + ".NA;\n");
    }


    // ComputationCodeBuilder --------------------------------------------------------------------------------------

}
