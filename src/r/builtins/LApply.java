package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.FunctionCall;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

// FIXME: only a subset of R functionality
// TODO: specializations for different argument types done in sapply can be also used in lapply
/**
 * "lapply" returns a list of the same length as X, each element of which is the result of applying FUN to the
 * corresponding element of X.
 *
 * <pre>
 * X -- a vector (atomic or list) or an expression object. Other objects
 *      (including classed objects) will be coerced by base::as.list.
 * FUN -- the function to be applied to each element of X. In the case of functions like
 *        +, %*%, the function name must be backquoted or quoted.
 *  ... -- optional arguments to FUN.
 * </pre>
 */
final class LApply extends CallFactory {

    static final CallFactory _ = new LApply("lapply", new String[]{"X", "FUN", "..."}, new String[]{"X", "FUN"});

    private LApply(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        // lapply will create a call node, let's prepare names and expressions (first is the variable)
        int cnArgs = 1 + names.length - 2; // "-2" because both FUN and X are required
        RSymbol[] cnNames = new RSymbol[cnArgs];
        RNode[] cnExprs = new RNode[cnArgs];
        cnNames[0] = null;
        ValueProvider firstArgProvider = new ValueProvider(call);
        cnExprs[0] = firstArgProvider;
        int j = 0;
        for (int i = 0; i < names.length; i++) {
            if (ia.position("X") == i || ia.position("FUN") == i) {
                continue;
            }
            cnNames[1 + j] = names[i];
            cnExprs[1 + j] = exprs[i];
            j++;
        }
        CallableProvider callableProvider = new CallableProvider(call, exprs[ia.position("FUN")]);
        FunctionCall callNode = FunctionCall.getFunctionCall(call, callableProvider, cnNames, cnExprs);
        return new Lapply(call, names, exprs, callNode, firstArgProvider, callableProvider, ia.position("X"), ia.position("FUN"));
    }

    public static class ValueProvider extends BaseR {
        RAny value;

        public ValueProvider(ASTNode ast) {
            super(ast);
        }

        @Override public final Object execute(Frame frame) {
            return value;
        }

        public void setValue(RAny value) {
            this.value = value;
        }
    }

    public static class CallableProvider extends BaseR {
        RCallable value;
        final RSymbol callsiteSymbol;

        public CallableProvider(ASTNode ast, RNode functionExpr) {
            super(ast);
            ASTNode feAST = functionExpr.getAST();
            if (feAST instanceof SimpleAccessVariable) {
                callsiteSymbol = ((SimpleAccessVariable) feAST).getSymbol();
            } else {
                callsiteSymbol = null;
            }
        }

        @Override public final Object execute(Frame frame) {
            return value;
        }

        public void matchAndSet(ASTNode setAst, Frame frame, RAny arg) {
            if (arg instanceof RCallable) {
                value = (RCallable) arg;
                return;
            }
            if (arg instanceof RString) { // FIXME: could save some performance through node-rewriting and/or caching, argument will often be a constant
                RString svalue = (RString) arg;
                if (svalue.size() != 1) { throw RError.getNotFunction(ast, arg); } // FIXME: GNU-R will give ast for the function argument
                RSymbol symbol = RSymbol.getSymbol(svalue.getString(0));
                value = MatchCallable.matchGeneric(ast, frame, symbol);
                return;
            }
            if (callsiteSymbol != null) {
                value = MatchCallable.matchGeneric(ast, frame, callsiteSymbol);
                return;
            }
            throw RError.getNotFunction(ast, arg); // FIXME: GNU-R will give ast for the function argument
        }
    }

    public static class Lapply extends Builtin {

        @Child ValueProvider firstArgProvider;
        @Child CallableProvider callableProvider;
        @Child FunctionCall callNode;
        final int xPosition;
        final int funPosition;

