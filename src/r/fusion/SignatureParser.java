package r.fusion;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 11/5/13 Time: 2:48 PM To change this template use File | Settings | File
 * Templates.
 */
public class SignatureParser {

    final FusionBuilder fb;
    int idx = 0;

    public SignatureParser(FusionBuilder fb) {
        this.fb = fb;
    }

    static Node parse(FusionBuilder fb) {
        return new SignatureParser(fb).parse();
    }

    /** Parses the type of the input in the signature.
     */
    private Node parse() {
        char x = fb.signature.charAt(idx);
        switch (x) {
            case Node.INPUT:
                return parseInput();
            case Node.BINARY:
                return parseBinary();
            case Node.UNARY:
                return parseUnary();
            default:
                throw new InvalidSignatureError("Invalid character", idx);
        }
    }

    /** Parses the data size (scalar / vector) in the signature).
     */
    private char parseSize() {
        char r = fb.signature.charAt(idx);
        if ((r != Node.VECTOR) && (r != Node.SCALAR))
            throw new InvalidSignatureError("Invalid size", idx);
        ++idx;
        return r;
    }

    /** Parses the result size (equal, from left, from right, scalar) from the signature.
     *
     * The scalar value for the result is parsed correctly, but later stages of the build progress will error on it
     * as scalar operations are not yet supported in the views and fused operators.
     */
    private char parseResultSize() {
        char r = fb.signature.charAt(idx);
        if ((r != Node.EQUALSIZE) && (r != Node.SIZEA) && (r != Node.SIZEB) && (r != Node.SCALAR))
            throw new InvalidSignatureError("Invalid result size", idx);
        ++idx;
        return r;
    }

    /** Parses the type of the data (input, or temporary) from the signature.
     *
     * Double, integer and complex views are supported. Logical is parsed too, but will cause an error later in the
     * build process.
     */
    private char parseType() {
        char r = fb.signature.charAt(idx);
        if ((r != Node.DOUBLE) && (r != Node.INT) && (r != Node.LOGICAL) && (r != Node.COMPLEX))
            throw new InvalidSignatureError("Invalid type character", idx);
        ++idx;
        return r;
    }

    /** Parses the binary operator type from the signature.
     */
    private char parseBinaryOperator() {
        char r = fb.signature.charAt(idx);
        if ((r != Node.ADD) && (r != Node.SUB) && (r != Node.MUL) && (r != Node.DIV) && (r != Node.MOD))
            throw new InvalidSignatureError("Invalid binary operator type", idx);
        ++idx;
        return r;
    }

    /** Parses the unary operator type from the signature.
     */
    private char parseUnaryOperator() {
        char r = fb.signature.charAt(idx);
        if ((r != Node.SUB))
            throw new InvalidSignatureError("Invalid unary operator type", idx);
        ++idx;
        return r;
    }

    /** Parses the input (leaf of the view) from the signature.
     *
     * Input is characterized by its size and type.
     */
    private Node parseInput() {
        ++idx;
        char size = parseSize();
        char type = parseType();
        Node.Input result = new Node.Input(size, type, fb.inputs.size());
        fb.inputs.add(result);
        if (size == Node.VECTOR)
            fb.vectorInputs.add(result);
        return result;
    }

    /** Parses the binary operator from the signature.
     *
     * Binary operator is characterized by the operator type, result size and type and information where to get the
     * result size from (both inputs are of the same size, from left, or from right, possibily scalar too).
     *
     * The left and right operand descriptions in prefix form follow.
     */
    private Node parseBinary() {
        ++idx;
        char op = parseBinaryOperator();
        char size = parseSize();
        char type = parseType();
        char resultSize = parseResultSize();
        Node left = parse();
        Node right = parse();
        return new Node.Binary(resultSize, size, type, op, left, right);
    }

    /** Parses the unary operator from the signature.
     *
     * The unary operator is characterized by the operator type, result size and type. The operand description
     * follows in the sugnature.
     */
    private Node parseUnary() {
        ++idx;
        char op = parseUnaryOperator();
        char size = parseSize();
        char type = parseType();
        Node operand = parse();
        return new Node.Unary(size, type, op, operand);
    }

}
