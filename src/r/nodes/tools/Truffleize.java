package r.nodes.tools;

import r.Utils;
import r.builtins.Primitives;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.nodes.*;
import r.nodes.Constant;
import r.nodes.Function;
import r.nodes.FunctionCall;
import r.nodes.If;
import r.nodes.Not;
import r.nodes.Sequence;
import r.nodes.UnaryMinus;
import r.nodes.UpdateVector;
import r.nodes.truffle.*;
import r.nodes.truffle.Loop;
import r.nodes.truffle.ReplacementCall.RememberLast;

import com.oracle.truffle.api.frame.*;

public class Truffleize implements Visitor {

    RNode result;
    public static final boolean DEBUG_SPECIAL_NODES = false;

    public RNode createLazyRootTree(final ASTNode ast) {
        return new BaseR(ast) {

            @Child RNode node = adoptChild(createLazyTree(ast));

            @Override
            public final Object execute(Frame frame) {
                try {
                    return node.execute(frame);
                } catch (Loop.ContinueException ce) {
                    throw RError.getNoLoopForBreakNext(ast);
                } catch (Loop.BreakException be) {
                    throw RError.getNoLoopForBreakNext(ast);
                }
            }
        };
    }

    public RNode createTree(ASTNode ast) {
        ast.accept(this);
        return result;
    }

    @SuppressWarnings("static-method")
    private RNode createLazyTree(ASTNode ast) {
        return new LazyBuild(ast);
    }

    @Override
    public void visit(If ast) {
        // FIXME: lazy build breaks Truffle inliner

        ASTNode falseBranch = ast.getFalseCase();
        ASTNode cond = ast.getCond();
        RNode rcond = createLazyTree(cond);
        RNode rtrueBranch = createLazyTree(ast.getTrueCase());
        RNode rfalseBranch = (falseBranch == null) ? r.nodes.truffle.Constant.getNull() : createLazyTree(falseBranch);

        if (cond instanceof EQ) {
            EQ e = (EQ) cond;
            ASTNode rhs = e.getRHS();
            ASTNode lhs = e.getLHS();
            if (rhs instanceof Constant) {
                result = new r.nodes.truffle.If.IfConst(ast, rcond, createTree(lhs), rtrueBranch, rfalseBranch, ((Constant) rhs).getValue());
                return;
            }
            if (lhs instanceof Constant) {
                result = new r.nodes.truffle.If.IfConst(ast, rcond, createTree(rhs), rtrueBranch, rfalseBranch, ((Constant) lhs).getValue());
                return;
            }
        }

        if (falseBranch == null) {
            result = new r.nodes.truffle.If.IfNoElse(ast, rcond, rtrueBranch);
        } else {
            result = new r.nodes.truffle.If.IfElse(ast, rcond, rtrueBranch, rfalseBranch);
        }
    }

    @Override
    public void visit(Repeat repeat) {
        result = new r.nodes.truffle.Loop.Repeat(repeat, createLazyTree(repeat.getBody()));
    }

    @Override
    public void visit(While n) {
        ASTNode cond = n.getCond();
        if (cond instanceof Constant) {
            RAny value = ((Constant) cond).getValue();
            int l = value.asLogical().getLogical(0);
            if (l == RLogical.TRUE) {
                result = new r.nodes.truffle.Loop.Repeat(n, createLazyTree(n.getBody()));
                return;
            }
        }
        result = new r.nodes.truffle.Loop.While(n, createTree(cond), createLazyTree(n.getBody()));
    }

    @Override
    public void visit(For n) {
        result = new r.nodes.truffle.Loop.For.IntSequenceRange(n, n.getCVar(), createTree(n.getRange()), createLazyTree(n.getBody()));
    }

    @Override
    public void visit(Break n) {
        result = new r.nodes.truffle.Loop.Break(n);
    }

    @Override
    public void visit(Next n) {
        result = new r.nodes.truffle.Loop.Next(n);
    }

