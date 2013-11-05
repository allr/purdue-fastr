package r.fusion;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 11/5/13 Time: 2:40 PM To change this template use File | Settings | File
 * Templates.
 */
public abstract class Node {
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

    public abstract String build(ComputationCodeBuilder cb);

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

        @Override public String build(ComputationCodeBuilder cb) {
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

        @Override public String build(ComputationCodeBuilder cb) {
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

        @Override public String build(ComputationCodeBuilder cb) {
            return cb.buildUnary(this);
        }
    }
}
