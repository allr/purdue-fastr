package r.fusion;

import r.data.*;
import r.data.internal.*;
import r.nodes.exec.Arithmetic;

/** Builds the signature for the view
 */
public class SignatureBuilder extends View.Visitor {

    static class UnsupportedViewStructure extends Error {
    }

    StringBuilder sb = new StringBuilder();

    /** Returns the signature of the view.
     *
     * If the signature of the view is not supported, returns null.
     */
    public static String build(View view) {
        try {
            SignatureBuilder sb = new SignatureBuilder();
            view.visit(sb);
            return sb.sb.toString();
        } catch (UnsupportedViewStructure e) {
            if (Fusion.DEBUG) {
                System.out.println("Unsupported view structure detected:");
                e.printStackTrace();
            }
            return null;
        }
    }

    public void visitLeaf(RAny element) {
        throw new UnsupportedViewStructure();
    }

    private void addInput(int size, char type) {
        sb.append("_" + (size == 1 ? "S" : "V") + String.valueOf(type));
    }

    private void addBinary(char op, char size, char type, char resultSize) {
        sb.append("B");
        sb.append(op);
        sb.append(size);
        sb.append(type);
        sb.append(resultSize);
    }

    private char operatorToChar(Arithmetic.ValueArithmetic arit) {
        if (arit == Arithmetic.ADD)
            return Node.ADD;
        else if (arit == Arithmetic.SUB)
            return Node.SUB;
        else if (arit == Arithmetic.MULT)
            return Node.MUL;
        else if (arit == Arithmetic.DIV)
            return Node.DIV;
        else if (arit == Arithmetic.MOD)
            return Node.MOD;
        else
            throw new UnsupportedViewStructure();
    }

    public void visitLeaf(RDouble element) {
        addInput(element.size(), 'D');
    }
    public void visitLeaf(RInt element) {
        addInput(element.size(), 'I');
    }
    public void visitLeaf(RComplex element) {
        addInput(element.size(), 'C');
    }

    public void visit(View view) {
        throw new UnsupportedViewStructure();
    }

    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'A');
    }

    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'B');
    }

    public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'E');
    }

    public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'A');
    }

    public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'B');
    }

    public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'E');
    }

    public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'E');
    }

    public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
        addBinary(operatorToChar(view.arit), 'V', 'D', 'A');
    }

    public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
        addBinary(operatorToChar(view.arit), 'V', 'I', 'E');
    }
}
