package r.runtime;

import r.data.*;

public class FrameDescriptor {

    RSymbol[] names;

    public FrameDescriptor(RSymbol[] names) {
        this.names = names;
    }

    public int findFrameSlot(RSymbol name) {
        for(int i = 0; i < names.length; i++) {
            if (names[i] == name) {
                return i;
            }
        }
        return -1;
    }

    public int numberOfSlots() {
        return names.length;
    }

    public RSymbol[] names() {
        return names;
    }
}
