package r.data.internal;

import r.data.*;

public interface ProfilingView {

    public static final boolean DEBUG_PROFILING = false;

    public static class ViewProfile {
            // static info
        int size;
        int depth;
        boolean created;

            // profiled info
        int externalGetCount;
        int externalMaterializeCount;
        int externalSumCount;

        int internalGetCount;
        int internalMaterializeCount;
        int internalSumCount;

        int maxRecursiveUseCount;

        public void onNewView(RArray realView) {
            depth = 0; // TODO
            size = realView.size();
            created = true;
            if (DEBUG_PROFILING) {
                System.err.println("creating profiling view " + this + " for " + realView);
            }
        }

        public void onAssignment(RArray realView, Object oldValue) {
            if (oldValue == null) {
                return;
            }
            if (!(oldValue instanceof RArray)) {
                return; // e.g. it can be a promise or a closure, etc
            }
            int count = FindValueInView.countOccurrences(realView, oldValue);
            if (count > maxRecursiveUseCount) {
                maxRecursiveUseCount = count;
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
            } else if (orig instanceof RComplex) {
                res = new RComplexProfilingView((RComplex) orig, profile);
            } else if (orig instanceof RLogical) {
                res = new RLogicalProfilingView((RLogical) orig, profile);
            } else if (orig instanceof RString) {
                res = new RStringProfilingView((RString) orig, profile);
            } else if (orig instanceof RRaw) {
                res = new RRawProfilingView((RRaw) orig, profile);
            } else if (orig instanceof RList) {
                res = new RListProfilingView((RList) orig, profile);
            } else {
                res = orig;
            }
            return (T) res;
        }

        private boolean shouldBeLazyReal() {
            if (!created) {
                if (DEBUG_PROFILING) {
                    System.err.println("MISSED VIEW in PROFILING (profilingView " + this + ")");
                }
                return false;
            }
            if (maxRecursiveUseCount > 0) {
                return false;
            }
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
                        "  internal " + internalGetCount + "/" + internalMaterializeCount + "/" + internalSumCount +
                        " maxRecursiveUseCount " + maxRecursiveUseCount);
                System.err.println("should be lazy heuristic advice: " + res + " (profiling view " + this + ")");
            }
            return res;
        }

    }

    public static class RListProfilingView extends View.RListProxy<RList> implements RList, ProfilingView {

        private ViewProfile profile;

        public RListProfilingView(RList orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        @Override
        public RAny getRAny(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getRAny(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RList materialize() {
            boolean internal = profile.enterMaterialize();
            try {
                return orig.materialize();
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeInto(RAny[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                if (orig instanceof View.RListView) {
                    ((View.RListView) orig).materializeInto(res);
                } else {
                    // FIXME: this case is needed because materializeInto could have been called because
                    //   "this" is a view (profiling view), if it were known to be a doubleimpl (inserting
                    //   proxy views is visible via instanceof)
                    RAny[] content = ((ListImpl) orig).getContent();
                    System.arraycopy(content, 0, res, 0, content.length);
                }
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeIntoOnTheFly(RAny[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                ((View.RListView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
        }

        // sum not implemented yet in RRaw
    }

    public static class RStringProfilingView extends View.RStringProxy<RString> implements RString, ProfilingView {

        private ViewProfile profile;

        public RStringProfilingView(RString orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        @Override
        public String getString(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getString(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RString materialize() {
            boolean internal = profile.enterMaterialize();
            try {
                return orig.materialize();
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeInto(String[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                if (orig instanceof RStringView) {
                    ((RStringView) orig).materializeInto(res);
                } else {
                    // FIXME: this case is needed because materializeInto could have been called because
                    //   "this" is a view (profiling view), if it were known to be a doubleimpl (inserting
                    //   proxy views is visible via instanceof)
                    String[] content = ((StringImpl) orig).getContent();
                    System.arraycopy(content, 0, res, 0, content.length);
                }
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeIntoOnTheFly(String[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                ((RStringView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
        }

        // sum not implemented yet in RLogical
    }

    public static class RComplexProfilingView extends View.RComplexProxy<RComplex> implements RComplex, ProfilingView {

        private ViewProfile profile;

        public RComplexProfilingView(RComplex orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        // note that each of getReal, getImag and getComplex counts as one "get", and the size is the number of complex elements
        // so views accessed using getReal and getImag will be more likely materialized that views accessed using getComplex,
        // which is desirable
        @Override
        public double getReal(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getReal(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public double getImag(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getImag(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public Complex getComplex(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getComplex(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RComplex materialize() {
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
                if (orig instanceof RComplexView) {
                    ((RComplexView) orig).materializeInto(res);
                } else {
                    double[] content = ((ComplexImpl) orig).getContent();
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
                ((RComplexView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
        }

        // sum not yet implemented by RComplex
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

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
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
                    int[] content = ((IntImpl) orig).getContent();
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

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
        }

        // sum not implemented yet in RInt
    }

    public static class RLogicalProfilingView extends View.RLogicalProxy<RLogical> implements RLogical, ProfilingView {

        private ViewProfile profile;

        public RLogicalProfilingView(RLogical orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        @Override
        public int getLogical(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getLogical(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RLogical materialize() {
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
                if (orig instanceof RLogicalView) {
                    ((RLogicalView) orig).materializeInto(res);
                } else {
                    // FIXME: this case is needed because materializeInto could have been called because
                    //   "this" is a view (profiling view), if it were known to be a doubleimpl (inserting
                    //   proxy views is visible via instanceof)
                    int[] content = ((LogicalImpl) orig).getContent();
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
                ((RLogicalView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
        }

        // sum not implemented yet in RLogical
    }

    public static class RRawProfilingView extends View.RRawProxy<RRaw> implements RRaw, ProfilingView {

        private ViewProfile profile;

        public RRawProfilingView(RRaw orig, ViewProfile profile) {
            super(orig);
            this.profile = profile;
            profile.onNewView(orig);
        }

        @Override
        public byte getRaw(int i) {
            boolean internal = profile.enterGet();
            try {
                return orig.getRaw(i);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public RRaw materialize() {
            boolean internal = profile.enterMaterialize();
            try {
                return orig.materialize();
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeInto(byte[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                if (orig instanceof RRawView) {
                    ((RRawView) orig).materializeInto(res);
                } else {
                    // FIXME: this case is needed because materializeInto could have been called because
                    //   "this" is a view (profiling view), if it were known to be a doubleimpl (inserting
                    //   proxy views is visible via instanceof)
                    byte[] content = ((RawImpl) orig).getContent();
                    System.arraycopy(content, 0, res, 0, content.length);
                }
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void materializeIntoOnTheFly(byte[] res) {
            boolean internal = profile.enterMaterialize();
            try {
                ((RRawView) orig).materializeIntoOnTheFly(res);
            } finally {
                profile.leave(internal);
            }
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }

        @Override
        public void onAssignment(Object oldValue) {
            profile.onAssignment(orig, oldValue);
        }

        // sum not implemented yet in RRaw
    }

}
