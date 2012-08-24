package r.data;

import r.*;
import r.data.RFrameDescriptor.ReadSetEntry;

import com.oracle.truffle.runtime.*;

public final class RFrame extends Frame {

    public static final int PARENT_SLOT = 0;
    public static final int FRAME_DESCRIPTOR = 1;
    public static final int EXTENSION_SLOT = 2;

    /**
     * Number of reserved slots (i.e., last slot id + 1).
     */
    public static final int RESERVED_SLOTS = EXTENSION_SLOT + 1;
    public static final long DIRTY_MASK = 1 << (Long.SIZE - 1);
    public static final long HOPS_BITS = 16;
    public static final long POS_BITS = 16;
    public static final long HOPS_MASK = ((1 << HOPS_BITS) - 1) << POS_BITS;

    public RFrame(int numLocals, RFrame parent, RFrameDescriptor fdesc) {
        /*
         * NOTE: We differ from the nomal one since you cannot screw up the special fields NOTE: primitives are NOT used
         * for primitives but for dirty check + linking
         */
        super(numLocals + RESERVED_SLOTS, parent);
        locals[FRAME_DESCRIPTOR] = fdesc;
    }

    public RAny read(RSymbol sym) {
        int pos = getPositionInWS(sym);
        RFrameDescriptor.ReadSetEntry rse;
        if (pos >= 0) {
            return readViaWriteSet(pos, sym);
        } else if ((rse = getRSEntry(sym)) != null) {
            return getParent().readViaReadSet(rse.frameHops - 1, rse.framePos, sym, this);
        } else {
            return readFromExtension(sym, null);
        }
    }

    public RAny readViaReadSet(int hops, int pos, RSymbol symbol) {
        assert Utils.check(hops != 0); // It was present in the writeSet
        return getParent().readViaReadSet(hops - 1, pos, symbol, this);
    }

    private RAny readViaReadSet(int hops, int pos, RSymbol symbol, RFrame first) {
        if (hops == 0) {
            Object val;
            if (isDirty(pos)) {
                val = first.readFromExtension(symbol, this);
                if (val != null) {
                    return Utils.cast(val);
                }
            }
            val = locals[pos];
            if (val != null) {
                return Utils.cast(val);
            }
            ReadSetEntry rse = getRSEFromCache(pos, symbol);
            RFrame f = getParent();

            return f.readViaReadSet(rse.frameHops - 1, rse.framePos, symbol, f);
        } else {
            return getParent().readViaReadSet(hops - 1, pos, symbol, first);
        }
    }

    private ReadSetEntry getRSEFromCache(int pos, RSymbol sym) {
        long cache = primitiveLocals[pos] & ~DIRTY_MASK;
        if (cache == 0) {
            ReadSetEntry rse = getFrameDescriptor().getReadSetEntry(sym);
            primitiveLocals[pos] |= (rse.frameHops << POS_BITS) | rse.framePos;
            return rse;
        } else {
            return new ReadSetEntry(null, (int) (cache & HOPS_MASK) >> POS_BITS, (int) (pos & ~HOPS_MASK));
        }
    }

    public RAny readViaWriteSet(int pos, RSymbol symbol) {
        Object val;

        val = locals[pos];
        if (val != null) {
            return Utils.cast(val);
        }
        ReadSetEntry rse = getRSEFromCache(pos, symbol);
        RFrame f = getParent();
        return f.readViaReadSet(rse.frameHops - 1, rse.framePos, symbol, f);
    }

    private RAny readFromExtension(RSymbol sym, RFrame stopFrame) {
        if (this == stopFrame) {
            return null;
        }
        RFrameExtension ext = getExtensionSlot();
        if (ext != null) {
            RAny val = ext.get(sym);
            if (val != null) {
                return val;
            }
        }
        return getParent().readFromExtension(sym, stopFrame);
    }

    private boolean isDirty(int pos) {
        return (primitiveLocals[pos] & DIRTY_MASK) != 0;
    }

    private int getPositionInWS(RSymbol sym) {
        return getFrameDescriptor().positionInWriteSet(sym);
    }

    private ReadSetEntry getRSEntry(RSymbol sym) { // TODO
        return getFrameDescriptor().getReadSetEntry(sym);
    }

    private RFrame getParent() {
        return Utils.cast(getObject(PARENT_SLOT));
    }

    private RFrameDescriptor getFrameDescriptor() {
        return Utils.cast(getObject(FRAME_DESCRIPTOR));
    }

    private RFrameExtension getExtensionSlot() {
        return Utils.cast(getObject(FRAME_DESCRIPTOR));
    }

    private static class RFrameExtension {

        // TODO better implementation

        private int size = 0;
        private int capacity = 10;

        private RSymbol[] names = new RSymbol[capacity];
        private RAny[] values = new RAny[capacity];

        RAny get(int pos) {
            assert Utils.check(pos < size);
            return values[pos];
        }

        RSymbol getNameAt(int pos) {
            return names[pos];
        }

        int getPosition(RSymbol name) {
            for (int i = 0; i < size; i++) {
                if (names[i] == name) {
                    return i;
                }
            }
            return -1;
        }

        RAny get(RSymbol name) {
            for (int i = 0; i < size; i++) {
                if (names[i] == name) {
                    return values[i];
                }
            }
            return null;
        }
    }
}
