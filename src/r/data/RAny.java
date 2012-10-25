package r.data;

import r.nodes.*;
import r.nodes.truffle.*;

public interface RAny {

    RAttributes getAttributes();

    String pretty();

    RLogical asLogical();

    RInt asInt();

    RDouble asDouble();

    RString asString();

    RList asList();

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);
}
