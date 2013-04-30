package r.builtins;

import com.oracle.truffle.api.frame.Frame;
import r.data.*;
import r.data.internal.*;
import r.nodes.ASTNode;
import r.nodes.truffle.RNode;

/** Hook to provide time for measurements.
 *
 */
public class _timerStart extends CallFactory {
    static final CallFactory _ = new _timerStart("_timerStart");

    final static  long _start = System.currentTimeMillis();

    public _timerStart(String name) {
        super(name);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new Builtin.Builtin0(call, names, exprs) {
            @Override
            public RAny doBuiltIn(Frame frame) {
                System.out.println("__TIMER__ timer started");
                return new ScalarIntImpl((int)(System.currentTimeMillis()-_start));
            }
        };
    }
}
