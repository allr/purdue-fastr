package r.data.internal;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class BaseObject {

    RAttributes attributes;

    public RAttributes getAttributes() {
        return attributes;
    }

    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        return factory.fromGeneric(); // This cast is stupid and will be fixed soon
    }
}
