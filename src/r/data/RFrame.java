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
         * NOTE: We differ from the normal one since you cannot screw up the special fields NOTE: primitives are NOT
         * used for primitives but for dirty check + linking
         */
        super(numLocals + RESERVED_SLOTS, parent);
        locals[FRAME_DESCRIPTOR] = fdesc;
    }

    public RAny read(RSymbol sym) {
        int pos = getPositionInWS(sym);
        RFrameDescriptor.ReadSetEntry rse;
        RAny val;
        if (pos >= 0) {
            return readViaWriteSet(pos, sym);
        } else if ((rse = getRSEntry(sym)) != null) {
            val = getParent().readViaReadSet(rse.frameHops - 1, rse.framePos, sym, this);
            if (val == null) {
                val = readFromTopLevel(sym);
            }
        } else {
            val = readFromExtension(sym, null);
            if (val == null) {
                val = readFromTopLevel(sym);
            }
        }
        return val;
    }

    public void write(RSymbol sym, RAny value) {
        int pos = getPositionInWS(sym);
        if (pos >= 0) {
            writeAt(pos, value);
        } else {
            writeInExtension(sym, value);
        }
    }

    public RAny readViaReadSet(int hops, int pos, RSymbol symbol) {
        assert Utils.check(hops != 0); // It was present in the writeSet
        RAny val = getParent().readViaReadSet(hops - 1, pos, symbol, this);
        if (val == null) {
            val = readFromTopLevel(symbol);
        }
        return val;
    }

    public RAny readViaWriteSet(int pos, RSymbol symbol) {
        Object val;

        val = locals[pos];
        if (val != null) {
            return Utils.cast(val);
        }
        ReadSetEntry rse = getRSEFromCache(pos, symbol);
        RFrame f = getParent();
        val = f.readViaReadSet(rse.frameHops - 1, rse.framePos, symbol, f);
        if (val == null) {
            val = readFromTopLevel(symbol);
        }
        return Utils.cast(val);
    }

    public RAny readFromTopLevel(RSymbol sym, int version) {
        if (sym.getVersion() != version) {
            RAny val = readFromExtension(sym, null);
            if (val != null) {
                return val;
            }
        }
        return sym.getValue();
    }

    public void writeAt(int pos, RAny value) {
        // Put an assertion or not ?
        locals[pos] = value;
    }

    public void writeInExtension(RSymbol sym, RAny value) {
        RFrameExtension ext = getExtensionSlot();
        if (ext == null) {
            ext = installExtension();
            ext.put(this, sym, value); // The extension is brand new, we can use the first slot safely
        } else {
            int pos = ext.getPosition(sym);
            if (pos >= 0) {
                ext.writeAt(pos, value);
            } else {
                ext.put(this, sym, value);
            }
        }
    }

    public static void writeInTopLevel(RSymbol sym, RAny value) {
        sym.setValue(value);
    }

    private static RAny readFromTopLevel(RSymbol sym) {
        return sym.value;
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

    public RAny readFromExtension(RSymbol sym, RFrame stopFrame) { // It's public beacause of ReadVariable
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

    public int getPositionInWS(RSymbol sym) {
        return getFrameDescriptor().positionInWriteSet(sym);
    }

    public ReadSetEntry getRSEntry(RSymbol sym) {
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

    private RFrameExtension installExtension() {
        RFrameExtension ext = new RFrameExtension();
        setObject(EXTENSION_SLOT, ext);
        return ext;
    }

    private void markDirty(int pos) {
        primitiveLocals[pos] |= ~DIRTY_MASK;
    }

    private static void markDirty(RFrame enclosing, RSymbol sym) {
        RFrame current = enclosing;
        while (current != null) {
            int pos;
            if ((pos = current.getPositionInWS(sym)) >= 0) {
                current.markDirty(pos);
                return;
            }
            current = enclosing.getParent();
        }
        sym.markDirty();
    }

    private static class RFrameExtension {

        private int used = 0;
        private int capacity = 10;
        // NOTE: we need a third counter for the last value use for storing the lastUsed value in case of removal

        private int bloom; // This bloom filter comes from Alex B. (renjin)
        // Does it make any sense ? for this dynamic structures

        // TODO Merge this two arrays, and use unsafe casts
        private RSymbol[] names = new RSymbol[capacity];
        private RAny[] values = new RAny[capacity];

        private RAny get(RSymbol name) {
            int pos = getPosition(name);
            if (pos >= 0) {
                return values[pos];
            }
            return null;
        }

        private int getPosition(RSymbol name) {
            if (RFrameDescriptor.isIn(name.hash(), bloom)) {
                RSymbol[] n = names;
                for (int i = 0; i < used; i++) {
                    if (n[i] == name) {
                        return i;
                    }
                }
            }
            return -1;
        }

        private void put(RFrame enclosing, RSymbol sym, RAny val) {
            int pos = used;
            if (pos == capacity) {
                expand(capacity * 2);
            }
            used++;
            names[pos] = sym;
            values[pos] = val;

            markDirty(enclosing, sym);
            bloom |= sym.id();
        }

        private void writeAt(int pos, RAny value) { // TODO or not TODO assert that the good name is still here
            assert Utils.check(pos < used);
            values[pos] = value;
        }

        private void expand(int newCap) {
            assert Utils.check(newCap > capacity);
            RSymbol[] newNames = new RSymbol[newCap];
            RAny[] newValues = new RAny[newCap];
            System.arraycopy(names, 0, newNames, 0, used);
            System.arraycopy(values, 0, newValues, 0, used);
            names = newNames;
            values = newValues;
        }
    }
}
