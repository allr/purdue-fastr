package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * "close"
 * 
 * <pre>
 * con -- a connection.
 * ... -- arguments passed to or from other methods.
 * </pre>
 */
final class Close extends CallFactory {

    static final CallFactory _ = new Close("close", new String[]{"con", "..."}, new String[]{"con"});

    private Close(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (!(arg instanceof RInt)) { throw Utils.nyi("unsupported argument"); }
                RInt iarg = (RInt) arg;
                if (iarg.size() != 1) { throw Utils.nyi("unsupported argument"); }
                int cindex = iarg.getInt(0);
                Connection con = RContext.getConnection(cindex);
                if (con == null) { throw RError.getInvalidConnection(ast); }
                if (con.isOpen()) {
                    con.close(ast);
                }
                RContext.freeConnection(cindex);
                return RNull.getNull();
            }
        };
    }
}
