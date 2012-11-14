package r.data;

import r.nodes.*;
import r.nodes.truffle.*;

public interface RAny {

    RAttributes getAttributes();

    String pretty();
    String prettyMatrixElement();

    RLogical asLogical();

    RInt asInt();

    RDouble asDouble();

    RString asString();

    RList asList();

    void ref();
    boolean isShared(); // FIXME: at some point will probably neet do distinguish "1" and "2"

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);
}
