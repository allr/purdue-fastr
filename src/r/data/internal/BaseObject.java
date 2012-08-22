package r.data.internal;

import com.oracle.truffle.*;

import r.data.*;
import r.nodes.*;

public class BaseObject {

    RAttributes attributes;

    public RAttributes getAttributes() {
        return attributes;
    }

    public <T extends Node> T callNodeFactoty(OperationFactory<T> factory) {
        return factory.fromGeneric((RAny) this); // This cast is stupid and will be fixed soon
    }
}