    @Override
    public void visit(Sequence sequence) {
        ASTNode[] exprs = sequence.getExprs();
        RNode[] rexprs = new RNode[exprs.length];
        for (int i = 0; i < exprs.length; i++) {
            rexprs[i] = createTree(exprs[i]);
        }
        result = new r.nodes.truffle.Sequence(sequence, rexprs);
    }

    @Override
    public void visit(Not n) {
        result = new r.nodes.truffle.Not.LogicalScalar(n, createTree(n.getLHS()));
    }

    @Override
    public void visit(UnaryMinus m) {
        result = new r.nodes.truffle.UnaryMinus.NumericScalar(m, createTree(m.getLHS()));
    }

    @Override
    public void visit(Constant constant) {
        result = new r.nodes.truffle.Constant(constant, constant.getValue());
    }

    @Override
    public void visit(SimpleAccessVariable readVariable) {
        RSymbol symbol = readVariable.getSymbol();
        result = r.nodes.truffle.ReadVariable.getUninitialized(readVariable, symbol);
    }

    /** FieldAccess closely resembles the subset operator, but uses the ReadList class which closely matches the
     * functionality of String-typed selector from vectors.
     */
    @Override
    public void visit(FieldAccess fa) {
        ASTNode lhs = fa.lhs();
        RNode n = createTree(lhs);
        result = new ReadVector.FieldSelection.UninitializedSelection(fa, n, RSymbol.getSymbol(fa.fieldName()));
    }

