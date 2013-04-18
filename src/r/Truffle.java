package r;

import r.data.*;
import r.data.internal.*;
import r.nodes.truffle.Arithmetic.FailedSpecialization;
import r.nodes.truffle.*;

public class Truffle {

    public abstract static class RootNode extends RNode {

    }

    public static class ControlFlowException extends Error {

    }

    public @interface ExplodeLoop {

    }

    public class VirtualFrame extends Frame {

        public void setObject(FrameSlot frameSlot, RAny value) {
            // TODO Auto-generated method stub

        }

    }

    public @interface Child {

    }

    public static class CallTarget {

        public Object call(RFrameHeader arguments) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class FrameSlot {

    }

    public static class Arguments {

    }

    public static class FrameDescriptor {

        public FrameSlot findFrameSlot(RSymbol symbol) {
            // TODO Auto-generated method stub
            return null;
        }

        public FrameSlot addFrameSlot(RSymbol rSymbol) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static @interface Children {

    }

    public static Runtime getRuntime() {
        return rt;
    }

    static Runtime rt = new Runtime();

    public static class Runtime {

        public MaterializedFrame createMaterializedFrame(RFrameHeader header) {
            // TODO Auto-generated method stub
            return null;
        }

        public CallTarget createCallTarget(FunctionImpl functionImpl, FrameDescriptor frameDescriptor) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class UnexpectedResultException extends Exception {

        public UnexpectedResultException(Object value) {
            // TODO Auto-generated constructor stub
        }

        public UnexpectedResultException(FailedSpecialization fixedType) {
            // TODO Auto-generated constructor stub
        }

        public Object getResult() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class Frame {

        public MaterializedFrame materialize() {
            // TODO Auto-generated method stub
            return null;
        }

        public RFrameHeader getArguments() {
            // TODO Auto-generated method stub
            return null;
        }

        public FrameDescriptor getFrameDescriptor() {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getObject(FrameSlot slot) {
            // TODO Auto-generated method stub
            return null;
        }

        public void setObject(FrameSlot slot, Object value) {
            // TODO Auto-generated method stub

        }
    }

    public static class MaterializedFrame extends Frame {}
}
