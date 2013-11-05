package r;

import r.data.*;
import r.data.internal.View;

import java.util.*;

/** View fusion manager.
 */
public class Fusion {

    /** Prototype class for all fusion operators.
     *
     * Each single fusion operator is capable of executing views of certain signature in a fused way with single loop
     * and grouped NA checks.
     *
     * Most subclasses of fusion operator prototype are dynamically generated at runtime for particular views observed
     * during the program execution.
     */
    public abstract static class Prototype {

        /** Rebounds the fusion operator to the new view.
         *
         * In practice this should consist of walking a view visitor collecting the inputs for the particular fusion
         * operator.
         */
        protected abstract void reinitialize(View view);

        /** Nullifies all inputs to the view so that they can be garbage collected if not referenced elsewhere.
         */
        protected abstract void free();

        /** Performs the fused computation of the view and returns the result.
         */
        protected abstract RArray materialize_();

        /** Bounds the fusion operator to the given view, computes and returns the result and calls the free() method to
         * allow potential garbage collection of the inputs.
         */
        public final RArray materialize(View view) {
            reinitialize(view);
            RArray result = materialize_();
            free();
            return result;
        }
    }

    // Fusion caching --------------------------------------------------------------------------------------------------

    /** HashMap containing created fusion operators and their respective view signatures.
     */
    static final HashMap<String, Prototype> operators = new HashMap<>();

    /** Materializes the given view.
     *
     * A signature of the view is obtained. An empty signature means that the view contains elements that cannot be
     * fused (automatically), in which case the view is materialized in the standard way.
     *
     * Otherwise the cache of existing fusion operators is scanned for given signature and if a match is found, the
     * fused operation is used to materialize the view.
     *
     * If a fused operation for given signature is not found, it can either be created, or the view is materialized
     * in the standard way.
     *
     * @param view View to be materialized
     * @return Materialized contents of the view.
     */
    public static RArray materialize(View view) {
        String signature = view.signature();
        assert (signature != null);
        if (signature.isEmpty())
            return view.materialize();
        Prototype fusion = operators.get(signature);
        if (fusion != null)
            return fusion.materialize(view);
        // add a fusion operator, or materialize the view directly
        // TODO add fusion creation logic
        return view.materialize();
    }

    // Fusion operator generation --------------------------------------------------------------------------------------

    /** Signatures -- the signatures should be as simple as possible
     *
     * operator arity (B, U)
     * operator type ( +, -, ...)
     * result size (S, A, B, E)
     * left operand size (V, S)
     * left operand type (D, I, C, L)
     * right operand size (V, S)
     * right operand type (D, I, C, L)
     * left operand
     * right operand
     */


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
    public static class FusionBuilder {

        public static final char BINARY = 'B';
        public static final char UNARY = 'U';
        public static final char INPUT = '_';
        public static final char ADD = '+';
        public static final char SUB = '-';
        public static final char MUL = '*';
        public static final char DIV = '/';
        public static final char MOD = '%';
        public static final char SIZEA = 'A';
        public static final char SIZEB = 'B';
        public static final char EQUALSIZE = 'E';
        public static final char VECTOR = 'V';
        public static final char SCALAR = 'S';
        public static final char DOUBLE = 'D';
        public static final char INT = 'I';
        public static final char LOGICAL = 'L';
        public static final char COMPLEX = 'C';


        public class InvalidSignatureError extends Error {
            public InvalidSignatureError(String message) {
                super(message);
            }
        }

        Vector<Node.Input> inputs = new Vector<>();
        Vector<Node.Input> vectorInputs = new Vector<>();

        private final String signature;
        private int idx;
        private Node ast;
        /** Index of input that is the same size as the result.
         */
        private int resultSize;


        public FusionBuilder(String signature) {
            idx = 0;
            this.signature = signature;
            ast = parse();
            resultSize = ast.calculateResultSize();
            ast.setSizeSameAsResult();
        }


        // view ast nodes ----------------------------------------------------------------------------------------------

        public static abstract class Node {

            public final char size;
            public final char type;

            protected Node(char size, char type) {
                this.size = size;
                this.type = type;
            }

            public String signature() {
                return String.valueOf(size) + String.valueOf(type);
            }

            public abstract int calculateResultSize();

            public abstract void setSizeSameAsResult();

            public abstract String build(CodeBuilder cb);

            // input node ----------------------------------------------------------------------------------------------

            public static class Input extends Node {
                public boolean sizeSameAsResult = false;
                public final int index;

                public Input(char size, char type, int index) {
                    super(size, type);
                    this.index = index;
                }

                @Override public String toString() {
                    return String.valueOf(size) + "[" + index +"]";
                }

                @Override public String signature() {
                    return "_" + super.signature();
                }

