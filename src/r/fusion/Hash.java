package r.fusion;

import r.data.*;
import r.data.internal.*;
import r.nodes.exec.Arithmetic;

/**
 * Hash computation for the view. Each characteristic of the view has a random number assigned to it. When the tree is
 * walked, the characteristics are added up and rotated left with the index of the current node in preorder walk. The
 * resulting hash is returned, or 0 if the view contains unsupported elements. FIXME THREAD UNSAFE This class uses
 * signleton instance to minimize memory allocation, this also means that the hashing is thread unsafe. Should not be
 * problem for now, but might be a problem if we add proper parallelism. TODO if we want even more speed, this should
 * not e a visitor to elliminate the double dispatch
 */
public class Hash extends View.Visitor {

    static final Hash instance = new Hash();

    int h = 0;
    int i = 0;

    private Hash() {}

    public static int view(View view) {
        return instance.build_(view);
    }

    private int build_(View view) {
        if (view instanceof ProfilingView) return 0;
        h = 0;
        i = 0;
        try {
            view.visit(this);
            if (h != 0) {
                System.out.println("Worked for " + view.getClass());
            } else {
                System.out.println("Failed for " + view.getClass());
            }
            return h;
        } catch (NotSupported e) {
            System.out.println("Exception Failed for " + view.getClass());
            return 0;
        }
    }

    @Override public void visitLeaf(RAny element) {
        //assert (false);
        throw new NotSupported();
    }

    @Override public void visit(View view) {
        System.out.println("Exception Failed for " + view.getClass());
        //assert (false);
        throw new NotSupported();
    }

    private int operatorToInt(Arithmetic.ValueArithmetic arit) {
        if (arit == Arithmetic.ADD) return Fusion.ADD;
        else if (arit == Arithmetic.SUB) return Fusion.SUB;
        else if (arit == Arithmetic.MULT) return Fusion.MUL;
        else if (arit == Arithmetic.DIV) return Fusion.DIV;
        else if (arit == Arithmetic.MOD) return Fusion.MOD;
        else throw new NotSupported();
    }

    private void addHash(int value) {
        i = (i + 1) % 32;
        h += Integer.rotateLeft(value, i);
    }

    @Override public void visitDoubleLeaf(RDouble element) {
        addHash(Fusion.INPUT + Fusion.DOUBLE);
    }

    @Override public void visitIntLeaf(RInt element) {
        addHash(Fusion.INPUT + Fusion.INT);
    }

    @Override public void visitComplexLeaf(RComplex element) {
        addHash(Fusion.INPUT + Fusion.COMPLEX);
    }

    @Override public void visitIntSequenceLeaf(IntImpl.RIntSequence element) {
        addHash(Fusion.INPUT + Fusion.INT_SEQUENCE);
    }

    @Override public void visit(RDouble.RIntView view) {
        addHash(Fusion.CONVERSION + Fusion.INT);
        visitDouble_(view.orig);
    }

    @Override public void visit(RInt.RDoubleView view) {
        addHash(Fusion.CONVERSION + Fusion.DOUBLE);
        visitInt_(view.orig);
    }

    @Override public void visit(RInt.RIntSubset view) {
        addHash(Fusion.SUBSET + Fusion.INT + Fusion.INT);
        visitInt_(view.base);
        visitInt_(view.index);
    }

    @Override public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.A + Fusion.VECTOR + Fusion.VECTOR);
        visitDouble_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.B + Fusion.VECTOR + Fusion.VECTOR);
        visitDouble_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.EQUAL + Fusion.VECTOR + Fusion.VECTOR);
        visitDouble_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.A + Fusion.VECTOR + Fusion.SCALAR);
        visitDouble_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.B + Fusion.SCALAR + Fusion.VECTOR);
        visitDouble_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.EQUAL + Fusion.VECTOR + Fusion.VECTOR);
        visitDouble_(view.a);
        visitInt_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.EQUAL + Fusion.VECTOR + Fusion.VECTOR);
        visitInt_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.DOUBLE + Fusion.EQUAL + Fusion.VECTOR + Fusion.VECTOR);
        visitInt_(view.a);
        visitDouble_(view.b);
    }

    @Override public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.INT + Fusion.EQUAL + Fusion.VECTOR + Fusion.VECTOR);
        visitInt_(view.a);
        visitInt_(view.b);
    }

    @Override public void visit(Arithmetic.IntViewForIntInt.VectorScalar view) {
        addHash(Fusion.BINARY + operatorToInt(view.arit) + Fusion.VECTOR + Fusion.INT + Fusion.A + Fusion.VECTOR + Fusion.SCALAR);
        visitInt_(view.a);
        visitInt_(view.b);
    }
}
