package r.data;

import r.*;
import r.nodes.*;
import r.nodes.truffle.*;

public interface RAny {

    RAttributes getAttributes();
    RAny stripAttributes();

    String pretty();
    String prettyMatrixElement();

        // casts that don't produce warnings and don't introduce NAs
    RLogical asLogical();
    RInt asInt();
    RDouble asDouble();
    RString asString();
    RList asList();

        // coercion - can produce warnings (NAs introduced by coercion)
    RLogical asLogical(RContext context, ASTNode ast);
    RInt asInt(RContext context, ASTNode ast);
    RDouble asDouble(RContext context, ASTNode ast);
    RString asString(RContext context, ASTNode ast);

    void ref();
    boolean isShared(); // FIXME: at some point will probably need do distinguish between 0, 1, and 2

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);
}
