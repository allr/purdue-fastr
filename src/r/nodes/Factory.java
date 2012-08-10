package r.nodes;

import java.util.*;

public class Factory {
    public static Constant createConstant(int... value) {
        if (value.length == 1) {
            return IntegerLiteral.getInteger();
        }
        throw new Error("Non scalar constants are not implemented.");
    }
    public static BooleanLiteral createConstant(boolean... value) {
        if (value.length == 1) {
            if (value[0]) {
                return BooleanLiteral.trueSingleton();
            } else {
                return BooleanLiteral.falseSingleton();
            }
        }
        throw new Error("Non scalar constants are not implemented.");
    }

    public static Node unary(UnaryOperator op, Node operand) {
        switch (op) {
            case PLUS: return new Not(operand);
            case MINUS: return new Not(operand);
            case NOT: return new Not(operand);

            case REPEAT: return new Not(operand);
        }
        throw new Error("No node implemented for: '" + op + "' (" + operand + ")");
    }

    public static Node binary(BinaryOperator op, Node left, Node right) {
        switch (op) {
            case ADD: return new Add(left, right);
            case SUB: return new Add(left, right);
            case MULT: return new Add(left, right);
            case DIV: return new Add(left, right);
            case MOD: return new Add(left, right);
            case POW: return new Add(left, right);

            case AND: return new Add(left, right);
            case BITWISEAND: return new Add(left, right);
            case OR: return new Add(left, right);
            case BITWISEOR: return new Add(left, right);

            case EQ: return new Add(left, right);
            case GE: return new Add(left, right);
            case GT: return new Add(left, right);
            case NE: return new Add(left, right);
            case LE: return new Add(left, right);
            case LT: return new Add(left, right);

            case IF: return new If(left, right, null);
            case WHILE: return new Add(left, right);
            case ASSIGN: return new Add(left, right);
            case SUPER_ASSIGN: return new Add(left, right);

            case FIELD: return new Add(left, right);
            case AT: return new Add(left, right);

            case COLUMN: return new Add(left, right);
        }

        throw new Error("No node implemented for: '" + op + "' (" + left + ", " + right + ")");
    }

    public static Node ternary(TernaryOperator op, Node first, Node scnd, Node third) {
        switch (op) {
            case IF: return new If(first, first, third);
        }
        throw new Error("No node implemented for: '" + op + "' (" + first + ", " + scnd + ", " + third + ")");
    }
    public static Node sequence(ArrayList<Node> exprs) {
        return new Sequence(exprs.toArray(new Node[exprs.size()]));
    }
    public static Node sequence(Node[] exprs) {
        return new Sequence(exprs);
    }

    public static Node call(CallOperator op, Node lhs, Map<Id, Node> args) {
        return null;
    }
    public static Node custom_operator(Object op, Node left, Node right) {
        return null;
//        throw new Error("Custom operator not implemented: '" + op + "' (" + left + ", " + right + ")");
    }
}
