package r.data;

import java.util.*;

import r.*;
import r.data.RFunction.*;
import r.data.internal.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;

// FIXME: make sure that there is no non-inlineable recursion on VirtualFrame (extract recursion to work on MaterializedFrame, or cut off)
// FIXME: make sure that there is no non-inlineable loop on VirtualFrame (extract looping to work on MaterializedFrame, or cut off)
// FIXME: make sure there is no pointer comparison on VirtualFrame
// FIXME: refactor super* calls (is the return value still needed? superWriteViaReadSetAndTopLevel could be re-used in superWriteViaWriteSetSlowPath

// FIXME: if there was a way to refactor without incurring performance overhead, it would be great (read, superWrite and match operations have a lot in common)

public class RFrameHeader extends Arguments {
    final Object[] arguments;
    Object returnValue;  // for top-level frames, used to store REnvironment
    boolean isDirty;

    final MaterializedFrame enclosingFrame;
    final RFunction function;
    RFrameExtension extension;
    REnvironment environment;

    public RFrameHeader(RFunction function, MaterializedFrame enclosingFrame, Object[] arguments) {
        this.function = function;
        this.enclosingFrame = enclosingFrame;
        this.arguments = arguments;
    }


    public Frame enclosingFrame() {
        return enclosingFrame;
    }

    public RFunction function() {
        return function;
    }

    private RFrameExtension extension() {
        return extension;
    }

    public Object returnValue() {
        assert Utils.check(returnValue instanceof RAny);
        return returnValue;
    }

    private REnvironment rootEnvironment() {
        return (REnvironment) returnValue;
    }

    private boolean isDirty() {
        return isDirty;
    }

    public Object[] arguments() {
        return arguments;
    }

    public static Frame enclosingFrame(Frame f) {
        return header(f).enclosingFrame;
    }

    public static RFunction function(Frame f) {
        return header(f).function();
    }

    private static RFrameExtension extension(Frame f) {
        return header(f).extension();
    }

    public static REnvironment environment(Frame f) {
        RFrameHeader h = header(f);
        REnvironment env = h.environment;
        if (env == null) {
            env = new EnvironmentImpl(f.materialize());
            h.environment = env;
        }
        return env;
    }

    public static Object returnValue(Frame f) {
        return header(f).returnValue();
    }

    private static REnvironment rootEnvironment(Frame f) {
        return header(f).rootEnvironment();
    }

    public static boolean isDirty(Frame f) {
        return header(f).isDirty();
    }

    public static Object[] arguments(Frame f) {
        return header(f).arguments();
    }

    public static RFrameHeader header(Frame f) {
        return (RFrameHeader) f.getArguments();
    }

    public static void setReturnValue(Frame f, RAny value) {
        header(f).returnValue = value;
    }

    public static void setRootEnvironment(Frame f, REnvironment env) {
        header(f).returnValue = env;
    }

    public static RFrameExtension installExtension(Frame f) {
        RFrameExtension ext = new RFrameExtension();
        header(f).extension = ext;
        return ext;
    }

    public static RFrameExtension installHashedExtension(Frame f, int size) {
        RFrameExtension ext = new RFrameExtension.Hashed(size);
        header(f).extension = ext;
        return ext;
    }

    public boolean hasVariable(RSymbol sym) {
        return function().isInWriteSet(sym);
    }

    public static FrameSlot findVariable(Frame frame, RSymbol symbol) {
        FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
        return frameDescriptor.findFrameSlot(symbol);
    }

    private void markDirty() {
        isDirty = true;
    }

    public static Object readViaWriteSet(Frame frame, FrameSlot slot, RSymbol symbol) {
        Object value = RFrameHeader.getObject(frame, slot);

        if (value != null) {  // TODO: another node (one branch needs to have deopt)
            return value;
        } else {
            return readViaWriteSetSlowPath(frame, symbol);
        }
    }

    public static Object readViaWriteSetSlowPath(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);

