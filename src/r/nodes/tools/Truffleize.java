package r.nodes.tools;

import r.*;
import r.builtins.*;
import r.data.*;
import r.nodes.*;
import r.nodes.BinaryOperation.BinaryOperator;
import r.nodes.Constant;
import r.nodes.Function;
import r.nodes.FunctionCall;
import r.nodes.If;
import r.nodes.Sequence;
import r.nodes.truffle.*;

public class Truffleize implements Visitor {

    RNode result;

    public RNode createRootTree(final ASTNode ast) {
        return new BaseR(ast) {

            final RNode node = updateParent(createLazyTree(ast));

            @Override
            public Object execute(RContext context, RFrame frame) {
                return node.execute(context, frame);
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
        result = new r.nodes.truffle.If(iff, createLazyTree(iff.getCond()), createLazyTree(iff.getTrueCase()), fbranch == null ? r.nodes.truffle.Constant.getNull() : createLazyTree(fbranch));
    }

    @Override
    public void visit(Repeat repeat) {
    }

    @Override
    public void visit(While wh1le) {
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
        result = r.nodes.truffle.WriteVariable.getUninitialized(assign, assign.getSymbol(), createLazyTree(assign.getExpr()));
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
                expressions[i] = createRootTree(exp);
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

        RFunction impl = function.createImpl(convertedNames, convertedExpressions, createRootTree(function.getBody()), encf);
        r.nodes.truffle.Function functionNode = new r.nodes.truffle.Function(impl);

        result = functionNode;
    }

    private static RFunction getEnclosing(ASTNode node) {
        // find lexically enclosing function if exists
        Function astEnc = findParent(node, Function.class);
        return astEnc == null ? null : astEnc.getRFunction();
    }

    @Override
    public void visit(FunctionCall functionCall) {
        // FIXME: In R, function call needs not have a symbol, it can be a lambda expression
        // TODO: FunctionCall for now are ONLY for variable (see Call.create ...). It's maybe smarter to move this instance of here and replace the type of name by expression
        splitArgumentList(functionCall.getArgs());

        result = Primitives.getNode(functionCall, getEnclosing(functionCall), convertedNames, convertedExpressions);
        // FIXME: remove this!! just a temporary hack to get some builtins
        if (result == null) {
            if (r.nodes.truffle.DummyBuiltin.handles(functionCall.getName())) {
                result = new r.nodes.truffle.DummyBuiltin(functionCall, functionCall.getName(), convertedNames, convertedExpressions);
            } else {
                RNode fexp = r.nodes.truffle.ReadVariable.getUninitialized(functionCall, functionCall.getName()); // FIXME: ReadVariable CANNOT be used ! Function lookup are != from variable lookups
                result = r.nodes.truffle.FunctionCall.getFunctionCall(functionCall, fexp, convertedNames, convertedExpressions);
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
        result = new r.nodes.truffle.Comparison(eq, createTree(eq.getLHS()), createTree(eq.getRHS()), BinaryOperator.EQ);
    }

    @Override
    public void visit(LE le) {
        result = new r.nodes.truffle.Comparison(le, createTree(le.getLHS()), createTree(le.getRHS()), BinaryOperator.LE);
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
}
