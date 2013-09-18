package r.nodes.ast;

import r.Convert;
import r.data.*;

@Precedence(Precedence.MAX)
public class Constant extends ASTNode {

    final RAny value;

    public Constant(RAny val) {
        value = val;
    }

    public String prettyValue() {
        return getValue().pretty();
    }

    public static ASTNode getNull() {
        return new Constant(RNull.getNull());
    }

    @Override
    public void visit_all(Visitor v) {
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
            return new Constant(RDouble.RDoubleFactory.getScalar(values[0]));
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
            return new Constant(RComplex.RComplexFactory.getScalar(0, values[0]));
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
            return new Constant(RInt.RIntFactory.getScalar(values[0]));
        }
        throw new Error("Non scalar constants are not implemented.");
    }

    public static Constant createBoolConstant(String... values) {
        int[] val = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            String s = values[i];
            if (!s.equals("NA")) {
                val[i] = Convert.string2logical(s);
            } else {
                val[i] = RLogical.NA;
            }
        }
        return createBoolConstant(val);
    }

    public static Constant createBoolConstant(int... values) {
        if (values.length == 1) {
            return new Constant(RLogical.RLogicalFactory.getScalar(values[0]));
        }
        throw new Error("Non scalar constants are not implemented.");
    }

    public static Constant createStringConstant(String... values) {
        if (values.length == 1) {
            return new Constant(RString.RStringFactory.getScalar(values[0]));
        }
        throw new Error("Non scalar constants are not implemented.");
    }

    @Override
    public String toString() {
        return getValue().pretty();
    }

    public RAny getValue() {
        return value;
    }
}
