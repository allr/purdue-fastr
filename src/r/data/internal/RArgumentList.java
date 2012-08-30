package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.truffle.*;

public class RArgumentList {

    final RSymbol[] names;
    final RNode[] expressions;

    public RArgumentList(RSymbol[] names, RNode[] expressions) {
        this.names = names;
        this.expressions = expressions;
        Utils.check(names.length == expressions.length);
    }

    public RSymbol name(int pos) {
        return names[pos];
    }

    public RNode expression(int pos) {
        return expressions[pos];
    }

    public int length() {
        return names.length;
    }
}
