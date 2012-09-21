package r.nodes;

import r.builtins.*;
import r.data.*;

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
            case DIV: return new Add(left, right);
            case MOD: return new Add(left, right);
            case POW: return new Add(left, right);

            case OR: return new Add(left, right);
            case AND: return new Add(left, right);
            case BITWISEOR: return new Add(left, right);
            case BITWISEAND: return new Add(left, right);

            case EQ: return new EQ(left, right);
            case GE: return new GE(left, right);
            case GT: return new GT(left, right);
            case NE: return new NE(left, right);
            case LE: return new LE(left, right);
            case LT: return new LT(left, right);

            case COLUMN : return new Colon(left, right);
        }
        throw new Error("No node implemented for: '" + op + "' (" + left + ", " + right + ")");
    }

    public static ASTNode create(String op, ASTNode left, ASTNode right) {
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
