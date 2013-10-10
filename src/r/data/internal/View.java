package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

// FIXME: the Impl classes for data types should also inherit from View.*, reducing code duplication

public abstract class View extends ArrayImpl implements RArray {

    @Override
    public RArray set(int i, Object val) {
        return materialize().set(i, val);
    }

    @Override
    public RArray subset(RAny keys) {
        return materialize().subset(keys);
    }

    @Override
    public RArray subset(RInt index) {
        return materialize().subset(index);
    }

    @Override
    public RArray subset(RString names) {
        return materialize().subset(names);
    }

    @Override
    public String pretty() {
        return materialize().pretty();
    }

    @Override
    public RArray setDimensions(int[] dimensions) {
        if (dimensions() != dimensions) {
            return materialize().setDimensions(dimensions);
        } else {
            return this;
        }
    }

    @Override
    public RArray setNames(Names names) {
        if (names() != names) {
            return materialize().setNames(names);
        } else {
            return this;
        }
    }

    @Override
    public RArray setAttributes(Attributes attributes) {
        if (attributes() != attributes) {
            return materialize().setAttributes(attributes);
        } else {
            return this;
        }
    }

    @Override
    public RArray stripAttributes() {
        // FIXME: this is quite unfortunate that we have to materialize; note that the ProxyViews are used for explicit casts,
        // where attributes should be dropped (and stripAttributes gets called, forcing materialization). At the same time, they
        // are used for unary math operations that should preserve attributes.

        // FIXME: we should probably have two versions of a proxy, one that would no be preserving attributes would be for the casts.
        // But then all code would have to be updated, so that it gets the attributes from the original value where it is available.
        if (dimensions() == null && names() == null && attributes() == null) {
            return this;
        }
        return materialize().stripAttributes();
    }

    @Override
    public RArray stripAttributesKeepNames() {

        if (dimensions() == null && attributes() == null) {
            return this;
        }
        return materialize().stripAttributesKeepNames();
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        Utils.nyi(); // Do we have to bind on the view node or on the implementation
        return null;
    }

    @Override
    public boolean isShared() {
        return true;  // optimization / hack -- unless the views change the default behavior of materialize-on-set, calculating "shared"
                      //   would take unnecessary time, we are copying anyway
    }

    @Override
    public final boolean isTemporary() { // final - if overriden, some things will break (isTemporary => not a view)
        return false;
    }

    public boolean isSharedReal() { // ever used now?
        return true;
    }

    @Override
    public boolean dependsOn(RAny value) {
        return true; // a safe default, but should be overridden for performance whenever possible
    }

    // this method in subclasses can re-use the storage for the oldValue, if not shared
    public RArray materializeOnAssignmentRef(Object oldValue) {
        RArray res = materialize();
        res.ref();
        return res;
    }

    public abstract static class ParametricView extends View { // just a marker for views that are worth materializing

    }

    public abstract static class RRawView extends ParametricView implements RRaw {
        @Override
        public Object get(int i) {
            return getRaw(i);
         }

        @Override
        public RRaw materialize() {
            return RRawFactory.copy(this);
        }

        @Override
        public RRaw materializeOnAssignmentRef(Object oldValue) {
            RRaw res = materialize();
            res.ref();
            return res;
        }

        @Override
        public RRaw asRaw() {
            return this;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return this;
        }

        @Override
        public RLogical asLogical() {
            return TracingView.ViewTrace.trace(new RRaw.RLogicalView(this));
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return asLogical();
        }

        @Override
        public RInt asInt() {
            return TracingView.ViewTrace.trace(new RRaw.RIntView(this));
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return asInt();
        }

        @Override
        public RDouble asDouble() {
            return TracingView.ViewTrace.trace(new RRaw.RDoubleView(this));
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return asDouble();
        }

        @Override
        public RComplex asComplex() {
            return TracingView.ViewTrace.trace(new RRaw.RComplexView(this));
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return asComplex();
        }

        @Override
        public RString asString() {
            return TracingView.ViewTrace.trace(new RRaw.RStringView(this));
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return asString();
        }

