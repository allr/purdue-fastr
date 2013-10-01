package r.runtime;

import r.*;
import r.data.*;
import r.data.RFunction.EnclosingSlot;
import r.data.internal.*;

public abstract class Frame {

    public static final boolean MATERIALIZE_ON_ASSIGNMENT = false;

    Object returnValue;  // for top-level frames, used to store REnvironment
    boolean isDirty; // FIXME: move down? empty frames can't be dirty
    final Frame enclosingFrame;
    final RFunction function;
    FrameExtension extension;
    REnvironment environment;

    public Frame(RFunction function, Frame enclosingFrame) {
        this.function = function;
        this.enclosingFrame = enclosingFrame;
    }

    public abstract FrameDescriptor descriptor();
    public abstract Object get(int i);
    public abstract void set(int i, Object value);


    public Frame enclosingFrame() {
        return enclosingFrame;
    }

    public RFunction function() {
        return function;
    }

    private FrameExtension extension() {
        return extension;
    }

    public Object returnValue() {
        assert Utils.check(returnValue instanceof RAny);
        return returnValue;
    }

    private REnvironment rootEnvironment() {
        if (returnValue != null) { // FIXME: get rid of this branch
            return (REnvironment) returnValue;
        } else {
            return REnvironment.GLOBAL;
        }
    }

    private boolean isDirty() {
        return isDirty;
    }

    public REnvironment environment() {
        if (environment == null) {
            environment = new EnvironmentImpl(this);
        }
        return environment;
    }

    public void returnValue(RAny value) {
        returnValue = value;
    }

    public void rootEnvironment(REnvironment rootEnvironment) {
        this.returnValue = rootEnvironment;
    }

    public FrameExtension installExtension() {
        extension = new FrameExtension();
        return extension;
    }

    public FrameExtension installHashedExtension(int size) {
        extension = new FrameExtension.Hashed(size);
        return extension;
    }

    public boolean hasLocalVariable(RSymbol sym) {
        return function().hasLocalSlot(sym);
    }

    public int findVariable(RSymbol symbol) {
        return descriptor().findFrameSlot(symbol);
    }

    private void markDirty() {
        isDirty = true;
    }

    public Object readViaWriteSet(int slot, RSymbol symbol) {
        Object value = get(slot);
        if (value != null) {
            return value;
        } else {
            return readViaWriteSetSlowPath(symbol);
        }
    }

    public Object readViaWriteSetSlowPath(RSymbol symbol) {

        EnclosingSlot rse = readSetEntry(symbol);
        if (rse == null) {
            return readFromExtensionsAndRootLevelEntry(symbol);
        } else {
            return enclosingFrame.readViaEnclosingSlot(rse.hops - 1, rse.slot, symbol, enclosingFrame);
        }
    }

    // this is called on a child (!)
    public Object readFromExtensionsAndRootLevelEntry(RSymbol symbol) {

        if (enclosingFrame == null) {
            return readFromRootLevel(symbol);
        }
        FrameExtension ext = enclosingFrame.extension();
        if (ext != null) {
            Object value = ext.getForcingPromises(symbol);
            if (value != null) {
                return value;
            }
        }
        return enclosingFrame.readFromExtensionsAndRootLevel(symbol);
    }

    // this method does NOT check the extension of childFrame
    public Object readFromExtensionsAndRootLevel(RSymbol symbol) {

        Frame f = this;
        for (;;) {
            Frame enclosing = f.enclosingFrame();
            if (enclosing == null) {
                return f.readFromRootLevel(symbol);
            }
            f = enclosing;
            FrameExtension ext = f.extension();
            if (ext != null) {
                Object value = ext.getForcingPromises(symbol);
                if (value != null) {
                    return value;
                }
            }
        }
    }

    // this method checks the extension of frame
    public Object readFromExtensionsAndRootLevelInclusive(RSymbol symbol) {

        Frame f = this;
        for (;;) {
            FrameExtension ext = f.extension();
            if (ext != null) {
                Object value = ext.getForcingPromises(symbol);
                if (value != null) {
                    return value;
                }
            }

            Frame enclosing = f.enclosingFrame();
            if (enclosing == null) {
                return f.readFromRootLevel(symbol);
            }
            f = enclosing;
        }
    }

    public Object readViaReadSet(int hops, int slot, RSymbol symbol) {
        assert Utils.check(hops != 0);
        return enclosingFrame.readViaEnclosingSlot(hops - 1, slot, symbol, this);
    }

