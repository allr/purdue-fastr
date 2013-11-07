package r.fusion;

import javassist.*;
import r.data.*;
import r.data.internal.View;
import r.nodes.ast.ASTNode;
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
        return instance.build_(view, hash);
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

        /** Input index used when binding the view inputs on the fields of the fused operator.
         */
        protected int inputIdx = 0;


        protected int resultSize;

        protected int[] resultDimensions;

        protected RArray.Names resultNames;

        protected RArray.Attributes resultAttributes;



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

        protected final void propagateAttributes(ASTNode ast, int leftSize, int[] leftDimensions, RArray.Names leftNames, RAny.Attributes leftAttributes) {
            resultDimensions = Arithmetic.resultDimensions(ast, leftDimensions, leftSize, resultDimensions, resultSize);
            resultNames = Arithmetic.resultNames(ast, leftNames, leftSize, resultNames, resultSize);
            resultAttributes = Arithmetic.resultAttributes(ast, leftAttributes, leftSize, resultAttributes, resultSize);
            resultSize = Arithmetic.resultSize(ast, leftSize, resultSize);
        }

        /** The visitor methods for understood operators.
         *
         * Each of such classes perform first the class checks to make sure the view signature is the same and then
         * visits its children so that the whole view is visited.
         */
        @Override
        public void visit(RDouble.RIntView view) {
            checkClass(view.getClass());
            visitDouble_(view.orig);
            // no need to propagate anything, the size and everything else stays
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitDouble_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitInt_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitInt_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitInt_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitDouble_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
        }

        @Override
        public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
            checkClass(view.getClass());
            checkClass(view.arit.getClass());
            visitInt_(view.a);
            int leftSize = resultSize;
            int[] leftDimensions = resultDimensions;
            RArray.Names leftNames = resultNames;
            RAny.Attributes leftAttributes = resultAttributes;
            visitInt_(view.b);
            propagateAttributes(view.ast(), leftSize, leftDimensions, leftNames, leftAttributes);
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
            view.visit(this);
            if (nodeClassesIndex != nodeClasses.length)
                throw new NotSupported();
        }

        /** Frees the fused operator from the previously bound inputs.
         *
         * This method should be overriden to set all inputs of the fused operator to null so that they can be garbage
         * collected if no-one holds their references.
         *
         * Overriden methods should always call the super method too.
         */
        public void free() {
            nodeClassesIndex = 0;
            inputIdx = 0;
            resultSize = 0;
            resultAttributes = null;
            resultDimensions = null;
            resultNames = null;
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
    /* Next free temporary variable for the computation.
     */
    int freeTemp = 0;

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

    /** Index of the input whose size the result has.
     *
     * This variable propagates through the tree linearly and must be captured by the branch points. At the end of the
     * execution it will hold the index of the input that determines the size of the materialized result.
     */
    int resultSize = 0;
    /** Variable in which the result is stored.
     *
     * This vaariable propagates through the tree linearly and must be captured by the branch points. At the end of the
     * execution it will hold the variable in which the result will be stored.
     */
    String resultVar;
    /** Type of the result.
     *
     * Determined by the view nodes and updates as the tree is visited. At the end, the type of the result will be
     * stored in the variable.
     */
    int resultType = 0;




    final ClassPool pool = ClassPool.getDefault();

    final CtClass prototype;

    final CtClass[] ctConstructorArgs;

    final Class[] constructorArgs;

    final CtClass[] ctConstructorExceptions = new CtClass[0];

    final Object[][] constructorArgsValues = new Object[1][];


    CtClass fop;

    int hash;

    View view;

    private FusedOperator() {
        CtClass p = null;
        CtClass cc = null;
        try {
            p = pool.get("r.fusion.FusedOperator$Prototype");
            cc = pool.get("java.lang.Class[]");
        } catch (NotFoundException e) {
            if (Fusion.DEBUG)
                e.printStackTrace();
            System.err.println("Initialization of Fusion framework failed, exitting...");
            System.exit(-1);
        }
        prototype = p;
        ctConstructorArgs = new CtClass[] { cc };
        constructorArgs = new Class[1];
        constructorArgs[0] = constructorArgs.getClass();
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
        sb.append("Computation code:\n");
        sb.append(code.toString());

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
            // add the methods
            addInputBindingMethods();
            addFreeMethod();
            addConstructor();
            addMaterializeMethod();
            // create the class and instantiate it
            Class fopClass = fop.toClass();
            constructorArgsValues[0] = new Class[nodeClasses.size()];
            constructorArgsValues[0] = nodeClasses.toArray(constructorArgsValues[0]);
            Prototype result = (Prototype) fopClass.getConstructor(constructorArgs).newInstance(constructorArgsValues);
            return result;
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

    private void addInputBindingMethods() throws CannotCompileException {
        StringBuilder sbd = new StringBuilder();
        StringBuilder sbi = new StringBuilder();
        sbd.append("public void visitDoubleLeaf(r.data.RDouble data) {\n");
        sbd.append("    resultSize = data.size();\n");
        sbd.append("    resultDimensions = data.dimensions();\n");
        sbd.append("    resultNames = data.names();\n");
        sbd.append("    resultAttributes = data.attributes();\n");
        sbd.append("    switch (inputIdx++) {\n");
        sbi.append("public void visitIntLeaf(r.data.RInt data) {\n");
        sbi.append("    resultSize = data.size();\n");
        sbi.append("    resultDimensions = data.dimensions();\n");
        sbi.append("    resultNames = data.names();\n");
        sbi.append("    resultAttributes = data.attributes();\n");
        sbi.append("    switch (inputIdx++) {\n");
        for (Input i : inputs) {
            switch (i.type) {
                case Fusion.DOUBLE:
                    sbd.append("        case " + i.index + ":\n");
                    sbd.append("            " + i.inputName() + " = data." + (i.isVector ? "getContent()" : "getDouble(0)") + ";\n");
                    sbd.append("            break;\n");
                    break;
                case Fusion.INT:
                    sbi.append("        case " + i.index + ":\n");
                    sbi.append("            " + i.inputName() + " = data." + (i.isVector ? "getContent()" : "getInt(0)") + ";\n");
                    sbi.append("            break;\n");
                    break;
                default:
                    assert (false);
            }
        }
        sbd.append("        default:\n");
        sbd.append("            throw new r.fusion.NotSupported();\n");
        sbd.append("    }\n");
        sbd.append("}\n");
        sbi.append("        default:\n");
        sbi.append("            throw new r.fusion.NotSupported();\n");
        sbi.append("    }\n");
        sbi.append("}\n");
//        System.out.println(sbd.toString());
        fop.addMethod(CtNewMethod.make(sbd.toString(), fop));
        fop.addMethod(CtNewMethod.make(sbi.toString(), fop));
    }

    private void addFreeMethod() throws CannotCompileException {
        StringBuilder sb = new StringBuilder();
        sb.append("public void free() {\n");
        sb.append("    super.free();\n");
        for (Input i : inputs)
            if (i.isVector)
                sb.append("    " + i.inputName() + " = null;\n");
        sb.append("}\n");
        fop.addMethod(CtNewMethod.make(sb.toString(), fop));
    }

    private void addConstructor() throws CannotCompileException {
        fop.addConstructor(CtNewConstructor.make(ctConstructorArgs, ctConstructorExceptions, "{ this.nodeClasses = $1; }", fop));
    }

    private void addMaterializeMethod() throws CannotCompileException {
        StringBuilder sb = new StringBuilder();
        sb.append("public r.data.RArray materialize_() {\n");
        // create the result array
        switch (resultType) {
            case Fusion.DOUBLE:
                sb.append("    double[] result = new double[input" + resultSize + ".length];\n");
                break;
            case Fusion.INT:
                sb.append("    int[] result = new int[input"+resultSize+".length];\n");
                break;
            default:
                assert (false);
        }
        // create indices for different size vectors inputs
        for (Input i : inputs)
            if (i.isVector && !i.isSameSizeAsResult)
                sb.append("    int i" + i.index + " = 0;\n");
        // main loop header
        sb.append("    for (int i = 0; i < result.length; ++i) {\n");
        // loading the input elements
        for (Input i : inputs)
            if (i.isVector)
                switch (i.type) {
                    case Fusion.DOUBLE:
                        sb.append("        double in" + i.index + " = input" + i.index + "[i" + (i.isSameSizeAsResult ? "" : i.index ) + "];\n");
                        break;
                    case Fusion.INT:
                        sb.append("        int in" + i.index + " = input" + i.index + "[i" + (i.isSameSizeAsResult ? "" : i.index ) + "];\n");
                        break;
                    default:
                        assert (false);
                }
        // initialize the boolean for NA checks
        sb.append("        boolean isNA = false;\n");
        // add the code of the computation
        sb.append(code.toString());
        // NA check
        sb.append("        if (isNA)\n");
        switch (resultType) {
            case Fusion.DOUBLE:
                sb.append("            " + resultVar + " = r.data.RDouble.NA;\n");
                break;
            case Fusion.INT:
                sb.append("            " + resultVar + " = r.data.RInt.NA;\n");
                break;
            default:
                assert (false);
        }
        // store the computed value
        sb.append("        result[i] = " + resultVar + ";\n");
        // increment the non-same size input indices
        for (Input i : inputs)
            if (i.isVector && !i.isSameSizeAsResult) {
                sb.append("        if (++i" + i.index + " == input" + i.index + ".length)\n");
                sb.append("            i" + i.index + " = 0;\n");
            }
        // end of the loop and create the result
        sb.append("    }\n");
        switch (resultType) {
            case Fusion.DOUBLE:
                sb.append("    return r.data.RDouble.RDoubleFactory.getFor(result, resultDimensions, resultNames, resultAttributes);\n");
                break;
            case Fusion.INT:
                sb.append("    return r.data.RInt.RDoubleFactory.getFor(result, resultDimensions, resultNames, resultAttributes);\n");
                break;
            default:
                assert (false);
        }
        // end of method
        sb.append("}\n");
        // create and add the method
        // System.out.println(sb.toString());
        fop.addMethod(CtNewMethod.make(sb.toString(), fop));
    }


    /** Adds given class to the list of classes that will be checked at runtime when binding the fused operator.
     *
     * Note that this must be the same order the classes will later be checked.
     */
    private void checkClass(Class clazz) {
        nodeClasses.add(clazz);
    }

    /** Returns the next free temporary variable name.
     */
    private String freeTemp() {
        String result = "t"+freeTemp;
        ++freeTemp;
        return result;
    }

    // code emit methods -----------------------------------------------------------------------------------------------

    private void binaryDDtoD(String left, String right, Arithmetic.ValueArithmetic arit) {
        resultVar = freeTemp();
        resultType = Fusion.DOUBLE;
        code.append("        double "+resultVar+";\n");
        if (arit == Arithmetic.ADD)
            code.append("        " + resultVar + " = " + left + " + " + right + ";\n");
        else if (arit == Arithmetic.SUB)
            code.append("        " + resultVar + " = " + left + " - " + right + ";\n");
        else if (arit == Arithmetic.MULT)
            code.append("        " + resultVar + " = " + left + " * " + right + ";\n");
        else if (arit == Arithmetic.DIV)
            code.append("        " + resultVar + " = " + left + " / " + right + ";\n");
        else if (arit == Arithmetic.MOD)
            code.append("        " + resultVar + " = " + left + " % " + right + ";\n");
        else
            throw new NotSupported();
    }

    private void binaryDItoD(String left, String right, Arithmetic.ValueArithmetic arit) {
        code.append("        isNA |= (" + right + " == r.data.RInt.NA);\n");
        binaryDDtoD(left, right, arit);
    }

    private void binaryIDtoD(String left, String right, Arithmetic.ValueArithmetic arit) {
        resultVar = freeTemp();
        resultType = Fusion.DOUBLE;
        code.append("        isNA |= (" + left + " == r.data.RInt.NA);\n");
        binaryDDtoD(left, right, arit);
    }

    private void binaryIItoI(String left, String right, Arithmetic.ValueArithmetic arit) {
        resultVar = freeTemp();
        resultType = Fusion.INT;
        code.append("        isNA |= (" + left + " == r.data.RInt.NA);\n");
        code.append("        isNA |= (" + right + " == r.data.RInt.NA);\n");
        code.append("        int "+resultVar+";\n");
        //  i/i -> i is meaningless
        if (arit == Arithmetic.ADD)
            code.append("        " + resultVar + " = r.nodes.exec.Arithmetic$Add.add(" + left + ", " + right + ");\n");
        else if (arit == Arithmetic.SUB)
            code.append("        " + resultVar + " = r.nodes.exec.Arithmetic$Sub.sub(" + left + ", " + right + ");\n");
        else if (arit == Arithmetic.MULT)
            code.append("        " + resultVar + " = r.nodes.exec.Arithmetic$Mult.mult(" + left + ", " + right + ");\n");
        else if (arit == Arithmetic.MOD)
            code.append("        " + resultVar + " = r.nodes.exec.Arithmetic$Mod.mod(" + left + ", " + right + ");\n");
        else
            throw new NotSupported();
    }

    private void conversionToInt(String source, int type) {
        resultVar = freeTemp();
        resultType = Fusion.INT;
        code.append("        int "+resultVar+";\n");
        switch (type) {
            case Fusion.DOUBLE:
                code.append("        " + resultVar + "Convert.double2Int(" + source + ");\n");
                break;
            default:
                throw new NotSupported();
        }
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
    public void visitDoubleLeaf(RDouble data) {
        resultSize = inputs.size(); // set the result size to the input, will be adjusted by operators
        Input i = new Input(data, inputs.size(), Fusion.DOUBLE, data.size() != 1);
        inputs.add(i);
        inputIndices.put(data, i);
        resultVar = i.inputElementName();
        resultType = Fusion.DOUBLE;
    }

    @Override
    public void visitIntLeaf(RInt data) {
        resultSize = inputs.size(); // set the result size to the input, will be adjusted by operators
        Input i = new Input(data, inputs.size(), Fusion.INT, data.size() != 1);
        inputs.add(i);
        inputIndices.put(data, i);
        resultVar = i.inputElementName();
        resultType = Fusion.INT;
    }

    @Override
    public void visitComplexLeaf(RComplex data) {
        // TODO support complex numbers
        throw new NotSupported();
    }

    @Override
    public void visit(RDouble.RIntView view) {
        checkClass(view.getClass());
        visitDouble_(view.orig);
        conversionToInt(resultVar, resultType);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericASized view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        int rs = resultSize; // backup result size because it will be overriden by b
        String left = resultVar;
        visitDouble_(view.b);
        binaryDDtoD(left, resultVar, view.arit);
        resultSize = rs;
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.GenericBSized view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        String left = resultVar;
        visitDouble_(view.b); // result size will stay that of b
        binaryDDtoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.EqualSizeVectorVector view) {
        String t = freeTemp();
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        String left = resultVar;
        visitDouble_(view.b); // both sizes are the same, we are happy with the second
        binaryDDtoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.VectorScalar view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        int rs = resultSize; // backup the result size of the vector
        String left = resultVar;
        visitDouble_(view.b);
        resultSize = rs;
        binaryDDtoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleDouble.ScalarVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        String left = resultVar;
        visitDouble_(view.b); // the result size of the vector will be returned
        binaryDDtoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForDoubleInt.EqualSizeVectorVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitDouble_(view.a);
        String left = resultVar;
        visitInt_(view.b); // equally sized operators, the second is ok
        binaryDItoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForIntDouble.EqualSizeVectorVector view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitInt_(view.a);
        String left = resultVar;
        visitDouble_(view.b); // equally sized operators, the second is ok
        binaryIDtoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.DoubleViewForIntDouble.VectorScalar view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitInt_(view.a);
        int rs = resultSize; // backup the result size from the vector operand
        String left = resultVar;
        visitDouble_(view.b);
        resultSize = rs;
        binaryIDtoD(left, resultVar, view.arit);
    }

    @Override
    public void visit(Arithmetic.IntViewForIntInt.EqualSize view) {
        checkClass(view.getClass());
        checkClass(view.arit.getClass());
        visitInt_(view.a);
        String left = resultVar;
        visitInt_(view.b); // equal sizes, both are good
        binaryIItoI(left, resultVar, view.arit);
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
        public void visit(RDouble.RIntView view) {
            visitDouble_(view.orig);
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
            super.free();
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
