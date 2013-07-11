package r.analysis.visitors;

import com.oracle.truffle.api.frame.*;
import r.analysis.NodeVisitor;
import r.data.*;
import r.fastr;
import r.nodes.truffle.*;

import java.util.HashMap;

public class LocalReadWriteFramelessReplacer implements NodeVisitor {

    final HashMap<RSymbol, Integer> args;
    final RAny[] locals;

    public static RAny[] execute(FrameDescriptor fd, RSymbol[] args, RNode inlinedBody) {
        HashMap<RSymbol, Integer> newArgs = new HashMap<>();
        for (int i = 0; i < args.length; ++i) {
            newArgs.put(args[i], i);
        }
        RAny[] locals = new RAny[args.length];
        new LocalReadWriteFramelessReplacer(newArgs, locals, inlinedBody);
        return locals;
    }

    public LocalReadWriteFramelessReplacer(HashMap<RSymbol, Integer> args, RAny[] locals, RNode node) {
        this.args = args;
        this.locals = locals;
        node.linearVisit(this);
    }

    @Override
    public boolean visit(RNode node) {
        if (node instanceof WriteVariable.Local) {

        } else if (node instanceof ReadVariable.SimpleLocal) {
            ReadVariable.SimpleLocal n = (ReadVariable.SimpleLocal) node;
            node.replace(new ReadVariable.InlinedLocal(n.getAST(), n.symbol, locals, args.get(n.symbol)));
            fastr.println("  replacing ReadVariable.InlinedLocal for variable " + n.symbol.name());
        }
        return true;
    }
}
