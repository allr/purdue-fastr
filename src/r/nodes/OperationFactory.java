package r.nodes;

import r.data.*;
import r.nodes.truffle.*;

public abstract class OperationFactory<T extends RNode> {

    public abstract T fromGeneric(RAny obj);

    public T fromInt(RInt obj) {
        return fromGeneric(obj);
    }

    public T fromLogical(RLogical obj) {
        return fromGeneric(obj);
    }

    public T fromDouble(RDouble obj) {
        return fromGeneric(obj);
    }

    public T fromNull(RNull obj) {
        return fromGeneric(obj);
    }
}
