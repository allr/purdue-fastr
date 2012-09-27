package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.data.*;
import r.nodes.*;

public abstract class AbstractCall extends BaseR {

    @Stable protected final RSymbol[] argNames;
    @Stable protected final RNode[] argExprs;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = updateParent(argsExprs);
    }
}
