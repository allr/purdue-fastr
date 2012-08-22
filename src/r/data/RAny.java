package r.data;

import r.nodes.*;
import r.nodes.truffle.*;

public interface RAny {

    RAttributes getAttributes();

    String pretty();

    RLogical asLogical();

    RInt asInt();

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);
}
