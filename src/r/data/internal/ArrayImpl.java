package r.data.internal;

import r.*;
import r.data.*;


public abstract class ArrayImpl extends BaseObject implements RArray {
    @Override
    public RArray subset(RAny keys) {
        if (keys instanceof RInt) {
            return subset((RInt) keys);
        } else if (keys instanceof RDouble) {
            return subset(((RDouble) keys).asInt());
        } else if (keys instanceof RString) {
            return subset((RString) keys);
        } else {
            Utils.nyi();
        }

        return null;
    }

    @Override
    public RArray subset(RString names) {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray subset(RInt index) {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray materialize() {
        return this;
    }

    @Override
    public RList asList() {
        return new RArray.RListView(this);
    }
}
