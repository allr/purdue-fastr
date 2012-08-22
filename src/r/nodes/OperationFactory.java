package r.nodes;

import com.oracle.truffle.*;

import r.data.*;

public abstract class OperationFactory<T extends Node> {

    public abstract T fromGeneric(RAny obj);

    public T fromInt(RInt obj) {
        return fromGeneric(obj);
    }

    public T fromLogical(RInt obj) {
        return fromGeneric(obj);
    }

    public T fromDouble(RInt obj) {
        return fromGeneric(obj);
    }

    public T fromNull(RNull obj) {
        return fromGeneric(obj);
    }
}
