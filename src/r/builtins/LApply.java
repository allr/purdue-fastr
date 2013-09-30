package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.FunctionCall;
import r.runtime.*;

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
        ValueProvider[] constantArgProviders = new ValueProvider[cnArgs];
        int j = 0;
        for (int i = 0; i < names.length; i++) {
            if (ia.position("X") == i || ia.position("FUN") == i) {
                continue;
            }
            cnNames[1 + j] = names[i];
            ValueProvider vp = new ValueProvider(call);
            cnExprs[1 + j] = vp;
            constantArgProviders[j] = vp;
            j++;
        }
        RNode funExpr = exprs[ia.position("FUN")];
        CallableProvider callableProvider = new CallableProvider(funExpr.getAST(), funExpr);
        RNode callNode = FunctionCall.getFunctionCall(call, callableProvider, cnNames, cnExprs);
        return new Lapply(call, names, exprs, callNode, firstArgProvider, constantArgProviders, callableProvider, ia.position("X"), ia.position("FUN"));
    }

    // !!! this node must not rewrite itself
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

    // will never rewrite itself
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

        public void matchAndSet(Frame frame, RAny arg) {
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

        ValueProvider firstArgProvider;         // !!! not a child, just a shortcut into arguments
        ValueProvider[] constantArgProviders;   // !!! not a child, just a shortcut into arguments
        CallableProvider callableProvider;      // !!! not a child, just a shortcut into callNode
        @Child RNode callNode;
        final int xPosition;
        final int funPosition;

        public Lapply(ASTNode call, RSymbol[] names, RNode[] exprs, RNode callNode, ValueProvider firstArgProvider, ValueProvider[] constantArgProviders, CallableProvider callableProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.callableProvider = callableProvider;  // !!! no adopt
            this.firstArgProvider = firstArgProvider;  // !!! no adopt
            this.constantArgProviders = constantArgProviders; // !!! no adopt
            this.callNode = adoptChild(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            assert Utils.check(oldNode != firstArgProvider);
            assert Utils.check(oldNode != callableProvider);
            if (callNode == oldNode) {
                callNode = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

        public RAny generic(Frame frame, RAny argx, Lapply lapply) {
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
                lapply.firstArgProvider.setValue(isList ? l.getRAny(i) : x.boxedGet(i));
                content[i] = (RAny) lapply.callNode.execute(frame);
            }
            return RList.RListFactory.getFor(content, null, isList ? l.names() : x.names());
        }

        public Specialized createSpecialized(RAny argxTemplate) {
            if (argxTemplate instanceof RList) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, RAny argx, Lapply lapply) throws SpecializationException {
                        if (!(argx instanceof RList)) { throw new SpecializationException(null); }
                        RList x = (RList) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            lapply.firstArgProvider.setValue(x.getRAny(i));
                            content[i] = (RAny) lapply.callNode.execute(frame);
                        }
                        return RList.RListFactory.getFor(content, null, x.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, constantArgProviders, callableProvider, xPosition, funPosition, a);
            }
            if (argxTemplate instanceof RArray) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, RAny argx, Lapply lapply) throws SpecializationException {
                        if (argx instanceof RList || !(argx instanceof RArray)) { throw new SpecializationException(null); }
                        RArray x = (RArray) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            lapply.firstArgProvider.setValue(x.boxedGet(i));
                            content[i] = (RAny) lapply.callNode.execute(frame);
                        }
                        return RList.RListFactory.getFor(content, null, x.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, constantArgProviders, callableProvider, xPosition, funPosition, a);
            }
            return null;
        }

        public Specialized createGeneric() {
            ApplyFunc a = new ApplyFunc() {
                @Override public RAny apply(Frame frame, RAny argx, Lapply lapply) throws SpecializationException {
                    return generic(frame, argx, lapply);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, constantArgProviders, callableProvider, xPosition, funPosition, a);
        }

        public RAny doApply(Frame frame, RAny[] args) {
            try {
                throw new SpecializationException(null);
            } catch (SpecializationException e) {
                Specialized sn = createSpecialized(args[xPosition]);
                if (sn != null) {
                    replace(sn, "install Specialized from Lapply");
                    return sn.doApply(frame, args);
                } else {
                    sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply");
                    return sn.doApply(frame, args);
                }
            }
        }

        @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
            return doApply(frame, args);
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(Frame frame, RAny argx, Lapply lapply) throws SpecializationException;
        }

        static class Specialized extends Lapply {
            final ApplyFunc apply;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, RNode callNode, ValueProvider firstArgProvider, ValueProvider[] constantArgProviders, CallableProvider callableProvider, int xPosition, int funPosition,
                    ApplyFunc apply) {
                super(call, names, exprs, callNode, firstArgProvider, constantArgProviders, callableProvider, xPosition, funPosition);
                this.apply = apply;
            }

            @Override public RAny doApply(Frame frame, RAny[] args) {
                int j = 0;
                RAny argx = null;
                for (int i = 0; i < args.length; i++) {
                    if (i == xPosition) {
                        argx = args[i];
                    } else if (i == funPosition) {
                        callableProvider.matchAndSet(frame, args[i]);
                    } else {
                        constantArgProviders[j].setValue(args[i]);
                        j++;
                    }
                }
                try {
                    return apply.apply(frame, argx, this);
                } catch (SpecializationException e) {
                    Specialized sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply.Specialized");
                    return sn.doApply(frame, args);
                }
            }
        }
    }

    // FIXME: should move these iterators to RArray ? (though note that the names semantics is sapply specific)
    public abstract static class ArgIterator {
        ValueProvider argProvider; // !! not a child
        int size;

        public abstract void reset(ValueProvider provider, RAny source) throws SpecializationException;

        public abstract void setNext();

        public int size() {
            return size;
        }

        public abstract RArray.Names names();

        public abstract RString stringNames();

        public abstract boolean hasNames();

        public static ArgIterator create(RAny sourceTemplate) {
            if (IntImpl.RIntSimpleRange.isInstance(sourceTemplate)) { return new IntSimpleRange(); }
            if (IntImpl.RIntSequence.isInstance(sourceTemplate)) { return new IntSequence(); }
            if (sourceTemplate instanceof RList) { return new List(); }
            if (sourceTemplate instanceof RString) { return new StringArray(); }
            if (sourceTemplate instanceof RArray) { return new NonlistNonstringArray(); }
            return new Generic(); // this will fail later when called
        }

        public static final class IntSequence extends ArgIterator {
            int step;
            int next;

            @Override public void reset(ValueProvider provider, RAny source) throws SpecializationException {
                if (!(IntImpl.RIntSequence.isInstance(source))) { throw new SpecializationException(null); }
                IntImpl.RIntSequence seq = IntImpl.RIntSequence.cast(source);
                this.argProvider = provider; // !! not adopt
                next = seq.from();
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

        public static final class IntSimpleRange extends ArgIterator {
            int next;

            @Override public void reset(ValueProvider provider, RAny source) throws SpecializationException {
                if (!(IntImpl.RIntSimpleRange.isInstance(source))) { throw new SpecializationException(null); }
                IntImpl.RIntSimpleRange seq = IntImpl.RIntSimpleRange.cast(source);
                this.argProvider = provider; // !! not adopt
                next = 1;
                size = seq.size();
            }

            @Override public void setNext() {
                argProvider.setValue(RInt.RIntFactory.getScalar(next));
                next++;
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

            @Override public void reset(ValueProvider provider, RAny source) throws SpecializationException {
                if (!(source instanceof RString)) { throw new SpecializationException(null); }
                this.argProvider = provider; // !! not adopt
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

            @Override public void reset(ValueProvider provider, RAny source) throws SpecializationException {
                if (source instanceof RList || source instanceof RString || !(source instanceof RArray)) { throw new SpecializationException(null); }
                this.argProvider = provider; // !! not adopt
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

            @Override public void reset(ValueProvider provider, RAny source) throws SpecializationException {
                if (!(source instanceof RList)) { throw new SpecializationException(null); }
                this.argProvider = provider; // !! not adopt
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
                this.argProvider = provider; // !! not adopt
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
