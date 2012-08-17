package r.nodes;

import java.io.*;

import r.*;
import r.data.*;
import r.nodes.tools.*;

public class Function extends Node {

    final ArgumentList signature;
    final Node body;

    Function(ArgumentList alist, Node body) {
        this.signature = alist;
        this.body = updateParent(body);
    }

    Closure getClosure(RFrame env) {
        return new Closure(env);
    }

    public ArgumentList getSignature() {
        return signature;
    }

    public Node getBody() {
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
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            new PrettyPrinter(new PrintStream(os)).print(Function.this);
            return os.toString();
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
    }

    public static Node create(ArgumentList alist, Node body) {
        return new Function(alist, body);
    }
}
