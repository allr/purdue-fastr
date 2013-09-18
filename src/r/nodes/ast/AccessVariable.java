package r.nodes.ast;

import r.data.*;

public abstract class AccessVariable extends ASTNode {

    public static ASTNode create(String name) {
        return new SimpleAccessVariable(RSymbol.getSymbol(name));
    }
}
