package r.nodes.truffle;

import r.data.*;
import r.nodes.*;

public abstract class AbstractCall extends BaseR {

    protected final RSymbol[] argsNames;
    protected final RNode[] argsValues;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
        super(orig);
        this.argsNames = argNames;
        this.argsValues = updateParent(argExprs);
    }

}
