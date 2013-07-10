package r.analysis.visitors;

import com.oracle.truffle.api.frame.*;
import r.analysis.NodeVisitor;
import r.data.RSymbol;
import r.data.internal.FunctionImpl;
import r.fastr;
import r.nodes.truffle.*;

import java.util.HashMap;

/** Visits the given execution tree and replaces all local reads and writes for the function with new slots created by
 * the replacer itself. It is not required to keep the slots anywhere else.
 */
public class LocalReadWriteReplacer implements NodeVisitor {


    final HashMap<RSymbol, FrameSlot> args;

    public static FrameSlot[] execute(FrameDescriptor fd, RSymbol[] args, RNode inlinedBody) {
        FrameSlot[] slots = new FrameSlot[args.length];
        HashMap<RSymbol, FrameSlot> newArgs = new HashMap<>();
        for (int i = 0; i < slots.length; ++i) {
            slots[i] = fd.addFrameSlot(new Object(), FrameSlotKind.Object);
            newArgs.put(args[i], slots[i]);
        }
        new LocalReadWriteReplacer(newArgs, inlinedBody);
        return slots;
    }

    public LocalReadWriteReplacer(HashMap<RSymbol, FrameSlot> args, RNode node) {
        this.args = args;
        node.linearVisit(this);
    }

    @Override
    public boolean visit(RNode node) {
        if (node instanceof WriteVariable.Local) {

        } else if (node instanceof ReadVariable.SimpleLocal) {
            ReadVariable.SimpleLocal n = (ReadVariable.SimpleLocal) node;
            node.replace(new ReadVariable.SimpleLocal(n.getAST(), n.symbol, args.get(n.symbol)));
            fastr.println("  replacing ReadVariable.SimpleLocal for variable "+n.symbol.name());
        }
        return true;
    }
}
