package r.data.internal;

import r.data.*;

public interface ProfilingView {

    public static class ViewProfile {
            // static info
        int size;
        int depth;

            // profiled info
        int externalGetCount;
        int externalMaterializeCount;
        int externalSumCount;

        int internalGetCount;
        int internalMaterializeCount;
        int internalSumCount;

        public void onNewView(RArray child) {
            depth = 0; // TODO
            size = child.size();
        }

        private static boolean internal = false;

        public boolean enter() {
            boolean isInternal = internal;
            if (!isInternal) {
                internal = true;
            }
            return isInternal;
        }

        public void leave(boolean wasInternal) {
            internal = wasInternal;
        }

        public boolean enterGet() {
            boolean isInternal = enter();
            if (isInternal) {
                internalGetCount++;
            } else {
                externalGetCount++;
            }
            return isInternal;
        }

        public boolean enterMaterialize() {
            boolean isInternal = enter();
            if (isInternal) {
                internalMaterializeCount++;
            } else {
                externalMaterializeCount++;
            }
            return isInternal;
        }

        public boolean enterSum() {
            boolean isInternal = enter();
            if (isInternal) {
                internalSumCount++;
            } else {
                externalSumCount++;
            }
            return isInternal;
        }

        public static <T extends RArray> T profile(RArray orig, ViewProfile profile) {
            RArray res;
            if (orig instanceof RDouble) {
                res = new RDoubleProfilingView((RDouble) orig, profile);
            } else {
                res = orig;
            }
            return (T) res;
        }

        public boolean shouldBeLazy() {
            System.err.println("should be lazy?: size=" + size + " externalMaterializeCount=" + externalMaterializeCount + " externalGetCount=" + externalGetCount);

            boolean unused = internalGetCount == 0 && externalGetCount == 0 && internalMaterializeCount == 0 && externalMaterializeCount == 0 &&
                    internalSumCount == 0 && externalSumCount == 0;

            if (unused) {
                return true; // although indeed it may just be used later..
            }
            if (size < 20) {
                return false;
            }
            if (externalMaterializeCount > 0) {
                return false;
            }
            if (externalGetCount > size) {
                return false;
            }
            return true;
        }

    }

    public static class RDoubleProfilingView extends View.RDoubleProxy<RDouble> implements RDouble, ProfilingView {

        private ViewProfile profile;

        public RDoubleProfilingView(RDouble orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        @Override
        public double getDouble(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getDouble(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RDouble materialize() {
            boolean internal = profile.enterMaterialize();
            try {
                return orig.materialize();
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeInto(double[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                ((RDoubleView) orig).materializeInto(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeIntoOnTheFly(double[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                ((RDoubleView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public double sum(boolean narm) {
            boolean internal = profile.enterSum();
            try {
                return orig.sum(narm);
            } finally {
                profile.leave(internal);
            }
        }

    }

}