                @Override public int calculateResultSize() {
                    return size == VECTOR ?  index : -1;
                }

                @Override public void setSizeSameAsResult() {
                    sizeSameAsResult = true;
                }

                @Override public String build(CodeBuilder cb) {
                    return cb.buildInput(this);
                }
            }

            // binary operator node ------------------------------------------------------------------------------------

            public static class Binary extends Node {
                public final char resultSize;
                public final char op;
                public final Node left;
                public final Node right;

                public Binary(char resultSize, char size, char type, char op, Node left, Node right) {
                    super(size, type);
                    this.resultSize = resultSize;
                    this.op = op;
                    this.left = left;
                    this.right = right;
                }
                @Override public String toString() {
                    return "(" + left.toString() + String.valueOf(op) + right.toString() + ")";
                }

                @Override public String signature() {
                    return "B" + String.valueOf(op) + super.signature() + String.valueOf(resultSize) + left.signature() + right.signature();
                }

                @Override public int calculateResultSize() {
                    switch (resultSize) {
                        case EQUALSIZE:
                        case SIZEA:
                            return left.calculateResultSize();
                        case SIZEB:
                            return right.calculateResultSize();
                        case SCALAR:
                        default: // no other option is possible
                            return -1; // for scalars
                    }
                }

                @Override public void setSizeSameAsResult() {
                    switch (resultSize) {
                        case EQUALSIZE:
                            left.setSizeSameAsResult();
                            right.setSizeSameAsResult();
                            break;
                        case SIZEA:
                            left.setSizeSameAsResult();
                            break;
                        case SIZEB:
                            right.setSizeSameAsResult();
                            break;
                        default:
                            // pass
                    }
                }

                @Override public String build(CodeBuilder cb) {
                    return cb.buildBinary(this);
                }
            }

            // unary operator node -------------------------------------------------------------------------------------

            public static class Unary extends Node {
                public final char op;
                public final Node operand;
                public Unary(char size, char type, char op, Node operand) {
                    super(size, type);
                    this.op = op;
                    this.operand = operand;
                }
                @Override public String toString() {
                    return String.valueOf(op) + operand.toString();
                }

                @Override public String signature() {
                    return "U" + String.valueOf(op) + super.signature() + operand.signature();
                }

                @Override public int calculateResultSize() {
                    return operand.calculateResultSize();
                }

                @Override public void setSizeSameAsResult() {
                    operand.setSizeSameAsResult();
                }

                @Override public String build(CodeBuilder cb) {
                    return cb.buildUnary(this);
                }
            }
        }

        // view ast parsing --------------------------------------------------------------------------------------------

        private Node parse() {
            char x = signature.charAt(idx);
            switch (x) {
                case INPUT:
                    return parseInput();
                case BINARY:
                    return parseBinary();
                case UNARY:
                    return parseUnary();
                default:
                    throw new InvalidSignatureError("Invalid character");
            }
        }

        private char parseSize() {
            char r = signature.charAt(idx);
            if ((r != VECTOR) && (r != SCALAR))
                throw new InvalidSignatureError("Invalid size");
            ++idx;
            return r;
        }

        private char parseResultSize() {
            char r = signature.charAt(idx);
            if ((r != EQUALSIZE) && (r != SIZEA) && (r != SIZEB) && (r != SCALAR))
                throw new InvalidSignatureError("Invalid result size");
            ++idx;
            return r;
        }

        private char parseType() {
            char r = signature.charAt(idx);
            if ((r != DOUBLE) && (r != INT) && (r != LOGICAL) && (r != COMPLEX))
                throw new InvalidSignatureError("Invalid type character");
            ++idx;
            return r;
        }

        private char parseBinaryOperator() {
            char r = signature.charAt(idx);
            if ((r != ADD) && (r != SUB) && (r != MUL) && (r != DIV) && (r != MOD))
                throw new InvalidSignatureError("Invalid binary operator type");
            ++idx;
            return r;
        }

        private char parseUnaryOperator() {
            char r = signature.charAt(idx);
            if ((r != SUB))
                throw new InvalidSignatureError("Invalid unary operator type");
            ++idx;
            return r;
        }

        private Node parseInput() {
            ++idx;
            char size = parseSize();
            char type = parseType();
            Node.Input result = new Node.Input(size, type, inputs.size());
            inputs.add(result);
            if (size == VECTOR)
                vectorInputs.add(result);
            return result;
        }

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

