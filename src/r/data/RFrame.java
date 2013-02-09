package r.data;

import java.util.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.RFunction.EnclosingSlot;
import r.data.internal.*;

// TODO: finish implementation of root level (global environment, empty environment, correct behavior with super assignments, etc)
public final class RFrame  {
//
//    public static final int PARENT_SLOT = 0; // frame of lexically enclosing function (not caller frame)
//    public static final int FUNCTION_SLOT = 1;
//    public static final int EXTENSION_SLOT = 2;
//    public static final int ENVIRONMENT_SLOT = 3;
//    public static final int RETURN_VALUE_SLOT = 4; // also root environment
//
//    /**
//     * Number of reserved slots (i.e., last slot id + 1).
//     */
//    public static final int RESERVED_SLOTS = RETURN_VALUE_SLOT + 1;
//    public static final long DIRTY_MASK = 1 << (Long.SIZE - 1);
//    public static final long HOPS_BITS = 16;
//    public static final long POS_BITS = 16;
//    public static final long HOPS_MASK = ((1 << HOPS_BITS) - 1) << POS_BITS;
//
//    /*
//     * NOTE: We differ from the normal one since you cannot screw up the special fields NOTE: primitives are NOT
//     * used for primitives but for dirty check + linking
//     */
//    public static Object[] createArgsArray(RFunction function) {
//        return new Object[function.nparams()];
//    }
//
//    // parent is the enclosing environment, not previous frame on call stack
//    public static Frame create(RFunction function, Frame parent) {
//        Frame f = new Frame(function.nlocals() + RESERVED_SLOTS, parent);
//        f.setObject(FUNCTION_SLOT, function);
//        return f;
//    }
//
//    public static RAny localRead(Frame f, RSymbol sym) {
//        int pos = getPositionInWS(f, sym);
//        if (pos >= 0) {
//            return Utils.cast(f.getObject(pos + RESERVED_SLOTS));
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            return ext.get(sym);
//        }
//        return null;
//    }
//
//    public static RAny customLocalRead(Frame f, RSymbol sym) {
//        RFrameExtension ext = getExtension(f);
//        return ext.get(sym);
//    }
//
//    public static RAny read(Frame f, RSymbol sym) {
//        int pos = getPositionInWS(f, sym);
//        RFunction.ReadSetEntry rse;
//        RAny val;
//        if (pos >= 0) {
//            return readViaWriteSet(f, pos, sym);
//        } else if ((rse = getRSEntry(f, sym)) != null) {
//            return readViaReadSetAndRootLevel(getParent(f), rse.hops - 1, rse.framePos, sym, f);
//        } else {
//            RFrameExtension ext = getExtension(f);
//            if (ext != null) {
//                val = Utils.cast(ext.get(sym));
//                if (val != null) {
//                    return val;
//                }
//            }
//            Frame parent = getParent(f);
//            if (parent != null) {
//                return read(parent, sym);
//            } else {
//                val = readFromRootLevel(f, sym);
//            }
//        }
//        return val;
//    }
//
//    public static RAny customRead(Frame f, RSymbol sym) {
//        RAny val;
//        RFrameExtension ext = getExtension(f);
//        val = Utils.cast(ext.get(sym));
//        if (val != null) {
//            return val;
//        }
//        Frame parent = getParent(f);
//        if (parent != null) {
//            return read(parent, sym);
//        } else {
//            val = readFromRootLevel(f, sym);
//        }
//        return val;
//    }
//
//    public static boolean localExists(Frame f, RSymbol symbol) {
//        int pos = getPositionInWS(f, symbol);
//        if (pos >= 0) {
//            return f.getObject(pos + RESERVED_SLOTS) != null;
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            return ext.get(symbol) != null;
//        }
//        return false;
//    }
//
//    public static boolean customLocalExists(Frame f, RSymbol symbol) {
//        RFrameExtension ext = getExtension(f);
//        return ext.get(symbol) != null;
//    }
//
//    public static boolean exists(Frame f, RSymbol symbol) {
//        int pos = getPositionInWS(f, symbol);
//        if (pos >= 0) {
//            if (f.getObject(pos + RESERVED_SLOTS) != null) {
//                return true;
//            }
//        }
//        RFunction.ReadSetEntry rse = getRSEntry(f, symbol);
//        if (rse != null) {
//            return existsViaReadSet(f, rse.hops - 1, rse.framePos, symbol);
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            if (ext.get(symbol) != null) {
//                return true;
//            }
//        }
//        Frame parent = getParent(f);
//        if (parent != null) {
//            return exists(parent, symbol);
//        } else {
//            return readFromRootLevel(f, symbol) != null;
//        }
//    }
//
//    public static boolean customExists(Frame f, RSymbol symbol) {
//        RFrameExtension ext = getExtension(f);
//        if (ext.get(symbol) != null) {
//            return true;
//        }
//        Frame parent = getParent(f);
//        if (parent != null) {
//            return exists(parent, symbol);
//        } else {
//            return readFromRootLevel(f, symbol) != null;
//        }
//    }
//
//    public static void localWrite(Frame f, RSymbol sym, RAny value) {
//        int pos = getPositionInWS(f, sym);
//        if (pos >= 0) {
//            writeAtRef(f, pos, value);
//        } else {
//            writeToExtension(f, sym, value); // marks the defining slot dirty
//        }
//    }
//
//    public static void customLocalWrite(Frame f, RSymbol sym, RAny value) {
//        RFrameExtension ext = getExtension(f);
//        int pos = ext.getPosition(sym);
//        if (pos >= 0) {
//            ext.writeAt(pos, value);
//        } else {
//            ext.put(f, sym, value);
//        }
//    }
//
//    // this is like "superWrite" - but starts with current frame
//    public static void reflectiveInheritsWrite(Frame frame, RSymbol symbol, RAny value) { // used for assign with inherits == TRUE
//        int pos = getPositionInWS(frame, symbol);
//        if (pos >= 0) {
//            superWriteViaWriteSet(frame, pos, symbol, value);
//        } else {
//            ReadSetEntry rse = RFrame.getRSEntry(frame, symbol);
//            if (rse != null) {
//                superWriteViaReadSet(frame, rse.hops, rse.framePos, symbol, value);
//            } else {
//                RFrameExtension ext = getExtension(frame);
//                if (ext != null) {
//                    int epos = ext.getPosition(symbol);
//                    if (epos != -1) {
//                        ext.writeAt(epos, value);
//                        return;
//                    }
//                }
//                Frame parentFrame = getParent(frame);
//                if (parentFrame != null) {
//                    reflectiveInheritsWrite(parentFrame, symbol, value);
//                } else {
//                    superWriteToTopLevel(symbol, value);
//                }
//            }
//        }
//    }
//
//    public static void customReflectiveInheritsWrite(Frame frame, RSymbol symbol, RAny value) { // used for assign with inherits == TRUE
//        RFrameExtension ext = getExtension(frame);
//        int epos = ext.getPosition(symbol);
//        if (epos != -1) {
//            ext.writeAt(epos, value);
//            return;
//        }
//        Frame parentFrame = getParent(frame);
//        if (parentFrame != null) {
//            reflectiveInheritsWrite(parentFrame, symbol, value);
//        } else {
//            superWriteToTopLevel(symbol, value);
//        }
//    }
//
//    // in contrast to e.g. read, match can be called with a null frame
//    // FIXME: this may need to be adapted to work with eval
//    public static RCallable match(Frame frame, RSymbol symbol) {
//        if (frame == null) {
//            return REnvironment.GLOBAL.match(symbol);  // FIXME: get rid of this special case, FIX it for eval
//        }
//        Frame f = frame;
//
//        for (;;) {
//            int pos = RFrame.getPositionInWS(f, symbol);
//            if (pos >= 0) {
//                return RFrame.matchViaWriteSet(f, pos, symbol);
//            }
//
//            ReadSetEntry rse = RFrame.getRSEntry(f, symbol);
//            if (rse != null) {
//                return RFrame.matchViaReadSet(f, rse.hops, rse.framePos, symbol);
//            }
//
//            RFrameExtension ext = getExtension(f);
//            if (ext != null) {
//                RAny value = Utils.cast(ext.get(symbol));
//                if (value != null && value instanceof RCallable) {
//                    return (RCallable) value;
//                }
//            }
//            Frame parent = getParent(f);
//            if (parent == null) {
//                return matchFromRootLevel(f, symbol);
//            }
//            f = parent;
//        }
//    }
//
//    public static RAny readViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
//        assert Utils.check(hops != 0);
//        Frame pf = getParent(f);
//        return readViaReadSetAndRootLevel(pf, hops - 1, pos, symbol, f);
//    }
//
//    public static RCallable matchViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
//        assert Utils.check(hops != 0);
//        return matchViaReadSet(getParent(f), hops - 1, pos, symbol, f);
//    }
//
//    public static boolean superWriteViaReadSet(Frame f, int hops, int pos, RSymbol symbol, RAny value) {
//        assert Utils.check(hops != 0);
//        if (superWriteViaReadSet(getParent(f), hops - 1, pos, symbol, value, f)) {
//            return true;
//        } else {
//            return superWriteToTopLevel(symbol, value);
//        }
//    }
//
//    public static boolean existsViaReadSet(Frame f, int hops, int pos, RSymbol symbol) {
//        assert Utils.check(hops != 0);
//        Frame pf = getParent(f);
//        if (existsViaReadSet(pf, hops - 1, pos, symbol, f)) {
//            return true;
//        } else {
//            return readFromRootLevel(pf, symbol) != null;
//        }
//    }
//
//    public static RAny readViaWriteSet(Frame f, int pos, RSymbol symbol) {
//        Object val;
//
//        val = f.getObject(pos + RESERVED_SLOTS);
//        if (val != null) {  // TODO: another node (one branch needs to have deopt)
//            return Utils.cast(val);
//        } else {
//            return readViaWriteSetSlowPath(f, pos, symbol);
//        }
//    }
//
//    public static RCallable matchViaWriteSet(Frame f, int pos, RSymbol symbol) {
//        Object val;
//
//        val = f.getObject(pos + RESERVED_SLOTS);
//        if (val != null && val instanceof RCallable) {
//            return (RCallable) val;
//        } else {
//            return matchViaWriteSetSlowPath(f, pos, symbol);
//        }
//    }
//
//    public static RAny readViaWriteSetSlowPath(Frame f, int pos, RSymbol symbol) {
//        assert Utils.check(f != null);
//
//        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//        if (rse == null) {
//            return readFromExtensionsAndRootLevel(f, symbol);
//        } else {
//            Frame pf = getParent(f);
//            return readViaReadSetAndRootLevel(pf, rse.hops - 1, rse.framePos, symbol, pf);
//        }
//    }
//
//    public static RCallable matchViaWriteSetSlowPath(Frame f, int pos, RSymbol symbol) {
//        assert Utils.check(f != null);
//
//        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//        if (rse == null) {
//            return matchFromExtensionsAndRootLevel(f, symbol);
//        } else {
//            Frame pf = getParent(f);
//            return matchViaReadSet(pf, rse.hops - 1, rse.framePos, symbol, pf);
//        }
//    }
//
//    public static boolean superWriteViaWriteSet(Frame parentFrame, int pos, RSymbol symbol, RAny value) {
//        Object oldVal = parentFrame.getObject(pos + RFrame.RESERVED_SLOTS);
//        if (oldVal != null) {
//            if (oldVal != value) {
//                RFrame.writeAtNoRef(parentFrame, pos, value);
//                value.ref();
//            }
//            return true;
//        } else {
//            return superWriteViaWriteSetSlowPath(parentFrame, pos, symbol, value);
//        }
//    }
//
//    public static boolean superWriteViaWriteSetSlowPath(Frame f, int pos, RSymbol symbol, RAny value) {
//        ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//        Frame pf = getParent(f);
//        if (rse == null) {
//            return superWriteToExtensionsAndTopLevel(pf, symbol, value);
//        }
//
//        if (superWriteViaReadSet(pf, rse.hops - 1, rse.framePos, symbol, value, pf)) {
//            return true;
//        }
//        return superWriteToTopLevel(symbol, value);
//    }
//
//    public static RAny readFromExtensionsAndRootLevel(Frame childFrame, RSymbol symbol) {
//        assert Utils.check(childFrame != null);
//
//        Frame f = childFrame;
//        for (;;) {
//            Frame pf = getParent(f);
//            if (pf == null) {
//                return readFromRootLevel(f, symbol);
//            }
//            f = pf;
//            RFrameExtension ext = getExtension(f);
//            if (ext != null) {
//                RAny val = ext.get(symbol);
//                if (val != null) {
//                    return val;
//                }
//            }
//        }
//    }
//
//    public static RCallable matchFromExtensionsAndRootLevel(Frame childFrame, RSymbol symbol) {
//        assert Utils.check(childFrame != null);
//
//        Frame f = childFrame;
//        for (;;) {
//            Frame pf = getParent(f);
//            if (pf == null) {
//                return matchFromRootLevel(f, symbol);
//            }
//            f = pf;
//            RFrameExtension ext = getExtension(f);
//            if (ext != null) {
//                RAny val = ext.get(symbol);
//                if (val != null && val instanceof RCallable) {
//                    return (RCallable) val;
//                }
//            }
//        }
//    }
//
//    public static void writeAtRef(Frame f, int pos, Object value) {
//        // Put an assertion or not ?
//        f.setObject(pos + RESERVED_SLOTS, value);
//        if (value instanceof RAny) {
//            ((RAny) value).ref();
//        }
//    }
//
//    public static void writeAtNoRef(Frame f, int pos, RAny value) {
//        f.setObject(pos + RESERVED_SLOTS, value);
//    }
//
//    public static void writeAtCondRef(Frame f, int pos, RAny value) {
//        final int rawPos = pos + RESERVED_SLOTS;
//        Object oldContent = f.getObject(rawPos);
//        if (value != oldContent) {
//            f.setObject(rawPos, value);
//            value.ref();
//        }
//    }
//
//    public static void writeAtRef(Frame f, int pos, RAny value) { // FIXME: should find a fast way to avoid .ref() calls when the variable in fact did not change
//        // Put an assertion or not ?
//        f.setObject(pos + RESERVED_SLOTS, value);
//        value.ref();
//    }
//
//    public static void writeToExtension(Frame f, RSymbol sym, RAny value) {
//        RFrameExtension ext = getExtension(f);
//        if (ext == null) {
//            ext = installExtension(f);
//            ext.put(f, sym, value); // The extension is brand new, we can use the first slot safely
//        } else {
//            int pos = ext.getPosition(sym);
//            if (pos >= 0) {
//                ext.writeAt(pos, value);
//            } else {
//                ext.put(f, sym, value);
//            }
//        }
//    }
//
//    public static void writeToTopLevelRef(RSymbol sym, RAny value) {
//        sym.setValue(value);
//        value.ref();
//    }
//
//    public static void writeToTopLevelNoRef(RSymbol sym, RAny value) {
//        sym.setValue(value);
//    }
//
//    public static void writeToTopLevelCondRef(RSymbol sym, RAny value) {
//        RAny oldValue = sym.getValue();
//        if (oldValue != value) {
//            sym.setValue(value);
//            value.ref();
//        }
//    }
//
//    public static boolean superWriteToExtensionsAndTopLevel(Frame f, RSymbol symbol, RAny value) {
//        if (superWriteToExtension(f, symbol, value, null)) {
//            return true;
//        }
//        return superWriteToTopLevel(symbol, value);
//    }
//
//    public static boolean superWriteToTopLevel(RSymbol symbol, RAny value) {
//        // FIXME: allow modification of builtins
//        writeToTopLevelCondRef(symbol, value);
//        return true;
//    }
//
//    public static RAny readFromRootLevel(Frame f, RSymbol sym) {
//        return getRootEnvironment(f).get(sym, true);
//    }
//
//    private static RCallable matchFromRootLevel(Frame frame, RSymbol symbol) {
//        return getRootEnvironment(frame).match(symbol);
//    }
//
//    private static RAny readViaReadSetAndRootLevel(Frame f, int hops, int pos, RSymbol symbol, Frame first) {
//        assert Utils.check(f != null);
//
//        if (hops == 0) {
//            Object val;
//            if (isDirty(f, pos)) {
//                val = readFromExtension(first, symbol, f);
//                if (val != null) {
//                    return Utils.cast(val);
//                }
//            }
//            // FIXME: how to unset the dirty flag?
//            val = f.getObject(pos + RESERVED_SLOTS);
//            if (val != null) {
//                return Utils.cast(val);
//            }
//            Frame pf = getParent(f);
//            if (pf == null) {
//                return readFromRootLevel(f, symbol);
//            }
//            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//            if (rse == null) {
//                return readFromExtensionsAndRootLevel(pf, symbol);
//            }
//            return readViaReadSetAndRootLevel(pf, rse.hops - 1, rse.framePos, symbol, pf);
//        } else {
//            return readViaReadSetAndRootLevel(getParent(f), hops - 1, pos, symbol, first);
//        }
//    }
//
//    private static RCallable matchViaReadSet(Frame f, int hops, int pos, RSymbol symbol, Frame first) {
//        assert Utils.check(f != null);
//
//        if (hops == 0) {
//            if (isDirty(f, pos)) {
//                RCallable res = matchFromExtension(first, symbol, f);
//                if (res != null) {
//                    return res;
//                }
//            }
//            Object val = f.getObject(pos + RESERVED_SLOTS);
//            if (val != null && val instanceof RCallable) {
//                return (RCallable) val;
//            }
//            Frame pf = getParent(f);
//            if (pf == null) {
//                return matchFromRootLevel(f, symbol);
//            }
//            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//            if (rse == null) {
//                return matchFromExtensionsAndRootLevel(pf, symbol);
//            }
//            return matchViaReadSet(pf, rse.hops - 1, rse.framePos, symbol, pf);
//        } else {
//            return matchViaReadSet(getParent(f), hops - 1, pos, symbol, first);
//        }
//    }
//
//    private static boolean superWriteViaReadSet(Frame f, int hops, int pos, RSymbol symbol, RAny value, Frame first) {
//        if (hops == 0) {
//            Object val;
//            if (isDirty(f, pos)) {
//                if (superWriteToExtension(first, symbol, value, f)) {
//                    return true;
//                }
//            }
//            val = f.getObject(pos + RESERVED_SLOTS);
//            if (val != null) {
//                RFrame.writeAtRef(f, pos, value);
//                return true;
//            }
//            Frame pf = getParent(f);
//            if (pf == null) {
//                return false;
//            }
//            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//            if (rse == null) {
//                return superWriteToExtension(f, symbol, value, null);
//            }
//
//            return superWriteViaReadSet(pf, rse.hops - 1, rse.framePos, symbol, value,  pf);
//        } else {
//            return superWriteViaReadSet(getParent(f), hops - 1, pos, symbol, value, first);
//        }
//    }
//
//    private static boolean existsViaReadSet(Frame f, int hops, int pos, RSymbol symbol, Frame first) {
//        if (hops == 0) {
//            if (f.getObject(pos + RESERVED_SLOTS) != null) {
//                return true;
//            }
//            if (isDirty(f, pos)) {
//                if (existsInExtension(first, symbol, f)) {
//                    return true;
//                }
//            }
//            Frame pf = getParent(f);
//            if (pf == null) {
//                return false;
//            }
//            ReadSetEntry rse = getRSEFromCache(f, pos, symbol);
//            if (rse == null) {
//                return existsInExtension(f, symbol, null);
//            }
//            return existsViaReadSet(pf, rse.hops - 1, rse.framePos, symbol, pf);
//        } else {
//            return existsViaReadSet(getParent(f), hops - 1, pos, symbol, first);
//        }
//    }
//
//    private static ReadSetEntry getRSEFromCache(Frame f, int pos, RSymbol sym) {
//        long cache = f.getLong(pos + RESERVED_SLOTS) & ~DIRTY_MASK;
//        if (cache == 0) {
//            ReadSetEntry rse = getFunction(f).getLocalReadSetEntry(sym);
//            if (rse != null) { // variable is top-level or constructed by reflection and read by reflection
//                long l = f.getLong(pos + RESERVED_SLOTS) | (rse.hops << POS_BITS) | rse.framePos;
//                f.setLong(pos, l);
//            }
//            return rse;
//        } else {
//            return new ReadSetEntry(null, (int) (cache & HOPS_MASK) >> POS_BITS, (int) (pos & ~HOPS_MASK));
//        }
//    }
//
//    public static void setReturnValue(Frame f, Object value) {
//        f.setObject(RETURN_VALUE_SLOT, value);
//    }
//
//    public static void setRootEnvironment(Frame f, Object value) {
//        assert Utils.check(getParent(f) == null);
//        f.setObject(RETURN_VALUE_SLOT, value);
//    }
//
//    public static Object getReturnValue(Frame f) {
//        return f.getObject(RETURN_VALUE_SLOT);
//    }
//
//    public static REnvironment getRootEnvironment(Frame f) {
//        if (f == null) { // FIXME: get rid of this branch
//            return REnvironment.GLOBAL;
//        }
//        REnvironment env = (REnvironment) f.getObject(RETURN_VALUE_SLOT);
//        if (env == null) {  // FIXME: get rid of this branch
//            return REnvironment.GLOBAL;
//        }
//        return env;
//    }
//
//    public static RAny readFromExtension(Frame f, RSymbol sym, Frame stopFrame) { // It's public because of ReadVariable
//        if (f == stopFrame) {
//            return null;
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            RAny val = ext.get(sym);
//            if (val != null) {
//                return val;
//            }
//        }
//        return readFromExtension(getParent(f), sym, stopFrame);
//    }
//
//    public static RCallable matchFromExtension(Frame f, RSymbol sym, Frame stopFrame) { // It's public because of ReadVariable
//        if (f == stopFrame) {
//            return null;
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            RAny val = ext.get(sym);
//            if (val != null && val instanceof RCallable) {
//                return (RCallable) val;
//            }
//        }
//        return matchFromExtension(getParent(f), sym, stopFrame);
//    }
//
//    public static boolean superWriteToExtension(Frame f, RSymbol sym, RAny value, Frame stopFrame) {
//        if (f == stopFrame) {
//            return false;
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            int epos = ext.getPosition(sym);
//            if (epos != -1) {
//                ext.writeAt(epos, value);
//                return true;
//            }
//        }
//        return superWriteToExtension(getParent(f), sym, value, stopFrame);
//    }
//
//    public static boolean existsInExtension(Frame f, RSymbol sym, Frame stopFrame) {
//        if (f == stopFrame) {
//            return false;
//        }
//        RFrameExtension ext = getExtension(f);
//        if (ext != null) {
//            if (ext.get(sym) != null) {
//                return true;
//            }
//        }
//        return existsInExtension(getParent(f), sym, stopFrame);
//    }
//
//    private static boolean isDirty(Frame f, int pos) {
//        return (f.getLong(pos + RESERVED_SLOTS) & DIRTY_MASK) != 0;
//    }
//
//    public static int getPositionInWS(Frame f, RSymbol sym) {
//        return getFunction(f).positionInLocalWriteSet(sym);
//    }
//
//    public static ReadSetEntry getRSEntry(Frame f, RSymbol sym) {
//        return getFunction(f).getLocalReadSetEntry(sym);
//    }
//
//    public static Frame getParent(Frame f) {
//        return Utils.cast(f.getObject(PARENT_SLOT));
//    }
//
//    public static RFunction getFunction(Frame f) {
//        return Utils.cast(f.getObject(FUNCTION_SLOT));
//    }
//
//    private static RFrameExtension getExtension(Frame f) {
//        return Utils.cast(f.getObject(EXTENSION_SLOT));
//    }
//
//    public static RFrameExtension installExtension(Frame f) {
//        RFrameExtension ext = new RFrameExtension();
//        f.setObject(EXTENSION_SLOT, ext);
//        return ext;
//    }
//
//    public static RFrameExtension installHashedExtension(Frame f, int size) {
//        RFrameExtension ext = new RFrameExtension.Hashed(size);
//        f.setObject(EXTENSION_SLOT, ext);
//        return ext;
//    }
//
//    public static REnvironment getEnvironment(Frame f) {
//        REnvironment e = Utils.cast(f.getObject(ENVIRONMENT_SLOT));
//        if (e == null) {
//            e = new EnvironmentImpl(f);
//            f.setObject(ENVIRONMENT_SLOT, e);
//        }
//        return e;
//    }
//
//    private static void markDirty(Frame f, int pos) {
//        int rawpos = pos + RESERVED_SLOTS;
//        long l = f.getLong(rawpos) | DIRTY_MASK;
//        f.setLong(rawpos, l);
//    }
//
//    private static void markDirty(Frame enclosing, RSymbol sym) {
//        Frame current = enclosing;
//        while (current != null) {
//            int pos;
//            if ((pos = getPositionInWS(current, sym)) >= 0) {
//                markDirty(current, pos);
//                return;
//            }
//            current = getParent(enclosing); // BUG: should be current
//        }
//        sym.markDirty();
//    }
//
//    private static class RFrameExtension {
//
//        protected int used = 0;
//        private int capacity = 10;
//        // NOTE: we need a third counter for the last value use for storing the lastUsed value in case of removal
//
//        private int bloom; // This bloom filter comes from Alex B. (renjin)
//        // Does it make any sense ? for this dynamic structures
//
//        // TODO Merge this two arrays, and use unsafe casts
//        private RSymbol[] names = new RSymbol[capacity];
//        private RAny[] values = new RAny[capacity];
//
//        protected RAny get(RSymbol name) {
//            int pos = getPosition(name);
//            if (pos >= 0) {
//                return values[pos];
//            }
//            return null;
//        }
//
//        protected int getPosition(RSymbol name) {
//            if (FunctionImpl.isIn(name.hash(), bloom)) {
//                RSymbol[] n = names;
//                for (int i = 0; i < used; i++) {
//                    if (n[i] == name) {
//                        return i;
//                    }
//                }
//            }
//            return -1;
//        }
//
//        protected void put(Frame enclosing, RSymbol sym, RAny val) {
//            int pos = used;
//            if (pos == capacity) {
//                expand(capacity * 2);
//            }
//            used++;
//            names[pos] = sym;
//            values[pos] = val;
//            val.ref();
//
//            markDirty(getParent(enclosing), sym);
//                // the put method only gets called when the current write set does not have the value,
//                // so we do not have to check the current write set and can immediately go to the parent
//                // FIXME: handle environments that are not connected to top-level
//            bloom |= sym.id();
//        }
//
//        private void writeAt(int pos, RAny value) { // TODO or not TODO assert that the good name is still here
//            assert Utils.check(pos < used);
//            if (values[pos] != value) {
//                values[pos] = value;
//                value.ref();
//            }
//        }
//
//        protected void expand(int newCap) {
//            assert Utils.check(newCap > capacity);
//            RSymbol[] newNames = new RSymbol[newCap];
//            RAny[] newValues = new RAny[newCap];
//            System.arraycopy(names, 0, newNames, 0, used);
//            System.arraycopy(values, 0, newValues, 0, used);
//            names = newNames;
//            values = newValues;
//            capacity = newCap;
//        }
//
//        protected RSymbol[] validNames() { // TODO: revisit when deletion is implemented
//            int nremoved = 0;
//            for (int i = 0; i < used; i++) {
//                if (values[i] == null) {
//                    nremoved++;
//                }
//            }
//            if (nremoved == 0 && used == names.length) {
//                return names;
//            } else {
//                int size = used - nremoved;
//                RSymbol[] vnames = new RSymbol[size];
//                int j = 0;
//                for (int i = 0; i < used; i++) {
//                    if (values[i] != null) {
//                        vnames[j++] = names[i];
//                    }
//                }
//                return vnames;
//            }
//        }
//
//        private static final class Hashed extends RFrameExtension {
//
//            private HashMap<RSymbol, Integer> map; // FIXME: use a primitive map
//
//            private Hashed(int size) {
//                map = new HashMap<RSymbol, Integer>(size);
//            }
//
//            @Override
//            protected int getPosition(RSymbol name) {
//                Integer pos = map.get(name);
//                if (pos != null) {
//                    return pos.intValue();
//                } else {
//                    return -1;
//                }
//            }
//
//            @Override
//            protected void put(Frame enclosing, RSymbol sym, RAny val) {
//                map.put(sym, used);
//                super.put(enclosing, sym, val);
//            }
//        }
//
//    }
//
//    public static RSymbol[] listSymbols(Frame frame) {
//        RSymbol[] ws = getFunction(frame).localWriteSet();
//        RFrameExtension ext = getExtension(frame);
//        if (ext != null) {
//            RSymbol[] es = ext.validNames();
//            if (ws.length == 0) {
//                return es;
//            }
//            int size = ws.length + es.length;
//            RSymbol[] res = new RSymbol[size];
//            System.arraycopy(ws, 0, res, 0, ws.length);
//            System.arraycopy(es, 0, res, ws.length, es.length);
//            return res;
//
//        } else {
//            return ws;
//        }
//    }
}
