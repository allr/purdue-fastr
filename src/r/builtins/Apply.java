package r.builtins;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.FunctionCall;

// FIXME: only a subset of R functionality
// TODO: specializations for different argument types done in sapply can be also used in lapply

public class Apply {
    private static final String[] paramNames = new String[]{"X", "FUN"};

    private static final int IX = 0;
    private static final int IFUN = 1;

    private static final boolean DEBUG_AP = false;

    public static class ValueProvider extends BaseR {
        RAny value;

        public ValueProvider(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
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

        @Override
        public final Object execute(RContext context, Frame frame) {
            return value;
        }

        public void matchAndSet(ASTNode setAst, RContext context, Frame frame, RAny arg) {
            if (arg instanceof RCallable) {
                value = (RCallable) arg;
                return;
            }
            if (arg instanceof RString) { // FIXME: could save some performance through node-rewriting and/or caching, argument will often be a constant
                RString svalue = (RString) arg;
                if (svalue.size() != 1) {
                    throw RError.getNotFunction(ast);
                }
                RSymbol symbol = RSymbol.getSymbol(svalue.getString(0));
                value = MatchCallable.matchGeneric(ast, context, frame, symbol); // will produce different error message from GNU-R
                return;
            }
            if (callsiteSymbol != null) {
                value = MatchCallable.matchGeneric(ast, context, frame, callsiteSymbol);
                return;
            }
            throw RError.getNotFunction(setAst);
        }
    }

    public static final CallFactory LAPPLY_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            int[] paramPositions = a.paramPositions;

            // lapply will create a call node, let's prepare names and expressions (first is the variable)

            int cnArgs = 1 + names.length - 2; // "-2" because both FUN and X are required
            RSymbol[] cnNames = new RSymbol[cnArgs];
            RNode[] cnExprs = new RNode[cnArgs];
            cnNames[0] = null;
            ValueProvider firstArgProvider = new ValueProvider(call);
            cnExprs[0] = firstArgProvider;
            int j = 0;
            for (int i = 0; i < names.length; i++) {
                if (paramPositions[IX] == i || paramPositions[IFUN] == i) {
                    continue;
                }
                cnNames[1 + j] = names[i];
                cnExprs[1 + j] = exprs[i];
                j++;
            }

