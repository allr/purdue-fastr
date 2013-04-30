package r.builtins;

import com.oracle.truffle.api.frame.Frame;
import r.data.*;
import r.nodes.ASTNode;
import r.nodes.truffle.RNode;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 4/30/13 Time: 7:20 PM To change this template use File | Settings | File
 * Templates.
 */
public class _timerEnd extends CallFactory {
    static final CallFactory _ = new _timerEnd("_timerEnd", new String[]{"x","name"}, new String[]{"x", "name"});

    _timerEnd(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    @Override
    public RNode create(ASTNode call, final RSymbol[] names, RNode[] exprs) {
        return new Builtin.Builtin2(call, names, exprs) {
            @Override
            public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                int time = ((RInt) arg0).getInt(0);
                time = ((int)(System.currentTimeMillis() -_timerStart._start) - time);
                System.out.println("__TIMER__ "+time+" "+((RString)arg1).getString(0));
                return RNull.getNull();
            }
        };
    }
}
