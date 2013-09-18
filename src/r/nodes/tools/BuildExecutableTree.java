package r.nodes.tools;

import r.Utils;
import r.builtins.Primitives;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.exec.*;
import r.nodes.ast.*;
import r.nodes.ast.Constant;
import r.nodes.ast.Function;
import r.nodes.ast.FunctionCall;
import r.nodes.ast.If;
import r.nodes.ast.Not;
import r.nodes.ast.Sequence;
import r.nodes.ast.UnaryMinus;
import r.nodes.ast.UpdateVector;
import r.runtime.*;

public class BuildExecutableTree implements Visitor {

    RFunction rootEnclosingFunction;
    RNode result;
    public static final boolean DEBUG_SPECIAL_NODES = false;

    public RNode createLazyRootTree(final ASTNode ast) {
        return new BaseR(ast) {

            @Child RNode node = adoptChild(createLazyTree(ast));

            @Override public final Object execute(Frame frame) {
                try {
                    return node.execute(frame);
                } catch (r.nodes.exec.Loop.ContinueException ce) {
                    throw RError.getNoLoopForBreakNext(ast);
                } catch (r.nodes.exec.Loop.BreakException be) {
                    throw RError.getNoLoopForBreakNext(ast);
                }
            }

            @Override
            protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
                assert oldNode != null;
                if (node == oldNode) {
                    node = newNode;
                    return adoptInternal(newNode);
                }
                return super.replaceChild(oldNode, newNode);
            }

        };
    }

    public RNode createTree(ASTNode ast) {
        ast.accept(this);
        return result;
    }

    public RNode createTree(ASTNode ast, RFunction enclosingFunction) {
        try {
            this.rootEnclosingFunction = enclosingFunction;
            ast.accept(this);
            return result;
        } finally {
            this.rootEnclosingFunction = null;
        }
    }

    @SuppressWarnings("static-method") private RNode createLazyTree(ASTNode ast) {
        return new LazyBuild(ast);
    }

    @Override public void visit(If ast) {
        // FIXME: lazy build breaks Truffle inliner

        ASTNode falseBranch = ast.getFalseCase();
        ASTNode cond = ast.getCond();
        RNode rcond = createLazyTree(cond);
        RNode rtrueBranch = createLazyTree(ast.getTrueCase());
        RNode rfalseBranch = (falseBranch == null) ? r.nodes.exec.Constant.getNull() : createLazyTree(falseBranch);

        if (cond instanceof EQ) {
            EQ e = (EQ) cond;
            ASTNode rhs = e.getRHS();
            ASTNode lhs = e.getLHS();
            if (rhs instanceof Constant) {
                result = new r.nodes.exec.If.IfConst(ast, rcond, createTree(lhs), rtrueBranch, rfalseBranch, ((Constant) rhs).getValue());
                return;
            }
            if (lhs instanceof Constant) {
                result = new r.nodes.exec.If.IfConst(ast, rcond, createTree(rhs), rtrueBranch, rfalseBranch, ((Constant) lhs).getValue());
                return;
            }
        }

        if (falseBranch == null) {
            result = new r.nodes.exec.If.IfNoElse(ast, rcond, rtrueBranch);
        } else {
            result = new r.nodes.exec.If.IfElse(ast, rcond, rtrueBranch, rfalseBranch);
        }
    }

    @Override public void visit(Repeat repeat) {
        result = new r.nodes.exec.Loop.Repeat(repeat, createLazyTree(repeat.getBody()));
    }

    @Override public void visit(While n) {
        ASTNode cond = n.getCond();
        if (cond instanceof Constant) {
            RAny value = ((Constant) cond).getValue();
            int l = value.asLogical().getLogical(0);
            if (l == RLogical.TRUE) {
                result = new r.nodes.exec.Loop.Repeat(n, createLazyTree(n.getBody()));
                return;
            }
        }
        result = new r.nodes.exec.Loop.While(n, createTree(cond), createLazyTree(n.getBody()));
    }

    @Override public void visit(For n) {
        // an experiment: this actually didn't give any speedups, but it doesn't make any sense...
        //        ASTNode body = n.getBody();
        //
        //        ASTNode sbody = skipTrivialSequences(body);
        //        if (sbody instanceof For) {
        //            // inner loops
        //            For innerFor = (For) sbody;
        //            FrameSlot cvarSlot = getFrameSlot(n, n.getCVar());
        //            FrameSlot innerCvarSlot = getFrameSlot(innerFor, innerFor.getCVar());
        //            if (cvarSlot != null && innerCvarSlot != null) {
        //                result = new r.nodes.truffle.Loop.For.NestedLocalIntSequenceRange(n, cvarSlot, createTree(n.getRange()), innerCvarSlot, createTree(innerFor.getRange()), createLazyTree(innerFor.getBody()));
        //                return;
        //            }
        //        }
        result = new r.nodes.exec.Loop.For.IntSequenceRange(n, n.getCVar(), createTree(n.getRange()), createLazyTree(n.getBody()));
    }

    @Override public void visit(Break n) {
        result = new r.nodes.exec.Loop.Break(n);
    }

    @Override public void visit(Next n) {
        result = new r.nodes.exec.Loop.Next(n);
    }

    @Override public void visit(Sequence sequence) {
        ASTNode[] exprs = sequence.getExprs();
        RNode[] rexprs = new RNode[exprs.length];
        for (int i = 0; i < exprs.length; i++) {
            rexprs[i] = createTree(exprs[i]);
        }
        switch (exprs.length) {
        case 1:
            result = rexprs[0];
            break;
        case 2:
            result = new r.nodes.exec.Sequence.Sequence2(sequence, rexprs);
            break;
        case 3:
            result = new r.nodes.exec.Sequence.Sequence3(sequence, rexprs);
            break;
        case 4:
            result = new r.nodes.exec.Sequence.Sequence4(sequence, rexprs);
            break;
        case 5:
            result = new r.nodes.exec.Sequence.Sequence5(sequence, rexprs);
            break;
        case 6:
            result = new r.nodes.exec.Sequence.Sequence6(sequence, rexprs);
            break;
        default:
            result = new r.nodes.exec.Sequence(sequence, rexprs);
            break;
        }
    }

    @Override public void visit(Not n) {
        result = new r.nodes.exec.Not.LogicalScalar(n, createTree(n.getLHS()));
    }

    @Override public void visit(UnaryMinus m) {
        result = new r.nodes.exec.UnaryMinus.NumericScalar(m, createTree(m.getLHS()));
    }

    @Override public void visit(Constant constant) {
        result = new r.nodes.exec.Constant(constant, constant.getValue());
    }

    @Override public void visit(SimpleAccessVariable readVariable) {
        RSymbol symbol = readVariable.getSymbol();
        result = r.nodes.exec.ReadVariable.getUninitialized(readVariable, symbol);
    }

    /**
     * FieldAccess closely resembles the subset operator, but uses the ReadList class which closely matches the
     * functionality of String-typed selector from vectors.
     */
    @Override public void visit(FieldAccess fa) {
        ASTNode lhs = fa.lhs();
        RNode n = createTree(lhs);
        result = new ReadVector.FieldSelection.UninitializedSelection(fa, n, RSymbol.getSymbol(fa.fieldName()));
    }

    @Override public void visit(SimpleAssignVariable assign) {
        RSymbol symbol = assign.getSymbol();
        ASTNode valueNode = assign.getExpr();

        //        if (false && r.builtins.Primitives.get(symbol) != null) {
        //            // FIXME: pidigits uses a variable "c"
        //            Utils.nyi(symbol.pretty() + ": we don't support variables over-shadowing primitives.");
        //            // NOTE: we could support this as long as the value assigned isn't a function, but checking that would be expensive
        //            // it may become cheaper once/if we type-specialize assignment nodes, at some point when we do boxing optimizations
        //        }

        // optimize expressions like x <- x + 1
        // more precisely x <- binaryOp(x,Constant) or y <- binaryOp(Constant, y)

        r.nodes.exec.Arithmetic.ValueArithmetic arit = getValueArithmetic(valueNode);

        if (arit != null) {
            BinaryOperation bin = (BinaryOperation) valueNode;
            ASTNode binLHS = bin.getLHS();
            ASTNode binRHS = bin.getRHS();
            SimpleAccessVariable binVar = null;
            Constant constNode = null;

            if (binLHS instanceof SimpleAccessVariable) {
                binVar = (SimpleAccessVariable) binLHS;
            } else if (binRHS instanceof SimpleAccessVariable) {
                binVar = (SimpleAccessVariable) binRHS;
            }
            if (binLHS instanceof Constant) {
                constNode = (Constant) binLHS;
            } else if (binRHS instanceof Constant) {
                constNode = (Constant) binRHS;
            }
            if (binVar != null && constNode != null) {
                RSymbol binVarSymbol = binVar.getSymbol();
                int slot = getFrameSlot(assign, binVarSymbol);
                if (binVarSymbol == symbol && !assign.isSuper() && (constNode.getValue() instanceof ScalarIntImpl) && slot != -1) {
                    int cValue = ((ScalarIntImpl) constNode.getValue()).getInt();
                    if (cValue == 1) {
                        if (valueNode instanceof Add) {
                            // integer increment
                            if (DEBUG_SPECIAL_NODES) {
                                Utils.debug("increment node at " + PrettyPrinter.prettyPrint(assign));
                            }
                            result = new ArithmeticUpdateVariable.ScalarIntLocalIncrement(assign, slot);
                            return;
                        }
                        if (binLHS instanceof SimpleAccessVariable && valueNode instanceof Sub) {
                            // integer decrement
                            if (DEBUG_SPECIAL_NODES) {
                                Utils.debug("decrement node at " + PrettyPrinter.prettyPrint(assign));
                            }
                            result = new ArithmeticUpdateVariable.ScalarIntLocalDecrement(assign, slot);
                            return;
                        }
                    }
                }
            }
        }

        if (assign.isSuper()) {
            result = r.nodes.exec.SuperWriteVariable.getUninitialized(assign, symbol, createTree(valueNode));
        } else {
            // Truffle does not like Lazy...
            //result = r.nodes.truffle.WriteVariable.getUninitialized(assign, assign.getSymbol(), createLazyTree(assign.getExpr()));
            result = r.nodes.exec.WriteVariable.getUninitialized(assign, symbol, createTree(valueNode));
        }

    }

    private static class SplitArgumentList {
        RSymbol[] convertedNames;
        RNode[] convertedExpressions;

        SplitArgumentList(RSymbol[] convertedNames, RNode[] convertedExpressions) {
            this.convertedNames = convertedNames;
            this.convertedExpressions = convertedExpressions;
        }
    }

    private SplitArgumentList splitArgumentList(ArgumentList alist, boolean root) {
        int args = alist.size();
        RSymbol[] names = new RSymbol[args];
        RNode[] expressions = new RNode[args];
        int i = 0;
        for (ArgumentList.Entry e : alist) {
            names[i] = e.getName();
            ASTNode exp = e.getValue();
            if (exp != null) {
                if (root) {
                    if (exp instanceof Constant) { // hack to make builtins see their constant arguments, constant won't rewrite anyway
                        expressions[i] = createTree(exp);
                    } else {
                        expressions[i] = createLazyRootTree(exp);
                    }
                } else {
                    expressions[i] = createTree(exp);
                }
            }
            i++;
        }
        assert Utils.check(i == args);
        return new SplitArgumentList(names, expressions);
    }

    private void detectRepeatedParameters(RSymbol[] params, ASTNode ast) {
        int n = params.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (params[i] == params[j]) { throw RError.getRepeatedFormal(ast, params[i].name()); }
            }
        }
    }

    @Override public void visit(Function function) {
        assert Utils.check(function.getRFunction() == null); // TODO the ast.Function must create the RFunction !

        RFunction encf = getEnclosingFunction(function);

        SplitArgumentList a = splitArgumentList(function.getSignature(), true); // the name is not really accurate since, these are parameters

        // note: body has to be built lazily, otherwise nested functions won't work correctly
        detectRepeatedParameters(a.convertedNames, function);
        RFunction impl = function.createImpl(a.convertedNames, a.convertedExpressions, createLazyRootTree(function.getBody()), encf);
        r.nodes.exec.Function functionNode = new r.nodes.exec.Function(impl);

        result = functionNode;
    }

    private RFunction getEnclosingFunction(ASTNode node) {
        // find lexically enclosing function if exists
        Function enfunc = findParent(node, Function.class);
        if (enfunc == null) { return rootEnclosingFunction; }
        RFunction rfunc = enfunc.getRFunction();
        Utils.check(rfunc != null, "RFunction is not yet ready - note lazy build is necessary for functions");
        return rfunc;
    }

    private int getFrameSlot(ASTNode ast, RSymbol symbol) {
        RFunction encFunction = getEnclosingFunction(ast);
        if (encFunction != null) { return encFunction.localSlot(symbol); }
        return -1;
    }

    private boolean hasLocalOrEnclosingFrameSlot(ASTNode ast, RSymbol symbol) {
        RFunction encFunction = getEnclosingFunction(ast);
        if (encFunction == null) { return false; }
        int slot = encFunction.localSlot(symbol);
        if (slot != -1) { return true; }
        return encFunction.enclosingSlot(symbol) != null;
    }

    public static ASTNode skipTrivialSequences(ASTNode astArg) {
        ASTNode ast = astArg;
        for (;;) {
            if (ast == null || !(ast instanceof Sequence)) { return ast; }
            Sequence s = (Sequence) ast;
            ASTNode[] exprs = s.getExprs();
            if (exprs.length != 1) { return ast; }
            ast = exprs[0];
        }
    }

    @Override public void visit(FunctionCall functionCall) {
        // FIXME: In R, function call needs not have a symbol, it can be a lambda expression

        // TODO: FunctionCall for now are ONLY for variable (see Call.create ...).
        // It's maybe smarter to move this instance of here and replace the type of name by expression
        SplitArgumentList a = splitArgumentList(functionCall.getArgs(), r.nodes.exec.FunctionCall.PROMISES);
        // NOTE: the "false" argument, which currently ensures that the arguments are not lazy, which in turn
        // makes it easy for hotspot to optimize the code

        RSymbol sym = functionCall.getName();
        RNode rCall = null;
        if (Primitives.STATIC_LOOKUP) {
            r.builtins.CallFactory factory = r.builtins.Primitives.getCallFactory(sym, getEnclosingFunction(functionCall));
            if (factory == null) {
                factory = r.nodes.exec.FunctionCall.FACTORY;
            }
            rCall = factory.create(functionCall, a.convertedNames, a.convertedExpressions);
        } else {
            if (!hasLocalOrEnclosingFrameSlot(functionCall, sym)) {
                rCall = r.nodes.exec.FunctionCall.createBuiltinCall(functionCall, a.convertedNames, a.convertedExpressions);
            }
            if (rCall == null) {
                rCall = r.nodes.exec.FunctionCall.FACTORY.create(functionCall, a.convertedNames, a.convertedExpressions);
            }
        }

        if (!functionCall.isAssignment()) {
            result = rCall;
            return;
        }

        // replacement assignment
        RNode valueExpr = a.convertedExpressions[a.convertedExpressions.length - 1];
        ReplacementCall.RememberLast remValueExpr = new ReplacementCall.RememberLast(valueExpr.getAST(), valueExpr);
        valueExpr.replace(remValueExpr);
        a.convertedExpressions[a.convertedExpressions.length - 1] = remValueExpr;

        SimpleAccessVariable xAST = (SimpleAccessVariable) a.convertedExpressions[0].getAST();

        result = new ReplacementCall(functionCall, functionCall.isSuper(), xAST.getSymbol(), rCall, remValueExpr);
    }

    public static boolean isArrayColumnSubset(boolean subset, RNode[] selectors, int dims) {
        if (!subset) { return false; }
        for (int i = 0; i < dims - 1; i++) {
            if (selectors[i] != null) { return false; }
        }
        if (selectors[dims - 1] == null) { return false; }
        return true;
    }

    // TODO This has to be changed for partial matching so that exact is checked also for vectors
    @Override public void visit(AccessVector a) {
        SplitArgumentList sa = splitArgumentList(a.getArgs(), false);

        if (sa.convertedExpressions.length == 1) { // vector

            // TODO: should support "exact" and "drop"
            if (a.isSubset()) {
                // expressions like b[x == c]
                // FIXME: add more variations of this
                ASTNode indexNode = a.getArgs().first().getValue();
                if (indexNode instanceof EQ) {
                    EQ eqNode = (EQ) indexNode;
                    if (eqNode.getRHS() instanceof Constant) {
                        RAny cv = ((Constant) eqNode.getRHS()).getValue();
                        if (cv instanceof RDouble && ((RDouble) cv).size() == 1) {
                            double c = ((RDouble) cv).getDouble(0);
                            if (RDouble.RDoubleUtils.isFinite(c)) {
                                result = new ReadVector.LogicalEqualitySelection(a, createTree(a.getVector()), createTree(eqNode.getLHS()), c);
                                return;
                            }
                        }
                    }
                }
            }

            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
                result = new ReadVector.SimpleIntSequenceSelection(a, createTree(a.getVector()), sa.convertedExpressions, a.isSubset());
            } else {
                RNode e = sa.convertedExpressions[0];
                if (e instanceof r.nodes.exec.Constant) {
                    RAny v = (RAny) e.execute(null);
                    if (v instanceof RDouble || v instanceof RInt) {
                        RInt iv = v.asInt();
                        if (iv.size() == 1) {
                            int index = v.asInt().getInt(0);
                            if (index > 0) {
                                result = new ReadVector.SimpleConstantScalarIntSelection(a, createTree(a.getVector()), sa.convertedExpressions, index, a.isSubset());
                                return;
                            }
                        }
                    }
                }
                result = new ReadVector.DoubleBaseSimpleSelection.ScalarIntSelection(a, createTree(a.getVector()), sa.convertedExpressions, a.isSubset());
            }
            return;
        }
        // array & matrix read
        if (sa.convertedExpressions.length >= 2) { // array or matrix ?

            RNode drop = null;
            RNode exact = null;
            RNode[] selectors = new RNode[sa.convertedExpressions.length];
            int[] nodeIndexes = new int[selectors.length];

            RNode[] nodes = sa.convertedExpressions;
            RSymbol[] names = sa.convertedNames;
            int dims = 0;

            for (int i = 0; i < nodes.length; i++) {
                if (names[i] == RSymbol.DROP_SYMBOL) {
                    assert Utils.check(drop == null); // GNU-R allows multiple occurrences
                    drop = nodes[i];
                } else if (names[i] == RSymbol.EXACT_SYMBOL) {
                    assert Utils.check(exact == null); // GNU-R allows multiple occurrences
                    exact = nodes[i];
                } else {
                    selectors[dims] = nodes[i];
                    nodeIndexes[dims] = i;
                    ++dims;
                }
            }

            assert Utils.check(dims != 0); // FIXME: GNU-R supports this

            if (dims == 2) { // if matrix read, use the specialized matrix form
                if (a.isSubset() && selectors[0] == null && selectors[1] != null) { // matrix column
                    result = new ReadArray.MatrixColumnSubset(a, createTree(a.getVector()), selectors[1], Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(a, exact));
                    return;

                }
                if (a.isSubset() && selectors[0] != null && selectors[1] == null) {
                    result = new ReadArray.MatrixRowSubset(a, createTree(a.getVector()), selectors[0], Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(a, exact));
                    return;
                }

                // special handling of m[a:b, c:d]
                ASTNode node0 = a.getArgs().getNode(nodeIndexes[0]);
                ASTNode node1 = a.getArgs().getNode(nodeIndexes[1]);

                if (a.isSubset() && node0 != null && node1 != null && node0 instanceof Colon && node1 instanceof Colon) {
                    Colon rows = (Colon) node0;
                    Colon cols = (Colon) node1;
                    result = new ReadArray.MatrixSequenceSubset(a, createTree(a.getVector()), createTree(rows.getLHS()), createTree(rows.getRHS()), createTree(cols.getLHS()),
                            createTree(cols.getRHS()), Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(a, exact));
                    return;

                }

                Selector.SelectorNode selectorIExpr = Selector.createSelectorNode(a, a.isSubset(), selectors[0]);
                Selector.SelectorNode selectorJExpr = Selector.createSelectorNode(a, a.isSubset(), selectors[1]);

                if (!a.isSubset()) {
                    result = new ReadArray.MatrixSubscript(a, createTree(a.getVector()), selectorIExpr, selectorJExpr, Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(a, exact));
                } else {
                    result = new ReadArray.MatrixRead(a, a.isSubset(), createTree(a.getVector()), selectorIExpr, selectorJExpr, Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(
                            a, exact));
                }
                return;
            }
            // otherwise use array read
            if (isArrayColumnSubset(a.isSubset(), selectors, dims)) {
                result = new ReadArray.ArrayColumnSubset(a, createTree(a.getVector()), dims, selectors[dims - 1], Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(a, exact));
                return;
            }

            Selector.SelectorNode[] selNodes = new Selector.SelectorNode[dims];
            for (int i = 0; i < selNodes.length; ++i) {
                selNodes[i] = Selector.createSelectorNode(a, a.isSubset(), selectors[i]);
            }

            result = new ReadArray.GenericRead(a, a.isSubset(), createTree(a.getVector()), selNodes, Selector.createDropOptionNode(a, drop), Selector.createExactOptionNode(a, exact));
            return;
        }
        Utils.nyi("unsupported indexing style");
    }

    @Override public void visit(UpdateVector u) {
        AccessVector a = u.getVector();
        SplitArgumentList sa = splitArgumentList(a.getArgs(), false);

        if (sa.convertedExpressions.length == 1) {
            ASTNode varAccess = a.getVector();
            if (!(varAccess instanceof SimpleAccessVariable)) {
                Utils.nyi("expecting vector name for vector update");
            }
            RSymbol var = ((SimpleAccessVariable) varAccess).getSymbol();

            if (a.isSubset()) { //FIXME: this optimization is helping only so little.. why?
                // expressions like b[x == c] <- ...
                // FIXME: add more variations of this
                ASTNode indexNode = a.getArgs().first().getValue();
                if (indexNode instanceof EQ) {
                    EQ eqNode = (EQ) indexNode;
                    if (eqNode.getRHS() instanceof Constant) {
                        RAny cv = ((Constant) eqNode.getRHS()).getValue();
                        if (cv instanceof RDouble && ((RDouble) cv).size() == 1) {
                            double c = ((RDouble) cv).getDouble(0);
                            if (RDouble.RDoubleUtils.isFinite(c)) {
                                result = new r.nodes.exec.UpdateVector.LogicalEqualitySelection(u, u.isSuper(), var, createTree(varAccess), createTree(eqNode.getLHS()), c, createTree(u.getRHS()),
                                        a.isSubset());

                                return;
                            }
                        }
                    }
                }
            }

            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
                result = new r.nodes.exec.UpdateVector.IntSequenceSelection(u, u.isSuper(), var, createTree(varAccess), sa.convertedExpressions, createTree(u.getRHS()), a.isSubset());
            } else {
                result = new r.nodes.exec.UpdateVector.DoubleBaseSimpleSelection.ScalarIntSelection(u, u.isSuper(), var, createTree(varAccess), sa.convertedExpressions, createTree(u.getRHS()),
                        a.isSubset());
            }
        } else if (sa.convertedExpressions.length >= 2) {

            // TODO is drop meaningful for updates??
            // RNode drop = null;
            RNode exact = null;
            RNode[] selectors = new RNode[sa.convertedExpressions.length];

            RNode[] nodes = sa.convertedExpressions;
            RSymbol[] names = sa.convertedNames;
            int dims = 0;

            for (int i = 0; i < nodes.length; i++) {
                /*
                 * if (names[i] == RSymbol.dropName) { if (drop != null) { throw RError.getIncorrectSubscripts(a); }
                 * drop = nodes[i]; continue; } else
                 */if (names[i] == RSymbol.EXACT_SYMBOL) {
                    if (exact != null) { throw RError.getIncorrectSubscripts(a); }
                    exact = nodes[i];
                    continue;
                } else {
                    selectors[dims] = nodes[i];
                    ++dims;
                    continue;
                }
            }
            if (dims == 0) {
                Utils.nyi("unsupported indexing style");
            }
            Selector.SelectorNode[] selNodes = new Selector.SelectorNode[dims];
            for (int i = 0; i < selNodes.length; ++i) {
                selNodes[i] = Selector.createSelectorNode(a, a.isSubset(), selectors[i]);
            }
            // Create the assignment, or super assignment nodes
            boolean isColumn = isArrayColumnSubset(a.isSubset(), selectors, dims);

            ASTNode varAccess = a.getVector();
            RSymbol varName = ((SimpleAccessVariable) varAccess).getSymbol();
            if (!(varAccess instanceof SimpleAccessVariable)) {
                Utils.nyi("expecting matrix name for matrix update");
            }
            RFunction encFunction = getEnclosingFunction(a);
            int varSlot = getFrameSlot(a, varName);

            if (u.isSuper()) {
                result = UpdateArraySuperAssignment.create(a, varName, createTree(varAccess), createTree(u.getRHS()), UpdateArray.create(a, selNodes, a.isSubset(), isColumn));
            } else {
                result = UpdateArrayAssignment.create(a, varName, encFunction, varSlot, createTree(u.getRHS()), UpdateArray.create(a, selNodes, a.isSubset(), isColumn));
                return;
            }
        } else {
            Utils.nyi("Unsupported indexing style");
        }
    }

    /**
     * Converts the field update ($ selector on topmost lhf of assignment a$x = xyz).
     */
    @Override public void visit(UpdateField u) {
        /// FIXME this uses the old variant of $ selector that works also on vectors. Should be replaced.
        FieldAccess fa = u.getVector();
        ASTNode varAccess = fa.lhs();
        if (!(varAccess instanceof SimpleAccessVariable)) {
            Utils.nyi("expecting vector name for vector update"); // TODO: support expressions like x$a$b <- 10
        }
        RSymbol var = ((SimpleAccessVariable) varAccess).getSymbol();
        result = new r.nodes.exec.UpdateVector.DollarListUpdate(u, u.isSuper(), var, createTree(varAccess), RSymbol.getSymbol(fa.fieldName()), createTree(u.getRHS()));
    }

    @Override public void visit(UpdateExpression u) {
        Utils.nyi("generic update expressions not implemented");
    }

    @SuppressWarnings("unchecked") private static <T extends ASTNode> T findParent(ASTNode node, Class<T> clazz) {
        ASTNode n = node.getParent();
        while (n != null) {
            if (clazz.isInstance(n)) { return (T) n; }
            n = n.getParent();
        }
        return null;
    }

    @Override public void visit(EQ eq) {
        result = new r.nodes.exec.Comparison(eq, createTree(eq.getLHS()), createTree(eq.getRHS()), r.nodes.exec.Comparison.getEQ());
    }

    @Override public void visit(NE ne) {
        result = new r.nodes.exec.Comparison(ne, createTree(ne.getLHS()), createTree(ne.getRHS()), r.nodes.exec.Comparison.getNE());
    }

    @Override public void visit(LE le) {
        result = new r.nodes.exec.Comparison(le, createTree(le.getLHS()), createTree(le.getRHS()), r.nodes.exec.Comparison.getLE());
    }

    @Override public void visit(GE ge) {
        result = new r.nodes.exec.Comparison(ge, createTree(ge.getLHS()), createTree(ge.getRHS()), r.nodes.exec.Comparison.getGE());
    }

    @Override public void visit(LT lt) {
        result = new r.nodes.exec.Comparison(lt, createTree(lt.getLHS()), createTree(lt.getRHS()), r.nodes.exec.Comparison.getLT());
    }

    @Override public void visit(GT gt) {
        result = new r.nodes.exec.Comparison(gt, createTree(gt.getLHS()), createTree(gt.getRHS()), r.nodes.exec.Comparison.getGT());
    }

    public static r.nodes.exec.Arithmetic.ValueArithmetic getValueArithmetic(ASTNode ast) {
        if (ast instanceof Add) { return r.nodes.exec.Arithmetic.ADD; }
        if (ast instanceof Mult) { return r.nodes.exec.Arithmetic.MULT; }
        if (ast instanceof IntegerDiv) { return r.nodes.exec.Arithmetic.INTEGER_DIV; }
        if (ast instanceof Mod) { return r.nodes.exec.Arithmetic.MOD; }
        if (ast instanceof Pow) { return r.nodes.exec.Arithmetic.POW; }
        if (ast instanceof Div) { return r.nodes.exec.Arithmetic.DIV; }
        if (ast instanceof Sub) { return r.nodes.exec.Arithmetic.SUB; }
        return null;
    }

    private void visitArithmetic(BinaryOperation op) {
        r.nodes.exec.Arithmetic.ValueArithmetic arit = getValueArithmetic(op);
        assert Utils.check(arit != null);
        result = new r.nodes.exec.Arithmetic(op, createTree(op.getLHS()), createTree(op.getRHS()), arit);
    }

    @Override public void visit(Add add) {
        visitArithmetic(add);
    }

    @Override public void visit(Mult mult) {
        visitArithmetic(mult);
    }

    @Override public void visit(MatMult mult) {
        result = new r.nodes.exec.MatrixOperation.MatrixProduct(mult, createTree(mult.getLHS()), createTree(mult.getRHS()));
    }

    @Override public void visit(OuterMult mult) {
        result = new r.nodes.exec.MatrixOperation.OuterProduct(mult, createTree(mult.getLHS()), createTree(mult.getRHS()));
    }

    @Override public void visit(IntegerDiv div) {
        visitArithmetic(div);
    }

    @Override public void visit(In in) {
        result = new r.nodes.exec.InOperation(in, createTree(in.getLHS()), createTree(in.getRHS()));
    }

    @Override public void visit(Mod mod) {
        visitArithmetic(mod);
    }

    @Override public void visit(Pow pow) {
        visitArithmetic(pow);
    }

    @Override public void visit(Div div) {
        visitArithmetic(div);
    }

    @Override public void visit(Sub sub) {
        visitArithmetic(sub);
    }

    @Override public void visit(Colon col) {
        // FIXME: allow symbol override?
        ASTNode lhs = col.getLHS();
        ASTNode rhs = col.getRHS();
        result = r.builtins.Primitives.getCallFactory(RSymbol.getSymbol(":"), null).create(col, createTree(lhs), createTree(rhs));
        if (lhs instanceof Constant && rhs instanceof Constant) { // TODO: more general constant folding
            RAny value = (RAny) result.execute(null);
            value.ref();
            value.ref();
            result = new r.nodes.exec.Constant(col, value);
        }
    }

    @Override public void visit(And and) {
        result = new r.nodes.exec.LogicalOperation.And(and, createTree(and.getLHS()), createTree(and.getRHS()));
    }

    @Override public void visit(ElementwiseAnd and) {
        result = r.nodes.exec.ElementwiseLogicalOperation.createUninitialized(and, createTree(and.getLHS()), r.nodes.exec.ElementwiseLogicalOperation.AND, createTree(and.getRHS()));
    }

    @Override public void visit(Or or) {
        result = new r.nodes.exec.LogicalOperation.Or(or, createTree(or.getLHS()), createTree(or.getRHS()));
    }

    @Override public void visit(ElementwiseOr or) {
        result = r.nodes.exec.ElementwiseLogicalOperation.createUninitialized(or, createTree(or.getLHS()), r.nodes.exec.ElementwiseLogicalOperation.OR, createTree(or.getRHS()));
    }

    @Override public void visit(ArgumentList.Default.DefaultEntry entry) {}
}
