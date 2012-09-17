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

    public abstract static class RIntView extends View implements RInt {
        @Override
        public Object get(int i) {
            return getInt(i);
         }

        @Override
        public RArray materialize() {
            return RIntFactory.copy(this);
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public RDouble asDouble() {
            return new RInt.RDoubleView(this);
        }


        public RAny boxedGet(int i) {
            return RIntFactory.getScalar(getInt(i));
        }

        @Override
        public RArray set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
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
        public RInt asInt() {
            return new RDouble.RIntView(this);
        }

        @Override
        public RDouble asDouble() {
            return this;
        }

        @Override
        public RArray materialize() {
            return RDouble.RDoubleFactory.copy(this);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RArray set(int i, double val) {
            return materialize().set(i, val);
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


        public RAny boxedGet(int i) {
            return RLogicalFactory.getScalar(getLogical(i));
        }

        @Override
        public RLogical set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RAttributes getAttributes() {
            return null;
        }
    }
}
