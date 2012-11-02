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
    @ContentStable final RNode[] dflParams;

    // experimental feature only, won't work with Truffle
    private static final boolean CACHING_FRAMES = false;
    Frame cachedFrame;

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
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble() {
        Utils.nyi();
        return null;
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
    }

    @Override
    public RList asList() {
        Utils.nyi();
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
    public final Object call(Context context, Object[] args) {
        return execute(context, args);
    }

    // non-truffle
    @Override
    public Object trivialCall(RContext context, Object arg0, Object arg1) {
        Frame frame = null;
        if (CACHING_FRAMES) {
            frame = cachedFrame;
            if (cachedFrame == null) {
                 frame = new Frame(nslots, environment);
            } else {
                cachedFrame = null; // for recursive calls
            }
        } else {
            frame = new Frame(nslots, environment);
        }

        frame.setLong(RFrame.RESERVED_SLOTS + 0, 0);
        frame.setLong(RFrame.RESERVED_SLOTS + 1, 0);

        frame.setObject(RFrame.FUNCTION_SLOT, function);
        RFrame.writeAt(frame, 0, arg0);
        RFrame.writeAt(frame, 1, arg1);
        Object res;
        try {
            res = body.execute(context, frame);
        } catch (ReturnException re) {
            res = RFrame.getReturnValue(frame);
        }
        if (CACHING_FRAMES) {
            cachedFrame = frame;
        }
        return res;
    }

    // non-truffle
    @Override
    public Object trivialCall(RContext context, Object arg0) {
        Frame frame = null;
        if (CACHING_FRAMES) {
            frame = cachedFrame;
            if (cachedFrame == null) {
                 frame = new Frame(nslots, environment);
            } else {
                cachedFrame = null; // for recursive calls
            }
        } else {
            frame = new Frame(nslots, environment);
        }

        frame.setLong(RFrame.RESERVED_SLOTS + 0, 0);

        frame.setObject(RFrame.FUNCTION_SLOT, function);
        RFrame.writeAt(frame, 0, arg0);
        Object res;
        try {
            res = body.execute(context, frame);
        } catch (ReturnException re) {
            res = RFrame.getReturnValue(frame);
        }
        if (CACHING_FRAMES) {
            cachedFrame = frame;
        }
        return res;
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
    public final Object executeHelper(Context context, Object[] args) {
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
