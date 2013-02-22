package r.data.internal;

import r.*;
import r.nodes.*;
import r.nodes.truffle.*;

public abstract class BaseObject {

    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        return factory.fromGeneric(); // This cast is stupid and will be fixed soon
    }

    public String pretty() {
        Utils.nyi();
        return null;
    }

    public String prettyMatrixElement() {
        return pretty();
    }
}
