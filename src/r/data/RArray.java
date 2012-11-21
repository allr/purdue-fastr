package r.data;

import r.data.internal.*;


public interface RArray extends RAny {
    int[] SCALAR_DIMENSIONS = new int[] {1, 1};

    int size();
    int[] dimensions(); // the returned array shall not be modified

    Object get(int i);
    Object getRef(int i);
    RAny boxedGet(int i);
    RArray set(int i, Object val);
    RArray setDimensions(int[] dimensions);
    boolean isNAorNaN(int i);
    int index(int i, int j);

    RArray subset(RAny keys);

    RArray subset(RInt index);
    RArray subset(RString names);

    RArray materialize();

    public static class RListView extends View.RListView implements RList {
        final RArray arr;
        public RListView(RArray arr) {
            this.arr = arr;
        }

        @Override
        public int size() {
            return arr.size();
        }

        @Override
        public RAny getRAny(int i) {
            return arr.boxedGet(i);
        }

        @Override
        public boolean isSharedReal() {
            return arr.isShared();
        }

        @Override
        public void ref() {
            arr.ref();
        }
    }
}
