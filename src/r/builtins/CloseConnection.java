package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class CloseConnection {

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "con", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    if (arg instanceof RInt) {
                        RInt iarg = (RInt) arg;
                        if (iarg.size() == 1) {
                            int cindex = iarg.getInt(0);
                            Connection con = context.getConnection(cindex);
                            if (con != null) {
                                if (con.isOpen()) {
                                    con.close(ast);
                                }
                                context.freeConnection(cindex);
                                return RNull.getNull();
                            } else {
                                throw RError.getInvalidConnection(ast);
                            }
                        }
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }
            };
        }
    };


}
