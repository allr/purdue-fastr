package r.runtime;

import r.data.*;

public class GenericFrame extends Frame {

    Object[] variables;

    FrameDescriptor descriptor;

    public GenericFrame(RFunction function, Frame enclosingFrame, FrameDescriptor descriptor) {
        super(function, enclosingFrame);
        variables = new Object[descriptor.numberOfSlots()];
        this.descriptor = descriptor;
    }

    @Override
    public FrameDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public Object get(int i) {
        return variables[i];
    }

    @Override
    public void set(int i, Object value) {
        variables[i] = value;
    }

}
