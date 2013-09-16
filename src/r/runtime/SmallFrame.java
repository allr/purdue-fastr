package r.runtime;

import r.*;
import r.data.*;

public abstract class SmallFrame extends Frame {

    FrameDescriptor descriptor;

    @Override
    public FrameDescriptor descriptor() {
        return descriptor;
    }

    protected SmallFrame(RFunction function, Frame enclosingFrame, FrameDescriptor descriptor) {
        super(function, enclosingFrame);
        this.descriptor = descriptor;
    }

    public static class SmallFrame1Slot extends SmallFrame {

        public Object slot1;

        public SmallFrame1Slot(RFunction function, Frame enclosingFrame, FrameDescriptor descriptor) {
            super(function, enclosingFrame, descriptor);
        }

        @Override
        public Object get(int i) {
            assert Utils.check(i == 0);
            return slot1;
        }

        @Override
        public void set(int i, Object value) {
            assert Utils.check(i == 0);
            slot1 = value;
        }
    }

    public static class SmallFrame2Slots extends SmallFrame {

        public Object slot1;
        public Object slot2;

        public SmallFrame2Slots(RFunction function, Frame enclosingFrame, FrameDescriptor descriptor) {
            super(function, enclosingFrame, descriptor);
        }

        @Override
        public Object get(int i) {
            if (i == 0) {
                return slot1;
            }
            assert Utils.check(i == 1);
            return slot2;
        }

        @Override
        public void set(int i, Object value) {
            if (i == 0) {
                slot1 = value;
            } else {
                assert Utils.check(i == 1);
                slot2 = value;
            }
        }
    }

    public static class SmallFrame3Slots extends SmallFrame {

        public Object slot1;
        public Object slot2;
        public Object slot3;

        public SmallFrame3Slots(RFunction function, Frame enclosingFrame, FrameDescriptor descriptor) {
            super(function, enclosingFrame, descriptor);
        }

        @Override
        public Object get(int i) {
            switch(i) {
                case 0: return slot1;
                case 1: return slot2;
                default:
                    assert Utils.check(i == 2);
                    return slot3;
            }
        }

        @Override
        public void set(int i, Object value) {
            switch(i) {
                case 0:
                    slot1 = value;
                    break;
                case 1:
                    slot2 = value;
                    break;
                default:
                    assert Utils.check(i == 2);
                    slot3 = value;
                    break;
            }
        }
    }

    public static class SmallFrame4Slots extends SmallFrame {

        public Object slot1;
        public Object slot2;
        public Object slot3;
        public Object slot4;

        public SmallFrame4Slots(RFunction function, Frame enclosingFrame, FrameDescriptor descriptor) {
            super(function, enclosingFrame, descriptor);
        }

        @Override
        public Object get(int i) {
            switch(i) {
                case 0: return slot1;
                case 1: return slot2;
                case 2: return slot3;
                default:
                    assert Utils.check(i == 3);
                    return slot4;
            }
        }

        @Override
        public void set(int i, Object value) {
            switch(i) {
                case 0:
                    slot1 = value;
                    break;
                case 1:
                    slot2 = value;
                    break;
                case 2:
                    slot3 = value;
                    break;
                default:
                    assert Utils.check(i == 3);
                    slot4 = value;
                    break;
            }
        }
    }

}
