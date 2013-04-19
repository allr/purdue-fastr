package r;

import r.data.*;
import r.nodes.truffle.Arithmetic.FailedSpecialization;
import r.nodes.truffle.*;

public class Truffle {

    public static class ControlFlowException extends Error {

    }

    public @interface ExplodeLoop {

    }

    public @interface Child {

    }

    public static class CallTarget {

        RNode r;
        RSymbol[] d;

        public CallTarget(RNode r, RSymbol[] d) {
            this.r = r;
            this.d = d;
        }

        public Object call(Frame caller, RFrameHeader a) {
            return r.execute(new Frame(d, caller, a));
        }

        public Object call(RFrameHeader a) {
            return r.execute(new Frame(d, null, a));
        }

    }

    public static @interface Children {

    }

    public static class UnexpectedResultException extends Exception {
        Object v;

        public UnexpectedResultException(Object value) {
            v = value;
        }

        public Object getResult() {
            return v;
        }
    }

    public static class Frame {
        private final Frame caller;
        private final RFrameHeader arguments;
        protected Object[] locals;
        RSymbol[] names;

        public Frame(RSymbol[] d, Frame f, RFrameHeader a) {
            names = d;
            this.caller = f;
            this.arguments = a;
            this.locals = new Object[d.length];
        }

        public RFrameHeader getArguments() {
            return arguments;
        }

        public void setObject(int i, RAny v) {
            locals[i] = v;
        }

        public Object getObject(int i) {
            return locals[i];
        }

        public int find(RSymbol symbol) {
            for (int i = 0; i < names.length; i++)
                if (symbol == names[i]) return i;
            return -1;
        }
    }
}
