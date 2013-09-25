package r.runtime;

import java.util.*;

import r.*;
import r.data.*;
import r.data.internal.*;

public class FrameExtension {

    protected int used = 0;
    private int capacity = 10;
    // NOTE: we need a third counter for the last value use for storing the lastUsed value in case of removal

    private int bloom; // This bloom filter comes from Alex B. (renjin)
    // Does it make any sense ? for this dynamic structures

    // TODO Merge these two arrays, and use unsafe casts
    private RSymbol[] names = new RSymbol[capacity];
    private Object[] values = new Object[capacity];

    protected Object getForcingPromises(RSymbol name) {
        int pos = getPosition(name);
        if (pos >= 0) {
            return RPromise.force(values[pos]);
        }
        return null;
    }

    protected Object getNotForcing(RSymbol name) {
        int pos = getPosition(name);
        if (pos >= 0) {
            return values[pos];
        }
        return null;
    }

    protected boolean exists(RSymbol name) {
        return getPosition(name) >= 0;
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

        Frame.markDirty(enclosing.enclosingFrame(), sym);
            // the put method only gets called when the current write set does not have the value,
            // so we do not have to check the current write set and can immediately go to the parent
            // FIXME: handle environments that are not connected to top-level
        bloom |= sym.id();
    }

    void writeAtRef(int pos, RAny value) { // TODO or not TODO assert that the good name is still here
        assert Utils.check(pos < used);
        if (values[pos] != value) { // FIXME: note that as we have immutable scalars, and hence so many boxes, the ref will nearly always execute
            values[pos] = value;
            value.ref();
        }
    }

    void writeAtNoRef(int pos, Object value) { // TODO or not TODO assert that the good name is still here
        assert Utils.check(pos < used);
        values[pos] = value; // NOTE: could be conditional once re-writing the same value is made common (see writeAtRef)
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

    public static final class Hashed extends FrameExtension {

        private HashMap<RSymbol, Integer> map; // FIXME: use a primitive map

        public Hashed(int size) {
            map = new HashMap<>(size);
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
