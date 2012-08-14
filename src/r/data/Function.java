package r.data;

import r.nodes.*;

import com.oracle.truffle.runtime.*;


public class Function {
    final Function parent;
    final Signature signature;
    final Node body;

    Function(Signature sig, Node body, Function parent) {
        this.parent = parent;
        this.signature = sig;
        this.body = body;
    }

    Closure getClosure(Frame env) {
        return new Closure(env);
    }

    public class Closure implements Node {
        final Frame frame;

        Closure(Frame frame) {
            this.frame = frame;
        }

        Frame activate() {
            return null;
        }

        @Override
        public void accept(Visitor v) {
            body.accept(v);
            v.visit(this);
        }
    }
}
