package r.data.internal;

public abstract class NonScalarArrayImpl extends ArrayImpl {

    private int refcount;

    @Override
    public final boolean isShared() {
        return refcount > 1;  // ==2
    }

    @Override
    public final void ref() {
        if (refcount == 0) {
            refcount = 1;
        } else if (refcount == 1) {
            refcount = 2;
        }
    }
}