        private Node parseUnary() {
            ++idx;
            char op = parseUnaryOperator();
            char size = parseSize();
            char type = parseType();
            Node operand = parse();
            return new Node.Unary(size, type, op, operand);
        }

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
            CodeBuilder cb = new CodeBuilder();
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
            if (ast.size != VECTOR)
                throw new InvalidSignatureError("Result of the view must be a vector. Scalars are not supported.");
            switch (ast.type) {
                case DOUBLE:
                    sb.append("    double[] result = new double[input" + resultSize + ".length];\n");
                    break;
                case INT:
                    sb.append("    int[] result = new int[input" + resultSize + ".length];\n");
                    break;
                case COMPLEX:
                    // TODO complex results
                    assert (false);
                    break;
                default:
                    throw new InvalidSignatureError("Result type of the view not supported (Double, Integer, Complex)");
            }
        }

        private void buildLoopHeader(StringBuilder sb) {
            // generate indices for inputs that are not of the same size as the result
            for (Node.Input i : inputs) {
                if (i.sizeSameAsResult || i.size == SCALAR)
                    continue;
                sb.append("    int idx" + i.index + " = 0;\n");
            }
            // generate the loop header itself
            sb.append("    for (int i = 0; i < result.length; ++i) {\n");
            // load all the indices
            for (Node.Input i : vectorInputs) {
                switch (i.type) {
                    case DOUBLE:
                        sb.append("        double in"+i.index+" = input"+i.index+"["+ (i.sizeSameAsResult ? "i" : "idx"+i.index) + "];\n");
                        break;
                    case INT:
                        sb.append("        int in"+i.index+" = input"+i.index+"["+ (i.sizeSameAsResult ? "i" : "idx"+i.index) + "];\n");
                        break;

                    default:
                        assert (false);
                }
            }
        }

        private void buildLoopFooter(StringBuilder sb) {
            //
            for (Node.Input i : inputs) {
                if (i.sizeSameAsResult || i.size == SCALAR)
                    continue;
                sb.append("        ++idx" + i.index + ";\n");
                sb.append("        if (idx" + i.index + " == input" + i.index +".length)\n");
                sb.append("            idx" + i.index + " = 0;\n");
            }
            sb.append("    }\n");
        }

        private String buildNACheck(char type, String src) {
            switch (type) {
                case DOUBLE:
                    return "RDouble.RDoubleUtils.arithIsNA(" + src + ")";
                case INT:
                    return "(" + src + " == RInt.NA)";
                case LOGICAL:
                    return "(" + src + " == RLogical.NA)";
                default:
                    assert (false);
                    return "";
            }
        }

        static String rType(char type) {
            switch (type) {
                case DOUBLE:
                    return "RDouble";
                case INT:
                    return "RInt";
                case LOGICAL:
                    return "RLogical";
                default:
                    assert (false);
                    return "";
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






        class CodeBuilder {
            int freeTemp = 0;
            StringBuilder sb = new StringBuilder();

            public String emit() {
                ast.build(this);
                return sb.toString();
            }

            public final String buildInput(Node.Input input) {
                return (input.size == SCALAR ? "input" : "in") + input.index;
            }


            public final void addResultDeclaration(char type, String result) {
                switch (type) {
                    case DOUBLE:
                        sb.append("double "+result+";\n");
                        break;
                    case INT:
                        sb.append("int "+result+";\n");
                        break;
                    case COMPLEX:
                        // TODO fix complex
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
                    case ADD:
                        add(binary, result, left, right);
                        break;
                    case SUB:
                        sub(binary, result, left, right);
                        break;
                    case MUL:
                        mul(binary, result, left, right);
                        break;
                    case DIV:
                        div(binary, result, left, right);
                        break;
                    case MOD:
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
                    case SUB:
                        minus(unary, result, operand);
                        break;
                    default:
                        throw new InvalidSignatureError("Unsupported unary operator " + unary.op);
                }
                return result;
            }


            // TODO complete the operations for the different types and operators

            public final void add(Node.Binary node, String result, String left, String right) {
                if ((node.left.type == DOUBLE || node.left.type == INT) && (node.right.type == DOUBLE || node.right.type == INT)) {
                    sb.append(result + " = " + left + " + " + right + ";\n");
                } else {
                    throw new InvalidSignatureError("Unsupported combination of add operand types " + node.left.type + ", " + node.right.type);
                }
            }

            public final void sub(Node.Binary node, String result, String left, String right) {
                if ((node.left.type == DOUBLE || node.left.type == INT) && (node.right.type == DOUBLE || node.right.type == INT)) {
                    sb.append(result + " = " + left + " - " + right + ";\n");
                } else {
                    throw new InvalidSignatureError("Unsupported combination of sub operand types " + node.left.type + ", " + node.right.type);
                }

            }

            public final void mul(Node.Binary node, String result, String left, String right) {
                if ((node.left.type == DOUBLE || node.left.type == INT) && (node.right.type == DOUBLE || node.right.type == INT)) {
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





        }

    }

}
