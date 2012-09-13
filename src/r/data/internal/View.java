package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public abstract class View implements RArray {

    @Override
    public RArray set(int i, Object val) {
        return materialize().set(i, val);
    }

    @Override
    public RArray subset(RAny keys) {
        return materialize().subset(keys);
    }

    @Override
    public RArray subset(RInt index) {
        return materialize().subset(index);
    }

    @Override
    public RArray subset(RString names) {
        return materialize().subset(names);
    }

    @Override
    public String pretty() {
        return materialize().pretty(); // FIXME what a stupid impl ...
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        Utils.nyi(); // Do we have to bind on the view node or on the implementation
        return null;
    }
}
