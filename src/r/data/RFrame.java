package r.data;

import r.*;

import com.oracle.truffle.runtime.*;

public class RFrame {

    public static final int PARENT_SLOT = 0;
    public static final int NAME_SLOT = 1;
    public static final int EXTENSION_SLOT = 2;

    /**
     * Number of reserved slots (i.e., last slot id + 1).
     */
    public static final int RESERVED_SLOTS = EXTENSION_SLOT + 1;

    /* Fast accesses */
    static RAny getByPos(Frame f, int pos) {
        return Utils.cast(f.getObject(pos), RAny.class);
    }

    static RAny getByPosFromExtension(Frame f, int pos) {
        return getExtensionSlot(f).getByPos(pos);
    }

    /* Slow accesses */
    static RAny getByName(Frame f, RSymbol name) {
        Frame current = f;
        while (current != null) {
            RAny v = getLocalByName(f, name);
            if (v != null) {
               return v;
            }
            current = getParent(f);
        }
        return null;
    }

    static RAny getLocalByName(Frame f, RSymbol name) {
        int pos = getStandardPos(name, getNameSlot(f));
        if (pos != -1) {
            return getByPos(f, pos);
        }
        RAny v = getFromExtension(name, null);
        return v;
    }

    private static Frame getParent(Frame f) {
        return Utils.cast(f.getObject(PARENT_SLOT));
    }

    private static RSymbol[] getNameSlot(Frame f) {
        return Utils.cast(f.getObject(NAME_SLOT));
    }

    private static RFrameExtension getExtensionSlot(Frame f) {
        return Utils.cast(f.getObject(NAME_SLOT));
    }

    private static int getStandardPos(RSymbol name, RSymbol[] names) {
        int len = names.length;
        for (int i = 0; i < len; i++) {
            if (names[i] == name) {
                return i;
            }
        }
        return -1;
    }

    private static RAny getFromExtension(RSymbol name, Frame f) {
        RFrameExtension ext = getExtensionSlot(f);
        return ext == null ? null : ext.getByName(name);
    }

    private static class RFrameExtension {
        private int size = 0;
        private int capacity = 10;

        private RSymbol[] names = new RSymbol[capacity];
        private RAny[] values = new RAny[capacity];

        RAny getByPos(int pos) {
            assert Utils.check(pos < size);
            return values[pos];
        }

        RAny getByName(RSymbol name) {
            for (int i = 0; i < size; i++) {
                if (names[i] == name) {
                    return values[i];
                }
            }
            return null;
        }
    }
}