        public Lapply(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.callableProvider = adoptChild(callableProvider);
            this.firstArgProvider = adoptChild(firstArgProvider);
            this.callNode = adoptChild(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        public static RAny generic(Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) {
            if (!(argx instanceof RArray)) { throw Utils.nyi("unsupported type"); }
            RArray x = (RArray) argx;
            int xsize = x.size();
            boolean isList = false;
            RList l = null;
            if (x instanceof RList) {
                l = (RList) x;
                isList = true;
            }
            RAny[] content = new RAny[xsize];
            for (int i = 0; i < xsize; i++) {
                firstArgProvider.setValue(isList ? l.getRAny(i) : x.boxedGet(i));
                content[i] = (RAny) callNode.execute(frame);
            }
            return RList.RListFactory.getFor(content, null, isList ? l.names() : x.names());
        }

        public Specialized createSpecialized(RAny argxTemplate) {
            if (argxTemplate instanceof RList) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        if (!(argx instanceof RList)) { throw new UnexpectedResultException(null); }
                        RList x = (RList) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.getRAny(i));
                            content[i] = (RAny) callNode.execute(frame);
                        }
                        return RList.RListFactory.getFor(content, null, x.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, a);
            }
            if (argxTemplate instanceof RArray) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        if (argx instanceof RList || !(argx instanceof RArray)) { throw new UnexpectedResultException(null); }
                        RArray x = (RArray) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.boxedGet(i));
                            content[i] = (RAny) callNode.execute(frame);
                        }
                        return RList.RListFactory.getFor(content, null, x.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, a);
            }
            return null;
        }

        public Specialized createGeneric() {
            ApplyFunc a = new ApplyFunc() {
                @Override public RAny apply(Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                    return generic(frame, argx, firstArgProvider, callNode);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, a);
        }

        public RAny doApply(Frame frame, RAny argx, RAny argfun) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized sn = createSpecialized(argx);
                if (sn != null) {
                    replace(sn, "install Specialized from Lapply");
                    return sn.doApply(frame, argx, argfun);
                } else {
                    sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply");
                    return sn.doApply(frame, argx, argfun);
                }
            }
        }

        @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
            RAny argx = args[xPosition];
            RAny argfun = args[funPosition];
            return doApply(frame, argx, argfun);
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException;
        }

        class Specialized extends Lapply {
            final ApplyFunc apply;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition,
                    ApplyFunc apply) {
                super(call, names, exprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition);
                this.apply = apply;
            }

            @Override public RAny doApply(Frame frame, RAny argx, RAny argfun) {
                try {
                    callableProvider.matchAndSet(ast, frame, argfun);
                    return apply.apply(frame, argx, firstArgProvider, callNode);
                } catch (UnexpectedResultException e) {
                    Specialized sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply.Specialized");
                    return sn.doApply(frame, argx, argfun);
                }
            }
        }
    }

    // FIXME: should move these iterators to RArray ? (though note that the names semantics is sapply specific)
    public abstract static class ArgIterator {
        ValueProvider argProvider;
        int size;

        public abstract void reset(ValueProvider provider, RAny source) throws UnexpectedResultException;

        public abstract void setNext();

        public int size() {
            return size;
        }

        public abstract RArray.Names names();

        public abstract RString stringNames();

        public abstract boolean hasNames();

        public static ArgIterator create(RAny sourceTemplate) {
            if (sourceTemplate instanceof IntImpl.RIntSequence) { return new IntSequence(); }
            if (sourceTemplate instanceof RList) { return new List(); }
            if (sourceTemplate instanceof RString) { return new StringArray(); }
            if (sourceTemplate instanceof RArray) { return new NonlistNonstringArray(); }
            return new Generic(); // this will fail later when called
        }

        public static final class IntSequence extends ArgIterator {
            int to;
            int step;
            int next;

            @Override public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof IntImpl.RIntSequence)) { throw new UnexpectedResultException(null); }
                IntImpl.RIntSequence seq = (IntImpl.RIntSequence) source;
                this.argProvider = provider;
                next = seq.from();
                to = seq.to();
                step = seq.step();
                size = seq.size();
            }

            @Override public void setNext() {
                argProvider.setValue(RInt.RIntFactory.getScalar(next));
                next += step;
            }

            @Override public RArray.Names names() {
                return null;
            }

            @Override public RString stringNames() {
                return null;
            }

            @Override public boolean hasNames() {
                return false;
            }
        }

        public static final class StringArray extends ArgIterator {
            RString string;
            int i;

            @Override public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof RString)) { throw new UnexpectedResultException(null); }
                this.argProvider = provider;
                this.string = (RString) source;
                i = 0;
                size = string.size();
            }

            @Override public void setNext() {
                argProvider.setValue(string.boxedGet(i));
                i++;
            }

            @Override public RArray.Names names() {
                RArray.Names snames = string.names();
                if (snames != null) {
                    return snames;
                } else {
                    return RArray.Names.create(RSymbol.getSymbols(string));
                }
            }

            @Override public RString stringNames() {
                RArray.Names snames = string.names();
                if (snames != null) {
                    return RString.RStringFactory.getFor(Convert.symbols2strings(snames.sequence()));
                } else {
                    return string;
                }
            }

            @Override public boolean hasNames() {
                return size > 0;
            }

        }

        public static final class NonlistNonstringArray extends ArgIterator {
            RArray array;
            int i;

            @Override public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (source instanceof RList || source instanceof RString || !(source instanceof RArray)) { throw new UnexpectedResultException(null); }
                this.argProvider = provider;
                this.array = (RArray) source;
                i = 0;
                size = array.size();
            }

            @Override public void setNext() {
                argProvider.setValue(array.boxedGet(i));
                i++;
            }

            @Override public RArray.Names names() {
                return array.names();
            }

            @Override public RString stringNames() {
                return RString.RStringFactory.getFor(Convert.symbols2strings(names().sequence()));
            }

            @Override public boolean hasNames() {
                return array.names() != null;
            }
        }

        public static final class List extends ArgIterator {
            RList list;
            int i;

            @Override public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof RList)) { throw new UnexpectedResultException(null); }
                this.argProvider = provider;
                this.list = (RList) source;
                i = 0;
                size = list.size();
            }

            @Override public void setNext() {
                argProvider.setValue(list.getRAny(i));
                i++;
            }

            @Override public RArray.Names names() {
                return list.names();
            }

            @Override public RString stringNames() {
                return RString.RStringFactory.getFor(Convert.symbols2strings(names().sequence()));
            }

            @Override public boolean hasNames() {
                return list.names() != null;
            }
        }

        public static final class Generic extends ArgIterator {
            RArray array;
            int i;

            @Override public void reset(ValueProvider provider, RAny source) {
                if (!(source instanceof RArray)) {
                    Utils.nyi("unsupported type");
                }
                this.argProvider = provider;
                this.array = (RArray) source;
                i = 0;
                size = array.size();
            }

            @Override public void setNext() {
                argProvider.setValue(array.boxedGet(i));
                i++;
            }

            @Override public RArray.Names names() {
                RArray.Names anames = array.names();
                if (anames != null) { return anames; }
                if (array instanceof RString) { return RArray.Names.create(RSymbol.getSymbols((RString) array)); }
                return null;
            }

            @Override public RString stringNames() {
                RArray.Names anames = array.names();
                if (anames != null) { return RString.RStringFactory.getFor(Convert.symbols2strings(array.names().sequence())); }
                return (RString) array;
            }

            @Override public boolean hasNames() {
                return array.names() != null || (array instanceof RString && size > 0);
            }
        }
    }
}
