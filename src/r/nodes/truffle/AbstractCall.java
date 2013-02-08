package r.nodes.truffle;

import r.data.*;
import r.nodes.*;

public abstract class AbstractCall extends BaseR {

    protected final RSymbol[] argNames;
    @Children protected final RNode[] argExprs;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = adoptChildren(argsExprs);
    }
}
