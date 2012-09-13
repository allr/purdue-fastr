package r.builtins;

import r.data.*;
import r.nodes.FunctionCall;
import r.nodes.truffle.*;


public interface CallFactory {
    RNode create(FunctionCall call, RSymbol[] names, RNode[] exprs);
}
