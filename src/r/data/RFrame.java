package r.data;

import r.*;
import r.data.RFunction.ReadSetEntry;
import r.data.internal.*;

import com.oracle.truffle.runtime.*;

public final class RFrame  {

    public static final int PARENT_SLOT = 0;
    public static final int FUNCTION_SLOT = 1;
    public static final int EXTENSION_SLOT = 2;
    public static final int RETURN_VALUE_SLOT = 3;

    /**
     * Number of reserved slots (i.e., last slot id + 1).
     */
    public static final int RESERVED_SLOTS = RETURN_VALUE_SLOT + 1;
    public static final long DIRTY_MASK = 1 << (Long.SIZE - 1);
    public static final long HOPS_BITS = 16;
    public static final long POS_BITS = 16;
    public static final long HOPS_MASK = ((1 << HOPS_BITS) - 1) << POS_BITS;

    /*
     * NOTE: We differ from the normal one since you cannot screw up the special fields NOTE: primitives are NOT
     * used for primitives but for dirty check + linking
     */
    public static Object[] createArgsArray(RFunction function) {
        return new Object[function.nparams()];
    }

    // parent is the enclosing environment, not previous frame on call stack
    public static Frame create(RFunction function, Frame parent) {
        Frame f = new Frame(function.nlocals() + RESERVED_SLOTS, parent);
        f.setObject(FUNCTION_SLOT, function);
        return f;
    }

    public static RAny read(Frame f, RSymbol sym) {
        int pos = getPositionInWS(f, sym);
        RFunction.ReadSetEntry rse;
        RAny val;
        if (pos >= 0) {
            return readViaWriteSet(f, pos, sym);
        } else if ((rse = getRSEntry(f, sym)) != null) {
            val = readViaReadSet(getParent(f), rse.frameHops - 1, rse.framePos, sym, f);
            if (val == null) {
                val = readFromTopLevel(sym);
            }
        } else {
            val = readFromExtension(f, sym, null);
            if (val == null) {
                val = readFromTopLevel(sym);
            }
        }
        return val;
    }

    public static void write(Frame f, RSymbol sym, RAny value) {
        int pos = getPositionInWS(f, sym);
        if (pos >= 0) {
            writeAt(f, pos, value);
        } else {
            writeInExtension(f, sym, value);
        }
    }

