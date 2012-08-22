package r.errors;

import java.io.*;

import r.nodes.*;
import r.nodes.tools.*;

public class ManageError {

    private PrintStream out;
    private PrettyPrinter nodePP;

    public static final String WARNING = "Warning";
    public static final String ERROR = "Error";

    public ManageError(PrintStream errorStream) {
        setOutputStream(errorStream);
        nodePP = PrettyPrinter.getStringPrettyPrinter();
    }

    public void setOutputStream(PrintStream errorStream) {
        if (out != null) {
            out.flush(); // FIXME is this flush needed ?
        }
        out = errorStream;
    }

    public void warning(ASTNode expr, String msg) {
        displayMessage(ManageError.WARNING, expr, msg);
    }

    public void warning(RError err) {
        displayMessage(ManageError.WARNING, (err instanceof RError.RErrorInExpr) ? ((RError.RErrorInExpr) err).getErrorNode() : null, err.getMessage());
    }

    public void error(ASTNode expr, String msg) {
        displayMessage(ManageError.ERROR, expr, msg);
    }

    public void error(RError err) {
        displayMessage(ManageError.ERROR, (err instanceof RError.RErrorInExpr) ? ((RError.RErrorInExpr) err).getErrorNode() : null, err.getMessage());
    }

    private void displayMessage(String prefix, ASTNode cause, String msg) {
        StringBuilder str = new StringBuilder(prefix);
        if (cause != null) {
            nodePP.print(cause);
            str.append(" in ").append(nodePP.toString());
        }
        str.append(": ");
        str.append(msg);
        System.err.println(str);
    }
}
