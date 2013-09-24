package r.data.internal;

import r.data.*;

public interface SymbolChangeListener {
    public boolean onChange(RSymbol symbol); // returns true if the listener should be kept in the list
}
