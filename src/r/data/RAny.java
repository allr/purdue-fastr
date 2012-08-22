package r.data;

import r.nodes.*;

import com.oracle.truffle.*;


public interface RAny {
    RAttributes getAttributes();

    String pretty();

    RLogical asLogical();
    RInt asInt();

    <T extends Node> T callNodeFactoty(OperationFactory factory);
}
