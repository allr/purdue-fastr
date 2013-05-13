package r.data;

import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

public final class RPromise {
    final RNode expression; // must be a root node
    final MaterializedFrame frame;
    RAny value;

    public RPromise(RNode expression, Frame frame) {
        this.expression = expression; // root node
        this.frame = frame == null ? null : frame.materialize();
    }

    public Object forceOrGet() {
        if (value == null) {
            value = (RAny) expression.execute(frame);
            value.ref();
        }
        return value;
    }

    public static Object force(Object o) {
        if (FunctionCall.PROMISES && o instanceof RPromise) {
            return ((RPromise) o).forceOrGet();
        } else {
            return o;
        }
    }
}
