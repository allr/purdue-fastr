package r.data;

import r.data.internal.*;


public interface RArray extends RAny {
    int size();

    Object get(int i);
    RAny boxedGet(int i);
    RArray set(int i, Object val);
    boolean isNAorNaN(int i);

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
    }
}
