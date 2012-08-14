package r.nodes;

import r.*;
import r.data.*;


public class Constant implements Node {
    RAny value;

    Constant(RAny val) {
        value = val;
    }

    public String prettyValue() {
        return value.pretty();
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public static Constant createDoubleConstant(String... values) {
        double[] val = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            val[i] = Convert.string2double(values[i]);
        }
        return createDoubleConstant(val);
    }
    public static Constant createDoubleConstant(double... values) {
        if (values.length == 1) {
            return new Constant(RDouble.RDoubleFactory.getArray(values));
        }
        throw new Error("Non scalar constants are not implemented.");
    }
    public static Constant createComplexConstant(String... values) {
        double[] val = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            val[i] = Convert.string2double(values[i]);
        }
        return createComplexConstant(val);
    }
    public static Constant createComplexConstant(double... values) {
        if (values.length == 1) {
//            return new Constant(RComplex.RDoubleFactory.getArray(values));
            // Punt since I don't whant to create a class constant
        }
        throw new Error("Non scalar constants are not implemented.");
    }
    public static Constant createIntConstant(String... values) {
        int[] val = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            val[i] = Convert.string2int(values[i]);
        }
        return createIntConstant(val);
    }
    public static Constant createIntConstant(int... values) {
        if (values.length == 1) {
            return new Constant(RInt.RIntFactory.getArray(values));
        }
        throw new Error("Non scalar constants are not implemented.");
    }
    public static Constant createBoolConstant(String... values) {
        int[] val = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            val[i] = Convert.string2lgl(values[i]);
        }
        return createBoolConstant(values);
    }
    public static Constant createBoolConstant(int... values) {
        if (values.length == 1) {
            return new Constant(RLogical.RLogicalFactory.getArray(values));
        }
        throw new Error("Non scalar constants are not implemented.");
    }

    public static Node getNull() {
        return new Constant(Null.getNull());
    }
}
