package r.nodes;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;

public abstract class ASTNode {

    ASTNode parent;

    public abstract void accept(Visitor v);

    public abstract void visit_all(Visitor v);

    public int getPrecedence() {
        Class< ? > clazz = getClass();
        Precedence prec = clazz.getAnnotation(Precedence.class);
        return prec == null ? Precedence.MIN : prec.value();
    }

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode node) {
        parent = node;
    }

    protected <T extends ASTNode> T[] updateParent(T[] children) {
        for (T node : children) {
            updateParent(node);
        }
        return children;
    }

    protected <T extends ASTNode> T updateParent(T child) {
        if (child != null) {
            child.setParent(this);
        }
        return child;
    }

    // FIXME should be abstract ... but I'm too lazy
    public RAny execute(RContext global, Frame frame) {
        return RNull.getNull();
    }
}
