package r.data;

import r.*;
import r.data.internal.*;


public interface RInt extends RNumber {
    int NA = Integer.MIN_VALUE;
    String TYPE_STRING = "integer";

    RInt BOXED_NA = RIntFactory.getArray(NA);
    RInt EMPTY = RIntFactory.getUninitializedArray(0);

    int getInt(int i);
    RArray set(int i, int val);

    public class RIntFactory {
        public static RInt getArray(int... values) {
            return new IntImpl(values);
        }
        public static RInt getUninitializedArray(int size) {
            return new IntImpl(size);
        }
        public static RInt copy(RInt i) {
            return new IntImpl(i);
        }
        public static RInt getForArray(int[] values) {  // re-uses values!
            return new IntImpl(values, false);
        }
    }

    public static class RDoubleView extends View implements RDouble {

        final RInt rint;
        public RDoubleView(RInt rint) {
            this.rint = rint;
        }

        @Override
        public Object get(int i) {
            return getDouble(i);
        }

        public int size() {
            return rint.size();
        }

        @Override
        public RInt asInt() {
            return rint;
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
            return rint.getAttributes();
        }

        @Override
        public RLogical asLogical() {
            return rint.asLogical();
        }

        @Override
        public RArray set(int i, double val) {
            return materialize().set(i, val);
        }

        @Override
        public double getDouble(int i) {
            int v = rint.getInt(i);
            return Convert.int2double(v);
        }
    }

}
