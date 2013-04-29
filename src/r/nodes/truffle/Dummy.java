package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;

import r.data.*;
import r.nodes.*;

public class Dummy extends RNode {

    public Dummy() {
    }

    @Override
    public Object execute(Frame frame) {
        assert (false);
        return null;
    }
}
