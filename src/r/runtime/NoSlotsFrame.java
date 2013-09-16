package r.runtime;

import r.*;
import r.data.*;

public class NoSlotsFrame extends Frame {

    public NoSlotsFrame(RFunction function, Frame enclosingFrame) {
        super(function, enclosingFrame);
    }

    public static final FrameDescriptor NO_SLOTS_DESCRIPTOR = new FrameDescriptor(new RSymbol[0]);

    @Override
    public FrameDescriptor descriptor() {
        return NO_SLOTS_DESCRIPTOR;
    }

    @Override
    public Object get(int i) {
        assert Utils.check(false, "frame has no slots");
        return null;
    }

    @Override
    public void set(int i, Object value) {
        assert Utils.check(false, "frame has no slots");
    }

}
