package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

public class Debugging {

    public static final CallFactory INSPECT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(Frame frame, RAny arg) {
                    System.out.println("INSPECT: " + arg + " type=" + arg.typeOf() + " isShared=" + arg.isShared() + " isTemporary=" + arg.isTemporary());
                    return RNull.getNull();
                }
            };
        }
    };
}
