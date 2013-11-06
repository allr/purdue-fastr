package r.fusion;

import javassist.*;
import r.data.*;
import r.data.internal.View;
import r.nodes.exec.Arithmetic;

import java.util.*;

/** The Fast and more complete fused operator class generator and prototype.
 *
 *
 *
 */
public class FusedOperator extends View.Visitor {

    private static final FusedOperator instance = new FusedOperator();

    public static FusedOperator.Prototype build(View view, int hash) {
        // TODO add try-catch for unsupported
        instance.build_(view, hash);
        return null;
    }

    // FusedOperator.Prototype -----------------------------------------------------------------------------------------

    /** Prototype of fused operators.
     *
     * Contains stubs of the methods used to bind the operator to particular
     *
     */
    public static abstract class Prototype extends View.Visitor {

        /** Contains the list of all inner node classes. Since the views are hashed, this is the protection against hash
         * collisions that may otherwise happen.
         *
         * During the binding of the fused operator when the view tree is walked, all inner nodes are checked if they
         * confirm to the given classes. For certain inner nodes more than one slot of the array may be used.
         */
        protected Class[] nodeClasses;

        /** Index to the nodeClasses array for checking the internal view node classes to make sure the hash collisions
         * can be identified.
         */
        protected int nodeClassesIndex = 0;

        /** Checks next class in the array of inner node classes.
         *
         * If the class does not correspond or no classes are left the NotSupported error is raised. Otherwise the index
         * to the node classes is automatically advanced.
         */
        protected void checkClass(Class clazz) {
            if ((nodeClassesIndex == nodeClasses.length) || (nodeClasses[nodeClassesIndex] != clazz))
                throw new NotSupported();
            ++nodeClassesIndex;
        }

        /** Any view that is not explicitely listed in a method below is not understood by the fused operator.
         *
         * Default case throws the NotSupported exception.
         */
        @Override
        public void visit(View view) {
            throw new NotSupported();
        }

        /** Any leaf type that is not especially overriden is not understood by the fused operator.
         *
         * Default case throws the NotSupported exception.
         *
         * Since the leaf nodes should bind the inputs to the fields of the fused operators, they must be generated in
         * the children and cannot be listed here.
         */
        @Override
        public void visitLeaf(RAny value) {
            throw new NotSupported();
        }

        /** The visitor methods for understood operators.
         *
         * Each of such classes perform first the class checks to make sure the view signature is the same and then
         * visits its children so that the whole view is visited.
         *
         * TODO compute result attributes, dimensions and names
         */
        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            visitInt_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitInt_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitInt_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitInt_(view.a);
            visitInt_(view.b);
        }

        /** Binds the fused operator to given view.
         *
         * The view is walked and checked that it is the same as the one for which the fused operator was created by
         * checking classes of inner nodes and their operations. Input types are checked and the inputs are bound to the
         * local input variables.
         *
         * TODO calculate dimensions attributes and names
         */
        public void bind(View view) {
            nodeClassesIndex = 0;
            view.visit(this);
        }

        /** Frees the fused operator from the previously bound inputs.
         *
         * This method should be overriden to set all inputs of the fused operator to null so that they can be garbage
         * collected if no-one holds their references.
         */
        public void free() {
        }

        /** Binds the fusion operator to the given view, computes and returns the result and calls the free() method to
         * allow potential garbage collection of the inputs.
         */
        public final RArray materialize(View view) {
            bind(view);
            RArray result = materialize_();
            free();
            return result;
        }


