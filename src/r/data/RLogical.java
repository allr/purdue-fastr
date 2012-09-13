package r.data;

import r.*;
import r.data.internal.*;

public interface RLogical extends RArray {

    String TYPE_STRING = "logical";
    int TRUE = 1;
    int FALSE = 0;
    int NA = Integer.MIN_VALUE;
    RLogical BOXED_TRUE = RLogicalFactory.getArray(TRUE);
    RLogical BOXED_FALSE = RLogicalFactory.getArray(FALSE);
    RLogical BOXED_NA = RLogicalFactory.getArray(NA);
    RLogical EMPTY = RLogicalFactory.getUninitializedArray(0);

    int getLogical(int il);
    RLogical set(int i, int val);

    public class RLogicalFactory {

        public static LogicalImpl getArray(int... values) {
            return new LogicalImpl(values);
        }

        public static LogicalImpl getUninitializedArray(int size) {
            return new LogicalImpl(size);
        }

        public static LogicalImpl getNAArray(int size) {
            LogicalImpl l = getUninitializedArray(size);
            for (int i = 0; i < size; i++) {
                l.set(i, NA);
            }
            return l;
        }

        public static LogicalImpl copy(RLogical l) {
            return new LogicalImpl(l);
        }
    }

    public static class RDoubleView extends View implements RDouble {

        final RLogical l;
        public RDoubleView(RLogical l) {
            this.l = l;
        }
        @Override
        public Object get(int i) {
            return getDouble(i);
        }

        public int size() {
            return l.size();
        }

        @Override
        public RInt asInt() {
            return l.asInt();
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
            return l.getAttributes();
        }

        @Override
        public RLogical asLogical() {
            return l;
        }

        @Override
        public RArray set(int i, double val) {
            return materialize().set(i, val);
        }

        @Override
        public double getDouble(int i) {
            int ll = l.getLogical(i);
            return Convert.logical2double(ll);
        }
    }

}
