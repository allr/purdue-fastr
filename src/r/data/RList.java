package r.data;

import r.*;
import r.data.internal.*;

public interface RList extends RArray {

    String TYPE_STRING = "list";

    RNull NULL = RNull.getNull();

    ListImpl EMPTY = (ListImpl) RArrayUtils.markShared(RList.RListFactory.getUninitializedArray(0));
    ListImpl EMPTY_NAMED_NA = (ListImpl) RArrayUtils.markShared(RListFactory.getFor(new RAny[] {}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));
    ListImpl BOXED_NULL = (ListImpl) RArrayUtils.markShared(RList.RListFactory.getArray(NULL));
    ListImpl NULL_NAMED_NA = (ListImpl) RArrayUtils.markShared(RListFactory.getFor(new RAny[] {NULL}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));

    RAny getRAny(int i);
    RAny getRAnyRef(int i);
    RArray set(int i, RAny val);

    public class RListFactory {
        public static ListImpl getScalar(RAny value) {
            return new ListImpl(new RAny[]{value}, null, null, false);
        }
        public static ListImpl getArray(RAny... values) {
            return new ListImpl(values);
        }
        public static ListImpl getUninitializedArray(int size) {
            return new ListImpl(size);
        }
        public static ListImpl getUninitializedArray(int size, int[] dimensions) {
            return new ListImpl(new RAny[size], dimensions);
        }
        public static ListImpl getNullArray(int size) {
            ListImpl v = getUninitializedArray(size);
            for (int i = 0; i < size; i++) {
                v.set(i, NULL);
            }
            return v;
        }
        public static ListImpl copy(RList l) {
            return new ListImpl(l, false);
        }
        public static ListImpl strip(RList l) {
            return new ListImpl(l, true);
        }
        public static ListImpl getFor(RAny[] values) {
            return getFor(values, null, null);
        }
        public static ListImpl getFor(RAny[] values, int[] dimensions) { // FIXME: remove this after fixing list element deletion
            return getFor(values, dimensions, null);
        }
        public static ListImpl getFor(RAny[] values, int[] dimensions, Names names) {  // re-uses values!
            return new ListImpl(values, dimensions, names, false);
        }
        public static ListImpl getEmpty(boolean named) {
            return named ? EMPTY_NAMED_NA : EMPTY;
        }
        public static ListImpl getNull(boolean named) {
            return named ? NULL_NAMED_NA : BOXED_NULL;
        }
        public static RList exclude(int excludeIndex, RList orig) {
            return new RListExclusion(excludeIndex, orig);
        }
        public static RList subset(RList value, RInt index) {
            return new RListSubset(value, index);
        }
    }

    public static class RListExclusion extends View.RListView implements RList {

        final RList orig;
        final int excludeIndex;
        final int size;

        public RListExclusion(int excludeIndex, RList orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public RAny getRAny(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getRAny(i);
            } else {
                return orig.getRAny(i + 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RListSubset extends View.RListView implements RList {

        final RList value;
        final int vsize;
        final RInt index;
        final int isize;

        public RListSubset(RList value, RInt index) {
            this.value = value;
            this.index = index;
            this.isize = index.size();
            this.vsize = value.size();
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public RAny getRAny(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RList.NULL;
            } else {
                return value.getRAny(j - 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return value.isShared() || index.isShared();
        }

        @Override
        public void ref() {
            value.ref();
            index.ref();
        }
    }
}
