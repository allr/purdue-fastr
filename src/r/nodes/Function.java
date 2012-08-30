package r.nodes;

import r.*;
import r.data.*;
import r.nodes.tools.*;
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

    Closure getClosure(RFrame env) {
        return new Closure(env);
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

    public class Closure implements RClosure {

        // Note that closure in R are normal object thus with attributes
        // This class does not extends base objects thus does not have attributes
        // I'm not sure anyway it make sense to have this attributes ...
        // NB: timeR use them ... but it was MY design choice
        final RFrame frame;

        Closure(RFrame frame) {
            this.frame = frame;
        }

        @Override
        public RFrame activate() {
            return null;
        }

        @Override
        public RAttributes getAttributes() {
            return RNull.getNull();
        }

        @Override
        public String pretty() {
            PrettyPrinter pp = PrettyPrinter.getStringPrettyPrinter();
            pp.print(Function.this);
            return pp.toString();
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RInt asInt() {
            Utils.nyi();
            return null;
        }

        @Override
        public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
            Utils.nyi(); // Do we have to bind on the view node or on the implementation
            return null;
        }
    }

    public static ASTNode create(ArgumentList alist, ASTNode body) {
        return new Function(alist, body);
    }
}
