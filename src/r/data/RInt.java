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
        public static IntImpl getScalar(int value) {
            return new IntImpl(new int[]{value}, false);
        }
        public static IntImpl getArray(int... values) {
            return new IntImpl(values);
        }
        public static IntImpl getUninitializedArray(int size) {
            return new IntImpl(size);
        }
        public static IntImpl getNAArray(int size) {
            IntImpl v = getUninitializedArray(size);
            for (int i = 0; i < size; i++) {
                v.set(i, NA);
            }
            return v;
        }
        public static IntImpl copy(RInt i) {
            return new IntImpl(i);
        }
        public static RInt getForArray(int[] values) {  // re-uses values!
            return new IntImpl(values, false);
        }
        public static RInt forSequence(int from, int to, int step) {
            return new IntImpl.RIntSequence(from, to, step);
        }
        public static RInt exclude(int excludeIndex, RInt orig) {
            return new RIntExclusion(excludeIndex, orig);
        }
    }

    public static class RDoubleView extends View.RDoubleView implements RDouble {

        final RInt rint;
        public RDoubleView(RInt rint) {
            this.rint = rint;
        }

        public int size() {
            return rint.size();
        }

        @Override
        public RInt asInt() {
            return rint;
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
        public double getDouble(int i) {
            int v = rint.getInt(i);
            return Convert.int2double(v);
        }
    }

    public static class RIntExclusion extends View.RIntView implements RInt {

        final RInt orig;
        final int excludeIndex;
        final int size;

        public RIntExclusion(int excludeIndex, RInt orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getInt(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getInt(i);
            } else {
                return orig.getInt(i + 1);
            }
        }
    }
}