    public static RAny readViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
        assert Utils.check(hops != 0); // It was present in the writeSet
        RAny val = readViaReadSet(getParent(f), hops - 1, pos, symbol, f);
        if (val == null) {
            val = readFromTopLevel(symbol);
        }
        return val;
    }

    public static RAny readViaWriteSet(Frame f, int pos, RSymbol symbol) {
        Object val;

        val = f.getObject(pos + RESERVED_SLOTS);
        if (val != null) {
            return Utils.cast(val);
        }
        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
        if (rse == null) {
            val = readFromTopLevel(symbol);
        } else {
            Frame pf = getParent(f);
            val = readViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
            if (val == null) {
                val = readFromTopLevel(symbol);
            }
        }
        return Utils.cast(val);
    }

    public static RAny readFromTopLevel(Frame f, RSymbol sym, int version) {
        if (sym.getVersion() != version) {
            RAny val = readFromExtension(f, sym, null);
            if (val != null) {
                return val;
            }
        }
        return sym.getValue();
    }

    public static void writeAt(Frame f, int pos, Object value) {
        // Put an assertion or not ?
        f.setObject(pos + RESERVED_SLOTS, value);
        if (value instanceof RAny) {
            ((RAny) value).ref();
        }
    }

    public static void writeInExtension(Frame f, RSymbol sym, RAny value) {
        RFrameExtension ext = getExtension(f);
        if (ext == null) {
            ext = installExtension(f);
            ext.put(f, sym, value); // The extension is brand new, we can use the first slot safely
        } else {
            int pos = ext.getPosition(sym);
            if (pos >= 0) {
                ext.writeAt(pos, value);
            } else {
                ext.put(f, sym, value);
            }
        }
    }

    public static void writeInTopLevel(RSymbol sym, RAny value) {
        sym.setValue(value);
        value.ref();
    }

    private static RAny readFromTopLevel(RSymbol sym) {
        return sym.value;
    }

    private static RAny readViaReadSet(Frame f, int hops, int pos, RSymbol symbol, Frame first) {
        if (hops == 0) {
            Object val;
            if (isDirty(f, pos)) {
                val = readFromExtension(first, symbol, f);
                if (val != null) {
                    return Utils.cast(val);
                }
            }
            val = f.getObject(pos + RESERVED_SLOTS);
            if (val != null) {
                return Utils.cast(val);
            }
            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
            Frame pf = getParent(f);

            return readViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
        } else {
            return readViaReadSet(getParent(f), hops - 1, pos, symbol, first);
        }
    }

    private static ReadSetEntry getRSEFromCache(Frame f, int pos, RSymbol sym) {
        long cache = f.getLong(pos + RESERVED_SLOTS) & ~DIRTY_MASK;
        if (cache == 0) {
            ReadSetEntry rse = getFunction(f).getLocalReadSetEntry(sym);
            if (rse != null) { // variable is top-level or constructed by reflection and read by reflection
                long l = f.getLong(pos + RESERVED_SLOTS) | (rse.frameHops << POS_BITS) | rse.framePos;
                f.setLong(pos, l);
            }
            return rse;
        } else {
            return new ReadSetEntry(null, (int) (cache & HOPS_MASK) >> POS_BITS, (int) (pos & ~HOPS_MASK));
        }
    }

    public static void setReturnValue(Frame f, Object value) {
        f.setObject(RETURN_VALUE_SLOT, value);
    }

    public static Object getReturnValue(Frame f) {
        return f.getObject(RETURN_VALUE_SLOT);
    }

    public static RAny readFromExtension(Frame f, RSymbol sym, Frame stopFrame) { // It's public because of ReadVariable
        if (f == stopFrame) {
            return null;
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            RAny val = ext.get(sym);
            if (val != null) {
                return val;
            }
        }
        return readFromExtension(getParent(f), sym, stopFrame);
    }

    private static boolean isDirty(Frame f, int pos) {
        return (f.getLong(pos) & DIRTY_MASK) != 0;
    }

    public static int getPositionInWS(Frame f, RSymbol sym) {
        return getFunction(f).positionInLocalWriteSet(sym);
    }

    public static ReadSetEntry getRSEntry(Frame f, RSymbol sym) {
        return getFunction(f).getLocalReadSetEntry(sym);
    }

    public static Frame getParent(Frame f) {
        return Utils.cast(f.getObject(PARENT_SLOT));
    }

    public static RFunction getFunction(Frame f) {
        return Utils.cast(f.getObject(FUNCTION_SLOT));
    }

    private static RFrameExtension getExtension(Frame f) {
        return Utils.cast(f.getObject(EXTENSION_SLOT));
    }

    private static RFrameExtension installExtension(Frame f) {
        RFrameExtension ext = new RFrameExtension();
        f.setObject(EXTENSION_SLOT, ext);
        return ext;
    }

    private static void markDirty(Frame f, int pos) {
        long l = f.getLong(pos) | ~DIRTY_MASK;
        f.setLong(pos, l);
    }

    private static void markDirty(Frame enclosing, RSymbol sym) {
        Frame current = enclosing;
        while (current != null) {
            int pos;
            if ((pos = getPositionInWS(current, sym)) >= 0) {
                markDirty(current, pos);
                return;
            }
            current = getParent(enclosing);
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
            if (FunctionImpl.isIn(name.hash(), bloom)) {
                RSymbol[] n = names;
                for (int i = 0; i < used; i++) {
                    if (n[i] == name) {
                        return i;
                    }
                }
            }
            return -1;
        }

        private void put(Frame enclosing, RSymbol sym, RAny val) {
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