        @Override
        public RAny boxedGet(int i) {
            return RRawFactory.getScalar(getRaw(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return false;
        }

        @Override
        public RRaw set(int i, byte val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RRaw.RRawFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RRaw.TYPE_STRING;
        }

        @Override
        public RRaw doStrip() {
            return RRaw.RRawFactory.strip(this);
        }

        @Override
        public RRaw doStripKeepNames() {
            return RRaw.RRawFactory.stripKeepNames(this);
        }
    }

    public abstract static class RRawProxy<O extends RArray> extends RRawView implements RRaw {
        protected final O orig;

        public RRawProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    public abstract static class RLogicalView extends ParametricView implements RLogical {
        @Override
        public Object get(int i) {
            return getLogical(i);
         }

        @Override
        public RLogical materialize() {
            return RLogicalFactory.copy(this);
        }

        @Override
        public RLogical materializeOnAssignmentRef(Object oldValue) {
            RLogical res = materialize();
            res.ref();
            return res;
        }

        @Override
        public RRaw asRaw() {
            return TracingView.ViewTrace.trace(new RLogical.RRawView(this));
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return RLogical.RLogicalUtils.logicalToRaw(this, warn);
        }

        @Override
        public RLogical asLogical() {
            return this;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return this;
        }

        @Override
        public RInt asInt() {
            return TracingView.ViewTrace.trace(new RLogical.RIntView(this));
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return asInt();
        }

        @Override
        public RDouble asDouble() {
            return TracingView.ViewTrace.trace(new RLogical.RDoubleView(this));
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return asDouble();
        }

        @Override
        public RComplex asComplex() {
            return TracingView.ViewTrace.trace(new RLogical.RComplexView(this));
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return asComplex();
        }

        @Override
        public RString asString() {
            return TracingView.ViewTrace.trace(new RLogical.RStringView(this));
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return asString();
        }

        @Override
        public RAny boxedGet(int i) {
            return RLogicalFactory.getScalar(getLogical(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getLogical(i) == RLogical.NA;
        }

        @Override
        public RLogical set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RLogical.RLogicalFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RLogical.TYPE_STRING;
        }

        @Override
        public RLogical doStrip() {
            return RLogical.RLogicalFactory.strip(this);
        }

        @Override
        public RLogical doStripKeepNames() {
            return RLogical.RLogicalFactory.stripKeepNames(this);
        }
    }

    // FIXME: copy-paste of RRawProxy as Java does not have multiple inheritance
    public abstract static class RLogicalProxy<O extends RArray> extends RLogicalView implements RLogical {
        protected final O orig;

        public RLogicalProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    public abstract static class RIntView extends ParametricView implements RInt {
        @Override
        public Object get(int i) {
            return getInt(i);
         }

        @Override
        public RInt materialize() {
            return RIntFactory.copy(this);
        }

        @Override
        public RInt materializeOnAssignmentRef(Object oldValue) {
            RInt res = materialize();
            res.ref();
            return res;
        }

        @Override
        public int[] getContent() {
            return materialize().getContent();
        }

        @Override
        public RRaw asRaw() {
            return TracingView.ViewTrace.trace(new RInt.RRawView(this));
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return RInt.RIntUtils.intToRaw(this, warn);
        }

        @Override
        public RLogical asLogical() {
            return TracingView.ViewTrace.trace(new RInt.RLogicalView(this));
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return asLogical();
        }

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return this;
        }

        @Override
        public RDouble asDouble() {
            return TracingView.ViewTrace.trace(new RInt.RDoubleView(this));
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return asDouble();
        }

        @Override
        public RComplex asComplex() {
            return TracingView.ViewTrace.trace(new RInt.RComplexView(this));
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return asComplex();
        }

        @Override
        public RString asString() {
            return TracingView.ViewTrace.trace(new RInt.RStringView(this));
        }

        @Override
        public RAny boxedGet(int i) {
            return RIntFactory.getScalar(getInt(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getInt(i) == RInt.NA;
        }

        @Override
        public RInt set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RInt.RIntFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RInt.TYPE_STRING;
        }

        @Override
        public RInt doStrip() {
            return RInt.RIntFactory.strip(this);
        }

        @Override
        public RInt doStripKeepNames() {
            return RInt.RIntFactory.stripKeepNames(this);
        }
    }

    // FIXME: copy-paste of RRawProxy as Java does not have multiple inheritance
    public abstract static class RIntProxy<O extends RArray> extends RIntView implements RInt {
        public final O orig;

        public RIntProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    public abstract static class RDoubleView extends ParametricView implements RDouble {
        @Override
        public Object get(int i) {
            return getDouble(i);
        }

        @Override
        public RAny boxedGet(int i) {
            return RDoubleFactory.getScalar(getDouble(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return RDouble.RDoubleUtils.isNAorNaN(getDouble(i));
        }

        @Override
        public RRaw asRaw() {
            return TracingView.ViewTrace.trace(new RDouble.RRawView(this));
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return RDouble.RDoubleUtils.doubleToRaw(this, warn);
        }

        @Override
        public RLogical asLogical() {
            return TracingView.ViewTrace.trace(new RDouble.RLogicalView(this));
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return asLogical();
        }

        @Override
        public RInt asInt() {
            return TracingView.ViewTrace.trace(new RDouble.RIntView(this));
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return RDouble.RDoubleUtils.double2int(this, warn);
        }

        @Override
        public RDouble asDouble() {
            return this;
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return this;
        }

        @Override
        public RComplex asComplex() {
            return TracingView.ViewTrace.trace(new RDouble.RComplexView(this));
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return asComplex();
        }

        @Override
        public RString asString() {
            return TracingView.ViewTrace.trace(new RDouble.RStringView(this));
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return asString();
        }

        @Override
        public RDouble materialize() {
            return RDouble.RDoubleFactory.copy(this);
        }

        @Override
        public RDouble materializeOnAssignmentRef(Object oldValue) {
            RDouble res = materialize();
            res.ref();
            return res;
        }

        @Override
        public double[] getContent() {
            return materialize().getContent();
        }

        @Override
        public RDouble set(int i, double val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RDouble.RDoubleFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RDouble.TYPE_STRING;
        }

        @Override
        public RDouble doStrip() {
            return RDouble.RDoubleFactory.strip(this);
        }

        @Override
        public RDouble doStripKeepNames() {
            return RDouble.RDoubleFactory.stripKeepNames(this);
        }

        @Override
        public double sum(boolean narm) {
            return RDouble.RDoubleUtils.sum(this, narm);
        }
    }

 // FIXME: copy-paste of RRawProxy as Java does not have multiple inheritance
    public abstract static class RDoubleProxy<O extends RArray> extends RDoubleView implements RDouble {
        public final O orig;

        public RDoubleProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    public abstract static class RComplexView extends ParametricView implements RComplex {
        @Override
        public Object get(int i) {
            return new Complex(getReal(i), getImag(i));
        }

        @Override
        public RAny boxedGet(int i) {
            return RComplex.RComplexFactory.getScalar(getReal(i), getImag(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return RComplex.RComplexUtils.eitherIsNAorNaN(getReal(i), getImag(i));
        }

        @Override
        public RRaw asRaw() {
            return asRaw(null);
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return RComplex.RComplexUtils.complexToRaw(this, warn);
        }

        @Override
        public RLogical asLogical() {
            return TracingView.ViewTrace.trace(new RComplex.RLogicalView(this));
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return asLogical();
        }

        @Override
        public RInt asInt() {
            return asInt(null);
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return RComplex.RComplexUtils.complex2int(this, warn);
        }

        @Override
        public RDouble asDouble() {
            return asDouble(null);
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return RComplex.RComplexUtils.complex2double(this, warn);
        }

        @Override
        public RComplex asComplex() {
            return this;
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return this;
        }

        @Override
        public RString asString() {
            return TracingView.ViewTrace.trace(new RComplex.RStringView(this));
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return asString();
        }

        @Override
        public RComplex materialize() {
            return RComplex.RComplexFactory.copy(this);
        }

        @Override
        public RComplex materializeOnAssignmentRef(Object oldValue) {
            RComplex res = materialize();
            res.ref();
            return res;
        }

        @Override
        public double[] getContent() {
            return materialize().getContent();
        }

        @Override
        public RComplex set(int i, double real, double imag) {
            return materialize().set(i, real, imag);
        }

        @Override
        public RArray subset(RInt index) {
            return RComplex.RComplexFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RComplex.TYPE_STRING;
        }

        @Override
        public RComplex doStrip() {
            return RComplex.RComplexFactory.strip(this);
        }

        @Override
        public RComplex doStripKeepNames() {
            return RComplex.RComplexFactory.stripKeepNames(this);
        }
    }

    // FIXME: copy-paste of RRawProxy as Java does not have multiple inheritance
    public abstract static class RComplexProxy<O extends RArray> extends RComplexView implements RComplex {
        public final O orig;

        public RComplexProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    public abstract static class RStringView extends ParametricView implements RString {
        @Override
        public Object get(int i) {
            return getString(i);
         }

        @Override
        public RString materialize() {
            return RString.RStringFactory.copy(this);
        }

        @Override
        public RString materializeOnAssignmentRef(Object oldValue) {
            RString res = materialize();
            res.ref();
            return res;
        }

        @Override
        public RRaw asRaw() {
            Utils.check(false, "unreachable");
            return null;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return RString.RStringUtils.stringToRaw(this, warn);
        }

        @Override
        public RLogical asLogical() {
            Utils.check(false, "unreachable");
            return null;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return RString.RStringUtils.stringToLogical(this, warn);
        }

        @Override
        public RInt asInt() {
            Utils.check(false, "unreachable");
            return null;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return RString.RStringUtils.stringToInt(this, warn);
        }

        @Override
        public RDouble asDouble() {
            Utils.check(false, "unreachable");
            return null;
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return RString.RStringUtils.stringToDouble(this, warn);
        }

        @Override
        public RComplex asComplex() {
            Utils.check(false, "unreachable");
            return null;
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return RString.RStringUtils.stringToComplex(this, warn);
        }

        @Override
        public RString asString() {
            return this;
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return this;
        }

        @Override
        public RAny boxedGet(int i) {
            return RStringFactory.getScalar(getString(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getString(i) == RString.NA;
        }

        @Override
        public RString set(int i, String val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RString.RStringFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RString.TYPE_STRING;
        }

        @Override
        public RString doStrip() {
            return RString.RStringFactory.strip(this);
        }

        @Override
        public RString doStripKeepNames() {
            return RString.RStringFactory.stripKeepNames(this);
        }
    }

    // FIXME: copy-paste of RRawProxy as Java does not have multiple inheritance
    public abstract static class RStringProxy<O extends RArray> extends RStringView implements RString {
        protected final O orig;

        public RStringProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    public abstract static class RListView extends ParametricView implements RList {
        @Override
        public Object get(int i) {
            return getRAny(i);
         }

        @Override
        public ListImpl materialize() {
            return RList.RListFactory.copy(this);
        }

        @Override
        public ListImpl materializeOnAssignmentRef(Object oldValue) {
            ListImpl res = materialize();
            res.ref();
            return res;
        }

        @Override
        public RRaw asRaw() {
            Utils.nyi();
            return null;
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RInt asInt() {
            Utils.nyi();
            return null;
        }

        @Override
        public RDouble asDouble() {
            Utils.nyi();
            return null;
        }

        @Override
        public RComplex asComplex() {
            Utils.nyi();
            return null;
        }

        @Override
        public RString asString() {
            Utils.nyi();
            return null;
        }

        @Override
        public RList asList() {
            return this;
        }

        @Override
        public RAny boxedGet(int i) {
            return RList.RListFactory.getScalar(getRAny(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            RAny v = getRAny(i);
            if (v instanceof RArray) {
                RArray a = (RArray) v;
                if (a.size() == 1) {
                    return a.isNAorNaN(0);
                }
            }
            return false;
        }

        @Override
        public RArray set(int i, RAny val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RList.RListFactory.subset(this, index);
        }

        @Override
        public RAny getRAnyRef(int i) {
            RAny v = getRAny(i);
            v.ref();
            return v;
        }

        @Override
        public String typeOf() {
            return RList.TYPE_STRING;
        }

        @Override
        public RList doStrip() {
            return RList.RListFactory.strip(this);
        }

        @Override
        public RList doStripKeepNames() {
            return RList.RListFactory.stripKeepNames(this);
        }
    }

    // FIXME: copy-paste of RRawProxy as Java does not have multiple inheritance
    public abstract static class RListProxy<O extends RArray> extends RListView implements RList {
        protected final O orig;

        public RListProxy(O orig) {
            this.orig = orig;
            Attributes.markShared(orig.attributes());
        }

        @Override
        public int size() {
            return orig.size();
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
        public int[] dimensions() {
            return orig.dimensions();
        }

        @Override
        public Names names() {
            return orig.names();
        }

        @Override
        public Attributes attributes() {
            return orig.attributes();
        }

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
        }
    }

    // FIXME: copy-paste of RIntView
    public abstract static class ConstantIntView extends View implements RInt {
        @Override
        public Object get(int i) {
            return getInt(i);
         }

        @Override
        public RInt materialize() {
            return RIntFactory.copy(this);
        }

        @Override
        public RInt materializeOnAssignmentRef(Object oldValue) {
            Utils.nyi("unreachable");
            return null;
        }

        @Override
        public int[] getContent() {
            return materialize().getContent();
        }

        @Override
        public RRaw asRaw() {
            return TracingView.ViewTrace.trace(new RInt.RRawView(this));
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return RInt.RIntUtils.intToRaw(this, warn);
        }

        @Override
        public RLogical asLogical() {
            return TracingView.ViewTrace.trace(new RInt.RLogicalView(this));
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return asLogical();
        }

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return this;
        }

        @Override
        public RDouble asDouble() {
            return TracingView.ViewTrace.trace(new RInt.RDoubleView(this));
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return asDouble();
        }

        @Override
        public RComplex asComplex() {
            return TracingView.ViewTrace.trace(new RInt.RComplexView(this));
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return asComplex();
        }

        @Override
        public RString asString() {
            return TracingView.ViewTrace.trace(new RInt.RStringView(this));
        }

        @Override
        public RAny boxedGet(int i) {
            return RIntFactory.getScalar(getInt(i));
        }

        @Override
        public boolean isNAorNaN(int i) {
            return getInt(i) == RInt.NA;
        }

        @Override
        public RInt set(int i, int val) {
            return materialize().set(i, val);
        }

        @Override
        public RArray subset(RInt index) {
            return RInt.RIntFactory.subset(this, index);
        }

        @Override
        public String typeOf() {
            return RInt.TYPE_STRING;
        }

        @Override
        public RInt doStrip() {
            return RInt.RIntFactory.strip(this);
        }

        @Override
        public RInt doStripKeepNames() {
            return RInt.RIntFactory.stripKeepNames(this);
        }
    }
}
