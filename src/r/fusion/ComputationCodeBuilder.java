package r.fusion;

/** Class which builds the computation code for the view.
 *
 * Essentially performs a visitor over the view ast nodes created by the parse step above.
 *
 *
 */
public class ComputationCodeBuilder {

    int freeTemp = 0;
    StringBuilder sb = new StringBuilder();
    private FusionBuilder fusionBuilder;

    public ComputationCodeBuilder(FusionBuilder fusionBuilder) {
        this.fusionBuilder = fusionBuilder;
    }

    public String emit() {
        fusionBuilder.ast.build(this);
        return sb.toString();
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
                throw fusionBuilder.new InvalidSignatureError("Type " + type + " is not supported yet");
            default:
                throw fusionBuilder.new InvalidSignatureError("Unsupported operator result type " + type);
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
                throw fusionBuilder.new InvalidSignatureError("Unsupported binary operator " + binary.op);
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
                throw fusionBuilder.new InvalidSignatureError("Unsupported unary operator " + unary.op);
        }
        return result;
    }


    // TODO complete the operations for the different types and operators

    public final void add(Node.Binary node, String result, String left, String right) {
        if ((node.left.type == Node.DOUBLE || node.left.type == Node.INT) && (node.right.type == Node.DOUBLE || node.right.type == Node.INT)) {
            sb.append(result + " = " + left + " + " + right + ";\n");
        } else {
            throw fusionBuilder.new InvalidSignatureError("Unsupported combination of add operand types " + node.left.type + ", " + node.right.type);
        }
    }

    public final void sub(Node.Binary node, String result, String left, String right) {
        if ((node.left.type == Node.DOUBLE || node.left.type == Node.INT) && (node.right.type == Node.DOUBLE || node.right.type == Node.INT)) {
            sb.append(result + " = " + left + " - " + right + ";\n");
        } else {
            throw fusionBuilder.new InvalidSignatureError("Unsupported combination of sub operand types " + node.left.type + ", " + node.right.type);
        }

    }

    public final void mul(Node.Binary node, String result, String left, String right) {
        if ((node.left.type == Node.DOUBLE || node.left.type == Node.INT) && (node.right.type == Node.DOUBLE || node.right.type == Node.INT)) {
            sb.append(result + " = " + left + " * " + right + ";\n");
        } else {
            throw fusionBuilder.new InvalidSignatureError("Unsupported combination of mul operand types " + node.left.type + ", " + node.right.type);
        }

    }

    public final void div(Node.Binary node, String result, String left, String right) {
        sb.append(result + " = " + left + " / " + right + ";\n");


    }

    public final void mod(Node.Binary node, String result, String left, String right) {

    }

    public final void minus(Node.Unary node, String result, String operand) {

    }





}
