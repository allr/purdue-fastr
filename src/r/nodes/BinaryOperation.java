package r.nodes;

public abstract class BinaryOperation extends Operation {
    Node rhs;

    public BinaryOperation(Node left, Node right) {
        super(left);
        setRHS(right);
    }

    public Node getRHS() {
        return rhs;
    }

    public void setRHS(Node right) {
        this.rhs = updateParent(right);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
        getRHS().accept(v);
    }

    public static Node create(BinaryOperator op, Node left, Node right) {
        switch (op) {
            case ADD: return new Add(left, right);
            case SUB: return new Add(left, right);
            case MULT: return new Mult(left, right);
            case DIV: return new Add(left, right);
            case MOD: return new Add(left, right);
            case POW: return new Add(left, right);

            case OR: return new Add(left, right);
            case AND: return new Add(left, right);
            case BITWISEOR: return new Add(left, right);
            case BITWISEAND: return new Add(left, right);

            case EQ: return new Add(left, right);
            case GE: return new Add(left, right);
            case GT: return new Add(left, right);
            case NE: return new Add(left, right);
            case LE: return new Add(left, right);
            case LT: return new Add(left, right);
        }
        throw new Error("No node implemented for: '" + op + "' (" + left + ", " + right + ")");
    }

    public static Node create(String op, Node left, Node right) {
        return null;
//        throw new Error("Custom operator not implemented: '" + op + "' (" + left + ", " + right + ")");
    }

    public enum BinaryOperator {
        ASSIGN,
        SUPER_ASSIGN,

        ADD,
        SUB,
        MULT,
        DIV,
        MOD,

        POW,

        MODEL,
        COLUMN,

        GE, GT,
        LE, LT,
        EQ, NE,

        OR,
        BITWISEOR,
        AND,
        BITWISEAND,
    }
}