        EnclosingSlot rse = readSetEntry(frame, symbol);
        if (rse == null) {
            return readFromExtensionsAndRootLevelEntry(frame, symbol);
        } else {
            Frame enclosingFrame = enclosingFrame(frame); // enclosingFrame is MaterializedFrame
            return readViaEnclosingSlot(enclosingFrame, rse.hops - 1, rse.slot, symbol, enclosingFrame);
        }
    }

    public static Object readFromExtensionsAndRootLevelEntry(Frame childFrame, RSymbol symbol) {
        assert Utils.check(childFrame != null);
        // childFrame can be VirtualFrame

        Frame enclosing = enclosingFrame(childFrame);
        if (enclosing == null) {
            return readFromRootLevel(childFrame, symbol);
        }
        RFrameExtension ext = extension(enclosing);
        if (ext != null) {
            Object val = ext.get(symbol);
            if (val != null) {
                return val;
            }
        }
        return readFromExtensionsAndRootLevel(enclosing, symbol);
    }

    // this method does NOT check the extension of childFrame
    public static Object readFromExtensionsAndRootLevel(Frame childFrame, RSymbol symbol) {
        assert Utils.check(childFrame != null);
        assert Utils.check(childFrame instanceof MaterializedFrame);

        Frame f = childFrame;
        for (;;) {
            Frame enclosing = enclosingFrame(f);
            if (enclosing == null) {
                return readFromRootLevel(f, symbol);
            }
            f = enclosing;
            RFrameExtension ext = extension(f);
            if (ext != null) {
                Object val = ext.get(symbol);
                if (val != null) {
                    return val;
                }
            }
        }
    }

    // this method checks the extension of frame
    public static Object readFromExtensionsAndRootLevelInclusive(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);
        assert Utils.check(frame instanceof MaterializedFrame);

        Frame f = frame;
        for (;;) {
            RFrameExtension ext = extension(f);
            if (ext != null) {
                Object val = ext.get(symbol);
                if (val != null) {
                    return val;
                }
            }

            Frame enclosing = enclosingFrame(f);
            if (enclosing == null) {
                return readFromRootLevel(f, symbol);
            }
            f = enclosing;
        }
    }

    public static Object readViaReadSet(Frame frame, int hops, FrameSlot slot, RSymbol symbol) {
        assert Utils.check(hops != 0);
        Frame enclosing = enclosingFrame(frame); // enclosing is MaterializedFrame
        return readViaEnclosingSlot(enclosing, hops - 1, slot, symbol, frame);
    }

    private static Object readViaEnclosingSlot(Frame frame, int frameHops, FrameSlot frameSlot, RSymbol symbol, Frame firstFrame) {
        assert Utils.check(frame instanceof MaterializedFrame);
        assert Utils.check(frame != null);

        Frame f = frame;
        int hops = frameHops;
        FrameSlot slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = enclosingFrame(frame);
            }
            if (isDirty(f)) {
                Object res = readFromExtensionEntry(first, symbol, f);
                if (res != null) {
                    return Utils.cast(res);
                }
            }
            // no inserted extension slot
            Object res = RFrameHeader.getObject(f, slot);
            if (res != null) {
                return Utils.cast(res);
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = findEnclosingVariable(f, symbol);
            if (eslot == null) {
                return readFromExtensionsAndRootLevel(f, symbol);
            }
            // try the next enclosing slot
            f = enclosingFrame(f);
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    private static Object readFromExtension(Frame frame, RSymbol symbol, Frame stopFrame) {
        assert Utils.check(frame == null || frame instanceof MaterializedFrame);
        assert Utils.check(stopFrame == null || stopFrame instanceof MaterializedFrame);

        for (Frame f = frame; f != stopFrame; f = enclosingFrame(f)) {
            RFrameExtension ext = extension(f);
            if (ext != null) {
                Object res = ext.get(symbol);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    public static Object readFromExtensionEntry(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);
        // note: frame can be a VirtualFrame

        RFrameExtension ext = extension(frame);
        if (ext != null) {
            Object res = ext.get(symbol);
            if (res != null) {
                return res;
            }
        }
        return readFromExtension(enclosingFrame(frame), symbol, null);
    }

    public static Object readFromExtensionEntry(Frame frame, RSymbol symbol, Frame stopFrame) {
        // note: frame can be a VirtualFrame
        // note: stopFrame can be == frame (so VirtualFrame), otherwise it is not a Virtual Frame
        assert Utils.check(frame == stopFrame || stopFrame == null || stopFrame instanceof MaterializedFrame);

        if (frame == stopFrame) {
            return null;
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            Object res = ext.get(symbol);
            if (res != null) {
                return res;
            }
        }
        return readFromExtension(enclosingFrame(frame), symbol, stopFrame);
    }

    public static RAny readFromRootLevel(Frame frame, RSymbol sym) {
        return getRootEnvironment(frame).get(sym, true);
    }

    public static RCallable matchViaWriteSet(Frame frame, FrameSlot slot, RSymbol symbol) {
        Object value = RFrameHeader.getObject(frame, slot);

        if (value != null && value instanceof RCallable) {  // TODO: another node (one branch needs to have deopt)
            return Utils.cast(value);
        } else {
            return matchViaWriteSetSlowPath(frame, symbol);
        }
    }

    public static RCallable matchViaWriteSetSlowPath(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);

        EnclosingSlot rse = readSetEntry(frame, symbol);
        if (rse == null) {
            return matchFromExtensionsAndRootLevelEntry(frame, symbol);
        } else {
            Frame enclosingFrame = enclosingFrame(frame); // enclosingFrame is MaterializedFrame
            return matchViaEnclosingSlot(enclosingFrame, rse.hops - 1, rse.slot, symbol, enclosingFrame);
        }
    }

    public static RCallable matchFromExtensionEntry(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);
        // note: frame can be a VirtualFrame

        RFrameExtension ext = extension(frame);
        if (ext != null) {
            Object res = ext.get(symbol);
            if (res != null && res instanceof RCallable) {
                return (RCallable) res;
            }
        }
        return matchFromExtension(enclosingFrame(frame), symbol, null);
    }

    public static RCallable matchFromExtensionEntry(Frame frame, RSymbol symbol, Frame stopFrame) {
        // note: frame can be a VirtualFrame
        // note: stopFrame can be ==frame (so VirtualFrame), otherwise it is not a Virtual Frame
        assert Utils.check(frame == stopFrame || stopFrame == null || stopFrame instanceof MaterializedFrame);

        if (frame == stopFrame) {
            return null;
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            Object res = ext.get(symbol);
            if (res != null && res instanceof RCallable) {
                return (RCallable) res;
            }
        }
        return matchFromExtension(enclosingFrame(frame), symbol, stopFrame);
    }

    public static RCallable matchFromExtensionsAndRootLevelEntry(Frame childFrame, RSymbol symbol) {
        assert Utils.check(childFrame != null);
        // childFrame can be a VirtualFrame

        Frame enclosing = enclosingFrame(childFrame);
        if (enclosing == null) {
            return matchFromRootLevel(childFrame, symbol);
        }
        RFrameExtension ext = extension(enclosing);
        if (ext != null) {
            Object val = ext.get(symbol);
            if (val != null && val instanceof RCallable) {
                return (RCallable) val;
            }
        }
        return matchFromExtensionsAndRootLevel(enclosing, symbol);
    }

    public static RCallable matchFromExtensionsAndRootLevelInclusiveEntry(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);
        // frame can be a VirtualFrame

        RFrameExtension ext = extension(frame);
        if (ext != null) {
            Object val = ext.get(symbol);
            if (val != null && val instanceof RCallable) {
                return (RCallable) val;
            }
        }

        Frame enclosing = enclosingFrame(frame);
        if (enclosing == null) {
            return matchFromRootLevel(frame, symbol);
        }

        ext = extension(enclosing);
        if (ext != null) {
            Object val = ext.get(symbol);
            if (val != null && val instanceof RCallable) {
                return (RCallable) val;
            }
        }
        return matchFromExtensionsAndRootLevel(enclosing, symbol);
    }

    private static RCallable matchFromExtensionsAndRootLevel(Frame childFrame, RSymbol symbol) {
        assert Utils.check(childFrame != null);
        assert Utils.check(childFrame instanceof MaterializedFrame);

        Frame f = childFrame;
        for (;;) {
            Frame enclosing = enclosingFrame(f);
            if (enclosing == null) {
                return matchFromRootLevel(f, symbol);
            }
            f = enclosing;
            RFrameExtension ext = extension(f);
            if (ext != null) {
                Object val = ext.get(symbol);
                if (val != null && val instanceof RCallable) {
                    return (RCallable) val;
                }
            }
        }
    }

    private static RCallable matchFromRootLevel(Frame frame, RSymbol symbol) {
        return getRootEnvironment(frame).match(symbol);
    }

    private static RCallable matchViaEnclosingSlot(Frame frame, int frameHops, FrameSlot frameSlot, RSymbol symbol, Frame firstFrame) {
        assert Utils.check(frame instanceof MaterializedFrame);
        assert Utils.check(frame != null);

        Frame f = frame;
        int hops = frameHops;
        FrameSlot slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = enclosingFrame(frame);
            }
            if (isDirty(f)) {
                RCallable res = matchFromExtensionEntry(first, symbol, f);
                if (res != null) {
                    return Utils.cast(res);
                }
            }
            // no extension inserted slot
            Object res = RFrameHeader.getObject(f, slot);
            if (res != null && res instanceof RCallable) {
                return Utils.cast(res);
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = findEnclosingVariable(f, symbol);
            if (eslot == null) {
                return matchFromExtensionsAndRootLevel(f, symbol);
            }
            // try the next enclosing slot
            f = enclosingFrame(f);
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    public static RCallable matchFromExtension(Frame frame, RSymbol symbol, Frame stopFrame) {
        assert Utils.check(frame == null || frame instanceof MaterializedFrame);
        assert Utils.check(stopFrame == null || stopFrame instanceof MaterializedFrame);
        assert Utils.check(frame != null || stopFrame == null);

        for (Frame f = frame; f != stopFrame; f = enclosingFrame(f)) {
            RFrameExtension ext = extension(f);
            if (ext != null) {
                Object res = ext.get(symbol);
                if (res != null && res instanceof RCallable) {
                    return (RCallable) res;
                }
            }
        }
        return null;
    }

    public static RCallable matchViaReadSet(Frame frame, int hops, FrameSlot slot, RSymbol symbol) {
        assert Utils.check(hops != 0);
        Frame enclosing = enclosingFrame(frame); // enclosing is MaterializedFrame
        return matchViaEnclosingSlot(enclosing, hops - 1, slot, symbol, frame);
    }

    // in contrast to e.g. read, match can be called with a null frame
    // FIXME: this may need to be adapted to work with eval
    public static RCallable match(Frame frame, RSymbol symbol) {

        if (frame == null) {
            return REnvironment.GLOBAL.match(symbol);  // FIXME: get rid of this special case, FIX it for eval
        }
        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            return matchViaWriteSet(frame, slot, symbol);
        }
        EnclosingSlot eslot = findEnclosingVariable(frame, symbol);
        if (eslot != null) {
            return matchViaEnclosingSlot(enclosingFrame(frame), eslot.hops - 1, eslot.slot, symbol, frame);
        }
        return matchFromExtensionsAndRootLevelInclusiveEntry(frame, symbol);
    }

    public static REnvironment getRootEnvironment(Frame frame) {
        if (frame == null) { // FIXME: get rid of this branch
            return REnvironment.GLOBAL;
        }
        REnvironment env = rootEnvironment(frame);
        if (env == null) {  // FIXME: get rid of this branch
            return REnvironment.GLOBAL;
        }
        return env;
    }

    public static EnclosingSlot readSetEntry(Frame frame, RSymbol symbol) {
        return function(frame).getLocalReadSetEntry(symbol);
    }

    public static EnclosingSlot findEnclosingVariable(Frame frame, RSymbol symbol) {
        return function(frame).enclosingSlot(symbol);
    }

    public static Object localRead(Frame frame, RSymbol symbol) {
        assert Utils.check(frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            return Utils.cast(RFrameHeader.getObject(frame, slot));
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            return ext.get(symbol);
        }
        return null;
    }

    public static Object customLocalRead(Frame frame, RSymbol sym) {
        assert Utils.check(frame instanceof MaterializedFrame);

        RFrameExtension ext = extension(frame);
        return ext.get(sym);
    }

    public static Object customRead(Frame frame, RSymbol symbol) {
        assert Utils.check(frame instanceof MaterializedFrame);

        RAny val;
        RFrameExtension ext = extension(frame);
        val = Utils.cast(ext.get(symbol));
        if (val != null) {
            return val;
        }
        Frame parent = enclosingFrame(frame);
        if (parent != null) {
            return read(parent, symbol);
        } else {
            val = readFromRootLevel(frame, symbol);
        }
        return val;
    }

    public static Object read(Frame frame, RSymbol symbol) {
        assert Utils.check(frame != null);
        assert Utils.check(frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            return readViaWriteSet(frame, slot, symbol);
        }
        EnclosingSlot eslot = findEnclosingVariable(frame, symbol);
        if (eslot != null) {
            return readViaEnclosingSlot(enclosingFrame(frame), eslot.hops - 1, eslot.slot, symbol, frame);
        }
        return readFromExtensionsAndRootLevelInclusive(frame, symbol);
    }

    public static boolean localExists(Frame frame, RSymbol symbol) {
        assert Utils.check(frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            return RFrameHeader.getObject(frame, slot) != null;
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            return ext.get(symbol) != null;
        }
        return false;
    }

    public static boolean customLocalExists(Frame frame, RSymbol symbol) {
        assert Utils.check(frame instanceof MaterializedFrame);

        RFrameExtension ext = extension(frame);
        return ext.get(symbol) != null;
    }

    public static boolean customExists(Frame frame, RSymbol symbol) {
        assert Utils.check(frame instanceof MaterializedFrame);

        RFrameExtension ext = extension(frame);
        if (ext.get(symbol) != null) {
            return true;
        }
        Frame parent = enclosingFrame(frame);
        if (parent != null) {
            return exists(parent, symbol);
        } else {
            return readFromRootLevel(frame, symbol) != null;
        }
    }

    public static boolean exists(Frame frame, RSymbol symbol) {
        assert Utils.check(frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            if (RFrameHeader.getObject(frame, slot) != null) {
                return true;
            }
        }
        EnclosingSlot rse = readSetEntry(frame, symbol);
        if (rse != null) {
            return existsViaReadSet(frame, rse.hops - 1, rse.slot, symbol);
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            if (ext.get(symbol) != null) {
                return true;
            }
        }
        Frame enclosing = enclosingFrame(frame);
        if (enclosing != null) {
            return exists(enclosing, symbol); // NOTE: recursion
        } else {
            return readFromRootLevel(frame, symbol) != null;
        }
    }

    public static boolean existsViaReadSet(Frame frame, int hops, FrameSlot slot, RSymbol symbol) {
        assert Utils.check(hops != 0);
        assert Utils.check(frame instanceof MaterializedFrame);

        Frame enclosing = enclosingFrame(frame);
        if (existsViaReadSet(enclosing, hops - 1, slot, symbol, frame)) {
            return true;
        } else {
            return readFromRootLevel(enclosing, symbol) != null;
        }
    }

    private static boolean existsViaReadSet(Frame frame, int hops, FrameSlot slot, RSymbol symbol, Frame first) {
        assert Utils.check(frame instanceof MaterializedFrame);

        if (hops == 0) {
            if (RFrameHeader.getObject(frame, slot) != null) {
                return true;
            }
            if (isDirty(frame)) {
                if (existsInExtension(first, symbol, frame)) {
                    return true;
                }
            }
            Frame enclosing = enclosingFrame(frame);
            if (enclosing == null) {
                return false;
            }
            EnclosingSlot rse = readSetEntry(frame, symbol);
            if (rse == null) {
                // TODO: is this wrong? what if there is a slot, but simply isn't read non-reflectively
                return existsInExtension(frame, symbol, null);
            }
            return existsViaReadSet(enclosing, rse.hops - 1, rse.slot, symbol, enclosing);
        } else {
            return existsViaReadSet(enclosingFrame(frame), hops - 1, slot, symbol, first);
        }
    }

    public static boolean existsInExtension(Frame frame, RSymbol sym, Frame stopFrame) {
        assert Utils.check(frame instanceof MaterializedFrame);

        if (frame == stopFrame) {
            return false;
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            if (ext.get(sym) != null) {
                return true;
            }
        }
        return existsInExtension(enclosingFrame(frame), sym, stopFrame); // NOTE: recursion
    }


    public static void writeToTopLevelCondRef(RSymbol sym, RAny value) {
        Object oldValue = sym.getValue();
        if (oldValue != value) {
            sym.setValue(value);
            value.ref();
        }
    }

    public static void writeToTopLevelNoRef(RSymbol symbol, Object value) {
        symbol.setValue(value);
    }

    public static void writeToTopLevelRef(RSymbol sym, RAny value) {
        sym.setValue(value);
        value.ref();
    }

    public static void writeToExtension(Frame frame, RSymbol sym, RAny value) {
        RFrameExtension ext = extension(frame);
        if (ext == null) {
            ext = installExtension(frame);
            ext.putRef(frame, sym, value);
        } else {
            int pos = ext.getPosition(sym);
            if (pos >= 0) {
                ext.writeAtRef(pos, value);
            } else {
                ext.putRef(frame, sym, value);
            }
        }
    }

    public static void writeToExtensionNoRef(Frame frame, RSymbol sym, Object value) {
        RFrameExtension ext = extension(frame);
        if (ext == null) {
            ext = installExtension(frame);
            ext.putNoRef(frame, sym, value); // The extension is brand new, we can use the first slot safely
        } else {
            int pos = ext.getPosition(sym);
            if (pos >= 0) {
                ext.writeAtNoRef(pos, value);
            } else {
                ext.putNoRef(frame, sym, value);
            }
        }
    }

    public static void writeAtCondRef(Frame f, FrameSlot slot, RAny value) {
        Object oldContent = RFrameHeader.getObject(f, slot);
        if (value != oldContent) {
            f.setObject(slot, value);
            value.ref();
        }
    }

    public static void writeAtNoRef(Frame f, FrameSlot slot, Object value) {
        f.setObject(slot, value);
    }

    public static void writeAtRef(Frame f, FrameSlot slot, Object value) {
        f.setObject(slot, value);
        ((RAny) value).ref();
    }

    public static void writeAtRef(Frame f, FrameSlot slot, RAny value) {
        f.setObject(slot, value);
        value.ref();
    }

    public static boolean superWriteViaWriteSet(Frame enclosingFrame, FrameSlot slot, RSymbol symbol, RAny value) {
        assert Utils.check(enclosingFrame instanceof MaterializedFrame);

        Object oldVal = RFrameHeader.getObject(enclosingFrame, slot);
        if (oldVal != null) {
            if (oldVal != value) {
                RFrameHeader.writeAtNoRef(enclosingFrame, slot, value);
                value.ref();
            }
            return true;
        } else {
            return superWriteViaWriteSetSlowPath(enclosingFrame, symbol, value);
        }
    }

    public static boolean superWriteViaWriteSetSlowPath(Frame frame, RSymbol symbol, RAny value) {
        assert Utils.check(frame instanceof MaterializedFrame);

        EnclosingSlot eslot = findEnclosingVariable(frame, symbol);
        Frame enclosing = enclosingFrame(frame);
        if (eslot == null) {
            return superWriteToExtensionsAndTopLevel(enclosing, symbol, value);
        }

        if (superWriteViaEnclosingSlot(enclosing, eslot.hops - 1, eslot.slot, symbol, value, enclosing)) {
            return true;
        }
        return superWriteToTopLevel(symbol, value);
    }

    public static boolean superWriteToExtensionsAndTopLevel(Frame frame, RSymbol symbol, RAny value) {
        assert Utils.check(frame instanceof MaterializedFrame);

        if (superWriteToExtensionEntry(frame, symbol, value)) {
            return true;
        }
        return superWriteToTopLevel(symbol, value);
    }

    public static boolean superWriteToTopLevel(RSymbol symbol, RAny value) {
        // FIXME: allow modification of builtins
        writeToTopLevelCondRef(symbol, value);
        return true;
    }

    public static boolean superWriteToExtensionEntry(Frame frame, RSymbol symbol, RAny value) {
        assert Utils.check(frame != null);
        // note: frame can be a VirtualFrame

        RFrameExtension ext = extension(frame);
        if (ext != null) {
            int epos = ext.getPosition(symbol);
            if (epos != -1) {
                ext.writeAtRef(epos, value);
                return true;
            }
        }
        return superWriteToExtension(enclosingFrame(frame), symbol, value, null);
    }

    public static boolean superWriteToExtensionEntry(Frame frame, RSymbol symbol, RAny value, Frame stopFrame) {
        // note: frame can be a VirtualFrame
        // note: stopFrame can be == frame (so VirtualFrame), otherwise it is not a Virtual Frame
        assert Utils.check(frame == stopFrame || stopFrame == null || stopFrame instanceof MaterializedFrame);

        if (frame == stopFrame) {
            return false;
        }
        RFrameExtension ext = extension(frame);
        if (ext != null) {
            int epos = ext.getPosition(symbol);
            if (epos != -1) {
                ext.writeAtRef(epos, value);
                return true;
            }
        }
        return superWriteToExtension(enclosingFrame(frame), symbol, value, stopFrame);
    }

    private static boolean superWriteToExtension(Frame frame, RSymbol symbol, RAny value, Frame stopFrame) {
        assert Utils.check(frame == null || frame instanceof MaterializedFrame);
        assert Utils.check(stopFrame == null || stopFrame instanceof MaterializedFrame);

        for (Frame f = frame; f != stopFrame; f = enclosingFrame(f)) {
            RFrameExtension ext = extension(f);
            if (ext != null) {
                int epos = ext.getPosition(symbol);
                if (epos != -1) {
                    ext.writeAtRef(epos, value);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean superWriteViaEnclosingSlotAndTopLevel(Frame frame, int hops, FrameSlot slot, RSymbol symbol, RAny value) {
        assert Utils.check(hops != 0);
        assert Utils.check(frame instanceof MaterializedFrame);

        if (superWriteViaEnclosingSlot(enclosingFrame(frame), hops - 1, slot, symbol, value, frame)) {
            return true;
        } else {
            return superWriteToTopLevel(symbol, value);
        }
    }

    private static boolean superWriteViaEnclosingSlot(Frame frame, int frameHops, FrameSlot frameSlot, RSymbol symbol, RAny value, Frame firstFrame) {
        assert Utils.check(frame instanceof MaterializedFrame);
        assert Utils.check(frame != null);

        Frame f = frame;
        int hops = frameHops;
        FrameSlot slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = enclosingFrame(frame);
            }
            Object val;
            if (isDirty(f)) {
                if (superWriteToExtensionEntry(first, symbol, value, f)) {
                    return true;
                }
            }
            // no inserted extension slot
            val = RFrameHeader.getObject(f, slot);
            if (val != null) {
                writeAtRef(f, slot, value);
                return true;
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = findEnclosingVariable(f, symbol);
            if (eslot == null) {
                return superWriteToExtensionsAndTopLevel(f, symbol, value);
            }
            // try the next enclosing slot
            f = enclosingFrame(f);
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    public static void localWrite(Frame frame, RSymbol symbol, RAny value) {
        assert Utils.check(frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            writeAtRef(frame, slot, value);
        } else {
            writeToExtension(frame, symbol, value); // marks the defining slot dirty
        }
    }

    public static void localWriteNoRef(Frame frame, RSymbol symbol, Object value) {
        assert Utils.check(frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            writeAtNoRef(frame, slot, value);
        } else {
            writeToExtensionNoRef(frame, symbol, value); // marks the defining slot dirty
        }
    }

    // this is like "superWrite" - but starts with current frame
    // used for assign with inherits == TRUE
    public static void reflectiveInheritsWrite(Frame frame, RSymbol symbol, RAny value) {
        assert Utils.check(frame != null && frame instanceof MaterializedFrame);

        FrameSlot slot = findVariable(frame, symbol);
        if (slot != null) {
            superWriteViaWriteSet(frame, slot, symbol, value);
            return;
        }
        EnclosingSlot eslot = findEnclosingVariable(frame, symbol);
        if (eslot != null) {
            superWriteViaEnclosingSlotAndTopLevel(frame, eslot.hops, eslot.slot, symbol, value);
            return;
        }
        superWriteToExtensionsAndTopLevel(frame, symbol, value);
    }

    public static void customLocalWrite(Frame f, RSymbol symbol, RAny value) {
        RFrameExtension ext = extension(f);
        int pos = ext.getPosition(symbol);
        if (pos >= 0) {
            ext.writeAtRef(pos, value);
        } else {
            ext.putRef(f, symbol, value);
        }
    }

    public static void customLocalWriteNoRef(Frame f, RSymbol symbol, Object value) {
        RFrameExtension ext = extension(f);
        int pos = ext.getPosition(symbol);
        if (pos >= 0) {
            ext.writeAtNoRef(pos, value);
        } else {
            ext.putNoRef(f, symbol, value);
        }
    }

    public static void customReflectiveInheritsWrite(Frame frame, RSymbol symbol, RAny value) { // used for assign with inherits == TRUE
        RFrameExtension ext = extension(frame);
        int epos = ext.getPosition(symbol);
        if (epos != -1) {
            ext.writeAtRef(epos, value);
            return;
        }
        Frame parentFrame = enclosingFrame(frame);
        if (parentFrame != null) {
            reflectiveInheritsWrite(parentFrame, symbol, value);
        } else {
            superWriteToTopLevel(symbol, value);
        }
    }

    private static void markDirty(Frame enclosing, RSymbol sym) {
        Frame current = enclosing;
        while (current != null) {
            RFrameHeader currentHeader = header(enclosing);
            if (currentHeader.hasVariable(sym)) {
                currentHeader.markDirty();
                return;
            }
            current = enclosingFrame(current);
        }
        sym.markDirty();
    }

    private static class RFrameExtension {

        protected int used = 0;
        private int capacity = 10;
        // NOTE: we need a third counter for the last value use for storing the lastUsed value in case of removal

        private int bloom; // This bloom filter comes from Alex B. (renjin)
        // Does it make any sense ? for this dynamic structures

        // TODO Merge this two arrays, and use unsafe casts
        private RSymbol[] names = new RSymbol[capacity];
        private Object[] values = new Object[capacity];

        protected Object get(RSymbol name) {
            int pos = getPosition(name);
            if (pos >= 0) {
                return RPromise.force(values[pos]);
            }
            return null;
        }

        protected int getPosition(RSymbol name) {
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

        final void putRef(Frame enclosing, RSymbol sym, RAny val) {
            putNoRef(enclosing, sym, val);
            val.ref();
        }

        protected void putNoRef(Frame enclosing, RSymbol sym, Object val) {
            int pos = used;
            if (pos == capacity) {
                expand(capacity * 2);
            }
            used++;
            names[pos] = sym;
            values[pos] = val;

            markDirty(enclosingFrame(enclosing), sym);
                // the put method only gets called when the current write set does not have the value,
                // so we do not have to check the current write set and can immediately go to the parent
                // FIXME: handle environments that are not connected to top-level
            bloom |= sym.id();
        }

        private void writeAtRef(int pos, RAny value) { // TODO or not TODO assert that the good name is still here
            assert Utils.check(pos < used);
            if (values[pos] != value) {
                values[pos] = value;
                value.ref();
            }
        }

        private void writeAtNoRef(int pos, Object value) { // TODO or not TODO assert that the good name is still here
            assert Utils.check(pos < used);
            if (values[pos] != value) {
                values[pos] = value;
            }
        }

        protected void expand(int newCap) {
            assert Utils.check(newCap > capacity);
            RSymbol[] newNames = new RSymbol[newCap];
            RAny[] newValues = new RAny[newCap];
            System.arraycopy(names, 0, newNames, 0, used);
            System.arraycopy(values, 0, newValues, 0, used);
            names = newNames;
            values = newValues;
            capacity = newCap;
        }

        protected RSymbol[] validNames() { // TODO: revisit when deletion is implemented
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

        private static final class Hashed extends RFrameExtension {

            private HashMap<RSymbol, Integer> map; // FIXME: use a primitive map

            private Hashed(int size) {
                map = new HashMap<RSymbol, Integer>(size);
            }

            @Override
            protected int getPosition(RSymbol name) {
                Integer pos = map.get(name);
                if (pos != null) {
                    return pos.intValue();
                } else {
                    return -1;
                }
            }

            @Override
            protected void putNoRef(Frame enclosing, RSymbol sym, Object val) {
                map.put(sym, used);
                super.putNoRef(enclosing, sym, val);
            }
        }
    }

    public static RSymbol[] listSymbols(Frame frame) {
        RSymbol[] ws = function(frame).localWriteSet();
        RFrameExtension ext = extension(frame);
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


    public static Object getObject(Frame frame, FrameSlot slot) {
        return RPromise.force(frame.getObject(slot));
    }
}
