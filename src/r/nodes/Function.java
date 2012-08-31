package r.nodes;

import r.data.*;
import r.nodes.truffle.*;

public class Function extends ASTNode {

    final ArgumentList signature;
    final ASTNode body;

    RFunction rfunction; // FIXME: is it ok this is not final?

    Function(ArgumentList alist, ASTNode body) {
        this.signature = alist;
        this.body = updateParent(body);
    }

    public RFunction getR() {
        return rfunction;
    }

    public void setR(RFunction rfunction) {
        this.rfunction = rfunction;
    }

    public ArgumentList getSignature() {
        return signature;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public void visit_all(Visitor v) {
        body.accept(v);
    }

    public static ASTNode create(ArgumentList alist, ASTNode body) {
        return new Function(alist, body);
    }

    @Override
    public String toString() {
        // FIXME: real R remembers the expression string for this
        StringBuilder str = new StringBuilder();
        str.append("function (");
        boolean first = true;
        for (ArgumentList.Entry a : signature) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(a.getName().pretty());
            ASTNode exp = a.getValue();
            if (exp != null) {
                str.append("=");
                str.append(exp.toString());
            }
        }
        str.append(") {");
        str.append(body.toString());
        str.append("}");
        return str.toString();
    }
}
