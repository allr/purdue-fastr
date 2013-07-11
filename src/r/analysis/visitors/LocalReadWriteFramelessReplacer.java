package r.analysis.visitors;

import com.oracle.truffle.api.frame.*;
import r.analysis.NodeVisitor;
import r.data.*;
import r.fastr;
import r.nodes.truffle.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;


// TODO not graal friendly
/** Bypasses the frame and stores the local variables in a RAny array. On Hotspot this is faster, but is generally not
 * graal friendly and will fail for recursive functions. However recursive functions are not supported yet.
 */
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

    /** Replaces local writes and reads with frameless reads and writes.
     */
    @Override
    public boolean visit(RNode node) {
        if (node instanceof WriteVariable.Local) {
            WriteVariable.Local n = (WriteVariable.Local) node;
            node.replace(new WriteVariable.Frameless(n.getAST(), n.symbol, n.getExpr(),  locals, args.get(n.symbol)));
            fastr.println("  replacing WriteVariable.Frameless for variable " + n.symbol.name());
        } else if (node instanceof ReadVariable.SimpleLocal) {
            ReadVariable.SimpleLocal n = (ReadVariable.SimpleLocal) node;
            node.replace(new ReadVariable.Frameless(n.getAST(), n.symbol, locals, args.get(n.symbol)));
            fastr.println("  replacing ReadVariable.Frameless for variable " + n.symbol.name());
        } else if (node instanceof ReadVariable) {
            throw new NotImplementedException(); // other than simple local reads not yet implemented, inlining will fail
        }
        return true;
    }
}
