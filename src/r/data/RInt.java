package r.data;

import java.util.*;

import r.*;
import r.Convert.ConversionStatus;
import r.data.internal.*;

public interface RInt extends RNumber {
    int NA = Integer.MIN_VALUE;
    String TYPE_STRING = "integer";
    ScalarIntImpl BOXED_NA = (ScalarIntImpl) RArrayUtils.markShared(RIntFactory.getScalar(NA));
    ScalarIntImpl BOXED_ZERO = (ScalarIntImpl) RArrayUtils.markShared(RIntFactory.getScalar(0));
    ScalarIntImpl BOXED_ONE = (ScalarIntImpl) RArrayUtils.markShared(RIntFactory.getScalar(1));
    IntImpl EMPTY = (IntImpl) RArrayUtils.markShared(RIntFactory.getUninitializedArray(0));
    IntImpl EMPTY_NAMED_NA = (IntImpl) RArrayUtils.markShared(RIntFactory.getFor(new int[] {}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));
    IntImpl NA_NAMED_NA = (IntImpl) RArrayUtils.markShared(RIntFactory.getFor(new int[] {NA}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));

    int getInt(int i);
    RInt set(int i, int val);
    RInt materialize();
    public int[] getContent();

    public class RIntUtils {
        public static RRaw intToRaw(RInt value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                int ival = value.getInt(i);
                content[i] = Convert.int2raw(ival, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions(), value.names());
        }
        public static int[] copyAsIntArray(RInt v) {
            int size = v.size();
            if (size == 1) {
                return new int[] {v.getInt(0)};
            } else {
                int[] res = new int[size];

                if (v instanceof IntImpl) {
                    System.arraycopy(((IntImpl) v).getContent(), 0, res, 0, size);
                } else {
                    for (int i = 0; i < size; i++) {
                        res[i] = v.getInt(i);
                    }
                }
                return res;
            }
        }
    }
    public class RIntFactory {
        public static ScalarIntImpl getScalar(int value) {
            return new ScalarIntImpl(value);
        }
        public static RInt getScalar(int value, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarIntImpl(value);
            } else {
                return getFor(new int[] {value}, dimensions, null);
            }
        }
        public static RInt getArray(int... values) {
            if (values.length == 1) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values);
        }
        public static RInt getArray(int[] values, int[] dimensions) {
            if (dimensions == null && values.length == 1) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values, dimensions, null);
        }
        public static RInt getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarIntImpl(0);
            }
            return new IntImpl(size);
        }
        public static RInt getUninitializedNonScalarArray(int size) {
            return new IntImpl(size);
        }
        public static RInt getUninitializedArray(int size, int[] dimensions, Names names, Attributes attributes) {
            if (size == 1 && dimensions == null && names == null && attributes == null) {
                return new ScalarIntImpl(0);
            }
            return new IntImpl(new int[size], dimensions, names, attributes, false);
        }
        public static RInt getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RInt getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            int[] content = new int[size];
            Arrays.fill(content, NA);
            return new IntImpl(content, dimensions, null, null, false);
        }
        public static IntImpl getMatrixFor(int[] values, int m, int n) {
            return new IntImpl(values, new int[] {m, n}, null, null, false);
        }
        public static RInt copy(RInt i) {
            if (i.size() == 1 && i.dimensions() == null && i.names() == null && i.attributes() == null) {
                return new ScalarIntImpl(i.getInt(0));
            }
            return new IntImpl(i, false);
        }
        public static RInt strip(RInt v) {
            if (v.size() == 1) {
                return new ScalarIntImpl(v.getInt(0));
            }
            return new IntImpl(v, true);
        }
        public static RInt stripKeepNames(RInt v) {
            Names names = v.names();
            if (v.size() == 1 && names == null) {
                return new ScalarIntImpl(v.getInt(0));
            }
            return new IntImpl(v, null, names, null);
        }
        public static RInt getFor(int[] values) { // re-uses values!
            return getFor(values, null, null);
        }
        public static RInt getFor(int[] values, int[] dimensions, Names names) {  // re-uses values!
            if (values.length == 1 && dimensions == null && names == null) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values, dimensions, names, null, false);
        }
        public static RInt getFor(int[] values, int[] dimensions, Names names, Attributes attributes) {  // re-uses values!
            if (values.length == 1 && dimensions == null && names == null && attributes == null) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values, dimensions, names, attributes, false);
        }
        public static RInt forSequence(int from, int to, int step) {
            if (from == to) {
                return getScalar(from);
            }
            if (from == 1 && to > 0 && step == 1) {
                return TracingView.ViewTrace.trace(new IntImpl.RIntSimpleRange(to));
            }
            return TracingView.ViewTrace.trace(new IntImpl.RIntSequence(from, to, step));
        }
        public static RInt getEmpty(boolean named) {
            return named ? EMPTY_NAMED_NA : EMPTY;
        }
        public static RInt getNA(boolean named) {
            return named ? NA_NAMED_NA : BOXED_NA;
        }
        public static RInt exclude(int excludeIndex, RInt orig) {
            Names names = orig.names();
            if (names == null) {
                return TracingView.ViewTrace.trace(new RIntExclusion(excludeIndex, orig));
            }
            int size = orig.size();
            int nsize = size - 1;
            int[] content = new int[nsize];
            for (int i = 0; i < excludeIndex; i++) {
                content[i] = orig.getInt(i);
            }
            for (int i = excludeIndex; i < nsize; i++) {
                content[i] = orig.getInt(i + 1);
            }
            return RIntFactory.getFor(content, null, names.exclude(excludeIndex));
        }
        public static RInt subset(RInt value, RInt index) {
            return TracingView.ViewTrace.trace(new RIntSubset(value, index));
        }
    }

    public static class RStringView extends View.RStringProxy<RInt> implements RString {

        public RStringView(RInt orig) {
            super(orig);
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public String getString(int i) {
            int v = orig.getInt(i);
            return Convert.int2string(v);
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }
    }

    public static class RComplexView extends View.RComplexProxy<RInt> implements RComplex {

        public RComplexView(RInt orig) {
            super(orig);
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getReal(int i) {
            int v = orig.getInt(i);
            return Convert.int2double(v);
        }

        @Override
        public double getImag(int i) {
            int v = orig.getInt(i);
            if (v == RInt.NA) {
                return RDouble.NA;
            } else {
                return 0;
            }
        }

        @Override
        public Complex getComplex(int i) {
            return Convert.int2complex(orig.getInt(i));
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }
    }

    public static class RDoubleView extends View.RDoubleProxy<RInt> implements RDouble {

        public RDoubleView(RInt orig) {
            super(orig);
        }

        /** FUSION type dispatched visitor for View.Visitor
         */
        @Override
        public void visit(View.Visitor visitor) {
            visitor.visit(this);
        }

     // fast debugging format does not support this shortcut, non-debugging format probably would
//        @Override
//        public RString asString() {
//            return orig.asString();
//        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getDouble(int i) {
            int v = orig.getInt(i);
            return Convert.int2double(v);
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }
    }

    public static class RLogicalView extends View.RLogicalProxy<RInt> implements RLogical {

        public RLogicalView(RInt orig) {
            super(orig);
        }

        @Override
        public int getLogical(int i) {
            int v = orig.getInt(i);
            return Convert.int2logical(v);
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }
    }

    public static class RRawView extends View.RRawProxy<RInt> implements RRaw {

        public RRawView(RInt orig) {
            super(orig);
        }

        @Override
        public byte getRaw(int i) {
            int v = orig.getInt(i);
            return Convert.int2raw(v);
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
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
            assert Utils.check(i < size, "bounds check");
            assert Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getInt(i);
            } else {
                return orig.getInt(i + 1);
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

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }

        @Override
        public void visit_all(ValueVisitor v) {
            orig.accept(v);
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RIntSubset extends View.RIntView implements RInt {

        public final RInt base;
        final int bsize;
        public final RInt index;
        final int isize;

        public RIntSubset(RInt base, RInt index) {
            this.base = base;
            this.index = index;
            this.isize = index.size();
            this.bsize = base.size();
        }

        /** FUSION View.Visitor dispatch.
         */
        @Override
        public void visit(View.Visitor visitor) {
            visitor.visit(this);
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public int getInt(int i) {
            int j = index.getInt(i);
            assert Utils.check(j > 0);
            if (j > bsize) {
                return RInt.NA;
            } else {
                return base.getInt(j - 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return base.isShared() || index.isShared();
        }

        @Override
        public void ref() {
            base.ref();
            index.ref();
        }

        @Override
        public boolean dependsOn(RAny v) {
            return base.dependsOn(v) || index.dependsOn(v);
        }

        private void subset(int[] baseArr, int[] indexArr, int[] res) {
            int n = isize;
            for (int i = 0; i < n; i++) {
                int j = indexArr[i];
                if (j > bsize) {
                    res[i] = RInt.NA;
                } else {
                    res[i] = baseArr[j - 1];
                }
            }
        }

        @Override
        public void materializeInto(int[] resContent) {
            if (base instanceof IntImpl) {
                if (index instanceof IntImpl) {
                    subset(base.getContent(), index.getContent(), resContent);
                    return;
                }
                if (index instanceof RIntView) {
                    ((RIntView) index).materializeInto(resContent);
                    subset(base.getContent(), resContent, resContent);
                    return;
                }
            }
            // note: indeed cannot use resContent to materialize the base, because of arbitrary read pattern to it
            //       (also the base can be longer than the index)
            super.materializeInto(resContent);
        }

        @Override
        public void materializeIntoOnTheFly(int[] resContent) {
            if (base instanceof IntImpl && index instanceof IntImpl) {
                subset(base.getContent(), index.getContent(), resContent);
            } else {
                super.materializeIntoOnTheFly(resContent);
            }
        }

        @Override
        public void visit_all(ValueVisitor v) {
            index.accept(v);
            base.accept(v);
        }

        @Override
        public void accept(ValueVisitor v) {
            v.visit(this);
        }
    }


}
