package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

public class List {
    public static final CallFactory FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            RArray.Names listNames = null;
            if (names != null) {
                boolean hasNonNull = false;
                for (RSymbol s : names) {
                    if (s != null) {
                        hasNonNull = true;
                        break;
                    }
                }
                if (hasNonNull) {
                    RSymbol[] symbols = new RSymbol[names.length];
                    for (int i = 0; i < symbols.length; i++) {
                        if (names[i] != null) {
                            symbols[i] = names[i];
                        } else {
                            symbols[i] = RSymbol.EMPTY_SYMBOL;
                        }
                    }
                    listNames = RArray.Names.create(symbols);
                }
            }
            final RArray.Names fListNames = listNames;
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] params) {
                    Utils.ref(params);
                    return RList.RListFactory.getFor(params, null, fListNames); // shallow copy (in fact no copy)
                }
            };
        }
    };
}
