package r.data;

import r.*;
import r.data.RFunction.ReadSetEntry;
import r.data.internal.*;

import com.oracle.truffle.runtime.*;

public final class RFrame  {

    public static final int PARENT_SLOT = 0; // frame of lexically enclosing function (not caller frame), must match Frame.PARENT_FRAME_SLOT
    public static final int FUNCTION_SLOT = 1;
    public static final int EXTENSION_SLOT = 2;
    public static final int ENVIRONMENT_SLOT = 3;
    public static final int RETURN_VALUE_SLOT = 4;

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

    public static RAny localRead(Frame f, RSymbol sym) {
        int pos = getPositionInWS(f, sym);
        if (pos >= 0) {
            return Utils.cast(f.getObject(pos + RESERVED_SLOTS));
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            return ext.get(sym);
        }
        return null;
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
            RFrameExtension ext = getExtension(f);
            if (ext != null) {
                val = Utils.cast(ext.get(sym));
                if (val != null) {
                    return val;
                }
            }
            Frame parent = getParent(f);
            if (parent != null) {
                return read(parent, sym);
            } else {
                val = readFromTopLevel(sym);
            }
        }
        return val;
    }

    public static boolean localExists(Frame f, RSymbol symbol) {
        int pos = getPositionInWS(f, symbol);
        if (pos >= 0) {
            return f.getObject(pos + RESERVED_SLOTS) != null;
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            return ext.get(symbol) != null;
        }
        return false;
    }

    public static boolean exists(Frame f, RSymbol symbol) {
        int pos = getPositionInWS(f, symbol);
        if (pos >= 0) {
            if (f.getObject(pos + RESERVED_SLOTS) != null) {
                return true;
            }
        }
        RFunction.ReadSetEntry rse = getRSEntry(f, symbol);
        if (rse != null) {
            return existsViaReadSet(f, rse.frameHops - 1, rse.framePos, symbol);
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            if (ext.get(symbol) != null) {
                return true;
            }
        }
        Frame parent = getParent(f);
        if (parent != null) {
            return exists(parent, symbol);
        } else {
            return readFromTopLevel(symbol) != null;
        }

    }

    public static void localWrite(Frame f, RSymbol sym, RAny value) {
        int pos = getPositionInWS(f, sym);
        if (pos >= 0) {
            writeAtRef(f, pos, value);
        } else {
            writeToExtension(f, sym, value); // marks the defining slot dirty
        }
    }

    // this is like "superWrite" - but starts with current frame
    public static void reflectiveInheritsWrite(Frame frame, RSymbol symbol, RAny value) { // used for assign with inherits == TRUE
        int pos = getPositionInWS(frame, symbol);
        if (pos >= 0) {
            superWriteViaWriteSet(frame, pos, symbol, value);
        } else {
            ReadSetEntry rse = RFrame.getRSEntry(frame, symbol);
            if (rse != null) {
                superWriteViaReadSet(frame, rse.frameHops, rse.framePos, symbol, value);
            } else {
                RFrameExtension ext = getExtension(frame);
                if (ext != null) {
                    int epos = ext.getPosition(symbol);
                    if (epos != -1) {
                        ext.writeAt(epos, value);
                        return;
                    }
                }
                Frame parentFrame = getParent(frame);
                if (parentFrame != null) {
                    reflectiveInheritsWrite(parentFrame, symbol, value);
                } else {
                    superWriteToTopLevel(symbol, value);
                }
            }
        }
    }

    public static RAny readViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
        assert Utils.check(hops != 0);
        RAny val = readViaReadSet(getParent(f), hops - 1, pos, symbol, f);
        if (val == null) {
            val = readFromTopLevel(symbol);
        }
        return val;
    }

    public static RCallable matchViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
        assert Utils.check(hops != 0);
        RCallable val = matchViaReadSet(getParent(f), hops - 1, pos, symbol, f);
        if (val == null) {
            val = matchFromTopLevel(symbol);
        }
        return val;
    }

    public static boolean superWriteViaReadSet(Frame f, int hops, int pos, RSymbol symbol, RAny value) {
        assert Utils.check(hops != 0);
        if (superWriteViaReadSet(getParent(f), hops - 1, pos, symbol, value, f)) {
            return true;
        } else {
            return superWriteToTopLevel(symbol, value);
        }
    }

    public static boolean existsViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
        assert Utils.check(hops != 0);
        if (existsViaReadSet(getParent(f), hops - 1, pos, symbol, f)) {
            return true;
        } else {
            return readFromTopLevel(symbol) != null;
        }
    }

    public static RAny readViaWriteSet(Frame f, int pos, RSymbol symbol) {
        Object val;

        val = f.getObject(pos + RESERVED_SLOTS);
        if (val != null) {
            return Utils.cast(val);
        } else {
            return readViaWriteSetSlowPath(f, pos, symbol);
        }
    }

    public static RCallable matchViaWriteSet(Frame f, int pos, RSymbol symbol) {
        Object val;

        val = f.getObject(pos + RESERVED_SLOTS);
        if (val != null && val instanceof RCallable) {
            return (RCallable) val;
        } else {
            return matchViaWriteSetSlowPath(f, pos, symbol);
        }
    }

    public static RAny readViaWriteSetSlowPath(Frame f, int pos, RSymbol symbol) {
        Object val;

        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
        Frame pf = getParent(f);
        if (rse == null) {
              val = readFromExtensionsAndTopLevel(pf, symbol);
        } else {
            val = readViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
            if (val == null) {
                val = readFromTopLevel(symbol);
            }
        }
        return Utils.cast(val);
    }

    public static RCallable matchViaWriteSetSlowPath(Frame f, int pos, RSymbol symbol) {
        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
        Frame pf = getParent(f);
        if (rse == null) {
            return matchFromExtensionsAndTopLevel(pf, symbol);
        } else {
            RCallable val = matchViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
            if (val == null) {
                return matchFromTopLevel(symbol);
            } else {
                return val;
            }
        }
    }

    public static boolean superWriteViaWriteSet(Frame parentFrame, int pos, RSymbol symbol, RAny value) {
        Object oldVal = parentFrame.getObject(pos + RFrame.RESERVED_SLOTS);
        if (oldVal != null) {
            if (oldVal != value) {
                RFrame.writeAtNoRef(parentFrame, pos, value);
                value.ref();
            }
            return true;
        } else {
            return superWriteViaWriteSetSlowPath(parentFrame, pos, symbol, value);
        }
    }

    public static boolean superWriteViaWriteSetSlowPath(Frame f, int pos, RSymbol symbol, RAny value) {
        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
        Frame pf = getParent(f);
        if (rse == null) {
            return superWriteToExtensionsAndTopLevel(pf, symbol, value);
        }

        if (superWriteViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, value, pf)) {
            return true;
        }
        return superWriteToTopLevel(symbol, value);
    }

    public static RAny readFromExtensionsAndTopLevel(Frame f, RSymbol sym) {
        RAny val = readFromExtension(f, sym, null);
        if (val != null) {
            return val;
        } else {
            return sym.getValue();
        }
    }

    public static RAny readFromTopLevel(Frame f, RSymbol sym, int version) {
        if (sym.getVersion() != version) {
            return readFromExtensionsAndTopLevel(f, sym);
        } else {
            return readFromTopLevel(sym);
        }
    }

    public static void writeAtRef(Frame f, int pos, Object value) {
        // Put an assertion or not ?
        f.setObject(pos + RESERVED_SLOTS, value);
        if (value instanceof RAny) {
            ((RAny) value).ref();
        }
    }

    public static void writeAtNoRef(Frame f, int pos, RAny value) {
        f.setObject(pos + RESERVED_SLOTS, value);
    }

    public static void writeAtCondRef(Frame f, int pos, RAny value) {
        final int rawPos = pos + RESERVED_SLOTS;
        Object oldContent = f.getObject(rawPos);
        if (value != oldContent) {
            f.setObject(rawPos, value);
            value.ref();
        }
    }

    public static void writeAtRef(Frame f, int pos, RAny value) { // FIXME: should find a fast way to avoid .ref() calls when the variable in fact did not change
        // Put an assertion or not ?
        f.setObject(pos + RESERVED_SLOTS, value);
        value.ref();
    }

    public static void writeToExtension(Frame f, RSymbol sym, RAny value) {
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

    public static void writeToTopLevelRef(RSymbol sym, RAny value) {
        sym.setValue(value);
        value.ref();
    }

    public static void writeToTopLevelNoRef(RSymbol sym, RAny value) {
        sym.setValue(value);
    }

    public static void writeToTopLevelCondRef(RSymbol sym, RAny value) {
        RAny oldValue = sym.getValue();
        if (oldValue != value) {
            sym.setValue(value);
            value.ref();
        }
    }

    public static boolean superWriteToExtensionsAndTopLevel(Frame f, RSymbol symbol, RAny value) {
        if (superWriteToExtension(f, symbol, value, null)) {
            return true;
        }
        return superWriteToTopLevel(symbol, value);
    }

    public static boolean superWriteToTopLevel(RSymbol symbol, RAny value) {
        // FIXME: allow modification of builtins
        writeToTopLevelCondRef(symbol, value);
        return true;
    }

    private static RAny readFromTopLevel(RSymbol sym) {
        return sym.value;
    }

    public static RCallable matchFromExtensionsAndTopLevel(Frame f, RSymbol sym) {
        RCallable res = matchFromExtension(f, sym, null);
        if (res != null) {
            return res;
        } else {
            return matchFromTopLevel(sym);
        }
    }

    private static RCallable matchFromTopLevel(RSymbol sym) {
        RAny res = sym.value;
        if (res instanceof RCallable) {
            return (RCallable) res;
        } else {
            return null;
        }
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
            // FIXME: how to unset the dirty flag?
            val = f.getObject(pos + RESERVED_SLOTS);
            if (val != null) {
                return Utils.cast(val);
            }
            Frame pf = getParent(f);
            if (pf == null) {
                return null;
            }
            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
            if (rse == null) {
                return readFromExtension(f, symbol, null);
            }
            return readViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
        } else {
            return readViaReadSet(getParent(f), hops - 1, pos, symbol, first);
        }
    }

    private static RCallable matchViaReadSet(Frame f, int hops, int pos, RSymbol symbol, Frame first) {
        if (hops == 0) {
            if (isDirty(f, pos)) {
                RCallable res = matchFromExtension(first, symbol, f);
                if (res != null) {
                    return res;
                }
            }
            Object val = f.getObject(pos + RESERVED_SLOTS);
            if (val != null && val instanceof RCallable) {
                return (RCallable) val;
            }
            Frame pf = getParent(f);
            if (pf == null) {
                return null;
            }
            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
            if (rse == null) {
                return matchFromExtension(f, symbol, null);
            }
            return matchViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
        } else {
            return matchViaReadSet(getParent(f), hops - 1, pos, symbol, first);
        }
    }

    private static boolean superWriteViaReadSet(Frame f, int hops, int pos, RSymbol symbol, RAny value, Frame first) {
        if (hops == 0) {
            Object val;
            if (isDirty(f, pos)) {
                if (superWriteToExtension(first, symbol, value, f)) {
                    return true;
                }
            }
            val = f.getObject(pos + RESERVED_SLOTS);
            if (val != null) {
                RFrame.writeAtRef(f, pos, value);
                return true;
            }
            Frame pf = getParent(f);
            if (pf == null) {
                return false;
            }
            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
            if (rse == null) {
                return superWriteToExtension(f, symbol, value, null);
            }

            return superWriteViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, value,  pf);
        } else {
            return superWriteViaReadSet(getParent(f), hops - 1, pos, symbol, value, first);
        }
    }

    private static boolean existsViaReadSet(Frame f, int hops, int pos, RSymbol symbol, Frame first) {
        if (hops == 0) {
            if (f.getObject(pos + RESERVED_SLOTS) != null) {
                return true;
            }
            if (isDirty(f, pos)) {
                if (existsInExtension(first, symbol, f)) {
                    return true;
                }
            }
            Frame pf = getParent(f);
            if (pf == null) {
                return false;
            }
            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
            if (rse == null) {
                return existsInExtension(f, symbol, null);
            }
            return existsViaReadSet(pf, rse.frameHops - 1, rse.framePos, symbol, pf);
        } else {
            return existsViaReadSet(getParent(f), hops - 1, pos, symbol, first);
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

    public static RCallable matchFromExtension(Frame f, RSymbol sym, Frame stopFrame) { // It's public because of ReadVariable
        if (f == stopFrame) {
            return null;
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            RAny val = ext.get(sym);
            if (val != null && val instanceof RCallable) {
                return (RCallable) val;
            }
        }
        return matchFromExtension(getParent(f), sym, stopFrame);
    }

    public static boolean superWriteToExtension(Frame f, RSymbol sym, RAny value, Frame stopFrame) {
        if (f == stopFrame) {
            return false;
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            int epos = ext.getPosition(sym);
            if (epos != -1) {
                ext.writeAt(epos, value);
                return true;
            }
        }
        return superWriteToExtension(getParent(f), sym, value, stopFrame);
    }

    public static boolean existsInExtension(Frame f, RSymbol sym, Frame stopFrame) {
        if (f == stopFrame) {
            return false;
        }
        RFrameExtension ext = getExtension(f);
        if (ext != null) {
            if (ext.get(sym) != null) {
                return true;
            }
        }
        return existsInExtension(getParent(f), sym, stopFrame);
    }

    private static boolean isDirty(Frame f, int pos) {
        return (f.getLong(pos + RESERVED_SLOTS) & DIRTY_MASK) != 0;
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

    public static REnvironment getEnvironment(Frame f) {
        REnvironment e = Utils.cast(f.getObject(ENVIRONMENT_SLOT));
        if (e == null) {
            e = new EnvironmentImpl(f);
            f.setObject(ENVIRONMENT_SLOT, e);
        }
        return e;
    }

    private static void markDirty(Frame f, int pos) {
        int rawpos = pos + RESERVED_SLOTS;
        long l = f.getLong(rawpos) | DIRTY_MASK;
        f.setLong(rawpos, l);
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
            val.ref();

            markDirty(enclosing, sym);
            bloom |= sym.id();
        }

        private void writeAt(int pos, RAny value) { // TODO or not TODO assert that the good name is still here
            assert Utils.check(pos < used);
            if (values[pos] != value) {
                values[pos] = value;
                value.ref();
            }
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

        private RSymbol[] validNames() { // TODO: revisit when deletion is implemented
            int nremoved = 0;
            for (int i = 0; i < used; i++) {
                if (values[i] == null) {
                    nremoved++;
                }
            }
            if (nremoved == 0 && used == names.length) {
                return names;
            } else {
                int size = used - nremoved;
                RSymbol[] vnames = new RSymbol[size];
                int j = 0;
                for (int i = 0; i < used; i++) {
                    if (values[i] != null) {
                        vnames[j++] = names[i];
                    }
                }
                return vnames;
            }
        }
    }

    public static RSymbol[] listSymbols(Frame frame) {
        RSymbol[] ws = getFunction(frame).localWriteSet();
        RFrameExtension ext = getExtension(frame);
        if (ext != null) {
            RSymbol[] es = ext.validNames();
            if (ws.length == 0) {
                return es;
            }
            int size = ws.length + es.length;
            RSymbol[] res = new RSymbol[size];
            System.arraycopy(ws, 0, res, 0, ws.length);
            System.arraycopy(es, 0, res, ws.length, es.length);
            return res;

        } else {
            return ws;
        }
    }
}
