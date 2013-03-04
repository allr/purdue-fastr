package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// call will be typically a function call, but can also be an operator
public abstract class CallFactory {

    public RSymbol name() {
        return null;
    }

    public abstract RNode create(ASTNode call, RSymbol[] names, RNode[] exprs);

    public RNode create(ASTNode call, RNode left, RNode right) {
        return create(call, null, new RNode[] { left, right });
    }

    /**
     * Verify that the function argument at index was passed into the call, else
     * throw an exception.
     */
    public static void checkArgumentIsPresent(ASTNode call, boolean[] provided, String[] names, int index) {
        if (provided[index]) { return; }
        BuiltIn.missingArg(call, names[index]);
    }

}
