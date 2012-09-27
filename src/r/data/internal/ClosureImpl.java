package r.data.internal;

import com.oracle.truffle.compiler.*;
import com.oracle.truffle.compiler.CompiledObject.*;
import com.oracle.truffle.nodes.control.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.nodes.truffle.*;

public class ClosureImpl extends BaseObject implements RClosure {

    final Frame environment;
    final RFunction function;

    final int nparams; // to make Truffle happy
    final int nslots;
    final RNode body;
    @Stable RNode[] dflParams;

    @Stable private CompiledObject<Object[]> compiledObject;
    private final int compileThreshold;
    private int counter;

    public ClosureImpl(RFunction function, Frame environment) {
        this.function = function;
        this.nparams = function.nparams();
        this.body = function.body();
        this.nslots = function.nlocals() + RFrame.RESERVED_SLOTS;
        this.dflParams = function.paramValues();
        this.environment = environment;
        this.counter = 0;
        this.compileThreshold = 1;  // FIXME: do something smarter here
    }

    @Override
    public String pretty() {
        Utils.check(function != null);
        StringBuilder str = new StringBuilder();
        str.append(function.getSource().toString());
        if (environment != null) {
            str.append(" <ENVIRONMENT " + environment + ">");
        }
        return str.toString();
    }

    @Override
    public RLogical asLogical() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RInt asInt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RDouble asDouble() {
        return null;
    }

    @Override
    public Frame environment() {
        return environment;
    }

    @Override
    public RFunction function() {
        return function;
    }

    public int count() {
        return counter++;
    }

    @Override
    public Object call(Context context, Object[] args) {
        return execute(context, args);
    }

    @Override
    public final Object execute(Context context, Object[] args) {
        if (context.getCompiler() == null) { // FIXME: handle this through node-rewriting
            return executeHelper(context, args);
        }
        for (;;) {
            if (compiledObject != null) {
                try {
                    return compiledObject.execute(context, args);
                } catch (InvalidatedException ex) {
                    compiledObject = null;
                }
            } else {
                if (count() < compileThreshold) {
                    return executeHelper(context, args);
                } else {
                    compiledObject = context.getCompiler().compile(this, new String("executeHelper"), new Class[] {Context.class, Object[].class});
                }
            }
        }
    }

    @Override
    public Object executeHelper(Context context, Object[] args) {
            // Frame frame = RFrame.create(function, environment);
            // but cannot call that directly because Truffle would miss the Frame allocation
        Frame frame = new Frame(nslots, environment);
        frame.setObject(RFrame.FUNCTION_SLOT, function);
        copyArgs(context, frame, args);

        Object res;
        try {
            res = body.execute(context, frame);
        } catch (ReturnException re) {
            res = RFrame.getReturnValue(frame);
        }
        return res;
    }

    @ExplodeLoop
    private void copyArgs(Context context, Frame frame, final Object[] args) {
        for (int i = 0; i < nparams; i++) {
            Object value = args[i];
            if (value == null) {
                RNode n = dflParams[i];
                if (n != null) {
                    value = n.execute(context, frame);
                }
            }
            RFrame.writeAt(frame, i, value);
        }
    }

    @Override
    public com.oracle.truffle.Function getFunction() {
        return null;
    }

    @Override
    public long getCallCount() {
        return 0;
    }

    @Override
    public Frame getDeclarationFrame() {
        return null;
    }
}
