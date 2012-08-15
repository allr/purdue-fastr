package r.nodes;

import java.util.*;


public abstract class Call extends Node {
    public static Node create(Node call, Map<Symbol, Node> args) {
        if (call instanceof Constant) {
            Constant ccall = (Constant) call;
            if (ccall.value instanceof Symbol) {
                create(call, args);
            }
            throw new Error(ccall.value.pretty() + " can't be use as a function name");
        }
        return null;
    }
    public static Node create(Symbol funName, Map<Symbol, Node> args) {
        return null;
    }
    public static Node create(CallOperator op, Node lhs, Map<Symbol, Node> args) {
        return null;
    }
    public enum CallOperator {
        SUBSET,
        SUBSCRIPT
    }
}
