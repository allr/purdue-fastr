package r.nodes.ast;

import r.nodes.exec.*;

public abstract class OperationFactory<T extends RNode> {

    public abstract T fromGeneric();

    public T fromInt() {
        return fromGeneric();
    }

    public T fromLogical() {
        return fromGeneric();
    }

    public T fromDouble() {
        return fromGeneric();
    }

    public T fromNull() {
        return fromGeneric();
    }
}
