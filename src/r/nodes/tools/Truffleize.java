package r.nodes.tools;

import java.util.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
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
            final RNode node = updateParent(createTree(ast));
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
    public void visit(Mult mult) {
    }

    @Override
    public void visit(Add add) {
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

    private RArgumentList convertArgumentList(ArgumentList alist) {
        int args = alist.size();
        RSymbol[] names = new RSymbol[args];
        RNode[] expressions = new RNode[args];
        int i = 0;
        for (ArgumentList.Entry e : alist) {
            names[i] = e.getName();
            ASTNode exp = e.getValue();
            if (exp != null) {
                expressions[i] = createTree(exp);
            }
            i++;
        }
        Utils.check(i == args);
        return new RArgumentList(names, expressions);
    }

    static class FindAccesses implements Visitor {

        HashSet<RSymbol> read;
        HashSet<RSymbol> written;

        public FindAccesses(HashSet<RSymbol> read, HashSet<RSymbol> written) {
            this.read = read;
            this.written = written;
        }

        public void run(Function func) {
            func.visit_all(this); // does a function body
              // FIXME: should visit_all also visit the default expressions by itself?
            ArgumentList al = func.getSignature();
            for (ArgumentList.Entry e : al) {
                ASTNode val = e.getValue();
                if (val != null) {
                    val.visit_all(this);
                }
                // note: formal arguments are added to write set elsewhere
            }
        }

        @Override
        public void visit(If iff) {
            iff.visit_all(this);
        }

        @Override
        public void visit(Repeat repeat) {
            repeat.visit_all(this);
        }

        @Override
        public void visit(While wh1le) {
            wh1le.visit_all(this);
        }

        @Override
        public void visit(Sequence sequence) {
            sequence.visit_all(this);
        }

        @Override
        public void visit(Mult mult) {
            mult.visit_all(this);
        }

        @Override
        public void visit(Add add) {
            add.visit_all(this);
        }

        @Override
        public void visit(Not n) {
            n.visit_all(this);
        }

        @Override
        public void visit(Constant constant) {
        }

        @Override
        public void visit(SimpleAccessVariable readVariable) {
            read.add(readVariable.getSymbol());
        }

        @Override
        public void visit(FieldAccess fieldAccess) {
            fieldAccess.visit_all(this);
        }

        @Override
        public void visit(SimpleAssignVariable assign) {
            written.add(assign.getSymbol());
        }

        @Override
        public void visit(Function function) {
        }

        @Override
        public void visit(FunctionCall functionCall) {
            read.add(functionCall.getName());
            functionCall.visit_all(this);  // visit default value expressions if any
        }
    }

    @Override
    public void visit(Function function) {
        RArgumentList ralist = convertArgumentList(function.getSignature());

        // find lexically enclosing function
        RFunction encf = null;
        ASTNode n = function.getParent();
        while (n != null) {
            if (n instanceof Function) {
                encf = ((Function) n).getR();
                break;
            }
            n = n.getParent();
        }

        // find variables accessed
        HashSet<RSymbol> read = new HashSet<>();
        HashSet<RSymbol> written = new HashSet<>();

        FindAccesses fa = new FindAccesses(read, written);
        fa.run(function);

        RFunction rfunction = FunctionImpl.create(ralist, null, encf, written, read);
        r.nodes.truffle.Function functionNode = new r.nodes.truffle.Function(function, rfunction, createLazyTree(function.getBody()));
        ((FunctionImpl) rfunction).setCode(functionNode);
        result = functionNode;
    }

    @Override
    public void visit(FunctionCall functionCall) {
        // FIXME: In R, function call needs not have a symbol, it can be a lambda expression
        RArgumentList ralist = convertArgumentList(functionCall.getArgs());
        RNode fexp = r.nodes.truffle.ReadVariable.getUninitialized(functionCall, functionCall.getName());
        result = new r.nodes.truffle.FunctionCall(functionCall, ralist, fexp);
    }

}
