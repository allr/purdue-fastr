package r;

import java.util.*;

import r.data.*;
import r.nodes.truffle.*;

public class Truffle {

    @SuppressWarnings("serial") public static class ControlFlowException extends Error {

    }

    public @interface ExplodeLoop {

    }

    public @interface Child {

    }

    public static class CallTarget {

        RNode r;
        RSymbol[] d;
        HashMap<RSymbol, Integer> h;

        public CallTarget(RNode r, HashMap<RSymbol, Integer> h, RSymbol[] d) {
            this.r = r;
            this.d = d;
            this.h = h;
        }

        public Object call(RFrameHeader a) {
            return r.execute(new Frame(d, h, a));
        }

    }

    public static @interface Children {

    }

    @SuppressWarnings("serial") public static class UnexpectedResultException extends Exception {
        Object v;

        public UnexpectedResultException(Object value) {
            v = value;
        }

        public Object getResult() {
            return v;
        }
    }

    public static class Frame {
        private final RFrameHeader arguments;
        protected Object[] locals;
        HashMap<RSymbol, Integer> h;

        public Frame(RSymbol[] d, HashMap<RSymbol, Integer> h, RFrameHeader a) {
            this.h = h;
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
            final Integer i = h.get(symbol);
            if (i == null) return -1;
            else return i;
        }
    }
}
