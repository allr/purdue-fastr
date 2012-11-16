package r.nodes.tools;

import org.junit.experimental.runners.*;

import com.oracle.truffle.nodes.control.*;
import com.oracle.truffle.runtime.Frame;
import com.oracle.truffle.runtime.Stable;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.Constant;
import r.nodes.Function;
import r.nodes.FunctionCall;
import r.nodes.If;
import r.nodes.Not;
import r.nodes.Sequence;
import r.nodes.UpdateVector;
import r.nodes.UnaryMinus;
import r.nodes.truffle.*;

public class Truffleize implements Visitor {

    RNode result;

    public RNode createLazyRootTree(final ASTNode ast) {
        return new BaseR(ast) {

            @Stable RNode node = updateParent(createLazyTree(ast));

            @Override
            public final Object execute(RContext context, Frame frame) {
                try {
                    return node.execute(context, frame);
                } catch (ContinueException ce) {
                    throw RError.getNoLoopForBreakNext(ast);
                } catch (BreakException be) {
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
                result = new r.nodes.truffle.If.IfConst(ast, rcond, createTree(lhs), rtrueBranch, rfalseBranch, ((Constant) rhs).execute(null, null));
                return;
            }
            if (lhs instanceof Constant) {
                result = new r.nodes.truffle.If.IfConst(ast, rcond, createTree(rhs), rtrueBranch, rfalseBranch, ((Constant) lhs).execute(null, null));
                return;
            }
        }

        result = new r.nodes.truffle.If(ast, rcond, rtrueBranch, rfalseBranch);
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

    @Override
    public void visit(FieldAccess fieldAccess) {
    }

    @Override
    public void visit(SimpleAssignVariable assign) {
        if (assign.isSuper()) {
            Utils.nyi();
        }
        // Truffle does not like Lazy...
        //result = r.nodes.truffle.WriteVariable.getUninitialized(assign, assign.getSymbol(), createLazyTree(assign.getExpr()));

        RSymbol sym = assign.getSymbol();
        if (r.builtins.Primitives.get(sym) != null) {
            Utils.nyi(sym.pretty() + ": we don't support variables over-shadowing primitives.");
            // NOTE: we could support this as long as the value assigned isn't a function, but checking that would be expensive
            // it may become cheaper once/if we type-specialize assignment nodes, at some point when we do boxing optimizations
        }
        result = r.nodes.truffle.WriteVariable.getUninitialized(assign, assign.getSymbol(), createTree(assign.getExpr()));
    }

    private RSymbol[] convertedNames;
    private RNode[] convertedExpressions;

    private void splitArgumentList(ArgumentList alist, boolean root) {
        int args = alist.size();
        RSymbol[] names = new RSymbol[args];
        RNode[] expressions = new RNode[args];
        int i = 0;
        for (ArgumentList.Entry e : alist) {
            names[i] = e.getName();
            ASTNode exp = e.getValue();
            if (exp != null) {
                if (root) {
                    expressions[i] = createLazyRootTree(exp);
                } else {
                    expressions[i] = createTree(exp);
                }
            }
            i++;
        }
        assert Utils.check(i == args);
        convertedNames = names;
        convertedExpressions = expressions;
    }

    @Override
    public void visit(Function function) {
        assert Utils.check(function.getRFunction() == null); // TODO the ast.Function must create the RFunction !

        RFunction encf = getEnclosingFunction(function);

        splitArgumentList(function.getSignature(), true); // the name is not really accurate since, these are parameters

            // note: body has to be built lazily, otherwise nested functions won't work correctly
        RFunction impl = function.createImpl(convertedNames, convertedExpressions, createLazyRootTree(function.getBody()), encf);
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
        splitArgumentList(functionCall.getArgs(), true);

        RSymbol sym = functionCall.getName();
        r.builtins.CallFactory factory = r.builtins.Primitives.getCallFactory(sym, getEnclosingFunction(functionCall));
        if (factory == null) {
            factory = r.nodes.truffle.FunctionCall.FACTORY;
        }
        result = factory.create(functionCall, convertedNames, convertedExpressions);
    }

    @Override
    public void visit(AccessVector a) {
        splitArgumentList(a.getArgs(), false);
        if (convertedExpressions.length == 1) {
            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
              result = new ReadVector.SimpleIntSequenceSelection(a, createTree(a.getVector()), convertedExpressions, a.isSubset());
            } else {
              RNode e = convertedExpressions[0];
              if (e instanceof r.nodes.truffle.Constant) {
                  RAny v = (RAny) e.execute(null, null);
                  if (v instanceof RDouble || v instanceof RInt) {
                      RInt iv = v.asInt();
                      if (iv.size() == 1) {
                          int index = v.asInt().getInt(0);
                          if (index > 0) {
                              result = new ReadVector.SimpleConstantScalarIntSelection(a, createTree(a.getVector()), convertedExpressions, index, a.isSubset());
                              return;
                          }
                      }
                  }
              }
              result = new ReadVector.SimpleScalarIntSelection(a, createTree(a.getVector()), convertedExpressions, a.isSubset());
            }
        }
    }

    @Override
    public void visit(UpdateVector u) {
        AccessVector a = u.getVector();
        splitArgumentList(a.getArgs(), false);

        if (convertedExpressions.length == 1) {
            ASTNode varAccess = a.getVector();
            if (!(varAccess instanceof SimpleAccessVariable)) {
                Utils.nyi("expect vector name for vector update");
            }
            RSymbol var = ((SimpleAccessVariable) varAccess).getSymbol();
            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
                result = new r.nodes.truffle.UpdateVector.IntSequenceSelection(u, var, createTree(varAccess), convertedExpressions, createTree(u.getRHS()), a.isSubset());
            } else {
                result = new r.nodes.truffle.UpdateVector.ScalarNumericSelection(u, var, createTree(varAccess), convertedExpressions, createTree(u.getRHS()), a.isSubset());
            }
        }
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

    @Override
    public void visit(Add add) {
        result = new r.nodes.truffle.Arithmetic(add, createTree(add.getLHS()), createTree(add.getRHS()), r.nodes.truffle.Arithmetic.ADD);
    }

    @Override
    public void visit(Mult mult) {
        result = new r.nodes.truffle.Arithmetic(mult, createTree(mult.getLHS()), createTree(mult.getRHS()), r.nodes.truffle.Arithmetic.MULT);
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
    public void visit(Pow pow) {
        result = new r.nodes.truffle.Arithmetic(pow, createTree(pow.getLHS()), createTree(pow.getRHS()), r.nodes.truffle.Arithmetic.POW);
    }

    @Override
    public void visit(Div div) {
        result = new r.nodes.truffle.Arithmetic(div, createTree(div.getLHS()), createTree(div.getRHS()), r.nodes.truffle.Arithmetic.DIV);
    }

    @Override
    public void visit(Sub sub) {
        result = new r.nodes.truffle.Arithmetic(sub, createTree(sub.getLHS()), createTree(sub.getRHS()), r.nodes.truffle.Arithmetic.SUB);
    }

    @Override
    public void visit(Colon col) {
        // FIXME: allow symbol override?
        result = r.builtins.Primitives.getCallFactory(RSymbol.getSymbol(":"), null).create(col, createTree(col.getLHS()), createTree(col.getRHS()));
    }

    @Override
    public void visit(ArgumentList.Default.DefaultEntry entry) {
    }
}
