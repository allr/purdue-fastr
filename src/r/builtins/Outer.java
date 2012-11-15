package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class Outer {
    private static final String[] paramNames = new String[]{"X", "Y", "FUN"};

    private static final int IX = 0;
    private static final int IY = 1;
    private static final int IFUN = 2;

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            Utils.nyi();
            return null;
        }
    };
}
