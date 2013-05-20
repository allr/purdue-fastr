package r.data;

import r.*;

// this class represents the content of "..."
public class RDots {
    final RSymbol[] names;
    final Object[] values;  // can be actual values (RAny) or promises (RPromise)

    public RDots(RSymbol[] names, Object[] values) {
        this.names = names;
        this.values = values;
        assert Utils.check(names.length == values.length);
    }

    public RSymbol[] names() {
        return names;
    }

    public Object[] values() {
        return values;
    }

}
