package r.nodes.tools;

import com.oracle.truffle.nodes.control.*;
import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.BinaryOperation.BinaryOperator;
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

            final RNode node = updateParent(createLazyTree(ast));

            @Override
            public Object execute(RContext context, Frame frame) {
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
    public void visit(If iff) {
        ASTNode fbranch = iff.getFalseCase();
        // lazy build breaks Truffle inliner
        //result = new r.nodes.truffle.If(iff, createLazyTree(iff.getCond()), createLazyTree(iff.getTrueCase()), fbranch == null ? r.nodes.truffle.Constant.getNull() : createLazyTree(fbranch));
        result = new r.nodes.truffle.If(iff, createTree(iff.getCond()), createTree(iff.getTrueCase()), fbranch == null ? r.nodes.truffle.Constant.getNull() : createTree(fbranch));
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
        result = r.nodes.truffle.ReadVariable.getUninitialized(readVariable, readVariable.getSymbol());
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
        result = r.nodes.truffle.WriteVariable.getUninitialized(assign, assign.getSymbol(), createTree(assign.getExpr()));
    }

    private RSymbol[] convertedNames;
    private RNode[] convertedExpressions;

    private void splitArgumentList(ArgumentList alist) {
        int args = alist.size();
        RSymbol[] names = new RSymbol[args];
        RNode[] expressions = new RNode[args];
        int i = 0;
        for (ArgumentList.Entry e : alist) {
            names[i] = e.getName();
            ASTNode exp = e.getValue();
            if (exp != null) {
                expressions[i] = createLazyRootTree(exp);
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

        RFunction encf = getEnclosing(function);

        splitArgumentList(function.getSignature()); // the name is not really accurate since, these are parameters

            // note: body has to be built lazily, otherwise nested functions won't work correctly
        RFunction impl = function.createImpl(convertedNames, convertedExpressions, createLazyRootTree(function.getBody()), encf);
        r.nodes.truffle.Function functionNode = new r.nodes.truffle.Function(impl);

        result = functionNode;
    }

    private static RFunction getEnclosing(ASTNode node) {
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
        splitArgumentList(functionCall.getArgs());

        r.builtins.CallFactory factory = r.builtins.Primitives.getCallFactory(functionCall.getName(), getEnclosing(functionCall));
        if (factory == null) {
            factory = r.nodes.truffle.FunctionCall.FACTORY;
        }
        result = factory.create(functionCall, convertedNames, convertedExpressions);
    }

    @Override
    public void visit(AccessVector a) {
        splitArgumentList(a.getArgs());
        if (convertedExpressions.length == 1) {
            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
              result = new ReadVector.SimpleIntSequenceSelection(a, createTree(a.getVector()), convertedExpressions, a.isSubset());
            } else {
              result = new ReadVector.SimpleScalarIntSelection(a, createTree(a.getVector()), convertedExpressions, a.isSubset());
            }
        }
    }

    @Override
    public void visit(UpdateVector u) {
        AccessVector a = u.getVector();
        splitArgumentList(a.getArgs());
        if (convertedExpressions.length == 1) {
            RNode vvalue;
            if (a.getArgs().first().getValue() instanceof Colon && a.isSubset()) {
                vvalue = new r.nodes.truffle.UpdateVector.IntSequenceSelection(u, createTree(a.getVector()), convertedExpressions, createTree(u.getRHS()), a.isSubset());
            } else {
                vvalue = new r.nodes.truffle.UpdateVector.ScalarNumericSelection(u, createTree(a.getVector()), convertedExpressions, createTree(u.getRHS()), a.isSubset());
            }
            ASTNode v = a.getVector();
            if (!(v instanceof SimpleAccessVariable)) {
                Utils.nyi("expect vector name for vector update");
            }
            RSymbol var = ((SimpleAccessVariable) v).getSymbol();
            result = r.nodes.truffle.WriteVariable.getUninitialized(u, var, vvalue);
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
        result = new r.nodes.truffle.Arithmetic(add, createTree(add.getLHS()), createTree(add.getRHS()), BinaryOperator.ADD);
    }

    @Override
    public void visit(Mult mult) {
        result = new r.nodes.truffle.Arithmetic(mult, createTree(mult.getLHS()), createTree(mult.getRHS()), BinaryOperator.MULT);
    }

    @Override
    public void visit(Sub sub) {
        result = new r.nodes.truffle.Arithmetic(sub, createTree(sub.getLHS()), createTree(sub.getRHS()), BinaryOperator.SUB);
    }

    @Override
    public void visit(Colon col) {
        // FIXME: this does not allow overriding when as operator, but maybe this should not be allowed anyway
        result = r.builtins.Colon.FACTORY.create(col, createTree(col.getLHS()), createTree(col.getRHS()));
    }
}