    private Object readViaEnclosingSlot(int frameHops, int frameSlot, RSymbol symbol, Frame firstFrame) {

        Frame f = this;
        int hops = frameHops;
        int slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = f.enclosingFrame();
            }
            if (f.isDirty()) {
                Object res = first.readFromExtensionEntry(symbol, f);
                if (res != null) {
                    return Utils.cast(res);
                }
            }
            // no inserted extension slot
            Object res = f.getObjectForcingPromises(slot);
            if (res != null) {
                return Utils.cast(res);
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = f.findEnclosingVariable(symbol);
            if (eslot == null) {
                return f.readFromExtensionsAndRootLevel(symbol);
            }
            // try the next enclosing slot
            f = f.enclosingFrame();
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    private Object readFromExtension(RSymbol symbol, Frame stopFrame) {

        for (Frame f = this; f != stopFrame; f = f.enclosingFrame()) {
            FrameExtension ext = f.extension();
            if (ext != null) {
                Object res = ext.getForcingPromises(symbol);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    public Object readFromExtensionEntry(RSymbol symbol) {

        if (extension != null) {
            Object res = extension.getForcingPromises(symbol);
            if (res != null) {
                return res;
            }
        }
        if (enclosingFrame != null) {
            return enclosingFrame.readFromExtension(symbol, null);
        }
        return null;
    }

    public Object readFromExtensionEntry(RSymbol symbol, Frame stopFrame) {

        if (this == stopFrame) {
            return null;
        }
        if (extension != null) {
            Object res = extension.getForcingPromises(symbol);
            if (res != null) {
                return res;
            }
        }
        return enclosingFrame.readFromExtension(symbol, stopFrame);
    }

    public RAny readFromRootLevel(RSymbol symbol) {
        return rootEnvironment().get(symbol, true);
    }

    public boolean existsFromRootLevel(RSymbol symbol) {
        return rootEnvironment().exists(symbol, true);
    }

    public RCallable matchViaWriteSet(int slot, RSymbol symbol) {
        Object value = getObjectForcingPromises(slot);

        if (value != null && value instanceof RCallable) {  // TODO: another node (one branch needs to have deopt)
            return Utils.cast(value);
        } else {
            return matchViaWriteSetSlowPath(symbol);
        }
    }

    public RCallable matchViaWriteSetSlowPath(RSymbol symbol) {

        EnclosingSlot rse = readSetEntry(symbol);
        if (rse == null) {
            return matchFromExtensionsAndRootLevelEntry(symbol);
        } else {
            return enclosingFrame.matchViaEnclosingSlot(rse.hops - 1, rse.slot, symbol, enclosingFrame);
        }
    }

    public RCallable matchFromExtensionEntry(RSymbol symbol) {

        if (extension != null) {
            Object res = extension.getForcingPromises(symbol);
            if (res != null && res instanceof RCallable) {
                return (RCallable) res;
            }
        }
        if (enclosingFrame != null) {
            return enclosingFrame.matchFromExtension(symbol, null);
        }
        return null;
    }

    public RCallable matchFromExtensionEntry(RSymbol symbol, Frame stopFrame) { // TODO: this == null
        if (this == stopFrame) {
            return null;
        }
        if (extension != null) {
            Object res = extension.getForcingPromises(symbol);
            if (res != null && res instanceof RCallable) {
                return (RCallable) res;
            }
        }
        if (enclosingFrame != null) {
            enclosingFrame.matchFromExtension(symbol, stopFrame);
        }
        return null;
    }

    // starts from a child frame
    public RCallable matchFromExtensionsAndRootLevelEntry(RSymbol symbol) {

        if (enclosingFrame == null) {
            return matchFromRootLevel(symbol);
        }
        FrameExtension ext = enclosingFrame.extension();
        if (ext != null) {
            Object value = ext.getForcingPromises(symbol);
            if (value != null && value instanceof RCallable) {
                return (RCallable) value;
            }
        }
        return enclosingFrame.matchFromExtensionsAndRootLevel(symbol);
    }

    public RCallable matchFromExtensionsAndRootLevelInclusiveEntry(RSymbol symbol) {

        if (extension != null) {
            Object value = extension.getForcingPromises(symbol);
            if (value != null && value instanceof RCallable) {
                return (RCallable) value;
            }
        }

        if (enclosingFrame == null) {
            return matchFromRootLevel(symbol);
        }

        FrameExtension ext = enclosingFrame.extension();
        if (ext != null) {
            Object value = ext.getForcingPromises(symbol);
            if (value != null && value instanceof RCallable) {
                return (RCallable) value;
            }
        }
        return enclosingFrame.matchFromExtensionsAndRootLevel(symbol);
    }

    // called for child
    private RCallable matchFromExtensionsAndRootLevel(RSymbol symbol) {

        Frame f = this;
        for (;;) {
            Frame enclosing = f.enclosingFrame();
            if (enclosing == null) {
                return f.matchFromRootLevel(symbol);
            }
            f = enclosing;
            FrameExtension ext = f.extension();
            if (ext != null) {
                Object value = ext.getForcingPromises(symbol);
                if (value != null && value instanceof RCallable) {
                    return (RCallable) value;
                }
            }
        }
    }

    private RCallable matchFromRootLevel(RSymbol symbol) {
        return rootEnvironment().match(symbol);
    }

    private RCallable matchViaEnclosingSlot(int frameHops, int frameSlot, RSymbol symbol, Frame firstFrame) {

        Frame f = this;
        int hops = frameHops;
        int slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = f.enclosingFrame();
            }
            if (f.isDirty()) {
                RCallable res = first.matchFromExtensionEntry(symbol, f);
                if (res != null) {
                    return Utils.cast(res);
                }
            }
            // no extension inserted slot
            Object res = f.getObjectForcingPromises(slot);
            if (res != null && res instanceof RCallable) {
                return Utils.cast(res);
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = f.findEnclosingVariable(symbol);
            if (eslot == null) {
                return f.matchFromExtensionsAndRootLevel(symbol);
            }
            // try the next enclosing slot
            f = f.enclosingFrame();
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    public RCallable matchFromExtension(RSymbol symbol, Frame stopFrame) { // TODO this == null

        for (Frame f = this; f != stopFrame; f = f.enclosingFrame()) {
            FrameExtension ext = f.extension();
            if (ext != null) {
                Object res = ext.getForcingPromises(symbol);
                if (res != null && res instanceof RCallable) {
                    return (RCallable) res;
                }
            }
        }
        return null;
    }

    public RCallable matchViaReadSet(int hops, int slot, RSymbol symbol) {
        assert Utils.check(hops != 0);
        return enclosingFrame.matchViaEnclosingSlot(hops - 1, slot, symbol, this);
    }

    // in contrast to e.g. read, match can be called with a null frame
    // FIXME: this may need to be adapted to work with eval
    public static RCallable match(Frame frame, RSymbol symbol) { // TODO: this == null

        if (frame == null) {
            return REnvironment.GLOBAL.match(symbol);  // FIXME: get rid of this special case, FIX it for eval
        }
        int slot = frame.findVariable(symbol);
        if (slot != -1) {
            return frame.matchViaWriteSet(slot, symbol);
        }
        EnclosingSlot eslot = frame.findEnclosingVariable(symbol);
        if (eslot != null) {
            return frame.enclosingFrame().matchViaEnclosingSlot(eslot.hops - 1, eslot.slot, symbol, frame);
        }
        return frame.matchFromExtensionsAndRootLevelInclusiveEntry(symbol);
    }

    public EnclosingSlot readSetEntry(RSymbol symbol) {
        return function.getLocalReadSetEntry(symbol);
    }

    public EnclosingSlot findEnclosingVariable(RSymbol symbol) {
        return function.enclosingSlot(symbol);
    }

    public Object localRead(RSymbol symbol) {

        int slot = findVariable(symbol);
        if (slot != -1) {
            return getObjectForcingPromises(slot);
        }
        if (extension != null) {
            return extension.getForcingPromises(symbol);
        }
        return null;
    }

    public Object localReadNotForcing(RSymbol symbol) {

        int slot = findVariable(symbol);
        if (slot != -1) {
            return get(slot);
        }
        if (extension != null) {
            return extension.getNotForcing(symbol);
        }
        return null;
    }


    public Object customLocalRead(RSymbol symbol) {
        return extension.getForcingPromises(symbol);
    }

    public Object customLocalReadNoForcing(RSymbol symbol) {
        return extension.getNotForcing(symbol);
    }

    public Object customRead(RSymbol symbol) {

        RAny value = Utils.cast(extension.getForcingPromises(symbol));
        if (value != null) {
            return value;
        }
        if (enclosingFrame != null) {
            return enclosingFrame.read(symbol);
        } else {
            value = readFromRootLevel(symbol);
        }
        return value;
    }

    public Object read(RSymbol symbol) {

        int slot = findVariable(symbol);
        if (slot != -1) {
            return readViaWriteSet(slot, symbol);
        }
        EnclosingSlot eslot = findEnclosingVariable(symbol);
        if (eslot != null) {
            return enclosingFrame.readViaEnclosingSlot(eslot.hops - 1, eslot.slot, symbol, this);
        }
        return readFromExtensionsAndRootLevelInclusive(symbol);
    }

    public boolean localExists(RSymbol symbol) {  // TODO: this == null
        int slot = findVariable(symbol);
        if (slot != -1) {
            return getObjectForcingPromises(slot) != null;
        }
        if (extension != null) {
            return extension.exists(symbol);
        }
        return false;
    }

    public boolean customLocalExists(RSymbol symbol) {
        return extension.exists(symbol);
    }

    public boolean customExists(RSymbol symbol) {

        if (extension.getForcingPromises(symbol) != null) {
            return true;
        }
        if (enclosingFrame != null) {
            return enclosingFrame.exists(symbol);
        } else {
            return existsFromRootLevel(symbol);
        }
    }

    public boolean exists(RSymbol symbol) {

        int slot = findVariable(symbol);
        if (slot != -1 && get(slot) != null) {
            return true;
        }
        EnclosingSlot eslot = findEnclosingVariable(symbol);
        if (eslot != null) {
            return enclosingFrame.existsViaEnclosingSlot(eslot.hops - 1, eslot.slot, symbol, this);
        }
        return existsFromExtensionsAndRootLevelInclusive(symbol);
    }

    // this method checks the extension of frame
    public boolean existsFromExtensionsAndRootLevelInclusive(RSymbol symbol) {

        Frame f = this;
        for (;;) {
            FrameExtension ext = f.extension();
            if (ext != null && ext.exists(symbol)) {
                return true;
            }

            Frame enclosing = f.enclosingFrame();
            if (enclosing == null) {
                return f.existsFromRootLevel(symbol);
            }
            f = enclosing;
        }
    }

    // this method does NOT check the extension of childFrame
    public boolean existsFromExtensionsAndRootLevel(RSymbol symbol) {

        Frame f = this;
        for (;;) {
            Frame enclosing = f.enclosingFrame();
            if (enclosing == null) {
                return f.existsFromRootLevel(symbol);
            }
            f = enclosing;
            FrameExtension ext = f.extension();
            if (ext != null && ext.exists(symbol)) {
                return true;
            }
        }
    }

    private boolean existsViaEnclosingSlot(int frameHops, int frameSlot, RSymbol symbol, Frame firstFrame) {

        Frame f = this;
        int hops = frameHops;
        int slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = f.enclosingFrame();
            }
            if (f.isDirty()) {
                if (first.existsFromExtensionEntry(symbol, f)) {
                    return true;
                }
            }
            // no inserted extension slot
            if (f.get(slot) != null) {
                return true;
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = f.findEnclosingVariable(symbol);
            if (eslot == null) {
                return f.existsFromExtensionsAndRootLevel(symbol);
            }
            // try the next enclosing slot
            f = f.enclosingFrame();
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    public boolean existsFromExtensionEntry(RSymbol symbol, Frame stopFrame) {
        if (this == stopFrame) {
            return false;
        }
        if (extension != null && extension.exists(symbol)) {
            return true;
        }
        if (enclosingFrame != null) {
            return enclosingFrame.existsFromExtension(symbol, stopFrame);
        }
        return false;
    }

    private boolean existsFromExtension(RSymbol symbol, Frame stopFrame) { // TODO: this == null

        for (Frame f = this; f != stopFrame; f = f.enclosingFrame()) {
            FrameExtension ext = f.extension();
            if (ext != null && ext.exists(symbol)) {
                return true;
            }
        }
        return false;
    }

    public static void writeToTopLevelCondRef(RSymbol sym, RAny value) {
        Object oldValue = sym.getValueNoForce();
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

    public void writeToExtension(RSymbol sym, RAny value) {
        if (extension == null) {
            installExtension();
            extension.putRef(this, sym, value);
        } else {
            int pos = extension.getPosition(sym);
            if (pos >= 0) {
                extension.writeAtRef(pos, value);
            } else {
                extension.putRef(this, sym, value);
            }
        }
    }

    public void writeToExtensionNoRef(RSymbol sym, Object value) {
        if (extension == null) {
            installExtension();
            extension.putNoRef(this, sym, value); // The extension is brand new, we can use the first slot safely
        } else {
            int pos = extension.getPosition(sym);
            if (pos >= 0) {
                extension.writeAtNoRef(pos, value);
            } else {
                extension.putNoRef(this, sym, value);
            }
        }
    }

    private void writeView(int slot, View view) {
        RAny v = view.materialize();
        set(slot, v);
        v.ref(); // always must ref
    }

    public void writeAtCondRef(int slot, RAny value) {
        Object oldContent = get(slot);
        if (value != oldContent) {
            if (MATERIALIZE_ON_ASSIGNMENT && value instanceof View) {
                writeView(slot, (View) value);
            } else {
                set(slot, value);
                value.ref();
            }
        }
    }

    public void writeAtNoRef(int slot, Object value) {
        if (MATERIALIZE_ON_ASSIGNMENT && value instanceof View) {
            writeView(slot, (View) value);
        } else {
            set(slot, value);
        }
    }

    public void writeAtRef(int slot, Object value) {
        if (MATERIALIZE_ON_ASSIGNMENT && value instanceof View) {
            writeView(slot, (View) value);
        } else {
            set(slot, value);
            ((RAny) value).ref();
        }
    }

    public void writeAtRef(int slot, RAny value) {
        if (MATERIALIZE_ON_ASSIGNMENT && value instanceof View) {
            writeView(slot, (View) value);
        } else {
            set(slot, value);
            value.ref();
        }
    }

    // starts from enclosing
    public boolean superWriteViaWriteSet(int slot, RSymbol symbol, RAny value) {
        Object oldVal = get(slot);
        if (oldVal != null) {
            if (oldVal != value) {
                writeAtRef(slot, value);
            }
            return true;
        } else {
            return superWriteViaWriteSetSlowPath(symbol, value);
        }
    }

    public boolean superWriteViaWriteSetSlowPath(RSymbol symbol, RAny value) {

        EnclosingSlot eslot = findEnclosingVariable(symbol);

        if (eslot != null) {
            return enclosingFrame.superWriteViaEnclosingSlot(eslot.hops - 1, eslot.slot, symbol, value, this);
        }

        if (enclosingFrame != null) {
            enclosingFrame.superWriteToExtensionsAndTopLevel(symbol, value);
        }

        return superWriteToTopLevel(symbol, value);
    }

    public boolean superWriteToExtensionsAndTopLevel(RSymbol symbol, RAny value) {

        if (superWriteToExtensionEntry(symbol, value)) {
            return true;
        }
        return superWriteToTopLevel(symbol, value);
    }

    public static boolean superWriteToTopLevel(RSymbol symbol, RAny value) {
        writeToTopLevelCondRef(symbol, value);
        return true;
    }

    public boolean superWriteToExtensionEntry(RSymbol symbol, RAny value) { // TODO: frame == null

        if (extension != null) {
            int epos = extension.getPosition(symbol);
            if (epos != -1) {
                extension.writeAtRef(epos, value);
                return true;
            }
        }
        if (enclosingFrame != null) {
            return superWriteToExtension(symbol, value, null);
        }
        return false;
    }

    public boolean superWriteToExtensionEntry(RSymbol symbol, RAny value, Frame stopFrame) {

        if (this == stopFrame) {
            return false;
        }
        if (extension != null) {
            int epos = extension.getPosition(symbol);
            if (epos != -1) {
                extension.writeAtRef(epos, value);
                return true;
            }
        }
        if (enclosingFrame != null) {
            return enclosingFrame.superWriteToExtension(symbol, value, stopFrame);
        }
        return false;
    }

    private boolean superWriteToExtension(RSymbol symbol, RAny value, Frame stopFrame) { // TODO: frame == null

        for (Frame f = this; f != stopFrame; f = f.enclosingFrame()) {
            FrameExtension ext = f.extension();
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

    public boolean superWriteViaEnclosingSlotAndTopLevel(int hops, int slot, RSymbol symbol, RAny value) {

        return enclosingFrame.superWriteViaEnclosingSlot(hops - 1, slot, symbol, value, this);
    }

    private boolean superWriteViaEnclosingSlot(int frameHops, int frameSlot, RSymbol symbol, RAny value, Frame firstFrame) {

        Frame f = this;
        int hops = frameHops;
        int slot = frameSlot;
        Frame first = firstFrame;

        for (;;) {
            for (int i = 0; i < hops; i++) {
                f = f.enclosingFrame();
            }
            Object val;
            if (f.isDirty()) {
                if (first.superWriteToExtensionEntry(symbol, value, f)) {
                    return true;
                }
            }
            // no inserted extension slot
            val = f.get(slot);
            if (val != null) {
                f.writeAtRef(slot, value);
                return true;
            }
            // variable not present in the enclosing slot
            EnclosingSlot eslot = f.findEnclosingVariable(symbol);
            if (eslot == null) {
                return f.superWriteToExtensionsAndTopLevel(symbol, value);
            }
            // try the next enclosing slot
            f = f.enclosingFrame();
            first = f;
            hops = eslot.hops - 1;
            slot = eslot.slot;
        }
    }

    public void localWrite(RSymbol symbol, RAny value) {

        int slot = findVariable(symbol);
        if (slot != -1) {
            writeAtRef(slot, value);
        } else {
            writeToExtension(symbol, value); // marks the defining slot dirty
        }
    }

    public void localWriteNoRef(RSymbol symbol, Object value) {

        int slot = findVariable(symbol);
        if (slot != -1) {
            writeAtNoRef(slot, value);
        } else {
            writeToExtensionNoRef(symbol, value); // marks the defining slot dirty
        }
    }

    // this is like "superWrite" - but starts with current frame
    // used for assign with inherits == TRUE
    public void reflectiveInheritsWrite(RSymbol symbol, RAny value) {

        int slot = findVariable(symbol);
        if (slot != -1) {
            superWriteViaWriteSet(slot, symbol, value);
            return;
        }
        EnclosingSlot eslot = findEnclosingVariable(symbol);
        if (eslot != null) {
            superWriteViaEnclosingSlotAndTopLevel(eslot.hops, eslot.slot, symbol, value);
            return;
        }
        superWriteToExtensionsAndTopLevel(symbol, value);
    }

    public void customLocalWrite(RSymbol symbol, RAny value) {
        int pos = extension.getPosition(symbol);
        if (pos >= 0) {
            extension.writeAtRef(pos, value);
        } else {
            extension.putRef(this, symbol, value);
        }
    }

    public void customLocalWriteNoRef(RSymbol symbol, Object value) {
        int pos = extension.getPosition(symbol);
        if (pos >= 0) {
            extension.writeAtNoRef(pos, value);
        } else {
            extension.putNoRef(this, symbol, value);
        }
    }

    public void customReflectiveInheritsWrite(RSymbol symbol, RAny value) { // used for assign with inherits == TRUE
        int epos = extension.getPosition(symbol);
        if (epos != -1) {
            extension.writeAtRef(epos, value);
            return;
        }
        if (enclosingFrame != null) {
            enclosingFrame.reflectiveInheritsWrite(symbol, value);
        } else {
            superWriteToTopLevel(symbol, value);
        }
    }

    public static void markDirty(Frame frame, RSymbol symbol) {
        Frame current = frame;
        while (current != null) {
            if (current.hasLocalVariable(symbol)) {
                current.markDirty();
                return;
            }
            current = current.enclosingFrame();
        }
        symbol.markDirty();
    }



    public RSymbol[] validWriteSet() { // TODO: this is probably very slow
        // TODO: rewrite this

        RSymbol[] ws = function.localWriteSet();
        if (ws.length == 0) {
            return ws;
        }

        FrameDescriptor frameDescriptor = descriptor();
        int nnull = 0;
        int nslots = frameDescriptor.numberOfSlots();
        for (int i = 0; i < nslots; i++) {
            if (get(i) == null) {
                nnull++;
            }
        }
        if (nnull == 0) {
            return ws;
        }

        int size = ws.length - nnull;
        RSymbol[] res = new RSymbol[size];
        int i = 0;
        RSymbol[] slotNames = frameDescriptor.names();
        for (int j = 0; j < nslots; j++) {
            if (get(j) != null) {
                res[i++] = slotNames[j];
            }
        }
        return res;
    }

    public RSymbol[] listSymbols() { // FIXME: this is probably very slow, but perhaps not on fastpath?
        RSymbol[] ws = validWriteSet();
        if (extension != null) {
            RSymbol[] es = extension.validNames();
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


    public Object getObjectForcingPromises(int slot) {
        return RPromise.force(get(slot));
    }

}
