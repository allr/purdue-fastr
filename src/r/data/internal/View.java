package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: the Impl classes for data types should also inherit from View.*, reducing code duplication

public abstract class View extends ArrayImpl implements RArray {

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
        return materialize().pretty();
    }

    @Override
    public RArray setDimensions(int[] dimensions) {
        return materialize().setDimensions(dimensions);
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        Utils.nyi(); // Do we have to bind on the view node or on the implementation
        return null;
    }

    @Override
    public boolean isShared() {
        return true;  // optimization / hack -- unless the views change the default behavior of materialize-on-set, calculating "shared"
                      //   would take unnecessary time, we are copying anyway
    }

    public boolean isSharedReal() {
        return true;
    }

    public abstract static class RIntView extends View implements RInt {
        @Override
        public Object get(int i) {
            return getInt(i);
         }

        @Override
        public RInt materialize() {
            return RIntFactory.copy(this);
        }

        @Override
        public RInt stripAttributes() {
            return RIntFactory.copyValuesOnly(this);
        }

        @Override
        public RLogical asLogical() {
            return new RInt.RLogicalView(this);
        }

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public RDouble asDouble() {
            return new RInt.RDoubleView(this);
        }

        @Override
        public RString asString() {
            Utils.nyi();
            return null;
        }

        @Override
        public RAny boxedGet(int i) {
            return RIntFactory.getScalar(getInt(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getInt(i) == RInt.NA;
        }

        @Override
        public RInt set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }

        @Override
        public RArray subset(RInt index) {
            return RInt.RIntFactory.subset(this, index);
        }
    }

    public abstract static class RDoubleView extends View implements RDouble {
        @Override
        public Object get(int i) {
            return getDouble(i);
        }

        @Override
        public RAny boxedGet(int i) {
            return RDoubleFactory.getScalar(getDouble(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return RDouble.RDoubleUtils.isNAorNaN(getDouble(i));
        }

        @Override
        public RInt asInt() {
            return new RDouble.RIntView(this);
        }

        @Override
        public RDouble asDouble() {
            return this;
        }

        @Override
        public RDouble materialize() {
            return RDouble.RDoubleFactory.copy(this);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }

        @Override
        public RLogical asLogical() {
            return new RDouble.RLogicalView(this);
        }

        @Override
        public RString asString() {
            Utils.nyi();
            return null;
        }

        @Override
        public RDouble set(int i, double val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RDouble.RDoubleFactory.subset(this, index);
        }
    }

    public abstract static class RLogicalView extends View implements RLogical {
        @Override
        public Object get(int i) {
            return getLogical(i);
         }

        @Override
        public RLogical materialize() {
            return RLogicalFactory.copy(this);
        }

        @Override
        public RLogical asLogical() {
            return this;
        }

        @Override
        public RInt asInt() {
            return new RLogical.RIntView(this);
        }

        @Override
        public RDouble asDouble() {
            return new RLogical.RDoubleView(this);
        }

        @Override
        public RString asString() {
            Utils.nyi();
            return null;
        }

        @Override
        public RAny boxedGet(int i) {
            return RLogicalFactory.getScalar(getLogical(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getLogical(i) == RLogical.NA;
        }

        @Override
        public RLogical set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }

        @Override
        public RArray subset(RInt index) {
            return RLogical.RLogicalFactory.subset(this, index);
        }
    }

    public abstract static class RStringView extends View implements RString {
        @Override
        public Object get(int i) {
            return getString(i);
         }

        @Override
        public RString materialize() {
            return RString.RStringFactory.copy(this);
        }

        @Override
        public RString asString() {
            return this;
        }

        @Override
        public RInt asInt() {
            Utils.nyi();
            return null;
        }

        @Override
        public RDouble asDouble() {
            Utils.nyi();
            return null;
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RAny boxedGet(int i) {
            return RStringFactory.getScalar(getString(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getString(i) == RString.NA;
        }

        @Override
        public RArray set(int i, String val) {
            return materialize().set(i, val);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }

        @Override
        public RArray subset(RInt index) {
            return RString.RStringFactory.subset(this, index);
        }
    }

    public abstract static class RListView extends View implements RList {
        @Override
        public Object get(int i) {
            return getRAny(i);
         }

        @Override
        public RList materialize() {
            return RList.RListFactory.copy(this);
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RInt asInt() {
            Utils.nyi();
            return null;
        }

        @Override
        public RDouble asDouble() {
            Utils.nyi();
            return null;
        }

        @Override
        public RString asString() {
            Utils.nyi();
            return null;
        }

        @Override
        public RList asList() {
            return this;
        }

        @Override
        public RAny boxedGet(int i) {
            return RList.RListFactory.getScalar(getRAny(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            RAny v = getRAny(i);
            if (v instanceof RArray) {
                RArray a = (RArray) v;
                if (a.size() == 1) {
                    return a.isNAorNaN(0);
                }
            }
            return false;
        }

        @Override
        public RArray set(int i, RAny val) {
            return materialize().set(i, val);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }

        @Override
        public RArray subset(RInt index) {
            return RList.RListFactory.subset(this, index);
        }

        @Override
        public RAny getRAnyRef(int i) {
            RAny v = getRAny(i);
            v.ref();
            return v;
        }
    }
}
