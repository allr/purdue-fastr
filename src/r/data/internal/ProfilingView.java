package r.data.internal;

import r.data.*;

public interface ProfilingView {

    public static final boolean DEBUG_PROFILING = false;

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
            if (DEBUG_PROFILING) {
                System.err.println("creating profiling view " + this + " for " + child);
            }
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
            } else if (orig instanceof RInt) {
                res = new RIntProfilingView((RInt) orig, profile);
            } else {
                res = orig;
            }
            return (T) res;
        }

        private boolean shouldBeLazyReal() {
            boolean unused = internalGetCount == 0 && externalGetCount == 0 && internalMaterializeCount == 0 && externalMaterializeCount == 0 &&
                    internalSumCount == 0 && externalSumCount == 0;

            if (size == 0) {
                return false;
            }
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

        public boolean shouldBeLazy() {
            boolean res = shouldBeLazyReal();
            if (DEBUG_PROFILING) {
                System.err.println("should be lazy?: size=" + size + " G/M/S external " + externalGetCount + "/" + externalMaterializeCount + "/" + externalSumCount +
                        "  internal " + internalGetCount + "/" + internalMaterializeCount + "/" + internalSumCount);
                System.err.println("should be lazy heuristic advice: " + res + " (profiling view " + this + ")");
                Thread.dumpStack();
            }
            return res;
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
                if (orig instanceof RDoubleView) {
                    ((RDoubleView) orig).materializeInto(res);
                } else {
                    // FIXME: this case is needed because materializeInto could have been called because
                    //   "this" is a view (profiling view), if it were known to be a doubleimpl (inserting
                    //   proxy views is visible via instanceof)
                    double[] content = ((DoubleImpl) orig).getContent();
                    System.arraycopy(content, 0, res, 0, content.length);
                }
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

    public static class RIntProfilingView extends View.RIntProxy<RInt> implements RInt, ProfilingView {

        private ViewProfile profile;

        public RIntProfilingView(RInt orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        @Override
        public int getInt(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getInt(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RInt materialize() {
            boolean internal = profile.enterMaterialize();
            try {
                return orig.materialize();
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeInto(int[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                if (orig instanceof RIntView) {
                    ((RIntView) orig).materializeInto(res);
                } else {
                    // FIXME: this case is needed because materializeInto could have been called because
                    //   "this" is a view (profiling view), if it were known to be a doubleimpl (inserting
                    //   proxy views is visible via instanceof)
                    double[] content = ((DoubleImpl) orig).getContent();
                    System.arraycopy(content, 0, res, 0, content.length);
                }
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeIntoOnTheFly(int[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                ((RIntView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        // sum not implemented yet in RInt
    }

}
