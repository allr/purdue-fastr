package r.nodes;

import r.data.*;

public abstract class AccessVariable extends Node {

    public static Node create(String name) {
        return new SimpleAccessVariable(RSymbol.getSymbol(name));
    }
}
