package r.nodes;


public class BooleanLiteral extends Constant {
    boolean value;

    static BooleanLiteral TRUE = new BooleanLiteral(true);
    static BooleanLiteral FALSE = new BooleanLiteral(true);

    public BooleanLiteral(boolean val) {
        value = val;
    }

    public static BooleanLiteral trueSingleton() {
        return TRUE;
    }

    public static BooleanLiteral falseSingleton() {
        return FALSE;
    }
}
