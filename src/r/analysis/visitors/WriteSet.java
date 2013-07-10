package r.analysis.visitors;

import com.oracle.truffle.api.nodes.Node;
import r.analysis.NodeVisitor;
import r.data.RSymbol;
import r.data.internal.FunctionImpl;
import r.fastr;
import r.nodes.truffle.*;

import java.util.*;


public class WriteSet implements NodeVisitor {

    public static WriteSet analyze(RNode node, FunctionImpl fimpl) {
        WriteSet res = new WriteSet(fimpl);
        node.linearVisit(res);
        return res;
    }

    private boolean valid = true;

    HashSet<RSymbol> locals = new HashSet<>();
    HashSet<RSymbol> topLevel = new HashSet<>();
    HashSet<RSymbol> extension = new HashSet<>();
    HashSet<RSymbol> sassign = new HashSet<>();

    final FunctionImpl fimpl;

    WriteSet(FunctionImpl fimpl) {
        this.fimpl = fimpl;
    }


    @Override
    public boolean visit(RNode node) {
        if (node instanceof WriteVariable) {
            visitWriteVariable((WriteVariable) node);
        } else if (node instanceof SuperWriteVariable) {
            visitSuperWriteVariable((SuperWriteVariable) node);
        } else if ((node instanceof FunctionCall) || (node instanceof FunctionCall.StableBuiltinCall)) {
            // at the moment we do not see or know the extend of the function calls, therefore we mark the analysis as
            // invalid
            fastr.println("Function or builtin call found, the analysis is invalid.");
            valid = false;
        }
        return true;
    }

    void visitWriteVariable(WriteVariable node) {
        if (node instanceof WriteVariable.Local)
            locals.add(node.symbol);
        else if (node instanceof WriteVariable.TopLevel)
            topLevel.add(node.symbol);
        else if (node instanceof WriteVariable.Extension)
            extension.add(node.symbol);
        else {
            fastr.println("Unknown variable write type encountered -- " + node.getClass().getName());
            valid = false;
        }
    }

    void visitSuperWriteVariable(SuperWriteVariable node) {
        sassign.add(node.symbol);
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isEmpty() {
        return valid &&  locals.isEmpty() && topLevel.isEmpty() && extension.isEmpty() && sassign.isEmpty();
    }


    /** Returns true if the only variables being written to are the arguments of the function itself. */
    public boolean isArgumentsOnly() {
        if (isValid() && topLevel.isEmpty() && extension.isEmpty() && sassign.isEmpty()) {
            HashSet<RSymbol> params = new HashSet<>();
            Collections.addAll(params, fimpl.getParamNames());
            return  (params.containsAll(locals));
        }
        return false;
    }

}
