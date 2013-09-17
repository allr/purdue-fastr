package r.data.internal;

import r.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

public abstract class BaseObject {
    // FIXME: more methods could go to this class, removing copy-paste

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
