package r.nodes.truffle;

import r.nodes.*;


public abstract class BaseRNode extends RNode {
    ASTNode ast;


    public BaseRNode(ASTNode orig) {
        ast = orig;
    }

    @Override
    public ASTNode getAST() {
        return ast == null ? super.getAST() : ast;
    }

}
