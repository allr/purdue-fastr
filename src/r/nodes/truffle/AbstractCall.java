package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.data.*;
import r.nodes.*;

public abstract class AbstractCall extends BaseR {

    @ContentStable protected final RSymbol[] argNames;
    @ContentStable protected final RNode[] argExprs;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = updateParent(argsExprs);
    }
}
