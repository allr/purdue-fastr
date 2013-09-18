package r.nodes.exec;

import r.nodes.ast.*;


public abstract class BaseR extends RNode {
    protected final ASTNode ast;


    public BaseR(ASTNode orig) {
        ast = orig;
    }

    @Override
    public final ASTNode getAST() {
        return ast == null ? super.getAST() : ast;
    }

}
