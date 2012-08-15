package r.nodes.tools;

import java.io.*;
import r.nodes.*;

public class PrettyPrinter extends BasicVisitor {

    int level = 0;
    final PrintStream out;
    StringBuilder buff = new StringBuilder();

    public PrettyPrinter(PrintStream stream) {
        out = stream;
    }

    public void print(Node n) {
        level = 0;
        n.accept(this);
        println("");
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
        out.println(buff);
        buff.setLength(0);
    }

    @Override
    public void visit(Node n) {
        println("## TODO: " + n);
        System.err.println("TODO: " + n);
    }

    @Override
    public void visit(Sequence n) {
        println("{");
        inc();
        for (Node e : n.getExprs()) {
            e.accept(this);
            println("");
        }
        dec();
        print("}");
    }

    @Override
    public void visit(If n) {
        print("if(");
        n.getCond().accept(this);
        print(") ");
        n.getTrueCase().accept(this);
        Node f = n.getFalseCase();
        if (f != null) {
            print(" else ");
            f.accept(this);
        }
    }

    @Override
    public void visit(BinaryOperation op) {
        Node left = op.getLHS();
        Node right = op.getRHS();
        // FIXME this is not the right place to do it but we need the parent otherwise
        int precedence = op.getPrecedence();
        if (left.getPrecedence() < precedence) {
            print("(");
            left.accept(this);
            print(")");
        } else {
            left.accept(this);
        }
        print(" ");
        print(op.getPrettyOperator());
        print(" ");
        if (right.getPrecedence() < precedence) {
            print("(");
            right.accept(this);
            print(")");
        } else {
            right.accept(this);
        }
    }

    @Override
    public void visit(UnaryOperation op) {
        print(op.getPrettyOperator());
        op.getLHS().accept(this);
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
        println("");
    }
}