        /** Computes the materialization of the previously bound view.
         *
         * This is the main method to be overriden in the subclasses.
         *
         * @return Materialzed view.
         */
        public abstract RArray materialize_();
    }

    // main build method and structures --------------------------------------------------------------------------------

    /** String builder used to emit the code of the actual computation.
     */
    StringBuilder code = new StringBuilder();

    Vector<Class> nodeClasses = new Vector<>();

    // Input -----------------------------------------------------------------------------------------------------------

    /** Simple class capturing all information about the input.
     */
    private static class Input {
        public final RAny data;
        public final int index;
        public final int type;
        public final boolean isVector;
        public boolean isSameSizeAsResult;
        public Input(RAny data, int index, int type, boolean isVector) {
            this.data = data;
            this.index = index;
            this.type = type;
            this.isVector = isVector;
            isSameSizeAsResult = false;
        }

        public final String inputName() {
            return (isVector ? "input": "in") + index;
        }

        public final String inputElementName() {
            return "in"+index;
        }
    }

    HashMap<RAny, Input> inputIndices = new HashMap<>();
    Vector<Input> inputs = new Vector<>();

    ResultSizePropagator rsp = new ResultSizePropagator();

    int resultSize = 0;

    final ClassPool pool = ClassPool.getDefault();

    final CtClass prototype;

    CtClass fop;

    int hash;

    View view;

    private FusedOperator() {
        CtClass p = null;
        try {
            p = pool.get("r.fusion.FusedOperator$Prototype");
        } catch (NotFoundException e) {
            if (Fusion.DEBUG)
                e.printStackTrace();
            System.err.println("Initialization of Fusion framework failed, exitting...");
            System.exit(-1);
        }
        prototype = p;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hash: "+hash+"\n");
        sb.append("Signature: "+Signature.view(view)+"\n");
        for (Input i: inputs) {
            sb.append("Input "+i.index+":  ");
            sb.append(i.isVector ? "V " : "S ");
            if (i.type == Fusion.DOUBLE)
                sb.append("double  ");
            else if (i.type == Fusion.INT)
                sb.append("int     ");
            else if (i.type == Fusion.COMPLEX)
                sb.append("complex ");
            else
                sb.append("ERROR   ");
            if (i.isSameSizeAsResult)
                sb.append("(result size)");
            sb.append("\n");
        }
        sb.append("Result size: input "+resultSize+"\n");

        return sb.toString();
    }

    private FusedOperator.Prototype build_(View view, int hash) {
        this.hash = hash;
        this.view = view;
        try {
            // walk the view, capture inputs and generate the code for the materialization loop and generate the list of
            // checked classes when the view will later be bound
            view.visit(this);
            // propagate the result size to all inputs so that we know which one is result sized and whose have to be wrapped
            rsp.propagate(view);
            // create the the CtClass for the fused operator, convert hash to class name
            fop = pool.makeClass("FOP_"+String.valueOf(hash).replaceAll("-","m"));
            fop.setSuperclass(prototype);
            // add the fields for the inputs
            addFields();

            System.out.println(this);
        } catch (CannotCompileException e) {
            if (Fusion.DEBUG)
                e.printStackTrace();
        } catch (Exception e) {
            if (Fusion.DEBUG) {
                e.printStackTrace();
                System.err.println("Unexpected error reported while building ");
            }

        } finally {
            // cleanup at the end to allow GC
            code.delete(0, code.length());
            inputIndices.clear();
            inputs.clear();
            nodeClasses.clear();
            fop = null;
            view = null;
        }
        return NO_FUSION;
    }

    private void addFields() throws NotFoundException, CannotCompileException {
        for (Input i: inputs) {
            if (i.type == Fusion.DOUBLE) {
                fop.addField(new CtField(pool.get(i.isVector ? "double[]" : "double"),i.inputName(), fop));
            } else if (i.type == Fusion.INT) {
                fop.addField(new CtField(pool.get(i.isVector ? "int[]" : "int"),i.inputName(), fop));
            } else {
                assert (false);
            }
        }

    }


    /** Adds given class to the list of classes that will be checked at runtime when binding the fused operator.
     *
     * Note that this must be the same order the classes will later be checked.
     */
    private void checkClass(Class clazz) {
        nodeClasses.add(clazz);
    }

    // node visitor implementation -------------------------------------------------------------------------------------

    /** Throws NotSupported exception for any input type that is not handled separately below.
     */
    @Override
    public void visitLeaf(RAny input) {
        throw new NotSupported();
    }

    /** Throws NotSupported exception for any inner node that is not handled separately below.
     */
    @Override
    public void visit(View view) {
        throw new NotSupported();
    }


    @Override
    public void visitLeaf(RDouble data) {
        resultSize = inputs.size(); // set the result size to the input, will be adjusted by operators
        Input i = new Input(data, inputs.size(), Fusion.DOUBLE, data.size() != 1);
        inputs.add(i);
        inputIndices.put(data, i);
    }

    @Override
    public void visitLeaf(RInt data) {
        resultSize = inputs.size(); // set the result size to the input, will be adjusted by operators
        Input i = new Input(data, inputs.size(), Fusion.INT, data.size() != 1);
        inputs.add(i);
        inputIndices.put(data, i);
    }

    @Override
    public void visitLeaf(RComplex data) {
        // TODO support complex numbers
        throw new NotSupported();
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        int rs = resultSize; // backup result size because it will be overriden by b
        visitDouble_(view.b);
        resultSize = rs;
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        visitDouble_(view.b); // result size will stay that of b
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        visitDouble_(view.b); // both sizes are the same, we are happy with the second
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        int rs = resultSize; // backup the result size of the vector
        visitDouble_(view.b);
        resultSize = rs;
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        visitDouble_(view.b); // the result size of the vector will be returned
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        visitInt_(view.b); // equally sized operators, the second is ok
    }

    @Override
    public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitInt_(view.a);
        visitDouble_(view.b); // equally sized operators, the second is ok
    }

    @Override
    public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitInt_(view.a);
        int rs = resultSize; // backup the result size from the vector operand
        visitDouble_(view.b);
        resultSize = rs;
    }

    @Override
    public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitInt_(view.a);
        visitInt_(view.b); // equal sizes, both are good
    }


    // ResultSizePropagator --------------------------------------------------------------------------------------------

    /** Propagates the size of the result to all inputs which have the same size.
     *
     * A very simple visitor which only visits subtrees that have the same size as the result of the view. When a leaf
     * is visited, it is marked as being the same size as the result.
     */
    class ResultSizePropagator extends View.Visitor {

        public void propagate(View view) {
            view.visit(this);
        }

        @Override
        public void visitLeaf(RAny value) {
            // TODO this is not terribly efficient, but deals with repeating inputs. If we ever build the AST for view,
            // it will go away
            for (Input i : inputs) {
                if (i.data == value)
                    i.isSameSizeAsResult = true;
            }
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
            visitDouble_(view.a);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
            visitDouble_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
            visitDouble_(view.a);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
            visitDouble_(view.a);
            visitInt_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
            visitInt_(view.a);
            visitDouble_(view.b);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
            visitInt_(view.a);
        }

        @Override
        public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
            visitInt_(view.a);
            visitInt_(view.b);
        }
    }

    // NoFusion --------------------------------------------------------------------------------------------------------

    /** Special prototype for signatures which failed the fused operatior generation so that they are not re-attempted.
     *
     * Just calls the materialize_() method on the view. Bypasses all view checking as it is not necessary because it
     * uses the view directly.
     */
    static class NoFusion extends Prototype {
        View view;

        @Override
        public void bind(View view) {
            this.view = view;
        }

        @Override
        public void free() {
            view = null;
        }

        @Override
        public RArray materialize_() {
            return view.materialize_();
        }
    }

    /** NoFusion singleton.
     */
    static final NoFusion NO_FUSION = new NoFusion();


}
