package r.builtins;


import com.oracle.truffle.api.frame.Frame;
import r.data.*;
import r.data.internal.*;
import r.nodes.ASTNode;
import r.nodes.truffle.RNode;

/** Hook to provide time for measurements.
 *
 */
public class _bkpt extends CallFactory {
    static final CallFactory _ = new _bkpt("_bkpt");

    final static  long _start = System.currentTimeMillis();

    public _bkpt(String name) {
        super(name);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new Builtin.Builtin0(call, names, exprs) {
            @Override
            public RAny doBuiltIn(Frame frame) {
                return RNull.getNull();

            }
        };
    }
}