            final CallableProvider callableProvider = new CallableProvider(call, exprs[paramPositions[IFUN]]);
            final FunctionCall callNode = FunctionCall.getFunctionCall(call, callableProvider, cnNames, cnExprs);
            return new Lapply(call, names, exprs, callNode, firstArgProvider, callableProvider, paramPositions[IX], paramPositions[IFUN]);
        }
    };

    public static final CallFactory SAPPLY_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            int[] paramPositions = a.paramPositions;

            // for now this initialization is copy-paste from lapply, but a full version of sapply would be different
            // sapply will create a call node, let's prepare names and expressions (first is the variable)

            int cnArgs = 1 + names.length - 2; // "-2" because both FUN and X are required
            RSymbol[] cnNames = new RSymbol[cnArgs];
            RNode[] cnExprs = new RNode[cnArgs];
            cnNames[0] = null;
            ValueProvider firstArgProvider = new ValueProvider(call);
            cnExprs[0] = firstArgProvider;
            int j = 0;
            for (int i = 0; i < names.length; i++) {
                if (paramPositions[IX] == i || paramPositions[IFUN] == i) {
                    continue;
                }
                cnNames[1 + j] = names[i];
                cnExprs[1 + j] = exprs[i];
                j++;
            }

            final CallableProvider callableProvider = new CallableProvider(call, exprs[paramPositions[IFUN]]);
            final FunctionCall callNode = FunctionCall.getFunctionCall(call, callableProvider, cnNames, cnExprs);
            return new Sapply(call, names, exprs, callNode, firstArgProvider, callableProvider, paramPositions[IX], paramPositions[IFUN]);
        }
    };

    public static class Lapply extends BuiltIn {

        @Stable ValueProvider firstArgProvider;
        @Stable CallableProvider callableProvider;
        @Stable FunctionCall callNode;
        final int xPosition;
        final int funPosition;

        public Lapply(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.callableProvider = updateParent(callableProvider);
            this.firstArgProvider = updateParent(firstArgProvider);
            this.callNode = updateParent(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        public static RAny generic(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) {
            if (!(argx instanceof RArray)) {
                Utils.nyi("unsupported type");
            }
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
                content[i] = (RAny) callNode.execute(context, frame);
            }
            return RList.RListFactory.getFor(content, null, l.names());
        }

        public Specialized createSpecialized(RAny argxTemplate) {
            if (argxTemplate instanceof RList) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        if (!(argx instanceof RList))  {
                            throw new UnexpectedResultException(null);
                        }
                        RList x = (RList) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.getRAny(i));
                            content[i] = (RAny) callNode.execute(context, frame);
                        }
                        return RList.RListFactory.getFor(content, null, x.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, a);
            }
            if (argxTemplate instanceof RArray) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        if (argx instanceof RList || !(argx instanceof RArray))  {
                            throw new UnexpectedResultException(null);
                        }
                        RArray x = (RArray) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.boxedGet(i));
                            content[i] = (RAny) callNode.execute(context, frame);
                        }
                        return RList.RListFactory.getFor(content, null, x.names());                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, a);
            }
            return null;
        }

        public Specialized createGeneric() {
            ApplyFunc a = new ApplyFunc() {
                @Override
                public RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                    return generic(context, frame, argx, firstArgProvider, callNode);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, a);
        }

        public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized sn = createSpecialized(argx);
                if (sn != null) {
                    replace(sn, "install Specialized from Lapply");
                    return sn.doApply(context, frame, argx, argfun);
                } else {
                    sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply");
                    return sn.doApply(context, frame, argx, argfun);
                }
            }
        }

        @Override
        public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
            RAny argx = args[xPosition];
            RAny argfun = args[funPosition];
            return doApply(context, frame, argx, argfun);
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException;
        }

        class Specialized extends Lapply {
            final ApplyFunc apply;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition, ApplyFunc apply) {
                super(call, names, exprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition);
                this.apply = apply;
            }

            @Override
            public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
                try {
                    callableProvider.matchAndSet(ast, context, frame, argfun);
                    return apply.apply(context, frame, argx, firstArgProvider, callNode);
                } catch (UnexpectedResultException e) {
                    Specialized sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply.Specialized");
                    return sn.doApply(context, frame, argx, argfun);
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
            if (sourceTemplate instanceof IntImpl.RIntSequence) {
                return new IntSequence();
            }
            if (sourceTemplate instanceof RList) {
                return new List();
            }
            if (sourceTemplate instanceof RString) {
                return new StringArray();
            }
            if (sourceTemplate instanceof RArray) {
                return new NonlistNonstringArray();
            }
            return new Generic(); // this will fail later when called
        }

        public static final class IntSequence extends ArgIterator {
            int to;
            int step;
            int next;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof IntImpl.RIntSequence)) {
                    throw new UnexpectedResultException(null);
                }
                IntImpl.RIntSequence seq = (IntImpl.RIntSequence) source;
                this.argProvider = provider;
                next = seq.from();
                to = seq.to();
                step = seq.step();
                size = seq.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(RInt.RIntFactory.getScalar(next));
                next += step;
            }

            @Override
            public RArray.Names names() {
                return null;
            }

            @Override
            public RString stringNames() {
                return null;
            }

            @Override
            public boolean hasNames() {
                return false;
            }
        }

        public static final class StringArray extends ArgIterator {
            RString string;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof RString)) {
                    throw new UnexpectedResultException(null);
                }
                this.argProvider = provider;
                this.string = (RString) source;
                i = 0;
                size = string.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(string.boxedGet(i));
                i++;
            }

            @Override
            public RArray.Names names() {
                RArray.Names snames = string.names();
                if (snames != null) {
                    return snames;
                } else {
                    return RArray.Names.create(RSymbol.getSymbols(string));
                }
            }

            @Override
            public RString stringNames() {
                RArray.Names snames = string.names();
                if (snames != null) {
                    return RString.RStringFactory.getFor(Convert.symbols2strings(snames.sequence()));
                } else {
                    return string;
                }
            }

            @Override
            public boolean hasNames() {
                return size > 0;
            }

        }

        public static final class NonlistNonstringArray extends ArgIterator {
            RArray array;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (source instanceof RList || source instanceof RString || !(source instanceof RArray)) {
                    throw new UnexpectedResultException(null);
                }
                this.argProvider = provider;
                this.array = (RArray) source;
                i = 0;
                size = array.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(array.boxedGet(i));
                i++;
            }

            @Override
            public RArray.Names names() {
                return array.names();
            }

            @Override
            public RString stringNames() {
                return RString.RStringFactory.getFor(Convert.symbols2strings(names().sequence()));
            }

            @Override
            public boolean hasNames() {
                return array.names() != null;
            }
        }

        public static final class List extends ArgIterator {
            RList list;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof RList)) {
                    throw new UnexpectedResultException(null);
                }
                this.argProvider = provider;
                this.list = (RList) source;
                i = 0;
                size = list.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(list.getRAny(i));
                i++;
            }

            @Override
            public RArray.Names names() {
                return list.names();
            }

            @Override
            public RString stringNames() {
                return RString.RStringFactory.getFor(Convert.symbols2strings(names().sequence()));
            }

            @Override
            public boolean hasNames() {
                return list.names() != null;
            }
        }

        public static final class Generic extends ArgIterator {
            RArray array;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) {
                if (!(source instanceof RArray)) {
                    Utils.nyi("unsupported type");
                }
                this.argProvider = provider;
                this.array = (RArray) source;
                i = 0;
                size = array.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(array.boxedGet(i));
                i++;
            }

            @Override
            public RArray.Names names() {
                RArray.Names anames = array.names();
                if (anames != null) {
                    return anames;
                }
                if (array instanceof RString) {
                    return RArray.Names.create(RSymbol.getSymbols((RString) array));
                }
                return null;
            }

            @Override
            public RString stringNames() {
                RArray.Names anames = array.names();
                if (anames != null) {
                    return RString.RStringFactory.getFor(Convert.symbols2strings(array.names().sequence()));
                }
                return (RString) array;
            }

            @Override
            public boolean hasNames() {
                return array.names() != null || (array instanceof RString && size > 0);
            }
        }
    }

    // TODO: handle names
    public static class Sapply extends BuiltIn {

        @Stable ValueProvider firstArgProvider;
        @Stable CallableProvider callableProvider;
        @Stable FunctionCall callNode;
        final int xPosition;
        final int funPosition;

        public Sapply(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.callableProvider = updateParent(callableProvider);
            this.firstArgProvider = updateParent(firstArgProvider);
            this.callNode = updateParent(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        // FIXME: this will be slow (a second pass through the results array)
        public static RArray.Names extractNames(ArgIterator argIterator, boolean resultsHaveNames, RAny[] results, int size) {
            boolean argHasNames = argIterator.hasNames();
            if (!argHasNames) {
                if (!resultsHaveNames) {
                    return null;
                }
                // just names from results
                RSymbol[] symbols = new RSymbol[size];
                for (int i = 0; i < size; i++) {
                    RArray a = (RArray) results[i];
                    RArray.Names n = a.names();
                    if (n != null) {
                        symbols[i] = n.sequence()[0];
                    } else {
                        symbols[i] = RSymbol.EMPTY_SYMBOL;
                    }
                }
                return RArray.Names.create(symbols);
            }
            // argNames != null
            if (!resultsHaveNames) {
                return argIterator.names();
            }
            // merge names
            RString argNames = argIterator.stringNames();
            RSymbol[] symbols = new RSymbol[size];
            for (int i = 0; i < size; i++) {
                String astr = argNames.getString(i);
                RArray a = (RArray) results[i];
                RArray.Names n = a.names();
                if (n != null) {
                    symbols[i] = RSymbol.getSymbol(astr + "." + n.sequence()[0].pretty());
                } else {
                    symbols[i] = RSymbol.getSymbol(astr);
                }
            }
            return RArray.Names.create(symbols);
        }

        public static RArray.Names mergeNames(RString argNames, RSymbol[] rnames, int size) {
            RSymbol[] symbols = new RSymbol[size];
            for (int i = 0; i < size; i++) {
                String astr = argNames.getString(i);
                symbols[i] = RSymbol.getSymbol(astr + "." + rnames[i].pretty());
            }
            return RArray.Names.create(symbols);
        }

        // TODO: support rownames, colnames (and names for a matrix result)
        public static RAny generic(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply, RAny[] partialContent) {

            boolean hasRaw = false;
            boolean hasLogical = false;
            boolean hasInt = false;
            boolean hasDouble = false;
            boolean hasComplex = false;
            boolean hasString = false;
            boolean notAllScalarLists = false;
            boolean hasList = false;
            boolean hasMultipleSizes = false;
            boolean hasNames = false;

            int elementSize = -1;
            RAny[] content;
            int xsize = argIterator.size();

            if (partialContent == null) {
                content = new RAny[xsize];
            } else {
                content = partialContent;
            }
            for (int i = 0; i < xsize; i++) {
                RAny v;
                if (partialContent == null || content[i] == null) {
                    argIterator.setNext();
                    FunctionCall callNode = sapply.callNode;
                    v = (RAny) callNode.execute(context, frame);
                    content[i] = v;
                } else {
                    v = content[i];
                }
                int vsize;
                if (v instanceof RDouble) {
                    hasDouble = true;
                    notAllScalarLists = true;
                    vsize = ((RDouble) v).size();
                } else if (v instanceof RInt) {
                    hasInt = true;
                    notAllScalarLists = true;
                    vsize = ((RInt) v).size();
                } else if (v instanceof RLogical) {
                    hasLogical = true;
                    notAllScalarLists = true;
                    vsize = ((RLogical) v).size();
                } else if (v instanceof RList) {
                    hasList = true;
                    vsize = ((RList) v).size();
                    if (vsize != 1) {
                        notAllScalarLists = true;
                    }
                } else if (v instanceof RString) {
                    hasString = true;
                    notAllScalarLists = true;
                    vsize = ((RString) v).size();
                } else if (v instanceof RComplex) {
                    hasComplex = true;
                    notAllScalarLists = true;
                    vsize = ((RComplex) v).size();
                } else if (v instanceof RRaw) {
                    hasRaw = true;
                    notAllScalarLists = true;
                    vsize = ((RRaw) v).size();
                } else if (v instanceof RNull) {
                    hasList = true;
                    notAllScalarLists = true;
                    vsize = 0;
                } else {
                    Utils.nyi("unsupported type");
                    return null;
                }
                if (elementSize != -1) {
                    if (vsize != elementSize) {
                        hasMultipleSizes = true;
                    }
                } else {
                    elementSize = vsize;
                }
                if (!hasNames) {
                    RArray.Names names = ((RArray) v).names(); // FIXME: could elide this cast by hand-inlining into code above
                    if (names != null) {
                        hasNames = true;
                    }
                }
            }
            if (elementSize > 1 && !hasMultipleSizes) {
                // result is a matrix
                int[] dimensions = new int[] {elementSize, xsize};
                int resSize = elementSize * xsize;

                if (hasList) {
                    RAny[] values = new RAny[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RList v = content[i].asList();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getRAny(j); // shallow but no need to ref here
                        }
                    }
                    return RList.RListFactory.getFor(values, dimensions);
                }
                if (hasString) {
                    String[] values = new String[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RString v = content[i].asString();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getString(j);
                        }
                    }
                    return RString.RStringFactory.getFor(values, dimensions, null);
                }
                if (hasComplex) {
                    double[] values = new double[2 * resSize];
                    for (int i = 0; i < xsize; i++) {
                        RComplex v = content[i].asComplex();
                        for (int j = 0; j < elementSize; j++) {
                            int offset = 2 * (i * elementSize + j);
                            values[offset] = v.getReal(j);
                            values[offset + 1] = v.getImag(j);
                        }
                    }
                    return RComplex.RComplexFactory.getFor(values, dimensions, null);
                }
                if (hasDouble) {
                    double[] values = new double[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RDouble v = content[i].asDouble();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getDouble(j);
                        }
                    }
                    return RDouble.RDoubleFactory.getFor(values, dimensions, null);
                }
                if (hasInt) {
                    int[] values = new int[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RInt v = content[i].asInt();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getInt(j);
                        }
                    }
                    return RInt.RIntFactory.getFor(values, dimensions, null);
                }
                if (hasLogical) {
                    int[] values = new int[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RLogical v = content[i].asLogical();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getLogical(j);
                        }
                    }
                    return RLogical.RLogicalFactory.getFor(values, dimensions, null);
                }
                if (hasRaw) {
                    byte[] values = new byte[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RRaw v = content[i].asRaw();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getRaw(j);
                        }
                    }
                    return RRaw.RRawFactory.getFor(values, dimensions, null);
                }

            } else {
                // result is a vector (or list) - not a matrix
                if (hasMultipleSizes) {
                    return RList.RListFactory.getFor(content, null, argIterator.names()); // result names not propagated
                }
                if (hasList) {
                    if (!notAllScalarLists) { // all elements are scalar lists
                        if (!hasNames) {
                            for (int i = 0; i < xsize; i++) {
                                RList v = (RList) content[i];
                                content[i] = v.getRAny(0); // shallow but no need to ref here
                            }
                        } else {
                            RSymbol[] symbols = new RSymbol[xsize];
                            for (int i = 0; i < xsize; i++) {
                                RList v = (RList) content[i];
                                content[i] = v.getRAny(0); // shallow but no need to ref here
                                RArray.Names n = v.names();
                                if (n != null) {
                                    symbols[i] = n.sequence()[0];
                                } else {
                                    symbols[i] = RSymbol.EMPTY_SYMBOL;
                                }
                            }
                            if (!argIterator.hasNames()) {
                                return RList.RListFactory.getFor(content, null, RArray.Names.create(symbols));
                            }
                            return RList.RListFactory.getFor(content, null, mergeNames(argIterator.stringNames(), symbols, xsize));
                        }

                    }
                    return RList.RListFactory.getFor(content, null, extractNames(argIterator, hasNames, content, xsize));
                }

                // this could be written using asXXX (but much slower)
                if (hasString) {
                    String[] values = new String[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RString) {
                            values[i] = ((RString) v).getString(0);
                        } else if (v instanceof RDouble) {
                            values[i] = Convert.double2string(((RDouble) v).getDouble(0));
                        } else if (v instanceof RInt) {
                            values[i] = Convert.int2string(((RInt) v).getInt(0));
                        } else if (v instanceof RLogical) {
                            values[i] = Convert.logical2string(((RLogical) v).getLogical(0));
                        } else if (v instanceof RComplex) {
                            RComplex cv = (RComplex) v;
                            values[i] = Convert.complex2string(cv.getReal(0), cv.getImag(0));
                        } else if (v instanceof RRaw) {
                            values[i] = Convert.raw2string(((RRaw) v).getRaw(0));
                        }
                    }
                    return RString.RStringFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasComplex) {
                    double[] values = new double[2 * xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RDouble) {
                            values[2 * i] = ((RDouble) v).getDouble(0);
                        } else if (v instanceof RInt) {
                            values[2 * i] = Convert.int2double(((RInt) v).getInt(0));
                        } else if (v instanceof RLogical) {
                            values[2 * i] = Convert.logical2double(((RLogical) v).getLogical(0));
                        } else if (v instanceof RComplex) {
                            RComplex cv = ((RComplex) v);
                            values[2 * i] = cv.getReal(0);
                            values[2 * i + 1] = cv.getImag(0);
                        } else {
                            values[2 * i] = Convert.raw2double(((RRaw) v).getRaw(0));
                        }
                    }
                    return RComplex.RComplexFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasDouble) {
                    double[] values = new double[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RDouble) {
                            values[i] = ((RDouble) v).getDouble(0);
                        } else if (v instanceof RInt) {
                            values[i] = Convert.int2double(((RInt) v).getInt(0));
                        } else if (v instanceof RLogical) {
                            values[i] = Convert.logical2double(((RLogical) v).getLogical(0));
                        } else {
                            values[i] = Convert.raw2double(((RRaw) v).getRaw(0));
                        }
                    }
                    return RDouble.RDoubleFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasInt) {
                    int[] values = new int[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RInt) {
                            values[i] = ((RInt) v).getInt(0);
                        } else if (v instanceof RLogical) {
                            values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                        } else {
                            values[i] = Convert.raw2int(((RRaw) v).getRaw(0));
                        }
                    }
                    return RInt.RIntFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasLogical) {
                    int[] values = new int[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RLogical) {
                            values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                        } else {
                            values[i] = Convert.raw2logical(((RRaw) v).getRaw(0));
                        }
                    }
                    return RLogical.RLogicalFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasRaw) {
                    byte[] values = new byte[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        values[i] = ((RRaw) v).getRaw(0);
                    }
                    return RRaw.RRawFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
            }
            return RList.EMPTY;
        }

        public static RAny[] unpackPartial(Object partial) {
            if (partial instanceof RAny[]) {
                return (RAny[]) partial;
            }
            PartialResult p = (PartialResult) partial;
            int size = p.content.size();
            RAny[] res = new RAny[size];
            RArray content = p.content;
            int csize = p.contentSize;
            int i = 0;
            for (; i < csize; i++) {
                res[i] = content.boxedGet(i);
            }
            res[i] = p.lastValue;
            return res;
        }

        public class PartialResult {
            public final RArray content;
            public final int contentSize;
            public final RAny lastValue;

            PartialResult(RArray content, int contentSize, RAny lastValue) {
                this.content = content;
                this.contentSize = contentSize;
                this.lastValue = lastValue;
            }
        }

        public Specialized createSpecialized(RAny resTemplate, ArgIterator argIterator) {
            if (resTemplate instanceof RDouble && ((RDouble) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        double[] content = new double[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof ScalarDoubleImpl) {
                                content[i] = ((ScalarDoubleImpl) v).getDouble();
                            } else { // NOTE: can also add Int and Logical support here
                                throw new UnexpectedResultException(new PartialResult(RDouble.RDoubleFactory.getFor(content), i, v));
                            }
                        }
                        return RDouble.RDoubleFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RDouble>");
            }
            if (resTemplate instanceof RInt && ((RInt) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof ScalarIntImpl) {
                               content[i] = ((ScalarIntImpl) v).getInt();
                            } else { // NOTE: can also add Logical support here
                               throw new UnexpectedResultException(new PartialResult(RInt.RIntFactory.getFor(content), i,  v));
                            }
                        }
                        return RInt.RIntFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RInt>");
            }
            if (resTemplate instanceof RLogical && ((RLogical) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof ScalarLogicalImpl) {
                                content[i] = ((ScalarLogicalImpl) v).getLogical();
                            } else {
                                throw new UnexpectedResultException(new PartialResult(RLogical.RLogicalFactory.getFor(content), i, v));
                            }
                        }
                        return RLogical.RLogicalFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RLogical>");
            }
            if (resTemplate instanceof RString && ((RString) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        String[] content = new String[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof ScalarStringImpl) {
                                content[i] = ((ScalarStringImpl) v).getString();
                            } else {
                                throw new UnexpectedResultException(new PartialResult(RString.RStringFactory.getFor(content), i, v));
                            }
                        }
                        return RString.RStringFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RString>");
            }
            if (resTemplate instanceof RComplex && ((RComplex) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        double[] content = new double[2 * xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof ScalarComplexImpl) {
                                ScalarComplexImpl cv = (ScalarComplexImpl) v;
                                content[2 * i] = cv.getReal();
                                content[2 * i + 1] = cv.getImag();
                            } else {
                                throw new UnexpectedResultException(new PartialResult(RComplex.RComplexFactory.getFor(content), i, v));
                            }
                        }
                        return RComplex.RComplexFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RComplex>");
            }
            if (resTemplate instanceof RList && ((RList) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        RAny[] content = new RAny[xsize];
                        boolean returnsList = false;
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            content[i] = v;
                            if (!returnsList) {
                                if (v instanceof RList || v instanceof RNull) {
                                    returnsList = true;
                                } else {
                                    if (v instanceof RArray) {
                                        int size = ((RArray) v).size();
                                        if (size != 1) {
                                            returnsList = true;
                                        }
                                    }
                                }
                            }
                        }
                        if (!returnsList) {
                            throw new UnexpectedResultException(content);
                        }
                        return RList.RListFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RList>");
            }
            if (resTemplate instanceof RRaw && ((RRaw) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        byte[] content = new byte[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof RRaw) {
                                content[i] = ((RRaw) v).getRaw(0);
                            } else {
                                throw new UnexpectedResultException(new PartialResult(RRaw.RRawFactory.getFor(content), i, v));
                            }
                        }
                        return RRaw.RRawFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RRaw>");
            }
            return null; // FIXME: should return generic by default?
        }

        public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {

            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (!(argx instanceof RArray)) {
                    Utils.nyi("unsupported type");
                }
                callableProvider.matchAndSet(ast, context, frame, argfun);
                ArgIterator argIterator = ArgIterator.create(argx);
                try {
                    argIterator.reset(firstArgProvider, argx);
                } catch (UnexpectedResultException e1) {
                    Utils.nyi("unsupported type");
                    return null;
                }
                RAny res = generic(context, frame, argIterator, this, null);
                Specialized sn = createSpecialized(res, argIterator);
                if (DEBUG_AP) Utils.debug("apply - rewrote initial generic to " + argIterator.toString() + " " + sn.dbg);
                replace(sn, "install Specialized from Sapply");
                return res;
            }
        }

        @Override
        public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
            RAny argx = args[xPosition];
            RAny argfun = args[funPosition];
            return doApply(context, frame, argx, argfun);
        }

        public Specialized createGeneric(ArgIterator argIterator) {
            ApplyFunc a = new ApplyFunc() {
                @Override
                public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                    return generic(context, frame, argIterator, sapply, null);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<Generic>");
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException;
        }

        class Specialized extends Sapply {
            final ApplyFunc apply;
            final ArgIterator argIterator;
            final String dbg;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition, ArgIterator argIterator, ApplyFunc apply, String dbg) {
                super(call, names, exprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition);
                this.apply = apply;
                this.argIterator = argIterator;
                this.dbg = dbg;
            }

            @Override
            public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
                callableProvider.matchAndSet(ast, context, frame, argfun);
                try {
                    argIterator.reset(firstArgProvider, argx);
                } catch (UnexpectedResultException e) {
                    ArgIterator ai = new ArgIterator.Generic();
                    Specialized sn = new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, ai, apply, dbg);
                    if (DEBUG_AP) Utils.debug("apply - Specialized " + argIterator.toString() + " " + dbg  + " failed, rewriting to " + ai.toString() + dbg );
                    replace(sn, "install Specialized<Generic, ?> from Sapply.Specialized");
                    return sn.doApply(context, frame, argx, argfun);
                }

                try {
                    return apply.apply(context, frame, argIterator, this);
                } catch (UnexpectedResultException e) {
                    RAny[] partialContent = unpackPartial(e.getResult());
                    Specialized sn = createGeneric(argIterator);
                    if (DEBUG_AP) Utils.debug("apply - Specialized " + argIterator.toString() + " " + dbg + " failed, rewriting to " + sn.argIterator.toString() + " " + sn.dbg);
                    replace(sn, "install Specialized<?, Generic> from Sapply.Specialized");
                    return generic(context, frame, argIterator, this, partialContent);
                }
            }
        }
    }
}
