package r.nodes.truffle;

import com.oracle.truffle.compiler.*;
import com.oracle.truffle.compiler.CompiledObject.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.nodes.*;

public abstract class AbstractCall extends BaseR implements Compilable<Frame> {

    protected final RSymbol[] argNames;
    protected final RNode[] argExprs;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = updateParent(argsExprs);
        this.counter = 0;
        this.compileThreshold = 1;  // FIXME: do something smarter here
    }

    @Stable private CompiledObject<Frame> compiledObject;
    private final int compileThreshold;
    private int counter;

    public int count() {
        return counter++;
    }

    @Override
    public final Object execute(RContext context, Frame frame) { // FIXME: would've been much easier if Compilable supported RContext
        return execute((Context) context, frame);
    }

    @Override
    public final Object execute(Context context, Frame frame) {
        if (context.getCompiler() == null) { // FIXME: handle this through node-rewriting
            return executeHelper(context, frame);
        }
        for (;;) {
            if (compiledObject != null) {
                try {
                    return compiledObject.execute(context, frame);
                } catch (InvalidatedException ex) {
                    compiledObject = null;
                }
            } else {
                if (count() < compileThreshold) {
                    return executeHelper(context, frame);
                } else {
                    compiledObject = context.getCompiler().compile(this, new String("executeHelper"), new Class[] {Context.class, Frame.class});
                }
            }
        }
    }

    @Override
    public abstract Object executeHelper(Context context, Frame frame);

}
