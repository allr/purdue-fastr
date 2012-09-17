package r.data;

import r.*;
import r.data.internal.*;

public interface RLogical extends RArray { // FIXME: should extend Number instead?

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
        public static LogicalImpl getScalar(int value) {
            return new LogicalImpl(new int[]{value}, false);
        }
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
        public static RLogical getForArray(int[] values) {  // re-uses values!
            return new LogicalImpl(values, false);
        }
        public static RLogical exclude(int excludeIndex, RLogical orig) {
            return new RLogicalExclusion(excludeIndex, orig);
        }
    }

    public static class RDoubleView extends View.RDoubleView implements RDouble {

        final RLogical l;
        public RDoubleView(RLogical l) {
            this.l = l;
        }

        @Override
        public int size() {
            return l.size();
        }

        @Override
        public RInt asInt() {
            return l.asInt();
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
        public double getDouble(int i) {
            int ll = l.getLogical(i);
            return Convert.logical2double(ll);
        }
    }

    public static class RIntView extends View.RIntView implements RInt {

        final RLogical l;
        public RIntView(RLogical l) {
            this.l = l;
        }

        @Override
        public int size() {
            return l.size();
        }

        @Override
        public RDouble asDouble() {
            return l.asDouble();
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
        public int getInt(int i) {
            return l.getLogical(i);
        }
    }

    public static class RLogicalExclusion extends View.RLogicalView implements RLogical {

        final RLogical orig;
        final int excludeIndex;
        final int size;

        public RLogicalExclusion(int excludeIndex, RLogical orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getLogical(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getLogical(i);
            } else {
                return orig.getLogical(i + 1);
            }
        }
    }
}