    @Override
    public void visit(SimpleAssignVariable assign) {
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

        r.nodes.truffle.Arithmetic.ValueArithmetic arit = getValueArithmetic(valueNode);

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
                RFunction encFunction =  getEnclosingFunction(assign);
                FrameSlot slot = null;
                if (encFunction != null) {
                    slot = encFunction.localSlot(binVarSymbol);
                }
                if (binVarSymbol == symbol && !assign.isSuper() && (constNode.getValue() instanceof ScalarIntImpl) && slot != null) {
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
            result = r.nodes.truffle.SuperWriteVariable.getUninitialized(assign, symbol, createTree(valueNode));
        } else {
            // Truffle does not like Lazy...
            //result = r.nodes.truffle.WriteVariable.getUninitialized(assign, assign.getSymbol(), createLazyTree(assign.getExpr()));
            result = r.nodes.truffle.WriteVariable.getUninitialized(assign, symbol, createTree(valueNode));
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

    @Override
    public void visit(Function function) {
        assert Utils.check(function.getRFunction() == null); // TODO the ast.Function must create the RFunction !

        RFunction encf = getEnclosingFunction(function);

        SplitArgumentList a = splitArgumentList(function.getSignature(), true); // the name is not really accurate since, these are parameters

            // note: body has to be built lazily, otherwise nested functions won't work correctly
        RFunction impl = function.createImpl(a.convertedNames, a.convertedExpressions, createLazyRootTree(function.getBody()), encf);
        r.nodes.truffle.Function functionNode = new r.nodes.truffle.Function(impl);

        result = functionNode;
    }

    private static RFunction getEnclosingFunction(ASTNode node) {
        // find lexically enclosing function if exists
        Function enfunc = findParent(node, Function.class);
        if (enfunc == null) {
            return null;
        }
        RFunction rfunc = enfunc.getRFunction();
        Utils.check(rfunc != null, "RFunction is not yet ready - note lazy build is necessary for functions");
        return rfunc;
    }

    @Override
    public void visit(FunctionCall functionCall) {
        // FIXME: In R, function call needs not have a symbol, it can be a lambda expression
        // TODO: FunctionCall for now are ONLY for variable (see Call.create ...). It's maybe smarter to move this instance of here and replace the type of name by expression
        SplitArgumentList a = splitArgumentList(functionCall.getArgs(), true);

        RSymbol sym = functionCall.getName();
        r.builtins.CallFactory factory;

        if (Primitives.STATIC_LOOKUP) {
            factory = r.builtins.Primitives.getCallFactory(sym, getEnclosingFunction(functionCall));
            if (factory == null) {
                factory = r.nodes.truffle.FunctionCall.FACTORY;
            }
        } else {
            factory = r.nodes.truffle.FunctionCall.FACTORY;
        }

        RNode rCall = factory.create(functionCall, a.convertedNames, a.convertedExpressions);

        if (!functionCall.isAssignment()) {
            result = rCall;
            return;
        }

        // replacement assignment
        RNode valueExpr = a.convertedExpressions[a.convertedExpressions.length - 1];
        RNode parentOfValueExpr = (RNode) valueExpr.getParent();
        RememberLast remValueExpr = new RememberLast(valueExpr.getAST(), valueExpr);
        parentOfValueExpr.replaceChild(valueExpr, remValueExpr);
        a.convertedExpressions[a.convertedExpressions.length - 1] = remValueExpr;

        SimpleAccessVariable xAST = (SimpleAccessVariable) a.convertedExpressions[0].getAST();

        result = new ReplacementCall(functionCall, functionCall.isSuper(), xAST.getSymbol(), rCall, remValueExpr);
    }


    // TODO This has to be changed for partial matching so that exact is checked also for vectors
    @Override
    public void visit(AccessVector a) {
        SplitArgumentList sa = splitArgumentList(a.getArgs(), false);

        if (sa.convertedExpressions.length == 1) { // vector
            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
              result = new ReadVector.SimpleIntSequenceSelection(a, createTree(a.getVector()), sa.convertedExpressions, a.isSubset());
            } else {
              RNode e = sa.convertedExpressions[0];
              if (e instanceof r.nodes.truffle.Constant) {
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

            RNode[] nodes = sa.convertedExpressions;
            RSymbol[] names = sa.convertedNames;
            int dims = 0;

            for (int i = 0; i < nodes.length; i++) {
                if (names[i] == RSymbol.DROP_SYMBOL) {
                    if (drop != null) {
                        throw RError.getIncorrectSubscripts(a);
                    }
                    drop = nodes[i];
                } else if (names[i] == RSymbol.EXACT_SYMBOL) {
                    if (exact != null) {
                        throw RError.getIncorrectSubscripts(a);
                    }
                    exact = nodes[i];
                } else {
                    selectors[dims] = nodes[i];
                    ++dims;
                }
            }
            if (dims == 0) {
                Utils.nyi("unsupported indexing style");
            }
            Selector.SelectorNode[] selNodes = new Selector.SelectorNode[dims];
            for (int i = 0; i < selNodes.length; ++i) {
                selNodes[i] = Selector.createSelectorNode(a, a.isSubset(), selectors[i]);
            }
            if (dims == 2) { // if matrix read, use the specialized matrix form
                if (!a.isSubset()) {
                    result = new ReadArray.MatrixSubscript(a, createTree(a.getVector()),
                            selNodes,
                            ReadArray.createDropOptionNode(a, drop),
                            ReadArray.createExactOptionNode(a, exact));
                } else {
                    result = new ReadArray.MatrixRead(a, a.isSubset(), createTree(a.getVector()),
                                  selNodes,
                                  ReadArray.createDropOptionNode(a, drop),
                                  ReadArray.createExactOptionNode(a, exact));
                }
            } else { // otherwise use the generalized array read
                result = new ReadArray.GeneralizedRead(a, a.isSubset(), createTree(a.getVector()),
                              selNodes,
                              ReadArray.createDropOptionNode(a, drop),
                              ReadArray.createExactOptionNode(a, exact));
            }
            return;
        }
        Utils.nyi("unsupported indexing style");
    }

    @Override
    public void visit(UpdateVector u) {
        AccessVector a = u.getVector();
        SplitArgumentList sa = splitArgumentList(a.getArgs(), false);

        if (sa.convertedExpressions.length == 1) {
            ASTNode varAccess = a.getVector();
            if (!(varAccess instanceof SimpleAccessVariable)) {
                Utils.nyi("expecting vector name for vector update");
            }
            RSymbol var = ((SimpleAccessVariable) varAccess).getSymbol();
            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
                result = new r.nodes.truffle.UpdateVector.IntSequenceSelection(u, u.isSuper(), var, createTree(varAccess), sa.convertedExpressions, createTree(u.getRHS()), a.isSubset());
            } else {
//                result = new r.nodes.truffle.UpdateVector.ScalarNumericSelection(u, u.isSuper(), var, createTree(varAccess), sa.convertedExpressions, createTree(u.getRHS()), a.isSubset());
                result = new r.nodes.truffle.UpdateVector.DoubleBaseSimpleSelection.ScalarIntSelection(u, u.isSuper(), var, createTree(varAccess), sa.convertedExpressions, createTree(u.getRHS()), a.isSubset());
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
                /*if (names[i] == RSymbol.dropName) {
                    if (drop != null) {
                        throw RError.getIncorrectSubscripts(a);
                    }
                    drop = nodes[i];
                    continue;
                } else */ if (names[i] == RSymbol.EXACT_SYMBOL) {
                    if (exact != null) {
                        throw RError.getIncorrectSubscripts(a);
                    }
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
            // Create the assignment, or superassignment nodes
            ASTNode varAccess = a.getVector();
            RSymbol var = ((SimpleAccessVariable) varAccess).getSymbol();
            if (!(varAccess instanceof SimpleAccessVariable)) {
                Utils.nyi("expecting matrix name for matrix update");
            }
            if (u.isSuper()) {
                result = UpdateArraySuperAssignment.create(a, var, createTree(varAccess), createTree(u.getRHS()), UpdateArray.create(a, selNodes, a.isSubset()));
            } else {
                result = UpdateArrayAssignment.create(a, var, createTree(u.getRHS()), UpdateArray.create(a, selNodes, a.isSubset()));
                return;
            }
        } else {
            Utils.nyi("Unsupported indexing style");
        }
    }

    /** Converts the field update ($ selector on topmost lhf of assignment a$x = xyz).
     */
    @Override
    public void visit(UpdateField u) {
        /// FIXME this uses the old variant of $ selector that works also on vectors. Should be replaced.
        FieldAccess fa = u.getVector();
        ASTNode varAccess = fa.lhs();
        if (!(varAccess instanceof SimpleAccessVariable)) {
            Utils.nyi("expecting vector name for vector update");
        }
        RSymbol var = ((SimpleAccessVariable) varAccess).getSymbol();
        RNode index =  createTree(Constant.createStringConstant(fa.fieldName()));
        result = new r.nodes.truffle.UpdateVector.DollarListUpdate(u, u.isSuper(), var, createTree(varAccess), index, createTree(u.getRHS()));
    }

    @SuppressWarnings("unchecked")
    private static <T extends ASTNode> T findParent(ASTNode node, Class<T> clazz) {
        ASTNode n = node.getParent();
        while (n != null) {
            if (clazz.isInstance(n)) {
                return (T) n;
            }
            n = n.getParent();
        }
        return null;
    }

    @Override
    public void visit(EQ eq) {
        result = new r.nodes.truffle.Comparison(eq, createTree(eq.getLHS()), createTree(eq.getRHS()), r.nodes.truffle.Comparison.getEQ());
    }

    @Override
    public void visit(NE ne) {
        result = new r.nodes.truffle.Comparison(ne, createTree(ne.getLHS()), createTree(ne.getRHS()), r.nodes.truffle.Comparison.getNE());
    }
    @Override
    public void visit(LE le) {
        result = new r.nodes.truffle.Comparison(le, createTree(le.getLHS()), createTree(le.getRHS()), r.nodes.truffle.Comparison.getLE());
    }

    @Override
    public void visit(GE ge) {
        result = new r.nodes.truffle.Comparison(ge, createTree(ge.getLHS()), createTree(ge.getRHS()), r.nodes.truffle.Comparison.getGE());
    }

    @Override
    public void visit(LT lt) {
        result = new r.nodes.truffle.Comparison(lt, createTree(lt.getLHS()), createTree(lt.getRHS()), r.nodes.truffle.Comparison.getLT());
    }

    @Override
    public void visit(GT gt) {
        result = new r.nodes.truffle.Comparison(gt, createTree(gt.getLHS()), createTree(gt.getRHS()), r.nodes.truffle.Comparison.getGT());
    }

    public static r.nodes.truffle.Arithmetic.ValueArithmetic getValueArithmetic(ASTNode ast) {
        if (ast instanceof Add) {
            return r.nodes.truffle.Arithmetic.ADD;
        }
        if (ast instanceof Mult) {
            return r.nodes.truffle.Arithmetic.MULT;
        }
        if (ast instanceof IntegerDiv) {
            return r.nodes.truffle.Arithmetic.INTEGER_DIV;
        }
        if (ast instanceof Mod) {
            return r.nodes.truffle.Arithmetic.MOD;
        }
        if (ast instanceof Pow) {
            return r.nodes.truffle.Arithmetic.POW;
        }
        if (ast instanceof Div) {
            return r.nodes.truffle.Arithmetic.DIV;
        }
        if (ast instanceof Sub) {
            return r.nodes.truffle.Arithmetic.SUB;
        }
        return null;
    }

    private void visitArithmetic(BinaryOperation op) {
        r.nodes.truffle.Arithmetic.ValueArithmetic arit = getValueArithmetic(op);
        assert Utils.check(arit != null);
        result = new r.nodes.truffle.Arithmetic(op, createTree(op.getLHS()), createTree(op.getRHS()), arit);
    }

    @Override
    public void visit(Add add) {
        visitArithmetic(add);
    }

    @Override
    public void visit(Mult mult) {
        visitArithmetic(mult);
    }

    @Override
    public void visit(MatMult mult) {
        result = new r.nodes.truffle.MatrixOperation.MatrixProduct(mult, createTree(mult.getLHS()), createTree(mult.getRHS()));
    }

    @Override
    public void visit(OuterMult mult) {
        result = new r.nodes.truffle.MatrixOperation.OuterProduct(mult, createTree(mult.getLHS()), createTree(mult.getRHS()));
    }

    @Override
    public void visit(IntegerDiv div) {
        visitArithmetic(div);
    }

    @Override
    public void visit(In in) {
        result = new r.nodes.truffle.InOperation(in, createTree(in.getLHS()), createTree(in.getRHS()));
    }

    @Override
    public void visit(Mod mod) {
        visitArithmetic(mod);
    }

    @Override
    public void visit(Pow pow) {
        visitArithmetic(pow);
    }

    @Override
    public void visit(Div div) {
        visitArithmetic(div);
    }

    @Override
    public void visit(Sub sub) {
        visitArithmetic(sub);
    }

    @Override
    public void visit(Colon col) {
        // FIXME: allow symbol override?
        result = r.builtins.Primitives.getCallFactory(RSymbol.getSymbol(":"), null).create(col, createTree(col.getLHS()), createTree(col.getRHS()));
    }

    @Override
    public void visit(And and) {
        result = new r.nodes.truffle.LogicalOperation.And(and, createTree(and.getLHS()), createTree(and.getRHS()));
    }

    @Override
    public void visit(ElementwiseAnd and) {
        result = r.nodes.truffle.ElementwiseLogicalOperation.createUninitialized(and, createTree(and.getLHS()), r.nodes.truffle.ElementwiseLogicalOperation.AND, createTree(and.getRHS()));
    }

    @Override
    public void visit(Or or) {
        result = new r.nodes.truffle.LogicalOperation.Or(or, createTree(or.getLHS()), createTree(or.getRHS()));
    }

    @Override
    public void visit(ElementwiseOr or) {
        result = r.nodes.truffle.ElementwiseLogicalOperation.createUninitialized(or, createTree(or.getLHS()), r.nodes.truffle.ElementwiseLogicalOperation.OR, createTree(or.getRHS()));
    }

    @Override
    public void visit(ArgumentList.Default.DefaultEntry entry) {
    }
}
