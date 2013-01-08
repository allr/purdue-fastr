package r.nodes;

public abstract class BinaryOperation extends Operation {
    ASTNode rhs;

    public BinaryOperation(ASTNode left, ASTNode right) {
        super(left);
        setRHS(right);
    }

    public ASTNode getRHS() {
        return rhs;
    }

    public void setRHS(ASTNode right) {
        this.rhs = updateParent(right);
    }

    @Override
    public void visit_all(Visitor v) {
        super.visit_all(v);
        getRHS().accept(v);
    }

    public static ASTNode create(BinaryOperator op, ASTNode left, ASTNode right) {
        switch (op) {
            case ADD: return new Add(left, right);
            case SUB: return new Sub(left, right);
            case MULT: return new Mult(left, right);
            case DIV: return new Div(left, right);
            case MOD: return new Mod(left, right);
            case POW: return new Pow(left, right);

            case OR: return new Or(left, right);
            case AND: return new And(left, right);
            case ELEMENTWISEOR: return new ElementwiseOr(left, right);
            case ELEMENTWISEAND: return new ElementwiseAnd(left, right);

            case EQ: return new EQ(left, right);
            case GE: return new GE(left, right);
            case GT: return new GT(left, right);
            case NE: return new NE(left, right);
            case LE: return new LE(left, right);
            case LT: return new LT(left, right);

            case COLON : return new Colon(left, right);
        }
        throw new Error("No node implemented for: '" + op + "' (" + left + ", " + right + ")");
    }

    public static ASTNode create(String op, ASTNode left, ASTNode right) {
        if ("%o%".equals(op)) {
            return new OuterMult(left, right);
        }
        if ("%*%".equals(op)) {
            return new MatMult(left, right);
        }
        if ("%/%".equals(op)) {
            return new IntegerDiv(left, right);
        }
        if ("%in%".equals(op)) {
            return new In(left, right);
        }
        return null;
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
        COLON,

        GE, GT,
        LE, LT,
        EQ, NE,

        OR,
        ELEMENTWISEOR,
        AND,
        ELEMENTWISEAND,
    }
}
