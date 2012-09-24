package r.nodes.tools;

import java.io.*;

import r.*;
import r.data.*;
import r.nodes.*;

public class PrettyPrinter extends BasicVisitor {

    public static final boolean PARENTHESIS = Utils.getProperty("RPrettyPrint.surroundpar", false);

    int level = 0;
    final PrintStream out;
    StringBuilder buff = new StringBuilder();
    private static PrettyPrinter pp = getStringPrettyPrinter();


    public PrettyPrinter(PrintStream stream) {
        out = stream;
    }

    public static String prettyPrint(ASTNode n) {
        pp.print(n);
        return pp.toString();
    }

    public void print(ASTNode n) {
        level = 0;
        n.accept(this);
        flush();
    }

    public void println(ASTNode n) {
        level = 0;
        n.accept(this);
        println("");
    }

    private static PrettyPrinter getStringPrettyPrinter() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        return new PrettyPrinter(new PrintStream(os)) {

            @Override
            public String toString() {
                String str = os.toString();
                os.reset();
                return str;
            }
        };
    }

    private void inc() {
        level++;
    }

    private void dec() {
        if (level == 0) {
            throw new RuntimeException("Unbalanced stack for indentation");
        }
        level--;
    }

    private void print(String arg) {
        buff.append(arg);
    }

    private void println(String arg) {
        for (int i = 0; i < level; i++) {
            out.append('\t');
        }
        buff.append(arg);
        buff.append('\n');
        flush();
    }

    private void flush() {
            out.print(buff);
            out.flush();
            buff.setLength(0);
    }

    @Override
    public void visit(ASTNode n) {
        print("##(TODO: " + n + ")##");
        System.err.println("TODO: " + n);
    }

    @Override
    public void visit(Next n) {
        print("next");
    }

    @Override
    public void visit(Break n) {
        print("break");
    }

    @Override
    public void visit(Sequence n) {
        ASTNode[] exprs = n.getExprs();
        switch (exprs.length) {
            case 0:
                print("{}");
                break;
            case 1:
                print("{ ");
                exprs[0].accept(this);
                print(" }");

                break;
            default:
                println("{");
                inc();
                for (ASTNode e : exprs) {
                    e.accept(this);
                    println("");
                }
                dec();
                print("}");
        }
    }

    @Override
    public void visit(If n) {
        print("if(");
        n.getCond().accept(this);
        print(") ");
        n.getTrueCase().accept(this);
        ASTNode f = n.getFalseCase();
        if (f != null) {
            print(" else ");
            f.accept(this);
        }
    }

    @Override
    public void visit(BinaryOperation op) {
        ASTNode left = op.getLHS();
        ASTNode right = op.getRHS();
        if (PARENTHESIS) {
            print("(");
        }
        // FIXME this is not the right place to do it but we need the parent otherwise
        int precedence = op.getPrecedence();
        if (left.getPrecedence() < precedence && !PARENTHESIS) { // FIXME should be <= if right associative
            print("(");
            left.accept(this);
            print(")");
        } else {
            left.accept(this);
        }
        print(" ");
        print(op.getPrettyOperator());
        print(" ");
        if (right.getPrecedence() < precedence && !PARENTHESIS) { // FIXME should be <= if left associative
            print("(");
            right.accept(this);
            print(")");
        } else {
            right.accept(this);
        }
        if (PARENTHESIS) {
            print(")");
        }
    }

    @Override
    public void visit(UnaryOperation op) {
        if (PARENTHESIS) {
            print("(");
        }
        print(op.getPrettyOperator());
        op.getLHS().accept(this);
        if (PARENTHESIS) {
            print(")");
        }
    }

    @Override
    public void visit(Constant n) {
        print(n.prettyValue());
    }

    @Override
    public void visit(Repeat n) {
        print("repeat ");
        n.getBody().accept(this);
    }

    @Override
    public void visit(While n) {
        print("while(");
        n.getCond().accept(this);
        print(") ");
        n.getBody().accept(this);
    }

    @Override
    public void visit(SimpleAssignVariable n) {
        print(n.getSymbol().pretty());
        print(" <- ");
        n.getExpr().accept(this);
    }

    @Override
    public void visit(AccessVector n) {
        print(n.getVector());
        print(n.isSubset() ? "[" : "[[");
        print(n.getArgs(), true);
        print(n.isSubset() ? "]" : "]]");
    }

    @Override
    public void visit(UpdateVector n) {
        print(n.getVector());
        print(" <- ");
        print(n.getRHS());
    }

    @Override
    public void visit(FunctionCall n) {
        print(n.getName().pretty() + "(");
        print(n.getArgs(), true);
        print(")");
    }

    @Override
    public void visit(Function n) {
        print("function(");
        print(n.getSignature(), false);
        print(") ");
        n.visit_all(this);
    }

    @Override
    public void visit(SimpleAccessVariable n) {
        print(n.getSymbol().pretty());
    }

    private void print(ArgumentList alist, boolean isCall) {
        boolean f = true;
        for (ArgumentList.Entry arg : alist) {
            if (!f) {
                print(", ");
            } else {
                f = false;
            }
            print(arg, isCall);
        }
    }

    private void print(ArgumentList.Entry arg, boolean isCall) {
        RSymbol n = arg.getName();
        ASTNode v = arg.getValue();
        if (n != null) {
            print(n.pretty());
            if (isCall || v != null) {
                print("=");
            }
        }
        if (v != null) {
            v.accept(this);
        }
    }
}
