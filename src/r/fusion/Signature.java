package r.fusion;

import r.data.*;
import r.data.internal.*;
import r.nodes.exec.Arithmetic;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 11/6/13 Time: 3:39 PM To change this template use File | Settings | File
 * Templates.
 */
public class Signature extends View.Visitor {


    static final Signature instance = new Signature();


    private Signature() {}

    StringBuilder sb = new StringBuilder();

    public static String view(View view) {
        return instance.build_(view);
    }

    private String build_(View view) {
        if (view instanceof ProfilingView)
            return "(PROFILING VIEW)";
        try {
            view.visit(this);
            String result = sb.toString();
            sb.delete(0, sb.length());
            return result;
        } catch (NotSupported e) {
            return "(UNSUPPORTED)";
        }
    }

    @Override
    public void visitLeaf(RAny element) {
        throw new NotSupported();
    }

    @Override
    public void visit(View view) {
        throw new NotSupported();
    }

    private String operatorToString(Arithmetic.ValueArithmetic arit) {
        if (arit == Arithmetic.ADD)
            return " + ";
        else if (arit == Arithmetic.SUB)
            return " - ";
        else if (arit == Arithmetic.MULT)
            return " * ";
        else if (arit == Arithmetic.DIV)
            return " / ";
        else if (arit == Arithmetic.MOD)
            return " % ";
        else
            throw new NotSupported();
    }


    @Override
    public void visitDoubleLeaf(RDouble element) {
        sb.append("double");
        if (element.size() != 1)
            sb.append("[]");
    }

    @Override
    public void visitIntLeaf(RInt element) {
        sb.append("int");
        if (element.size() != 1)
            sb.append("[]");
    }

    @Override
    public void visitComplexLeaf(RComplex element) {
        sb.append("complex");
        if (element.size() != 1)
            sb.append("[]");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
        sb.append("(A ");
        visitDouble_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
        sb.append("(B ");
        visitDouble_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
        sb.append("(E ");
        visitDouble_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
        sb.append("(A ");
        visitDouble_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
        sb.append("(B ");
        visitDouble_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
        sb.append("(E ");
        visitDouble_(view.a);
        sb.append(operatorToString(view.arit));
        visitInt_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
        sb.append("(E ");
        visitInt_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
        sb.append("(E ");
        visitInt_(view.a);
        sb.append(operatorToString(view.arit));
        visitDouble_(view.b);
        sb.append(")");
    }

    @Override
    public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
        sb.append("(E ");
        visitInt_(view.a);
        sb.append(operatorToString(view.arit));
        visitInt_(view.b);
        sb.append(")");
    }


}
